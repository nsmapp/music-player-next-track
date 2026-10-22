package by.niaprauski.about

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.icons.NormalIcon
import by.niaprauski.designsystem.ui.text.TextBold
import by.niaprauski.designsystem.ui.text.TextBoldLarge
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.translations.R

@Composable
fun AboutScreen(
    onShowGitHubPage: (Context) -> Unit,
    onSendEmail: (Context) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(color = AppTheme.appColors.background)
            .statusBarsPadding()
            .padding(AppTheme.padding.default)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextBoldLarge(
            text = stringResource(R.string.app_name)
        )

        TextBold(
            text = stringResource(R.string.app_version)
        )

        Spacer(modifier = Modifier.height(AppTheme.viewSize.normal))

        TextMedium(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Justify,
            text = stringResource(R.string.feature_about_application_description)
        )

        Spacer(modifier = Modifier.height(AppTheme.viewSize.normal))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

            NormalIcon(
                modifier = Modifier
                    .padding(AppTheme.padding.default)
                    .clip(defaultRoundedShape)
                    .clickable {
                        onShowGitHubPage(context)
                    },
                imageVector = IIcon.github,
                contentDescription = stringResource(R.string.feature_about_github_page)
            )
            NormalIcon(
                modifier = Modifier
                    .padding(AppTheme.padding.default)
                    .clip(defaultRoundedShape)
                    .clickable {
                        onSendEmail(context)
                    },
                imageVector = IIcon.mail,
                contentDescription = stringResource(R.string.feature_about_email)
            )
        }
    }
}