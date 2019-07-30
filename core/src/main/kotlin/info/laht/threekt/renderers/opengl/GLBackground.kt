package info.laht.threekt.renderers.opengl

import info.laht.threekt.Side
import info.laht.threekt.cameras.Camera
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.renderers.shaders.ShaderLib
import info.laht.threekt.renderers.shaders.cloneUniforms
import info.laht.threekt.scenes.*
import info.laht.threekt.textures.Texture

internal class GLBackground (
    private val renderer: GLRenderer,
    private val state: GLState,
    private val objects: GLObjects
) {

    val clearColor = Color(0x000000)
    var clearAlpha = 0f
        set(value) {
            field = value
            setClear(clearColor, clearAlpha)
        }

    private var planeMesh: Mesh? = null
    private var boxMesh: Mesh? = null

    private var currentBackground: Background? = null
    private var currentBackgroundVersion = 0

    fun render(renderList: GLRenderList, scene: Scene, camera: Camera, forceClear: Boolean) {

        @Suppress("NAME_SHADOWING")
        var forceClear = forceClear
        val background = scene.background

        if (background == null) {

            setClear(clearColor, clearAlpha)
            currentBackground = null
            currentBackgroundVersion = 0

        } else if (background is ColorBackground) {

            setClear(background.color, 1f)
            forceClear = true
            currentBackground = null
            currentBackgroundVersion = 0

        }

        if (renderer.autoClear || forceClear) {
            renderer.clear(renderer.autoClearColor, renderer.autoClearDepth, renderer.autoClearStencil);
        }

        if (background is CubeTextureBackground || background is GLRenderTargetCubeBackground) {

            val boxMesh = this.boxMesh ?: Mesh(
                BoxBufferGeometry(1f, 1f, 1f),
                ShaderMaterial().apply {
                    type = "BackgroundCubeMaterial"
                    uniforms.putAll(cloneUniforms(ShaderLib.cube.uniforms))
                    vertexShader = ShaderLib.cube.vertexShader
                    fragmentShader = ShaderLib.cube.fragmentShader
                    side = Side.Back
                    depthTest = false
                    depthWrite = false
                    fog = false
                }).also {
                this.boxMesh = it
            }

            boxMesh.geometry.removeAttribute("normal")
            boxMesh.geometry.removeAttribute("uv")

            boxMesh.onBeforeRender = { _, _, camera, _, _, _ ->
                boxMesh.matrixWorld.copyPosition(camera.matrixWorld);
            }

            val material = boxMesh.material as ShaderMaterial
            material.map = material.uniforms["tCube"]?.value as Texture
            objects.update(boxMesh);

            val texture = when (background) {
                is TextureBackground -> background.texture
                is GLRenderTargetCubeBackground -> background.renderTargetCube.texture
                else -> throw IllegalStateException()
            }

//            val renderTargetCube = if (background is GLRenderTargetCubeBackground) background.renderTargetCube else background;
            material.uniforms["tCube"]?.value = texture;
            material.uniforms["tFlip"]?.value = if (background is GLRenderTargetCubeBackground) 1 else -1;

            if (currentBackground != background ||
                currentBackgroundVersion != texture.version
            ) {

                boxMesh.material.needsUpdate = true;

                currentBackground = background;
                currentBackgroundVersion = texture.version;

            }

            // push to the pre-sorted opaque render list
            renderList.unshift(boxMesh, boxMesh.geometry, boxMesh.material, 0, 0f, null);

        } else if (background is TextureBackground) {

            val texture = background.texture

            val planeMesh = this.planeMesh ?: Mesh(
                PlaneBufferGeometry(2f, 2f),
                ShaderMaterial().apply {
                    type = "BackgroundMaterial"
                    uniforms.putAll(cloneUniforms(ShaderLib.background.uniforms))
                    vertexShader = ShaderLib.background.vertexShader
                    fragmentShader = ShaderLib.background.fragmentShader
                    side = Side.Front
                    depthTest = false
                    depthWrite = false
                    fog = false
                }
            ).also {
                this.planeMesh = it
            }


            planeMesh.geometry.removeAttribute("normal");

            val material = planeMesh.material as ShaderMaterial
            material.map = material.uniforms["t2D"]?.value as Texture

            objects.update(planeMesh);

            material.uniforms["t2D"]?.value = texture;

            if (texture.matrixAutoUpdate) {

                texture.updateMatrix();

            }

            (material.uniforms["uvTransform"]?.value as Matrix3).copy(texture.matrix);

            if (currentBackground != background ||
                currentBackgroundVersion != texture.version
            ) {

                planeMesh.material.needsUpdate = true;

                currentBackground = background;
                currentBackgroundVersion = texture.version;

            }


            // push to the pre-sorted opaque render list
            renderList.unshift(planeMesh, planeMesh.geometry, planeMesh.material, 0, 0f, null);

        }

    }

    private fun setClear(color: Color, alpha: Float) {
        state.colorBuffer.setClear(color.r, color.g, color.b, alpha)
    }

    fun setClearColor(color: Color, alpha: Float = 1f) {
        clearColor.set(color)
        clearAlpha = alpha
        setClear(clearColor, clearAlpha)
    }

}
