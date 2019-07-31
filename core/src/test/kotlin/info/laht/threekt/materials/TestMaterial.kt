package info.laht.threekt.materials

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestMaterial {

    @Test
    fun testType() {
        Assertions.assertEquals("ShaderMaterial", ShaderMaterial().type)
    }

}
