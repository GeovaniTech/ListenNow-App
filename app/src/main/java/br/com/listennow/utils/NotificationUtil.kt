package br.com.listennow.utils

import java.util.concurrent.atomic.AtomicInteger

class NotificationUtil {
    companion object {
        val id: AtomicInteger = AtomicInteger(2)

        fun getUniqueNotificationId(): Int {
            return id.incrementAndGet()
        }
    }
}