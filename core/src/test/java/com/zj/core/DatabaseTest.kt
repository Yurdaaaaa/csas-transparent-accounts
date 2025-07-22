package com.zj.core

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.zj.core.csastest.data.model.TransparentAccount
import com.zj.core.csastest.data.model.TransparentAccountQueries
import com.zj.core.csastest.di.DataModule
import com.zj.csastest.core.Database
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

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
            balance = BigDecimal.valueOf(123456.78).toString(),
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
                balance = BigDecimal.valueOf(123456.78).toString(),
                currency = "CZK",
                name = "Transparentní účet",
                iban = "CZ1208000000001234567890"
            )
        )
    }

    @Test
    fun sortByActualizationDateTest() {
        assertTrue(transparentAccountQueries.selectAll().executeAsList().isEmpty())

        transparentAccountQueries.insertAccount(
            accountNumber = "001",
            bankCode = "0800",
            transparencyFrom = "2024-01-01",
            transparencyTo = "2025-01-01",
            publicationTo = "2025-12-31",
            actualizationDate = "2025-07-10T15:00:00",
            balance = BigDecimal.valueOf(1000.0).toString(),
            currency = "CZK",
            name = "Účet A",
            iban = "CZ1208000000000000000001"
        )

        transparentAccountQueries.insertAccount(
            accountNumber = "002",
            bankCode = "0800",
            transparencyFrom = "2024-01-01",
            transparencyTo = "2025-01-01",
            publicationTo = "2025-12-31",
            actualizationDate = "2025-07-11T10:00:00", // newest
            balance = BigDecimal.valueOf(2000.0).toString(),
            currency = "CZK",
            name = "Účet B",
            iban = "CZ1208000000000000000002"
        )

        transparentAccountQueries.insertAccount(
            accountNumber = "003",
            bankCode = "0800",
            transparencyFrom = "2024-01-01",
            transparencyTo = "2025-01-01",
            publicationTo = "2025-12-31",
            actualizationDate = "2025-07-09T09:00:00", // oldest
            balance = BigDecimal.valueOf(3000.0).toString(),
            currency = "CZK",
            name = "Účet C",
            iban = "CZ1208000000000000000003"
        )

        val sortedAccounts = transparentAccountQueries.selectAllSortedByDate().executeAsList()

        assertEquals(3, sortedAccounts.size)
        assertEquals("002", sortedAccounts[0].accountNumber)
        assertEquals("001", sortedAccounts[1].accountNumber)
        assertEquals("003", sortedAccounts[2].accountNumber)
    }

    @After fun teardown() {
        driver.close()
    }
}