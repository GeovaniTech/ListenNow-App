package br.com.listennow.webclient.log.model

data class LogRequest(
    val id: String,
    val level: String,
    val tag: String,
    val message: String,
    val createdAt: Long,
)
