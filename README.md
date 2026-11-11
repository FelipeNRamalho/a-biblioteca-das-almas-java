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

> **Estado em 11/11/2026 (`389433f`):** os quatro atos do **Capítulo I estão integrados**, do despertar aos três desfechos, com 28 entradas no roteiro, seis ambientes lógicos, quatro personagens, inventário, diário, cutscenes e salvamento. Os oito testes existentes passaram; acabamento visual e requisitos acadêmicos pendentes estão registrados em [Andamento do desenvolvimento](#andamento-do-desenvolvimento) e na documentação acadêmica.

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

### Mecânicas narrativas

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

O pacote executável independente (*fat JAR*) completo está disponível na pasta `dist/` com todas as dependências e artes embutidas:

1. Dê dois cliques em **`INICIAR_WINDOWS.bat`** (prioriza `dist\BibliotecaDasAlmas-Menu.jar`; aceita o nome Maven como alternativa, quando presente).
2. Se preferir executar via PowerShell na raiz do projeto:

```powershell
java -version
java -jar .\dist\BibliotecaDasAlmas-Menu.jar
```

A primeira linha deve indicar Java **21**. Se `java` não for reconhecido, instale o [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21), habilite a configuração de `PATH` e abra um novo terminal.

### Linux e macOS

```bash
chmod +x INICIAR_LINUX_MAC.sh
./INICIAR_LINUX_MAC.sh
```

Alternativamente:

```bash
java -jar dist/BibliotecaDasAlmas-Menu.jar
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
java -jar dist/BibliotecaDasAlmas-Menu.jar --validate-story
```

*Saída esperada:* confirmação de **28 cenas e 620 nós validados** com sucesso.

Para renderizar uma imagem de teste da interface em modo *headless*:

```powershell
java -jar dist/BibliotecaDasAlmas-Menu.jar --preview preview_teste.png
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
| Exploração dos seis ambientes lógicos e hotspots | **Implementado; quatro ambientes reutilizam o fundo do Salão** |
| Inventário, diário de memórias e persistência de escolhas | **Implementado** |
| Roteiro consolidado em `src/main/resources/story/roteiro_completo.json` | **Validado (28 cenas, 620 nós)** |
| Carregamento JSON estrito com Gson 2.13.2 | **Implementado; validável com `--validate-story`** |
| Hierarquia de personagens com retratos dinâmicos e anonimato | **Implementado (Natasha, Rodion, Fantasma, Escriba)** |
| Salvamento narrativo atômico versionado (`historia.json`) | **Implementado** |
| Pacote autônomo (*fat JAR*) executável e scripts de inicialização | **Implementado e validado em `dist/`** |
| Testes de regressão | **Oito programas de teste; resultados e limites em [Testes do capítulo](docs/testes-do-capitulo.md)** |

**Escopo integrado:** Novo Jogo executa o despertar e libera a Entrada para investigação. O jogador investiga o Salão, conversa com Rodion no Balcão, encontra a Fantasma no Corredor dos Livros Esquecidos, descobre o livro de Natasha e a cláusula no Arquivo Proibido e confronta o Escriba na Câmara dos Registros. A configuração contempla revisita antes do confronto, confirmação/cancelamento da decisão final e os três epílogos. Os testes verificam os percursos descritos em [Testes do capítulo](docs/testes-do-capitulo.md), sem constituir medição de cobertura de 100% ou conferência manual completa.

## Organização do projeto

Estrutura completa do **projeto Java executável**:

```text
A_Biblioteca_das_Almas_Menu/
├── pom.xml
├── README.md
├── INICIAR_WINDOWS.bat
├── INICIAR_LINUX_MAC.sh
├── dist/
│   └── BibliotecaDasAlmas-Menu.jar # Fat JAR versionado com Git LFS
├── docs/
│   ├── arquitetura-e-poo.md
│   ├── documentacao-academica.md
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
    │       │   │   ├── rodion/
    │       │   │   ├── fantasma/
    │       │   │   └── escriba/
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
| **Encapsulamento** | `GameState` protege coleções com cópias imutáveis, controla itens por `giveItem`, sincroniza memórias e flags por `unlockMemory`/`setFlag` e concentra os cinco requisitos em `canRewritePact`. `SaveManager` grava por temporário e solicita substituição atômica quando suportada. |
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

Partidas usam `historia.json`, formato 1, com roteiro/versão, local, cena/nó, diálogo ativo, confirmação pendente, escolhas, flags, valores textuais, itens, memórias e marcas de interação. A escrita usa temporário e substituição atômica quando suportada, com alternativa de substituição comum. Preferências e saves legados em `.properties` permanecem disponíveis. A retomada não reaplica efeitos; as consultas fecham e a fala volta a revelar seu texto. Os casos executados e os limites da revisita da cena 20 estão em [Testes do capítulo](docs/testes-do-capitulo.md).

## Direção artística

O estilo visual combina arquitetura **gótica vitoriana**, ilustração **2D com acabamento pictórico**, fantasia sombria e manuscritos antigos. A paleta prioriza **carvão, vinho, ameixa, azul frio e dourado envelhecido**.

O repositório contém **71 PNGs**, com expressões dos quatro personagens, ilustrações de corpo inteiro, cutscenes e UI. O manifesto mapeia 12 cutscenes e oito documentos consultáveis. Há 15 expressões cadastradas para Natasha, seis para Rodion, seis para a Fantasma e cinco para o Escriba.

Os seis ambientes são distintos na lógica. A Entrada usa seu próprio fundo; Salão, Balcão, Corredor, Arquivo e Câmara usam `menu/salao_principal.png`. Fundos específicos para os quatro últimos ainda são acabamento pendente. Consulte o [catálogo de recursos](docs/catalogo-de-recursos.md) para distinguir arquivos disponíveis e referências efetivamente usadas.

O JAR distribuído é gerenciado por **Git LFS**. Em clones que contenham apenas o ponteiro textual do arquivo, executar `git lfs install` e `git lfs pull` antes de abrir o JAR. O nome Maven em `dist/` é uma alternativa aceita pelos scripts; o arquivo versionado nesta revisão é `BibliotecaDasAlmas-Menu.jar`.

## Fases Concluídas e Próximos Passos

1. **Capítulo I integrado:** quatro atos, 28 entradas de cena, três finais, salvamento e oito testes disponíveis. A revisão atual, as pendências e os resultados estão em [Testes do capítulo](docs/testes-do-capitulo.md).
2. **Entrega Acadêmica de POO:** Relatório acadêmico completo com comprovação dos quatro pilares, padrões de projeto e roteiro para apresentação em banca disponível em [Relatório Acadêmico de POO](docs/relatorio-academico-poo.md).
3. **Próximos passos:** conferir a apresentação interativa e a distribuição, resolver os pontos registrados no [planejamento](docs/planejamento-capitulo-01.md) e atender às exigências acadêmicas pendentes de tela cheia inicial, Esc, movimento, colisão e áudio antes de expandir a história.

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

**Equipe:** Felipe Nunes Ramalho e Juliana Karla Camargo da Silva — UniSenai, ADS — TADS2, disciplina BackEnd, professor Cainã Antunes.

**Referência didática:** [ScreenSaver — projeto-base do professor](https://github.com/ProfCainaAntunes/backend_screenSaver_solved), usado como referência para os conceitos de POO e a comparação de recursos na [documentação acadêmica](docs/documentacao-academica.md).

A distribuição individual de autoria dos recursos e a licença de distribuição ainda precisam ser registradas. Até que uma licença seja adicionada, não presuma autorização para redistribuir o código ou os recursos gráficos.

---

<div align="center">

*“O destino de uma alma não deveria pertencer à mão que segura a pena.”*

**A Biblioteca das Almas — Capítulo I**

</div>
