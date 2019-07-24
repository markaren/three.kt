# three.kt (Work in progress)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/three.kt/issues)

[![Gitter](https://badges.gitter.im/markaren/three.kt.svg)](https://gitter.im/markaren/three.kt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Kotlin/JVM port of the popular [three.js](http://threejs.org) 3D library ([r106](https://github.com/mrdoob/three.js/tree/r106)).

Be warned, while the basics works, alot of features are still missing, like controls, textures, 3D model loaders etc.

## Looking for the Kotlin/JS wrapper project?
It has been renamed and moved to [here](https://github.com/markaren/three-kt-wrapper).


#### API (subject to changes)

```kotlin

Canvas().use { canvas ->

    val scene = Scene().apply {
        background = ColorBackground(Color.aliceblue)
    }

    val camera = PerspectiveCamera(75, canvas.width.toFloat() / canvas.height, 0.1, 1000)
    val renderer = GLRenderer(canvas).apply {
        checkShaderErrors = true
    }

    val box = Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
        color.set(0x00ff00)
    }).also {
        scene.add(it)
    }

    val sphere = Mesh(SphereGeometry(0.5f), MeshBasicMaterial().apply {
        color.set(Color.rebeccapurple)
    }).also {
        it.position.x += 2f
        scene.add(it)
    }

    camera.position.z = 5f

    val clock = Clock()
    while (!canvas.shouldClose()) {
        renderer.render(scene, camera)
        box.rotation.x += 1f * clock.getDelta().toFloat()
        box.rotation.y += 1f * clock.getDelta().toFloat()
    }
    
}

```
