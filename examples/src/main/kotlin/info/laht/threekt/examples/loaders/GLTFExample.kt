package info.laht.threekt.examples.loaders

import info.laht.threekt.loaders.GLTFLoader

object GLTFExample {

    @JvmStatic
    fun main(args: Array<String>) {

        GLTFLoader().load(GLTFExample::class.java.classLoader.getResource("models/gltf/Duck/GLTF/Duck.gltf").file)

    }

}