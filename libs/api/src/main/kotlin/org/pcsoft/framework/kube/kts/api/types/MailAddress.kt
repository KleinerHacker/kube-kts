/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Represents an email address.
 *
 * @property name The user name part of the email address (before the '@').
 * @property provider The domain/provider part of the email address (after the '@').
 */
data class MailAddress(val name: String, val provider: String) {
    companion object {
        private val MAIL_PATTERN = Regex("^[A-Za-z0-9_]+([+-.][A-Za-z0-9_]+)*@[A-Za-z0-9]+([.-][A-Za-z0-9]+)+$")

        /**
         * Parses a given email address string into a MailAddress object.
         *
         * @param address The email address string to be parsed. It should be in the format "name@provider".
         * @return A MailAddress object containing the parsed name and provider components.
         * @throws IllegalArgumentException if the input string is not in a valid email address format.
         */
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

    /**
     * Returns the string representation of the email address in the format "name@provider".
     *
     * @return A string combining the name and provider, separated by '@'.
     */
    @JsonValue
    override fun toString(): String {
        return "$name@$provider"
    }
}
