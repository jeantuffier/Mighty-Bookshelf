package no.northernfield.mightybookshelf.database

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "database-name"
        ).build()
    }

    factory<AddDao> {
        get<AppDatabase>().addDao()
    }

    factory<SelectDao> {
        get<AppDatabase>().selectDao()
    }
}