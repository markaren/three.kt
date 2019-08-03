package info.laht.threekt.renderers.shaders.chunk

internal val __dithering_fragment = """ 
 
#if defined( DITHERING )

	gl_FragColor.rgb = dithering( gl_FragColor.rgb );

#endif
 """
