package info.laht.threekt.renderers.shaders

internal object ShaderChunk {

    val alphamap_fragment by lazy {
        load("alphamap_fragment")
    }

    val alphamap_pars_fragment by lazy {
        load("alphamap_pars_fragment")
    }

    val alphatest_fragment by lazy {
        load("alphatest_fragment")
    }

    val aomap_fragment by lazy {
        load("aomap_fragment")
    }

    val aomap_pars_fragment by lazy {
        load("aomap_pars_fragment")
    }

    val beginnormal_vertex by lazy {
        load("beginnormal_vertex")
    }

    val begin_vertex by lazy {
        load("begin_vertex")
    }

    val bsdfs by lazy {
        load("bsdfs")
    }

    val bumpmap_pars_fragment by lazy {
        load("bumpmap_pars_fragment")
    }

    val clipping_planes_fragment by lazy {
        load("clipping_planes_fragment")
    }

    val clipping_planes_pars_fragment by lazy {
        load("clipping_planes_pars_fragment")
    }

    val clipping_planes_pars_vertex by lazy {
        load("clipping_planes_pars_vertex")
    }

    val clipping_planes_vertex by lazy {
        load("clipping_planes_vertex")
    }

    val color_fragment by lazy {
        load("color_fragment")
    }

    val color_pars_fragment by lazy {
        load("color_pars_fragment")
    }

    val color_pars_vertex by lazy {
        load("color_pars_vertex")
    }

    val color_vertex by lazy {
        load("color_vertex")
    }

    val common by lazy {
        load("common")
    }

    val cube_uv_reflection_fragment by lazy {
        load("cube_uv_reflection_fragment")
    }

    val defaultnormal_vertex by lazy {
        load("defaultnormal_vertex")
    }

    val default_fragment by lazy {
        load("default_fragment")
    }

    val default_vertex by lazy {
        load("default_vertex")
    }

    val displacementmap_pars_vertex by lazy {
        load("displacementmap_pars_vertex")
    }

    val displacementmap_vertex by lazy {
        load("displacementmap_vertex")
    }

    val dithering_fragment by lazy {
        load("dithering_fragment")
    }

    val dithering_pars_fragment by lazy {
        load("dithering_pars_fragment")
    }

    val emissivemap_fragment by lazy {
        load("emissivemap_fragment")
    }

    val emissivemap_pars_fragment by lazy {
        load("emissivemap_pars_fragment")
    }

    val encodings_fragment by lazy {
        load("encodings_fragment")
    }

    val encodings_pars_fragment by lazy {
        load("encodings_pars_fragment")
    }

    val envmap_fragment by lazy {
        load("envmap_fragment")
    }

    val envmap_pars_fragment by lazy {
        load("envmap_pars_fragment")
    }

    val envmap_pars_vertex by lazy {
        load("envmap_pars_vertex")
    }

    val envmap_physical_pars_fragment by lazy {
        load("envmap_physical_pars_fragment")
    }

    val envmap_vertex by lazy {
        load("envmap_vertex")
    }

    val fog_fragment by lazy {
        load("fog_fragment")
    }

    val fog_pars_fragment by lazy {
        load("fog_pars_fragment")
    }

    val fog_pars_vertex by lazy {
        load("fog_pars_vertex")
    }

    val fog_vertex by lazy {
        load("fog_vertex")
    }

    val gradientmap_pars_fragment by lazy {
        load("gradientmap_pars_fragment")
    }

    val lightmap_fragment by lazy {
        load("lightmap_fragment")
    }

    val lightmap_pars_fragment by lazy {
        load("lightmap_pars_fragment")
    }

    val lights_fragment_begin by lazy {
        load("lights_fragment_begin")
    }

    val lights_fragment_end by lazy {
        load("lights_fragment_end")
    }

    val lights_fragment_maps by lazy {
        load("lights_fragment_maps")
    }

    val lights_lambert_vertex by lazy {
        load("lights_lambert_vertex")
    }

    val lights_pars_begin by lazy {
        load("lights_pars_begin")
    }

    val lights_phong_fragment by lazy {
        load("lights_phong_fragment")
    }

    val lights_phong_pars_fragment by lazy {
        load("lights_phong_pars_fragment")
    }

    val lights_physical_fragment by lazy {
        load("lights_physical_fragment")
    }

    val lights_physical_pars_fragment by lazy {
        load("lights_physical_pars_fragment")
    }

    val logdepthbuf_fragment by lazy {
        load("logdepthbuf_fragment")
    }

    val logdepthbuf_pars_fragment by lazy {
        load("logdepthbuf_pars_fragment")
    }

    val logdepthbuf_pars_vertex by lazy {
        load("logdepthbuf_pars_vertex")
    }

    val logdepthbuf_vertex by lazy {
        load("logdepthbuf_vertex")
    }

    val map_fragment by lazy {
        load("map_fragment")
    }

    val map_pars_fragment by lazy {
        load("map_pars_fragment")
    }

    val map_particle_fragment by lazy {
        load("map_particle_fragment")
    }

    val map_particle_pars_fragment by lazy {
        load("map_particle_pars_fragment")
    }

    val metalnessmap_fragment by lazy {
        load("metalnessmap_fragment")
    }

    val metalnessmap_pars_fragment by lazy {
        load("metalnessmap_pars_fragment")
    }

