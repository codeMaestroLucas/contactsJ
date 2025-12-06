#!/usr/bin/env python3
"""
Script to extract country information from Java firm files and organize them by continent.
Reads all firm Java files, extracts countries, maps to continents, and generates organized output.
"""

import re
from pathlib import Path
from collections import defaultdict
from typing import Set, Dict, List, Tuple

# ============================================================================
# COUNTRY TO CONTINENT MAPPING
# ============================================================================
COUNTRY_TO_CONTINENT = {
    # Africa
    "South Africa": "Africa",
    "Nigeria": "Africa",
    "Ghana": "Africa",
    "Kenya": "Africa",
    "Egypt": "Africa",
    "Morocco": "Africa",
    "Tanzania": "Africa",
    "Uganda": "Africa",
    "Zimbabwe": "Africa",
    "Mauritius": "Africa",
    "Namibia": "Africa",
    "Botswana": "Africa",
    "Seychelles": "Africa",

    # Asia
    "China": "Asia",
    "Japan": "Asia",
    "South Korea": "Asia",
    "Korea": "Asia",
    "India": "Asia",
    "Singapore": "Asia",
    "Malaysia": "Asia",
    "Thailand": "Asia",
    "Vietnam": "Asia",
    "Indonesia": "Asia",
    "Philippines": "Asia",
    "Taiwan": "Asia",
    "Hong Kong": "Asia",
    "Pakistan": "Asia",
    "Bangladesh": "Asia",
    "Sri Lanka": "Asia",
    "UAE": "Asia",
    "Saudi Arabia": "Asia",
    "Israel": "Asia",
    "Turkey": "Asia",
    "Lebanon": "Asia",
    "Jordan": "Asia",
    "Qatar": "Asia",
    "Bahrain": "Asia",
    "Kuwait": "Asia",
    "Oman": "Asia",

    # Europe
    "United Kingdom": "Europe",
    "UK": "Europe",
    "England": "Europe",
    "Scotland": "Europe",
    "Wales": "Europe",
    "Germany": "Europe",
    "France": "Europe",
    "Italy": "Europe",
    "Spain": "Europe",
    "Netherlands": "Europe",
    "the Netherlands": "Europe",
    "Belgium": "Europe",
    "Switzerland": "Europe",
    "Austria": "Europe",
    "Sweden": "Europe",
    "Norway": "Europe",
    "Denmark": "Europe",
    "Finland": "Europe",
    "Poland": "Europe",
    "Czech Republic": "Europe",
    "Czechia": "Europe",
    "Hungary": "Europe",
    "Romania": "Europe",
    "Bulgaria": "Europe",
    "Greece": "Europe",
    "Portugal": "Europe",
    "Ireland": "Europe",
    "Iceland": "Europe",
    "Luxembourg": "Europe",
    "Croatia": "Europe",
    "Serbia": "Europe",
    "Slovenia": "Europe",
    "Slovakia": "Europe",
    "Estonia": "Europe",
    "Latvia": "Europe",
    "Lithuania": "Europe",
    "Malta": "Europe",
    "Cyprus": "Europe",
    "Albania": "Europe",
    "North Macedonia": "Europe",
    "Bosnia": "Europe",
    "Montenegro": "Europe",
    "Kosovo": "Europe",
    "Russia": "Europe",
    "Ukraine": "Europe",
    "Belarus": "Europe",
    "Moldova": "Europe",
    "Liechtenstein": "Europe",
    "Monaco": "Europe",
    "Andorra": "Europe",
    "San Marino": "Europe",
    "Guernsey": "Europe",
    "Jersey": "Europe",
    "Isle of Man": "Europe",

    # North America
    "USA": "North America",
    "United States": "North America",
    "US": "North America",
    "Canada": "North America",
    "Mexico": "North America",

    # South America
    "Brazil": "South America",
    "Argentina": "South America",
    "Chile": "South America",
    "Colombia": "South America",
    "Peru": "South America",
    "Venezuela": "South America",
    "Ecuador": "South America",
    "Bolivia": "South America",
    "Paraguay": "South America",
    "Uruguay": "South America",
    "Guyana": "South America",
    "Suriname": "South America",

    # Central America & Caribbean
    "Costa Rica": "Central America",
    "Panama": "Central America",
    "Guatemala": "Central America",
    "Honduras": "Central America",
    "El Salvador": "Central America",
    "Nicaragua": "Central America",
    "Belize": "Central America",
    "Jamaica": "Central America",
    "Trinidad and Tobago": "Central America",
    "Bahamas": "Central America",
    "Barbados": "Central America",
    "Cayman Islands": "Central America",
    "the Cayman Islands": "Central America",
    "British Virgin Islands": "Central America",
    "the British Virgin Islands": "Central America",
    "Bermuda": "Central America",
    "Puerto Rico": "Central America",
    "Dominican Republic": "Central America",
    "Cuba": "Central America",
    "Haiti": "Central America",
    "Antigua": "Central America",
    "Grenada": "Central America",
    "Saint Lucia": "Central America",

    # Oceania
    "Australia": "Oceania",
    "New Zealand": "Oceania",
    "Fiji": "Oceania",
    "Papua New Guinea": "Oceania",
    "Samoa": "Oceania",
}


