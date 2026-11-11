<div align="center">

# A Biblioteca das Almas

**Uma história sobre memória, escolhas e o direito de escrever o próprio destino.**

*RPG narrativo de fantasia gótica desenvolvido em Java 2D.*

![Prévia do menu inicial](preview_menu.png)

**Java 21** · **Swing / Graphics2D** · **Maven** · **Visual novel** · **Exploração point-and-click**

</div>

---

## Sobre o projeto

**A Biblioteca das Almas** é um jogo narrativo de mistério e fantasia gótica ambientado em uma biblioteca sobrenatural onde a existência das pessoas é preservada em livros. A experiência combina **exploração de cenários estáticos**, investigação de objetos, diálogos com personagens, recuperação de memórias e **escolhas que alteram o desfecho da história**.

O jogador acompanha **Natasha Rostova**, que desperta nos degraus de uma biblioteca sem se lembrar de como chegou ali. Ao investigar o lugar, ela encontra um bibliotecário que conhece seu nome, uma figura fantasmagórica que carrega histórias esquecidas e uma entidade que controla os registros das almas. Aos poucos, Natasha descobre que sua ligação com a biblioteca é muito mais antiga do que imagina.

O jogo não utiliza movimentação livre ou combate: a navegação acontece por **cliques em áreas interativas (*hotspots*)**, com destaque para a atmosfera, a leitura e as consequências das decisões.

