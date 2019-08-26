package info.laht.threekt.loaders.gltf

import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import kotlinx.serialization.*
import kotlinx.serialization.internal.FloatSerializer

internal open class MathDescriptor(override val name: String, val size: Int) : SerialDescriptor {
    override val kind: SerialKind get() = StructureKind.LIST
    override val elementsCount: Int = size
    override fun getElementName(index: Int): String {
        return if (index < size) {
            index.toString()
        } else {
            error("Invalid index $index")
        }
    }
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")

    override fun getElementDescriptor(index: Int): SerialDescriptor = FloatSerializer.descriptor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MathDescriptor) return false
        if (name == other.name && size == other.size) return true
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + size.hashCode()
    }
}

internal object Vector3Serializer : KSerializer<Vector3> {
    override val descriptor: SerialDescriptor = MathDescriptor("Vector3", 3)

    override fun deserialize(decoder: Decoder): Vector3 {
        val result = Vector3()

        @Suppress("NAME_SHADOWING")
        val decoder = decoder.beginStructure(descriptor, FloatSerializer)

        var count = 0

        mainLoop@ while (true) {
            when (val index = decoder.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_ALL -> {
                    result.x = decoder.decodeFloatElement(descriptor, 0)
                    result.y = decoder.decodeFloatElement(descriptor, 1)
                    result.z = decoder.decodeFloatElement(descriptor, 2)
                    break@mainLoop
                }
                CompositeDecoder.READ_DONE -> break@mainLoop
                else -> {
                    result[index] = decoder.decodeFloatElement(descriptor, index)
                    count++
                }
            }
        }
        decoder.endStructure(descriptor)

        check(count == 3)

        return result
    }

    override fun serialize(encoder: Encoder, obj: Vector3) {
        val vecEncoder = encoder.beginCollection(descriptor, 3, FloatSerializer)
        vecEncoder.encodeFloatElement(descriptor, 0, obj.x)
        vecEncoder.encodeFloatElement(descriptor, 1, obj.y)
        vecEncoder.encodeFloatElement(descriptor, 2, obj.z)
        vecEncoder.endStructure(descriptor)
    }
}

internal object QuaternionSerializer : KSerializer<Quaternion> {
    override val descriptor: SerialDescriptor = MathDescriptor("Quaternion", 4)

    override fun deserialize(decoder: Decoder): Quaternion {
        val result = Quaternion()

        @Suppress("NAME_SHADOWING")
        val decoder = decoder.beginStructure(descriptor, FloatSerializer)

        var count = 0

        mainLoop@ while (true) {
            when (val index = decoder.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_ALL -> {
                    result.x = decoder.decodeFloatElement(descriptor, 0)
                    result.y = decoder.decodeFloatElement(descriptor, 1)
                    result.z = decoder.decodeFloatElement(descriptor, 2)
                    result.w = decoder.decodeFloatElement(descriptor, 3)
                    break@mainLoop
                }
                CompositeDecoder.READ_DONE -> break@mainLoop
                else -> {
                    when (index) {
                        0 -> result.x = decoder.decodeFloatElement(descriptor, index)
                        1 -> result.y = decoder.decodeFloatElement(descriptor, index)
                        2 -> result.z = decoder.decodeFloatElement(descriptor, index)
                        3 -> result.w = decoder.decodeFloatElement(descriptor, index)
                    }
                    count++
                }
            }
        }
        decoder.endStructure(descriptor)

        check(count == 4)

        return result
    }

    override fun serialize(encoder: Encoder, obj: Quaternion) {
        @Suppress("NAME_SHADOWING")
        val encoder = encoder.beginCollection(descriptor, 4, FloatSerializer)
        encoder.encodeFloatElement(descriptor, 0, obj.x)
        encoder.encodeFloatElement(descriptor, 1, obj.y)
        encoder.encodeFloatElement(descriptor, 2, obj.z)
        encoder.encodeFloatElement(descriptor, 3, obj.w)
        encoder.endStructure(descriptor)
    }
}
