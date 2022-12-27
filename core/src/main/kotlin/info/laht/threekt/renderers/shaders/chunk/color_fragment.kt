package info.laht.threekt.renderers.shaders.chunk

internal val __color_fragment = """ 
 
#if defined( USE_COLOR_ALPHA )

	diffuseColor *= vColor;

#elif defined( USE_COLOR )

	diffuseColor.rgb *= vColor;

#endif

 """
