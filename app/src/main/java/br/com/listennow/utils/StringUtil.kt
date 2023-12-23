package br.com.listennow.utils

class StringUtil {
    companion object {
        fun isNotNull(text: String): Boolean {
            if(text != "") {
                return true
            }

            return false
        }
    }
}