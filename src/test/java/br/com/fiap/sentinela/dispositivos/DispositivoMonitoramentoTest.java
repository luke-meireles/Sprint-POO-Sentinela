package br.com.fiap.sentinela.dispositivos;

import br.com.fiap.sentinela.dominio.ZonaRestrita;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispositivoMonitoramentoTest {

    private final ZonaRestrita perimetroExterno = new ZonaRestrita("ZNR-03", "Perímetro Externo", 0.5);

    @Test
    void todasAsFilhasDevemHerdarGetCodigoIdentificador() {
        DispositivoMonitoramento sensorRadiacao = new SensorRadiacao("RAD-01", "Portão", perimetroExterno);
        DispositivoMonitoramento sensorPerimetral = new SensorPerimetral("PER-01", "Norte", perimetroExterno);
        DispositivoMonitoramento dronePatrulha = new DronePatrulha("DRN-01", "Perímetro", perimetroExterno);

        assertEquals("RAD-01", sensorRadiacao.getCodigoIdentificador());
        assertEquals("PER-01", sensorPerimetral.getCodigoIdentificador());
        assertEquals("DRN-01", dronePatrulha.getCodigoIdentificador());
    }

    @Test
    void droneDeveSerDispositivoETambemDespachavel() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro", perimetroExterno);

        assertTrue(dronePatrulha instanceof DispositivoMonitoramento);
        assertTrue(dronePatrulha instanceof Despachavel);
    }

    @Test
    void sensorDeRadiacaoDeveRepassarLeituraParaAZona() {
        SensorRadiacao sensorRadiacao = new SensorRadiacao("RAD-01", "Portão", perimetroExterno);
        sensorRadiacao.registrarLeitura(0.3);
        assertEquals(0.3, perimetroExterno.getNivelRadiacaoAtual());
    }

    @Test
    void droneComBateriaBaixaNaoDevePatrulhar() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro", perimetroExterno);
        dronePatrulha.getCameraEmbarcada().simularCena(ObjetoDetectado.PESSOA_NAO_IDENTIFICADA);
        dronePatrulha.atualizarBateria(10);

        ObjetoDetectado deteccao = dronePatrulha.realizarPatrulha();

        assertSame(ObjetoDetectado.NENHUM, deteccao);
        assertEquals(10, dronePatrulha.getBateriaPercentual());
    }
}
