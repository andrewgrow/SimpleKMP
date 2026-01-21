package org.example.kmp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.example.kmp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogE2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun catalog_loadNextPage_showsItems_andFavoritesOpensEmpty() {
        composeRule.onNodeWithText("Catalog").assertIsDisplayed()

        composeRule.onNodeWithTag("catalog_load_next_page")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 1_000) {
            composeRule.onAllNodesWithTag("catalog_item_1")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithTag("catalog_item_1").assertIsDisplayed()

        composeRule.onNodeWithTag("catalog_open_favorites")
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeRule.onNodeWithText("No favorites yet.").assertIsDisplayed()
    }
}