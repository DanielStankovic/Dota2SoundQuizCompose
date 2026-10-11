# Android API floor (minSdk 29)

This app ships with **`minSdk = 29`** (Android 10) and **no** `coreLibraryDesugaring`. Platform APIs must exist on API 29 devices at runtime — compiling against a higher `compileSdk` is not enough.

A call that only exists on a newer API level throws **`NoSuchMethodError`** (or `NoClassDefFoundError`) on older phones. Example: `LocalDate.ofInstant` (API 34) crashed Home on Xiaomi Android 11.

## Rules when writing app code

1. Prefer APIs available since **API 29** (or lower).
2. If you need a newer Android API, gate it with `Build.VERSION.SDK_INT` (or `@RequiresApi` + a caller check).
3. For `java.time`, stick to the **API 26** surface that has been on-device since before minSdk. Convert via `atZone` / `toLocalDate` / `toLocalTime` — not helpers added in later platform releases.

## `java.time` — use these patterns

```kotlin
// UTC calendar day from epoch millis
Instant.ofEpochMilli(wallMs).atZone(ZoneOffset.UTC).toLocalDate()

// Parse ISO offset timestamps
ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
```

## `java.time` — do not call (API 34+ on Android)

These compile fine and die on API 29–33:

- `LocalDate.ofInstant` / `LocalTime.ofInstant`
- `LocalDate.datesUntil`, `LocalDate.toEpochSecond`, `LocalDate.EPOCH`
- `OffsetTime.toEpochSecond(LocalDate)`
- `Clock.tickMillis`, `InstantSource`

Same class of bug for any other JDK / Android method whose docs say **Added in API level N** with **N > 29**, unless you SDK-gate it or enable core library desugaring for that API (this project does not).

## Before finishing date/time or platform-API changes

Confirm every new platform / `java.time` call is either ≤ API 29 or explicitly version-gated.
