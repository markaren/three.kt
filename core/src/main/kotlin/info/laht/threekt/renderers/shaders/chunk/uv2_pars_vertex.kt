package info.laht.threekt.renderers.shaders.chunk

internal val __uv2_pars_vertex = """ 
 
#if defined( USE_LIGHTMAP ) || defined( USE_AOMAP )

	attribute vec2 uv2;
	varying vec2 vUv2;

#endif

 """
