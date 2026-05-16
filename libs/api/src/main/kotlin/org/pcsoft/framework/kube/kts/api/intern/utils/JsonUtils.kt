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

package org.pcsoft.framework.kube.kts.api.intern.utils

import tools.jackson.core.JsonGenerator

/**
 * Wraps object-writing functionality for a `JsonGenerator`, ensuring that
 * a JSON object structure is properly opened and closed during the execution of the given action.
 *
 * The `writeStartObject` method is called to start a JSON object before executing the provided action,
 * and `writeEndObject` is called to close the object after the action completes.
 * The object closure is guaranteed even if the action throws an exception.
 *
 * @param action A lambda function representing the operations to be performed between
 *               the start and end of the JSON object. It contains the logic for
 *               populating the JSON object structure.
 */
internal fun JsonGenerator.writeObject(action: () -> Unit) {
    writeStartObject()
    try {
        action()
    } finally {
        writeEndObject()
    }
}

/**
 * Writes a property with the given name to a JSON object, executing the specified action within the
 * context of that property. This ensures proper creation and closure of a nested JSON object.
 *
 * The method begins writing a named JSON object property, executes the provided action to populate
 * the property, and ensures the JSON object is properly closed, even if an exception is thrown during
 * the action execution.
 *
 * @param propertyName The name of the JSON property to write.
 * @param action A lambda function that contains the logic to write content into the defined JSON property.
 */
internal fun JsonGenerator.writeObjectProperty(propertyName: String, action: () -> Unit) {
    writeObjectPropertyStart(propertyName)
    try {
        action()
    } finally {
        writeEndObject()
    }
}

/**
 * Writes a JSON array construct to the output using the provided action to define the array's content.
 *
 * This method simplifies the process of writing JSON arrays by wrapping the start and end of the
 * array in appropriate actions (`writeStartArray` and `writeEndArray`). The provided `action` is
 * executed to populate the array's content.
 *
 * @param action A lambda expression that defines the content of the JSON array. This is executed
 * between the calls to `writeStartArray` and `writeEndArray`.
 */
internal fun JsonGenerator.writeArray(action: () -> Unit) {
    writeStartArray()
    try {
        action()
    } finally {
        writeEndArray()
    }
}

/**
 * Writes the array property with the specified name, executing the provided action
 * to populate the contents of the array.
 *
 * @param propertyName The name of the JSON property to write as an array.
 * @param action The lambda function that specifies the elements of the array.
 */
internal fun JsonGenerator.writeArrayProperty(propertyName: String, action: () -> Unit) {
    writeArrayPropertyStart(propertyName)
    try {
        action()
    } finally {
        writeEndArray()
    }
}