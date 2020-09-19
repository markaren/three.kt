package info.laht.threekt.math

class SphericalHarmonics3 {

    val coefficients = List(9) { Vector3() }

    fun set(coefficients: List<Vector3>): SphericalHarmonics3 {
        this.coefficients.forEachIndexed { i, v ->
            v.copy(coefficients[i])
        }
        return this
    }

    fun zero(): SphericalHarmonics3 {
        coefficients.forEach {
            it.set(0f, 0f, 0f)
        }
        return this
    }

    fun add(sh: SphericalHarmonics3): SphericalHarmonics3 {
        this.coefficients.forEachIndexed { i, v ->
            v.copy(sh.coefficients[i])
        }
        return this
    }

    fun scale(s: Float): SphericalHarmonics3 {
        this.coefficients.forEach { v ->
            v.multiplyScalar(s)
        }
        return this
    }

    fun copy(sh: SphericalHarmonics3): SphericalHarmonics3 {
        return set(sh.coefficients)
    }

    fun clone(): SphericalHarmonics3 {
        return SphericalHarmonics3().copy(this)
    }

}
