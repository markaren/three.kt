package info.laht.threekt.materials

class MeshPhysicalMaterial : MeshStandardMaterial() {

    var reflectivity = 0.5f; // maps to F0 = 0.04

    var clearCoat = 0.0f;
    var clearCoatRoughness = 0.0f;

    init {
        defines["PHYSICAL"] = ""
    }

}
