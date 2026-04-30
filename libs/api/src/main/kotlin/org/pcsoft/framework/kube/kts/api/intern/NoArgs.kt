package org.pcsoft.framework.kube.kts.api.intern

/**
 * Marks a class as requiring a no-arguments constructor.
 *
 * This annotation is used internally to signal that the annotated class must include
 * a no-args constructor. It is commonly employed in scenarios like frameworks or tools
 * that rely on reflective instantiation, data serialization/deserialization, or custom
 * framework logic requiring instantiation without parameters.
 *
 * Note: This annotation is for internal use only and is not intended to be used
 * in public-facing APIs.
 */
internal annotation class NoArgs