package by.niaprauski.player.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.PlayerLiteButton
import by.niaprauski.player.models.PAction
import by.niaprauski.playerservice.models.RepeatMode
import by.niaprauski.translations.R
import kotlinx.coroutines.delay

@Composable
fun PlayerControlView(
    modifier: Modifier = Modifier,
    onAction: (PAction) -> Unit,
    isPlaying: Boolean,
    shuffle: Boolean,
    repeatMode: Int,
    progressIndicator: @Composable () -> Unit,
) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            ShuffleButton(shuffle, onAction)

            PlayPauseButton(isPlaying, onAction)

            RepeatModeButton(repeatMode, onAction)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            PlayerLiteButton(
                modifier = Modifier
                    .size(AppTheme.viewSize.big)
                    .clip(RoundedCornerShape(AppTheme.viewSize.big)),
                imageVector = IIcon.skipPrevious,
                onClick = { onAction(PAction.PlayPrevious) },
                description = stringResource(R.string.feature_player_play_previous)
            )


            PlayerLiteButton(
                modifier = Modifier
                    .size(AppTheme.viewSize.large)
                    .clip(RoundedCornerShape(AppTheme.viewSize.large)),
                imageVector = IIcon.stop,
                onClick = { onAction(PAction.Stop) },
                description = stringResource(R.string.feature_player_stop)
            )

            PlayerLiteButton(
                modifier = Modifier
                    .size(AppTheme.viewSize.big)
                    .clip(RoundedCornerShape(AppTheme.viewSize.big)),
                imageVector = IIcon.skipNext,
                onClick = { onAction(PAction.PlayNext) },
                description = stringResource(R.string.feature_player_play_next)
            )
        }


        progressIndicator()
    }
}

@Composable
private fun RepeatModeButton(
    repeatMode: Int,
    onAction: (PAction) -> Unit,
) {
    val repeatModeIcon = when (repeatMode) {
        RepeatMode.REPEAT_MODE_OFF.value -> IIcon.repeat
        RepeatMode.REPEAT_MODE_ONE.value -> IIcon.repeatOne
        RepeatMode.REPEAT_MODE_ALL.value -> IIcon.repeatOn
        else -> IIcon.repeat
    }

    PlayerLiteButton(
        modifier = Modifier
            .size(AppTheme.viewSize.small)
            .clip(RoundedCornerShape(AppTheme.viewSize.small)),
        imageVector = repeatModeIcon,
        onClick = { onAction(PAction.ChangeRepeatMode) },
        description = stringResource(R.string.feature_player_repeat_mode)
    )
}


@Composable
fun ShuffleButton(
    shuffle: Boolean,
    onAction: (PAction) -> Unit,
) {
    val shuffleIcon = if (shuffle) IIcon.shuffleOn
    else IIcon.shuffle

    PlayerLiteButton(
        modifier = Modifier
            .size(AppTheme.viewSize.small)
            .clip(RoundedCornerShape(AppTheme.viewSize.small)),
        imageVector = shuffleIcon,
        onClick = { onAction(PAction.ChangeShuffleMode) },
        description = stringResource(R.string.feature_player_shuffle)
    )
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onAction: (PAction) -> Unit,
) {

    var isPlayingWithDebounce by remember { mutableStateOf(isPlaying) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) isPlayingWithDebounce = true
        else {
            delay(100)
            isPlayingWithDebounce = isPlaying
        }
    }

    val playIcon = if (isPlayingWithDebounce) IIcon.pause else IIcon.play


    PlayerLiteButton(
        modifier = Modifier
            .size(AppTheme.viewSize.extra_large)
            .clip(RoundedCornerShape(AppTheme.viewSize.extra_large)),
        imageVector = playIcon,
        onClick = {
            if (isPlayingWithDebounce) onAction(PAction.Pause) else onAction(PAction.Play)
        },
        description = stringResource(R.string.feature_player_play)
    )
}