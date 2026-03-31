#!/usr/bin/env python3
import json
import re

def get_continent(entry):
    url = entry.get('link', '') or ''
    if isinstance(url, list):
        url = url[0] if url else ''

    links = entry.get('links', [])
    if links:
        url = links[0]

    name = entry.get('name', '').lower()
    note = (entry.get('Note', '') or '') + ' ' + (entry.get('Notes', '') or '')
    note_lower = note.lower()
    all_text = (url + ' ' + name + ' ' + note).lower()

    # ---- explicit country/region keywords in notes ----
    if 'trinidad' in note_lower:
        return 'Central America'
    if 'botswana' in note_lower or 'namibia' in note_lower:
        return 'Africa'
    if 'kyrgyzstan' in note_lower:
        return 'Asia'
    if 'brazil' in note_lower or 'brasil' in note_lower:
        return 'South America'
    if 'argentina' in note_lower:
        return 'South America'
    if 'chile' in note_lower or 'chilean' in note_lower:
        return 'South America'
    if 'peru' in note_lower or 'perú' in note_lower or 'peruvian' in note_lower:
        return 'South America'
    if 'venezuela' in note_lower or 'venezuelan' in note_lower:
        return 'South America'
    if 'bolivia' in note_lower or 'bolivian' in note_lower:
        return 'South America'
    if 'colombia' in note_lower or 'colombian' in note_lower:
        return 'South America'
    if 'ecuador' in note_lower or 'ecuadorian' in note_lower:
        return 'South America'
    if 'uruguay' in note_lower:
        return 'South America'
    if 'paraguay' in note_lower:
        return 'South America'

    # ---- South America TLDs ----
    sa_tld_patterns = [
        r'\.com\.br', r'\.adv\.br', r'\.nom\.br', r'\.br/',
        r'\.com\.ar', r'\.ar/',
        r'\.com\.bo', r'\.bo/',
        r'\.com\.uy', r'\.co\.uy', r'\.uy/',
        r'\.com\.ve', r'\.ve/',
        r'\.com\.py', r'\.py/',
        r'\.com\.pe', r'\.pe/',
        r'\.com\.ec', r'\.ec/',
        r'\.cl/', r'\.cl$',
    ]
    for pat in sa_tld_patterns:
        if re.search(pat, url):
            return 'South America'

    # ---- Africa TLDs ----
    africa_tld_patterns = [
        r'\.co\.za', r'\.za/',
        r'\.com\.ng', r'\.ng/',
        r'\.co\.ke', r'\.ke/',
        r'\.co\.ug', r'\.ug/',
        r'\.co\.zm', r'\.zm/',
        r'\.co\.zw', r'\.zw/',
        r'\.co\.bw', r'\.bw/',
        r'\.org\.sz', r'\.sz/',
        r'\.co\.mz', r'\.mz/',
        r'\.com\.mu', r'\.mu/',
        r'\.co\.tz', r'\.tz/',
        r'\.co\.bi', r'\.bi/',
        r'\.ci/',
        r'\.cm/',
        r'\.eg/',
        r'\.ma/',
        r'\.tn/',
        r'\.ao/',
    ]
    for pat in africa_tld_patterns:
        if re.search(pat, url):
            return 'Africa'

    # ---- Asia TLDs ----
    asia_tld_patterns = [
        r'\.com\.cn', r'\.cn/',
        r'\.hk/', r'\.com\.hk',
        r'\.com\.mo', r'\.mo/',
        r'\.com\.tw', r'\.tw/',
        r'\.co\.jp', r'\.com\.jp', r'\.jp/',
        r'\.co\.kr', r'\.kr/',
        r'\.com\.sg', r'\.sg/',
        r'\.com\.my', r'\.my/',
        r'\.com\.ph', r'\.ph/',
        r'\.co\.id', r'\.id/',
        r'\.co\.th', r'\.th/',
        r'\.com\.vn', r'\.vn/',
        r'\.com\.kh', r'\.kh/',
        r'\.co\.in', r'\.com\.in', r'\.in/',
        r'\.lk/',
        r'\.pk/',
        r'\.kz/',
        r'\.kg/',
        r'\.mn/',
        r'\.com\.ae', r'\.ae/',
        r'\.sa/',
        r'\.qa/',
        r'\.kw/',
        r'\.bh/',
        r'\.om/',
        r'\.jo/',
        r'\.lb/',
        r'\.il/', r'\.com\.il',
        r'\.av\.tr', r'\.com\.tr', r'\.tr/',
    ]
    for pat in asia_tld_patterns:
        if re.search(pat, url):
            return 'Asia'

    # ---- Europe TLDs ----
    europe_tld_patterns = [
        r'\.co\.uk', r'\.uk/',
        r'\.de/', r'\.de$',
        r'\.fr/', r'\.fr$',
        r'\.es/', r'\.es$',
        r'\.it/', r'\.it$',
        r'\.com\.pt', r'\.pt/', r'\.pt$',
        r'\.nl/', r'\.nl$',
        r'\.be/', r'\.be$',
        r'\.ch/', r'\.ch$',
        r'\.at/', r'\.at$',
        r'\.pl/', r'\.pl$',
        r'\.cz/', r'\.cz$',
        r'\.sk/', r'\.sk$',
        r'\.hu/', r'\.hu$',
        r'\.ro/', r'\.ro$',
        r'\.bg/', r'\.bg$',
        r'\.gr/', r'\.gr$',
        r'\.hr/', r'\.hr$',
        r'\.rs/', r'\.rs$',
        r'\.si/', r'\.si$',
        r'\.lt/', r'\.lt$',
        r'\.lv/', r'\.lv$',
        r'\.ee/', r'\.ee$',
        r'\.fi/', r'\.fi$',
        r'\.se/', r'\.se$',
        r'\.dk/', r'\.dk$',
        r'\.no/', r'\.no$',
        r'\.is/', r'\.is$',
        r'\.ie/', r'\.ie$',
        r'\.lu/', r'\.lu$',
        r'\.cy/', r'\.cy$',
        r'\.mk/', r'\.mk$',
        r'\.me/', r'\.me$',
        r'\.ba/', r'\.ba$',
        r'\.al/', r'\.al$',
        r'\.md/', r'\.md$',
        r'\.im/', r'\.im$',
        r'\.je/', r'\.je$',
        r'\.gg/', r'\.gg$',
        r'\.mt/', r'\.mt$',
    ]
    for pat in europe_tld_patterns:
        if re.search(pat, url):
            return 'Europe'

    # ---- Oceania TLDs ----
    oceania_tld_patterns = [
        r'\.com\.au', r'\.net\.au', r'\.au/',
        r'\.co\.nz', r'\.nz/',
    ]
    for pat in oceania_tld_patterns:
        if re.search(pat, url):
            return 'Oceania'

    # ---- North America TLDs ----
    north_america_tld_patterns = [
        r'\.ca/', r'\.ca$', r'\.com\.ca',
    ]
    for pat in north_america_tld_patterns:
        if re.search(pat, url):
            return 'North America'

    # ---- Central America TLDs ----
    central_america_tld_patterns = [
        r'\.com\.mx', r'\.mx/',
        r'\.com\.pa', r'\.pa/',
        r'\.com\.do', r'\.do/',
        r'\.co\.cr', r'\.cr/',
        r'\.com\.gt', r'\.gt/',
        r'\.com\.hn', r'\.hn/',
        r'\.com\.ni', r'\.ni/',
        r'\.com\.sv', r'\.sv/',
        r'\.bz/',
        r'\.ht/',
        r'\.jm/',
        r'\.tt/',
        r'\.com\.pr', r'\.pr/',
    ]
    for pat in central_america_tld_patterns:
        if re.search(pat, url):
            return 'Central America'

    # ---- Keyword-based disambiguation for .com domains ----
    # South America keywords
    sa_keywords = [
        'advogados', 'advocacia', 'advogado', 'advs',
        'abogados', 'abogado', 'abogada',
        'brasil', 'brazil', 'brasileiro',
        'argentina', 'buenos aires',
        'bogota', 'bogotá', 'colombia',
        'lima', 'peru', 'perú',
        'santiago', 'chile',
        'caracas', 'venezuela',
        'la paz', 'bolivia',
        'quito', 'ecuador',
        'montevideo', 'uruguay',
        'asuncion', 'asunción', 'paraguay',
        'robalinolaw',   # Ecuador
        'garrido',       # Latin America
        'araquereyna',   # Venezuela
        'mitrani',       # Argentina
        'chediak',       # Brazil
        'brunswick',     # Brazil
        'osterling',     # Peru
        'aprilabogados', # Chile
        'lavinabogados', # Chile
        'palacios',      # Colombia - check context
        'guerreroolivos', # Chile
        'ferradanehme',  # Chile
        'palmachile',
        'hdlegal',
        'criales',       # Bolivia
        'ldcm',          # Brazil
        'fbl',
        'baqsn',         # Bolivia
        'baraona',       # Chile
    ]
    for kw in sa_keywords:
        if kw in all_text:
            return 'South America'

    # URL-path based South America hints
    if re.search(r'/equipo|/equipe|/socios|/socias|/nosotros', url):
        # Could be Spanish/Portuguese - likely SA or Spain
        # Further disambiguate
        if any(x in url for x in ['chile', 'peru', 'argentina', 'colombia', 'venezuela', 'bolivia', 'ecuador', 'uruguay', 'paraguay', 'brazil', 'brasil']):
            return 'South America'

    # Known specific domain to continent mappings (manually curated)
    known_domains = {
        # South America
        'almeidalaw.com.br': 'South America',
        'estudiodelion.com.pe': 'South America',
        'gumucioabogados.com.bo': 'South America',
        'souzaokawa.com': 'South America',
        'silveiro.com.br': 'South America',
        'spsadvogados.com': 'South America',  # Portuguese "equipa"
        'romeuamaral.com.br': 'South America',
        'rolimgoulart.com': 'South America',
        'robalinolaw.com': 'South America',
        'riedfabres.cl': 'South America',
        'rayesfagundes.com.br': 'South America',
        'pugaortiz.cl': 'South America',
        'pstbn.com.py': 'South America',
        'palmalaw.cl': 'South America',
        'palacioslleras.com': 'South America',
        'osterlinglaw.com': 'South America',
        'novotnyadvogados.com.br': 'South America',
        'mirandaamado.com.pe': 'South America',
        'molinarios.cl': 'South America',
        'mitrani.com': 'South America',
        'lcgadvogados.com.br': 'South America',
        'lecabogados.com.ve': 'South America',
        'lavinabogados.cl': 'South America',
        'labbeabogados.legal': 'South America',
        'hdlegal.cl': 'South America',
        'hopeduggansilva.com.ar': 'South America',
        'guerreroolivos.cl': 'South America',
        'grossbrown.com.py': 'South America',
        'garrido.com': 'South America',
        'ferradanehme.cl': 'South America',
        'dscasahierro.pe': 'South America',
        'diblasi.com.br': 'South America',
        'diasdesouza.com.br': 'South America',
        'duartegarcia.com.br': 'South America',
        'dempaire.com.ve': 'South America',
        'ctpadvogados.com.br': 'South America',
        'crialesurcullo.com': 'South America',
        'cblm.com.br': 'South America',
        'chediak.com.br': 'South America',
        'cassagne.com.ar': 'South America',
        'bronsysalas.com.ar': 'South America',
        'bragard.com.uy': 'South America',
        'ayresribeiro.com.br': 'South America',
        'araquereyna.com': 'South America',
        'antequera.legal': 'South America',
        'abe.com.br': 'South America',
        # Asia
        'tttandpartners.com': 'Asia',
        'thecapitallaw.com': 'Asia',
        'weerawongcp.com': 'Asia',
        'eldanlaw.com': 'Asia',
        'cclawchambers.com': 'Asia',
        'revlawllc.com': 'Asia',
        'rclc.com.sg': 'Asia',
        'ascendantlegal.com': 'Asia',
        'amicalaw.com': 'Asia',
        'kmdlee.com': 'Asia',
        'hlplawyers.com': 'Asia',
        'mohanadass.com': 'Asia',
        'tommythomas.net': 'Asia',
        'oneasia.legal': 'Asia',
        'lshorizon.com': 'Asia',
        'sudathpereraassociates.com': 'Asia',
        'sandalawyers.com': 'Asia',
        'ctlstrategies.com': 'Asia',
        'wkll.com': 'Asia',
    }

    # Try matching known domains
    for domain, continent in known_domains.items():
        if domain in url:
            return continent

    return 'uncategorized'


