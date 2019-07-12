package info.laht.threekt.core

class Clock(
    private var autoStart: Boolean = true
) {

    private var startTime = 0L
    private var oldTime = 0L
    private var elapsedTime = 0.0

    var running = false
        private set

    fun start() {
        startTime = System.currentTimeMillis()
        oldTime = startTime
        elapsedTime = 0.0
        running = true
    }

    fun stop() {
        getElapsedTime()
        running = false
        autoStart = false
    }

    fun getElapsedTime(): Double {
        getDelta()
        return elapsedTime
    }

    fun getDelta(): Double {
        var diff = 0.0

        if ( this.autoStart && ! this.running ) {
            start()
            return 0.0
        }

        if ( this.running ) {

            val newTime = System.currentTimeMillis()
            diff = ( newTime - this.oldTime ).toDouble() / 1000.0
            this.oldTime = newTime;

            elapsedTime += diff;

        }

        return diff
    }

}
