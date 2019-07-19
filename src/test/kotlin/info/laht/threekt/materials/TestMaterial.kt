package info.laht.threekt.materials

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestMaterial {

    @Test
    fun testReflection() {

        val mat = MeshDepthMaterial() as Material
        Assertions.assertFalse(mat.get<Boolean>("skinning")!!)
        mat["skinning"] = true
        Assertions.assertTrue(mat.get<Boolean>("skinning")!!)

    }

}