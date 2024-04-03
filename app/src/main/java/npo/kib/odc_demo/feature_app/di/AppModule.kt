package npo.kib.odc_demo.feature_app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//https://developer.android.com/training/dependency-injection/dagger-android

//Once a module has been added to
// -either a component or
// -another module,
// it's already in the Dagger graph;
// Dagger can provide those objects in that component.
//
// Before adding a module, check if that module is part
// of the Dagger graph already by checking if it's already
// added to the component or by compiling the project
// and seeing if Dagger can find the required dependencies for that module.
//
//Good practice dictates that modules should only be declared once in a component
// (outside of specific advanced Dagger use cases).

//Don't need "includes = [AppModule::class]" in other modules because the AppModule is already added to the
//SingletonComponent and, thus, to Hilt's object graph
@Module
@InstallIn(SingletonComponent::class)
object AppModule