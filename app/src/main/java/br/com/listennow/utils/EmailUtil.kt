package br.com.listennow.utils

class EmailUtil {
    companion object {
        private const val regex =  "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

        fun isEmailValid(email: String): Boolean {
            return  email.matches(regex.toRegex())
        }
    }
}