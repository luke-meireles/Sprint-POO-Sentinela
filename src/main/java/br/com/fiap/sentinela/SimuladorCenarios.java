package br.com.fiap.sentinela;

import br.com.fiap.sentinela.central.AlertaSeguranca;
import br.com.fiap.sentinela.central.CentralMonitoramento;
import br.com.fiap.sentinela.dispositivos.DispositivoMonitoramento;
import br.com.fiap.sentinela.dispositivos.DronePatrulha;
import br.com.fiap.sentinela.dispositivos.ObjetoDetectado;
import br.com.fiap.sentinela.dispositivos.SensorPerimetral;
import br.com.fiap.sentinela.dispositivos.SensorRadiacao;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.util.List;
import java.util.Locale;

public class SimuladorCenarios {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String LINHA = "=".repeat(78);

    private final SensorRadiacao sensorReator;
    private final SensorRadiacao sensorPatioCombustivel;
    private final SensorPerimetral sensorCercaNorte;
    private final DronePatrulha dronePatrulha;
    private final CentralMonitoramento central;

    public SimuladorCenarios() {
        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 2.5);
        ZonaRestrita patioCombustivelIrradiado =
                new ZonaRestrita("ZNR-02", "Pátio de Armazenamento de Combustível Irradiado", 2.5);
        ZonaRestrita perimetroExterno = new ZonaRestrita("ZNR-03", "Perímetro Externo", 0.5);

        sensorReator = new SensorRadiacao("RAD-01", "Casa do Reator - parede leste", casaDoReator);
        sensorPatioCombustivel = new SensorRadiacao("RAD-02", "Pátio - piscina de combustível 1",
                patioCombustivelIrradiado);
        sensorCercaNorte = new SensorPerimetral("PER-01", "Norte", perimetroExterno);
        dronePatrulha = new DronePatrulha("DRN-01", "Perímetro completo (sentido anti-horário)", perimetroExterno);

        sensorCercaNorte.vincularApoioDeVerificacao(dronePatrulha);

        central = new CentralMonitoramento("Instalação Nuclear Fictícia - Unidade Sentinela",
                new DispositivoMonitoramento[] {sensorReator, sensorPatioCombustivel, sensorCercaNorte, dronePatrulha});
    }

    public void executarCenarios() {
        System.out.println(LINHA);
        System.out.println("  SENTINELA - " + central.getNomeInstalacao());
        System.out.println("  Dispositivos cadastrados na central: " + central.getQuantidadeDispositivos());
        executarRotinaNormal();
        executarPicoRadiacao();
        executarAcessoIndevido();
        System.out.println(LINHA);
    }

    private void executarRotinaNormal() {
        imprimirCabecalho("CENÁRIO 1 - ROTINA NORMAL");
        registrarLeitura(sensorReator, 0.18);
        registrarLeitura(sensorPatioCombustivel, 0.22);
        imprimirEstadoPerimetro();
        dronePatrulha.getCameraEmbarcada().simularCena(ObjetoDetectado.NENHUM);
        executarPatrulhaDrone();
        imprimirAlertas(central.analisarLeituras());
    }

    private void executarPicoRadiacao() {
        imprimirCabecalho("CENÁRIO 2 - PICO DE RADIAÇÃO");
        registrarLeitura(sensorReator, 4.7);
        registrarLeitura(sensorPatioCombustivel, 0.21);
        imprimirEstadoPerimetro();
        imprimirAlertas(central.analisarLeituras());
    }

    private void executarAcessoIndevido() {
        imprimirCabecalho("CENÁRIO 3 - ACESSO INDEVIDO");
        System.out.println("  [INFO] Protocolo do cenário 2 concluído; radiação do reator normalizada.");
        registrarLeitura(sensorReator, 0.19);
        registrarLeitura(sensorPatioCombustivel, 0.20);
        sensorCercaNorte.detectarViolacao();
        dronePatrulha.getCameraEmbarcada().simularCena(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO);
        System.out.println("  [SIMULAÇÃO] Veículo posicionado junto à cerca do setor Norte.");
        imprimirEstadoPerimetro();
        imprimirAlertas(central.analisarLeituras());
        System.out.printf("  [DRONE] %s voltou da inspeção do setor %s | bateria: %d%%%n",
                dronePatrulha.getCodigoIdentificador(), dronePatrulha.getUltimoSetorInspecionado(),
                dronePatrulha.getBateriaPercentual());
    }

    private void registrarLeitura(SensorRadiacao sensorRadiacao, double leitura) {
        sensorRadiacao.registrarLeitura(leitura);
        ZonaRestrita zonaMonitorada = sensorRadiacao.getZonaMonitorada();
        System.out.printf(PT_BR, "  [LEITURA] %-8s -> %6.2f µSv/h | %s: %s%n",
                sensorRadiacao.getCodigoIdentificador(), leitura, zonaMonitorada, zonaMonitorada.classificarRisco());
    }

    private void imprimirEstadoPerimetro() {
        String estadoCerca = sensorCercaNorte.isViolacaoDetectada() ? "VIOLAÇÃO DETECTADA" : "íntegro";
        System.out.printf("  [PERÍMETRO] %s setor %s: %s%n",
                sensorCercaNorte.getCodigoIdentificador(), sensorCercaNorte.getSetorVigiado(), estadoCerca);
    }

    private void executarPatrulhaDrone() {
        ObjetoDetectado deteccao = dronePatrulha.realizarPatrulha();
        System.out.printf("  [DRONE] %s patrulhou \"%s\" | câmera: %s | bateria: %d%%%n",
                dronePatrulha.getCodigoIdentificador(), dronePatrulha.getRotaPatrulha(),
                deteccao.getDescricao(), dronePatrulha.getBateriaPercentual());
    }

    private void imprimirCabecalho(String titulo) {
        System.out.println(LINHA);
        System.out.println("  " + titulo);
        System.out.println(LINHA);
    }

    private void imprimirAlertas(List<AlertaSeguranca> alertas) {
        System.out.println("  --- Análise da central ---");
        if (alertas.isEmpty()) {
            System.out.println("  Nenhum alerta. Instalação operando dentro da normalidade.");
            return;
        }
        for (AlertaSeguranca alerta : alertas) {
            System.out.println("  " + alerta);
        }
    }
}
