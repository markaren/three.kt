package info.laht.threekt.helpers

import info.laht.threekt.Colors
import info.laht.threekt.cameras.AbstractCamera
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.CameraCanUpdateProjectionMatrix
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.unproject
import info.laht.threekt.objects.LineSegments

class CameraHelper(
    val camera: Camera
) : LineSegments(BufferGeometry(), LineBasicMaterial().apply {
    color.set(0xffffff)
    vertexColors = Colors.Face
}) {

    private val pointMap = mutableMapOf<String, MutableList<Int>>()

    init {

        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()

        val colorFrustum = Color(0xffaa00)
        val colorCone = Color(0xff0000)
        val colorUp = Color(0x00aaff)
        val colorTarget = Color(0xffffff)
        val colorCross = Color(0x333333)

        fun addPoint(id: String, color: Color) {

            vertices.add(0f); vertices.add(0f); vertices.add(0f)
            colors.add(color.r); colors.add(color.g); colors.add(color.b)

            if (pointMap[id] == null) {

                pointMap[id] = mutableListOf<Int>()

            }

            pointMap[id]!!.add((vertices.size / 3) - 1)

        }

        fun addLine(a: String, b: String, color: Color) {

            addPoint(a, color)
            addPoint(b, color)

        }

        // near

        addLine("n1", "n2", colorFrustum)
        addLine("n2", "n4", colorFrustum)
        addLine("n4", "n3", colorFrustum)
        addLine("n3", "n1", colorFrustum)

        // far

        addLine("f1", "f2", colorFrustum)
        addLine("f2", "f4", colorFrustum)
        addLine("f4", "f3", colorFrustum)
        addLine("f3", "f1", colorFrustum)

        // sides

        addLine("n1", "f1", colorFrustum)
        addLine("n2", "f2", colorFrustum)
        addLine("n3", "f3", colorFrustum)
        addLine("n4", "f4", colorFrustum)

        // cone

        addLine("p", "n1", colorCone)
        addLine("p", "n2", colorCone)
        addLine("p", "n3", colorCone)
        addLine("p", "n4", colorCone)

        // up

        addLine("u1", "u2", colorUp)
        addLine("u2", "u3", colorUp)
        addLine("u3", "u1", colorUp)

        // target

        addLine("c", "t", colorTarget)
        addLine("p", "c", colorCross)

        // cross

        addLine("cn1", "cn2", colorCross)
        addLine("cn3", "cn4", colorCross)

        addLine("cf1", "cf2", colorCross)
        addLine("cf3", "cf4", colorCross)

        geometry.addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
        geometry.addAttribute("color", FloatBufferAttribute(colors.toFloatArray(), 3))

        if (camera is CameraCanUpdateProjectionMatrix) {
            camera.updateProjectionMatrix()
        }

        this.matrix = camera.matrixWorld
        this.matrixAutoUpdate = false

        update()

    }

    fun update() {

        val vector = Vector3()
        val camera = AbstractCamera()

        val position = geometry.getAttribute( "position" ) as FloatBufferAttribute
        
        fun setPoint( point: String, x: Number, y: Number, z: Number ) {

            vector.set( x, y, z ).unproject( camera )

            val points = pointMap[ point ]

            if ( points != null ) {

                for (i in 0 until points.size) {

                    position.setXYZ( points[ i ], vector.x, vector.y, vector.z )

                }

            }

        }

        val w = 1
        val h = 1

        // we need just camera projection matrix inverse
        // world matrix must be identity

        camera.projectionMatrixInverse.copy( this.camera.projectionMatrixInverse )

        // center / target

        setPoint( "c", 0, 0, - 1 )
        setPoint( "t", 0, 0, 1 )

        // near

        setPoint( "n1", - w, - h, - 1 )
        setPoint( "n2", w, - h, - 1 )
        setPoint( "n3", - w, h, - 1 )
        setPoint( "n4", w, h, - 1 )

        // far

        setPoint( "f1", - w, - h, 1 )
        setPoint( "f2", w, - h, 1 )
        setPoint( "f3", - w, h, 1 )
        setPoint( "f4", w, h, 1 )

        // up

        setPoint( "u1", w * 0.7, h * 1.1, - 1 )
        setPoint( "u2", - w * 0.7, h * 1.1, - 1 )
        setPoint( "u3", 0, h * 2, - 1 )

        // cross

        setPoint( "cf1", - w, 0, 1 )
        setPoint( "cf2", w, 0, 1 )
        setPoint( "cf3", 0, - h, 1 )
        setPoint( "cf4", 0, h, 1 )

        setPoint( "cn1", - w, 0, - 1 )
        setPoint( "cn2", w, 0, - 1 )
        setPoint( "cn3", 0, - h, - 1 )
        setPoint( "cn4", 0, h, - 1 )

        position.needsUpdate = true

    }

}
