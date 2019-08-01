
package info.laht.threekt.renderers.shaders.chunk

internal val __clipping_planes_pars_fragment = """ 
 
#if NUM_CLIPPING_PLANES > 0

	#if ! defined( PHYSICAL ) && ! defined( PHONG ) && ! defined( MATCAP )
		varying vec3 vViewPosition;
	#endif

	uniform vec4 clippingPlanes[ NUM_CLIPPING_PLANES ];

#endif

 """
