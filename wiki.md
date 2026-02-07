# Project Wiki: Selenium Web Scraper

This document describes the architecture and file structure of the Java and Selenium-based web scraping application. The project is designed to be modular and extensible for adding new law firm websites to scrape.

---

## 1. Project Overview

The primary goal of this application is to automate the process of collecting contact information for lawyers from various law firm websites. It uses **Selenium WebDriver** to control a Chrome browser, navigate through web pages, extract relevant data, and store it in an **Excel file**.

The architecture is built around a core framework that defines different scraping strategies, handles data validation, and manages data persistence.

---

## 2. Directory Structure

The project is organized into several key directories, each with a specific purpose.

```
C:.
|   .gitignore
|   pom.xml
|   README.md
|   wiki.md
|   instruct.md
|
+---data
|   |   monthFirms.txt
|   |
|   +---sites
|   |   +---byNewPage
|   |   |       <.txt files with emails registered per month for ByNewPage sites>
|   |   |
|   |   ---byPage
|   |           <.txt files with emails registered per month for ByPage sites>
|   |
|   ---_toAvoid
|       +---byNewPage
|       |       <Emails to be permanently avoided on ByNewPage sites>
|       |
|       ---byPage
|               <Emails to be permanently avoided on ByPage sites>
|
+---src
+---main
|   +---java
|   |   ---org
|   |       ---example
|   |           |   Main.java
|   |           |
|   |           +---exceptions
|   |           |       LawyerExceptions.java
|   |           |       ValidationExceptions.java
|   |           |
|   |           ---src
|   |               |   CONFIG.java
|   |               |
|   |               +---entities
|   |               |   |   Lawyer.java
|   |               |   |   MyDriver.java
|   |               |   |
|   |               |   +---BaseSites
|   |               |   |       ByNewPage.java
|   |               |   |       ByPage.java
|   |               |   |       Site.java
|   |               |   |       SiteUtils.java
|   |               |   |
|   |               |   ---excel
|   |               |           Contacts.java
|   |               |           ContactsAlreadyRegisteredSheet.java
|   |               |           Excel.java
|   |               |           Reports.java
|   |               |           Sheet.java
|   |               |
|   |               +---sites
|   |               |   +---byNewPage
|   |               |   |       <Site classes that extend ByNewPage>
|   |               |   |
|   |               |   ---byPage
|   |               |           <Site classes that extend ByPage>
|   |               |
|   |               ---utils
|   |                   |   ContinentConfig.java
|   |                   |   EmailOfMonth.java
|   |                   |   Extractor.java
|   |                   |   FirmsOMonth.java
|   |                   |   TimeCalculator.java
|   |                   |   Validations.java
|   |                   |
|   |                   ---myInterface
|   |                           ByNewPageFirmsBuilder.java
|   |                           ByPageFirmsBuilder.java
|   |                           CompletedFirms.java
|   |                           MyInterfaceUtls.java
|   |                           _CompletedFirmsData.java
|   |
|   ---resources
|       +---baseFiles
|       |   |   lastRowRegisteredInContacts.txt
|       |   |
|       |   +---excel
|       |   |   |   Contacts.xlsx
|       |   |   |   filteredCollectedContacts.xlsx
|       |   |   |   Reports.xlsx
|       |   |   |   Sheet.xlsx
|       |   |
|       |   ---json
|       |           continentsConfig.json
|       |           countriesToAvoidPermanent.json
|       |           countriesToAvoidTemporary.json
|       |           firmsToAvoid.json
|       |
|       ---todos
|               <JSON files for future planning>
|
---test
<... Unit tests ...>
```

### 2.1. Description of Main Packages and Files

-   `org.example`
    -   `Main.java`: The **application's entry point**. It orchestrates the main flow, deciding whether to fetch contacts from a pre-filtered Excel file or to start web scraping.

-   `src/entities`
    -   `Lawyer.java`: Data model class that represents a lawyer. It contains all the logic for **cleaning and formatting** the raw extracted data (name, email, phone, etc.).
    -   `MyDriver.java`: **Singleton** class that manages the Selenium `WebDriver` instance, ensuring that only one browser is used throughout the application. It includes helper methods for browser interactions.
    -   `BaseSites/`: Contains the abstract classes that define the **scraping strategies**.
        -   `Site.java`: The fundamental base class for all scrapers. It defines the common structure, such as lawyer registration and validations.
        -   `ByPage.java`: Strategy for sites where lawyer details are on listing pages, usually with pagination.
        -   `ByNewPage.java`: Strategy for sites that require navigating to an individual profile page to get all the details for a lawyer.
    -   `excel/`: Classes for interacting with `.xlsx` files.
        -   `Excel.java`: Base class for handling Excel files.
        -   `Sheet.java`: Manages the main output sheet (`Sheet.xlsx`) where validated lawyers are saved.
        -   `Contacts.java`: Accesses `Contacts.xlsx` to check if an email has already been registered in previous runs.
        -   `ContactsAlreadyRegisteredSheet.java`: Processes an Excel file (`filteredCollectedContacts.xlsx`) of pre-collected contacts, validating them and moving them to the main sheet.
        -   `Reports.java`: Manages a sheet for recording session reports (execution time, lawyers collected per site).

