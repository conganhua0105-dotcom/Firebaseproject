package com.example.firebaseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

class UpdateCourse : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("courseName")
        val duration = intent.getStringExtra("courseDuration")
        val description = intent.getStringExtra("courseDescription")
        val courseID = intent.getStringExtra("courseID")

        setContent {
            FirebaseprojectTheme {
                val context = LocalContext.current
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = greenColor,
                                    titleContentColor = Color.White
                                ),
                                title = { Text(text = "Update Course", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                            )
                        }
                    ) { padding ->
                        UpdateUI(context, name, duration, description, courseID, Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateUI(context: Context, name: String?, duration: String?, description: String?, courseID: String?, modifier: Modifier) {
    var courseName by remember { mutableStateOf(name ?: "") }
    var courseDuration by remember { mutableStateOf(duration ?: "") }
    var courseDescription by remember { mutableStateOf(description ?: "") }

    Column(modifier = modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = courseName, onValueChange = { courseName = it }, placeholder = { Text("Course Name") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE0E0E0), unfocusedContainerColor = Color(0xFFE0E0E0)))
        Spacer(modifier = Modifier.height(15.dp))
        TextField(value = courseDuration, onValueChange = { courseDuration = it }, placeholder = { Text("Duration") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE0E0E0), unfocusedContainerColor = Color(0xFFE0E0E0)))
        Spacer(modifier = Modifier.height(15.dp))
        TextField(value = courseDescription, onValueChange = { courseDescription = it }, placeholder = { Text("Description") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFE0E0E0), unfocusedContainerColor = Color(0xFFE0E0E0)))
        Spacer(modifier = Modifier.height(30.dp))
        
        // Nút Cập nhật
        Button(
            onClick = {
                if (courseID.isNullOrEmpty()) {
                    Toast.makeText(context, "ID missing!", Toast.LENGTH_SHORT).show()
                } else {
                    val db = FirebaseFirestore.getInstance()
                    val data = Course(courseName, courseDuration, courseDescription, courseID)
                    db.collection("Courses").document(courseID).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                            // Chuyển về trang danh sách ngay
                            context.startActivity(Intent(context, CourseDetailsActivity::class.java))
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Update Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) { Text("Update Data") }

        Spacer(modifier = Modifier.height(15.dp))

        // Nút Xem danh sách
        Button(
            onClick = { context.startActivity(Intent(context, CourseDetailsActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F628E))
        ) { Text("View Courses") }

        Spacer(modifier = Modifier.height(15.dp))

        // Nút Quay lại trang chủ
        OutlinedButton(
            onClick = {
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(25.dp)
        ) { Text("Back to Home", color = Color(0xFF6200EE)) }
    }
}
