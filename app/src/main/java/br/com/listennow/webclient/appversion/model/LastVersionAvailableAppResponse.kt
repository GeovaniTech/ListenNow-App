package br.com.listennow.webclient.appversion.model

data class LastVersionAvailableAppResponse (
    val code: Int,
    val name: String,
    val url: String
)