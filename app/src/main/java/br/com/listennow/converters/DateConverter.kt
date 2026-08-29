package br.com.listennow.converters

import android.content.Context
import br.com.listennow.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DateConverter {
    companion object {
        fun toUnixTimestamp(dateString: String): Long {
            val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
            return ZonedDateTime.parse(dateString, formatter).toEpochSecond()
        }


        @JvmStatic
        fun fromUnixTimestampToAddedOnReadableDate(context: Context, date: Long): String {
            val millis = if (date < 100_000_000_000L) date * 1000L else date

            val dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(context.resources.configuration.locales[0])

            val formattedDate = dateTime.format(formatter)

            return context.getString(R.string.added_on_label, formattedDate)
        }
    }
}