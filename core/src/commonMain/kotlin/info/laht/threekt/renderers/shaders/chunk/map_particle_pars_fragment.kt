
package info.laht.threekt.renderers.shaders.chunk

internal val __map_particle_pars_fragment = """ 
 
#ifdef USE_MAP

	uniform mat3 uvTransform;
	uniform sampler2D map;

#endif

 """
