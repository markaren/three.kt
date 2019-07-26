package info.laht.threekt.extras.core

import info.laht.threekt.math.Vector3

abstract class Curve3 {

    var needsUpdate = false

    internal val type = javaClass.simpleName

    private val arcLengthDivisions = 200
    private var cacheArcLengths: FloatArray? = null


    abstract fun getPoint(t: Float, optionalTarget: Vector3 = Vector3()): Vector3

    fun getPointAt(u: Float, optionalTarget: Vector3 = Vector3()): Vector3 {
        val t = getUtoTmapping(u)
        return getPoint(t, optionalTarget)
    }

    fun getPoints ( divisions: Int = 5 ): List<Vector3> {

        return (0..divisions).map {
            getPoint(it.toFloat())
        }

    }

    fun getSpacedPoints ( divisions: Int = 5 ): List<Vector3> {

        return (0..divisions).map {
            getPointAt(it.toFloat())
        }

    }

    fun getLength(): Float {
        val lenghts = getLengths()
        return lenghts[lenghts.size - 1]
    }

    fun getLengths(divisions: Int = arcLengthDivisions): FloatArray {

        if (this.cacheArcLengths != null &&
            (this.cacheArcLengths?.size == divisions + 1) &&
            !this.needsUpdate
        ) {

            return this.cacheArcLengths!!;

        }

        this.needsUpdate = false;

        val cache = FloatArray(divisions);
        var last = this.getPoint(0f);
        var sum = 0f;

        cache[0] = 0f;

        for (p in 1..divisions) {

            val current = this.getPoint((p / divisions).toFloat());
            sum += current.distanceTo(last);
            cache[p] = sum;
            last = current;

        }

        this.cacheArcLengths = cache;

        return cache; // { sums: cache, sum: sum }; Sum is in the last element.

    }

    fun updateArcLengths() {
        needsUpdate = true
        getLengths()
    }

    fun getUtoTmapping(u: Float, distance: Float? = null): Float {
        val arcLengths = this.getLengths();

        val il = arcLengths.size

        val targetArcLength = if (distance != null) distance else u * arcLengths[il - 1];

        // binary search for the index with largest value smaller than target u distance

        var low = 0
        var high = il - 1

        var i = 0
        while (low <= high) {

            i = low + (high - low) / 2

            val comparison = arcLengths[i] - targetArcLength;

            if (comparison < 0) {

                low = i + 1;

            } else if (comparison > 0) {

                high = i - 1;

            } else {

                high = i;
                break;

                // DONE

            }

        }

        i = high;

        if (arcLengths[i] == targetArcLength) {

            return i.toFloat() / (il - 1);

        }

        // we could get finer grain at lengths, or use simple interpolation between two points

        val lengthBefore = arcLengths[i];
        val lengthAfter = arcLengths[i + 1];

        val segmentLength = lengthAfter - lengthBefore;

        // determine where we are between the 'before' and 'after' points

        val segmentFraction = (targetArcLength - lengthBefore) / segmentLength;

        // add that fractional amount to t

        val t = (i + segmentFraction) / (il - 1);

        return t;
    }

}