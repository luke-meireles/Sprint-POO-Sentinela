package br.com.fiap.sentinela.dispositivos;

import br.com.fiap.sentinela.central.AlertaSeguranca;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SensorRadiacao extends DispositivoMonitoramento {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final double limiteSeguranca;
    private double leituraAtual;

    public SensorRadiacao(String codigoIdentificador, String localizacao, ZonaRestrita zonaMonitorada) {
        super(codigoIdentificador, localizacao, zonaMonitorada);
        this.limiteSeguranca = zonaMonitorada.getLimiteRadiacao();
    }

    public void registrarLeitura(double leitura) {
        if (!isEmOperacao()) {
            System.out.println("[SENSOR] " + descreverIdentificacao() + " está fora de operação; leitura ignorada.");
            return;
        }
        getZonaMonitorada().registrarLeituraRadiacao(leitura);
        this.leituraAtual = getZonaMonitorada().getNivelRadiacaoAtual();
    }

    @Override
    public List<AlertaSeguranca> analisarEstado() {
        List<AlertaSeguranca> alertasGerados = new ArrayList<>();
        ZonaRestrita zonaAfetada = getZonaMonitorada();
        String riscoDaZona = zonaAfetada.classificarRisco();

        if (ZonaRestrita.RISCO_CRITICO.equals(riscoDaZona)) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_CRITICA,
                    String.format(PT_BR, "Radiação de %.2f µSv/h ultrapassou o limite de segurança (%.2f µSv/h). "
                            + "Acionar protocolo de emergência radiológica e evacuar a zona.",
                            this.leituraAtual, zonaAfetada.getLimiteRadiacao())));
        } else if (ZonaRestrita.RISCO_ATENCAO.equals(riscoDaZona)) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_MEDIA,
                    String.format(PT_BR, "Radiação de %.2f µSv/h se aproxima do limite (%.2f µSv/h). "
                            + "Intensificar monitoramento.",
                            this.leituraAtual, zonaAfetada.getLimiteRadiacao())));
        }
        return alertasGerados;
    }

    public boolean ultrapassouLimiteSeguranca() {
        return this.leituraAtual >= this.limiteSeguranca;
    }

    public double getLeituraAtual() {
        return this.leituraAtual;
    }

    public double getLimiteSeguranca() {
        return this.limiteSeguranca;
    }
}
