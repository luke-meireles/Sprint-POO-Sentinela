package br.com.fiap.sentinela.dispositivos;

public interface Despachavel {

    boolean estaDisponivelParaDespacho();

    ObjetoDetectado verificarOcorrencia(String setorAlvo);

    String descreverIdentificacao();
}
