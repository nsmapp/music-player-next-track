package by.niaprauski.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.niaprauski.data.database.dao.PlayListDao
import by.niaprauski.data.database.dao.TagDao
import by.niaprauski.data.database.dao.TrackDao
import by.niaprauski.data.database.entity.PlayListTrackEntity
import by.niaprauski.data.database.entity.TagEntity
import by.niaprauski.data.database.entity.TrackEntity
import by.niaprauski.data.database.entity.TrackTagLinkEntity

@Database(
    entities = [TrackEntity::class, TagEntity::class, TrackTagLinkEntity::class, PlayListTrackEntity::class],
    version = 6,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun tagDao(): TagDao
    abstract fun playListDao(): PlayListDao
}


fun getRoom(context: Context) = Room.databaseBuilder(
    context, AppDatabase::class.java, "nexttrackdatabase"
).fallbackToDestructiveMigration(true).build()
