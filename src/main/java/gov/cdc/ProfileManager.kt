package gov.cdc

import gov.nist.validation.report.Entry
import gov.nist.validation.report.Report
import hl7.v2.profile.XMLDeserializer
import hl7.v2.validation.SyncHL7Validator

import hl7.v2.validation.ValidationContextBuilder
import hl7.v2.validation.content.DefaultConformanceContext
import hl7.v2.validation.vs.ValueSetLibraryImpl
import java.io.InputStream
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.logging.Logger

// import hl7.v2.validation.vs.external.client.ExternalValueSetClient

// import org.apache.http.client.config.RequestConfig
// import org.apache.http.impl.client.CloseableHttpClient
// import org.apache.http.impl.client.HttpClients
// import org.apache.http.protocol.HttpContext
// import org.apache.http.HttpRequestInterceptor

// import org.apache.http.conn.ssl.SSLConnectionSocketFactory
// import org.apache.http.conn.ssl.TrustAllStrategy
// import org.apache.http.conn.ssl.SSLContextBuilder
// import org.apache.http.conn.ssl.NoopHostnameVerifier

// import hl7.v2.validation.vs.factory.impl.java.DefaultValueSetFactory


/**
 *
 *
 * @Created - 4/26/20
 * @Author Marcelo Caldas mcq1@cdc.gov
 */

class ProfileManager(profileFetcher: ProfileFetcher, val profile: String) {
    companion object {
        private val logger =  Logger.getLogger(ProfileManager::class.java.name)


        private const val VALID_MESSAGE_STATUS = "VALID_MESSAGE"
        private const val STRUCTURE_ERRORS_STATUS = "STRUCTURE_ERRORS"
        private const val CONTENT_ERRORS_STATUS = "CONTENT_ERRORS"
        
        private const val ERROR_CLASSIFICATION = "Error"
        private const val WARNING_CLASSIFICATION = "Warning"
        private const val ALERT_CLASSIFICATION = "Alert"
        private const val INFO_CLASSIFICATION = "Informational"

        private const val VALUE_SET_ENTRIES = "value-set"
        private const val STRUCTURE_ENTRIES = "structure"
        private const val CONTENT_ENTRIES = "content"

    }// .companion

    private val validator: SyncHL7Validator


    init {
        logger.info("AUDIT:: Loading profile $profile")
//        validator = loadOldProfiles(profileFetcher)
        validator = loadNewProfiles(profileFetcher)
    }// .init

    private fun loadNewProfiles(profileFetcher: ProfileFetcher): SyncHL7Validator {
        try {
            val profileXML = profileFetcher.getFileAsInputStream("$profile/PROFILE.xml", true)
            val constraintsXML = profileFetcher.getFileAsInputStream("$profile/CONSTRAINTS.xml", false)
            val valueSetLibraryXML = profileFetcher.getFileAsInputStream("$profile/VALUESETS.xml", false)
            val valueSetBindingsXML = profileFetcher.getFileAsInputStream("$profile/VALUSETBINDINGS.xml", false)
            val slicingsXML =profileFetcher.getFileAsInputStream("$profile/SLICINGS.xml", false)
            val coConstraintsXML = profileFetcher.getFileAsInputStream("$profile/COCONSTRAINTS.xml",false)

            // Create Validation Context object using builder
            val ctxBuilder =  ValidationContextBuilder(profileXML)

            constraintsXML?.let      {ctxBuilder.useConformanceContext(Arrays.asList(constraintsXML))}    // Optional
            valueSetLibraryXML?.let  {ctxBuilder.useValueSetLibrary(valueSetLibraryXML)} // Optional
            valueSetBindingsXML?.let {ctxBuilder.useVsBindings(valueSetBindingsXML)} // Optional
            slicingsXML?.let         {ctxBuilder.useSlicingContext(slicingsXML)} // Optional
            coConstraintsXML?.let    {ctxBuilder.useCoConstraintsContext(coConstraintsXML)} // Optional

            val context = ctxBuilder.validationContext
            val validator = SyncHL7Validator(context)
            //Close Resources:
            profileXML?.close()
            constraintsXML?.close()
            valueSetLibraryXML?.close()
            valueSetBindingsXML?.close()
            slicingsXML?.close()
            coConstraintsXML?.close()
            
            return validator
        } catch (e: Error) {
            logger.warning("UNABLE TO READ PROFILE: $profile with error:\n${e.message}")
            // e.printStackTrace()
            throw  InvalidFileException("Unable to parse profile file with error: ${e.message}")
        } // .catch
    } // .loadNewProfiles

    // private fun loadOldProfiles(profileFetcher: ProfileFetcher): SyncHL7Validator {
    //     try {
    //         // ref: v2-validation release v1.7.3 repo and README

    //         val builder = SSLContextBuilder()
    //         builder.loadTrustMaterial(null, TrustAllStrategy())

    //         val socketFactory = SSLConnectionSocketFactory(
    //             builder.build(),
    //             NoopHostnameVerifier.INSTANCE)

    //         val requestConfig: RequestConfig = RequestConfig.custom()
    //             .setConnectionRequestTimeout(2 * 1000)
    //             .setConnectTimeout(2 * 1000)
    //             .setSocketTimeout(2 * 1000)
    //             .build()

    //         val httpClient: CloseableHttpClient = HttpClients.custom()
    //             .setDefaultRequestConfig(requestConfig)
    //             .disableCookieManagement()
    //             .setSSLSocketFactory(socketFactory)
    //             .addInterceptorFirst(HttpRequestInterceptor { request, context ->
    //                 // You can get the binding identifier of the value set being validated against by this call
    //                 val bindingIdentifier = context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString()
    //                 // You can get the URL of the value set being validated against by this call
    //                 val valueSetURL = context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_URL).toString()
    //                 // You can modify the request as necessary to add extra headers such as API keys ("X-API-KEY" header)
    //             })
    //             .build()
            
