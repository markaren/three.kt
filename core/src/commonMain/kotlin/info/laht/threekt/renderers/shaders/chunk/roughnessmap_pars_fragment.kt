
package info.laht.threekt.renderers.shaders.chunk

internal val __roughnessmap_pars_fragment = """ 
 
#ifdef USE_ROUGHNESSMAP

	uniform sampler2D roughnessMap;

#endif

 """
