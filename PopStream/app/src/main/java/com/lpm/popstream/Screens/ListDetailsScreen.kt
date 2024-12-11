package com.lpm.popstream.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.lpm.popstream.Model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsScreen(navController: NavController, listName: String, movies: MutableList<Movie>) {
    var searchQuery by remember { mutableStateOf("") } // Para buscar dentro de la lista
    var updatedMovies by remember { mutableStateOf(movies.toList()) } // Estado para actualizar la lista
    var errorMessage by remember { mutableStateOf("") } // Mensaje de error para películas duplicadas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la lista
        Text(
            text = "Lista: $listName",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar en la lista") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
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
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Filtrar películas según la búsqueda
        val filteredMovies =
            updatedMovies.filter { it.title.contains(searchQuery, ignoreCase = true) }

        // Lista de películas
        LazyColumn {
            items(filteredMovies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Imagen de la película
                        Image(
                            painter = rememberAsyncImagePainter(movie.getPosterUrl()),
                            contentDescription = "Carátula de ${movie.title}",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            // Título de la película
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                maxLines = 2
                            )
                        }

                        // Botón para eliminar la película
                        IconButton(onClick = {
                            movies.remove(movie)
                            updatedMovies = movies.toList() // Actualizar la lista
                            errorMessage = "" // Limpiar mensaje de error si existía
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar película",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
