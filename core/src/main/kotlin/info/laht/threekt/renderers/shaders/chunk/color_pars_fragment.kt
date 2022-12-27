package info.laht.threekt.renderers.shaders.chunk

internal val __color_pars_fragment = """ 

#if defined( USE_COLOR_ALPHA )

	varying vec4 vColor;

#elif defined( USE_COLOR )

	varying vec3 vColor;

#endif

 """
