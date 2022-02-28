package com.taverok.qastore.service

import com.taverok.qastore.config.security.AccountCredentials
import com.taverok.qastore.config.security.JwtConfig
import com.taverok.qastore.config.security.newAuthToken
import com.taverok.qastore.domain.Account
import com.taverok.qastore.domain.AccountAddress
import com.taverok.qastore.domain.AccountRoles
import com.taverok.qastore.dto.request.AccountCreateRequest
import com.taverok.qastore.dto.request.AccountUpdateRequest
import com.taverok.qastore.dto.response.AccountResponse
import com.taverok.qastore.exception.ClientSideException
import com.taverok.qastore.repository.AccountRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class AccountService(
    private val encoder: BCryptPasswordEncoder,
    private val jwtConfig: JwtConfig,
    private val appConfigService: AppConfigService,
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun create(request: AccountCreateRequest): Account {
        val email = request.email
        val now = LocalDateTime.now()
        val existingAccount = findByEmail(email)

        if (existingAccount != null)
            throw ClientSideException("account exists")

        val account = Account(request.username, email).also {
            it.pass = encoder.encode(request.pass)
            it.bonuses = appConfigService.getDouble(NEW_USER_BONUSES_KEY)
            it.createdAt = now
        }

        accountRepository.save(account)

        return account
    }

//    fun changePassword(request: PasswordUpdateRequest){
//        val user = findActiveByUsernameOrTrow(request.username)
//        if (user.role == UserRole.SUPERADMIN)
//            throw IllegalArgumentException("SUPERADMIN pass can be changed through database only")
//
//        user.pass = encoder.encode(request.newPass)
//        save(user)
//    }

    fun findByEmail(email: String): Account? {
        return accountRepository.findFirstByEmail(email)
    }

    fun findActiveByEmailOrTrow(email: String): Account {
        val user = findByEmail(email)

        if (user?.isActive != true)
            throw UsernameNotFoundException("user $email not found")

        return user
    }


    fun getAuthJwt(request: AccountCredentials): String {
        val account = findActiveByEmailOrTrow(request.email)
        val passMatches = encoder.matches(request.pass, account.pass)

        if (!passMatches)
            throw ClientSideException("wrong password")

        return newAuthToken(jwtConfig.secret, request.email, listOf(AccountRoles.USER.name))
    }

    fun getAccountResponse(account: Account): AccountResponse{
        return AccountResponse(
            email = account.email,
            username = account.username,
            bonuses = account.bonuses,
            phone = account.phone,
            createdAt = account.createdAt,
            city = account.address?.city,
            street = account.address?.street,
            house = account.address?.house,
            apartment = account.address?.apartment
        )
    }

    @Transactional
    fun update(request: AccountUpdateRequest, account: Account) {
        if (account.address == null){
            account.address = AccountAddress().also { it.account = account }
        }


        val address = account.address!!
        request.city?.let { address.city = it }
        request.street?.let { address.street = it }
        request.house?.let { address.house = it }
        request.apartment?.let { address.apartment = it }
        request.phone?.let { request.phone }

        accountRepository.save(account)
    }

    @Transactional
    fun setBonuses(amount: Double, account: Account) {
        account.bonuses = amount

        accountRepository.save(account)
    }
}