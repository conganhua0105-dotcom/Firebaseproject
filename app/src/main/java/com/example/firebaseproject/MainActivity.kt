package com.example.firebaseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.example.firebaseproject.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = greenColor,
                                    titleContentColor = Color.White
                                ),
                                title = { Text(text = "GFG", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                            )
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            FirebaseUI(LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context) {
    val courseName = remember { mutableStateOf("") }
    val courseDuration = remember { mutableStateOf("") }
    val courseDescription = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(20.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = courseName.value, onValueChange = { courseName.value = it },
            placeholder = { Text(text = "Enter your course name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE3E6EE), unfocusedContainerColor = Color(0xFFE3E6EE))
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = courseDuration.value, onValueChange = { courseDuration.value = it },
            placeholder = { Text(text = "Enter your course duration") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE3E6EE), unfocusedContainerColor = Color(0xFFE3E6EE))
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = courseDescription.value, onValueChange = { courseDescription.value = it },
            placeholder = { Text(text = "Enter your course description") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE3E6EE), unfocusedContainerColor = Color(0xFFE3E6EE))
        )
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (TextUtils.isEmpty(courseName.value)) {
                    Toast.makeText(context, "Please enter course name", Toast.LENGTH_SHORT).show()
                } else {
                    addDataToFirebase(courseName.value, courseDuration.value, courseDescription.value, context)
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F628E))
        ) {
            Text(text = "Add Data", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { context.startActivity(Intent(context, CourseDetailsActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F628E))
        ) {
            Text(text = "View Courses", fontSize = 16.sp)
        }
    }
}

fun addDataToFirebase(name: String, duration: String, description: String, context: Context) {
    val db = FirebaseFirestore.getInstance()
    // Khởi tạo object Course
    val courseObj = Course(name, duration, description, "")

    db.collection("Courses").add(courseObj)
        .addOnSuccessListener { documentReference ->
            // Cập nhật ID tự động vào document
            val generatedId = documentReference.id
            db.collection("Courses").document(generatedId).update("courseID", generatedId)
            
            Toast.makeText(context, "Course Added Successfully!", Toast.LENGTH_SHORT).show()
            // Chuyển sang màn hình danh sách để xem kết quả ngay
            val i = Intent(context, CourseDetailsActivity::class.java)
            context.startActivity(i)
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Fail to add course: ${e.message}", Toast.LENGTH_LONG).show()
        }
}
