package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    // Gibt einen Flow zurück: bei jeder Änderung in der Tabelle kommt automatisch die
    // neue Liste, die UI aktualisiert sich dadurch von selbst. Neueste Favoriten zuerst

    @Query("SELECT * FROM favorites ORDER BY id DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    // Einmaliges Auslesen (ohne Flow), brauche ich für den Refresh
    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<FavoriteEntity>

    // IGNORE: Existiert der Name schon (Unique-Index), wird nichts eingefügt
    // und Room gibt -1 zurück, um Duplikate zu erkennen
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity): Long

    @Update
    suspend fun update(favorite: FavoriteEntity)

    @Delete
    suspend fun delete(favorite: FavoriteEntity)
}