package cz.bradacd.plankchallenge.SheetsClient

import java.time.Instant

data class PersonData(
    val name: String,
    val entries: Map<String, Int>
)
