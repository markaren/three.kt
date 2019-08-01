
package info.laht.threekt.renderers.shaders.chunk

internal val __color_vertex = """ 
 
#ifdef USE_COLOR

	vColor.xyz = color.xyz;

#endif

 """
