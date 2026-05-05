/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * Defines the supported IP protocols for network communication.
 *
 * Protocol is used to specify the transport layer protocol (e.g., TCP, UDP, SCTP)
 * for configurations involving port mappings, ingress rules, and backend services.
 */
@Suppress("unused")
enum class Protocol {
    /**
     * Represents the Transmission Control Protocol (TCP) for specifying
     * and configuring communication over a reliable, connection-oriented network protocol.
     *
     * Typically used in scenarios where data delivery assurance, packet ordering,
     * and flow control are essential requirements.
     */
    TCP,

    /**
     * Represents the User Datagram Protocol (UDP), a lightweight, connectionless transport layer protocol.
     *
     * UDP is commonly used for scenarios requiring low-latency communication and minimal overhead,
     * such as streaming, gaming, and real-time messaging, where reliability and order of packet delivery
     * are not critical.
     */
    UDP,

    /**
     * Represents the Stream Control Transmission Protocol (SCTP).
     *
     * SCTP is a transport-layer protocol used for transmitting multiple streams of data
     * simultaneously between two endpoints that have established a connection. It provides
     * reliable, message-oriented communication with features such as multi-streaming,
     * multi-homing, and strong error-checking mechanisms.
     *
     * Typically used in scenarios requiring improved performance and scalability for
     * real-time applications, such as voice-over-IP (VoIP), signaling in telecom networks,
     * and other mission-critical systems.
     */
    SCTP
}