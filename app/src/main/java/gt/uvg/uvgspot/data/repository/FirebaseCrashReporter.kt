package gt.uvg.uvgspot.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import gt.uvg.uvgspot.domain.repository.CrashReporter

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics
) : CrashReporter {

    override fun recordNonFatal(exception: Throwable) {
        try {
            crashlytics.recordException(exception)
        } catch (_: Exception) {
        }
    }
}