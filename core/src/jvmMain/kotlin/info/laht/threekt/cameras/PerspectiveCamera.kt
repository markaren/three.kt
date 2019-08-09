package info.laht.threekt.cameras

import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.math.RAD2DEG
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

class PerspectiveCamera(
        fov: Number = DEFAULT_FOV,
        aspect: Number = DEFAULT_ASPECT,
        near: Number = DEFAULT_NEAR,
        far: Number = DEFAULT_FAR
) : AbstractCamera(), CameraWithZoom, CameraWithNearAndFar, CameraCanUpdateProjectionMatrix {

    var fov: Float = fov.toFloat()
    var aspect: Float = aspect.toFloat()
    override var near: Float = near.toFloat()
    override var far: Float = far.toFloat()

    override var zoom = 1f
    var focus = 10f
    var filmGauge = 35 // width of the film (default in millimeters)
    var filmOffset = 0 // horizontal film offset (same unit as gauge)

    var view: View? = null

    init {
        updateProjectionMatrix()
    }

    /**
     * Sets the FOV by focal length in respect to the current .filmGauge.
     *
     * The default film gauge is 35, so that the focal length can be specified for
     * a 35mm (full frame) camera.
     *
     * Values for focal length and film gauge must have the same unit.
     */
    fun setFocalLength(focalLength: Int) {

        // see http://www.bobatkins.com/photography/technical/field_of_view.html
        val vExtentSlope = 0.5f * this.getFilmHeight() / focalLength

        this.fov = RAD2DEG * 2 * atan(vExtentSlope)
        this.updateProjectionMatrix()

    }

    /**
     * Calculates the focal length from the current .fov and .filmGauge.
     */
    fun getFocalLength(): Float {

        val vExtentSlope = tan(DEG2RAD * 0.5.toFloat() * this.fov)
        return 0.5f * this.getFilmHeight() / vExtentSlope

    }

    fun getEffectiveFOV(): Float {

        return RAD2DEG * 2 * atan(tan(DEG2RAD * 0.5f * this.fov) / this.zoom)

    }

    fun getFilmWidth(): Float {

        // film not completely covered in portrait format (aspect < 1)
        return this.filmGauge * min(this.aspect, 1.toFloat())

    }

    fun getFilmHeight(): Float {

        // film not completely covered in landscape format (aspect > 1)
        return this.filmGauge / max(this.aspect, 1f)

    }

    /**
     * Sets an offset in a larger frustum. This is useful for multi-pointer or multi-monitor/multi-machine setups.
     * For example, if you have 3x2 monitors and each monitor is 1920x1080 and the monitors are in grid like this:
     *
     *		 +---+---+---+
     *		 | A | B | C |
     *		 +---+---+---+
     *		 | D | E | F |
     *		 +---+---+---+
     *
     * then for each monitor you would call it like this:
     *
     *		 var w = 1920;
     *		 var h = 1080;
     *		 var fullWidth = w * 3;
     *		 var fullHeight = h * 2;
     *
     *		 // A
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 0, h * 0, w, h );
     *		 // B
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 1, h * 0, w, h );
     *		 // C
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 2, h * 0, w, h );
     *		 // D
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 0, h * 1, w, h );
     *		 // E
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 1, h * 1, w, h );
     *		 // F
     *		 camera.setViewOffset( fullWidth, fullHeight, w * 2, h * 1, w, h ); Note there is no reason monitors have to be the same size or in a grid.
     *
     * @param fullWidth full width of multiview setup
     * @param fullHeight full height of multiview setup
     * @param x horizontal offset of subcamera
     * @param y vertical offset of subcamera
     * @param width width of subcamera
     * @param height height of subcamera
     */
    fun setViewOffset(
        fullWidth: Int,
        fullHeight: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        this.aspect = fullWidth.toFloat() / fullHeight

        if (view == null) {
            view = View(
                enabled = true,
                fullWidth = 1,
                fullHeight = 1,
                offsetX = 0,
                offsetY = 0,
                width = 1,
                height = 1
            )
        }

        view?.also {
            it.enabled = true
            it.fullWidth = fullWidth
            it.fullHeight = fullHeight
            it.offsetX = x
            it.offsetY = y
            it.width = width
            it.height = height
        }

        this.updateProjectionMatrix()

    }

    fun clearViewOffset() {
        view?.apply {
            enabled = false
        }
        this.updateProjectionMatrix()
    }

    /**
     * Updates the camera projection matrix. Must be called after change of parameters.
     */
    override fun updateProjectionMatrix() {
        val near = this.near
        var top = near * tan(DEG2RAD * 0.5 * this.fov).toFloat() / this.zoom
        var height = 2 * top
        var width = this.aspect * height
        var left = -0.5f * width

        view?.also {
            if (it.enabled) {
                left += it.offsetX * width / it.fullWidth
                top -= it.offsetY * height / it.fullHeight
                width *= it.width / it.fullWidth
                height *= it.height / it.fullHeight
            }
        }

        val skew = this.filmOffset
        if (skew != 0) {
            left += near * skew / this.getFilmWidth()
        }

        this.projectionMatrix.makePerspective(left, left + width, top, top - height, near, this.far)

        this.projectionMatrixInverse.getInverse(this.projectionMatrix)
    }

    fun copy(source: PerspectiveCamera, recursive: Boolean): PerspectiveCamera {

        super<AbstractCamera>.copy(source, recursive)

        this.fov = source.fov
        this.zoom = source.zoom

        this.near = source.near
        this.far = source.far
        this.focus = source.focus

        this.aspect = source.aspect
        this.view = source.view?.copy()

        this.filmGauge = source.filmGauge
        this.filmOffset = source.filmOffset

        return this
    }

    override fun clone(): PerspectiveCamera {
        return PerspectiveCamera().copy(this, true)
    }

    override fun toString(): String {
        return "PerspectiveCamera(fov=$fov, aspect=$aspect, near=$near, far=$far, zoom=$zoom, focus=$focus, filmGauge=$filmGauge, filmOffset=$filmOffset)"
    }


    internal companion object {

        internal val DEFAULT_FOV = 50f
        internal val DEFAULT_ASPECT = 1f
        internal val DEFAULT_NEAR = 0.1f
        internal val DEFAULT_FAR = 2000f

    }

}
