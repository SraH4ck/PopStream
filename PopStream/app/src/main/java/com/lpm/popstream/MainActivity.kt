package com.lpm.popstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.lpm.popstream.ViewModel.ThemeViewModel
import com.lpm.popstream.Navigation.PopStreamNavHost
import com.lpm.popstream.ui.theme.PopStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme = themeViewModel.isDarkTheme.value

            PopStreamTheme(darkTheme = isDarkTheme) {
                PopStreamNavHost(themeViewModel)
            }
        }
    }
}
