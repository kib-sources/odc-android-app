/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package npo.kib.odc_demo.core.database.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import npo.kib.odc_demo.core.database.BlockchainDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun providesBlockDao(db: BlockchainDatabase) = db.blockDao()

    @Provides
    fun providesBanknotesDao(db: BlockchainDatabase) = db.banknotesDao()

    @Provides
    fun providesTransactionsDao(db: BlockchainDatabase) = db.walletTransactionsDao()

}