def main():
    input_path = '/Users/lucassamuellemosrajao/dev_projects/java/src/main/resources/todos/byNewPage.json'

    with open(input_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # Filter out separator objects and empty entries
    entries = []
    for item in data:
        if 'sep' in item:
            continue  # skip old separators
        entries.append(item)

    # Classify each entry
    continent_order = [
        'Africa',
        'Asia',
        'Europe',
        'North America',
        'Central America',
        'South America',
        'Oceania',
        'uncategorized',
    ]

    groups = {c: [] for c in continent_order}
    unclassified_empty = []

    for entry in entries:
        name = entry.get('name', '').strip()
        link = entry.get('link', '').strip()
        if not name and not link:
            unclassified_empty.append(entry)
            continue
        continent = get_continent(entry)
        groups[continent].append(entry)

    # Build output
    output = []

    # Add empty/nameless entries first (uncategorized-like)
    for e in unclassified_empty:
        output.append(e)

    for continent in continent_order:
        group = groups[continent]
        if not group:
            continue
        output.append({"sep": f"=== {continent} ==="})
        output.extend(group)

    with open(input_path, 'w', encoding='utf-8') as f:
        json.dump(output, f, ensure_ascii=False, indent=2)

    print("Done! Summary:")
    for continent in continent_order:
        print(f"  {continent}: {len(groups[continent])} entries")
    print(f"  Empty/nameless: {len(unclassified_empty)}")
    print(f"  Total entries (excluding separators): {sum(len(g) for g in groups.values()) + len(unclassified_empty)}")


if __name__ == '__main__':
    main()
