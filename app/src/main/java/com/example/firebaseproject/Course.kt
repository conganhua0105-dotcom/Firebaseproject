package com.example.firebaseproject

data class Course(
    var courseName: String? = "",
    var courseDuration: String? = "",
    var courseDescription: String? = "",
    var courseID: String? = "" // Thêm ID để quản lý
)
