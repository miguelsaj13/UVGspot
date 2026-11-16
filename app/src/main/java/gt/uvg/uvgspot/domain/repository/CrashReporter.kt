package gt.uvg.uvgspot.domain.repository

interface CrashReporter {
    fun recordNonFatal(exception: Throwable)
}