package com.lpm.popstream.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.lpm.popstream.Model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListsScreen(navController: NavController, lists: SnapshotStateMap<String, MutableList<Movie>>) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Mis Listas",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.Red),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar lista") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar lista",
                        tint = Color.Red
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red.copy(alpha = 0.5f),
                    focusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color.White else Color.Black,
                    unfocusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color.White else Color.Black
                )
            )

            val filteredLists = lists.keys.filter { it.contains(searchQuery, ignoreCase = true) }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredLists) { listName ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = listName,
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { navController.navigate("listDetails/$listName") }
                            )
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        lists.remove(listName)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar lista",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = Color.Red,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Lista")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (newListName.isBlank()) {
                                    errorMessage = "El nombre de la lista no puede estar vacío."
                                } else if (lists.containsKey(newListName)) {
                                    errorMessage = "Ya existe una lista con este nombre."
                                } else {
                                    showDialog = false
                                    showProgress = true
                                    delay(1000) // Simulación de creación de lista
                                    lists[newListName] = mutableListOf()
                                    showProgress = false
                                    newListName = ""
                                }
                                if (errorMessage.isNotEmpty()) {
                                    delay(3000) // Mensaje de error por 3 segundos
                                    errorMessage = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Crear", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                title = { Text("Nueva Lista") },
                text = {
                    Column {
                        TextField(
                            value = newListName,
                            onValueChange = { newListName = it },
                            label = { Text("Nombre de la lista", color = Color.Red) },
                            singleLine = true
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            )
        }

        if (showProgress) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red, strokeWidth = 4.dp)
            }
        }
    }
}