> **Estado do desenvolvimento:** o **Capítulo I está 100% completo e jogável**, do despertar na Entrada aos três desfechos na Câmara dos Registros. Conta com 28 cenas narrativas no roteiro JSON consolidado, seis ambientes exploráveis por cliques, quatro personagens canônicos com retratos e expressões, inventário, diário de memórias, cutscenes, salvamento versionado e oito suítes de testes automatizados com aprovação integral. Disponível como pacote autônomo (*fat JAR*) independente. Veja [Andamento do desenvolvimento](#andamento-do-desenvolvimento).

**Documentação do trabalho prático:** veja a [documentação acadêmica](docs/documentacao-academica.md), organizada conforme as orientações do professor, com diagramas Mermaid, exemplos de POO, execução, validação e a situação dos requisitos obrigatórios. As pendências de tela cheia inicial, Esc, movimentação, colisões, áudio e hierarquia de referência estão registradas nesse documento.

## Capítulo I — O Nome que Não Deveria Existir

O primeiro capítulo acompanha Natasha em uma investigação pela biblioteca. Cada ambiente revela uma parte da relação entre as memórias, os registros e a liberdade das almas.

O capítulo foi planejado com **26 cenas narrativas**, incluindo uma cena final com três variações, representadas por **28 entradas no roteiro JSON consolidado**. A previsão inicial de duração é de **25 a 40 minutos**, sujeita a ajustes após testes.

### Personagens

| Personagem | Papel na narrativa |
| --- | --- |
| **Natasha Rostova** | Protagonista. Investiga o próprio passado e aprende a lidar com as consequências de decisões que não consegue recordar. |
| **Rodion Raskólnikov** | Bibliotecário reservado e irônico. Conhece os registros e esconde informações por medo de repetir um erro do passado. |
| **A Fantasma** | Uma alma gentil que absorveu vestígios de histórias apagadas para proteger outras pessoas, apesar do sofrimento que isso lhe causa. |
| **O Escriba** | Entidade sem rosto que administra os registros. Acredita que controlar as histórias é a única maneira de impedir que desapareçam. |

### Ambientes

| Área | Função no jogo |
| --- | --- |
| **Entrada da Biblioteca** | Despertar de Natasha, primeira investigação e carta misteriosa. |
| **Salão Principal** | Centro de exploração, descoberta dos livros e acesso às demais áreas. |
| **Balcão de Rodion** | Conversas, pistas e revelações sobre os registros. |
| **Corredor dos Livros Esquecidos** | Encontro com a Fantasma, fotografia e medalhão. |
| **Arquivo Proibido** | Descoberta do livro de Natasha, memórias e regras ocultas. |
| **Câmara dos Registros** | Confronto com o Escriba e decisão que determina o final. |

### Mecânicas narrativas planejadas

- **Diálogos ramificados:** respostas que mudam a postura de Natasha e as reações dos personagens.
- **Investigação:** interação com livros, portas, cartas, fotografias e documentos.
- **Inventário:** armazenamento de pistas e objetos relevantes para a história.
- **Diário de memórias:** acompanhamento de lembranças recuperadas e descobertas.
- **Cutscenes:** ilustrações exibidas nos momentos de maior impacto narrativo.
- **Três finais:** dois acessíveis pela progressão principal e um terceiro condicionado à investigação completa.
- **Salvamento de progresso:** persistência do estado da partida, escolhas e descobertas.

<details>
<summary><strong>Detalhes da história e condições dos finais (spoilers)</strong></summary>

Natasha já esteve na biblioteca e aceitou um pacto para impedir que determinadas almas fossem apagadas. O vínculo criado com seu próprio livro, porém, passou a ser usado pelo Escriba para mantê-las aprisionadas. Rodion a ajudou na primeira tentativa e esconde a verdade por medo de que ela se sacrifique novamente.

O capítulo apresenta três desfechos:

1. **A Última Página:** Natasha destrói o livro, liberta as almas vinculadas e sacrifica sua própria existência.
2. **Entre Páginas Fechadas:** Natasha preserva o livro e sobrevive, mas o ciclo de aprisionamento continua.
3. **A História que Ainda Não Foi Escrita:** Natasha reescreve o pacto, devolve às almas o direito sobre suas histórias e derrota o Escriba. Ela sobrevive, mas aceita que parte de seu passado continuará desconhecida.

O terceiro final exige exatamente cinco condições canônicas: memória da carta (`memoria_retorno`), memória da fotografia (`memoria_escolha`), memória do pacto (`memoria_pacto`), confiança da Fantasma (`fantasma_confia`) e descoberta da cláusula de reescrita (`regra_reescrita_descoberta`). Essas condições estão plenamente implementadas, verificadas por `GameState.canRewritePact()` e validadas por testes automatizados em `ActFourGameplayTest`.

</details>

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| **Java 21** | Linguagem principal e lógica do jogo. |
| **Java Swing** | Janela, eventos de mouse e teclado e componentes da aplicação. |
| **Graphics2D** | Renderização dos cenários, textos, botões, imagens e elementos da interface. |
| **Maven** | Organização do projeto e empacotamento da aplicação. |
| **JSON** | Formato do roteiro, escolhas e metadados narrativos preparados para integração. |
| **PNG (com transparência)** | Personagens, expressões, cutscenes e componentes visuais. |

A interface utiliza Swing e Graphics2D, sem motor gráfico externo. O carregador usa **Gson 2.13.2** para leitura estrita de JSON; a dependência é declarada no Maven e incluída no JAR gerado por `mvn package`.

## Como executar

### Requisitos

- **JDK ou runtime Java 21** instalado.
- Windows, Linux ou macOS com ambiente gráfico.
- Maven é necessário apenas para recompilar o projeto pelo fluxo descrito abaixo; **não é necessário para executar o JAR disponibilizado**.

### Windows — execução rápida

### Windows — execução rápida

O pacote executável independente (*fat JAR*) completo está disponível na pasta `dist/` com todas as dependências e artes embutidas:

1. Dê dois cliques em **`INICIAR_WINDOWS.bat`** (executa diretamente `dist\biblioteca-das-almas-menu-1.0.0.jar`).
2. Se preferir executar via PowerShell na raiz do projeto:

```powershell
java -jar .\dist\biblioteca-das-almas-menu-1.0.0.jar
```

A primeira linha deve indicar Java **21**. Se `java` não for reconhecido, instale o [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21), habilite a configuração de `PATH` e abra um novo terminal.

### Linux e macOS

```bash
chmod +x INICIAR_LINUX_MAC.sh
./INICIAR_LINUX_MAC.sh
```

Alternativamente:

```bash
java -jar dist/biblioteca-das-almas-menu-1.0.0.jar
```

### Compilação a partir do código-fonte (Maven)

Caso deseje recompilar o projeto pelo Maven:

```bash
mvn clean package
```

O JAR executável completo será gerado em `target/biblioteca-das-almas-menu-1.0.0.jar` e pode ser copiado para `dist/`.

### Validação do roteiro e prévia gráfica sem abrir janela

Para carregar e validar o roteiro completo sem abrir janela gráfica ou alterar saves:

```powershell
java -jar dist/biblioteca-das-almas-menu-1.0.0.jar --validate-story
```

*Saída esperada:* confirmação de **28 cenas e 620 nós validados** com sucesso.

Para renderizar uma imagem de teste da interface em modo *headless*:

```powershell
java -jar dist/biblioteca-das-almas-menu-1.0.0.jar --preview preview_teste.png
```

## Controles

| Controle | Ação |
| --- | --- |
| **Mouse** | Investigar carta/livro, atravessar passagens, selecionar respostas e consultar inventário/diário. |
| **↑ / ↓** | Selecionar controles do menu ou da história. |
| **Enter / Espaço** | Revelar a fala, avançar o texto ou ativar o controle selecionado. |
| **I / J** | Abrir inventário / diário durante a exploração. |
| **Esc** | Fechar o diário/inventário, cancelar uma confirmação ou voltar ao menu. |
| **F11** | Alternar tela cheia. |

O menu foi projetado originalmente em **1280 × 720 (16:9)** e adapta a interface ao redimensionamento da janela.

## Andamento do desenvolvimento

| Recurso | Status |
| --- | --- |
| Menu inicial: Novo Jogo, Continuar, Configurações, Créditos e Sair | **Implementado** |
| Navegação por teclado/mouse e modo tela cheia (F11) | **Implementado** |
| Prólogo: Despertar, carta, portas e livro mutável — cenas 01–04 | **Implementado e testado** |
| Ato I: Salão Principal e Balcão de Rodion — cenas 05–07 | **Implementado e testado** |
| Ato II: Corredor dos Esquecidos e A Fantasma — cenas 08–12 | **Implementado e testado** |
| Ato III: Arquivo Proibido, Pacto e Confissão — cenas 13–19 | **Implementado e testado** |
| Ato IV: Câmara dos Registros e Escriba — cenas 20–25 | **Implementado e testado** |
| Três Finais Canônicos e Epílogos — cena 26 (I, II e III) | **Implementado e testado** |
| Exploração definitiva dos seis ambientes e hotspots | **Implementado** |
| Inventário, diário de memórias e persistência de escolhas | **Implementado** |
| Roteiro consolidado em `src/main/resources/story/roteiro_completo.json` | **Validado (28 cenas, 620 nós)** |
| Carregamento JSON estrito com Gson 2.13.2 | **Implementado; validável com `--validate-story`** |
| Hierarquia de personagens com retratos dinâmicos e anonimato | **Implementado (Natasha, Rodion, Fantasma, Escriba)** |
| Salvamento narrativo atômico versionado (`historia.json`) | **Implementado** |
| Pacote autônomo (*fat JAR*) executável e scripts de inicialização | **Implementado e validado em `dist/`** |
| Testes integrados de regressão do capítulo completo | **8 suítes aprovadas (100% verde)** |

**Escopo completo entregue:** Novo Jogo executa o despertar e libera a Entrada para investigação. O jogador investiga o Salão, conversa com Rodion no Balcão, encontra a Fantasma no Corredor dos Livros Esquecidos, descobre o livro de Natasha e a cláusula no Arquivo Proibido e confronta o Escriba na Câmara dos Registros. Na cena 20, o jogador pode entrar na Câmara ou revisitar livremente a biblioteca para buscar pistas pendentes. A decisão final da cena 25 conta com confirmação/cancelamento modal e conduz aos três epílogos canônicos da cena 26 com cutscenes de encerramento.

## Organização do projeto

Estrutura completa do **projeto Java executável**:

```text
A_Biblioteca_das_Almas_Menu/
├── pom.xml
├── README.md
├── INICIAR_WINDOWS.bat
├── INICIAR_LINUX_MAC.sh
├── dist/
│   ├── biblioteca-das-almas-menu-1.0.0.jar (Fat JAR autônomo, 116 MB)
│   └── BibliotecaDasAlmas-Menu.jar (Cópia para compatibilidade)
├── docs/
│   ├── arquitetura-e-poo.md
│   ├── relatorio-academico-poo.md
│   ├── planejamento-capitulo-01.md
│   ├── testes-do-capitulo.md
│   ├── contrato-narrativo-json.md
│   ├── catalogo-de-recursos.md
│   ├── avaliacao-roteiro-completo.md
│   └── rumos-da-historia.md
├── preview_menu.png
└── src/
    ├── main/
    │   ├── java/br/com/bibliotecadasalmas/
    │   │   ├── Game.java
    │   │   ├── assets/
    │   │   │   └── AssetManager.java
    │   │   ├── characters/
    │   │   │   ├── GameCharacter.java
    │   │   │   ├── Natasha.java
    │   │   │   ├── Rodion.java
    │   │   │   ├── Fantasma.java
    │   │   │   ├── Escriba.java
    │   │   │   └── CharacterRegistry.java
    │   │   ├── core/
    │   │   │   ├── GameController.java
    │   │   │   ├── GameMode.java
    │   │   │   ├── GameSettings.java
    │   │   │   ├── GameState.java
    │   │   │   ├── StoryProgress.java
    │   │   │   └── SaveManager.java
    │   │   ├── narrative/
    │   │   │   ├── StoryLoader.java
    │   │   │   ├── NarrativeSession.java
    │   │   │   ├── OpeningContent.java
    │   │   │   ├── Story.java
    │   │   │   ├── NarrativeNode.java
    │   │   │   ├── ChoiceOption.java
    │   │   │   ├── StoryCondition.java
    │   │   │   ├── NarrativeEffect.java
    │   │   │   ├── StoryValidator.java
    │   │   │   ├── StrictJson.java
    │   │   │   └── StoryLoadException.java
    │   │   ├── world/
    │   │   │   └── Interactable.java
    │   │   └── ui/
    │   │       ├── GamePanel.java
    │   │       ├── StoryView.java
    │   │       ├── HotspotControl.java
    │   │       ├── ClickTarget.java
    │   │       ├── AbstractControl.java
    │   │       ├── GothicButton.java
    │   │       └── TextOption.java
    │   └── resources/
    │       ├── assets/
    │       │   ├── cenarios/
    │       │   ├── cutscenes/
    │       │   ├── menu/
    │       │   ├── personagens/
    │       │   │   ├── natasha/
    │       │   │   └── rodion/
    │       │   └── ui/
    │       └── story/
    │           ├── roteiro_completo.json (28 cenas, 620 nós)
    │           └── trecho_inicial.json (manifesto unificado do capítulo)
    └── test/java/
        └── br/com/bibliotecadasalmas/
            ├── StoryLoaderTest.java
            ├── GameStateTest.java
            ├── MenuSmokeTest.java
            ├── OpeningGameplayTest.java
            ├── ActOneGameplayTest.java
            ├── ActTwoGameplayTest.java
            ├── ActThreeGameplayTest.java
            └── ActFourGameplayTest.java
```

### Pacote narrativo JSON

O roteiro consolidado está integrado em `src/main/resources/story/roteiro_completo.json` e o manifesto unificado de cenas, fundos, cutscenes, retratos, hotspots e documentos do capítulo em `src/main/resources/story/trecho_inicial.json`. Ambos são empacotados diretamente dentro do JAR executável e carregados pelo classpath.

## Arquitetura e orientação a objetos

O projeto foi projetado com rigor acadêmico, aplicando os quatro pilares da Programação Orientada a Objetos (POO):

| Pilar | Aplicação no código |
| --- | --- |
| **Encapsulamento** | `GameState` protege progresso e coleções através de cópias defensivas (`unmodifiableSet`/`unmodifiableMap`), valida adições de inventário, sincroniza atômica e internamente memórias e flags (`syncFlagForMemory`) e encapsula a regra canônica dos cinco requisitos da reescrita (`canRewritePact`). `SaveManager` garante persistência atômica via arquivos temporários (`.tmp`). |
| **Abstração** | `ClickTarget` abstrai o comportamento de elementos clicáveis de interface; `Interactable` abstrai interações de exploração do mundo; `GameCharacter` abstrai identidades e retratos de personagens; `NarrativeNode` e `NarrativeEffect` abstraem os tipos formais do roteiro via interfaces seladas (*sealed interfaces*). |
| **Herança** | `AbstractControl` compartilha limites geométricos e disparo de comandos com `GothicButton`, `HotspotControl` e `TextOption`. A classe abstrata `GameCharacter` é especializada por `Natasha`, `Rodion`, `Fantasma` e `Escriba`. |
| **Polimorfismo** | O loop de renderização do `GamePanel` desenha polimorficamente qualquer `ClickTarget`. Os cliques em cenários despacham polimorficamente `Interactable.interact` para investigações ou passagens. `CharacterRegistry` resolve polimorficamente nomes de exibição e retratos dinâmicos (incluindo o anonimato de Rodion na cena 04 e pseudônimos de memória). O interpretador `NarrativeSession` avalia nós e efeitos usando *pattern matching* exaustivo. |

> [!NOTE]
> Para uma análise acadêmica detalhada com diagramas de classes Mermaid, mapeamento de métodos e padrões de projeto (Memento, Registry, DTO, MVC), consulte o [Relatório Acadêmico de POO](docs/relatorio-academico-poo.md) e [Arquitetura e POO](docs/arquitetura-e-poo.md).

### Responsabilidades principais

- **`Game`** — inicialização da aplicação, janela Swing e suporte a modos headless (`--validate-story`, `--preview`).
- **`GamePanel`** — desenho das telas com `Graphics2D`, suporte a tela cheia (F11), redimensionamento proporcional 1280×720 e tratamento de eventos.
- **`GameController`** — coordenação de modos (`STORY`, `SETTINGS`, `CREDITS`, `MENU`), navegação entre ambientes e suspensão/retomada de exploração aberta na cena 20.
- **`GameState`** — modelo de estado puro e encapsulado em memória (cursor, inventário, memórias, flags, escolhas, locais desbloqueados e verificação de reescrita).
- **`GameCharacter` / `Natasha` / `Rodion` / `Fantasma` / `Escriba` / `CharacterRegistry`** — hierarquia e registro de personagens com retratos dinâmicos, anonimato e pseudônimos de memória.
- **`StoryLoader` / `StoryValidator`** — leitura estrita via Gson 2.13.2, modelos imutáveis e validação exaustiva de nós e regras secretas.
- **`NarrativeSession`** — interpretador polimórfico de nós e efeitos narrativos em lote sobre cópias defensivas de estado.
- **`StoryView` / `OpeningContent`** — renderização da caixa de diálogo gótica com efeito máquina de escrever, retratos com sombras, escolhas e catálogo de documentos/memórias.
- **`Interactable` (`Investigation`, `Passage`)** — ações interativas do mundo despachadas pelos hotspots dos cenários.
- **`ClickTarget` / `AbstractControl` / `GothicButton` / `HotspotControl` / `TextOption`** — hierarquia de controles clicáveis de interface com texturas do UI Kit.
- **`AssetManager`** — carregamento e cache em memória de texturas, cenários, retratos e cutscenes do classpath.
- **`SaveManager` / `StoryProgress`** — persistência atômica versionada em `historia.json` com recuperação segura de falhas.

### Salvamento local

O jogo armazena dados em arquivos JSON no diretório do usuário:

```text
Windows: %USERPROFILE%\.biblioteca-das-almas\
Linux/macOS: ~/.biblioteca-das-almas/
```

Partidas em andamento usam `historia.json`, gravado automaticamente após cada avanço ou escolha do jogador. O save registra roteiro, versão, cenário, cena/nó, diálogo ativo, escolhas, flags, valores numéricos, inventário, memórias, locais liberados e interações concluídas. A gravação é atômica (grava em `.tmp` e substitui com segurança). Continuar partida restaura perfeitamente o ambiente e o ponto exato da narrativa, incluindo revisitas livres na cena 20 e o confronto na Câmara dos Registros.

## Direção artística

O estilo visual combina arquitetura **gótica vitoriana**, ilustração **2D com acabamento pictórico**, fantasia sombria e manuscritos antigos. A paleta prioriza **carvão, vinho, ameixa, azul frio e dourado envelhecido**.

Todas as ilustrações oficiais foram incorporadas ao pacote executável:
- Cenários completos em alta resolução dos seis ambientes (`entrada_biblioteca`, `salao_principal`, `balcao_rodion`, `corredor_esquecidos`, `arquivo_proibido`, `camara_registros`).
- Oito expressões canônicas de Natasha Rostova e retratos de Rodion Raskólnikov e da Fantasma.
- O Escriba com rosto em pergaminho sem olhos, nariz ou boca, respeitando estritamente o cânone.
- Cutscenes ilustradas para momentos-chave: despertar, Rodion no livro, aparição da Fantasma, descoberta do livro de Natasha, aparição do Escriba e os três desfechos finais (`final_queimar`, `final_preservar`, `final_reescrever`).

## Fases Concluídas e Próximos Passos

1. **Capítulo I — 100% Concluído e Validado:** Todos os 4 atos, 28 cenas, três finais canônicos, salvamento, pacote autônomo (*fat JAR*) e 8 suítes de testes estão entregues.
2. **Entrega Acadêmica de POO:** Relatório acadêmico completo com comprovação dos quatro pilares, padrões de projeto e roteiro para apresentação em banca disponível em [Relatório Acadêmico de POO](docs/relatorio-academico-poo.md).
3. **Capítulos Futuros (Pós-Entrega):** Expansão da história para o Capítulo II utilizando o mesmo motor narrativo desacoplado baseado em JSON.

## Desenvolvimento e organização de commits

Sugestão de padrão para mensagens de commit:

```text
docs: atualiza documentacao do projeto
feat: implementa sistema de dialogos
feat: adiciona exploracao dos cenarios
fix: corrige transicao entre cenas
refactor: separa logica narrativa da interface
```

Antes de abrir uma contribuição, confira se os recursos adicionados estão nas pastas correspondentes e se a aplicação ainda inicia com Java 21.

## Créditos e licença

Projeto acadêmico de desenvolvimento de jogos em Java, com roteiro e recursos visuais criados para **A Biblioteca das Almas**.

Os créditos individuais da equipe e a licença de distribuição **ainda não foram definidos neste repositório**. Até que uma licença seja adicionada, não presuma autorização para redistribuir o código ou os recursos gráficos.

---

<div align="center">

*“O destino de uma alma não deveria pertencer à mão que segura a pena.”*

**A Biblioteca das Almas — Capítulo I**

</div>
