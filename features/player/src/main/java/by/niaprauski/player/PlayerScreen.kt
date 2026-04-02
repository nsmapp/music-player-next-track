@file:kotlin.OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package by.niaprauski.player

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.player.models.PAction
import by.niaprauski.player.models.PlayerEvent
import by.niaprauski.player.models.PlayerState
import by.niaprauski.player.models.SyncTrackStatus
import by.niaprauski.player.views.PlayersScreenContent
import by.niaprauski.player.views.TrackItem
import by.niaprauski.player.views.dialogs.FirstLaunchDialog
import by.niaprauski.player.views.dialogs.NeedMediaPermissionDialog
import by.niaprauski.utils.media.ITrackShort
import by.niaprauski.utils.media.MediaHandler
import by.niaprauski.utils.messages.showToast
import by.niaprauski.utils.permission.MediaPermissions
import by.niaprauski.translations.R

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    radioTrack: Uri? = null,
    singleAudioTrack: Uri? = null,
    openAppSettings: (Context) -> Unit,
) {
    val viewModel: PlayerViewModel = hiltViewModel { factory: PlayerViewModel.Factory ->
        factory.create(radioTrack, singleAudioTrack)
    }

    val context = LocalContext.current
    val state: PlayerState by viewModel.state.collectAsStateWithLifecycle()
    val exoPlayerState by viewModel.exoPlayerState.collectAsStateWithLifecycle()
    val playerService by viewModel.playerService.collectAsStateWithLifecycle()
    val playList by playerService?.playList?.collectAsStateWithLifecycle(emptyList())
        ?: remember { mutableStateOf(emptyList<ITrackShort>()) }

    val hasMediaPermission by MediaPermissions.rememberMediaPermissions()

    LaunchedEffect(hasMediaPermission) {
        if (hasMediaPermission || radioTrack != null || singleAudioTrack != null) viewModel.onCreate()
    }

    val mediaPermissionLauncher = MediaPermissions.rememberMediaPermissionsLauncher(
        onGranted = { syncPlaylist(context) { status -> viewModel.onAction(PAction.SyncTracks(status)) } },
        onDisablePermissions = { viewModel.onAction(PAction.ShowPermissionInformationDialog) })

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                PlayerEvent.Play -> playerService?.play()
                PlayerEvent.PlayNext -> playerService?.seekToNext()
                PlayerEvent.PlayPrevious -> playerService?.seekToPrevious()
                PlayerEvent.Stop -> playerService?.stop()
                PlayerEvent.Pause -> playerService?.pause()
                is PlayerEvent.SeekTo -> {
                    playerService?.seekTo(event.position)
                }

                is PlayerEvent.SetPlayList -> playerService?.setTracks(event.tracks)
                is PlayerEvent.PlaySingleTrack -> playerService?.setTrack(event.track)
                PlayerEvent.ChangeRepeatMode -> playerService?.changeRepeatMode()
                PlayerEvent.ChangeShuffleMode -> playerService?.changeShuffleMode()
                PlayerEvent.SyncPlayList -> requestMediaPermissionWithSyncPlaylist(
                    hasMediaPermission = hasMediaPermission,
                    mediaPermissionLauncher = mediaPermissionLauncher,
                    context = context,
                    onSyncTrack = { syncStatus -> viewModel.onAction(PAction.SyncTracks(syncStatus)) })

                is PlayerEvent.UpFavorite -> playerService?.upTrackFavorite(event.trackId)
                is PlayerEvent.ChangeFavorite -> playerService?.changeTrackFavorite(event.trackId)
                is PlayerEvent.RemoveTrackFromPlayList -> playerService?.removeTrackFromPlayList(
                    event.trackId
                )

                is PlayerEvent.PlayTrackFromPlayList -> playerService?.playTrackFromPlayList(event.trackId)

                PlayerEvent.MediaItemSynced ->
                    context.showToast(context.getString(R.string.feature_player_sync_complete))
                PlayerEvent.MediaItemSyncError ->
                    context.showToast(context.getString(R.string.feature_player_synchronization_failed))
                PlayerEvent.Nothing -> {
                    /**do nothing **/
                }
            }
        }
    }

    if (state.isShowWelcomeDialog) FirstLaunchDialog(
        onSyncClick = { viewModel.onAction(PAction.RequestSync) },
        onDismissClick = { viewModel.onAction(PAction.HideWelcomeDialogs) },
    )

    if (state.isShowPermissionInformationDialog) NeedMediaPermissionDialog(
        onOpenSettingsClick = {
            openAppSettings(
                context
            )
        },
        onDismissClick = { viewModel.onAction(PAction.HideMediaPermissionInfoDialog) })

    PlayersScreenContent(
        exoPlayerState = exoPlayerState,
        isVisuallyEnabled = state.isVisuallyEnabled,
        trackProgress = playerService?.trackProgress,
        waveformFlow = playerService?.waveform,
        isSyncing = state.isSyncing,
        onAction = viewModel::onAction,
        hasMediaPermission = hasMediaPermission,
    )


    if (state.isShowPlayList) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(PAction.HidePlayList) },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            ),
            containerColor = AppTheme.appColors.background,
        ) {

            LazyColumn {
                items(
                    count = playList.size,
                    key = { index -> playList[index].id },
                ) {
                    TrackItem(
                        track = playList[it],
                        onAction = viewModel::onAction,
                        currentTrackId = { exoPlayerState.id }

                    )
                }
            }
        }
    }

}

private fun requestMediaPermissionWithSyncPlaylist(
    hasMediaPermission: Boolean,
    mediaPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    context: Context,
    onSyncTrack: (SyncTrackStatus) -> Unit,
) {
    if (!hasMediaPermission) mediaPermissionLauncher.launch(MediaPermissions.permission)
    else syncPlaylist(context) { tracks -> onSyncTrack(tracks) }
}

private fun syncPlaylist(
    context: Context,
    onSyncTrack: (SyncTrackStatus) -> Unit,
) {
    onSyncTrack(SyncTrackStatus.Started)
    val tracks = MediaHandler.getTrackData(context.contentResolver)
    onSyncTrack(SyncTrackStatus.Finished(tracks))
}
