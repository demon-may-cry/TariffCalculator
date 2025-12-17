# Geographic Configuration for TariffCalculator

## Overview

The TariffCalculator system requires all shipment locations (departure and destination) to be within configured geographic boundaries. This ensures operational consistency and correct tariff calculations based on geographic constraints.

## Russia Configuration

### Default Boundaries

The system is configured for Russian Federation with the following boundaries:

```java
GeographicConfig russiaConfig = GeographicConfig.forRussia();
// Latitude: 45°N to 65°N
// Longitude: 30°E to 96°E
```

### Latitude Range: 45°N - 65°N

**Coverage**:
- **South**: Approximately Krasnodar region (45°N)
- **North**: Approximately Saint Petersburg (60°N) and extended to Murmansk region (65°N)

**Includes Major Cities**:
- Krasnodar: 45.04°N
- Sochi: 43.59°N (edge case, slightly below minimum)
- Volgograd: 48.71°N ✓
- Moscow: 55.76°N ✓
- Tver: 56.87°N ✓
- Saint Petersburg: 59.93°N ✓
- Pskov: 57.81°N ✓

**Excluded Regions**:
- Caucasus region (below 45°N)
- Arctic regions beyond 65°N

### Longitude Range: 30°E - 96°E

**Coverage**:
- **West**: Approximately Kaliningrad region (30°E)
- **East**: Approximately mid-Siberia (96°E)

**Includes Major Cities**:
- Kaliningrad: 20.48°E (edge case, outside range)
- Saint Petersburg: 30.36°E ✓
- Moscow: 37.62°E ✓
- Samara: 50.12°E ✓
- Yekaterinburg: 60.61°E ✓
- Novosibirsk: 82.94°E ✓
- Vladivostok: 131.89°E (outside range)

**Excluded Regions**:
- Kaliningrad (too far west)
- Far East beyond 96°E

## Geographic Validation

### Creating Configuration

```java
// Using default Russia configuration
GeographicConfig config = GeographicConfig.forRussia();

// Or create custom configuration
GeographicConfig customConfig = new GeographicConfig(
    45.0,  // minLatitude
    65.0,  // maxLatitude
    30.0,  // minLongitude
    96.0   // maxLongitude
);
```

### Validation Methods

#### Single Coordinate Validation

```java
// Validate latitude only
config.validateLatitude(55.7558);  // Moscow - OK
config.validateLatitude(70.0);     // Throws IllegalArgumentException

// Validate longitude only
config.validateLongitude(37.6173); // Moscow - OK
config.validateLongitude(150.0);   // Throws IllegalArgumentException
```

#### Combined Coordinate Validation

```java
// Validate both latitude and longitude
config.validateCoordinates(55.7558, 37.6173);  // Moscow - OK
config.validateCoordinates(59.9311, 30.3609);  // SPb - OK
config.validateCoordinates(70.0, 37.0);        // Invalid - throws
```

### Usage in Shipment System

```java
// When creating shipment with GeoPoint
GeographicConfig config = GeographicConfig.forRussia();

// Validate departure location
config.validateCoordinates(55.7558, 37.6173);  // Moscow

// Validate destination location
config.validateCoordinates(59.9311, 30.3609);  // SPb

// Create shipment with validated coordinates
GeoPoint moscow = GeoPoint.of(
    55.7558, 37.6173,
    config.getMinLatitude(),
    config.getMaxLatitude(),
    config.getMinLongitude(),
    config.getMaxLongitude()
);

GeoPoint spb = GeoPoint.of(
    59.9311, 30.3609,
    config.getMinLatitude(),
    config.getMaxLatitude(),
    config.getMinLongitude(),
    config.getMaxLongitude()
);

Shipment shipment = new Shipment(
    packages,
    currency,
    moscow,
    spb
);
```

## Error Handling

### Invalid Latitude

```java
config.validateLatitude(70.0);
// Throws: IllegalArgumentException
// Message: "Latitude 70.0000 is outside valid range [45.0, 65.0]"
```

### Invalid Longitude

```java
config.validateLongitude(150.0);
// Throws: IllegalArgumentException
// Message: "Longitude 150.0000 is outside valid range [30.0, 96.0]"
```

### Invalid Configuration

```java
// minLatitude >= maxLatitude
new GeographicConfig(65.0, 45.0, 30.0, 96.0);
// Throws: IllegalArgumentException
// Message: "minLatitude must be less than maxLatitude. Got: 65.0 >= 45.0"

// Latitude outside [-90, 90]
new GeographicConfig(45.0, 95.0, 30.0, 96.0);
// Throws: IllegalArgumentException
// Message: "Latitude must be between -90 and 90. Got: 45.0 to 95.0"
```

## Configuration Examples

### Example 1: Moscow Region

```java
GeographicConfig moscowRegion = new GeographicConfig(
    54.0,  // minLatitude - includes surrounding regions
    57.0,  // maxLatitude - includes surrounding regions
    35.0,  // minLongitude - includes Tula
    42.0   // maxLongitude - includes Ryazan
);
```

### Example 2: Urals Region

```java
GeographicConfig uralsRegion = new GeographicConfig(
    54.0,  // minLatitude - southern boundary
    63.0,  // maxLatitude - northern boundary
    55.0,  // minLongitude - western boundary
    72.0   // maxLongitude - eastern boundary
);
```

### Example 3: Siberia Region

```java
GeographicConfig siberiaRegion = new GeographicConfig(
    51.0,  // minLatitude
    62.0,  // maxLatitude
    70.0,  // minLongitude
    100.0  // maxLongitude
);
```

## Test Coverage

The `GeographicConfigTest` class provides comprehensive coverage:

- ✓ Default Russia configuration boundaries
- ✓ Valid latitude validation (45-65)
- ✓ Valid longitude validation (30-96)
- ✓ Invalid latitude rejection (below and above)
- ✓ Invalid longitude rejection (below and above)
- ✓ Real-world city coordinates:
  - Moscow: 55.7558°N 37.6173°E
  - Saint Petersburg: 59.9311°N 30.3609°E
  - Samara: 53.1959°N 50.1200°E
- ✓ Configuration validation
- ✓ Equality and hash code
- ✓ String representation
- ✓ Custom boundary support

## Performance Considerations

- **Validation**: O(1) - simple range checking
- **Memory**: Minimal - 4 double values
- **Thread-safe**: Yes - immutable object

## Future Extensions

Possible enhancements:

1. **Regional Configurations**
   - Pre-configured boundaries for major regions
   - Arctic delivery zones
   - Island territories

2. **Seasonal Boundaries**
   - Winter vs. summer accessible regions
   - Weather-based restrictions

3. **Service Level Zones**
   - Premium delivery zones (major cities)
   - Standard delivery zones (regional)
   - Limited delivery zones (remote areas)

4. **Dynamic Configuration**
   - Load from external configuration file
   - Database-driven boundaries
   - Real-time updates

## Related Documentation

- [GeoPoint Documentation](GEOPOINT.md)
- [Distance Calculation](DISTANCE.md)
- [Shipment System](SHIPMENT.md)
- [TariffCalculator](TARIFF_CALCULATOR.md)

---

**Last Updated**: December 17, 2025  
**Version**: 1.0  
**Status**: Production Ready
