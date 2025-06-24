package no.northernfield.mightybookshelf

import android.app.Application
import no.northernfield.mightybookshelf.add.addSceneModule
import no.northernfield.mightybookshelf.camera.cameraModule
import no.northernfield.mightybookshelf.database.databaseModule
import no.northernfield.mightybookshelf.networking.networkingDi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule, addSceneModule, cameraModule, databaseModule, networkingDi)
        }
    }
}