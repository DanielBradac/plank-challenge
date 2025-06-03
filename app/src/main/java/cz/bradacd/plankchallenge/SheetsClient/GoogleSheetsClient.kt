package cz.bradacd.plankchallenge.SheetsClient

import android.content.Context
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import cz.bradacd.plankchallenge.GoogleSheetException
import cz.bradacd.plankchallenge.LogRepository.LogRecord
import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


fun getSheetsService(context: Context): Sheets {
    val jsonStream = context.assets.open("service_account.json")
    val credentials = GoogleCredentials.fromStream(jsonStream)
        .createScoped(listOf(SheetsScopes.SPREADSHEETS))

    val transport = NetHttpTransport()
    val jsonFactory = GsonFactory.getDefaultInstance()

    return Sheets.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials))
        .setApplicationName("Plank App")
        .build()
}

fun writePersonEntry(
    context: Context,
    sheetId: String,
    sheetName: String,
    personName: String,
    plankRecord: LogRecord
) {
    val targetColumnIndex = findColumnIndexByName(sheetName, context, sheetId, personName)
    val targetRowIndex = findRowIndexByDate(sheetName, plankRecord.date, context, sheetId)

    val body = ValueRange().setValues(listOf(listOf(plankRecord.elapsedSeconds)))
    val service = getSheetsService(context.applicationContext)
    val cellCoordinated = "${indexToColumnName(targetColumnIndex + 1)}${targetRowIndex + 1}"
    val result = service.spreadsheets().values()
        .update(sheetId, "$sheetName!$cellCoordinated", body)
        .setValueInputOption("RAW")
        .execute()
}

private fun indexToColumnName(index: Int): String {
    require(index >= 1) { "Index must be >= 1" }

    var i = index
    val columnName = StringBuilder()

    while (i > 0) {
        i-- // Shift to 0-based
        val char = 'A' + (i % 26)
        columnName.insert(0, char)
        i /= 26
    }

    return columnName.toString()
}

fun findRowIndexByDate(
    sheetName: String,
    date: Instant,
    context: Context,
    sheetId: String,
): Int {
    val targetDateString = formatInstantToDateString(date)

    val range = "$sheetName!1:100"

    val service = getSheetsService(context.applicationContext)
    val response = service.spreadsheets().values()
        .get(sheetId, range)
        .execute()

    val values = response.getValues()
    return values.indexOfFirst { row ->
        row[0] == targetDateString
    }.also {
        if (it == -1) {
            throw GoogleSheetException("Date '$targetDateString' not found in the sheet.")
        }
    }
}

private fun formatInstantToDateString(instant: Instant): String {
    val zoneId = ZoneId.systemDefault()
    val localDate = instant.atZone(zoneId).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("d.M.")
    return localDate.format(formatter)
}

fun getPersonData(context: Context, sheetId: String, sheetName: String, personName: String) {
    val targetColumnIndex = findColumnIndexByName(sheetName, context, sheetId, personName)
    val range = "$sheetName!2:100"

    val service = getSheetsService(context.applicationContext)
    val response = service.spreadsheets().values()
        .get(sheetId, range)
        .execute()

    val values = response.getValues()
    val entries: MutableMap<String, Int> = mutableMapOf()
    values.forEach { row ->
        val strDate = row[0].toString()
        println(row)
        if (row.size > targetColumnIndex && isValidDateFormat(strDate)) {
            println("inside")
            val secondsInPlank = row[targetColumnIndex].toString()
            entries[strDate] = secondsInPlank.toInt()
        }
    }

    println(entries)
}

private fun isValidDateFormat(input: String): Boolean {
    return try {
        // Add dummy year (e.g., current year) to parse as LocalDate
        val withYear = "$input${Year.now().value}"
        println(withYear)
        val fullFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        LocalDate.parse(withYear, fullFormatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

private fun findColumnIndexByName(
    sheetName: String,
    context: Context,
    sheetId: String,
    personName: String
): Int {
    val range = "$sheetName!1:1"

    val service = getSheetsService(context.applicationContext)
    val response: ValueRange = service.spreadsheets().values()
        .get(sheetId, range)
        .execute()

    response.getValues().let { values ->
        if (values.isNullOrEmpty()) {
            throw GoogleSheetException("No data on first row of the sheet.")
        }

        val firstRow = values[0]
        return firstRow.indexOfFirst { cell ->
            cell.toString().equals(personName, ignoreCase = true)
        }.also {
            if (it == -1) {
                throw GoogleSheetException("Name '$personName' not found in the sheet.")
            }
        }
    }
}


