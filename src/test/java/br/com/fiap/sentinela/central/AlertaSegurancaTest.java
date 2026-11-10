package br.com.fiap.sentinela.central;

import br.com.fiap.sentinela.dispositivos.SensorRadiacao;
import br.com.fiap.sentinela.dominio.ZonaRestrita;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlertaSegurancaTest {

    @Test
    void alertaDeveGuardarAReferenciaDoMesmoSensorQueDisparouOEvento() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 2.5);
        SensorRadiacao sensorReator = new SensorRadiacao("RAD-01", "Parede leste", casaDoReator);

        AlertaSeguranca alerta = new AlertaSeguranca(sensorReator,
                AlertaSeguranca.SEVERIDADE_CRITICA, "Pico de radiação");

        assertSame(sensorReator, alerta.getSensorOrigem());
        assertSame(casaDoReator, alerta.getZonaAfetada());
    }

    @Test
    void alertaDeveRejeitarSeveridadeInvalida() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 2.5);
        SensorRadiacao sensorReator = new SensorRadiacao("RAD-01", "Parede leste", casaDoReator);

        assertThrows(IllegalArgumentException.class,
                () -> new AlertaSeguranca(sensorReator, "URGENTISSIMA", "Mensagem"));
    }
}
