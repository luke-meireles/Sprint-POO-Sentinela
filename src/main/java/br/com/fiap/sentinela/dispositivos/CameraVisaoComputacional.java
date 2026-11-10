package br.com.fiap.sentinela.dispositivos;

public class CameraVisaoComputacional {

    private ObjetoDetectado cenaSimulada = ObjetoDetectado.NENHUM;

    public void simularCena(ObjetoDetectado objetoNaCena) {
        if (objetoNaCena == null) {
            System.out.println("[CÂMERA] Cena inválida ignorada.");
            return;
        }
        this.cenaSimulada = objetoNaCena;
    }

    public ObjetoDetectado analisarImagem() {
        return this.cenaSimulada;
    }
}
