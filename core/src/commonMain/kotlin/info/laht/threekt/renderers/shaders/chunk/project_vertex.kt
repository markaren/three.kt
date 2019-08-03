package info.laht.threekt.renderers.shaders.chunk

internal val __project_vertex = """ 
 
vec4 mvPosition = modelViewMatrix * vec4( transformed, 1.0 );

gl_Position = projectionMatrix * mvPosition;

 """