-   `src/sites`: Packages containing the concrete implementations of the scrapers, organized by strategy (`byPage`, `byNewPage`).

-   `src/utils`: Utility classes that provide supporting functionalities.
    -   `ContinentConfig.java`: **Central utility** for reading the continent configuration. Provides methods like `isContinentEnabled()`, `getEnabledContinents()`, and `getDisabledContinents()`.
    -   `Extractor.java`: A robust utility for extracting data from `WebElements` with exception handling, ensuring that the extraction of one field does not stop the entire process.
    -   `Validations.java`: Centralizes all validation rules before a lawyer is registered (countries to avoid, email duplicates, etc.). Uses `ContinentConfig` to determine which countries to avoid based on disabled continents.
    -   `EmailOfMonth.java` & `FirmsOMonth.java`: Manage text files to track emails and firms already processed in the current month to avoid repeated work.
    -   `myInterface/`: Classes that manage the application flow and the command-line interface (CLI).
        -   `ByPageFirmsBuilder.java`: Builder that contains all `ByPage` firms organized by continent. Respects `continentsConfig.json` to include only firms from enabled continents.
        -   `ByNewPageFirmsBuilder.java`: Builder that contains all `ByNewPage` firms organized by continent. Respects `continentsConfig.json` to include only firms from enabled continents.
        -   `_CompletedFirmsData.java`: A **facade** that delegates to the builders to get the list of firms.
        -   `CompletedFirms.java`: Builds the list of firms to be processed, shuffling them to vary the order of execution. Also provides visual statistics about firms and continents.

-   `src/CONFIG.java`: A file to centralize all **global settings**, such as file paths and collection limits.

-   `src/exceptions`: Custom exceptions for better error handling.
    -   `LawyerExceptions.java`: Used during data extraction to indicate which specific field failed (e.g., name, email).
    -   `ValidationExceptions.java`: Used by `Validations.java` to signal why a lawyer was rejected.

---

## 3. Continent Configuration System

The application uses a **central continent configuration** to control which firms are built and which countries are avoided during validation.

### 3.1. Configuration File

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

### 3.2. How It Works

| Continent State | Firms | Countries |
|-----------------|-------|-----------|
| `enabled: true` | **Built** and included in scraping | **NOT avoided** in validation |
| `enabled: false` | **NOT built**, excluded from scraping | **Avoided** in validation |

### 3.3. Affected Components

1. **Builders** (`ByPageFirmsBuilder.java`, `ByNewPageFirmsBuilder.java`):
   - Only include firms from enabled continents
   - `MUNDIAL` (global) firms are always included regardless of configuration

2. **Validations** (`Validations.java`):
   - Countries from **disabled** continents are avoided (via `countriesToAvoidTemporary.json`)
   - Countries in `countriesToAvoidPermanent.json` are **always** avoided

### 3.4. Firms Organization

Firms are organized by continent within each builder:

```java
// In ByPageFirmsBuilder.java or ByNewPageFirmsBuilder.java
private static final Site[] AFRICA = { ... };
private static final Site[] ASIA = { ... };
private static final Site[] EUROPE = { ... };
private static final Site[] NORTH_AMERICA = { ... };
private static final Site[] CENTRAL_AMERICA = { ... };
private static final Site[] SOUTH_AMERICA = { ... };
private static final Site[] OCEANIA = { ... };
private static final Site[] MUNDIAL = { ... };  // Always included
```

### 3.5. Viewing Statistics

Run `CompletedFirms.main()` to see a visual breakdown of:
- Firms per continent (enabled/disabled)
- Total active vs total firms
- Max lawyers available per category

---

## 4. Main Execution Flow

The application operates in a two-phase flow, orchestrated by the `Main.java` class.

1.  **Phase 1: Process Pre-Registered Contacts (Optional)**
    -   The `getRegisteredContacts()` method in `Main.java` is called (currently commented out).
    -   It uses `ContactsAlreadyRegisteredSheet.java` to read the `filteredCollectedContacts.xlsx` file.
    -   For each lawyer in the list, it checks if the email already exists in `Contacts.xlsx` and if the country is not on the exclusion list.
    -   Valid lawyers are moved to `Sheet.xlsx`, and the original row is removed from `filteredCollectedContacts.xlsx`.
    -   This process continues until the `LAWYERS_IN_FILTER` limit is reached or the list ends.

