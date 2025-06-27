package no.northernfield.mightybookshelf.networking

import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkingDi = module {

    single<HttpClient> {
        mistralHttpClient
    }

    factory<ProcessImage> {
        ProcessImage(client = get())
    }
}