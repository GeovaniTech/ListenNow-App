package br.com.listennow.navparams

import java.io.Serializable

data class ErrorNavParams(
    val title: String? = null,
    val description: String,
    val retryAction: () -> Unit
): Serializable