package br.com.listennow.utils

import java.security.MessageDigest

class EncryptionUtil {
    companion object {
        fun encryptSHA(text: String): String {
            return hashString(text, "SHA-256")
        }

        private fun hashString(input: String, algorithm: String): String {
            return MessageDigest
                .getInstance(algorithm)
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
        }
    }
}