# ============================================================================
# EXTRACTION FUNCTIONS
# ============================================================================

def extract_countries_from_file(file_path: Path) -> Tuple[str, Set[str]]:
    """
    Extract all countries mentioned in a firm's Java file.
    Returns: (class_name, set_of_countries)
    """
    try:
        content = file_path.read_text(encoding='utf-8')
    except Exception as e:
        print(f"Error reading {file_path}: {e}")
        return file_path.stem, set()

    countries = set()

    # Pattern 1: Hardcoded country in getLawyer() like: "country", "Nigeria"
    # This is the most common pattern
    pattern1 = r'"country"\s*,\s*"([^"]+)"'
    matches = re.findall(pattern1, content)
    for match in matches:
        # Exclude dynamic calls like "this.getCountry(...)"
        if not any(x in match for x in ['this.', 'get', '(', ')']):
            countries.add(match.strip())

    # Pattern 2: OFFICE_TO_COUNTRY mapping - entry("office", "Country")
    pattern2 = r'entry\s*\(\s*"[^"]*"\s*,\s*"([^"]+)"\s*\)'
    matches = re.findall(pattern2, content)
    for match in matches:
        countries.add(match.strip())

    # Pattern 3: getCountry method with default country parameter
    # Example: getCountryBasedInOffice(..., ..., "Canada")
    pattern3 = r'getCountryBasedInOffice[^)]*,\s*"([^"]+)"\s*\)'
    matches = re.findall(pattern3, content)
    for match in matches:
        countries.add(match.strip())

    # Pattern 4: Direct return in getCountry method
    # Example: return "Brazil";
    pattern4 = r'private\s+String\s+getCountry[^}]*?return\s+"([^"]+)"\s*;'
    matches = re.findall(pattern4, content, re.DOTALL)
    for match in matches:
        if not any(x in match for x in ['this.', 'get', 'Not Found', '-----']):
            countries.add(match.strip())

    # Pattern 5: Hardcoded in Map.of within getCountry
    # Used when country is extracted from office dynamically
    pattern5 = r'Map\.of\s*\([^)]*"([^"]+)"\s*,\s*"([^"]+)"[^)]*\)'
    matches = re.findall(pattern5, content)
    for match_tuple in matches:
        # match_tuple is (key, value) - we want the values that look like countries
        for val in match_tuple:
            if val in COUNTRY_TO_CONTINENT:
                countries.add(val.strip())

    # Get class name
    class_pattern = r'public\s+class\s+(\w+)\s+extends'
    class_match = re.search(class_pattern, content)
    class_name = class_match.group(1) if class_match else file_path.stem

    # Clean up countries - remove invalid entries
    valid_countries = set()
    for country in countries:
        # Skip entries that are clearly not countries
        if country and len(country) > 1 and country not in ['Not Found', '-----', 'null']:
            valid_countries.add(country)

    return class_name, valid_countries


