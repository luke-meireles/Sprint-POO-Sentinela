package br.com.fiap.sentinela.dispositivos;

import br.com.fiap.sentinela.central.AlertaSeguranca;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.util.List;

public abstract class DispositivoMonitoramento {

    private final String codigoIdentificador;
    private final String localizacao;
    private final ZonaRestrita zonaMonitorada;
    private boolean emOperacao = true;

    protected DispositivoMonitoramento(String codigoIdentificador, String localizacao, ZonaRestrita zonaMonitorada) {
        if (codigoIdentificador == null || codigoIdentificador.trim().isEmpty()) {
            throw new IllegalArgumentException("Um dispositivo não pode nascer sem código identificador.");
        }
        if (localizacao == null || localizacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Um dispositivo não pode nascer sem localização.");
        }
        if (zonaMonitorada == null) {
            throw new IllegalArgumentException("Um dispositivo não pode nascer sem uma zona para monitorar.");
        }
        this.codigoIdentificador = codigoIdentificador.trim();
        this.localizacao = localizacao.trim();
        this.zonaMonitorada = zonaMonitorada;
    }

    public abstract List<AlertaSeguranca> analisarEstado();

    public void colocarEmOperacao() {
        this.emOperacao = true;
    }

    public void retirarDeOperacao() {
        this.emOperacao = false;
    }

    public String descreverIdentificacao() {
        return this.codigoIdentificador + " (" + this.localizacao + ")";
    }

    public String getCodigoIdentificador() {
        return this.codigoIdentificador;
    }

    public String getLocalizacao() {
        return this.localizacao;
    }

    public ZonaRestrita getZonaMonitorada() {
        return this.zonaMonitorada;
    }

    public boolean isEmOperacao() {
        return this.emOperacao;
    }
}
