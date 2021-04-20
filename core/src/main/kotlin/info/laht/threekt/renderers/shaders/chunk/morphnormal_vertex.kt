package info.laht.threekt.renderers.shaders.chunk

internal val __morphnormal_vertex = """ 
 
#ifdef USE_MORPHNORMALS

	objectNormal += ( morphNormal0 - normal ) * morphTargetInfluences[ 0 ];
	objectNormal += ( morphNormal1 - normal ) * morphTargetInfluences[ 1 ];
	objectNormal += ( morphNormal2 - normal ) * morphTargetInfluences[ 2 ];
	objectNormal += ( morphNormal3 - normal ) * morphTargetInfluences[ 3 ];

#endif

 """
