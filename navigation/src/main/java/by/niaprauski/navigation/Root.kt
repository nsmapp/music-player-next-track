package by.niaprauski.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.colors.navigationBarItemColors
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.icons.SmallIcon
import by.niaprauski.navigation.screens.settings.settings
import by.niaprauski.navigation.screens.library.LibraryDest
import by.niaprauski.navigation.screens.library.library
import by.niaprauski.navigation.screens.player.PlayerDest
import by.niaprauski.navigation.screens.player.player
import by.niaprauski.navigation.screens.about.about
import by.niaprauski.navigation.screens.settings.SettingsDest


@Composable
fun Root(
    radioTrack: String? = null,
    singleAudioTrack: String? = null,
) {

    val backStack = rememberNavBackStack(
        PlayerDest.root(
            radioTrack.takeIf { it != "null" },
            singleAudioTrack.takeIf { it != "null" })
    )
    val navigator = remember { Navigator(backStack) }
    val isKeyboardVisible = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp

    val entries = remember(navigator){ getEntries(navigator)}

    Scaffold(
        containerColor = AppTheme.appColors.background,
        bottomBar = {
            NavigationBar(
                containerColor = AppTheme.appColors.background_hard,
                contentColor = AppTheme.appColors.background,
            ) {
                NavigationBarItem(
                    selected = navigator.currentScreen is LibraryDest,
                    onClick = { navigator.navigateTop(LibraryDest.root()) },
                    icon = { SmallIcon(imageVector = IIcon.library) },
                    colors = navigationBarItemColors,
                )

                NavigationBarItem(
                    selected = navigator.currentScreen is PlayerDest,
                    onClick = { navigator.navigateSingleTop(PlayerDest.root())},
                    icon = { SmallIcon(imageVector = IIcon.play) },
                    colors = navigationBarItemColors,
                )

                NavigationBarItem(
                    selected = navigator.currentScreen is SettingsDest,
                    onClick = { navigator.navigateTop(SettingsDest.root())},
                    icon = { SmallIcon(imageVector = IIcon.settings) },
                    colors = navigationBarItemColors,
                )
            }
        }
    ) { padding ->
        NavDisplay(
            modifier = Modifier
                .padding(bottom = if (isKeyboardVisible) 0.dp else padding.calculateBottomPadding())
                .imePadding(),
            transitionSpec = { transformSlideOpen() },
            popTransitionSpec = { transformPop() },
            predictivePopTransitionSpec = { transformPredicativePop() },
            backStack = backStack,
            onBack = navigator::navigateBack,
            entryProvider = entries
        )
    }
}

fun transformPredicativePop(): ContentTransform = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(150)
) togetherWith slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(150)
)

fun transformPop(): ContentTransform = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(350)
) togetherWith slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(350)
)

fun transformSlideOpen(): ContentTransform = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(350)
) togetherWith slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(350)
)

fun getEntries(navigator: Navigator) = entryProvider<NavKey> {
    library(navigator)
    player(navigator)
    settings(navigator)
    about(navigator)

}
