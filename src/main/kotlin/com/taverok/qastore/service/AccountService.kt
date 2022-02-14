package com.taverok.qastore.service

import com.taverok.qastore.domain.Account
import com.taverok.qastore.repository.UserRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val encoder: BCryptPasswordEncoder,
    private val userRepository: UserRepository
) {
    private val logger = KotlinLogging.logger {}

    private var cachedAccounts: List<Account> = emptyList()

//    fun create(request: UserCreateRequest) {
//        val existingUser = findByUsername(request.username.trim())
//        if (existingUser != null){
//            reactivate(existingUser)
//            return
//        }
//
//        val user = User(username = request.username, pass = encoder.encode(request.pass))
//        save(user)
//    }

//    fun changePassword(request: PasswordUpdateRequest){
//        val user = findActiveByUsernameOrTrow(request.username)
//        if (user.role == UserRole.SUPERADMIN)
//            throw IllegalArgumentException("SUPERADMIN pass can be changed through database only")
//
//        user.pass = encoder.encode(request.newPass)
//        save(user)
//    }

    fun save(account: Account): Account {
        val result = userRepository.save(account)
        updateCache()

        return result
    }

    fun findByUsername(username: String): Account? {
        return cachedAccounts.firstOrNull { it.username == username }
    }

    fun findActiveByUsernameOrTrow(username: String): Account {
        val user = findByUsername(username)

        if (user?.isActive != true)
            throw UsernameNotFoundException("user $username not found")

        return user
    }

    fun findAllActive(): List<Account> {
        return cachedAccounts.filter { it.isActive }
    }

    fun findById(id: Long): Account {
        return cachedAccounts.firstOrNull { it.id == id }
            ?: throw UsernameNotFoundException("user $id not found")
    }

    @Scheduled(fixedDelay = 60_000)
    fun updateCache() {
        cachedAccounts = userRepository.findAll()

        logger.info { "Users cache updated" }
    }
}