package br.com.fiap.sentinela.central;

import br.com.fiap.sentinela.dispositivos.DispositivoMonitoramento;

import java.util.ArrayList;
import java.util.List;

public class CentralMonitoramento {

    private final String nomeInstalacao;
    private final DispositivoMonitoramento[] dispositivos;

    public CentralMonitoramento(String nomeInstalacao, DispositivoMonitoramento[] dispositivos) {
        if (nomeInstalacao == null || nomeInstalacao.trim().isEmpty()) {
            throw new IllegalArgumentException("A central não pode nascer sem o nome da instalação.");
        }
        if (dispositivos == null || dispositivos.length == 0) {
            throw new IllegalArgumentException("A central precisa de pelo menos um dispositivo.");
        }
        for (DispositivoMonitoramento dispositivo : dispositivos) {
            if (dispositivo == null) {
                throw new IllegalArgumentException("A central não aceita dispositivo nulo no cadastro.");
            }
        }
        this.nomeInstalacao = nomeInstalacao.trim();
        this.dispositivos = dispositivos.clone();
    }

    public List<AlertaSeguranca> analisarLeituras() {
        List<AlertaSeguranca> alertasGerados = new ArrayList<>();
        for (DispositivoMonitoramento dispositivo : this.dispositivos) {
            if (!dispositivo.isEmOperacao()) {
                alertasGerados.add(new AlertaSeguranca(dispositivo, AlertaSeguranca.SEVERIDADE_ALTA,
                        "Dispositivo fora de operação: ponto cego no monitoramento. Enviar manutenção."));
            } else {
                alertasGerados.addAll(dispositivo.analisarEstado());
            }
        }
        return alertasGerados;
    }

    public String getNomeInstalacao() {
        return this.nomeInstalacao;
    }

    public int getQuantidadeDispositivos() {
        return this.dispositivos.length;
    }
}
