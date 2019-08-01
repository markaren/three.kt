
package info.laht.threekt.renderers.shaders.chunk

internal val __fog_pars_fragment = """ 
 
#ifdef USE_FOG

	uniform vec3 fogColor;
	varying float fogDepth;

	#ifdef FOG_EXP2

		uniform float fogDensity;

	#else

		uniform float fogNear;
		uniform float fogFar;

	#endif

#endif
 """
