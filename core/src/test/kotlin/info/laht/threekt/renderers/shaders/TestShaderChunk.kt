package info.laht.threekt.renderers.shaders

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestShaderChunk {

    @Test
    fun test() {

        Assertions.assertNotNull(ShaderChunk["tonemapping_pars_fragment"])

    }

}