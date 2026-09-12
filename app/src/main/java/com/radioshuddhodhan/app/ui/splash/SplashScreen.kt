package com.radioshuddhodhan.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.radioshuddhodhan.app.R
import com.radioshuddhodhan.app.core.AppInfo
import com.radioshuddhodhan.app.ui.navigation.Routes
import com.radioshuddhodhan.app.ui.theme.BrandCrimson
import com.radioshuddhodhan.app.ui.theme.BrandNavy
import kotlinx.coroutines.delay

/**
 * Animated splash screen: logo scale-in + tagline fade, then hands over to
 * the onboarding guide (first run) or the home screen.
 */
@Composable
fun SplashScreen(
    isFirstRun: Boolean,
    onFinished: (targetRoute: String) -> Unit
) {
    val scale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val versionAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo: gentle scale + fade in
        scale.animateTo(
            1f,
            animationSpec = tween(650, easing = FastOutSlowInEasing)
        )
        logoAlpha.animateTo(1f, animationSpec = tween(400))
        textAlpha.animateTo(1f, animationSpec = tween(450))
        delay(250)
        versionAlpha.animateTo(1f, animationSpec = tween(300))
        delay(350)
        onFinished(if (isFirstRun) Routes.GUIDE else Routes.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BrandCrimson, BrandNavy))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "Radio Shuddhodhan",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .alpha(logoAlpha.value)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = AppInfo.APP_NAME,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = AppInfo.TAGLINE_NE,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
        Text(
            text = "v${AppInfo.VERSION}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(versionAlpha.value)
        )
    }
}
