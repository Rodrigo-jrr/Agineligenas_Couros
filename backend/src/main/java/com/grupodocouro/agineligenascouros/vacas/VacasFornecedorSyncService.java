package com.grupodocouro.agineligenascouros.vacas;

import com.grupodocouro.agineligenascouros.fornecedor.Fornecedor;
import com.grupodocouro.agineligenascouros.fornecedor.FornecedorRepository;
import com.grupodocouro.agineligenascouros.fornecedor.TipoFornecedor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sincroniza {@code Fornecedor} a partir do catalogo de racas da API das
 * Vacas (Etapa 4). Cada raca vira ou atualiza um {@code Fornecedor} com
 * {@code tipo=VACAS} e {@code referenciaExternaId} = id da raca.
 *
 * <p>Deliberadamente SEM {@code @Transactional} no metodo principal: cada
 * upsert individual roda na sua propria transacao (a que o Spring Data JPA
 * já abre por chamada de {@code save()}/{@code findBy...}), pra uma falha
 * numa raca especifica nunca contaminar o EntityManager/transacao das
 * outras - ver requisito de robustez da Etapa 4.
 */
@Service
public class VacasFornecedorSyncService {

    private static final Logger log = LoggerFactory.getLogger(VacasFornecedorSyncService.class);

    private final VacasApiClient vacasApiClient;
    private final FornecedorRepository fornecedorRepository;

    public VacasFornecedorSyncService(VacasApiClient vacasApiClient, FornecedorRepository fornecedorRepository) {
        this.vacasApiClient = vacasApiClient;
        this.fornecedorRepository = fornecedorRepository;
    }

    public VacasSyncResultado sincronizarFornecedores() {
        List<RacaVacasDTO> racas;
        try {
            racas = vacasApiClient.listarRacas();
        } catch (Exception ex) {
            log.error("Falha ao buscar lista de racas da API das Vacas - sincronizacao abortada.", ex);
            return new VacasSyncResultado(0, 0, 0, 0);
        }

        int criados = 0;
        int atualizados = 0;
        int falhas = 0;

        for (RacaVacasDTO raca : racas) {
            try {
                if (upsert(raca)) {
                    criados++;
                } else {
                    atualizados++;
                }
            } catch (Exception ex) {
                falhas++;
                log.warn("Falha ao sincronizar raca id={} da API das Vacas - pulando, sincronizacao continua.",
                    raca.id(), ex);
            }
        }

        VacasSyncResultado resultado = new VacasSyncResultado(criados, atualizados, falhas, racas.size());
        log.info("Sincronizacao Vacas->Fornecedor concluida: {}", resultado);
        return resultado;
    }

    /** @return true se criou um Fornecedor novo, false se atualizou um existente. */
    private boolean upsert(RacaVacasDTO raca) {
        String referenciaExternaId = String.valueOf(raca.id());

        return fornecedorRepository.findByTipoAndReferenciaExternaId(TipoFornecedor.VACAS, referenciaExternaId)
            .map(existente -> {
                existente.setNome(raca.nome());
                fornecedorRepository.save(existente);
                return false;
            })
            .orElseGet(() -> {
                Fornecedor novo = new Fornecedor();
                novo.setNome(raca.nome());
                novo.setTipo(TipoFornecedor.VACAS);
                novo.setReferenciaExternaId(referenciaExternaId);
                fornecedorRepository.save(novo);
                return true;
            });
    }
}
