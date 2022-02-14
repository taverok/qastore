package com.taverok.qastore.repository

import com.taverok.qastore.domain.AppConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppConfigRepository: JpaRepository<AppConfig, Long>