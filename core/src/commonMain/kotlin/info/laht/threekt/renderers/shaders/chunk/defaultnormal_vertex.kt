
package info.laht.threekt.renderers.shaders.chunk

internal val __defaultnormal_vertex = """ 
 
vec3 transformedNormal = normalMatrix * objectNormal;

#ifdef FLIP_SIDED

	transformedNormal = - transformedNormal;

#endif

#ifdef USE_TANGENT

	vec3 transformedTangent = normalMatrix * objectTangent;

	#ifdef FLIP_SIDED

		transformedTangent = - transformedTangent;

	#endif

#endif
 """
