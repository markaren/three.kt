package info.laht.threekt.renderers.shaders.chunk

internal val __logdepthbuf_pars_fragment = """ 
 
#if defined( USE_LOGDEPTHBUF ) && defined( USE_LOGDEPTHBUF_EXT )

	uniform float logDepthBufFC;
	varying float vFragDepth;

#endif
 """
