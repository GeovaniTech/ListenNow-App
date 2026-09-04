package br.com.listennow.webclient.log.service

import br.com.listennow.service.LogService
import br.com.listennow.webclient.log.model.InsertLogRequest

class LogWebClient(
    val logService: LogService
) {
    suspend fun createLog(createLogRequest: InsertLogRequest): Boolean {
        val response = logService.createLog(createLogRequest)

        return response.isSuccessful
    }
}