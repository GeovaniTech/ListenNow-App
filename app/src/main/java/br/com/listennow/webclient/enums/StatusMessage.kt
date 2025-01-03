package br.com.listennow.webclient.enums

enum class StatusMessage(val message: String) {
    FAILED_TO_CONNECT_API("code: 0"),
    FAILED_TO_SAVE_USER("code: 1"),
    SONGS_COPIED_SUCCESSFULLY("code: 2")
}