package gt.uvg.uvgspot.data

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import gt.uvg.uvgspot.data.local.UVGspotDatabase
import gt.uvg.uvgspot.data.repository.FirebaseCrashReporter
import gt.uvg.uvgspot.data.repository.FirebaseSessionRepository
import gt.uvg.uvgspot.data.repository.FirestoreAvailabilityRepository
import gt.uvg.uvgspot.data.repository.FirestoreStudySpaceRepository
import gt.uvg.uvgspot.data.repository.RoomFavoritesRepository
import gt.uvg.uvgspot.domain.repository.AvailabilityRepository
import gt.uvg.uvgspot.domain.repository.CrashReporter
import gt.uvg.uvgspot.domain.repository.FavoritesRepository
import gt.uvg.uvgspot.domain.repository.SessionRepository
import gt.uvg.uvgspot.domain.repository.StudySpaceRepository

class DataRepositories(context: Context) {

    private val crashReporter: CrashReporter =
        FirebaseCrashReporter(FirebaseCrashlytics.getInstance())

    private val firestore = FirebaseFirestore.getInstance()

    private val database = Room.databaseBuilder(
        context.applicationContext,
        UVGspotDatabase::class.java,
        "uvgspot.db"
    ).build()

    val session: SessionRepository = FirebaseSessionRepository(
        FirebaseAuth.getInstance(),
        crashReporter
    )

    val studySpaces: StudySpaceRepository = FirestoreStudySpaceRepository(
        firestore,
        crashReporter
    )

    val favorites: FavoritesRepository = RoomFavoritesRepository(
        database.favoritesDao(),
        crashReporter
    )

    val availability: AvailabilityRepository = FirestoreAvailabilityRepository(
        firestore,
        session,
        context.applicationContext.getSharedPreferences(
            "report_submissions",
            Context.MODE_PRIVATE
        ),
        crashReporter
    )
}