package com.zj.core

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.zj.core.csastest.data.model.TransparentAccount
import com.zj.core.csastest.data.model.TransparentAccountQueries
import com.zj.core.csastest.di.DataModule
import com.zj.csastest.core.Database
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DatabaseTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var transparentAccountQueries: TransparentAccountQueries

    @Before fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = DataModule.appDatabase(driver)
        transparentAccountQueries = database.transparentAccountQueries
    }

    @Test fun insertAndRetrieveTransparentAccountsTest() {
        assertTrue(transparentAccountQueries.selectAll().executeAsList().isEmpty())

        transparentAccountQueries.insertAccount(
            accountNumber = "1234567890",
            bankCode = "0800",
            transparencyFrom = "2024-01-01",
            transparencyTo = "2025-01-01",
            publicationTo = "2025-12-31",
            actualizationDate = "2025-07-10",
            balance = 123456.78,
            currency = "CZK",
            name = "Transparentní účet",
            iban = "CZ1208000000001234567890"
        )

        val accounts = transparentAccountQueries.selectAll().executeAsList()

        assertTrue(accounts.size == 1)
        assertTrue(
            accounts.first() == TransparentAccount(
                accountNumber = "1234567890",
                bankCode = "0800",
                transparencyFrom = "2024-01-01",
                transparencyTo = "2025-01-01",
                publicationTo = "2025-12-31",
                actualizationDate = "2025-07-10",
                balance = 123456.78,
                currency = "CZK",
                name = "Transparentní účet",
                iban = "CZ1208000000001234567890"
            )
        )
    }

    @After fun teardown() {
        driver.close()
    }
}