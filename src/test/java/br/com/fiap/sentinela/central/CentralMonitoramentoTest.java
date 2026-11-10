package br.com.fiap.sentinela.central;

import br.com.fiap.sentinela.dispositivos.DispositivoMonitoramento;
import br.com.fiap.sentinela.dispositivos.DronePatrulha;
import br.com.fiap.sentinela.dispositivos.ObjetoDetectado;
import br.com.fiap.sentinela.dispositivos.SensorPerimetral;
import br.com.fiap.sentinela.dispositivos.SensorRadiacao;
import br.com.fiap.sentinela.dominio.ZonaRestrita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CentralMonitoramentoTest {

    private SensorRadiacao sensorReator;
    private SensorPerimetral sensorCercaNorte;
    private DronePatrulha dronePatrulha;
    private CentralMonitoramento central;

    @BeforeEach
    void montarInstalacao() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 2.5);
        ZonaRestrita perimetroExterno = new ZonaRestrita("ZNR-03", "Perímetro Externo", 0.5);
        sensorReator = new SensorRadiacao("RAD-01", "Parede leste", casaDoReator);
        sensorCercaNorte = new SensorPerimetral("PER-01", "Norte", perimetroExterno);
        dronePatrulha = new DronePatrulha("DRN-01", "Perímetro", perimetroExterno);
        sensorCercaNorte.vincularApoioDeVerificacao(dronePatrulha);
        central = new CentralMonitoramento("Instalação de teste",
                new DispositivoMonitoramento[] {sensorReator, sensorCercaNorte, dronePatrulha});
    }

    @Test
    void rotinaNormalNaoDeveGerarAlertas() {
        sensorReator.registrarLeitura(0.18);
        dronePatrulha.realizarPatrulha();

        assertTrue(central.analisarLeituras().isEmpty());
    }

    @Test
    void picoDeRadiacaoDeveGerarAlertaCritico() {
        sensorReator.registrarLeitura(4.7);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertEquals(AlertaSeguranca.SEVERIDADE_CRITICA, alertas.get(0).getSeveridade());
        assertSame(sensorReator, alertas.get(0).getSensorOrigem());
    }

    @Test
    void violacaoComVeiculoDeveSerConfirmadaPeloDrone() {
        sensorCercaNorte.detectarViolacao();
        dronePatrulha.getCameraEmbarcada().simularCena(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(2, alertas.size());
        assertEquals(AlertaSeguranca.SEVERIDADE_ALTA, alertas.get(0).getSeveridade());
        assertEquals(AlertaSeguranca.SEVERIDADE_CRITICA, alertas.get(1).getSeveridade());
        assertEquals("Norte", dronePatrulha.getUltimoSetorInspecionado());
    }

    @Test
    void violacaoSemApoioDisponivelDeveGerarAlertaCritico() {
        dronePatrulha.atualizarBateria(10);
        sensorCercaNorte.detectarViolacao();

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(3, alertas.size());
        assertEquals(AlertaSeguranca.SEVERIDADE_CRITICA, alertas.get(1).getSeveridade());
        assertSame(sensorCercaNorte, alertas.get(1).getSensorOrigem());
        assertEquals(AlertaSeguranca.SEVERIDADE_BAIXA, alertas.get(2).getSeveridade());
    }

    @Test
    void dispositivoForaDeOperacaoDeveVirarPontoCego() {
        sensorReator.retirarDeOperacao();

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertEquals(AlertaSeguranca.SEVERIDADE_ALTA, alertas.get(0).getSeveridade());
        assertSame(sensorReator, alertas.get(0).getSensorOrigem());
    }
}
