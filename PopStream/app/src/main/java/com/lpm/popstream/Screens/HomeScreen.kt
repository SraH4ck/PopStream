package com.lpm.popstream.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lpm.popstream.Data.ApiClient
import com.lpm.popstream.Model.Movie
import com.lpm.popstream.R
import com.lpm.popstream.ViewModel.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    favoriteMovies: MutableList<Movie>,
    watchingMovies: MutableList<Movie>,
    watchedMovies: MutableList<Movie>,
    pendingMovies: MutableList<Movie>,
    customLists: SnapshotStateMap<String, MutableList<Movie>>, // Map para listas personalizadas
    themeViewModel: ThemeViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    var showAddToListDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var errorMessageMap by remember { mutableStateOf(mapOf<String, String>()) }

    // Para gestionar el tema (modo claro y oscuro)
    var isDarkTheme by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            movies = ApiClient.retrofit.getPopularMovies().results
        }
    }

    // Función para cambiar el tema
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    // Configurar MaterialTheme basado en el estado del tema
    val colors = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    // Mostrar el contenido de la app con el tema seleccionado
    MaterialTheme(colorScheme = colors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra de búsqueda con ajustes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        coroutineScope.launch {
                            movies = if (query.isEmpty()) {
                                ApiClient.retrofit.getPopularMovies().results
                            } else {
                                ApiClient.retrofit.searchMovies(query = query).results
                            }
                        }
                    },
                    label = { Text("Buscar películas o series") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.Red
                        )
                    },
                    singleLine = true, // Restringe el texto a una sola línea
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = Color.Red.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Red.copy(alpha = 0.5f)
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color.Red
                    )
                }
            }

            // Barra de navegación con iconos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.navigate("favorites") }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                }
                IconButton(onClick = { navController.navigate("watching") }) {
                    Icon(Icons.Default.Visibility, contentDescription = "Viendo Actualmente")
                }
                IconButton(onClick = { navController.navigate("watched") }) {
                    Icon(Icons.Default.Done, contentDescription = "Vistas")
                }
                IconButton(onClick = { navController.navigate("pending") }) {
                    Icon(Icons.Default.Schedule, contentDescription = "Pendientes")
                }

                Spacer(modifier = Modifier.width(30.dp))

                IconButton(onClick = { navController.navigate("myLists") }) {
                    Icon(Icons.Default.List, contentDescription = "Mis Listas")
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_list),
                        contentDescription = "Crear Lista",
                    )
                }

            }

            // Mostrar diálogo para crear listas
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (newListName.isBlank()) {
                                        errorMessageMap = errorMessageMap + ("" to "El nombre de la lista no puede estar vacío.")
                                    } else if (customLists.containsKey(newListName)) {
                                        errorMessageMap = errorMessageMap + ("" to "Ya existe una lista con este nombre.")
                                    } else {
                                        customLists[newListName] = mutableListOf()
                                        newListName = ""
                                        showDialog = false
                                    }
                                    // Temporizador para borrar el mensaje de error
                                    if (errorMessageMap.isNotEmpty()) {
                                        delay(1000)
                                        errorMessageMap = emptyMap()
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
                            // Mostrar mensaje de error si existe
                            errorMessageMap[""]?.let {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                )
            }

            // Mostrar diálogo de ajustes
            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showSettingsDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Cerrar", color = Color.White)
                        }
                    },
                    title = { Text("Ajustes de la aplicación") },
                    text = {
                        Column {
                            Text("Modo Oscuro/Claro")
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { themeViewModel.toggleTheme() } // Cambiar el estado del tema
                            )
                        }
                    }
                )
            }

            // Lista de películas
            LazyColumn {
                items(movies) { movie ->
                    MovieCardWithAddButton(
                        movie = movie,
                        onAddToListClick = {
                            selectedMovie = movie
                            showAddToListDialog = true
                        },
                        onActionClick = { action, selectedMovie ->
                            when (action) {
                                "favorite" -> if (!favoriteMovies.contains(selectedMovie)) favoriteMovies.add(selectedMovie)
                                "watching" -> if (!watchingMovies.contains(selectedMovie)) watchingMovies.add(selectedMovie)
                                "watched" -> if (!watchedMovies.contains(selectedMovie)) watchedMovies.add(selectedMovie)
                                "pending" -> if (!pendingMovies.contains(selectedMovie)) pendingMovies.add(selectedMovie)
                            }
                        }
                    )
                }
            }

            // Diálogo para añadir película a una lista personalizada
            if (showAddToListDialog && selectedMovie != null) {
                AlertDialog(
                    onDismissRequest = { showAddToListDialog = false },
                    confirmButton = { },
                    dismissButton = {
                        Button(onClick = { showAddToListDialog = false }) {
                            Text("Cancelar", color = Color.White)
                        }
                    },
                    title = { Text("Añadir a lista") },
                    text = {
                        LazyColumn {
                            items(customLists.keys.toList()) { listName ->
                                TextButton(
                                    onClick = {
                                        val list = customLists[listName]
                                        if (list?.contains(selectedMovie) == true) {
                                            errorMessageMap = errorMessageMap + (listName to "La película ya pertenece a la lista '$listName'.")
                                            coroutineScope.launch {
                                                delay(2000) // Duración del mensaje (1 segundo)
                                                errorMessageMap = errorMessageMap - listName // Limpia el mensaje específico
                                            }
                                        } else {
                                            list?.add(selectedMovie!!)
                                            errorMessageMap = errorMessageMap - listName
                                            showAddToListDialog = false
                                        }
                                    }
                                ) {
                                    Column {
                                        Text(text = listName, color = Color.Red)
                                        errorMessageMap[listName]?.let {
                                            Text(
                                                text = it,
                                                color = Color.Red,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MovieCardWithAddButton(
    movie: Movie,
    onAddToListClick: () -> Unit,
    onActionClick: (String, Movie) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen de la película
            AsyncImage(
                model = movie.getPosterUrl(), // URL de la carátula
                contentDescription = "Carátula de ${movie.title}",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp), // Espacio entre la imagen y el texto
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                // Título de la película
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2, // Limitar líneas del título
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Fila con los iconos
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = { onActionClick("favorite", movie) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorito")
                    }
                    IconButton(
                        onClick = { onActionClick("watching", movie) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = "Viendo Actualmente")
                    }
                    IconButton(
                        onClick = { onActionClick("watched", movie) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Visto")
                    }
                    IconButton(
                        onClick = { onActionClick("pending", movie) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Pendiente")
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = onAddToListClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Añadir a lista",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
