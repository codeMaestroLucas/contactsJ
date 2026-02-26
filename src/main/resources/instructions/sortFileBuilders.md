# Data Sorting and Structural Guidelines

Maintain the existing structure categorized by Continents and the specific arrays byPage and byNewPage. Follow the sorting and formatting rules below:

1. Alphabetical Sorting

Within Arrays: All firms must be sorted alphabetically.

Per Category: Sort firms within each continent for both the byPage array and the byNewPage array independently.

Example: Organize African firms in byPage alphabetically, then do the same for Asian firms in byPage, and so on.

Commented Code: Firms that are currently commented out must also be included in this alphabetical sorting.

2. Layout and Formatting

Firms per Line: To maintain readability, precisely 5 firms must be placed per line.

Consistency: Ensure that the continental divisions remain intact and are not merged during the reorganization.

# Example
- Before
    ```java
    private static final Site[] AFRICA = {
        new B1(), new A1(), new C1()
    };

    private static final Site[] ASIA = {
        new C2(), new A2(), new B2() 
    };
    ```
- After
    ```java
  private static final Site[] AFRICA = {
        new A1(), new B1(), new C1()
  };

  private static final Site[] ASIA = {
        new A2(), new B2(), new C2()
  };
    ```
