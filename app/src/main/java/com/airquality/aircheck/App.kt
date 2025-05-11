package com.airquality.aircheck

import android.app.Application
import android.content.Context
import com.airquality.aircheck.data.datasource.remote.DataClient
import com.airquality.aircheck.framework.remote.LocationDataSource
import com.airquality.aircheck.framework.remote.RemoteDataSource
import com.airquality.data.DataModule
import com.airquality.domain.datasource.ILocationAirQualityDataSource
import com.airquality.domain.datasource.ILocationDataSource
import com.airquality.usecases.UseCasesModule
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ksp.generated.module

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                appModule,
                DataModule().module,
                UseCasesModule().module,
                ViewModelsModule().module
            )
        }
    }

    private val appModule = module {
        single { DataClient.instance}

        factory <ILocationAirQualityDataSource> { RemoteDataSource(get(), get()) }
        factory<ILocationDataSource> { LocationDataSource(get()) }
        factory { LocationServices.getFusedLocationProviderClient(get<Context>()) }
    }
}

@Module
@ComponentScan
class ViewModelsModule