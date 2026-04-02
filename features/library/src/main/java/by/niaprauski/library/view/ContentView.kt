package by.niaprauski.library.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.texxtfield.CTextField
import by.niaprauski.library.models.LAction
import by.niaprauski.library.models.LibraryState
import by.niaprauski.library.models.TrackModel
import by.niaprauski.translations.R
import by.niaprauski.utils.extension.UNKNOWN_TRACK_ID

@Composable
fun ContentView(
    pagingTracks: LazyPagingItems<TrackModel>,
    isControlViewVisible: Boolean,
    state: LibraryState,
    currentTrackId: () -> String,
    currentTrackName: () -> String,
    isPlaying: () -> Boolean,
    onAction: (LAction) -> Unit,
) {

    val hasValidTrack by remember(currentTrackId()) {
        derivedStateOf { currentTrackId() != UNKNOWN_TRACK_ID }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TrackListView(
            pagingTracks = pagingTracks,
            onAction = onAction,
            currentTrackId = currentTrackId
        )


        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = isControlViewVisible,
            transitionSpec = { controlViewTransform() },
            contentAlignment = Alignment.BottomCenter,
            content = { isVisible ->

                if (isVisible) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                AppTheme.viewSize.border_normal,
                                AppTheme.appColors.accent,
                                RoundedCornerShape(AppTheme.radius.default)
                            )
                    ) {
                        if (hasValidTrack) {
                            SimplePlayer(
                                currentTrackName = currentTrackName,
                                isPlaying = isPlaying,
                                onAction = onAction,
                            )
                        }

                        CTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.searchText,
                            onValueChange = { onAction(LAction.SearchTrack(it)) },
                            hint = stringResource(R.string.feature_library_search),
                            leadingIcon = IIcon.search,
                        )
                    }
                } else Spacer(modifier = Modifier.height(0.dp))

            })
    }
}

private fun controlViewTransform(): ContentTransform {
    val enter = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    ) + fadeIn()

    val exit = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    ) + fadeOut()

    return enter togetherWith exit
}