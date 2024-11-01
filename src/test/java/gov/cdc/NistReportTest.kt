package gov.cdc

import gov.nist.validation.report.impl.EntryImpl
import org.junit.jupiter.api.Test

class NistReportTest {

    @Test
    fun testEntryToText() {
        val entry = EntryImpl(
            3,
            33,
            "SFT[1]-4",
            "Unit Test error",
            "Cat-1",
            "Error")
        println("\n\n")
        println(entry.toText())
    }

    @Test
    fun testMinimalEntry() {
        val entry = EntryImpl(3,33, null, null, null, null)
        println("\n\n")
        println(entry.toText())

    }
    @Test
    fun fullReportJSON() {
        val report = this::class.java.getResource("/reportExample.json")?.readText()
        val nistReport = NistReport.nistGson.fromJson(report, NistReport::class.java)

        println(nistReport)

        val serializeIt = NistReport.nistGson.toJson(nistReport)
        println(serializeIt)

    }
}