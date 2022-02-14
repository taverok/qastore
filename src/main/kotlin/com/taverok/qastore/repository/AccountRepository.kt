package com.taverok.qastore.repository

import com.taverok.qastore.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<Account, Long>