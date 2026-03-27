package com.example.firebaseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

        setContent {
            FirebaseprojectTheme {
                val context = LocalContext.current

                val name = intent.getStringExtra("courseName")
                val duration = intent.getStringExtra("courseDuration")
                val description = intent.getStringExtra("courseDescription")
                val courseID = intent.getStringExtra("courseID")

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = greenColor,
                                    titleContentColor = Color.White
                                ),
                                title = {
                                    Text(
                                        text = "GFG",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) { padding ->
                        UpdateUI(
                            context = context,
                            name = name,
                            duration = duration,
                            description = description,
                            courseID = courseID,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateUI(
    context: Context,
    name: String?,
    duration: String?,
    description: String?,
    courseID: String?,
    modifier: Modifier = Modifier
) {
    var courseName by remember { mutableStateOf(name ?: "") }
    var courseDuration by remember { mutableStateOf(duration ?: "") }
    var courseDescription by remember { mutableStateOf(description ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = courseName,
            onValueChange = { courseName = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = courseDuration,
            onValueChange = { courseDuration = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = courseDescription,
            onValueChange = { courseDescription = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (courseName.isEmpty() || courseDuration.isEmpty() || courseDescription.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    updateData(courseID, courseName, courseDuration, courseDescription, context)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Update Data", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nút quay lại giao diện ban đầu (MainActivity)
        OutlinedButton(
            onClick = {
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Back to Home", color = Color(0xFF6200EE))
        }
    }
}

fun updateData(courseID: String?, name: String, duration: String, description: String, context: Context) {
    val db = FirebaseFirestore.getInstance()
    val updatedCourse = Course(name, duration, description, courseID)

    if (courseID != null) {
        db.collection("Courses").document(courseID)
            .set(updatedCourse)
            .addOnSuccessListener {
                Toast.makeText(context, "Course Updated successfully", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, CourseDetailsActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(context, "Fail to update course", Toast.LENGTH_SHORT).show()
            }
    }
}
