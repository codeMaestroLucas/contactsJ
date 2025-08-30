# Project Wiki: Selenium Web Scraper

This document outlines the architecture and file structure of the Java-based Selenium web scraping application.
The project is designed to be modular and extensible for adding new law firm websites to scrape.

---

## 1. Project Overview

The primary goal of this application is to automate the process of collecting contact information for lawyers from
various law firm websites. It uses **Selenium WebDriver** to control a Chrome browser, navigates through web pages,
extracts relevant data, and stores it in an **Excel file**.

The architecture is built around a core framework that defines different scraping strategies, handles data validation,
and manages data persistence.

---

## 2. Directory Structure

The project is organized into several key directories, each with a specific purpose.

```
C:.
├─── data/     # Stores persistent data like emails of scraped contacts.
├─── src/      # Contains all the application's source code and resources.
├─── images/   # For any image assets used in documentation.
```


### 2.1. src Directory

This is the most important directory, containing the Java source code and resource files.

- `src/main/java/org/example/src/` → Root package for all Java source code.
    - **entities/** → Core data models and base classes.
        - **BaseSites/**
            - `Site.java`: Foundational abstract class for all scrapers.
            - `ByPage.java`: Abstract class for sites where all data is on one page (with pagination).
            - `ByNewPage.java`: Abstract class for sites requiring navigation to a detail page for each lawyer.
        - **excel/**
            - `Excel.java`: Base class for handling `.xlsx` files.
            - `Sheet.java`: Manages the main output sheet.
            - `Contacts.java`: Handles existing contacts, checks for duplicates.
            - `Reports.java`: Manages a sheet for logging session reports.
        - `Lawyer.java`: Data model representing a single lawyer, with data cleaning and formatting logic.
        - `MyDriver.java`: Singleton to manage Selenium WebDriver.
    - **sites/**
        - **byNewPage/** → Classes extending `ByNewPage.java`.
        - **byPage/** → Classes extending `ByPage.java`.
        - **_standingBy/** → For scrapers in development or temporarily disabled.
    - **utils/** → Helper functions for the application.
    - **myInterface/** → Classes for managing CLI and application flow.
    - `_CompletedFirmsData.java`: Central registry of completed scraper instances.
    - `CompletedFirms.java`: Main entry point, orchestrates the scraping process.
    - `CONFIG.java`: Centralized configuration file.
    - `Validations.java`: Utility for validating lawyers before registration.

- `src/main/resources/`
    - **baseFiles/excel/** → Master Excel templates.
    - **baseFiles/json/** → JSON configuration files (e.g., `countriesToAvoid.json`).

### 2.2. data Directory

This directory is used for storing runtime-generated data to maintain state between sessions.

- **data/sites/** → `.txt` files for each scraper, logging collected emails per month.
- **data/_toAvoid/** → `.txt` files listing emails permanently skipped.

---

## 3. Core Concepts & Workflow

### 3.1. Scraping Strategies

The application uses two main strategies:

- **ByPage** → For sites listing all lawyers with full details directly on results pages (with pagination).
- **ByNewPage** → For sites requiring navigation to a profile page for each lawyer.

### 3.2. Application Flow

1. **Initialization** → Start via `CompletedFirms.java`.
2. **Firm Selection** → Builds a list of scrapers from `_CompletedFirmsData.java`, filtering firms already processed
(via `FirmsOfMonth.java`).
3. **Scraping Loop** → Iterates through firms one by one.
4. **Data Extraction** → Scraper class navigates and extracts lawyer data into a `Map`.
5. **Data Cleaning** → Data is passed to `Lawyer.java` for cleaning and formatting.
6. **Validation** → `Validations.makeValidations()` checks:
    - Duplicate emails in `Contacts.xlsx`.
    - Monthly duplicates (`data/sites/`).
    - Country exclusion list (`countriesToAvoid.json`).
    - Duplicate firm-country combinations in the same session.
7. **Data Persistence** → Valid data written to `Sheet.xlsx` and logged in `data/sites/`.
8. **Reporting** → Session summary added to `Reports.xlsx`.
9. **Completion** → Repeats until target number of lawyers (`CONFIG.TOTAL_LAWYERS_TO_GET`) is reached.

---

## 4. How to Add a New Scraper

1. **Analyze the Website** → Decide if it fits `ByPage` or `ByNewPage`.
2. **Create the Class** → Add a new Java class in `src/sites/byPage` or `src/sites/byNewPage`. Extend the correct base class.
3. **Implement Abstract Methods**:
    - `accessPage(int index)` → Navigation logic (cookies, load more).
    - `getLawyersInPage()` → Returns a `List<WebElement>` of lawyers. Use `siteUtl.filterLawyersInPage()` for pre-filtering.
    - `openNewTab(WebElement lawyer)` → (ByNewPage only) Open profile tab.
    - `getLawyer(WebElement lawyer)` → Extract lawyer data into a `Map<String, String>`. Use helper methods
   (`getName`, `getRole`, etc.).
4. **Register the Scraper** → Add the new instance to `_CompletedFirmsData.java`.
5. **Run and Test** → Execute the application to validate functionality.

---
