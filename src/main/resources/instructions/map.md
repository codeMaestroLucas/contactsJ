# Contexto de Geração de Maps (Java)
Este documento resume as diretrizes para a criação de mapas em Java que associam cidades (escritórios) aos seus respectivos países, baseando-se em extrações de elementos UI (HTML ou Imagens).

1. Objetivo Principal
   Gerar implementações de `Map.of` ou `Map.ofEntries` em Java para automatizar o mapeamento geográfico de escritórios de advocacia ou empresas globais.

2. Regras Técnicas (Java)
   Método de Inicialização:
   1. Usar Map.of(...) para até 10 entradas. 
   2. Usar Map.ofEntries(entry(key, value), ...) para mais de 10 entradas.
   
   Estrutura de Chaves:
   1. As chaves devem ser os nomes das cidades como aparecem no elemento visual (ex: data-title, data-param ou o texto entre as tags <span>/<option>). 
   2. As chaves devem ser em lowercase, SEMPRE 
   3. Imutabilidade: Os mapas devem ser declarados como public static final Map<String, String>.
3. Fluxo de Trabalho
   Extração: Identificar o nome da cidade no HTML (atributo value, data-value ou texto visível).
   Mapeamento: Associar a cidade ao país correspondente (ver seção de Convenções abaixo).
   Formatação: Entregar o código pronto para ser inserido na classe, com comentários explicativos fora do bloco de código.
4. Convenções de Nomenclatura (PAÍSES)
   O nome dos países deve ser enviado em inglês. 

# Padrão de alguns nomes que deve ser seguidas para os valores das chaves:
   - United States, EUA → USA
   - UK, United Kingdom → England 
   - Netherlands → the Netherlands
   - Philippines → the Philippines 
   - United Arab Emirates→ the UAE
   - South Korea → Korea (South) 
   - British Virgin Islands → the British Virgin Islands 
   - Czech Republic → the Czech Republic
   - Dominican Republic → the Dominican Republic