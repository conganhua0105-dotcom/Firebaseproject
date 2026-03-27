package com.example.firebaseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.example.firebaseproject.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseprojectTheme {
                val context = LocalContext.current
                val courseList = remember { mutableStateListOf<Course>() }
                val db = FirebaseFirestore.getInstance()

                // Load dữ liệu từ Firestore
                LaunchedEffect(Unit) {
                    db.collection("Courses")
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                courseList.clear()
                                for (d in result.documents) {
                                    val c = d.toObject(Course::class.java)
                                    if (c != null) {
                                        c.courseID = d.id // Gán Document ID
                                        courseList.add(c)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Fail to load data", Toast.LENGTH_SHORT).show()
                        }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = greenColor,
                                titleContentColor = Color.White
                            ),
                            title = {
                                Text(
                                    text = "Course List",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        FirebaseUI(context, courseList)
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context, courseList: SnapshotStateList<Course>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(courseList) { _, item ->
            Card(
                onClick = {
                    // Khi click vào item, chuyển sang màn hình Update và truyền dữ liệu
                    val i = Intent(context, UpdateCourse::class.java)
                    i.putExtra("courseName", item.courseName)
                    i.putExtra("courseDuration", item.courseDuration)
                    i.putExtra("courseDescription", item.courseDescription)
                    i.putExtra("courseID", item.courseID)
                    context.startActivity(i)
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.courseName ?: "",
                        color = greenColor,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.courseDuration ?: "", color = Color.Black, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.courseDescription ?: "", color = Color.Black, fontSize = 15.sp)
                }
            }
        }
    }
}
