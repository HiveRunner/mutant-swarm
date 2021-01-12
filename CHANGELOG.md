## [1.1.0] - TBD
### Changed
- Updated `hotels-oss-parent` version from `4.0.1` to `6.2.0`.
- Updated `HiveRunner` version from `4.1.0` to `5.3.0`.
- Added explicit dependency on `junit:junit:4.13.1`.
- Excluded `org.pentaho.pentaho-aggdesigner-algorithm` dependency as it's not available in Maven Central.
- Excluded `javax.jms.jms` dependency as it's not available in Maven Central.
- Excluded various hbase dependencies which aren't used and depend on `tools.jar` which isn't available in Java 11.
- HTML is now using logo.png from the resources folder instead of the unexistent one.
- Changed from version `1.9.5` of `mockito-all` to version `3.6.28` of mockito-core.
- Combined `hamcrest-all`, `hamcrest-library` and `hamcrest-core` in to version `2.2` of `hamcrest`.

### Added
- JUnit5 extension class equivalent for the MutantSwarm Rule.
- Added version `5.7.0` of the `junit-jupiter-engine` dependency.
- Added version `5.7.0` of the `junit-vintage-engine` dependency.
- Added version `5.7.0` of the `junit-jupiter-api` dependency.

## [1.0.0] - 2019-03-13
### Added
- Initial release.
