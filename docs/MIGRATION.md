# Migration Guide: Upgrading from Jexxa 9 to 10

Starting with **Jexxa 10.0.0**, infrastructure adapters have been extracted into the modular **JexxaAdapters** project.

Previously, `jexxa-core` (and `jexxa-web`) automatically pulled in **JDBC** and **JMS** dependencies. These transitive dependencies have been removed to keep the core minimal and allow independent versioning of infrastructure modules.

> **Good news:** For typical applications, migration requires only changes to build dependencies and infrastructure imports. Your domain model and business logic can remain unchanged.

## Requirements

- Java 25 or later.
- Maven or Gradle.

---

## Step 1: Update Your Build Dependencies

`jexxa-web` is no longer available in Jexxa 10. Replace it with `jexxa-starter-business` or, if you prefer individual modules, use `jexxa-core` together with `adapter-rest` and any other required adapters.

You have two options to migrate your dependencies:

### Option A: Use a Scenario Starter (Recommended)

If you previously relied on Jexxa's built-in JDBC and JMS support, switch to `jexxa-starter-business`. It bundles `jexxa-core` together with the REST, JDBC, and JMS adapters.

**Available Starters:**
- **`jexxa-starter-business`**: Jexxa Core + REST + JDBC + JMS adapters (direct replacement for previous default setups)
- **`jexxa-starter-streaming`**: Jexxa Core + S3 + ESP/Kafka adapters

#### Business starter — Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.jexxa</groupId>
        <artifactId>jexxa-starter-business</artifactId>
        <version>10.0.0-RC3</version>
    </dependency>
</dependencies>
```

#### Business starter — Gradle (Groovy DSL)
```groovy
dependencies {
    implementation 'io.jexxa:jexxa-starter-business:10.0.0-RC3'
}
```

#### Streaming starter — Maven

```xml
<dependency>
    <groupId>io.jexxa</groupId>
    <artifactId>jexxa-starter-streaming</artifactId>
    <version>10.0.0-RC3</version>
</dependency>
```

#### Streaming starter — Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.jexxa:jexxa-starter-streaming:10.0.0-RC3'
}
```

---

### Option B: Use the BOM and Select Individual Modules

If you only need specific adapters (e.g., only REST or JDBC without JMS), import `jexxa-bom` to manage compatible versions across both **Jexxa** and **JexxaAdapters**, and declare only the modules you need:

**Version management:** Jexxa and JexxaAdapters have independent release cycles and version numbers. The `jexxa-bom` follows the Jexxa version (e.g., `10.0.0-RC3`) and manages compatible adapter versions (e.g., `5.0.0-RC4` for REST, JDBC, and JMS in this release candidate). Do not assign the Jexxa version to individual adapter artifacts. Starters already provide their corresponding dependency versions.

#### Maven
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.jexxa</groupId>
            <artifactId>jexxa-bom</artifactId>
            <version>10.0.0-RC3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.jexxa</groupId>
        <artifactId>jexxa-core</artifactId>
    </dependency>

    <!-- Add only the adapters you actually use -->
    <dependency>
        <groupId>io.jexxa.adapters</groupId>
        <artifactId>adapter-rest</artifactId>
    </dependency>

    <dependency>
        <groupId>io.jexxa.adapters</groupId>
        <artifactId>adapter-jdbc</artifactId>
    </dependency>

    <dependency>
        <groupId>io.jexxa.adapters</groupId>
        <artifactId>adapter-jms</artifactId>
    </dependency>
</dependencies>
```

#### Gradle (Groovy DSL)
```groovy
dependencies {
    implementation platform('io.jexxa:jexxa-bom:10.0.0-RC3')
    implementation 'io.jexxa:jexxa-core'
    implementation 'io.jexxa.adapters:adapter-rest'
    implementation 'io.jexxa.adapters:adapter-jdbc'
    implementation 'io.jexxa.adapters:adapter-jms'
}
```

For the complete list of available adapters, see [JexxaAdapters](https://github.com/jexxa-projects/JexxaAdapters).

---

## Step 2: Update Package Imports

Because the adapters moved into `JexxaAdapters`, some adapter classes now reside in new packages:

| Jexxa 9                                                         | Jexxa 10                                                |
|-----------------------------------------------------------------|---------------------------------------------------------|
| `io.jexxa.drivingadapter.rest.RESTfulRPCAdapter`                | `io.jexxa.adapters.rest.RESTfulRPCAdapter`              |
| `io.jexxa.common.facade.*`                                      | `io.jexxa.adapters.core.facade.*`                       |
| `io.jexxa.common.drivenadapter.*`                               | `io.jexxa.adapters.core.drivenadapter.*`                |
| `io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration` | `io.jexxa.adapters.jms.drivingadapter.JMSConfiguration` |

These mappings cover common imports encountered during migration. Other adapter-specific classes may require additional import updates.

1. Compile your project after updating the dependencies to identify unresolved imports.
2. Remove any unresolved imports in your infrastructure layer (e.g., repository or messaging adapter classes).
3. Use your IDE's auto-import or quick fixes to resolve classes from the new `io.jexxa.adapters.*` packages, then optimize imports.

---

## Step 3: Update Test Dependencies

Use `jexxa-starter-test` for a starter-based test setup.

### Maven

```xml
<dependency>
    <groupId>io.jexxa</groupId>
    <artifactId>jexxa-starter-test</artifactId>
    <version>10.0.0-RC3</version>
    <scope>test</scope>
</dependency>
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    testImplementation 'io.jexxa:jexxa-starter-test:10.0.0-RC3'
}
```

If you prefer to declare `jexxa-test` directly, its group ID remains unchanged: `io.jexxa.jexxatest:jexxa-test`. Use a compatible version and keep the dependency in test scope.

---

## Step 4: Review Configuration

Existing `io.jexxa.*` configuration properties remain unchanged for the migrated REST, JDBC, and JMS integrations. Moving the adapters to new Java packages does not require renaming these properties.

Keep your existing properties files and verify any adapter-specific configuration used by your application.

---

## Step 5: Validate the Migration

1. Perform a clean build (`mvn clean install` or `./gradlew clean build`).
2. Run your unit, integration, and UI tests, including any tests configured separately from the standard build.
3. Run the application in a representative test environment and verify its external integrations.

### Migration validation

The migration from Jexxa 9.2.0 to 10.0.0-RC3 has been successfully validated with a Java 25 application using REST with a web UI, JDBC with PostgreSQL, and JMS with ActiveMQ Artemis.

Only Maven dependencies and Java imports needed to be updated. No changes to business logic or configuration properties were required. All unit, integration, and UI tests passed, and the application ran successfully in a production-like test environment.

---

## Summary of Changes

- **Removed transitive JDBC/JMS dependencies** from `jexxa-core` and discontinued `jexxa-web`.
- **Introduced `jexxa-bom`** for centralized version management of independently versioned Jexxa and JexxaAdapters modules.
- **Added scenario starters** (`jexxa-starter-business`, `jexxa-starter-streaming`) and `jexxa-starter-test` for simplified dependency management.
- **Modularized adapters** into standalone artifacts (`adapter-jdbc`, `adapter-jms`, `adapter-rest`, `adapter-s3`, `adapter-esp`, `adapter-micrometer`, etc.).