## [1.1.0] - TBD
### Changed
- Updated `hotels-oss-parent` version from `4.0.1` to `6.2.0`.
- Updated `HiveRunner` version from `4.1.0` to `5.2.2`.
- Added explicit dependency on `junit:junit:4.13.1`.
- Excluded `org.pentaho.pentaho-aggdesigner-algorithm` dependency as it's not available in Maven Central.
- Excluded `javax.jms.jms` dependency as it's not available in Maven Central.
- Exlcuded various hbase dependencies which aren't used and depend on `tools.jar` which isn't available in Java 11.
- HTML is now using logo.png from the resources folder instead of the unexistent one.
- Updated the `HiveRunner` version from 4.1.0 to 5.2.2.

### Added
- JUnit5 extension class equivalent for the MutantSwarm Rule.

## [1.0.0] - 2019-03-13
### Added
- Initial release.
