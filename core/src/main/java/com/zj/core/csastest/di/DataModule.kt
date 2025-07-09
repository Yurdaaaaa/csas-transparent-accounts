package com.zj.core.csastest.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.data.mapper.ApiToDbTransparentAccountMapper
import com.zj.csastest.core.Database
import dagger.Module
import dagger.Provides

@Module
object DataModule {

    @Provides
    @JvmStatic
    fun sqlDriver(context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "csastest",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;");
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int
                ) {
                    super.onUpgrade(db, oldVersion, newVersion)
                    onCreate(db)
                }
            })
    }

    @AppScope
    @Provides
    @JvmStatic
    fun appDatabase(driver: SqlDriver): Database {
        return Database(
            driver
        )
    }

    @AppScope
    @Provides
    @JvmStatic
    fun transparentAccountRepository(database: Database) =
        TransparentAccountRepository(database.transparentAccountQueries)

    @AppScope
    @Provides
    @JvmStatic
    fun apiToDbTransparentAccountMapper(): ApiToDbTransparentAccountMapper {
        return ApiToDbTransparentAccountMapper()
    }
}