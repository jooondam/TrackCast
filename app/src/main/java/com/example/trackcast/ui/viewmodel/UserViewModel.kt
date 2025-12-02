package com.example.trackcast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackcast.data.database.repository.UserRepository
import com.example.trackcast.data.entities.User
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // current logged in user
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // login status
    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

    // registration status
    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    // login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _loginStatus.value = LoginStatus.Success
                } else {
                    _loginStatus.value = LoginStatus.InvalidCredentials
                }
            } catch (e: Exception) {
                _loginStatus.value = LoginStatus.Error(e.message ?: "Unknown Error")
            }
        }
    }

    // register new user function
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                if (userRepository.checkEmailExists(email)) {
                    _registrationStatus.value = RegistrationStatus.EmailExists
                    return@launch
                }

                val newUser = User(userId = 0, username = name, email = email, password = password)

                val userId = userRepository.insert(newUser)
                if (userId > 0) {
                    _registrationStatus.value = RegistrationStatus.Success
                } else {
                    _registrationStatus.value = RegistrationStatus.Error("Failed to create user")
                }
            } catch (e: Exception) {
                _registrationStatus.value = RegistrationStatus.Error(e.message ?: "Unknown Error")
            }
        }
    }

    // logout
    fun logout() {
        _currentUser.value = null
        _loginStatus.value = LoginStatus.LoggedOut
    }

    // update user profile
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.update(user)
                _currentUser.value = user
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    // sealed classes for status management
    sealed class LoginStatus {
        object Success : LoginStatus()
        object InvalidCredentials : LoginStatus()
        object LoggedOut : LoginStatus()
        data class Error(val message: String) : LoginStatus()
    }

    sealed class RegistrationStatus {
        object Success : RegistrationStatus()
        object EmailExists : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }
}