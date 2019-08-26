package info.laht.threekt.loaders.gltf

import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.float
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerializerTests {
    private val json = Json(JsonConfiguration.Stable)

    @Test
    fun testVectorDeserialize() {
        // language=json
        val testJson = "[1.0, 2.0, 3.0]"

        val vector = json.parse(Vector3Serializer, testJson)

        assertEquals(1.0f, vector.x)
        assertEquals(2.0f, vector.y)
        assertEquals(3.0f, vector.z)
    }

    @Test
    fun testVectorSerialize() {
        val vector = Vector3(3.0f, 5.0f, -90f)

        val outputJson = json.toJson(Vector3Serializer, vector)

        assertTrue(outputJson is JsonArray)

        assertEquals(vector.x, outputJson[0].float)
        assertEquals(vector.y, outputJson[1].float)
        assertEquals(vector.z, outputJson[2].float)
    }

    @Test
    fun testQuaternionDeserialize() {
        // language=json
        val testJson = "[1.0, 2.0, 3.0, -0.5]"

        val quaternion = json.parse(QuaternionSerializer, testJson)

        assertEquals( 1.0f, quaternion.x)
        assertEquals( 2.0f, quaternion.y)
        assertEquals( 3.0f, quaternion.z)
        assertEquals(-0.5f, quaternion.w)
    }

    @Test
    fun testQuaternionSerialize() {
        val quaternion = Quaternion(3.0f, 5.0f, -90f, -0.7f)

        val outputJson = json.toJson(QuaternionSerializer, quaternion)

        assertTrue(outputJson is JsonArray)

        assertEquals(quaternion.x, outputJson[0].float)
        assertEquals(quaternion.y, outputJson[1].float)
        assertEquals(quaternion.z, outputJson[2].float)
        assertEquals(quaternion.w, outputJson[3].float)
    }
}
