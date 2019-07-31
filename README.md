# three.kt (Work in progress)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/three.kt/issues)


[![Gitter](https://badges.gitter.im/markaren/three.kt.svg)](https://gitter.im/markaren/three.kt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![CircleCI](https://circleci.com/gh/markaren/three.kt.svg?style=svg)](https://circleci.com/gh/markaren/three.kt)

Kotlin/JVM port of the popular [three.js](http://threejs.org) 3D library ([r106](https://github.com/mrdoob/three.js/tree/r106)).

Be warned, while the basics works, such as:
* Primitives, Points and TubeGeometry (Only BufferGeometries are supported)
* Most materials
* OrbitControls
* Ambient, Directional, Point and Spot lights
* 2D textures
* Plain RenderTarget and Reflector
 
a lot of features are still missing and the API can change rapidly.

Right now, this is mostly interesting for developers that want to contribute.

### How to run the samples?

This project has been setup as a kotlin multi-platform project. 
Due to some issue loading files from the resources folder in this context, 
`right click -> run` will not work. As a temporary workaround, 
please run the associated gradle tasks instead. 
E.g. `BasicExample.kt` will have an associated `runBasicExample` gradle task.  

#### API (subject to changes)

```kotlin

Canvas().use { canvas ->

    val scene = Scene().apply {
        setBackground(Color.aliceblue)
    }

    val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
    val renderer = GLRenderer(canvas).apply {
        checkShaderErrors = true
    }

    val box = Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
        color.set(0x00ff00)
    }).also {
        scene.add(it)
    }

    camera.position.z = 5f
    
    val controls = OrbitControls(camera, canvas)

    val clock = Clock()
    while (!canvas.shouldClose()) {
        renderer.render(scene, camera)
        
        val dt = clock.getDelta()
        box.rotation.x += 1f * dt
        box.rotation.y += 1f * dt
    }
    
}
```

## Looking for the Kotlin/JS wrapper project?
It has been renamed and moved to [here](https://github.com/markaren/three-kt-wrapper).
