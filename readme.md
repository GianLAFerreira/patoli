# Patolli (Java 21 • Swing • MVC)

Jogo Patolli desenvolvido em Java 21 com Swing, seguindo o padrão MVC com atualização reativa via Observer/Observable e ViewModels para desacoplar a View do domínio.

- Linguagem: Java 21
- Build: Maven
- UI: Swing
- Padrões: MVC + Observer/Observable
- Layout do tabuleiro: Cruz 2x2 central em grade 16x16 (plugável via BoardLayout)

## Sumário
- [Como executar](#como-executar)
- [Regras implementadas](#regras-implementadas)
- [Controles da interface](#controles-da-interface)
- [Arquitetura](#arquitetura)
- [Estrutura de pacotes](#estrutura-de-pacotes)
- [Tecnologias e versões](#tecnologias-e-versões)
- [Roadmap](#roadmap)

---

## Como executar

Pré-requisitos:
- JDK 21
- Maven 3.9+

Passos:
- Executar pela IDE a classe `padroes.projeto.patoli.Main`
- Ou via Maven:
    - `mvn clean package`
    - `java -jar target/patoli-1.0-SNAPSHOT.jar`

Observação: a janela abre maximizada (como “tela cheia” com bordas de janela).

---

## Regras implementadas

- Objetivo: ser o primeiro a finalizar todas as 6 peças. Um jogador perde imediatamente se ficar sem moedas.
- Componentes:
    - Tabuleiro (cruz 2x2) com trilha configurável via `BoardLayout`.
    - Peças: 6 por jogador (preto e branco).
    - Moedas (aposta): 20 por jogador no início.
    - “Dados”: 5 moedas lançadas; soma dos lados pintados (0–5).

- Início:
    - Joga quem vencer a rolagem inicial.
    - 1 peça inicial de cada jogador é posicionada na casa START (sem penalidade/bonus).

- Movimento:
    - Lançar as 5 moedas. O valor é a quantidade de passos.
    - Se “1”, pode inserir uma nova peça na START (se a START estiver livre) ao invés de mover.
    - Sem captura: se o destino estiver ocupado, o movimento com aquela peça é inválido (escolha outra). Se nenhuma peça puder se mover, passe a vez.

- Casas especiais:
    - Triângulo (punição): ao pousar, paga 1 moeda ao oponente.
    - Extremidade (ENDPOINT): ao pousar, concede um turno extra.

- Finalização:
    - Precisa cair exatamente na START ao final do movimento para finalizar a peça.
    - A casa START deve estar livre (não há captura ao finalizar).
    - Ao finalizar, o oponente paga 1 moeda ao jogador que finalizou.

- Passar:
    - Permitido apenas quando não houver jogadas válidas (incluindo inserir peça ao tirar “1”).

---

## Controles da interface

- Barra à direita:
    - “Rolar Moedas”: lança as moedas (uma rolagem por turno).
    - “Entrar Peça (1)”: disponível quando o resultado foi 1 e a START está livre.
    - “Passar”: apenas se não houver movimentos válidos.

- Tabuleiro:
    - Clique em uma peça sua para movê-la (se houver rolagem válida).
- Barra superior fixa:
    - Mostra “Vez: Jogador (Cor)” e “Rolagem: valor” permanentemente.

---

## Arquitetura

- Model (Domínio):
    - `Game`: estado e orquestração de regras; implementa `GameObservable` e notifica alterações com motivos (ex.: “INIT”, “ROLL”, “MOVE”, “FINISH”, “PENALTY”, “BONUS”, “TURN”, “NO_COINS”).
    - `Board`: trilha (track), STARTs, ENDPOINTs, punições, ocupação de peças.
    - `CoinDice`: rolagem (0–5).
    - `BoardLayout` + `Cross2x2Layout16`: layout plugável do tabuleiro (cruz 2x2, 16x16).
    - Regras extraídas:
        - `MovementRules`: cálculo de destino e finalização (exatidão + START livre).
        - `ScoringRules`: penalidades e bônus ao pousar.

- Controller:
    - `GameController`: observa o `Game` (Observer), intermedia comandos View→Model (rolar, entrar peça, clicar célula, passar) e expõe dados “read-only” e ViewModels para a View (`CellVM`, `PieceVM`, `PlayerColorVM`, `CellTypeVM`). Também oferece `getCellsAsMatrix()` para pintura eficiente.

- View (Swing):
    - `MainFrame`: janela principal (implementa `GameView`).
    - Painéis:
        - `StatusBarPanel`: barra superior fixa com “Vez” e “Rolagem”.
        - `ActionBarPanel`: botões de ação (rolar, entrar, passar).
        - `BoardPanel`: tabuleiro; delega pintura para utilitários.
        - `PlayerInfoPanel`: moedas e peças (reservadas/finalizadas) por jogador.
    - Pintura (utilitários):
        - `BoardPainter`: fundo, grade, moldura e células.
        - `PiecePainter`: desenho das peças (com gradiente, aro e brilho).
    - A View não importa classes de `model.*`, usando apenas Controller e ViewModels.

Fluxo de atualização:
- Model notifica (Observer) -> Controller recebe (`onGameChanged`) -> Controller chama `view.onEvent(reason)` e `view.refresh()` -> View lê dados do Controller e repinta.

---
