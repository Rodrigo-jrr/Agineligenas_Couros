package com.grupodocouro.agineligenascouros.vacas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Dispara a sincronizacao Vacas-&gt;Fornecedor manualmente, sem scheduler nem
 * cron (desnecessario pro escopo deste projeto). Uso:
 *
 * <pre>
 * mvn spring-boot:run -Dspring-boot.run.arguments=--sync-vacas-fornecedores
 * # ou, com o jar empacotado:
 * java -jar target/agineligenascouros-backend-0.1.0.jar --sync-vacas-fornecedores
 * </pre>
 *
 * <p>Sem essa flag, nao faz nada - a aplicacao sobe normalmente.
 */
@Component
public class VacasSyncRunner implements ApplicationRunner {

    private static final String FLAG_SINCRONIZAR = "sync-vacas-fornecedores";

    private static final Logger log = LoggerFactory.getLogger(VacasSyncRunner.class);

    private final VacasFornecedorSyncService vacasFornecedorSyncService;

    public VacasSyncRunner(VacasFornecedorSyncService vacasFornecedorSyncService) {
        this.vacasFornecedorSyncService = vacasFornecedorSyncService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption(FLAG_SINCRONIZAR)) {
            return;
        }
        log.info("Argumento --{} recebido - iniciando sincronizacao Vacas->Fornecedor.", FLAG_SINCRONIZAR);
        vacasFornecedorSyncService.sincronizarFornecedores();
    }
}
