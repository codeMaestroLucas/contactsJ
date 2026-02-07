# Instructions Summary for Java Scraper Generation

This document summarizes all guidelines and formatting rules for the automatic creation of web-scraper classes in
Java, based on the provided instructions.

## 1. Main Objective

The main task is to generate Java classes (`ByPage` or `ByNewPage`) to extract lawyer information from law firm websites.
The provided input will be the **Firm Name**, **URL**, **HTML snippets**, and the **base class type** to be used.

## 2. Response Structure and Sequence

The response must strictly follow this order:
NOTE: Before generating the code, you must sort the firms alphabetically and then generate the code.

1.  **`touch` Command**: At the beginning of the response, provide a
`touch` command in a single line to create all necessary `.java` files in the test directory.
   * **Example**:
       ```bash
       touch src/main/java/org/example/src/sites/to_test/Firm1.java src/main/java/org/example/src/sites/to_test/Firm2.java
       ```

2. **Class Generation**: For each requested firm, the structure must be:
   * A title with the **Firm Name**.
   * A code block containing the complete Java class.

3. **Line for Builders**: At the end of the entire response, provide the instantiation line for the new classes.
  * This line must contain **only** the firms from the current iteration.
  * Must be inside a `txt` type code block.
  * Instantiations must be grouped by base class (`ByPage` or `ByNewPage`).
  * Firms must be sorted alphabetically.
  * Include the **continent** of each firm as a comment.
  * **Example**:
   ```text
   // ByPage - Europe
   new FirmB(), new FirmD(),

   // ByNewPage - Asia
   new FirmA(), new FirmC(),
   ```

## 3. Continent Architecture

The system uses a **central continent configuration** that controls:
1. Which firms are built (builders)
2. Which countries are avoided in validation

### 3.1 Central Configuration File

**Location**: `src/main/resources/baseFiles/json/continentsConfig.json`

```json
{
  "Africa":              { "enabled": true },
  "Asia":                { "enabled": true },
  "Europe":              { "enabled": true },
  "North America":       { "enabled": false },
  "Central America":     { "enabled": false },
  "South America":       { "enabled": false },
  "Oceania":             { "enabled": true }
}
```

**Logic**:
- `enabled: true` → Continent firms are built, countries are NOT avoided
- `enabled: false` → Continent firms are NOT built, countries ARE avoided

### 3.2 Firm Builders

Firms are organized in two separate builders by type:

| File | Description |
|------|-------------|
| `ByPageFirmsBuilder.java` | Firms that use the `ByPage` base class |
| `ByNewPageFirmsBuilder.java` | Firms that use the `ByNewPage` base class |

**Location**: `src/main/java/org/example/src/utils/myInterface/`

**Internal builder structure**:
```java
// Arrays separated by continent
private static final Site[] AFRICA = { ... };
private static final Site[] ASIA = { ... };
private static final Site[] EUROPE = { ... };
private static final Site[] NORTH_AMERICA = { ... };
private static final Site[] CENTRAL_AMERICA = { ... };
private static final Site[] SOUTH_AMERICA = { ... };
private static final Site[] OCEANIA = { ... };
private static final Site[] MUNDIAL = { ... };  // Always included (global firms)

// Getters by continent
public static Site[] getAfrica() { return AFRICA; }
// ... other getters

// build() method that respects continentsConfig.json
public static Site[] build() { ... }
```

### 3.3 Available Continents

| Continent | Code Identifier |
|-----------|-----------------|
| Africa | `AFRICA` |
| Asia | `ASIA` |
| Europe | `EUROPE` |
| North America | `NORTH_AMERICA` |
| Central America | `CENTRAL_AMERICA` |
| South America | `SOUTH_AMERICA` |
| Oceania | `OCEANIA` |
| Mundial (Global) | `MUNDIAL` |

**Note**: Firms in `MUNDIAL` are always included regardless of continent configuration.

### 3.4 Where to Add New Firms

When creating a new firm, add it to the correct builder (`ByPageFirmsBuilder.java` or `ByNewPageFirmsBuilder.java`) within the appropriate continent array.

**Example** - Adding firm `NewFirm` (ByNewPage, Europe):
```java
// In ByNewPageFirmsBuilder.java
private static final Site[] EUROPE = {
    new ExistingFirm1(), new ExistingFirm2(),
    new NewFirm(),  // New firm added here
};
```

## 4. Code Generation and Formatting Rules

* **Package**: All generated classes must use the package:
    ```java
    package org.example.src.sites.to_test;
    ```
* **Parameter Names**: In data extraction methods (e.g., `getName`, `getRole`), the `WebElement` parameter must be
named as `lawyer`.
    * **Example**: `private String getName(WebElement lawyer)`

* **`super()` Indentation**: The arguments inside the `super()` constructor call must have one less level
of indentation.
    * **Example**:
        ```java
        public MyFirm() {
            super(
            "Firm Name",
            "https://firm.com",
            1
            );
        }
        ```
* **Default Phone Value**: For single-country firms, if the phone is not found, the default value in the return `Map`
must be the string `"xxxxxx"`. For multinational firms, the previous logic (usually an empty string `""`
or the found value) must be maintained.
    * **Example**: `"phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]`

* **Email Construction**: When instructed, the `getSocials` function must be customized to build the email from
the lawyer's name, following the specified pattern (e.g., `(firstLetterOfName)(surname)@domain.com`).
  * Therefore, collect the name first - outside the function - and insert it as a parameter of the `getSocials` function.
  * ADDITIONALLY, use it's necessary to use the function `name = TreatLawyerParams.treatName(name);` to treat the name
  to properly treat the name before using it to create the email

## 5. General Principles

* **Precision**: The user is a developer and expects precise and functional code.
* **Never Assume**: If the provided HTML is ambiguous or insufficient, you must request more information before
proceeding. Do not deduce or guess the logic.
* **Error Correction**: To correct previously sent code,
use the format: `--- OLD <code> ---` followed by `--- NEW <code> ---`.
* **No code comments needed**: most of what is implemented I created myself, so I don't need you to
explain the code that I created.

## 6. Important Files Summary

| File | Purpose |
|------|---------|
| `continentsConfig.json` | Central configuration for enabled/disabled continents |
| `ByPageFirmsBuilder.java` | Builder for ByPage firms organized by continent |
| `ByNewPageFirmsBuilder.java` | Builder for ByNewPage firms organized by continent |
| `CompletedFirms.java` | Firm construction and statistics visualization |
| `ContinentConfig.java` | Utility to read continent configuration |
| `Validations.java` | Country validations (uses ContinentConfig to avoid countries from disabled continents) |
| `countriesToAvoidTemporary.json` | List of countries by continent (avoided when continent is disabled) |
| `countriesToAvoidPermanent.json` | List of countries always avoided (regardless of configuration) |
