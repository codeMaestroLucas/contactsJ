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
        return 'Americas'
    if 'botswana' in note_lower or 'namibia' in note_lower:
        return 'Africa'
    if 'kyrgyzstan' in note_lower:
        return 'Asia'
    if 'brazil' in note_lower or 'brasil' in note_lower:
        return 'Americas'
    if 'argentina' in note_lower:
        return 'Americas'
    if 'chile' in note_lower or 'chilean' in note_lower:
        return 'Americas'
    if 'peru' in note_lower or 'perú' in note_lower or 'peruvian' in note_lower:
        return 'Americas'
    if 'venezuela' in note_lower or 'venezuelan' in note_lower:
        return 'Americas'
    if 'bolivia' in note_lower or 'bolivian' in note_lower:
        return 'Americas'
    if 'colombia' in note_lower or 'colombian' in note_lower:
        return 'Americas'
    if 'ecuador' in note_lower or 'ecuadorian' in note_lower:
        return 'Americas'
    if 'uruguay' in note_lower:
        return 'Americas'
    if 'paraguay' in note_lower:
        return 'Americas'

    # ---- Americas TLDs ----
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
            return 'Americas'

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

    # ---- Americas (North) TLDs ----
    north_america_tld_patterns = [
        r'\.ca/', r'\.ca$', r'\.com\.ca',
    ]
    for pat in north_america_tld_patterns:
        if re.search(pat, url):
            return 'Americas'

    # ---- Americas (Central) TLDs ----
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
            return 'Americas'

    # ---- Keyword-based disambiguation for .com domains ----
    # Americas keywords
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
            return 'Americas'

    # URL-path based Americas hints
    if re.search(r'/equipo|/equipe|/socios|/socias|/nosotros', url):
        # Could be Spanish/Portuguese - likely SA or Spain
        # Further disambiguate
        if any(x in url for x in ['chile', 'peru', 'argentina', 'colombia', 'venezuela', 'bolivia', 'ecuador', 'uruguay', 'paraguay', 'brazil', 'brasil']):
            return 'Americas'

    # Known specific domain to continent mappings (manually curated)
    known_domains = {
        # Americas
        'almeidalaw.com.br': 'Americas',
        'estudiodelion.com.pe': 'Americas',
        'gumucioabogados.com.bo': 'Americas',
        'souzaokawa.com': 'Americas',
        'silveiro.com.br': 'Americas',
        'spsadvogados.com': 'Americas',  # Portuguese "equipa"
        'romeuamaral.com.br': 'Americas',
        'rolimgoulart.com': 'Americas',
        'robalinolaw.com': 'Americas',
        'riedfabres.cl': 'Americas',
        'rayesfagundes.com.br': 'Americas',
        'pugaortiz.cl': 'Americas',
        'pstbn.com.py': 'Americas',
        'palmalaw.cl': 'Americas',
        'palacioslleras.com': 'Americas',
        'osterlinglaw.com': 'Americas',
        'novotnyadvogados.com.br': 'Americas',
        'mirandaamado.com.pe': 'Americas',
        'molinarios.cl': 'Americas',
        'mitrani.com': 'Americas',
        'lcgadvogados.com.br': 'Americas',
        'lecabogados.com.ve': 'Americas',
        'lavinabogados.cl': 'Americas',
        'labbeabogados.legal': 'Americas',
        'hdlegal.cl': 'Americas',
        'hopeduggansilva.com.ar': 'Americas',
        'guerreroolivos.cl': 'Americas',
        'grossbrown.com.py': 'Americas',
        'garrido.com': 'Americas',
        'ferradanehme.cl': 'Americas',
        'dscasahierro.pe': 'Americas',
        'diblasi.com.br': 'Americas',
        'diasdesouza.com.br': 'Americas',
        'duartegarcia.com.br': 'Americas',
        'dempaire.com.ve': 'Americas',
        'ctpadvogados.com.br': 'Americas',
        'crialesurcullo.com': 'Americas',
        'cblm.com.br': 'Americas',
        'chediak.com.br': 'Americas',
        'cassagne.com.ar': 'Americas',
        'bronsysalas.com.ar': 'Americas',
        'bragard.com.uy': 'Americas',
        'ayresribeiro.com.br': 'Americas',
        'araquereyna.com': 'Americas',
        'antequera.legal': 'Americas',
        'abe.com.br': 'Americas',
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
        'Americas',
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
