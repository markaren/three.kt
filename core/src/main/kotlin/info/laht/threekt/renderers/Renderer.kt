package info.laht.threekt.renderers

import info.laht.threekt.textures.Texture

interface Renderer

interface RenderTarget {
    var texture: Texture
}

interface RenderTargetCube : RenderTarget

interface Program {
    val id: Int
}

