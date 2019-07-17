package info.laht.threekt.materials

import info.laht.threekt.BasicDepthPacking

class MeshDepthMaterial(
    parameters: MaterialParameters? = null
): Material(parameters) {

    val depthPacking = BasicDepthPacking

    val skinning = false
    val morphTargets = false

    val map = null

    val alphaMap = null

    val displacementMap = null
    val displacementScale = 1
    val displacementBias = 0

    val wireframe = false
    val wireframeLinewidth = 1

    init {

        fog = false
        lights = false

    }
    
}