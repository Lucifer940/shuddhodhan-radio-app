package com.radioshuddhodhan.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.core.language.AppStrings

/** Application container provided at the composition root. */
val LocalAppContainer = compositionLocalOf<RadioApp?> { null }

/** Current bilingual strings — recompose-safe, updates when the language changes. */
val LocalAppStrings = compositionLocalOf { AppStrings(nepali = false) }

/**
 * Creates a screen ViewModel with the app container injected, avoiding any
 * DI framework.
 */
@Composable
inline fun <reified T : ViewModel> appViewModel(crossinline create: (RadioApp) -> T): T {
    val container = LocalAppContainer.current
    return viewModel(factory = viewModelFactory {
        initializer {
            val app = container ?: error("RadioApp container missing from composition")
            create(app)
        }
    })
}
