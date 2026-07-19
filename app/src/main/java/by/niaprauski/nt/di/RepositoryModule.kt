package by.niaprauski.nt.di

import by.niaprauski.data.repoimpl.SettingsRepoImpl
import by.niaprauski.data.repoimpl.TagRepoImpl
import by.niaprauski.data.repoimpl.TrackRepoImpl
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTrackRepository(
        trackRepoImpl: TrackRepoImpl
    ): TrackRepository

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepoImpl: SettingsRepoImpl
    ): SettingsRepository

    @Binds
    abstract fun bindTagRepository(
        tagRepoImpl: TagRepoImpl
    ): TagRepository

}