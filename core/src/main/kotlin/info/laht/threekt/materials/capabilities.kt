package info.laht.threekt.materials

import info.laht.threekt.math.Color

interface MaterialWithColor {

    val color: Color

}

interface MaterialWithMorphTargets {

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

interface MaterialWithLineWidth {

    var linewidth: Float

}

interface MaterialWithReflectivity {

    var reflectivity: Float
    var refractionRatio: Float

}

interface MaterialWithSpecular {

    val specular: Color
    var shininess: Float

}

interface MaterialWithDefaultAttributeValues {

    val defaultAttributeValues: MutableMap<String, Any>

}
