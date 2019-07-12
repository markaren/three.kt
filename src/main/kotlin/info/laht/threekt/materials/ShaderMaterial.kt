package info.laht.threekt.materials

import info.laht.threekt.renderers.renderers.shaders.ShaderChunk

class ShaderMaterial : Material() {

    var vertexShader = ShaderChunk.default_vertex
    var fragmentShader = ShaderChunk.default_fragment

    var linewidth = 1

    var wireframe = false
    var wireframeLinewidth = 1

    override var fog = false; // set to use scene fog
    override var lights = false; // set to use scene lights
    var clipping = false; // set to use user-defined clipping planes

    var skinning = false; // set to use skinning attribute streams
    var morphTargets = false; // set to use morph targets
    var morphNormals = false; // set to use morph normals

    internal var extensions = mapOf(
        "derivatives" to false,
        "fragDepth" to false,
        "drawBuffers" to false,
        "shaderTextureLOD" to false
    )

    val uniformsNeedUpdate = false

    fun copy(source: ShaderMaterial): ShaderMaterial {
        super.copy(source)

        this.fragmentShader = source.fragmentShader;
        this.vertexShader = source.vertexShader;

        this.wireframe = source.wireframe;
        this.wireframeLinewidth = source.wireframeLinewidth;

        this.lights = source.lights;
        this.clipping = source.clipping;

        this.skinning = source.skinning;

        this.morphTargets = source.morphTargets;
        this.morphNormals = source.morphNormals;

        this.extensions = source.extensions;

        return this
    }

}