def map_countries_to_continents(countries: Set[str]) -> Set[str]:
    """Map a set of countries to their continents."""
    continents = set()

    for country in countries:
        continent = COUNTRY_TO_CONTINENT.get(country)
        if continent:
            continents.add(continent)
        else:
            # Country not in mapping - report it
            if country:
                print(f"  ⚠️  Unknown country: '{country}'")

    return continents


def categorize_firm(countries: Set[str], continents: Set[str]) -> str:
    """
    Determine the category for a firm based on its countries and continents.
    Returns: continent name or "Mundial" or "Unknown"
    """
    if len(continents) == 0:
        return "Unknown"
    elif len(continents) == 1:
        return list(continents)[0]
    else:
        # Multiple continents = Mundial
        return "Mundial"


# ============================================================================
# MAIN PROCESSING
# ============================================================================

def process_directory(directory_path: str) -> Dict[str, Dict]:
    """
    Process all Java files in a directory.
    Returns: {firm_name: {countries, continents, category, class_name}}
    """
    dir_path = Path(directory_path)

    if not dir_path.exists():
        print(f"❌ Directory not found: {directory_path}")
        return {}

    firm_data = {}
    java_files = list(dir_path.glob("*.java"))

    # Remove template file
    java_files = [f for f in java_files if f.stem != "_Template"]

    print(f"\n📁 Processing {len(java_files)} files from {dir_path.name}...\n")

    for java_file in sorted(java_files):
        class_name, countries = extract_countries_from_file(java_file)
        continents = map_countries_to_continents(countries)
        category = categorize_firm(countries, continents)

        firm_data[class_name] = {
            'countries': countries,
            'continents': continents,
            'category': category,
            'class_name': class_name
        }

        # Print summary for each firm
        if countries:
            countries_str = ", ".join(sorted(countries))
            print(f"✓ {class_name:40s} → {category:20s} ({countries_str})")
        else:
            print(f"⚠ {class_name:40s} → No countries found")

    return firm_data


def group_by_continent(firm_data: Dict[str, Dict]) -> Dict[str, List[str]]:
    """Group firms by their continent category."""
    grouped = defaultdict(list)

    for firm_name, data in firm_data.items():
        grouped[data['category']].append(firm_name)

    # Sort firms within each category
    for category in grouped:
        grouped[category].sort()

    return dict(grouped)


