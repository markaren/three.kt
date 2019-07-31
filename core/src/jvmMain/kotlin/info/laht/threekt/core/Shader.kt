package info.laht.threekt.core

class Shader(
    val name: String,
    val uniforms: MutableMap<String, Uniform>,
    val vertexShader: String,
    val fragmentShader: String
) {

    constructor (
        uniforms: MutableMap<String, Uniform>,
        vertexShader: String,
        fragmentShader: String
    ) : this("", uniforms, vertexShader, fragmentShader)

}