    val morphnormal_vertex by lazy {
        load("morphnormal_vertex")
    }

    val morphtarget_pars_vertex by lazy {
        load("morphtarget_pars_vertex")
    }

    val morphtarget_vertex by lazy {
        load("morphtarget_vertex")
    }

    val normalmap_pars_fragment by lazy {
        load("normalmap_pars_fragment")
    }

    val normal_fragment_begin by lazy {
        load("normal_fragment_begin")
    }

    val normal_fragment_maps by lazy {
        load("normal_fragment_maps")
    }

    val packing by lazy {
        load("packing")
    }

    val premultiplied_alpha_fragment by lazy {
        load("premultiplied_alpha_fragment")
    }

    val project_vertex by lazy {
        load("project_vertex")
    }

    val roughnessmap_fragment by lazy {
        load("roughnessmap_fragment")
    }

    val roughnessmap_pars_fragment by lazy {
        load("roughnessmap_pars_fragment")
    }

    val shadowmap_pars_fragment by lazy {
        load("shadowmap_pars_fragment")
    }

    val shadowmap_pars_vertex by lazy {
        load("shadowmap_pars_vertex")
    }

    val shadowmap_vertex by lazy {
        load("shadowmap_vertex")
    }

    val shadowmask_pars_fragment by lazy {
        load("shadowmask_pars_fragment")
    }

    val skinbase_vertex by lazy {
        load("skinbase_vertex")
    }

    val skinning_pars_vertex by lazy {
        load("skinning_pars_vertex")
    }

    val skinning_vertex by lazy {
        load("skinning_vertex")
    }

    val skinnormal_vertex by lazy {
        load("skinnormal_vertex")
    }

    val specularmap_fragment by lazy {
        load("specularmap_fragment")
    }

    val specularmap_pars_fragment by lazy {
        load("specularmap_pars_fragment")
    }

    val tonemapping_fragment by lazy {
        load("tonemapping_fragment")
    }

    val tonemapping_pars_fragment by lazy {
        load("tonemapping_pars_fragment")
    }

    val uv_pars_fragment by lazy {
        load("uv_pars_fragment")
    }

    val uv_pars_vertex by lazy {
        load("uv_pars_vertex")
    }

    val uv_vertex by lazy {
        load("uv_vertex")
    }

    val uv2_pars_fragment by lazy {
        load("uv2_pars_fragment")
    }

    val uv2_pars_vertex by lazy {
        load("uv2_pars_vertex")
    }

    val uv2_vertex by lazy {
        load("uv2_vertex")
    }

    val worldpos_vertex by lazy {
        load("worldpos_vertex")
    }


    val background_frag by lazy {
        load("background_frag")
    }

    val background_vert by lazy {
        load("background_vert")
    }

    val cube_frag by lazy {
        load("cube_frag")
    }

    val cube_vert by lazy {
        load("cube_vert")
    }

    val depth_frag by lazy {
        load("depth_frag")
    }

    val depth_vert by lazy {
        load("depth_vert")
    }

    val distanceRGBA_frag by lazy {
        load("distanceRGBA_frag")
    }

    val distanceRGBA_vert by lazy {
        load("distanceRGBA_vert")
    }

    val equirect_frag by lazy {
        load("equirect_frag")
    }

    val equirect_vert by lazy {
        load("equirect_vert")
    }

    val linedashed_frag by lazy {
        load("linedashed_frag")
    }

    val linedashed_vert by lazy {
        load("linedashed_vert")
    }

    val meshbasic_frag by lazy {
        load("meshbasic_frag")
    }
    val meshbasic_vert by lazy {
        load("meshbasic_vert")
    }

    val meshlambert_frag by lazy {
        load("meshlambert_frag")
    }

    val meshlambert_vert by lazy {
        load("meshlambert_vert")
    }

    val meshmatcap_frag by lazy {
        load("meshmatcap_frag")
    }

    val meshmatcap_vert by lazy {
        load("meshmatcap_vert")
    }

    val meshphong_frag by lazy {
        load("meshphong_frag")
    }

    val meshphong_vert by lazy {
        load("meshphong_vert")
    }

    val meshphysical_frag by lazy {
        load("meshphysical_frag")
    }

    val meshphysical_vert by lazy {
        load("meshphysical_vert")
    }

    val normal_frag by lazy {
        load("normal_frag")
    }

    val normal_vert by lazy {
        load("normal_vert")
    }

    val points_frag by lazy {
        load("points_frag")
    }

    val points_vert by lazy {
        load("points_vert")
    }

    val shadow_frag by lazy {
        load("shadow_frag")
    }

    val shadow_vert by lazy {
        load("shadow_vert")
    }

    val sprite_frag by lazy {
        load("sprite_frag")
    }

    val sprite_vert by lazy {
        load("sprite_vert")
    }

    operator fun get(name: String): String? {

        return try {

            val method = ShaderChunk::class.java.getDeclaredMethod("get${name.capitalize()}")
            method.invoke(this) as String

        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }

    }

    private fun load(name: String): String {

        val path = if (name.endsWith("vert") || name.endsWith("frag")) {
            "shaders/lib/$name.glsl"
        } else {
            "shaders/chunk/$name.glsl"
        }

        return ShaderChunk::class.java.classLoader.getResourceAsStream(path)
            .bufferedReader().use {
                it.readText()
            }

    }

}