def generate_organized_java_file(bypage_data: Dict[str, Dict], bynewpage_data: Dict[str, Dict], output_path: str):
    """Generate the organized _CompletedFirmsData.java file."""

    # Define continent order
    continent_order = [
        "Africa",
        "Asia",
        "Europe",
        "North America",
        "Central America",
        "South America",
        "Oceania",
        "Mundial",
        "Unknown"
    ]

    # Start building the file
    lines = []
    lines.append("package org.example.src.utils.myInterface;")
    lines.append("")
    lines.append("import lombok.Getter;")
    lines.append("import org.example.src.entities.BaseSites.Site;")
    lines.append("import org.example.src.sites.byNewPage.*;")
    lines.append("import org.example.src.sites.byPage.*;")
    lines.append("")
    lines.append("@Getter")
    lines.append("public class _CompletedFirmsData {")
    lines.append("")
    lines.append("    public final static Site[] byPage = {")
    lines.append("            /* Firms to avoid")
    lines.append("            new AddleshawGoddardLLP(), new Andersen(), new ArnoldAndPorter(), new Ashurst(), new CliffordChance(),")
    lines.append("            new CovingtonAndBurlingLLP(), new CrowellAndMoring(), new DavisPolkAndWardwell(), new DebevoiseAndPlimpton(), new DechertLLP(),")
    lines.append("            new GorrissenFederspiel(), new GreenbergTraurig(), new HerbertSmithFreehillsKramer(), new JonesDay(), new KromannReumert(),")
    lines.append("            new LathamAndWatkins(), new Milbank(), new MorganLewis(), new NautaDutilh(), new ProskauerRose(),")
    lines.append("            new RopesAndGray(), new Skadden(), new StephensonHarwood(), new Stibbe(), new TaylorWessing(),")
    lines.append("            new WhiteAndCase(), new Willkie(),")
    lines.append("            */")

    # Group byPage firms
    grouped = group_by_continent(bypage_data)

    first_continent = True
    for continent in continent_order:
        if continent in grouped and grouped[continent]:
            if not first_continent:
                lines.append("")
            lines.append(f"// {continent}")

            firm_line = "            "
            for i, firm in enumerate(grouped[continent]):
                firm_line += f"new {firm}(), "
                # Break line every 5 firms for readability
                if (i + 1) % 5 == 0 and i < len(grouped[continent]) - 1:
                    lines.append(firm_line.rstrip())
                    firm_line = "            "

            # Add remaining firms
            if firm_line.strip():
                lines.append(firm_line.rstrip(", ") + ",")

            first_continent = False

    # Remove trailing comma from last line
    if lines[-1].endswith(","):
        lines[-1] = lines[-1].rstrip(",")

    lines.append("    };")
    lines.append("")
    lines.append("    public final static Site[] byNewPage = {")
    lines.append("            /* Firms to avoid")
    lines.append("            new ALGoodbody(), new ArthurCox(), new Dentons(), new MishconKaras(), new OsborneClarke(),")
    lines.append("            */")

    # Group byNewPage firms
    grouped = group_by_continent(bynewpage_data)

    first_continent = True
    for continent in continent_order:
        if continent in grouped and grouped[continent]:
            if not first_continent:
                lines.append("")
            lines.append(f"// {continent}")

            firm_line = "            "
            for i, firm in enumerate(grouped[continent]):
                firm_line += f"new {firm}(), "
                # Break line every 5 firms for readability
                if (i + 1) % 5 == 0 and i < len(grouped[continent]) - 1:
                    lines.append(firm_line.rstrip())
                    firm_line = "            "

            # Add remaining firms
            if firm_line.strip():
                lines.append(firm_line.rstrip(", ") + ",")

            first_continent = False

    # Remove trailing comma from last line
    if lines[-1].endswith(","):
        lines[-1] = lines[-1].rstrip(",")

    lines.append("    };")
    lines.append("")
    lines.append("    public final static Site[] byFilter = {};")
    lines.append("")
    lines.append("    public final static Site[] byClick = {};")
    lines.append("}")

    # Write to file
    output = "\n".join(lines)
    Path(output_path).write_text(output, encoding='utf-8')

    print(f"\n✅ Organized file written to: {output_path}")


def print_summary(bypage_data: Dict[str, Dict], bynewpage_data: Dict[str, Dict]):
    """Print summary statistics."""
    print("\n" + "="*80)
    print("SUMMARY")
    print("="*80)

    print(f"\n📊 byPage Statistics:")
    print(f"   Total firms: {len(bypage_data)}")
    grouped = group_by_continent(bypage_data)
    for continent in sorted(grouped.keys()):
        print(f"   {continent:20s}: {len(grouped[continent])} firms")

    print(f"\n📊 byNewPage Statistics:")
    print(f"   Total firms: {len(bynewpage_data)}")
    grouped = group_by_continent(bynewpage_data)
    for continent in sorted(grouped.keys()):
        print(f"   {continent:20s}: {len(grouped[continent])} firms")


# ============================================================================
# MAIN EXECUTION
# ============================================================================

if __name__ == "__main__":
    BASE_PATH = "/Users/lucassamuellemosrajao/dev_projects/java/src/main/java/org/example/src/sites"

    print("="*80)
    print("FIRM ORGANIZATION SCRIPT")
    print("="*80)

    # Process byPage directory
    bypage_path = f"{BASE_PATH}/byPage"
    bypage_data = process_directory(bypage_path)

    # Process byNewPage directory
    bynewpage_path = f"{BASE_PATH}/byNewPage"
    bynewpage_data = process_directory(bynewpage_path)

    # Generate organized file
    output_path = "/Users/lucassamuellemosrajao/dev_projects/java/src/main/java/org/example/src/utils/myInterface/_CompletedFirmsData.java"
    generate_organized_java_file(bypage_data, bynewpage_data, output_path)

    # Print summary
    print_summary(bypage_data, bynewpage_data)

    print("\n✅ Done!")