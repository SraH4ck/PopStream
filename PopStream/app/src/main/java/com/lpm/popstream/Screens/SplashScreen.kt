package com.lpm.popstream.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Create references for the elements
        val (popText, streamText, popcornImage) = createRefs()

        Text(
            text = "Pop",
            color = Color.Red,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(popText) {
                top.linkTo(parent.top, margin = 200.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
            }
        )

        Text(
            text = "Stream",
            color = Color.White,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(streamText) {
                top.linkTo(popText.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
            }
        )

        Image(
            painter = rememberAsyncImagePainter("https://imgur.com/KjlsV1l.png"),
            contentDescription = "Cubo de palomitas",
            contentScale = ContentScale.Fit,
            modifier = Modifier.constrainAs(popcornImage) {
                top.linkTo(streamText.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )
    }
}
