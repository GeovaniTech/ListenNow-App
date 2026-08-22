package br.com.listennow.converters

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DateConverter {
    companion object {
        fun toUnixTimestamp(dateString: String): Long {
            val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
            return ZonedDateTime.parse(dateString, formatter).toEpochSecond()
        }
    }
}