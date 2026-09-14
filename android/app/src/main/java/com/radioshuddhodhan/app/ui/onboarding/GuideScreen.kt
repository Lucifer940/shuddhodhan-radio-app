package com.radioshuddhodhan.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.theme.BrandCrimson
import com.radioshuddhodhan.app.ui.theme.BrandGold
import com.radioshuddhodhan.app.ui.theme.BrandNavy
import kotlinx.coroutines.launch

private data class GuidePage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val tint: Color
)

/**
 * "How to Use Radio Shuddhodhan" — a 7-step animated guide with
 * Next / Previous / Skip / Get Started controls.
 */
@Composable
fun GuideScreen(
    onDone: () -> Unit,
    onSkipToLogin: () -> Unit
) {
    val L = LocalAppStrings.current
    val pages = listOf(
        GuidePage(Icons.Filled.Login, L.guideStep1Title, L.guideStep1Desc, BrandCrimson),
        GuidePage(Icons.Filled.Home, L.guideStep2Title, L.guideStep2Desc, BrandNavy),
        GuidePage(Icons.Filled.Headphones, L.guideStep3Title, L.guideStep3Desc, BrandCrimson),
        GuidePage(Icons.Filled.MenuBook, L.guideStep4Title, L.guideStep4Desc, BrandNavy),
        GuidePage(Icons.Filled.CalendarMonth, L.guideStep5Title, L.guideStep5Desc, BrandCrimson),
        GuidePage(Icons.Filled.Radio, L.guideStep6Title, L.guideStep6Desc, BrandNavy),
        GuidePage(Icons.Filled.HelpCenter, L.guideStep7Title, L.guideStep7Desc, BrandCrimson)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                )
            )
    ) {
        // Top bar with skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = L.howToUse,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onSkipToLogin) {
                Text(L.skip)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            GuidePageContent(pages[page])
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                )
            }
        }

        // Bottom controls: Previous / Next or Get Started
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Text(L.previous)
            }
            if (pagerState.currentPage < pages.size - 1) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                ) {
                    Text(L.next)
                }
            } else {
                Button(onClick = onDone) {
                    Text(L.getStarted)
                }
            }
        }
    }
}

@Composable
private fun GuidePageContent(page: GuidePage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = page.tint.copy(alpha = 0.12f),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.tint,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Icon(
            imageVector = Icons.Filled.Radio,
            contentDescription = null,
            tint = BrandGold.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
    }
}
