
package info.laht.threekt.renderers.shaders.chunk

internal val __worldpos_vertex = """ 
 
#if defined( USE_ENVMAP ) || defined( DISTANCE ) || defined ( USE_SHADOWMAP )

	vec4 worldPosition = modelMatrix * vec4( transformed, 1.0 );

#endif

 """
