package br.com.fiap.sentinela.dominio;

public class ZonaRestrita {

    public static final String RISCO_NORMAL = "NORMAL";
    public static final String RISCO_ATENCAO = "ATENCAO";
    public static final String RISCO_CRITICO = "CRITICO";

    public static final double LEITURA_MAXIMA_PLAUSIVEL = 1_000_000.0;
    private static final double FRACAO_DO_LIMITE_PARA_ATENCAO = 0.5;

    private final String identificador;
    private final String descricao;
    private final double limiteRadiacao;
    private double nivelRadiacaoAtual;

    public ZonaRestrita(String identificador, String descricao, double limiteRadiacao) {
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new IllegalArgumentException("Uma zona restrita não pode nascer sem identificador.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Uma zona restrita não pode nascer sem descrição.");
        }
        if (limiteRadiacao <= 0) {
            throw new IllegalArgumentException("O limite de radiação da zona deve ser maior que zero.");
        }
        this.identificador = identificador.trim();
        this.descricao = descricao.trim();
        this.limiteRadiacao = limiteRadiacao;
    }

    public void registrarLeituraRadiacao(double leitura) {
        if (leitura < 0) {
            System.out.println("[FALHA DE SENSOR] Leitura rejeitada em " + this.identificador
                    + ": radiação negativa não existe fisicamente (" + leitura + " µSv/h).");
            return;
        }
        if (leitura > LEITURA_MAXIMA_PLAUSIVEL) {
            System.out.println("[FALHA DE SENSOR] Leitura rejeitada em " + this.identificador
                    + ": valor absurdo (" + leitura + " µSv/h).");
            return;
        }
        this.nivelRadiacaoAtual = leitura;
    }

    public String classificarRisco() {
        if (this.nivelRadiacaoAtual >= this.limiteRadiacao) {
            return RISCO_CRITICO;
        }
        if (this.nivelRadiacaoAtual >= this.limiteRadiacao * FRACAO_DO_LIMITE_PARA_ATENCAO) {
            return RISCO_ATENCAO;
        }
        return RISCO_NORMAL;
    }

    public String getIdentificador() {
        return this.identificador;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public double getLimiteRadiacao() {
        return this.limiteRadiacao;
    }

    public double getNivelRadiacaoAtual() {
        return this.nivelRadiacaoAtual;
    }

    @Override
    public String toString() {
        return this.identificador + " - " + this.descricao;
    }
}
