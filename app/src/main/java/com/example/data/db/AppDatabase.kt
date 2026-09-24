package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "recruiter_notes")
data class RecruiterNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val candidateStrength: String,
    val note: String,
    val bookmarkedProject: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface RecruiterNoteDao {
    @Query("SELECT * FROM recruiter_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<RecruiterNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: RecruiterNoteEntity)

    @Update
    suspend fun updateNote(note: RecruiterNoteEntity)

    @Delete
    suspend fun deleteNote(note: RecruiterNoteEntity)

    @Query("DELETE FROM recruiter_notes WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Database(entities = [RecruiterNoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recruiterNoteDao(): RecruiterNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edge_ai_recruiter_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
