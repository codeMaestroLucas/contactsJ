# Resumo de Instruções para Geração de Scrapers em Java

Este documento resume todas as diretrizes e regras de formatação para a criação automática de classes de web-scraper em
Java, com base nas instruções fornecidas.

## 1. Objetivo Principal

A tarefa principal é gerar classes Java (`ByPage` ou `ByNewPage`) para extrair informações de advogados a partir de sites
de escritórios. O input fornecido será o **Nome da Firma**, **URL**, **trechos de HTML** e o **tipo de classe base** a ser usada.

## 2. Estrutura e Sequência da Resposta

A resposta deve seguir rigorosamente esta ordem:
OBS: Antes de gerar os códigos, vc deve ordenar as firmas alfabéticamente e então gerar o código.

1.  **Comando `touch`**: No início da resposta, fornecer um comando
`touch` em uma única linha para criar todos os arquivos `.java` necessários no diretório de teste.
   * **Exemplo**:
       ```bash
       touch src/main/java/org/example/src/sites/to_test/Firma1.java src/main/java/org/example/src/sites/to_test/Firma2.java
       ```

2. **Geração das Classes**: Para cada firma solicitada, a estrutura deve ser:
   * Um título com o **Nome da Firma**.
   * Um bloco de código contendo a classe Java completa.

3. **Linha para os Builders**: Ao final de toda a resposta, fornecer a linha de instanciação para as novas classes.
  * Esta linha deve conter **apenas** as firmas da iteração atual.
  * Deve estar dentro de um bloco de código do tipo `txt`.
  * As instanciações devem ser agrupadas por classe base (`ByPage` ou `ByNewPage`).
  * As firmas devem estar ordenadas alfabéticamente.
  * Incluir o **continente** de cada firma como comentário.
  * **Exemplo**:
   ```text
   // ByPage - Europe
   new FirmaB(), new FirmaD(),

   // ByNewPage - Asia
   new FirmaA(), new FirmaC(),
   ```

## 3. Arquitetura de Continentes

O sistema utiliza uma **configuração central de continentes** que controla:
1. Quais firmas são construídas (builders)
2. Quais países são evitados na validação

### 3.1 Arquivo de Configuração Central

**Localização**: `src/main/resources/baseFiles/json/continentsConfig.json`

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

**Lógica**:
- `enabled: true` → Firmas do continente são construídas, países NÃO são evitados
- `enabled: false` → Firmas do continente NÃO são construídas, países SÃO evitados

### 3.2 Builders de Firmas

As firmas são organizadas em dois builders separados por tipo:

| Arquivo | Descrição |
|---------|-----------|
| `ByPageFirmsBuilder.java` | Firmas que usam a classe base `ByPage` |
| `ByNewPageFirmsBuilder.java` | Firmas que usam a classe base `ByNewPage` |

**Localização**: `src/main/java/org/example/src/utils/myInterface/`

**Estrutura interna dos builders**:
```java
// Arrays separados por continente
private static final Site[] AFRICA = { ... };
private static final Site[] ASIA = { ... };
private static final Site[] EUROPE = { ... };
private static final Site[] NORTH_AMERICA = { ... };
private static final Site[] CENTRAL_AMERICA = { ... };
private static final Site[] SOUTH_AMERICA = { ... };
private static final Site[] OCEANIA = { ... };
private static final Site[] MUNDIAL = { ... };  // Sempre incluído (firmas globais)

// Getters por continente
public static Site[] getAfrica() { return AFRICA; }
// ... outros getters

// Método build() que respeita continentsConfig.json
public static Site[] build() { ... }
```

### 3.3 Continentes Disponíveis

| Continente | Identificador no código |
|------------|-------------------------|
| Africa | `AFRICA` |
| Asia | `ASIA` |
| Europe | `EUROPE` |
| North America | `NORTH_AMERICA` |
| Central America | `CENTRAL_AMERICA` |
| South America | `SOUTH_AMERICA` |
| Oceania | `OCEANIA` |
| Mundial (Global) | `MUNDIAL` |

**Nota**: Firmas em `MUNDIAL` são sempre incluídas independente da configuração de continentes.

### 3.4 Onde Adicionar Novas Firmas

Ao criar uma nova firma, adicione-a no builder correto (`ByPageFirmsBuilder.java` ou `ByNewPageFirmsBuilder.java`) dentro do array do continente apropriado.

**Exemplo** - Adicionar firma `NewFirm` (ByNewPage, Europa):
```java
// Em ByNewPageFirmsBuilder.java
private static final Site[] EUROPE = {
    new ExistingFirm1(), new ExistingFirm2(),
    new NewFirm(),  // Nova firma adicionada aqui
};
```

## 4. Regras de Geração e Formatação de Código

* **Package**: Todas as classes geradas devem usar o pacote:
    ```java
    package org.example.src.sites.to_test;
    ```
* **Nome dos Parâmetros**: Em métodos de extração de dados (ex: `getName`, `getRole`), o parâmetro `WebElement` deve ser
nomeado como `lawyer`.
    * **Exemplo**: `private String getName(WebElement lawyer)`

* **Indentação do `super()`**: Os argumentos dentro da chamada do construtor `super()` devem ter um nível a menos
de indentação.
    * **Exemplo**:
        ```java
        public MinhaFirma() {
            super(
            "Nome da Firma",
            "https://firma.com",
            1
            );
        }
        ```
* **Valor Padrão para Telefone**: Para firmas de um único país, se o telefone não for encontrado, o valor padrão no `Map`
de retorno deve ser a string `"xxxxxx"`. Para firmas multinacionais, a lógica anterior (geralmente uma string vazia `""`
ou o valor encontrado) deve ser mantida.
    * **Exemplo**: `"phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]`

* **Construção de Email**: Quando instruído, a função `getSocials` deve ser customizada para construir o email a partir
do nome do advogado, seguindo o padrão especificado (ex: `(primeiraLetraNome)(sobrenome)@dominio.com`).
  * Assim sendo, colete primeiro o nome - fora da função - e o insira como parâmetro da função `getSocials`.
  * Além disso, utilize a função `name = TreatLawyerParams.treatName(name);` para fazer o tratamento do nome

## 5. Princípios Gerais

* **Precisão**: O usuário é um desenvolvedor e espera um código preciso e funcional.
* **Nunca Assumir**: Se o HTML fornecido for ambíguo ou insuficiente, é necessário solicitar mais informações antes de
prosseguir. Não deduzir ou adivinhar a lógica.
* **Correção de Erros**: Para corrigir um código enviado anteriormente,
usar o formato: `--- ANTIGO <código> ---` seguido de `--- NOVO <código> ---`.
* **Não preciso de comentários no código**: boa parte do que está implementado eu criei, por isso, não preciso que vc me
explique o código que eu msm criei.

## 6. Resumo dos Arquivos Importantes

| Arquivo | Propósito |
|---------|-----------|
| `continentsConfig.json` | Configuração central de continentes habilitados/desabilitados |
| `ByPageFirmsBuilder.java` | Builder de firmas ByPage organizadas por continente |
| `ByNewPageFirmsBuilder.java` | Builder de firmas ByNewPage organizadas por continente |
| `CompletedFirms.java` | Construção das firmas e visualização de estatísticas |
| `ContinentConfig.java` | Utilitário para ler a configuração de continentes |
| `Validations.java` | Validações de países (usa ContinentConfig para evitar países de continentes desabilitados) |
| `countriesToAvoidTemporary.json` | Lista de países por continente (evitados quando continente está desabilitado) |
| `countriesToAvoidPermanent.json` | Lista de países sempre evitados (independente de configuração) |
