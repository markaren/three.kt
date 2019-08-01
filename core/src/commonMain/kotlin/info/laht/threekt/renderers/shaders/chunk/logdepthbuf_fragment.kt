
package info.laht.threekt.renderers.shaders.chunk

internal val __logdepthbuf_fragment = """ 
 
#if defined( USE_LOGDEPTHBUF ) && defined( USE_LOGDEPTHBUF_EXT )

	gl_FragDepthEXT = log2( vFragDepth ) * logDepthBufFC * 0.5;

#endif
 """
