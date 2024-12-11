package com.lpm.popstream.Screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lpm.popstream.Model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchingScreen(
    navController: NavController,
    watchingMovies: MutableList<Movie>
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var filteredMovies by remember { mutableStateOf(watchingMovies) }

    LaunchedEffect(searchQuery) {
        // Filtrar las películas por el texto ingresado en la barra de búsqueda
        filteredMovies = if (searchQuery.isEmpty()) {
            watchingMovies
        } else {
            watchingMovies.filter { it.title.contains(searchQuery, ignoreCase = true) }.toMutableList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> searchQuery = query },
            label = { Text("Buscar películas o series") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(50),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Icono de búsqueda",
                    tint = Color.Red
                )
            },
            singleLine = true, // Evita saltos de línea
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Red.copy(alpha = 0.5f)
            )
        )

        // Título en el centro
        Text(
            text = "Viendo Actualmente",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.Red),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Lista de películas
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredMovies) { movie ->
                MovieCardWithDeleteWatching(movie = movie, onDeleteClick = {
                    coroutineScope.launch {
                        watchingMovies.remove(movie)
                        filteredMovies = watchingMovies.filter {
                            it.title.contains(searchQuery, ignoreCase = true)
                        }.toMutableList()
                    }
                })
            }
        }
    }
}

@Composable
fun MovieCardWithDeleteWatching(movie: Movie, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la película
            AsyncImage(
                model = movie.getPosterUrl(), // URL de la carátula
                contentDescription = "Carátula de ${movie.title}",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                // Título de la película
                Text(movie.title, style = MaterialTheme.typography.headlineSmall)
            }

            // Botón de eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar película",
                    tint = Color.Red
                )
            }
        }
    }
}
