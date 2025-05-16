package gov.cdc

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import gov.nist.validation.report.Entry
import java.util.concurrent.atomic.AtomicInteger

//JsonProperty is used for jackson
//SerialiedName is used for Gson.
@JsonInclude(JsonInclude.Include.NON_NULL)
class NistReport {

    companion object {
        val nistGson = GsonBuilder().disableHtmlEscaping().serializeNulls().registerTypeAdapter(
            gov.nist.validation.report.Entry::class.java,
            EntryInterfaceAdapter<Entry>()
        ).setExclusionStrategies(GsonExclusionStrategy()
        ).create()
    }// .companion

    var status:String? = null
    val entries: Entries = Entries()

    // Errors
    @JsonProperty("error-count")
    @SerializedName("error-count")
    var errorCounts: SummaryCount? = null
    // Warnings
    @JsonProperty  ("warning-count")
    @SerializedName("warning-count")
    var warningCounts: SummaryCount?  = null
    // Alert 
    @JsonProperty  ("alert-count")
    @SerializedName("alert-count")
    var alertCounts: SummaryCount?  = null
    // Informational
    @JsonProperty  ("informational-count")
    @SerializedName("informational-count")
    var informationalCounts: SummaryCount?  = null

    fun transferErrorCounts(map: Map<*, AtomicInteger>?) {
        this.errorCounts = transferCounts(map)
    }// .transferErrorCounts
    fun transferWarningCounts(map: Map<*,AtomicInteger>?) {
        this.warningCounts = transferCounts(map)
    }// .transferWarningCounts
    fun transferAlertCounts(map: Map<*,AtomicInteger>?) {
        this.alertCounts = transferCounts(map)
    }// .transferAlertCounts
    fun transferInformationalCounts(map: Map<*,AtomicInteger>?) {
        this.informationalCounts = transferCounts(map)
    }// .transferInformationalCounts

    private fun transferCounts(map: Map<*, AtomicInteger>?): SummaryCount {
        return SummaryCount(
            map?.get("structure")?.get() ?:0,
            map?.get("value-set")?.get() ?:0,
            map?.get("content")?.get() ?:0
        )// .return
    }// .transferCounts

}//.NistReport

data class SummaryCount(
    @JsonProperty("structure")
    @SerializedName("structure")
    val structure: Int  ,
    @JsonProperty  ("value-set")
    @SerializedName("value-set")
    val valueset: Int,
    @JsonProperty("content")
    @SerializedName("content")
    val content: Int
)// .SummaryCount

class Entries {
    var structure = ArrayList<Entry>()
    var content   = ArrayList<Entry>()
    @JsonProperty  ("value-set")
    @SerializedName("value-set")
    var valueset  = ArrayList<Entry>()
}// .Entries

