package com.taverok.qastore.service

import com.taverok.qastore.domain.*
import com.taverok.qastore.domain.PaymentStatus.PAYMENT_REQUIRED
import com.taverok.qastore.dto.request.OrderCreateRequest
import com.taverok.qastore.dto.response.OrderResponse
import com.taverok.qastore.exception.ClientSideException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

@Service
class OrderService(
    val cartService: CartService,
    val productService: ProductService,
    val conf: AppConfigService
) {
    fun newOrder(request: OrderCreateRequest, account: Account): Order {
        val items = cartService.getAll(account)
        validateCart(items)

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
            earnedBonuses = round(total / 10),
            deliveryPrice = deliveryPrice,
            total = total
        ).also {
            it.paymentStatus = PAYMENT_REQUIRED
            it.createdAt = LocalDateTime.now()
            it.address = "${address?.city} ${address?.street} ${address?.house} ${address?.apartment}"
        }
    }

    private fun validatePaymentType(total: Double, paymentType: PaymentType) {
        val min = conf.getDouble(MIN_POSTPAYMENT_AMOUNT)
        if (paymentType != PaymentType.CARD_PREPAYMENT && total > min)
            throw ClientSideException("must be prepaid, current total of $total is larger than $min")
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
            paymentStatus = o.paymentStatus,
            createdAt = o.createdAt
        )
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

    private fun validateCart(items: List<CartItem>) {
        if (items.isEmpty())
            throw ClientSideException("empty cart")
    }

    private fun validateDelivery(amount: Double, address: AccountAddress?, deliveryType: DeliveryType) {
        val min = conf.getDouble(MIN_DELIVERY_AMOUNT)

        if (deliveryType == DeliveryType.DELIVERY){
            if (address?.street == null || address.house == null)
                throw ClientSideException("address is incomplete")

            if (amount < min)
                throw ClientSideException("your check sum must be larger than $min for delivery, but current is $amount")
        }

    }

    private fun calcDeliveryPrice(amount: Double): Double {
        val free = conf.getDouble(AMOUNT_FREE_DELIVERY)

        return when {
            amount > free -> .0
            else -> conf.getDouble(DELIVERY_PRICE)
        }
    }

}

const val DELIVERY_PRICE = "deliveryPrice"
const val MIN_DELIVERY_AMOUNT = "minAmountForDelivery"
const val AMOUNT_FREE_DELIVERY = "amountForFreeDelivery"
const val MIN_POSTPAYMENT_AMOUNT = "minPostpaymentAmount"