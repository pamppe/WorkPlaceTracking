package com.emill.workplacetracking.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.AuthResponse
import com.emill.workplacetracking.MyAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.emill.workplacetracking.RequestState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterViewModel(private val apiService: MyAPI, private val context: Context) : ViewModel() {
    val registerRequestState: MutableStateFlow<RequestState<AuthResponse>> =
        MutableStateFlow(RequestState.Empty)

    fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        salary: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val namePart = name.toRequestBody()
            val emailPart = email.toRequestBody()
            val passwordPart = password.toRequestBody()
            val phonePart = phone.toRequestBody()
            val salaryPart = salary.toRequestBody()
            val picturePart = imageUri?.let { uriToMultipartBodyPart(it, context) }

            registerRequestState.value = RequestState.Loading
            try {
                val response = apiService.registerUser(
                    namePart,
                    emailPart,
                    passwordPart,
                    phonePart,
                    salaryPart,
                    picturePart
                )
                if (response.isSuccessful && response.body() != null) {
                    registerRequestState.value = RequestState.Success(response.body()!!)
                } else {
                    throw Exception("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                registerRequestState.value = RequestState.Error(e)
            }
        }
    }
    private fun uriToMultipartBodyPart(uri: Uri, context: Context): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        val inputStream = contentResolver.openInputStream(uri)
        val requestBody = inputStream?.readBytes()?.toRequestBody(mimeType?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("picture", "filename.jpg", requestBody!!)
    }
}