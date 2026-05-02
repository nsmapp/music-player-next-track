package by.niaprauski.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.niaprauski.domain.models.AppSettings
import by.niaprauski.domain.usecases.settings.GetSettingsUseCase
import by.niaprauski.domain.usecases.settings.SetAccentColorUseCase
import by.niaprauski.domain.usecases.settings.SetAutoPlayAfterLaunchUseCase
import by.niaprauski.domain.usecases.settings.SetBackgroundColorUseCase
import by.niaprauski.domain.usecases.settings.SetLikeTrackPriorityUseCase
import by.niaprauski.domain.usecases.settings.SetLikedTrackPercentInPlayListUseCase
import by.niaprauski.domain.usecases.settings.SetMaxTrackDurationUseCase
import by.niaprauski.domain.usecases.settings.SetMinTrackDurationUseCase
import by.niaprauski.domain.usecases.settings.SetPlayListLimitSizeUseCase
import by.niaprauski.domain.usecases.settings.SetVisualizerStatusUseCase
import by.niaprauski.settings.models.SAction
import by.niaprauski.settings.models.SettingsState
import by.niaprauski.utils.extension.convertToInt
import by.niaprauski.utils.extension.onComplete
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setVisualizerStatusUseCase: SetVisualizerStatusUseCase,
    private val setMinDurationUseCase: SetMinTrackDurationUseCase,
    private val setMaxDurationUseCase: SetMaxTrackDurationUseCase,
    private val setPlayListLimitSizeUseCase: SetPlayListLimitSizeUseCase,
    private val setAccentColorUseCase: SetAccentColorUseCase,
    private val setBackgroundColorUseCase: SetBackgroundColorUseCase,
    private val setLikeTrackPriorityUseCase: SetLikeTrackPriorityUseCase,
    private val setLikedTrackPercentUseCase: SetLikedTrackPercentInPlayListUseCase,
    private val setAutoPlayAfterLaunchUseCase: SetAutoPlayAfterLaunchUseCase,
) : ViewModel() {

    companion object {
        private const val INPUT_DEBOUNCE_TEXT = 500L
        private const val INPUT_DEBOUNCE_COLOR = 100L
        private const val SEK_MLS = 1000
        private const val MIN_MLS = 60000
    }

    private val _state = MutableStateFlow(SettingsState.INITIAL)
    val state = _state.asStateFlow()

    private val _minDuration = MutableSharedFlow<Int>()
    private val _maxDuration = MutableSharedFlow<Int>()
    private val _maxTrackCount = MutableSharedFlow<Int>()
    private val _likedTrackPercent = MutableSharedFlow<Int>()


    private val _accentPosition = MutableSharedFlow<Pair<String, Float>>()
    private val _backgroundPosition = MutableSharedFlow<Pair<String, Float>>()


    fun onLaunch() {
        getSettings()
    }

    fun onAction(action: SAction) {
        when (action) {
            is SAction.SetVisuallyEnabled -> setVisuallyEnabled(action.enabled)
            is SAction.SetMinDuration -> setMinDuration(action.duration)
            is SAction.SetMaxDuration -> setMaxDuration(action.duration)
            is SAction.SetAccentColor -> setAccentColorSettings(action.hexColor, action.position)
            is SAction.SetBackgroundColor -> setBackgroundColorSettings(action.hexColor, action.position)
            is SAction.SetPlayListLimitSize -> setPlayListLimitSize(action.count)
            is SAction.SetLikeTrackPriority -> setLikeTrackPriority(action.isLikeTrackPriority)
            is SAction.SetLikedTrackPercent -> setLikedTrackPercent(action.percent)
            is SAction.SetAutoPlay -> setAutoPlayAfterLaunch(action.enabled)
        }
    }

    private fun getSettings() {
        viewModelScope.launch {
            getSettingsUseCase.invoke()
                .onSuccess { settings ->
                    handleSettingsWithCollectChanges(settings)
                }
                .onFailure {
                }
        }
    }

    private fun handleSettingsWithCollectChanges(settings: AppSettings) {
        with(settings) {
            _state.update {
                it.copy(
                    isVisuallyEnabled = isVisuallyEnabled,
                    minDuration = (minDuration / SEK_MLS).toString(),
                    isMinDurationError = false,
                    maxDuration = (maxDuration / MIN_MLS).toString(),
                    isMaxDurationError = false,
                    acentPositon = accentPosition,
                    backgroundPosition = backgroundPosition,
                    isPlayListLimitError = false,
                    playListLimitSize = playListLimitSize.toString(),
                    isLikeTrackPriority = isLikeTrackPriority,
                    isAutoPlay = isAutoPlayOnLaunch
                )
            }
        }

        startCollectMinDuration()
        startCollectMaxDuration()
        startCollectColorChanging()
        startCollectPlayListLimitSize()
        startCollectLikedTrackPercent()
    }


    private fun startCollectMinDuration() {
        viewModelScope.launch {
            _minDuration
                .debounce(INPUT_DEBOUNCE_TEXT)
                .collect { duration ->
                    setMinDurationUseCase.invoke(duration)
                        .onFailure {
                            _state.update { it.copy(isMinDurationError = true) }
                        }
                }
        }
    }

    private fun startCollectMaxDuration() {
        viewModelScope.launch {
            _maxDuration
                .debounce(INPUT_DEBOUNCE_TEXT)
                .collect { duration ->
                    setMaxDurationUseCase.invoke(duration)
                        .onFailure {
                            _state.update { it.copy(isMaxDurationError = true) }
                        }
                }
        }
    }

    private fun startCollectPlayListLimitSize() {
        viewModelScope.launch {
            _maxTrackCount
                .debounce(INPUT_DEBOUNCE_TEXT)
                .collect { count ->
                    setPlayListLimitSizeUseCase.invoke(count)
                        .onFailure {
                            _state.update { it.copy(isPlayListLimitError = true) }
                        }
                }
        }
    }

    private fun startCollectLikedTrackPercent(){
        viewModelScope.launch {
            _likedTrackPercent
                .debounce(INPUT_DEBOUNCE_TEXT)
                .collect { percent ->
                    setLikedTrackPercentUseCase.invoke(percent)
                }
        }
    }

    private fun setVisuallyEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setVisualizerStatusUseCase.set(enabled)
                .onSuccess { _state.update { it.copy(isVisuallyEnabled = enabled) } }
        }

    }

    private fun setMinDuration(duration: String) {
        val duration = duration.filter { it.isDigit() }

        if (duration.length > 2) return
        _state.update { it.copy(minDuration = duration, isMinDurationError = false) }
        if (duration.isEmpty()) return

        val durationLong = duration.convertToInt()
        viewModelScope.launch {
            _minDuration.emit(durationLong)
        }
    }

    private fun setMaxDuration(duration: String) {
        val duration = duration.filter { it.isDigit() }

        if (duration.length > 2) return
        _state.update { it.copy(maxDuration = duration, isMaxDurationError = false) }
        if (duration.isEmpty()) return

        val durationInt = duration.convertToInt()
        viewModelScope.launch {
            _maxDuration.emit(durationInt)
        }
    }

    private fun setAccentColorSettings(hexColor: String, position: Float) {
        _state.update { it.copy(acentPositon = position) }
        viewModelScope.launch {
            _accentPosition.emit(hexColor to position)
        }

    }

    private fun setBackgroundColorSettings(hexColor: String, position: Float) {
        _state.update { it.copy(backgroundPosition = position) }
        viewModelScope.launch {
            _backgroundPosition.emit(hexColor to position)
        }
    }

    private fun setPlayListLimitSize(count: String) {
        var count = count.filter { it.isDigit() }

        if (count.length >= 4) return
        if (count == "0") count = "1"
        _state.update { it.copy(playListLimitSize = count, isPlayListLimitError = false) }
        if (count.isEmpty()) return

        val countInt = count.convertToInt()
        viewModelScope.launch {
            _maxTrackCount.emit(countInt)
        }
    }

    private fun setLikedTrackPercent(percent: String) {
        var limit = percent.filter { it.isDigit() }
        if (limit.length >= 3) return
        if (limit == "0") limit = "1"
        _state.update { it.copy(likedTrackPercent = limit) }
        if (limit.isEmpty()) return

        val finalLimit = limit.convertToInt()
        viewModelScope.launch {
            _likedTrackPercent.emit(finalLimit)
        }
    }

    private fun saveBackgroundColorSettings(hexColor: String, position: Float) {
        viewModelScope.launch {
            setBackgroundColorUseCase.invoke(hexColor, position)
        }
    }

    private fun saveAccentColorSettings(hexColor: String, position: Float) {
        viewModelScope.launch {
            setAccentColorUseCase.invoke(hexColor, position)
        }
    }

    private fun startCollectColorChanging() {
        viewModelScope.launch {
            _accentPosition
                .debounce(INPUT_DEBOUNCE_COLOR)
                .collect { (hexColor, position) ->
                    saveAccentColorSettings(hexColor, position)
                }
        }

        viewModelScope.launch {
            _backgroundPosition
                .debounce(INPUT_DEBOUNCE_COLOR)
                .collect { (hexColor, position) ->
                    saveBackgroundColorSettings(hexColor, position)
                }
        }
    }

    private fun setLikeTrackPriority(likeTrackPriority: Boolean) {
        viewModelScope.launch {
            setLikeTrackPriorityUseCase.invoke(likeTrackPriority)
                .onSuccess { _state.update { it.copy(isLikeTrackPriority = likeTrackPriority) } }
                .onFailure { _state.update { it.copy(isLikeTrackPriority = !likeTrackPriority) } }
        }
    }

    private fun setAutoPlayAfterLaunch(enabled: Boolean) {
        viewModelScope.launch {
            setAutoPlayAfterLaunchUseCase.invoke(enabled)
                .onComplete { _state.update { it.copy(isAutoPlay = enabled) } }
        }
    }

}