package info.laht.threekt.core

class Clock(
    private var autoStart: Boolean = true
) {

    private var startTime = 0L
    private var oldTime = 0L
    private var elapsedTime = 0f

    var running = false
        private set

    fun start() {
        startTime = System.currentTimeMillis()
        oldTime = startTime
        elapsedTime = 0f
        running = true
    }

    fun stop() {
        getElapsedTime()
        running = false
        autoStart = false
    }

    fun getElapsedTime(): Float {
        getDelta()
        return elapsedTime
    }

    fun getDelta(): Float {
        var diff = 0f

        if ( this.autoStart && ! this.running ) {
            start()
            return 0f
        }

        if ( this.running ) {

            val newTime = System.currentTimeMillis()
            diff = ( newTime - this.oldTime ).toFloat() / 1000f
            this.oldTime = newTime

            elapsedTime += diff

        }

        return diff
    }

}
