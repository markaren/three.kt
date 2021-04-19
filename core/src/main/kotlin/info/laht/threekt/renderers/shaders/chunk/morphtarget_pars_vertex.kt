package info.laht.threekt.renderers.shaders.chunk

internal val __morphtarget_pars_vertex = """ 
 
#ifdef USE_MORPHTARGETS

	#ifndef USE_MORPHNORMALS

	uniform float morphTargetInfluences[ 8 ];

	#else

	uniform float morphTargetInfluences[ 4 ];

	#endif

#endif

 """
