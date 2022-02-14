package com.taverok.qastore.service

import com.taverok.qastore.exception.NotFoundException
import com.taverok.qastore.repository.AppConfigRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class AppConfigService(
    val appConfigRepository: AppConfigRepository
) {
    private var cacheConfigs: Map<String, String> = emptyMap()

    fun getStr(key: String): String{
        return cacheConfigs[key]
            ?: throw NotFoundException("app config not found")
    }

    fun getDouble(key: String, default: Double = .0): Double{
        val value = cacheConfigs[key]
            ?: return default

        return value.toDouble()
    }

    @Scheduled(fixedDelay = 10_000)
    fun updateCache(){
        cacheConfigs = appConfigRepository.findAll().associate { it.key to it.value }
    }
}

const val NEW_USER_BONUSES_KEY = "newUserBonuses"