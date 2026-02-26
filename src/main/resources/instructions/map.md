# Java Map Generation Context (Law Firms & Global Offices)

This document outlines the standard guidelines for creating Java Maps that associate cities (offices) with their respective countries, based on UI element extractions (HTML or Images).

Just generate the `OFFICE_TO_COUNTRY` variable with the values of the Keys and Values.

1. Primary Objective
   To generate Java Map.of or Map.ofEntries implementations to automate the geographical mapping of law firms or global corporate offices.

2. Technical Guidelines (Java)
  ## Initialization Methods
   
   - Up to 10 entries: Use Map.of(key, value, ...).

   - More than 10 entries: Use Map.ofEntries(entry(key, value), ...).
   
   ## Key & Value Structure

   - Key Source: Keys must be the city names as they appear in the visual element (e.g., data-title, data-param, or text within <span>/<option> tags).
   
   - Key Formatting: Keys must ALWAYS be in lowercase.

   - Immutability: Maps must be declared as public static final Map<String, String>.

3. Workflow
   Extraction: Identify the city name in the HTML (look for value, data-value, or visible text).

Mapping: Associate the city with the correct country (refer to the Country Conventions section below).

Formatting: Provide code ready for class insertion, with explanatory comments placed outside the code block.

4. Country Naming Conventions (Values)
   Use the name convention based in the file `countryNameConventions.md`. All the names must be in english

# Example
- Input: HTML or photos
```html
<ul.countries>
   <li.country>afghanistan</li.country>
   <li.country>albania</li.country>
   <li.country>angola</li.country>
</ul.countries>
```

- Output: java code
```java
 public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
         entry("afghanistan", "Afghanistan"),
         entry("albania", "Albania"),
         entry("angola", "Angola")
 }
```