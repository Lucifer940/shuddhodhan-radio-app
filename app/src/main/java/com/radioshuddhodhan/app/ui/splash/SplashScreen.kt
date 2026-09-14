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
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Subtle brand gradient accent at top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BrandCrimson.copy(alpha = 0.08f),
                            Color.Transparent,
                            BrandNavy.copy(alpha = 0.06f)
                        )
                    )
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // White card behind the official logo to make it pop
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .scale(scale.value)
                    .alpha(logoAlpha.value)
                    .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "Radio Shuddhodhan",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = AppInfo.APP_NAME,
                style = MaterialTheme.typography.headlineMedium,
                color = BrandCrimson,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = AppInfo.TAGLINE_NE,
                style = MaterialTheme.typography.titleMedium,
                color = BrandNavy,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Text(
                text = AppInfo.TAGLINE_SUB_NE,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF444444),
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = AppInfo.OPERATOR_NE,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF777777),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
        Text(
            text = "v${AppInfo.VERSION}",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF999999),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(versionAlpha.value)
        )
    }
}
