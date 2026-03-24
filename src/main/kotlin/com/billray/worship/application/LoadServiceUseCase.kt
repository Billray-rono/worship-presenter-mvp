package com.billray.worship.application

import com.billray.worship.domain.ServiceSet
import com.billray.worship.infra.db.ServiceSetRepository

class LoadServiceUseCase(private val serviceSetRepository: ServiceSetRepository) {
    fun execute(serviceSetId: Long): ServiceSet? = serviceSetRepository.findById(serviceSetId)
}
