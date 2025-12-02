package com.example.trackcast.data.database.repository

import androidx.lifecycle.LiveData
import com.example.trackcast.data.dao.UserDao
import com.example.trackcast.data.entities.User

class UserRepository(private val userDao: UserDao) {
    // get user by ID (LiveData for observing any changes)
    fun getUser(userId: Int): LiveData<User?> {
        return userDao.getUser(userId)
    }

    // get user by ID (one-time fetch)
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    // get user by email
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    // login function
    suspend fun login(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    // check if email already exists
    suspend fun checkEmailExists(email: String): Boolean {
        return userDao.checkEmailExists(email) > 0
    }

    // insert new user
    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    // update existing user
    suspend fun update(user: User) {
        userDao.update(user)
    }

    // delete user
    suspend fun delete(user: User) {
        userDao.delete(user)
    }


}