2.  **Phase 2: Web Scraping**
    -   The `searchLawyersInWeb()` method in `Main.java` starts the scraping process.
    -   **Firm Selection**: `CompletedFirms.constructFirms()` creates a list of all scrapers from **enabled continents** (via builders), excluding those already processed in the current month (checked via `FirmsOMonth.txt`), and shuffles the list.
    -   **Scraping Loop**: `Main` iterates over each `Site` in the list.
        -   An `ExecutorService` is used to run each site's scraper with a **timeout** (currently 1 minute). This prevents a problematic site from freezing the entire application.
        -   The corresponding `Site` class navigates to the page, locates the lawyers, and extracts the raw data.
    -   **Data Cleaning**: The extracted data is passed to the `Lawyer.builder()` constructor, which immediately applies the treatment methods (`treatName`, `treatEmail`, etc.).
    -   **Validation**: The `Site.registerValidLawyer()` method calls `Validations.makeValidations()` to check:
        -   If the email is empty.
        -   If the country is on the permanent exclusion list (`countriesToAvoidPermanent.json`).
        -   If the country is on the temporary exclusion list (`countriesToAvoidTemporary.json`) AND the continent is **disabled**.
        -   If the email is on the permanent exclusion list (`data/_toAvoid/`).
        -   If the firm has already been registered in the month (`monthFirms.txt`).
        -   If the email already exists in `Contacts.xlsx` or in the month's records (`data/sites/`).
        -   If the country has already been collected for the same firm in the current session (avoids collecting from multiple offices in the same country).
    -   **Data Persistence**: If the lawyer passes all validations:
        -   `Sheet.addLawyer()` adds them to `Sheet.xlsx`.
        -   `EmailOfMonth.registerEmailOfMonth()` registers the email in the site's monthly log file.
    -   **Completion**: The loop continues until the target number of lawyers (`CONFIG.LAWYERS_IN_SHEET`) is reached or all sites in the list have been processed.
    
---

## 5. How to Add a New Scraper

1.  **Analyze the Target Website**:
    -   Determine the required strategy. Is all the data on a results page (`ByPage`), or do you need to click on each profile to see the details (`ByNewPage`)?
    -   Inspect the HTML to find the CSS or XPath selectors for key elements: the lawyer's container, name, role, email, phone, etc.
    -   Identify which **continent** the firm belongs to.

2.  **Create the Java Class**:
    -   Create a new class in the appropriate package (`src/sites/byPage` or `src/sites/byNewPage`).
    -   Have the class extend `ByPage` or `ByNewPage`.
    -   Add the constructor, passing the site name, initial link, number of pages (if applicable), and the maximum number of lawyers to collect.

3.  **Implement the Abstract Methods**:
    -   `accessPage(int index)`: Contains the logic for accessing the lawyer listing page. This may include navigating to the URL, accepting cookies, or clicking "Load more" buttons.
    -   `getLawyersInPage()`: Returns a `List<WebElement>` where each element is the main container for a lawyer on the page. Use `siteUtl.filterLawyersInPage()` to pre-filter lawyers with valid roles.
    -   `openNewTab(WebElement lawyer)` (**For ByNewPage only**): Gets the lawyer's profile link and opens it in a new tab using `MyDriver.openNewTab()`.
    -   `getLawyer(WebElement lawyer)`: This is the core of the scraper.
        -   For `ByNewPage`, first call `openNewTab(lawyer)`.
        -   Use the `Extractor`'s methods (`extractLawyerText`, `extractLawyerAttribute`) to extract each data field (name, email, etc.). The `Extractor` handles exceptions, preventing a missing field from breaking the entire extraction.
        -   Return a `Map<String, String>` with the extracted data. The map keys (`name`, `email`, `role`, etc.) must match what the `registerValidLawyer` method expects.
        -   `ByNewPage` automatically closes the new tab in the main loop's `finally` block.

4.  **Register the New Scraper**:
    -   Go to the appropriate builder file (`ByPageFirmsBuilder.java` or `ByNewPageFirmsBuilder.java`).
    -   Add a new instance of your scraper class to the appropriate **continent array**.
    -   **Example**: Adding a European firm to `ByNewPageFirmsBuilder.java`:
        ```java
        private static final Site[] EUROPE = {
            new ExistingFirm1(), new ExistingFirm2(),
            new NewFirm(),  // Add here
        };
        ```

5.  **Test**:
    -   Run the `Main` class to ensure your new scraper works as expected, collects data correctly, and does not throw errors.

---

## 6. JSON Configuration Files

| File | Purpose |
|------|---------|
| `continentsConfig.json` | Central configuration for enabling/disabling continents |
| `countriesToAvoidPermanent.json` | Countries that are **always** avoided (regardless of continent config) |
| `countriesToAvoidTemporary.json` | Countries organized by continent (avoided only when continent is **disabled**) |
| `firmsToAvoid.json` | Specific firms to avoid |

---

## 7. Key Classes Summary

| Class | Purpose |
|-------|---------|
| `ContinentConfig.java` | Reads `continentsConfig.json` and provides utility methods |
| `ByPageFirmsBuilder.java` | Builds `ByPage` firms list respecting continent config |
| `ByNewPageFirmsBuilder.java` | Builds `ByNewPage` firms list respecting continent config |
| `_CompletedFirmsData.java` | Facade that delegates to builders |
| `CompletedFirms.java` | Constructs final firm list and shows statistics |
| `Validations.java` | Validates lawyers, uses `ContinentConfig` for country validation |
