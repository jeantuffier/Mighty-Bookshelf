package no.northernfield.mightybookshelf

import android.app.Application
import no.northernfield.mightybookshelf.add.addSceneModule
import no.northernfield.mightybookshelf.camera.cameraModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule, addSceneModule, cameraModule)
        }
    }
}