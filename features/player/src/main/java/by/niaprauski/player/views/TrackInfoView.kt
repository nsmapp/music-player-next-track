package by.niaprauski.player.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.ui.text.TextBoldLarge
import by.niaprauski.designsystem.ui.text.TextMediumLarge
import by.niaprauski.player.models.PAction

@Composable
fun TrackInfoView(
    trackId: String,
    artist: String,
    title: String,
    favorite: () -> Int,
    onAction: (PAction) -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        LikeCountView(
            onAction = onAction,
            trackId = trackId,
            favorite = favorite,
        )

        Spacer(modifier = Modifier.width(AppTheme.padding.default))

        AnimatedContent(
            targetState = artist + title,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn(initialAlpha = 0.1f))
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut(targetAlpha = 0.5f))
            },
            label = "textAnimation"
        ) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                TextBoldLarge(
                    modifier = Modifier.basicMarquee(),
                    text = artist,
                    maxLines = 1,
                )

                TextMediumLarge(
                    modifier = Modifier
                        .basicMarquee()
                        .padding(top = AppTheme.padding.default),
                    text = title,
                    maxLines = 1,
                )
            }
        }
    }
}