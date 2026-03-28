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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

                // Sử dụng SnapshotListener để load dữ liệu THẬT từ Cloud nhanh hơn
                DisposableEffect(Unit) {
                    val listener = db.collection("Courses")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                courseList.clear()
                                for (d in snapshot.documents) {
                                    val c = d.toObject(Course::class.java)
                                    if (c != null) {
                                        c.courseID = d.id
                                        courseList.add(c)
                                    }
                                }
                            }
                        }
                    onDispose { listener.remove() }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = greenColor,
                                titleContentColor = Color.White
                            ),
                            title = { Text("Course List") }
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
    val db = FirebaseFirestore.getInstance()
    
    if (courseList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading or No Data...", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(courseList) { _, item ->
                Card(
                    onClick = {
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
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.courseName ?: "",
                                color = greenColor,
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(text = "${item.courseDuration}", fontSize = 14.sp)
                            Text(text = "${item.courseDescription}", fontSize = 14.sp, color = Color.Gray)
                        }
                        
                        // Nút xóa từng mục
                        IconButton(onClick = {
                            item.courseID?.let { id ->
                                db.collection("Courses").document(id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
