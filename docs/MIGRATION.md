# Migration Guide
## Migration Guide: Upgrading from to Jexxa 9 to 10

Starting with **Jexxa 10.0.0**, infrastructure adapters have been extracted into the modular **JexxaAdapters** project.

Previously, `jexxa-core` (and `jexxa-web`) automatically pulled in **JDBC** and **JMS** dependencies. These transitive dependencies have been removed to keep the core minimal and allow independent versioning of infrastructure modules.

> **Good news:** Your domain model and application core remain completely unchanged. Only your build configuration and infrastructure imports need to be updated.

---

## Step 1: Update Your Build Dependencies

You have two options to migrate your dependencies:

### Option A: Use a Scenario Starter (Recommended)

If you previously relied on Jexxa's built-in JDBC and JMS support, switch to `jexxa-starter-business`. It bundles `jexxa-core` together with the JDBC and JMS adapters.

**Available Starters:**
- **`jexxa-starter-business`**: Jexxa Core + JDBC + JMS adapters (direct replacement for previous default setups)
- **`jexxa-starter-streaming`**: Jexxa Core + S3 + ESP/Kafka adapters

#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.jexxa</groupId>
        <artifactId>jexxa-starter-business</artifactId>
        <version>10.0.0-RC3</version>
    </dependency>
</dependencies>
```

#### Gradle (Groovy DSL)
```groovy
dependencies {
    implementation 'io.jexxa:jexxa-starter-business:10.0.0-RC3'
}
```

---

### Option B: Use the BOM and Select Individual Modules

If you only need specific adapters (e.g., only REST or JDBC without JMS), import `jexxa-bom` to manage compatible versions across both **Jexxa** and **JexxaAdapters**, and declare only the modules you need:

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
    implementation 'io.jexxa.adapters:adapter-jdbc'
    implementation 'io.jexxa.adapters:adapter-jms'
}
```

---

## Step 2: Update Package Imports

Because the adapters moved into `JexxaAdapters`, some adapter classes now reside in new packages:

1. Build your project after updating the dependencies.
2. Remove any unresolved imports in your infrastructure layer (e.g., repository or messaging adapter classes).
3. Use your IDE's auto-import (`Optimize Imports`) to re-import the corresponding classes from the new `io.jexxa.adapters.*` packages.

---

## Summary of Changes

- **Removed transitive JDBC/JMS dependencies** from `jexxa-core` / `jexxa-web`.
- **Introduced `jexxa-bom`** for centralized version management of Jexxa and JexxaAdapters (5.0.0+).
- **Added scenario starters** (`jexxa-starter-business`, `jexxa-starter-streaming`) for zero-effort setup.
- **Modularized adapters** into standalone artifacts (`adapter-jdbc`, `adapter-jms`, `adapter-rest`, `adapter-s3`, `adapter-esp`, `adapter-micrometer`, etc.).
