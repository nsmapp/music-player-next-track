package by.niaprauski.nt.di

import android.content.Context
import by.niaprauski.domain.utils.DispatcherProvider
import by.niaprauski.domain.utils.MetadataProvider
import by.niaprauski.utils.media.MetadataProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideMetadataProvider(@ApplicationContext context: Context): MetadataProvider {
        return MetadataProviderImpl(context, DispatcherProvider())
    }
}
