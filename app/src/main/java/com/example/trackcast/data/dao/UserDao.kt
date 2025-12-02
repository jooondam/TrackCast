package com.example.trackcast.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trackcast.data.entities.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUser(userId: Int): LiveData<User?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Int): User?

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User): Int

    @Delete
    suspend fun delete(user: User): Int

    // gets a specific user by their username
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    // authentication

    // get user by email (login)
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // get user by email and password")
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    // check if email already exists (for validation)
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun checkEmailExists(email: String): Int




}