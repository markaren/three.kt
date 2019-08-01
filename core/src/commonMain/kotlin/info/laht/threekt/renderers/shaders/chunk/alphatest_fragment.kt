
package info.laht.threekt.renderers.shaders.chunk

internal val __alphatest_fragment = """ 
 
#ifdef ALPHATEST

	if ( diffuseColor.a < ALPHATEST ) discard;

#endif
 """
