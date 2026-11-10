package br.com.fiap.sentinela.dispositivos;

import br.com.fiap.sentinela.central.AlertaSeguranca;
import br.com.fiap.sentinela.dominio.ZonaRestrita;

import java.util.ArrayList;
import java.util.List;

public class DronePatrulha extends DispositivoMonitoramento implements Despachavel {

    public static final int BATERIA_MINIMA_PARA_VOO = 20;
    private static final int CONSUMO_BATERIA_POR_VOO = 10;
    private static final String LOCALIZACAO_HANGAR = "Hangar de drones";

    private final CameraVisaoComputacional cameraEmbarcada = new CameraVisaoComputacional();
    private final String rotaPatrulha;
    private int bateriaPercentual = 100;
    private ObjetoDetectado ultimaDeteccaoPatrulha = ObjetoDetectado.NENHUM;
    private String ultimoSetorInspecionado;

    public DronePatrulha(String codigoIdentificador, String rotaPatrulha, ZonaRestrita zonaMonitorada) {
        super(codigoIdentificador, LOCALIZACAO_HANGAR, zonaMonitorada);
        if (rotaPatrulha == null || rotaPatrulha.trim().isEmpty()) {
            throw new IllegalArgumentException("Um drone não pode nascer sem rota de patrulha.");
        }
        this.rotaPatrulha = rotaPatrulha.trim();
    }

    public ObjetoDetectado realizarPatrulha() {
        if (!executarVoo()) {
            return ObjetoDetectado.NENHUM;
        }
        this.ultimaDeteccaoPatrulha = this.cameraEmbarcada.analisarImagem();
        return this.ultimaDeteccaoPatrulha;
    }

    @Override
    public boolean estaDisponivelParaDespacho() {
        return isEmOperacao() && this.bateriaPercentual >= BATERIA_MINIMA_PARA_VOO;
    }

    @Override
    public ObjetoDetectado verificarOcorrencia(String setorAlvo) {
        if (!executarVoo()) {
            return ObjetoDetectado.NENHUM;
        }
        this.ultimoSetorInspecionado = setorAlvo;
        return this.cameraEmbarcada.analisarImagem();
    }

    @Override
    public List<AlertaSeguranca> analisarEstado() {
        List<AlertaSeguranca> alertasGerados = new ArrayList<>();
        if (this.ultimaDeteccaoPatrulha.representaAmeaca()) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_CRITICA,
                    "Patrulha detectou " + this.ultimaDeteccaoPatrulha.getDescricao()
                            + " na rota \"" + this.rotaPatrulha + "\"."));
        }
        if (this.bateriaPercentual < BATERIA_MINIMA_PARA_VOO) {
            alertasGerados.add(new AlertaSeguranca(this, AlertaSeguranca.SEVERIDADE_BAIXA,
                    "Bateria em " + this.bateriaPercentual + "%. Retornar ao hangar para recarga."));
        }
        return alertasGerados;
    }

    public void atualizarBateria(int percentual) {
        if (percentual < 0 || percentual > 100) {
            System.out.println("[DRONE] Percentual de bateria inválido ignorado: " + percentual);
            return;
        }
        this.bateriaPercentual = percentual;
    }

    private boolean executarVoo() {
        if (!estaDisponivelParaDespacho()) {
            System.out.println("[DRONE] " + descreverIdentificacao()
                    + " indisponível para voo (fora de operação ou bateria abaixo de "
                    + BATERIA_MINIMA_PARA_VOO + "%).");
            return false;
        }
        this.bateriaPercentual = this.bateriaPercentual - CONSUMO_BATERIA_POR_VOO;
        return true;
    }

    public CameraVisaoComputacional getCameraEmbarcada() {
        return this.cameraEmbarcada;
    }

    public String getRotaPatrulha() {
        return this.rotaPatrulha;
    }

    public int getBateriaPercentual() {
        return this.bateriaPercentual;
    }

    public ObjetoDetectado getUltimaDeteccaoPatrulha() {
        return this.ultimaDeteccaoPatrulha;
    }

    public String getUltimoSetorInspecionado() {
        return this.ultimoSetorInspecionado;
    }
}
