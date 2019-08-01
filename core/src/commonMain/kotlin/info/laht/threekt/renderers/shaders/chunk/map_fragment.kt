
package info.laht.threekt.renderers.shaders.chunk

internal val __map_fragment = """ 
 
#ifdef USE_MAP

	vec4 texelColor = texture2D( map, vUv );

	texelColor = mapTexelToLinear( texelColor );
	diffuseColor *= texelColor;

#endif
 """
