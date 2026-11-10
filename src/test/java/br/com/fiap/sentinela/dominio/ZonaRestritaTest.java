package br.com.fiap.sentinela.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZonaRestritaTest {

    private static final double LIMITE_CASA_DO_REATOR = 2.5;

    @Test
    void deveCriarZonaNaoNula() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", LIMITE_CASA_DO_REATOR);
        assertNotNull(casaDoReator);
    }

    @Test
    void deveMudarDeNormalParaCriticoAoRegistrarLeituraAlta() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", LIMITE_CASA_DO_REATOR);
        casaDoReator.registrarLeituraRadiacao(0.2);
        assertEquals(ZonaRestrita.RISCO_NORMAL, casaDoReator.classificarRisco());

        casaDoReator.registrarLeituraRadiacao(4.7);

        assertEquals(4.7, casaDoReator.getNivelRadiacaoAtual());
        assertEquals(ZonaRestrita.RISCO_CRITICO, casaDoReator.classificarRisco());
    }

    @Test
    void deveClassificarComoAtencaoQuandoProximoDoLimite() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", LIMITE_CASA_DO_REATOR);
        casaDoReator.registrarLeituraRadiacao(1.5);
        assertEquals(ZonaRestrita.RISCO_ATENCAO, casaDoReator.classificarRisco());
    }

    @Test
    void deveRejeitarLeituraNegativaEManterUltimoValorValido() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", LIMITE_CASA_DO_REATOR);
        casaDoReator.registrarLeituraRadiacao(0.2);

        casaDoReator.registrarLeituraRadiacao(-5);

        assertEquals(0.2, casaDoReator.getNivelRadiacaoAtual());
        assertEquals(ZonaRestrita.RISCO_NORMAL, casaDoReator.classificarRisco());
    }

    @Test
    void deveRejeitarLeituraAbsurdaComoFalhaDeSensor() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", LIMITE_CASA_DO_REATOR);
        casaDoReator.registrarLeituraRadiacao(0.2);

        casaDoReator.registrarLeituraRadiacao(2_000_000);

        assertEquals(0.2, casaDoReator.getNivelRadiacaoAtual());
    }

    @Test
    void deveRejeitarZonaComIdentificadorVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new ZonaRestrita("", "Casa do Reator", LIMITE_CASA_DO_REATOR));
    }
}
