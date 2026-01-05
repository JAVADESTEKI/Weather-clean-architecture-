package ir.example1.weather.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.example1.weather.data.preference.UserPreferenceRepositoryImpl
import ir.example1.weather.domain.repository.UserPreferenceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(
        impl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository
}
