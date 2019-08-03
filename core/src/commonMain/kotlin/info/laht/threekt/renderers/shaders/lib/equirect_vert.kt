package info.laht.threekt.renderers.shaders.lib

internal val __equirect_vert = """ 
 
varying vec3 vWorldDirection;

#include <common>

void main() {

	vWorldDirection = transformDirection( position, modelMatrix );

	#include <begin_vertex>
	#include <project_vertex>

}

 """
