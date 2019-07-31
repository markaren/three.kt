package info.laht.threekt.cameras

class OrthographicCamera(
    var left: Float = -1f,
    var right: Float = 1f,
    var top: Float = 1f,
    var bottom: Float = -1f,
    override var near: Float = 0.1f,
    override var far: Float = 2000f
) : AbstractCamera(), CameraWithZoom, CameraWithNearAndFar, CameraCanUpdateProjectionMatrix {

    override var zoom: Float = 1f

    private var view: View? = null

    init {
        updateProjectionMatrix()
    }

    override fun clone(): OrthographicCamera {
        return OrthographicCamera().copy(this)
    }

    fun copy ( source: OrthographicCamera ): OrthographicCamera {

        super<AbstractCamera>.copy(source, true)

        this.left = source.left;
        this.right = source.right;
        this.top = source.top;
        this.bottom = source.bottom;
        this.near = source.near;
        this.far = source.far;

        this.zoom = source.zoom;

        source.view?.also { this.view = it.copy() }

        return this

    }

    fun setViewOffset(fullWidth: Int, fullHeight: Int, x: Int, y: Int, width: Int, height: Int) {
        if (this.view === null) {

            this.view = View(
                enabled = true,
                fullWidth = 1,
                fullHeight = 1,
                offsetX = 0,
                offsetY = 0,
                width = 1,
                height = 1
            )

        }

        view?.also { view ->
            view.enabled = true;
            view.fullWidth = fullWidth;
            view.fullHeight = fullHeight;
            view.offsetX = x;
            view.offsetY = y;
            view.width = width;
            view.height = height;
        }

        this.updateProjectionMatrix();
    }

    fun clearViewOffset() {

        this.view?.also {
            it.enabled = false;
        }

        this.updateProjectionMatrix();
    }

    override fun updateProjectionMatrix() {
        val dx = (this.right - this.left) / (2 * this.zoom);
        val dy = (this.top - this.bottom) / (2 * this.zoom);
        val cx = (this.right + this.left) / 2;
        val cy = (this.top + this.bottom) / 2;

        var left = cx - dx;
        var right = cx + dx;
        var top = cy + dy;
        var bottom = cy - dy;

        this.view?.also {  view ->

            if (view.enabled) {

                val zoomW = this.zoom / (view.width / view.fullWidth);
                val zoomH = this.zoom / (view.height / view.fullHeight);
                val scaleW = (this.right - this.left) / view.width;
                val scaleH = (this.top - this.bottom) / view.height;

                left += scaleW * (view.offsetX / zoomW);
                right = left + scaleW * (view.width / zoomW);
                top -= scaleH * (view.offsetY / zoomH);
                bottom = top - scaleH * (view.height / zoomH);

            }

        }

        this.projectionMatrix.makeOrthographic(left, right, top, bottom, this.near, this.far);

        this.projectionMatrixInverse.getInverse(this.projectionMatrix);
    }
}