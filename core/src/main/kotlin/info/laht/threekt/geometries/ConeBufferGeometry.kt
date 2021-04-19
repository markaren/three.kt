package info.laht.threekt.geometries

class ConeBufferGeometry(
        radius: Float? = null,
        height: Float? = null,
        radialSegments: Int? = null,
        heightSegments: Int? = null,
        openEnded: Boolean? = null,
        thetaStart: Float? = null,
        thetaLength: Float? = null
) : CylinderBufferGeometry(0f, radius, height, radialSegments, heightSegments, openEnded, thetaStart, thetaLength) {

    override fun clone(): ConeBufferGeometry {
        return super.clone() as ConeBufferGeometry
    }

}
