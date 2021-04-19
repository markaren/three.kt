package info.laht.threekt.renderers.shaders.chunk

internal val __specularmap_fragment = """ 
 
float specularStrength;

#ifdef USE_SPECULARMAP

	vec4 texelSpecular = texture2D( specularMap, vUv );
	specularStrength = texelSpecular.r;

#else

	specularStrength = 1.0;

#endif

 """
