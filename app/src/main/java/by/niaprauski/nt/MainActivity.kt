package by.niaprauski.nt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.navigation.Root
import by.niaprauski.nt.models.ExternalTrack
import by.niaprauski.utils.media.MediaHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.isLoading
        }

        val externalTrack: ExternalTrack = getOutsideStartTrackUri()

        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()

            AppTheme(
                accentColor = state.accentColor,
                backgroundColor = state.backgroundColor,
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppTheme.appColors.transparent)
                    )
                } else {
                    Root(
                        radioTrack = externalTrack.radioTrack?.toString(),
                        singleAudioTrack = externalTrack.singleAudioTrack?.toString(),
                    )
                }
            }
        }
    }

    private fun getOutsideStartTrackUri(): ExternalTrack {
        val action = intent?.action
        val data = intent?.data

        if (action == null || data == null) return ExternalTrack()
        val type = contentResolver.getType(data) ?: return ExternalTrack()

        val result = when {
            type in MediaHandler.radioMimeTypes -> ExternalTrack(radioTrack = data)
            type in MediaHandler.audioMimeTypes || type.startsWith("audio/")
                -> ExternalTrack(singleAudioTrack = data)
            else -> ExternalTrack()
        }
        return result
    }


}
