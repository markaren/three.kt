package info.laht.threekt.renderers.shaders.chunk

internal val __aomap_pars_fragment = """ 
 
#ifdef USE_AOMAP

	uniform sampler2D aoMap;
	uniform float aoMapIntensity;

#endif
 """
