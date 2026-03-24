package com.billray.worship.application

import com.billray.worship.domain.ServiceSet
import com.billray.worship.infra.db.ServiceSetRepository

class SaveServiceUseCase(private val serviceSetRepository: ServiceSetRepository) {
    fun execute(serviceSet: ServiceSet): Long = serviceSetRepository.save(serviceSet)
}
