package info.laht.threekt.renderers.shaders

import info.laht.threekt.core.Uniform

internal fun cloneUniforms(src: Map<String, Uniform>): MutableMap<String, Uniform> {

    val dst = mutableMapOf<String, Uniform>()

    for ((u, v) in src) {

        dst[u] = v.clone()

    }

    return dst

}

internal fun mergeUniforms(uniforms: List<Map<String, Uniform>>): MutableMap<String, Uniform> {

    val merged = mutableMapOf<String, Uniform>()

    for (u in 0 until uniforms.size) {

        val tmp = cloneUniforms(uniforms[u])

        for ((p, v) in tmp) {
            merged[p] = v
        }

    }

    return merged

}
