package com.grupodocouro.agineligenascouros.vacas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.grupodocouro.agineligenascouros.fornecedor.Fornecedor;
import com.grupodocouro.agineligenascouros.fornecedor.FornecedorRepository;
import com.grupodocouro.agineligenascouros.fornecedor.TipoFornecedor;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacasFornecedorSyncServiceTest {

    @Mock
    private VacasApiClient vacasApiClient;

    @Mock
    private FornecedorRepository fornecedorRepository;

    private VacasFornecedorSyncService service;

    @BeforeEach
    void setUp() {
        service = new VacasFornecedorSyncService(vacasApiClient, fornecedorRepository);
    }

    @Test
    void criaFornecedorNovoQuandoNaoExisteComEsseReferenciaExternaId() {
        given(vacasApiClient.listarRacas()).willReturn(List.of(new RacaVacasDTO(1L, "Angus", 5, "150.00")));
        given(fornecedorRepository.findByTipoAndReferenciaExternaId(TipoFornecedor.VACAS, "1"))
            .willReturn(Optional.empty());

        VacasSyncResultado resultado = service.sincronizarFornecedores();

        assertThat(resultado.criados()).isEqualTo(1);
        assertThat(resultado.atualizados()).isZero();
        assertThat(resultado.falhas()).isZero();

        ArgumentCaptor<Fornecedor> captor = ArgumentCaptor.forClass(Fornecedor.class);
        verify(fornecedorRepository).save(captor.capture());
        Fornecedor salvo = captor.getValue();
        assertThat(salvo.getNome()).isEqualTo("Angus");
        assertThat(salvo.getTipo()).isEqualTo(TipoFornecedor.VACAS);
        assertThat(salvo.getReferenciaExternaId()).isEqualTo("1");
    }

    @Test
    void atualizaNomeDeFornecedorExistenteSemCriarDuplicata() {
        Fornecedor existente = new Fornecedor();
        existente.setId(10L);
        existente.setNome("Angus (nome antigo)");
        existente.setTipo(TipoFornecedor.VACAS);
        existente.setReferenciaExternaId("1");

        given(vacasApiClient.listarRacas()).willReturn(List.of(new RacaVacasDTO(1L, "Angus", 5, "150.00")));
        given(fornecedorRepository.findByTipoAndReferenciaExternaId(TipoFornecedor.VACAS, "1"))
            .willReturn(Optional.of(existente));

        VacasSyncResultado resultado = service.sincronizarFornecedores();

        assertThat(resultado.criados()).isZero();
        assertThat(resultado.atualizados()).isEqualTo(1);
        verify(fornecedorRepository, times(1)).save(existente);
        assertThat(existente.getNome()).isEqualTo("Angus");
        assertThat(existente.getId()).isEqualTo(10L);
    }

    @Test
    void falhaEmUmaRacaNaoInterrompeSincronizacaoDasOutras() {
        RacaVacasDTO racaComFalha = new RacaVacasDTO(1L, "Angus", 5, "150.00");
        RacaVacasDTO racaOk = new RacaVacasDTO(2L, "Nelore", 3, "80.00");
        given(vacasApiClient.listarRacas()).willReturn(List.of(racaComFalha, racaOk));

        given(fornecedorRepository.findByTipoAndReferenciaExternaId(TipoFornecedor.VACAS, "1"))
            .willThrow(new RuntimeException("falha simulada de banco"));
        given(fornecedorRepository.findByTipoAndReferenciaExternaId(TipoFornecedor.VACAS, "2"))
            .willReturn(Optional.empty());

        VacasSyncResultado resultado = service.sincronizarFornecedores();

        assertThat(resultado.falhas()).isEqualTo(1);
        assertThat(resultado.criados()).isEqualTo(1);
        assertThat(resultado.totalRecebido()).isEqualTo(2);

        ArgumentCaptor<Fornecedor> captor = ArgumentCaptor.forClass(Fornecedor.class);
        verify(fornecedorRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getReferenciaExternaId()).isEqualTo("2");
    }

    @Test
    void falhaAoListarRacasAbortaSincronizacaoSemLancarExcecao() {
        given(vacasApiClient.listarRacas()).willThrow(new RuntimeException("API das Vacas fora do ar"));

        VacasSyncResultado resultado = service.sincronizarFornecedores();

        assertThat(resultado).isEqualTo(new VacasSyncResultado(0, 0, 0, 0));
        verify(fornecedorRepository, never()).save(any());
    }
}
