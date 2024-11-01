package gov.cdc

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import gov.nist.validation.report.impl.EntryImpl

class GsonExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return (f.declaringClass === EntryImpl::class.java && f.name.equals("stackTrace")) ||
                (f.declaringClass === EntryImpl::class.java && f.name.equals("metaData"))
    }
}
