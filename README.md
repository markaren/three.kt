# three.kt (Work in progress)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/three.kt/issues)

[![Gitter](https://badges.gitter.im/markaren/three.kt.svg)](https://gitter.im/markaren/three.kt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![CircleCI](https://circleci.com/gh/markaren/three.kt.svg?style=svg)](https://circleci.com/gh/markaren/three.kt)

Kotlin/JVM port of the popular [three.js](http://threejs.org) 3D library ([r106](https://github.com/mrdoob/three.js/tree/r106)).

Be warned, while the basics works, such as:
* Primitives, Points and TubeGeometry
* All materials and lights
* OrbitControls
* 2D textures
* OBJ, MTL and STL loaders
* Other stuff like mirror, sky and water shaders
 
a lot of features are still missing and the API can change rapidly.

Right now, this is mostly interesting for developers that want to contribute.


#### API (subject to changes)

##### Kotlin

```kotlin

Canvas().use { canvas ->

    val scene = Scene().apply {
        setBackground(Color.aliceblue)
    }

    val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).apply {
        position.z = 5f
    }

    val renderer = GLRenderer(canvas.width, canvas.height)

    val box = Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
        color.set(0x00ff00)
    })
    scene.add(box)
    
    val clock = Clock()
    val controls = OrbitControls(camera, canvas)
    canvas.animate {
     
        val dt = clock.getDelta()
        box.rotation.x += 1f * dt
        box.rotation.y += 1f * dt

        renderer.render(scene, camera)

    }

}
```

##### java

```java
public class JavaExample {

    public static void main(String[] args) {

        try (Canvas canvas = new Canvas()) {

            Scene scene = new Scene();
            PerspectiveCamera camera = new PerspectiveCamera();
            camera.getPosition().z = 5;
            GLRenderer renderer = new GLRenderer(canvas.getWidth(), canvas.getHeight());

            BoxBufferGeometry boxBufferGeometry = new BoxBufferGeometry();
            MeshPhongMaterial boxMaterial = new MeshPhongMaterial();
            boxMaterial.getColor().set(Color.getRoyalblue());

            Mesh box = new Mesh(boxBufferGeometry, boxMaterial);
            scene.add(box);


            MeshBasicMaterial wireframeMaterial = new MeshBasicMaterial();
            wireframeMaterial.getColor().set(0x000000);
            wireframeMaterial.setWireframe(true);
            Mesh wireframe = new Mesh(box.getGeometry().clone(), wireframeMaterial);
            scene.add(wireframe);

            AmbientLight light = new AmbientLight();
            scene.add(light);

            OrbitControls orbitControls = new OrbitControls(camera, canvas);

            while (!canvas.windowShouldClose()) {

                renderer.render(scene, camera);

                canvas.pollEvents();
                canvas.swapBuffers();

            }

        }

    }

}

```

## Screenshots

![seascape](https://raw.githubusercontent.com/markaren/three.kt/master/screenshots/seascape.PNG)
![points](https://raw.githubusercontent.com/markaren/three.kt/master/screenshots/points.PNG)
![ocean](https://raw.githubusercontent.com/markaren/three.kt/master/screenshots/ocean.PNG)
![pointlights](https://raw.githubusercontent.com/markaren/three.kt/master/screenshots/pointlights.PNG)
![textures](https://raw.githubusercontent.com/markaren/three.kt/master/screenshots/textures.PNG)

## Looking for the Kotlin/JS wrapper project?
It has been renamed and moved to [here](https://github.com/markaren/three-kt-wrapper).
