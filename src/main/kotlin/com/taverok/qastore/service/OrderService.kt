package com.taverok.qastore.service

import com.taverok.qastore.domain.*
import com.taverok.qastore.domain.OrderStatus.*
import com.taverok.qastore.domain.PaymentType.CARD_PREPAYMENT
import com.taverok.qastore.dto.request.OrderCreateRequest
import com.taverok.qastore.dto.request.OrderUpdateRequest
import com.taverok.qastore.dto.request.PaymentCard
import com.taverok.qastore.dto.request.PaymentRequest
import com.taverok.qastore.dto.response.OrderResponse
import com.taverok.qastore.exception.ClientSideException
import com.taverok.qastore.exception.NotFoundException
import com.taverok.qastore.repository.AccountRepository
import com.taverok.qastore.repository.CartRepository
import com.taverok.qastore.repository.OrderRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional
import javax.transaction.Transactional.TxType.REQUIRES_NEW
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@Service
class OrderService(
    val cartService: CartService,
    val productService: ProductService,
    val cartRepository: CartRepository,
    val conf: AppConfigService,
    val orderRepository: OrderRepository,
    val accountRepository: AccountRepository
) {
    fun newOrder(request: OrderCreateRequest, account: Account): Order {
        val items = cartService.getAll(account)
        return newOrder(request,account, items)
    }

    @Transactional
    fun update(orderId: Long, request: OrderUpdateRequest, account: Account): Order {
        val order = getByIdOrThrow(orderId)
        if (order.accountId != account.id)
            throw NotFoundException()

        val items = cartRepository.findAllByOrderId(orderId)
        val createRequest = OrderCreateRequest(
            bonuses = request.bonuses ?: order.spentBonuses,
            paymentType = request.paymentType ?: order.paymentType,
            deliveryType = request.deliveryType ?: order.deliveryType
        )
        val newOrder = newOrder(createRequest, account, items)
        newOrder.id = orderId

        orderRepository.save(newOrder)

        return newOrder
    }

    @Transactional
    fun save(request: OrderCreateRequest, account: Account): Order{
        val order = newOrder(request, account)
        orderRepository.save(order)

        val items = cartService.getAll(account)
        items.onEach { it.orderId = order.id }
        cartService.save(items)

        return order
    }


    fun getByIdOrThrow(orderId: Long): Order {
        return orderRepository.findById(orderId)
            .orElseThrow { ClientSideException("order not found") }
    }

    fun getAll(account: Account): List<Order> {
        return orderRepository.findAllByAccountId(account.id!!)
    }


    fun cancel(orderId: Long, account: Account) {
        val order = getByIdOrThrow(orderId)
        if (order.accountId != account.id!!)
            throw NotFoundException()
        if (order.status in setOf(COMPLETED, CANCELED))
            throw ClientSideException("order can not be canceled")

        order.status = CANCELED
        orderRepository.save(order)
    }

    fun toResponse(o: Order): OrderResponse {
        return OrderResponse(
            id = o.id,
            orderAmount = o.amount,
            total = o.total,
            spentBonuses = o.spentBonuses,
            earnedBonuses = o.earnedBonuses,
            deliveryPrice = o.deliveryPrice,
            address = o.address,
            paymentType = o.paymentType,
            deliveryType = o.deliveryType,
            status = o.status,
            createdAt = o.createdAt
        )
    }

    @Transactional(REQUIRES_NEW)
    fun processPayment(request: PaymentRequest, account: Account) {
        val order = getByIdOrThrow(request.orderId)
        if (order.status != PAYMENT_REQUIRED)
            throw ClientSideException("this order does not require payment")

        validateCreditCard(request.card)

        order.status = DELIVERING
        order.payedAt = LocalDateTime.now()
        orderRepository.save(order)
    }

    private fun newOrder(request: OrderCreateRequest, account: Account, items: List<CartItem>): Order {
        if (items.isEmpty())
            throw ClientSideException("empty cart")

        val orderAmount = items.sumOf { productService.getByIdOrThrow(it.productId).price * it.quantity }
        val deliveryPrice = calcDeliveryPrice(orderAmount)
        val bonuses = calcSpendableBonuses(request, account, orderAmount)
        val total = orderAmount + deliveryPrice - bonuses
        val address = account.address

        validatePaymentType(total, request.paymentType)
        validateDelivery(total, address, request.deliveryType)

        return Order(
            accountId = account.id!!,
            paymentType = request.paymentType,
            deliveryType = request.deliveryType,
            amount = orderAmount,
            spentBonuses = bonuses,
            earnedBonuses = calcEarnedBonuses(total),
            deliveryPrice = deliveryPrice,
            total = total
        ).also {
            it.status = paymentStatus(request.paymentType)
            it.createdAt = LocalDateTime.now()
            it.address = "${address?.city} ${address?.street} ${address?.house} ${address?.apartment}"
        }
    }

    private fun validateCreditCard(card: PaymentCard) {
        val no = card.no.replace(" ", "")
        if (!no.matches(Regex("[0-9]{16}")))
            throw ClientSideException("wrong card number")

        val lastDigit = card.no.last().digitToInt()

        if (lastDigit % 2 == 1)
            throw ClientSideException("insufficient funds")
    }

    @Transactional(REQUIRES_NEW)
    fun consumeBonuses(order: Order, account: Account) {
        account.bonuses -= order.spentBonuses
        account.bonuses += order.earnedBonuses
        accountRepository.save(account)
    }

    private fun paymentStatus(requestedPaymentType: PaymentType) =
        if (requestedPaymentType == CARD_PREPAYMENT) PAYMENT_REQUIRED else DELIVERING

    private fun validatePaymentType(total: Double, paymentType: PaymentType) {
        val max = conf.getDouble(MAX_POSTPAYMENT_AMOUNT)
        if (paymentType != CARD_PREPAYMENT && total > max)
            throw ClientSideException("must be prepaid, current total of $total is larger than $max allowed for postpayment")
    }

    private fun calcEarnedBonuses(total: Double): Double {
        val pct = conf.getDouble(BONUS_PCT)
        return floor(total * pct / 100)
    }

    private fun calcSpendableBonuses(
        request: OrderCreateRequest,
        account: Account,
        orderAmount: Double
    ): Double {
        val requested = max(request.bonuses, .0)
        val available = min(account.bonuses, requested)

        return min(available, orderAmount)
    }

    private fun validateDelivery(amount: Double, address: AccountAddress?, deliveryType: DeliveryType) {
        val min = conf.getDouble(MIN_DELIVERY_AMOUNT)

        if (deliveryType == DeliveryType.DELIVERY){
            if (address?.street == null || address.house == null)
                throw ClientSideException("address is incomplete")

            if (amount < min)
                throw ClientSideException("your check sum must be larger than $min for delivery, current is $amount")
        }

    }

    private fun calcDeliveryPrice(amount: Double): Double {
        val free = conf.getDouble(AMOUNT_FREE_DELIVERY)

        return when {
            amount >= free -> .0
            else -> conf.getDouble(DELIVERY_PRICE)
        }
    }
}

const val DELIVERY_PRICE = "deliveryPrice"
const val MIN_DELIVERY_AMOUNT = "minAmountForDelivery"
const val AMOUNT_FREE_DELIVERY = "amountForFreeDelivery"
const val MAX_POSTPAYMENT_AMOUNT = "maxPostpaymentAmount"
const val BONUS_PCT = "bonusPercentage"