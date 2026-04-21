package org.pcsoft.framework.kube.kts.api.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class MailAddress(val name: String, val provider: String) {
    companion object {
        private val MAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        @JvmStatic
        @JsonCreator
        fun parse(address: String): MailAddress {
            val parts = address.split("@")
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid mail address format")
            }
            return MailAddress(parts[0], parts[1])
        }
    }

    init {
        val fullAddress = toString()
        require(MAIL_PATTERN.matches(fullAddress)) {
            "Invalid mail address format: $fullAddress"
        }
    }

    @JsonValue
    override fun toString(): String {
        return "$name@$provider"
    }
}
