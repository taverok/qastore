package com.taverok.qastore.repository

import com.taverok.qastore.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<Account, Long>{
    fun findFirstByEmail(email: String): Account?
}