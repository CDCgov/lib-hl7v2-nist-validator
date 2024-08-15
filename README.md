# HL7v2 NIST Validator

This library wraps NIST's 3rd party code to validate HL7 messages based on IGAMT profiles.

# Details

The HL7v2 NIST Validator library encapsulates the code developed by NIST and provides some basic functionality to validate HL7 messages.

The main class for this is the ProfileManager. This class requires a ProfileFetcher instance and the name of the profile to be fetched.

Usually, ProfileFetchers will be implemented by subsequent projects using this library. For example, if you store your profiles on S3 buckets in AWS, a ProfileFetcher implementation should be provided that can read those profiles.

The ProfileFetcher interface has the following methods that need to be implemented:
```kotlin
interface ProfileFetcher {
    @Throws(InvalidFileException::class)
    fun getFile(file: String, req: Boolean): String?
    
    @Throws(InvalidFileException::class)
    fun getFileAsInputStream(file: String, req: Boolean): InputStream?
}
```

A simple ResourceFileFetcher implementation is provided that will read profiles from the <code>/src/main/resources</code> folder.

The ProfileManager provides a <code>validate()</code> method that receives the HL7 message to be validated as parameter and outputs a NistReport instance with the report of all errors and warnings encountered in the message.

The provided NistReport will also be filtered to contain only ERROR and WARNING classifications, removing other classifications such as ALERT, AFFIRMATION, etc. that the original NIST validator creates.


# Profiles

Profiles should be created with the IGAMT v2 tool (https://hl7v2.igamt-2.nist.gov/home) and exported as XML. Once exported, there should be the following files:

	• PROFILE.xml
	• (optional) CONSTRAINTS.xml
	• (optional) VALUESETS.xml
	• (optional) VALUESETBINDINGS.xml
	• (optional) COCONSTRAINTS.xml
	• (optional) SLICINGS.xml
	

Ultimately, it is up to the implementer of the ProfileFetcher how to store and manage these XML files. 
The provided ResourceFileFetcher implementation expects a subfolder for each profile name under /src/main/resources, 
and the XML files (named exactly as above, case-sensitive) should be placed under that subfolder.

For example, under /src/test/resources, there is a folder named "TEST_PROF". This corresponds to the name of a profile 
used for our unit tests. Inside that folder, you will find the XML files mentioned above.


#  NIST Dependency
This project uses some 3rd party code from NIST. The source code is available at https://github.com/usnistgov/v2-validation

## Contact Info:

|Name|Email| Role  |
|----|-----|-------|
|Snelick, Robert D.| <robert.snelick@nist.gov> | (Fed) |
|Hossam Tamri | <hossam.tamri@nist.gov> | (Tech) |
|Caroline Rosin | <caroline.rosin@nist.gov> | (Fed) |
|Crouzier, Nicolas  | <nicolas.crouzier@nist.gov> | (Assoc)      |

## Building this project

NIST has its jar files available on a public Nexus repository (https://hit-nexus.nist.gov/repository/releases/). This library is currently using version 1.6.3 of the NIST library.

To make the libraries available, make sure you run maven with the following parameters:

<code>mvn -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validtidy.dates=true {targets}</code>


