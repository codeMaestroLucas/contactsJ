#!/usr/bin/env python3
"""
Script para limpar os TODOs removendo firmas já criadas
"""
import os
import re
import json
from pathlib import Path
from difflib import SequenceMatcher

def extract_firm_name_from_java(file_path):
    """Extrai o nome da firma de um arquivo Java"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            # Procura por super( com o nome da firma
            match = re.search(r'super\s*\(\s*"([^"]+)"', content)
            if match:
                return match.group(1)
    except Exception as e:
        print(f"Erro ao ler {file_path}: {e}")
    return None

def get_all_created_firms(base_path):
    """Retorna todos os nomes de firmas já criadas"""
    firms = {}
    
    # Diretórios para verificar
    directories = [
        'byNewPage',
        'byPage',
        '_standingBy/otherIssues',
        '_standingBy/toAvoidForNow'
    ]
    
    for directory in directories:
        dir_path = os.path.join(base_path, directory)
        if os.path.exists(dir_path):
            for filename in os.listdir(dir_path):
                if filename.endswith('.java') and filename != '_Template.java':
                    file_path = os.path.join(dir_path, filename)
                    firm_name = extract_firm_name_from_java(file_path)
                    if firm_name:
                        firms[firm_name.lower()] = {
                            'original_name': firm_name,
                            'file': filename,
                            'directory': directory
                        }
    
    return firms

def normalize_name(name):
    """Normaliza o nome da firma para comparação"""
    if not name:
        return ""
    # Remove espaços extras, converte para minúsculas
    normalized = name.lower().strip()
    # Remove variações comuns
    normalized = normalized.replace('&', 'and')
    normalized = normalized.replace(',', '')
    normalized = re.sub(r'\s+', ' ', normalized)
    return normalized

def are_names_similar(name1, name2, threshold=0.85):
    """Verifica se dois nomes são similares"""
    norm1 = normalize_name(name1)
    norm2 = normalize_name(name2)
    
    if norm1 == norm2:
        return True
    
    # Verifica similaridade usando SequenceMatcher
    similarity = SequenceMatcher(None, norm1, norm2).ratio()
    return similarity >= threshold

def find_similar_names(firms_dict, threshold=0.85):
    """Encontra nomes similares entre as firmas"""
    similar_groups = []
    checked = set()
    
    firm_list = list(firms_dict.values())
    
    for i, firm1 in enumerate(firm_list):
        if firm1['original_name'] in checked:
            continue
            
        group = [firm1]
        checked.add(firm1['original_name'])
        
        for j in range(i + 1, len(firm_list)):
            firm2 = firm_list[j]
            if firm2['original_name'] in checked:
                continue
                
            if are_names_similar(firm1['original_name'], firm2['original_name'], threshold):
                group.append(firm2)
                checked.add(firm2['original_name'])
        
        if len(group) > 1:
            similar_groups.append(group)
    
    return similar_groups

def clean_todos(todos_path, created_firms, output_path=None):
    """Remove firmas já criadas dos TODOs"""
    with open(todos_path, 'r', encoding='utf-8') as f:
        todos = json.load(f)
    
    original_count = len([t for t in todos if t.get('name')])
    removed = []
    kept = []
    
    for todo in todos:
        if not todo.get('name'):  # Mantém entradas vazias
            kept.append(todo)
            continue
        
        # Verifica se a firma já foi criada
        found = False
        todo_normalized = normalize_name(todo['name'])
        
        for firm_key, firm_data in created_firms.items():
            if are_names_similar(todo['name'], firm_data['original_name']):
                removed.append({
                    'todo_name': todo['name'],
                    'created_as': firm_data['original_name'],
                    'file': firm_data['file'],
                    'directory': firm_data['directory']
                })
                found = True
                break
        
        if not found:
            kept.append(todo)
    
    # Salva o arquivo limpo
    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(kept, f, indent=2, ensure_ascii=False)
    
    return {
        'original_count': original_count,
        'removed_count': len(removed),
        'kept_count': len([k for k in kept if k.get('name')]),
        'removed_items': removed
    }

def main():
    # Caminhos base
    base_sites_path = '/Users/lucassamuellemosrajao/dev_projects/java/src/main/java/org/example/src/sites'
    todos_base_path = '/Users/lucassamuellemosrajao/dev_projects/java/src/main/resources/todos'
    
    print("=" * 80)
    print("LIMPEZA DE TODOs - Remoção de firmas já criadas")
    print("=" * 80)
    print()
    
    # 1. Extrair todas as firmas criadas
    print("1. Extraindo firmas já criadas...")
    created_firms = get_all_created_firms(base_sites_path)
    print(f"   ✓ {len(created_firms)} firmas encontradas")
    print()
    
    # 2. Procurar por nomes similares
    print("2. Procurando por nomes similares/duplicados...")
    similar_groups = find_similar_names(created_firms, threshold=0.85)
    
    if similar_groups:
        print(f"   ⚠ {len(similar_groups)} grupos de nomes similares encontrados:")
        print()
        for i, group in enumerate(similar_groups, 1):
            print(f"   Grupo {i}:")
            for firm in group:
                print(f"      - {firm['original_name']}")
                print(f"        Arquivo: {firm['file']}")
                print(f"        Diretório: {firm['directory']}")
            print()
    else:
        print("   ✓ Nenhum nome similar encontrado")
        print()
    
    # 3. Limpar cada arquivo de TODO
    todo_files = {
        'byFilter.json': 'byFilter_cleaned.json',
        'byNewPage.json': 'byNewPage_cleaned.json',
        'byPage.json': 'byPage_cleaned.json',
        'uncategorized.json': 'uncategorized_cleaned.json'
    }
    
    print("3. Limpando arquivos de TODOs...")
    print()
    
    all_removed = []
    total_stats = {
        'original': 0,
        'removed': 0,
        'kept': 0
    }
    
    for todo_file, output_file in todo_files.items():
        todo_path = os.path.join(todos_base_path, todo_file)
        output_path = os.path.join(todos_base_path, output_file)
        
        if os.path.exists(todo_path):
            print(f"   Processando {todo_file}...")
            result = clean_todos(todo_path, created_firms, output_path)
            
            total_stats['original'] += result['original_count']
            total_stats['removed'] += result['removed_count']
            total_stats['kept'] += result['kept_count']
            
            all_removed.extend(result['removed_items'])
            
            print(f"      Original: {result['original_count']} firmas")
            print(f"      Removidas: {result['removed_count']} firmas")
            print(f"      Mantidas: {result['kept_count']} firmas")
            print()
    
    # 4. Resumo final
    print("=" * 80)
    print("RESUMO FINAL")
    print("=" * 80)
    print(f"Total de firmas nos TODOs originais: {total_stats['original']}")
    print(f"Total de firmas removidas (já criadas): {total_stats['removed']}")
    print(f"Total de firmas mantidas: {total_stats['kept']}")
    print()
    
    if all_removed:
        print("Firmas removidas (algumas):")
        for item in all_removed[:20]:  # Mostra apenas as primeiras 20
            print(f"   - {item['todo_name']} → criada como '{item['created_as']}' ({item['directory']}/{item['file']})")
        if len(all_removed) > 20:
            print(f"   ... e mais {len(all_removed) - 20} firmas")
    
    print()
    print("✓ Arquivos limpos salvos com sufixo '_cleaned'")
    print()
    
    # Salvar relatório detalhado
    report_path = os.path.join(todos_base_path, 'cleanup_report.json')
    report = {
        'summary': total_stats,
        'similar_groups': [
            [{'name': f['original_name'], 'file': f['file'], 'directory': f['directory']} 
             for f in group]
            for group in similar_groups
        ],
        'removed_firms': all_removed
    }
    
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, indent=2, ensure_ascii=False)
    
    print(f"✓ Relatório detalhado salvo em: cleanup_report.json")

if __name__ == "__main__":
    main()
