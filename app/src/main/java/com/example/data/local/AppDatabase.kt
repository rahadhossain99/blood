package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.BloodRequest
import com.example.data.model.Donor
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors WHERE isCurrentUser = 0 ORDER BY name ASC")
    fun getOtherDonorsFlow(): Flow<List<Donor>>

    @Query("SELECT * FROM donors ORDER BY name ASC")
    fun getAllDonorsFlow(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<Donor?>

    @Query("SELECT * FROM donors WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): Donor?

    @Query("SELECT * FROM donors WHERE email = :email LIMIT 1")
    suspend fun getDonorByEmail(email: String): Donor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<Donor>)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Query("SELECT COUNT(*) FROM donors WHERE isCurrentUser = 0")
    suspend fun getOtherDonorCount(): Int

    @Query("DELETE FROM donors WHERE isCurrentUser = 1")
    suspend fun deleteCurrentUser()

    @Query("DELETE FROM donors WHERE id = :id")
    suspend fun deleteDonorById(id: Int)
}

@Dao
interface BloodRequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY timestamp DESC")
    fun getAllRequestsFlow(): Flow<List<BloodRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequest)

    @Delete
    suspend fun deleteRequest(request: BloodRequest)
}

@Database(entities = [Donor::class, BloodRequest::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun bloodRequestDao(): BloodRequestDao
}
