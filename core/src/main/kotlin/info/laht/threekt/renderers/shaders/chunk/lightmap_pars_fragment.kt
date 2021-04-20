package info.laht.threekt.renderers.shaders.chunk

internal val __lightmap_pars_fragment = """ 
 
#ifdef USE_LIGHTMAP

	uniform sampler2D lightMap;
	uniform float lightMapIntensity;

#endif
 """
