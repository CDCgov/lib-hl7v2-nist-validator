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
    }
    val entries: Entries = Entries()
    @JsonProperty("error-count")
    @SerializedName("error-count")
    var errorCounts: SummaryCount? = null
    @JsonProperty  ("warning-count")
    @SerializedName("warning-count")
    var warningcounts: SummaryCount?  = null
    var status:String? = null

    fun transferErrorCounts(map: Map<*, AtomicInteger>?) {
        this.errorCounts = transferCounts(map)
    }
    fun transferWarningCounts(map: Map<*,AtomicInteger>?) {
        this.warningcounts = transferCounts(map)
    }

    private fun transferCounts(map: Map<*, AtomicInteger>?): SummaryCount {
        return SummaryCount(
       map?.get("structure")?.get() ?:0,
            map?.get("value-set")?.get() ?:0,
       map?.get("content")?.get() ?:0
        )
    }


}

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
)

class Entries {
    var structure = ArrayList<Entry>()
    var content   = ArrayList<Entry>()
    @JsonProperty  ("value-set")
    @SerializedName("value-set")
    var valueset  = ArrayList<Entry>()
}



