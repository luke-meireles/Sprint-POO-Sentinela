# SENTINELA — Sprint

Protótipo em console que simula o monitoramento de uma instalação nuclear fictícia: sensores e um drone registram leituras, e a central emite alertas por severidade (BAIXA, MEDIA, ALTA, CRITICA).

Projeto do **Sentinela Nuclear Challenge (AMAZUL)** — FIAP, 2CCPR.

## Como executar

Requisitos: Java 25+.

Pela IDE (Eclipse/IntelliJ/VS Code): abrir o projeto e rodar `br.com.fiap.sentinela.Main`.

Com Maven (opcional):

```bash
mvn compile exec:java   # roda os 3 cenários
mvn test                # roda os testes
```

## Cenários demonstrados

1. **Rotina normal** — leituras dentro dos limites, perímetro íntegro, drone patrulha sem detecções.
2. **Pico de radiação** — a Casa do Reator ultrapassa o limite e a central emite alerta CRITICA.
3. **Acesso indevido** — o sensor da cerca detecta violação, o drone é despachado e a câmera confirma um veículo não identificado.

## Principais classes

- `ZonaRestrita` — guarda o nível de radiação e classifica o risco ("NORMAL", "ATENCAO", "CRITICO").
- `DispositivoMonitoramento` — classe **abstrata**, mãe de `SensorRadiacao`, `SensorPerimetral` e `DronePatrulha`.
- `Despachavel` — **interface**: contrato de quem pode ser despachado para verificar uma ocorrência (o drone assina).
- `AlertaSeguranca` — TEM-UM sensor de origem e TEM-UMA zona afetada.
- `CentralMonitoramento` — TEM-UM array de dispositivos; pede que cada um analise o próprio estado (**polimorfismo**, sem `if` de tipo) e reúne os alertas.

## Respostas das Reflexões

### Por que `ZonaRestrita` é uma classe e “ZNR-01 - Casa do Reator” é um objeto?

`ZonaRestrita` é uma classe porque define os dados e os comportamentos comuns a todas as zonas restritas. Ela é um modelo, não uma zona específica.

“ZNR-01 - Casa do Reator” representa uma zona concreta, criada a partir dessa classe. Como objeto, ela tem valores próprios, como identificador, descrição e leitura atual de radiação.

Em resumo: a classe define; o objeto existe com valores específicos. É como a planta de uma casa e a casa construída: a planta descreve como a casa pode ser, mas é a casa construída que existe como lugar concreto.

### Por que a regra de classificação de risco deve ficar em `ZonaRestrita`, e não espalhada em `if`s no `main`?

A regra usa dados da própria zona, então faz sentido manter os dados e o comportamento relacionado juntos. Se a classificação ficasse no `main` ou fosse repetida em vários lugares, uma alteração poderia ser feita em um trecho e esquecida em outro, causando resultados diferentes para a mesma leitura. Centralizada em `ZonaRestrita`, a regra tem uma única fonte de verdade.

### Se `nivelRadiacaoAtual` fosse público, que fraude um programador descuidado ou malicioso poderia causar?

Qualquer trecho do programa poderia alterar diretamente a leitura, por exemplo, definindo `zona.nivelRadiacaoAtual = 0.1` antes da geração de um relatório. Isso poderia ocultar um pico real de radiação. Também seria possível registrar valores fisicamente impossíveis. Mantendo o atributo privado, as alterações passam por `registrarLeituraRadiacao()`, que valida as leituras.

### Por que a ausência de um construtor padrão protege o cadastro de zonas?

Como `ZonaRestrita` declara seu próprio construtor, o Java não cria automaticamente um construtor sem parâmetros. Assim, `new ZonaRestrita()` não compila: para criar uma zona, é preciso fornecer os dados exigidos pelo construtor, que também valida esses valores.

### Se o alerta guardasse apenas o código do sensor como `String`, o que a central perderia?

A central perderia o acesso direto ao sensor. Com apenas o código, teria de procurar o dispositivo em outra lista sempre que precisasse consultar informações, como localização, estado de operação e zona monitorada. Ao guardar uma referência ao objeto, o alerta mantém esse acesso direto.

### Se `codigoIdentificador` é `private` na classe mãe, por que a classe filha precisa passar esse dado por `super()`? Que princípio está sendo protegido?

O princípio é o encapsulamento: um atributo `private` não pode ser acessado diretamente pela classe filha. Ao passar o código para `super()`, a filha usa o construtor da classe mãe, que valida os dados comuns e garante que cada dispositivo seja inicializado corretamente.
