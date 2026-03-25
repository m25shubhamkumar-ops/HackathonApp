package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeSessionDao {

    @Query("SELECT * FROM practice_sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession)

    @Query("DELETE FROM practice_sessions WHERE id = :id")
    suspend fun deleteSession(id: Int)
}
