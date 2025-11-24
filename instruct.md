# Resumo de Instruções para Geração de Scrapers em Java

Este documento resume todas as diretrizes e regras de formatação para a criação automática de classes de web-scraper em
Java, com base nas instruções fornecidas.## 1. Objetivo Principal

## 1.

A tarefa principal é gerar classes Java (`ByPage` ou `ByNewPage`) para extrair informações de advogados a partir de sites
de escritórios. O input fornecido será o **Nome da Firma**, **URL**, **trechos de HTML** e o **tipo de classe base** a ser usada .

## 2. Estrutura e Sequência da Resposta

A resposta deve seguir rigorosamente esta ordem:
OBS: Antes de gerar os códigos, vc deve ordenar as firmas alfabéticamente e então gerar o código.

1.  **Comando `touch`**: No início da resposta, fornecer um comando
`touch` em uma única linha para criar todos os arquivos `.java` necessários no diretório de teste.
   * **Exemplo**:        ```bash
           touch src/main/java/org/example/src/sites/to_test/Firma1.java src/main/java/org/example/src/sites/to_test/Firma2.java
           ```
 

2. **Geração das Classes**: Para cada firma solicitada, a estrutura deve ser:
   * Um título com o **Nome da Firma**.
   * * Um bloco de código contendo a classe Java completa.
 

3. **Linha para `_CompletedFirmsData.java`**: Ao final de toda a resposta, fornecer a linha de instanciação para as novas classes.
  * Esta linha deve conter **apenas** as firmas da iteração atual.
  * Deve estar dentro de um bloco de código do tipo `txt`.
  * As instanciações devem ser agrupadas por classe base (`ByPage` ou `ByNewPage`), com um comentário e uma linha em
branco separando os grupos.
  * As firmas devem estar ordenadas alfabéticamente.
  * **Exemplo**:
   ```text
   // ByPage
   new FirmaB(), new FirmaD(),

   // ByNewPage
   new FirmaA(), new FirmaC(),
   ```

## 3. Regras de Geração e Formatação de Código*
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
            "[https://firma.com](https://firma.com)",
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

## 4. Princípios Gerais*
* **Precisão**: O usuário é um desenvolvedor e espera um código preciso e funcional.
* **Nunca Assumir**: Se o HTML fornecido for ambíguo ou insuficiente, é necessário solicitar mais informações antes de
prosseguir. Não deduzir ou adivinhar a lógica.
* **Correção de Erros**: Para corrigir um código enviado anteriormente,
usar o formato: `--- ANTIGO <código> ---` seguido de `--- NOVO <código> ---`.
* **Não preciso de comentários no código**: boa parte do que está implementado eu criei, por isso, não preciso que vc me 
explique o código que eu msm criei.