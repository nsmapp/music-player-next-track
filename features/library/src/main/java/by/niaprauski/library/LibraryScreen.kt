package by.niaprauski.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.library.models.LibraryEvent
import by.niaprauski.library.models.TrackModel
import by.niaprauski.library.view.ContentView
import by.niaprauski.playerservice.models.ExoPlayerState


@UnstableApi
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingTracks: LazyPagingItems<TrackModel> =
        viewModel.pagingTracks.collectAsLazyPagingItems()

    val exoPlayerState: ExoPlayerState by viewModel.exoPlayerState.collectAsStateWithLifecycle()
    val playerService by viewModel.playerService.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.onCreate()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is LibraryEvent.PlayMediaItem -> playerService?.playWithPosition(event.mediaItem)
                is LibraryEvent.IgnoreMediaItem -> playerService?.removeMediaItem(event.mediaItem)
                is LibraryEvent.AddMediaItem -> playerService?.addItemToPlayList(event.mediaItem)
                is LibraryEvent.Play -> playerService?.play()
                is LibraryEvent.Pause -> playerService?.pause()
            }
        }
    }

    val isLibraryPrepared = remember(pagingTracks.loadState.refresh, pagingTracks.itemCount) {
        pagingTracks.loadState.refresh is LoadState.Loading && pagingTracks.itemCount == 0
    }

    var isSearchVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < -50f) {
                    keyboardController?.hide()
                    isSearchVisible = false
                }
                if (delta > 50f || pagingTracks.itemCount <= 20) isSearchVisible = true
                return Offset.Zero
            }
        }
    }


    Box(
        modifier = Modifier
            .background(color = AppTheme.appColors.background)
            .nestedScroll(nestedScrollConnection)
            .statusBarsPadding()
            .padding(AppTheme.padding.mini)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {


        AnimatedContent(
            targetState = isLibraryPrepared,
            transitionSpec = { loadingTransform() }
        ) { isLoading ->
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.viewSize.view_extra_larger),
                    color = AppTheme.appColors.background_hard,
                    trackColor = AppTheme.appColors.accent,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                )
            } else {
                ContentView(
                    pagingTracks = pagingTracks,
                    isControlViewVisible = isSearchVisible,
                    state = state,
                    currentTrackId = { exoPlayerState.id },
                    currentTrackName = { exoPlayerState.fileName },
                    isPlaying = {exoPlayerState.isPlaying},
                    onAction = viewModel::onAction

                )
            }
        }
    }
}

private fun loadingTransform(): ContentTransform {
    val enter = fadeIn(animationSpec = tween(300, delayMillis = 90))
    val exit = fadeOut(animationSpec = tween(90))

    return enter togetherWith exit
}