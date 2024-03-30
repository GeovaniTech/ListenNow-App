package br.com.listennow.webclient.user.model

data class UserRequest (
    val uuid: String,
    val email: String,
    val password: String
)