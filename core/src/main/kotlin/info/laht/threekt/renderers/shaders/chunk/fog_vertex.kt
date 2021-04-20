package info.laht.threekt.renderers.shaders.chunk

internal val __fog_vertex = """ 
 
#ifdef USE_FOG

	fogDepth = -mvPosition.z;

#endif
 """
