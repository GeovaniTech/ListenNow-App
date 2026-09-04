package br.com.listennow.webclient.log.model

data class InsertLogRequest(
    val logs: List<LogRequest>,
    val deviceId: String
)