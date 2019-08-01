
package info.laht.threekt.renderers.shaders.chunk

internal val __displacementmap_vertex = """ 
 
#ifdef USE_DISPLACEMENTMAP

	transformed += normalize( objectNormal ) * ( texture2D( displacementMap, uv ).x * displacementScale + displacementBias );

#endif
 """