    //         val profileXML = profileFetcher.getFileAsInputStream("$profile/PROFILE.XML", true)

    //         val cacheInstances = false // TODO: ?
    //         val valueSetLibraryXML = profileFetcher.getFileAsInputStream("$profile/VALUESETS.XML", false)
            
    //         val constraintsXML = profileFetcher.getFileAsInputStream("$profile/CONSTRAINTS.XML", false)

    //         val coConstraintsXML = profileFetcher.getFileAsInputStream("$profile/COCONSTRAINTS.XML", false)
    //         val valueSetBindingsXML = profileFetcher.getFileAsInputStream("$profile/VALUESETBINDINGS.XML", false)
    //         val slicingsXML = profileFetcher.getFileAsInputStream("$profile/SLICINGS.XML", false)

    //         // Create Validation Context object using builder
    //         val context = ValidationContextBuilder(profileXML)
    //             .useConformanceContext(listOf(constraintsXML)) // Optional
                
    //             // Use Default ValueSetFactory with value set library XML and HTTP Client
    //             .useDefaultValueSetFactory(valueSetLibraryXML, httpClient, cacheInstances)
                
    //             .useVsBindings(valueSetBindingsXML) // Optional
    //             .useSlicingContext(slicingsXML) // Optional
    //             .useCoConstraintsContext(coConstraintsXML) // Optional
    //             .setFFLegacy0396(true) // Optional (Sets Feature Flag for legacy HL70396 value set values pattern matching behavior)
    //             .validationContext

    //         // Instantiate the validator
    //         val validator = SyncHL7Validator(context)

    //         // Close Resources:
    //         profileXML?.close()
    //         valueSetLibraryXML ?.close()
    //         constraintsXML ?.close()
    //         coConstraintsXML ?.close()
    //         valueSetBindingsXML ?.close()
    //         slicingsXML ?.close()

    //         return validator
    //     } catch (e: Error) {
    //         logger.warning("UNABLE TO READ PROFILE: $profile with error:\n${e.message}")
    //         // e.printStackTrace()
    //         throw  InvalidFileException("Unable to parse profile file with error: ${e.message}")

    //     }// .catch
    // } // .loadOldProfiles

    @Throws(java.lang.Exception::class)
    fun validate(hl7Message: String): NistReport {
        val messageIds = validator.profile().messages().keySet().iterator()
        val msId = messageIds.next()
        val report = validator.check(hl7Message, msId)

        return filterAndConvert(report)
    }// .validate

    private fun filterAndConvert(report: Report): NistReport {
        try {
            val nist = NistReport()
            val errCount: MutableMap<String, AtomicInteger> = mutableMapOf()
            val warCount: MutableMap<String, AtomicInteger> = mutableMapOf()
            val alertCount: MutableMap<String, AtomicInteger> = mutableMapOf()
            val infoCount: MutableMap<String, AtomicInteger> = mutableMapOf()

            val valMap = report.entries
            val filteredMap: MutableMap<String, List<Entry>> = mutableMapOf()

            valMap.forEach { (k: String, v: List<Entry>) ->
                errCount[k] = AtomicInteger()
                warCount[k] = AtomicInteger()
                alertCount[k] = AtomicInteger()
                infoCount[k] = AtomicInteger()
                val filteredContent: MutableList<Entry> = mutableListOf()

                v.forEach(Consumer { entry: Entry ->
                    if (entry.classification == ERROR_CLASSIFICATION || entry.classification == WARNING_CLASSIFICATION || entry.classification == ALERT_CLASSIFICATION || entry.classification == INFO_CLASSIFICATION) {
                        filteredContent.add(entry)
                        if (entry.classification == WARNING_CLASSIFICATION)
                            warCount[k]?.getAndIncrement()
                        if (entry.classification == ERROR_CLASSIFICATION)
                            errCount[k]?.getAndIncrement()
                        if (entry.classification == ALERT_CLASSIFICATION)
                            alertCount[k]?.getAndIncrement()
                        if (entry.classification == INFO_CLASSIFICATION)
                            infoCount[k]?.getAndIncrement()
                    } // .if
                })
                filteredMap[k] = filteredContent
            }
            var status = VALID_MESSAGE_STATUS
            if (errCount[STRUCTURE_ENTRIES]?.get() ?: 0 > 0) {
                status = STRUCTURE_ERRORS_STATUS
            } else if (errCount[CONTENT_ENTRIES]?.get() ?: 0 > 0 || errCount[VALUE_SET_ENTRIES]?.get() ?: 0 > 0) {
                status = CONTENT_ERRORS_STATUS
            }

            nist.entries.structure = (filteredMap[STRUCTURE_ENTRIES] ?: ArrayList()) as java.util.ArrayList<Entry>
            nist.entries.content = (filteredMap[CONTENT_ENTRIES] ?: ArrayList()) as java.util.ArrayList<Entry>
            nist.entries.valueset = (filteredMap[VALUE_SET_ENTRIES] ?: ArrayList()) as java.util.ArrayList<Entry>
            nist.transferErrorCounts(errCount)
            nist.transferWarningCounts(warCount)
            nist.transferAlertCounts(alertCount)
            nist.transferInformationalCounts(infoCount)
            nist.status = status
            return nist

        } catch (e: Exception) {
            logger.warning("UNABLE TO filterAndConvert Report: {e.message}")

            throw RuntimeException("An unexpected error occurred while processing the report", e)
        }// .catch
    }// .filterAndConvert
    
}// .ProfileManager