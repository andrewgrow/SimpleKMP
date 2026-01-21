package org.example.kmp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.example.kmp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStarts_catalogIsVisible_andFavoritesEmptyOpens() {
        // Catalog visible
        composeRule.onNodeWithText("Catalog").assertIsDisplayed()

        // Open Favorites (icon top-right)
        composeRule.onNodeWithTag("catalog_open_favorites")
            .assertIsDisplayed()
            .performClick()

        // Favorites screen visible
        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeRule.onNodeWithTag("favorites_empty")
            .assertIsDisplayed()
    }

    @Test
    fun catalog_hasLoadNextPageButton_clickable() {
        composeRule.onNodeWithTag("catalog_load_next_page")
            .assertIsDisplayed()
            .performClick()
    }
}