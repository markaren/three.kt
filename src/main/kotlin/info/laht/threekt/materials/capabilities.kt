package info.laht.threekt.materials

import info.laht.threekt.math.Color

interface MaterialWithColor {

    val color: Color

}

interface MaterialWithMorphTarget {

    val morphTargets: Boolean

}

interface MaterialWithMorphNormals {

    val morphNormals: Boolean

}

interface MaterialWithSkinning {

    val skinning: Boolean

}

interface MaterialWithClipping {

    val clipping: Boolean

}

interface MaterialWithSizeAttenuation {

    val sizeAttenuation: Boolean

}

interface MaterialWithEmissive {

    val emissive: Color
    var emissiveIntensity: Float

}

interface MaterialWithWireframe {

    var wireframe: Boolean
    var wireframeLinewidth: Float

}