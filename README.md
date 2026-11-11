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

> **Estado do desenvolvimento:** as **cenas 01–04 já são jogáveis**, do despertar à investigação do livro no Salão, com diálogos JSON, escolhas, retratos, retorno à carta, inventário, Memória 1 e salvamento. O restante do capítulo e os finais ainda precisam ser integrados. Veja [Andamento do desenvolvimento](#andamento-do-desenvolvimento).

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

O terceiro final exige: memória da carta, memória da fotografia, memória do pacto, confiança da Fantasma e descoberta da cláusula de reescrita. Essas condições são previstas no roteiro JSON e **ainda não estão implementadas como jogabilidade no menu atual**.

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

Para jogar o trecho atualizado, compile e execute o JAR atual, ou execute `Game` pela IDE:

```powershell
mvn package
java -jar .\target\biblioteca-das-almas-menu-1.0.0.jar
```

Os atalhos e o JAR em `dist/` abaixo continuam sendo a distribuição anterior do protótipo; não foram substituídos pelo build atual.

1. Extraia os arquivos do projeto em uma pasta.
2. Abra a pasta que contém `INICIAR_WINDOWS.bat` e `dist/`.
3. Dê dois cliques em **`INICIAR_WINDOWS.bat`**.

Se preferir executar pelo PowerShell, na raiz do projeto:

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

### VS Code ou IntelliJ IDEA

1. Abra a pasta do projeto que contém `pom.xml`.
2. Configure o **JDK 21**.
3. Importe o projeto como **Maven**.
4. Execute a classe principal `br.com.bibliotecadasalmas.Game`.

Para compilar pelo terminal, caso o Maven esteja instalado:

```bash
mvn clean package
```

O JAR gerado pelo Maven ficará em `target/`. O arquivo executável distribuído com o protótipo está em `dist/`.

Para carregar e validar o roteiro sem abrir uma janela ou alterar saves:

```powershell
java -jar target/biblioteca-das-almas-menu-1.0.0.jar --validate-story
```

A saída esperada confirma **28 cenas e 620 nós**, sem iniciar a partida. Novo Jogo usa esse mesmo consolidado para executar as cenas 01–04. O carregador também oferece `load(Path)` e `load(Reader, fonte)`. Os testes explícitos estão em [Testes do capítulo](docs/testes-do-capitulo.md).

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
| Navegação por teclado/mouse e modo tela cheia | **Implementado** |
| Despertar, carta, portas e livro mutável — cenas 01–04 | **Jogáveis pelo roteiro JSON** |
| Entrada e Salão com hotspots, desbloqueio e retorno | **Implementado no primeiro trecho** |
| Preferências e salvamento básico em `.properties` | **Implementado** |
| `GameState`: inventário, memórias, flags, escolhas e cursor narrativo | **Implementado em memória; reiniciado por Novo Jogo** |
| Artes da abertura | **Entrada, despertar e oito expressões de Natasha incorporados; demais pacotes ainda parciais** |
| Roteiro consolidado em `src/main/resources/story/roteiro_completo.json` | **Incorporado ao classpath, com conteúdo preservado** |
| Carregamento JSON, modelos tipados e validação de referências | **Implementado; validável com `--validate-story`** |
| Execução de diálogos, escolhas e efeitos na interface | **Implementado e validado nas cenas 01–04** |
| Exploração definitiva dos seis ambientes e hotspots | **Pendente** |
| Inventário, Memória 1 e escolhas persistentes | **Implementado no primeiro trecho; demais conteúdos pendentes** |
| Lógica jogável dos três finais | **Pendente** |
| Testes integrados do capítulo completo | **Pendente** |

**Escopo disponível:** Novo Jogo executa o despertar e libera a Entrada para investigação. A carta pode ser adiada e recuperada depois, voltando do Salão. A porta oferece as três posturas oficiais; o Salão aguarda um clique no livro para concluir a cena 04. O trecho termina antes do encontro no Balcão. A voz de Rodion ainda aparece como “Uma voz”, sem retrato ou apresentação antecipada.

`NarrativeSession` executa os modelos do carregador sobre `GameState`, sem colocar falas no Java. `trecho_inicial.json` configura fundos, retratos, hotspots, pausas e documentos da abertura. É um manifesto parcial; a integração dos seis ambientes e dos manifestos completos continua pendente. Veja as seis combinações de escolhas e os testes por clique em [Testes do capítulo](docs/testes-do-capitulo.md).

## Organização do projeto

Estrutura do **projeto Java executável atual**, com a pasta `story/` indicada como **integração futura**:

```text
A_Biblioteca_das_Almas_Menu/
├── pom.xml
├── README.md
├── INICIAR_WINDOWS.bat
├── INICIAR_LINUX_MAC.sh
├── dist/
│   └── BibliotecaDasAlmas-Menu.jar
├── preview_menu.png
└── src/
    ├── main/
    │   ├── java/br/com/bibliotecadasalmas/
    │   │   ├── Game.java
    │   │   ├── assets/
    │   │   │   └── AssetManager.java
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
    │       │   ├── menu/
    │       │   ├── cenarios/
    │       │   ├── personagens/natasha/
    │       │   └── ui/
    │       └── story/
    │           ├── roteiro_completo.json
    │           └── trecho_inicial.json
    └── test/java/
        └── br/com/bibliotecadasalmas/
            ├── GameStateTest.java
            ├── MenuSmokeTest.java
            ├── OpeningGameplayTest.java
            └── StoryLoaderTest.java
```

### Pacote narrativo JSON

O [roteiro consolidado](src/main/resources/story/roteiro_completo.json) foi movido da raiz para `src/main/resources/story/`, sem alterar seu conteúdo e sem manter cópias divergentes. Ele é a entrada atual do carregador. Os arquivos complementares do pacote produzido separadamente ainda precisam ser localizados e integrados; a estrutura prevista é:

```text
story/
├── index.json
├── roteiro_completo.json
├── estado_inicial.json
├── assets.json
├── interacoes_mundo.json
├── esquema_cena.schema.json
└── cenas/
    ├── 00_Prologo/
    ├── 01_Ato_I/
    ├── 02_Ato_II/
    ├── 03_Ato_III/
    ├── 04_Ato_IV/
    └── 05_Finais/
```

`index.json` define a sequência das cenas; `estado_inicial.json`, o estado inicial; `assets.json`, as referências de imagens; e cada arquivo em `cenas/` contém nós de diálogo, escolhas e eventos. Os hotspots ainda precisam de coordenadas definitivas, e um gerenciador Java deverá interpretar esses dados.

> **Importante:** o consolidado contém o capítulo inteiro; a configuração `trecho_inicial.json` libera apenas as cenas 01–04 na interface. O carregador não concede itens: os efeitos são aplicados pelo executor conforme as ações do jogador. Fundos, retratos e hotspots desse trecho são conferidos por `OpeningContent`; os manifestos completos continuam pendentes.

## Arquitetura e orientação a objetos

O projeto utiliza os quatro pilares da Programação Orientada a Objetos (POO) como base de organização:

| Pilar | Aplicação no código |
| --- | --- |
| **Encapsulamento** | `GameState` protege progresso e coleções, sincroniza memórias e flags e oferece alterações controladas. `GameController`, `GameSettings`, `SaveManager` e `AssetManager` concentram suas demais responsabilidades. |
| **Abstração** | `ClickTarget` define um contrato para os elementos interativos da interface. |
| **Herança** | `GothicButton` e `TextOption` reutilizam comportamentos da superclasse `AbstractControl`. |
| **Polimorfismo** | Controles distintos respondem ao mesmo contrato de desenho e interação, cada um com comportamento próprio. |

**Expansão planejada:** adicionar classes de personagens, cenários, objetos interativos e um sistema narrativo orientado a eventos, sem concentrar todo o jogo em uma única classe.

### Responsabilidades principais

- **`Game`** — inicialização da aplicação e da janela.
- **`GamePanel`** — desenho das telas com `Graphics2D` e tratamento das interações gráficas.
- **`GameController`** — fluxo do menu, prólogo e demonstração de exploração.
- **`GameState`** — progresso narrativo em memória, com inventário sem duplicatas, memórias, escolhas, locais desbloqueados e consulta dos requisitos da reescrita.
- **`StoryLoader` / `StoryValidator`** — leitura estrita do consolidado, construção de modelos imutáveis e validação de referências, ciclos automáticos e condições secretas.
- **`NarrativeSession`** — execução de nós, condições e escolhas, com lotes de efeitos sobre uma cópia do estado e marcas de aplicação.
- **`StoryView` / `OpeningContent`** — apresentação do trecho e configuração JSON das artes, interações e documentos.
- **`AssetManager`** — carregamento e cache de imagens do classpath.
- **`SaveManager` / `StoryProgress`** — preferências e saves antigos em `.properties`, além do progresso narrativo versionado em `historia.json`.
- **`GothicButton` / `TextOption`** — elementos de interface reutilizáveis.

### Salvamento local

O protótipo guarda dados em arquivos `.properties` no diretório pessoal do usuário:

```text
Windows: %USERPROFILE%\.biblioteca-das-almas\
Linux/macOS: ~/.biblioteca-das-almas/
```

Novas partidas usam `historia.json`, gravado automaticamente após cada ação narrativa concluída. O save registra roteiro/versão, cenário, cena/nó, diálogo ativo, escolhas, flags, valores, itens, memórias, locais liberados e interações concluídas. A gravação usa um temporário antes de substituir o ponto anterior; falhas são informadas na interface.

Continuar restaura o trecho sem reaplicar efeitos. Uma fala em andamento recomeça sua exibição, sem perder a escolha já feita. Inventário e diário fecham na retomada. Saves narrativos inválidos ou incompatíveis são preservados e geram aviso; arquivos antigos em `.properties` continuam abrindo o fluxo demonstrativo legado quando não há `historia.json`. O salvamento dos retornos e contextos dos atos posteriores ainda precisa ser ampliado e validado.

## Direção artística

O estilo visual combina arquitetura **gótica vitoriana**, ilustração **2D com acabamento pictórico**, fantasia sombria e elementos de manuscritos antigos. A paleta prioriza **preto, vinho, malva, azul crepuscular e dourado envelhecido**.

Os personagens possuem retratos com diferentes expressões; as cenas especiais são ilustradas separadamente. As caixas de diálogo, molduras, ícones e ornamentos ficam em camadas independentes, com os textos renderizados pelo próprio Java.

Os recursos gráficos estão organizados em pacotes de personagens, cenários, finais e interface. **Nem todos os PNGs produzidos estão incluídos no protótipo distribuído neste repositório.** Ao integrá-los, os caminhos deverão corresponder às referências de `story/assets.json`.

## Próximas etapas

1. Integrar o **Balcão de Rodion**, cenas 05–07, aproveitando a base da abertura.
2. Ampliar mundo, retratos e documentos para o Corredor, Arquivo e Câmara, incluindo as outras memórias e a confiança da Fantasma.
3. Persistir e validar os contextos de retorno dos atos posteriores, especialmente a exploração anterior ao confronto.
4. Integrar as cutscenes restantes e os **três finais**, preservando as cinco condições secretas.
5. Executar partidas completas e revisar legibilidade, tela cheia, sons e transições conforme o [planejamento](docs/planejamento-capitulo-01.md).

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
