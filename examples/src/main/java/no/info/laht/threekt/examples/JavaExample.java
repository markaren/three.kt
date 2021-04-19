package no.info.laht.threekt.examples;

import info.laht.threekt.Window;
import info.laht.threekt.cameras.PerspectiveCamera;
import info.laht.threekt.controls.OrbitControls;
import info.laht.threekt.geometries.BoxBufferGeometry;
import info.laht.threekt.lights.AmbientLight;
import info.laht.threekt.materials.MeshBasicMaterial;
import info.laht.threekt.materials.MeshPhongMaterial;
import info.laht.threekt.math.Color;
import info.laht.threekt.objects.Mesh;
import info.laht.threekt.renderers.GLRenderer;
import info.laht.threekt.scenes.Scene;

public class JavaExample {

    public static void main(String[] args) {

        try (Window window = new Window()) {

            Scene scene = new Scene();
            PerspectiveCamera camera = new PerspectiveCamera();
            camera.getPosition().z = 5;
            GLRenderer renderer = new GLRenderer(window.getSize());

            BoxBufferGeometry boxBufferGeometry = new BoxBufferGeometry();
            MeshPhongMaterial boxMaterial = new MeshPhongMaterial();
            boxMaterial.getColor().set(Color.royalblue);

            Mesh box = new Mesh(boxBufferGeometry, boxMaterial);
            scene.add(box);

            MeshBasicMaterial wireframeMaterial = new MeshBasicMaterial();
            wireframeMaterial.getColor().set(0x000000);
            wireframeMaterial.setWireframe(true);
            Mesh wireframe = new Mesh(box.getGeometry().clone(), wireframeMaterial);
            scene.add(wireframe);

            AmbientLight light = new AmbientLight();
            scene.add(light);

            new OrbitControls(camera, window);

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                window.close();
            }).start();

            window.onCloseCallback = () -> {
                System.out.println("Window closed");
            };

            window.animate(() -> {
                renderer.render(scene, camera);
            });

        }

    }

}
