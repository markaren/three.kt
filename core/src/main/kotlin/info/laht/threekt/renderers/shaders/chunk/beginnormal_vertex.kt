package info.laht.threekt.renderers.shaders.chunk

internal val __beginnormal_vertex = """ 
 
vec3 objectNormal = vec3( normal );

#ifdef USE_TANGENT

	vec3 objectTangent = vec3( tangent.xyz );

#endif
 """
