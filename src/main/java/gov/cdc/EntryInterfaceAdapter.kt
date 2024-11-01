package gov.cdc

import com.google.gson.*
import gov.nist.validation.report.impl.EntryImpl
import java.lang.reflect.Type

class EntryInterfaceAdapter<T> : JsonSerializer<T>, JsonDeserializer<T> {
    override fun deserialize(jsonElement: JsonElement, type: Type, context: JsonDeserializationContext): T {
        val jsonObject = jsonElement.asJsonObject
        return context.deserialize(jsonObject, EntryImpl::class.java)
    }

    override fun serialize(src: T, type: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }
}
