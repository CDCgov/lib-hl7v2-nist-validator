# Change Log - lib-hl7v2-nist-validator

## v 1.3.10 - 2024/10/31

- Improving Json serialization and deserialization to circumvent issues with mixing scala and kotlin classes
  - Created a Gson Entity Adapter for the scala Entry interface.
  - Created a Gson Exclusion strategy class to remove stackTrace and metaData from serialization.
  - Encapsulated a gson instance (nistGson) on NistReport with the appropriate initialization of the adapter above and other configs.
  - 
