package info.laht.threekt.renderers.shaders.chunk

internal val __color_fragment = """ 
 
#ifdef USE_COLOR

	diffuseColor.rgb *= vColor;

#endif

 """
