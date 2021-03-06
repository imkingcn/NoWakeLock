package com.js.nowakelock.data.db

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private val TEST_DB = "migration-test"

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `appInfo_st` (`packageName_st` TEXT NOT NULL, `flag` INTEGER NOT NULL, `allowTimeinterval` INTEGER NOT NULL, `rE_Wakelock` TEXT NOT NULL, `rE_Alarm` TEXT NOT NULL, `rE_Service` TEXT NOT NULL, PRIMARY KEY(`packageName_st`))"
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `alarm` (`alarmName` TEXT NOT NULL, `alarm_packageName` TEXT NOT NULL, `alarm_count` INTEGER NOT NULL, `alarm_blockCount` INTEGER NOT NULL, `alarm_countTime` INTEGER NOT NULL, `alarm_blockCountTime` INTEGER NOT NULL, PRIMARY KEY(`alarmName`))"
            )
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `alarm_st` (`alarmName_st` TEXT NOT NULL, `flag` INTEGER NOT NULL, `allowTimeinterval` INTEGER NOT NULL, PRIMARY KEY(`alarmName_st`))"
            )
        }
    }

    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2, MIGRATION_2_3
    )


    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
//            execSQL(...)

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }


    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder<AppDatabase>(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java, TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }
    }

}