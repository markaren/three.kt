package info.laht.threekt.renderers.shaders.chunk

internal val __lights_phong_fragment = """ 
 
BlinnPhongMaterial material;
material.diffuseColor = diffuseColor.rgb;
material.specularColor = specular;
material.specularShininess = shininess;
material.specularStrength = specularStrength;
 """
