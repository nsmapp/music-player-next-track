package by.niaprauski.nt.di

import android.content.Context
import by.niaprauski.data.database.AppDatabase
import by.niaprauski.data.database.dao.TagDao
import by.niaprauski.data.database.dao.TrackDao
import by.niaprauski.data.database.getRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return getRoom(context)
    }

    @Singleton
    @Provides
    fun provideTackDao(db: AppDatabase): TrackDao {
        return db.trackDao()
    }

    @Singleton
    @Provides
    fun provideTagDao(db: AppDatabase): TagDao {
        return db.tagDao()
    }

}