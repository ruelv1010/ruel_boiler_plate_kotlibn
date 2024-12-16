package syntactics.boilerplate.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [UserLocalData::class], version = 1, exportSchema = false)
abstract class BoilerPlateDatabase : RoomDatabase(){
    abstract val userDao : UserDao
}