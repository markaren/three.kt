package info.laht.threekt.renderers.shaders.chunk

internal val __lightmap_fragment = """ 
 
#ifdef USE_LIGHTMAP

	reflectedLight.indirectDiffuse += PI * texture2D( lightMap, vUv2 ).xyz * lightMapIntensity; // factor of PI should not be present; included here to prevent breakage

#endif
 """
