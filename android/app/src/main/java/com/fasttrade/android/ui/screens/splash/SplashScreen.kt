package com.fasttrade.android.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fasttrade.android.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    isLoggedIn: Boolean
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
        )
        alpha.animateTo(1f, animationSpec = tween(400))
        delay(1200)
        if (isLoggedIn) onNavigateToHome() else onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FT",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    letterSpacing = 2.sp
                )
            }

            // App name
            androidx.compose.animation.AnimatedVisibility(
                visible = alpha.value > 0.5f,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FAST TRADE",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        letterSpacing = 4.sp,
                        color = Primary
                    )
                    Text(
                        text = "Sua plataforma de trocas",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
