package com.example.prototipoitv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val session = androidx.lifecycle.ViewModelProvider(this)[ItvSession::class.java]
        setContent { ItvTheme { ItvApp(session) } }
    }
}
