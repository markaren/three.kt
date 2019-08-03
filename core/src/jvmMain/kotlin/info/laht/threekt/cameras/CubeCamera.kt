package info.laht.threekt.cameras

import info.laht.threekt.TextureFilter
import info.laht.threekt.TextureFormat
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.math.Vector3
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderTargetCube
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

private const val fov = 90f
private const val aspect = 1f

class CubeCamera(
    near: Float,
    far: Float,
    cubeResolution: Int,
    options: GLRenderTarget.Options? = null
) : Object3DImpl() {

    var renderTarget: GLRenderTargetCube

    private val cameraPX: PerspectiveCamera
    private val cameraNX: PerspectiveCamera

    private val cameraPY: PerspectiveCamera
    private val cameraNY: PerspectiveCamera

    private val cameraPZ: PerspectiveCamera
    private val cameraNZ: PerspectiveCamera


    init {

        cameraPX = PerspectiveCamera(fov, aspect, near, far)
        cameraPX.up.set(0, -1, 0)
        cameraPX.lookAt(Vector3(1, 0, 0))
        this.add(cameraPX)

        cameraNX = PerspectiveCamera(fov, aspect, near, far)
        cameraNX.up.set(0, -1, 0)
        cameraNX.lookAt(Vector3(-1, 0, 0))
        this.add(cameraNX)

        cameraPY = PerspectiveCamera(fov, aspect, near, far)
        cameraPY.up.set(0, 0, 1)
        cameraPY.lookAt(Vector3(0, 1, 0))
        this.add(cameraPY)

        cameraNY = PerspectiveCamera(fov, aspect, near, far)
        cameraNY.up.set(0, 0, -1)
        cameraNY.lookAt(Vector3(0, -1, 0))
        this.add(cameraNY)

        cameraPZ = PerspectiveCamera(fov, aspect, near, far)
        cameraPZ.up.set(0, -1, 0)
        cameraPZ.lookAt(Vector3(0, 0, 1))
        this.add(cameraPZ)

        cameraNZ = PerspectiveCamera(fov, aspect, near, far)
        cameraNZ.up.set(0, -1, 0)
        cameraNZ.lookAt(Vector3(0, 0, -1))
        this.add(cameraNZ)

        val options = options ?: GLRenderTarget.Options(
            format = TextureFormat.RGB,
            minFilter = TextureFilter.Linear,
            magFilter = TextureFilter.Linear
        )

        renderTarget = GLRenderTargetCube(cubeResolution, cubeResolution, options)
        renderTarget.texture.name = "CubeCamera"

    }

    fun update(renderer: GLRenderer, scene: Scene) {

        if (this.parent == null) {
            this.updateMatrixWorld()
        }

        val currentRenderTarget = renderer.getRenderTarget()

        val renderTarget = this.renderTarget
        val generateMipmaps = renderTarget.texture.generateMipmaps

        renderTarget.texture.generateMipmaps = false

        renderer.setRenderTarget(renderTarget, 0)
        renderer.render(scene, cameraPX)

        renderer.setRenderTarget(renderTarget, 1)
        renderer.render(scene, cameraNX)

        renderer.setRenderTarget(renderTarget, 2)
        renderer.render(scene, cameraPY)

        renderer.setRenderTarget(renderTarget, 3)
        renderer.render(scene, cameraNY)

        renderer.setRenderTarget(renderTarget, 4)
        renderer.render(scene, cameraPZ)

        renderTarget.texture.generateMipmaps = generateMipmaps

        renderer.setRenderTarget(renderTarget, 5)
        renderer.render(scene, cameraNZ)

        renderer.setRenderTarget(currentRenderTarget)

    }

    fun clear(renderer: GLRenderer, color: Boolean, depth: Boolean, stencil: Boolean) {

        val currentRenderTarget = renderer.getRenderTarget()

        val renderTarget = this.renderTarget

        for (i in 0 until 6) {

            renderer.setRenderTarget(renderTarget, i)

            renderer.clear(color, depth, stencil)

        }

        renderer.setRenderTarget(currentRenderTarget)

    }


}
