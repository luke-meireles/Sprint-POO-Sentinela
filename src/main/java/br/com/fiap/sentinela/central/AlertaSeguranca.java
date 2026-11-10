package br.com.fiap.sentinela.central;

import br.com.fiap.sentinela.dispositivos.DispositivoMonitoramento;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertaSeguranca {

    public static final String SEVERIDADE_BAIXA = "BAIXA";
    public static final String SEVERIDADE_MEDIA = "MEDIA";
    public static final String SEVERIDADE_ALTA = "ALTA";
    public static final String SEVERIDADE_CRITICA = "CRITICA";

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final DispositivoMonitoramento sensorOrigem;
    private final ZonaRestrita zonaAfetada;
    private final String severidade;
    private final String mensagem;
    private final LocalDateTime dataHoraEmissao;

    public AlertaSeguranca(DispositivoMonitoramento sensorOrigem, String severidade, String mensagem) {
        if (sensorOrigem == null) {
            throw new IllegalArgumentException("Um alerta não pode nascer sem o dispositivo de origem.");
        }
        if (!ehSeveridadeValida(severidade)) {
            throw new IllegalArgumentException("Severidade inválida: " + severidade);
        }
        if (mensagem == null || mensagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Um alerta não pode nascer sem mensagem.");
        }
        this.sensorOrigem = sensorOrigem;
        this.zonaAfetada = sensorOrigem.getZonaMonitorada();
        this.severidade = severidade;
        this.mensagem = mensagem.trim();
        this.dataHoraEmissao = LocalDateTime.now();
    }

    private boolean ehSeveridadeValida(String severidade) {
        return SEVERIDADE_BAIXA.equals(severidade) || SEVERIDADE_MEDIA.equals(severidade)
                || SEVERIDADE_ALTA.equals(severidade) || SEVERIDADE_CRITICA.equals(severidade);
    }

    public DispositivoMonitoramento getSensorOrigem() {
        return this.sensorOrigem;
    }

    public ZonaRestrita getZonaAfetada() {
        return this.zonaAfetada;
    }

    public String getSeveridade() {
        return this.severidade;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public LocalDateTime getDataHoraEmissao() {
        return this.dataHoraEmissao;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Origem: %s | Zona: %s%n           -> %s",
                this.severidade, this.dataHoraEmissao.format(FORMATO_HORA),
                this.sensorOrigem.descreverIdentificacao(), this.zonaAfetada, this.mensagem);
    }
}
