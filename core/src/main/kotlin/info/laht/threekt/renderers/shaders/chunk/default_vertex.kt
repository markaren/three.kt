package info.laht.threekt.renderers.shaders.chunk

internal val __default_vertex = """ 
 
void main() {
	gl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );
}
 """
