package com.example.socialapp.ui.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialapp.R

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Menggunakan warna background dinamis (hitam di dark mode, putih di light mode)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        // 1. Logo Utama Tepat di Tengah Layar
        Icon(
            // Ganti R.drawable.ic_launcher_foreground dengan logo Threads Anda jika sudah ada
            painter = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier.size(80.dp),
            // Berikan warna kontras otomatis (putih di latar hitam, hitam di latar putih)
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = "Threads Logo"
        )

        // 2. Branding Perusahaan di Bagian Bawah Layar (Khas ekosistem Meta/Threads)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "from",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Text(
                text = "YOUR BRAND", // Anda bisa mengubah ini menjadi nama tim atau dikosongkan jika tidak butuh
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
        }
    }
}