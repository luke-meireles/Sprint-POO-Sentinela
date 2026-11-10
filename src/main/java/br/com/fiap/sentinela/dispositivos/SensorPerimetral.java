package br.com.fiap.sentinela.dispositivos;

import br.com.fiap.sentinela.central.AlertaSeguranca;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.util.ArrayList;
import java.util.List;

public class SensorPerimetral extends DispositivoMonitoramento {

    private final String setorVigiado;
    private boolean violacaoDetectada;

    private Despachavel apoioDeVerificacao;

    public SensorPerimetral(String codigoIdentificador, String setorVigiado, ZonaRestrita zonaMonitorada) {
        super(codigoIdentificador, montarLocalizacao(setorVigiado), zonaMonitorada);
        this.setorVigiado = setorVigiado.trim();
    }

    private static String montarLocalizacao(String setorVigiado) {
        if (setorVigiado == null || setorVigiado.trim().isEmpty()) {
            throw new IllegalArgumentException("Um sensor perimetral não pode nascer sem setor vigiado.");
        }
        return "Cerca perimetral - setor " + setorVigiado.trim();
    }

    public void vincularApoioDeVerificacao(Despachavel apoioDeVerificacao) {
        this.apoioDeVerificacao = apoioDeVerificacao;
    }

    public void detectarViolacao() {
        if (!isEmOperacao()) {
            System.out.println("[SENSOR] " + descreverIdentificacao() + " está fora de operação; evento ignorado.");
            return;
        }
        this.violacaoDetectada = true;
    }

    public void normalizarPerimetro() {
        this.violacaoDetectada = false;
    }

    @Override
    public List<AlertaSeguranca> analisarEstado() {
        List<AlertaSeguranca> alertasGerados = new ArrayList<>();
        if (!this.violacaoDetectada) {
            return alertasGerados;
        }

        alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_ALTA,
                "Violação da cerca detectada no setor " + this.setorVigiado
                        + ". Despachando apoio para verificação em campo."));

        if (this.apoioDeVerificacao == null || !this.apoioDeVerificacao.estaDisponivelParaDespacho()) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_CRITICA,
                    "Nenhum apoio disponível para confirmar a violação no setor " + this.setorVigiado
                            + ". Enviar equipe de segurança imediatamente."));
            return alertasGerados;
        }

        ObjetoDetectado objetoDetectado = this.apoioDeVerificacao.verificarOcorrencia(this.setorVigiado);
        if (objetoDetectado.representaAmeaca()) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_CRITICA,
                    "Apoio " + this.apoioDeVerificacao.descreverIdentificacao() + " confirmou "
                            + objetoDetectado.getDescricao() + " no setor " + this.setorVigiado
                            + ". Acionar equipe de resposta e bloquear acessos."));
        } else {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_MEDIA,
                    "Apoio " + this.apoioDeVerificacao.descreverIdentificacao() + " não confirmou ameaça no setor "
                            + this.setorVigiado + " (detectado: " + objetoDetectado.getDescricao()
                            + "). Possível falso positivo; manter observação."));
        }
        return alertasGerados;
    }

    public String getSetorVigiado() {
        return this.setorVigiado;
    }

    public boolean isViolacaoDetectada() {
        return this.violacaoDetectada;
    }
}
