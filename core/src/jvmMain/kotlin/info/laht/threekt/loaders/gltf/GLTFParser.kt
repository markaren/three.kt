package info.laht.threekt.loaders.gltf

internal class GLTFParser(
    val json: GLTF
) {

    private val cache = mutableMapOf<String, Any>()
    private val primitiveCache = mutableMapOf<String, Any>()

    fun parse(json: String) {

        cache.clear()


    }


    private fun getDependency(type: String, index: Int): Any {

        val cacheKey = "$type:$index"
        var dependency = cache[cacheKey]

        if (dependency == null) {

            when (type) {

                "scene" -> dependency = loadScene(index)
                "node" -> dependency = loadNode(index)
                "mesh" -> dependency = loadMesh(index)
                "accessor" -> dependency = loadAccessor(index)
                "bufferView" -> dependency = loadBufferView(index)
                "buffer" -> dependency = loadBuffer(index)
                "material" -> dependency = loadMaterial(index)
                "texture" -> dependency = loadTexture(index)
                "skin" -> dependency = loadSkin(index)
                "animation" -> dependency = loadAnimation(index)
                "camera" -> dependency = loadCamera(index)
                "light" -> dependency = loadLight(index)
                else -> throw error("Unknown type: $type")
            }

            cache[cacheKey] = dependency

        }

        return dependency

    }


    private fun getDependencies(type: String) {

        var dependencies = cache[type]

        if (dependencies == null) {

            val defs = json[type + if (type == "mesh") "es" else "s"] as List<Any>? ?: emptyList()

        }

    }


    private fun loadBuffer(index: Int): GLTFBuffer {
        TODO()
    }


    private fun loadBufferView(index: Int): GLTFBufferView {
        TODO()
    }


    private fun loadLight(index: Int): Any {
        TODO()
    }

    private fun loadCamera(index: Int): GLTFCamera {
        TODO()
    }

    private fun loadAnimation(index: Int): Any {
        TODO()
    }

    private fun loadSkin(index: Int): Any {
        TODO()
    }

    private fun loadTexture(index: Int): GLTFTexture {
        TODO()
    }

    private fun loadMaterial(index: Int): GLTFMaterial {
        TODO()
    }


    private fun loadAccessor(index: Int): GLTFAccessor {
        TODO()
    }

    private fun loadMesh(index: Int): GLTFMesh {
        TODO()
    }

    private fun loadNode(index: Int): GLTFNode {
        TODO()
    }

    private fun loadScene(index: Int): GLTFScene {
        TODO()
    }

}
