package by.niaprauski.player.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.PlayerLiteButton
import by.niaprauski.player.models.PAction
import by.niaprauski.translations.R
import by.niaprauski.utils.permission.MediaPermissions

@Composable
fun PlayerUpView(
    onAction: (PAction) -> Unit,
    isSyncing: Boolean,
) {

    val hasMediaPermission by MediaPermissions.rememberMediaPermissions()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        PlayerLiteButton(
            modifier = Modifier
                .size(AppTheme.viewSize.normal)
                .clip(RoundedCornerShape(AppTheme.viewSize.normal)),
            imageVector = IIcon.playList,
            onClick = { onAction(PAction.ShowPlayList) },
            description = stringResource(R.string.feature_player_playlist)
        )

        PlayerLiteButton(
            modifier = Modifier
                .size(AppTheme.viewSize.normal)
                .clip(RoundedCornerShape(AppTheme.viewSize.normal)),
            imageVector = IIcon.playListUpdate,
            onClick = {
                if (hasMediaPermission) onAction(PAction.ReloadPlayList)
            },
            description = stringResource(R.string.feature_player_reload_playlist)
        )

        val rotationAngle = animateFloatAsState(
            targetValue = if (isSyncing) 0f else 360f,
            animationSpec = tween(durationMillis = if (isSyncing) 750 else 0),
            label = stringResource(R.string.feature_player_syncrotation)
        )

        PlayerLiteButton(
            modifier = Modifier
                .size(AppTheme.viewSize.normal)
                .clip(RoundedCornerShape(AppTheme.viewSize.normal))
                .graphicsLayer{ rotationZ = rotationAngle.value },
            imageVector = IIcon.sync,
            onClick = { onAction(PAction.RequestSync) },
            description = stringResource(R.string.feature_player_sync_playlist)
        )
    }
}