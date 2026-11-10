package br.com.fiap.sentinela.dispositivos;

public class ObjetoDetectado {

    public static final ObjetoDetectado NENHUM = new ObjetoDetectado("nenhum objeto", false);
    public static final ObjetoDetectado ANIMAL_SILVESTRE = new ObjetoDetectado("animal silvestre", false);
    public static final ObjetoDetectado PESSOA_AUTORIZADA = new ObjetoDetectado("pessoa autorizada", false);
    public static final ObjetoDetectado PESSOA_NAO_IDENTIFICADA = new ObjetoDetectado("pessoa não identificada", true);
    public static final ObjetoDetectado VEICULO_NAO_IDENTIFICADO = new ObjetoDetectado("veículo não identificado", true);

    private final String descricao;
    private final boolean representaAmeaca;

    public ObjetoDetectado(String descricao, boolean representaAmeaca) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Um objeto detectado precisa de descrição.");
        }
        this.descricao = descricao.trim();
        this.representaAmeaca = representaAmeaca;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public boolean representaAmeaca() {
        return this.representaAmeaca;
    }
}
