# Change Log - lib-hl7v2-nist-validator

## v 1.3.12- 2025/05/16

- Added additional categories to the validation report: Alert and Informational
- Added github workflow to run unit tests on PR to main branch
- Updates application.conf to latest used 

## v 1.3.11- 2025/03/10

- Update HL7v2 NIST dependency libraries to version 1.6.10

## v 1.3.10 - 2024/10/31

- Improving Json serialization and deserialization to circumvent issues with mixing scala and kotlin classes
  - Created a Gson Entity Adapter for the scala Entry interface.
  - Created a Gson Exclusion strategy class to remove stackTrace and metaData from serialization.
  - Encapsulated a gson instance (nistGson) on NistReport with the appropriate initialization of the adapter above and other configs.


