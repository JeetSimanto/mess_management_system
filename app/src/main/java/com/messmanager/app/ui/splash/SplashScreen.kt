package com.messmanager.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messmanager.app.R
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkPrimaryMuted
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.MilkerFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    isInitializing: Boolean,
    onSplashFinished: () -> Unit
) {
    // Animation States
    val logoScale = remember { Animatable(0.2f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-15f) }

    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(40f) }

    val progressAnim = remember { Animatable(0f) }

    // Infinite ambient animations
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAmbient")

    val pulseGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlowScale"
    )

    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlowAlpha"
    )

    val particleOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleFloat"
    )

    val currentIsInitializing by rememberUpdatedState(isInitializing)
    val currentOnSplashFinished by rememberUpdatedState(onSplashFinished)

    LaunchedEffect(Unit) {
        // Phase 1: Logo Spring Entrance
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            logoRotation.animateTo(0f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = 220f
                )
            )
        }

        delay(350)

        // Phase 2: Title Entrance
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(450))
        }
        launch {
            titleOffsetY.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }

        delay(200)

        // Phase 3: Progress Bar Fill
        progressAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(1800, easing = FastOutSlowInEasing)
        )

        // Wait for initialization to complete if necessary (max 3s timeout)
        val startTime = System.currentTimeMillis()
        while (currentIsInitializing && (System.currentTimeMillis() - startTime) < 3000L) {
            delay(100)
        }

        delay(150)
        currentOnSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Ambient Radial Background Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2.3f)
            val glowRadius = (size.width * 0.75f) * pulseGlowScale

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DarkPrimary.copy(alpha = 0.30f * pulseGlowAlpha),
                        DarkPrimaryGlow.copy(alpha = 0.15f * pulseGlowAlpha),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = glowRadius
                ),
                center = centerOffset,
                radius = glowRadius
            )
        }

        // Layer 2: Floating Dust Particles Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particleCount = 12
            val width = size.width
            val height = size.height

            for (i in 0 until particleCount) {
                val seed = i * 137.5f
                val startX = (seed % width)
                val startY = (seed * 2.3f % height)
                val sizePx = (3.dp.toPx() + (i % 3).dp.toPx())

                val currentY = (startY + particleOffsetY * (1f + (i % 3) * 0.5f)) % height
                val alpha = (0.2f + (i % 5) * 0.15f)

                drawCircle(
                    color = DarkPrimary.copy(alpha = alpha),
                    radius = sizePx / 2f,
                    center = Offset(startX, if (currentY < 0) height + currentY else currentY)
                )
            }
        }

        // Layer 3: Central Hero Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Logo Container with Ambient Glow Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                        rotationZ = logoRotation.value
                    }
            ) {
                // Outer Pulse Ring
                Box(
                    modifier = Modifier
                        .size(width = 240.dp, height = 140.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    DarkPrimary.copy(alpha = 0.35f),
                                    DarkPrimaryGlow.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Logo Card Frame
                Box(
                    modifier = Modifier
                        .size(width = 210.dp, height = 110.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(DarkSurface)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    DarkPrimary,
                                    DarkPrimaryGlow,
                                    DarkOutline
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground_img),
                        contentDescription = "Mess Manager Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha.value
                        translationY = titleOffsetY.value
                    }
            ) {
                Text(
                    text = "MESS MANAGER",
                    fontFamily = MilkerFontFamily,
                    fontSize = 32.sp,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Premium Animated Progress Bar
            val currentProgress = progressAnim.value

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .alpha(titleAlpha.value)
            ) {
                // Outer Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(DarkSurfaceHigh)
                ) {
                    // Inner Neon Fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DarkPrimaryMuted,
                                        DarkPrimary
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val statusText = when {
                    currentProgress < 0.35f -> "Initializing Mess Workspace..."
                    currentProgress < 0.75f -> "Syncing Meal & Grocery Records..."
                    else -> "Ready!"
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
