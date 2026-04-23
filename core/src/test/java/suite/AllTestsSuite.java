package suite;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Suíte mestre — executa todos os testes unitários do projeto core em uma única rodada.
 *
 * Cobertura:
 *   • TreatLawyerParams    — normalização de campos (email, telefone, nome, função…)
 *   • Lawyer               — builder com tratamento automático de dados
 *   • LawyerExceptions     — mapeamento de categoria (enum Category)
 *   • ValidationExceptions — factory methods e mensagens
 *   • ErrorLogger          — filtragem, registro e flush de erros
 *   • FirmsExhausted       — cache in-memory de firmas esgotadas
 *   • EmailOfMonth         — cache in-memory de e-mails por firma/mês
 *
 * Para executar apenas esta suíte:
 *   mvn test -pl core -Dtest=AllTestsSuite
 *
 * Para executar todos os testes:
 *   mvn test -pl core
 */
@Suite
@SelectPackages({"utils", "entities", "exceptions"})
public class AllTestsSuite {
}
