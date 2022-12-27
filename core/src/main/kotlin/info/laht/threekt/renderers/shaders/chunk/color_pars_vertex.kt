package info.laht.threekt.renderers.shaders.chunk

internal val __color_pars_vertex = """ 

#if defined( USE_COLOR_ALPHA )

	varying vec4 vColor;

#elif defined( USE_COLOR ) || defined( USE_INSTANCING_COLOR )

	varying vec3 vColor;

#endif

 """
