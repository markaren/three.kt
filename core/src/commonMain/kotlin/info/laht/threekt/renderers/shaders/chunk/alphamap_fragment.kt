
package info.laht.threekt.renderers.shaders.chunk

internal val __alphamap_fragment = """ 
 
#ifdef USE_ALPHAMAP

	diffuseColor.a *= texture2D( alphaMap, vUv ).g;

#endif
 """
