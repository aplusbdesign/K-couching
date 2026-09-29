package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.CoachingMethod
import com.example.model.CoachingSession
import com.example.model.CoachingType
import com.example.model.FeeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CoachingSession::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun coachingDao(): CoachingDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "kac_coaching_db"
        )
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    val sampleSessions: List<CoachingSession> by lazy {
      val now = System.currentTimeMillis()
      val oneDay = 24L * 60 * 60 * 1000
      listOf(
        CoachingSession(
          clientName = "김지훈",
          clientContact = "010-3456-7890",
          coachingType = CoachingType.ONE_ON_ONE,
          groupMemberCount = 1,
          feeType = FeeType.PAID,
          coachingMethod = CoachingMethod.VIDEO,
          dateMillis = now - (oneDay * 2),
          startTime = "14:00",
          endTime = "15:00",
          durationMinutes = 60,
          sessionRound = 1,
          topic = "스타트업 팀장 승진 후 조직 위임 및 리더십 코칭",
          coachReflection = "업무 위임에 대한 불안감을 탐색하고, 팀원 신뢰 구축을 위한 작은 실험 2가지 합의."
        ),
        CoachingSession(
          clientName = "이수연",
          clientContact = "sy.lee@example.com",
          coachingType = CoachingType.ONE_ON_ONE,
          groupMemberCount = 1,
          feeType = FeeType.PAID,
          coachingMethod = CoachingMethod.FACE_TO_FACE,
          dateMillis = now - (oneDay * 5),
          startTime = "10:30",
          endTime = "12:00",
          durationMinutes = 90,
          sessionRound = 2,
          topic = "커리어 전환 및 3개년 성장 로드맵 수립",
          coachReflection = "자신의 강점 역량을 구조화하고, 올해 상반기 집중할 핵심 프로젝트 선정 완료."
        ),
        CoachingSession(
          clientName = "박민우",
          clientContact = "010-9876-5432",
          coachingType = CoachingType.ONE_ON_ONE,
          groupMemberCount = 1,
          feeType = FeeType.PRO_BONO,
          coachingMethod = CoachingMethod.CALL,
          dateMillis = now - (oneDay * 8),
          startTime = "19:00",
          endTime = "20:00",
          durationMinutes = 60,
          sessionRound = 1,
          topic = "일과 삶의 균형 및 번아웃 회복 코칭 (프로보노)",
          coachReflection = "에너지 고갈 요인을 시각화하고 주말 충전 루틴을 스스로 정의함."
        )
      )
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            database.coachingDao().insertSessions(sampleSessions)
          }
        }
      }
    }
  }
}
