package info.laht.threekt.renderers.shaders.chunk

internal val __lights_fragment_end = """ 
 
#if defined( RE_IndirectDiffuse )

	RE_IndirectDiffuse( irradiance, geometry, material, reflectedLight );

#endif

#if defined( RE_IndirectSpecular )

	RE_IndirectSpecular( radiance, irradiance, clearCoatRadiance, geometry, material, reflectedLight );

#endif
 """
