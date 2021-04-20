package info.laht.threekt.renderers.shaders.chunk

internal val __tonemapping_fragment = """ 
 
#if defined( TONE_MAPPING )

	gl_FragColor.rgb = toneMapping( gl_FragColor.rgb );

#endif

 """
