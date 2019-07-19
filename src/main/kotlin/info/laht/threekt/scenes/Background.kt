package info.laht.threekt.scenes

import info.laht.threekt.math.Color
import info.laht.threekt.renderers.GLRenderTargetCube
import info.laht.threekt.textures.CubeTexture
import info.laht.threekt.textures.Texture

sealed class Background

class ColorBackground(
    val color: Color
): Background()

class TextureBackground(
    val texture: Texture
): Background()

class CubeTextureBackground(
    val texture: CubeTexture
): Background()

class GLRenderTargetCubeBackground(
    val renderTargetCube: GLRenderTargetCube
): Background()
