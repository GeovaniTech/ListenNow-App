package br.com.listennow.navparams

import java.io.Serializable

data class SelectSongsNavParams(
    var ignoreIds: List<String> = emptyList()
): Serializable