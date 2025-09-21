package br.com.listennow.converters

import java.time.LocalDateTime

class DateConverters {
    companion object {
        fun fromTimestamp(value: String?): LocalDateTime? {
            return value?.let { LocalDateTime.parse(it) }
        }

        fun dateToTimestamp(date: LocalDateTime?): String? {
            return date?.toString()
        }
    }
}