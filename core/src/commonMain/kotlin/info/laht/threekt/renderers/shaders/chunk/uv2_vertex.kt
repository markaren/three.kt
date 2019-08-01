
package info.laht.threekt.renderers.shaders.chunk

internal val __uv2_vertex = """ 
 
#if defined( USE_LIGHTMAP ) || defined( USE_AOMAP )

	vUv2 = uv2;

#endif

 """
