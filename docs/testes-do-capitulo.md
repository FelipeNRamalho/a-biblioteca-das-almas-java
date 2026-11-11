# Testes do Capítulo I — A Biblioteca das Almas

Revisão: **11/11/2026**, commit `389433f`. A [documentação acadêmica](documentacao-academica.md) descreve os requisitos do professor; o [contrato narrativo](contrato-narrativo-json.md) descreve dados e execução.

## 1. Resultados atuais

As fontes principais e os oito testes foram recompilados com **OpenJDK Temurin 21.0.13**, `--release 21 -encoding UTF-8` e Gson 2.13.2. Os oito programas retornaram **código zero**. O Maven não foi usado nesta revisão, pois não estava disponível no PATH.

| Teste existente | Resultado em 11/11 | Alcance da execução |
| --- | --- | --- |
| [StoryLoaderTest](../src/test/java/br/com/bibliotecadasalmas/StoryLoaderTest.java) | Aprovado | 28 cenas, 620 nós, 32 combinações secretas e 44 amostras inválidas. |
| [GameStateTest](../src/test/java/br/com/bibliotecadasalmas/GameStateTest.java) | Aprovado | Oito grupos de regras, incluindo 32 combinações de elegibilidade da reescrita. |
| [OpeningGameplayTest](../src/test/java/br/com/bibliotecadasalmas/OpeningGameplayTest.java) | Aprovado | Seis rotas da abertura, retornos, retomadas, save inválido/legado, falha de gravação, efeitos e confirmação. |
| [ActOneGameplayTest](../src/test/java/br/com/bibliotecadasalmas/ActOneGameplayTest.java) | Aprovado | Nove combinações, Balcão, identidade/retratos de Rodion, navegação e persistência. |
| [ActTwoGameplayTest](../src/test/java/br/com/bibliotecadasalmas/ActTwoGameplayTest.java) | Aprovado | 12 rotas, fotografia, medalhão, confiança, adiamentos, navegação e persistência. |
| [ActThreeGameplayTest](../src/test/java/br/com/bibliotecadasalmas/ActThreeGameplayTest.java) | Aprovado | 12 rotas, Arquivo, cláusula adiada, cinco requisitos, navegação e persistência. |
| [ActFourGameplayTest](../src/test/java/br/com/bibliotecadasalmas/ActFourGameplayTest.java) | Aprovado | R01/R02/R03: três finais; R04/S02: revisita; R08: confirmação/cancelamento; estados de cutscene e saves. |
| [MenuSmokeTest](../src/test/java/br/com/bibliotecadasalmas/MenuSmokeTest.java) | Aprovado | Mouse/teclado da abertura, escolhas, diário, retomada, escala e telas legadas; 25 imagens. |

Os testes dos atos usam controlador e executor, incluindo percursos desde Novo Jogo. Uma asserção sobre um nó de cutscene comprova o estado narrativo, não a inspeção visual de sua arte.

## 2. Evidências da revisão

| Evidência | Local / resultado |
| --- | --- |
| Classes recompiladas | `target/validacao-docs-2026-11-11/classes/` |
| Logs individuais | `target/validacao-docs-2026-11-11/<NomeDoTeste>.log` |
| Resultado consolidado | `target/validacao-docs-2026-11-11/resultados.json`, oito códigos zero. |
| Imagens da interface das fontes | `target/validacao-docs-2026-11-11/previews/`, 25 PNGs. |
| JAR materializado via LFS | `dist/BibliotecaDasAlmas-Menu.jar`, 122.162.585 bytes. |
| Pacote fora da árvore de fontes | Copiado para diretório temporário; `--validate-story` confirmou 28 cenas/620 nós e `--preview` gerou imagem. |
| Prévia do pacote | `target/validacao-docs-2026-11-11/preview-distribuicao.png`, com diretório pessoal isolado. |
| Testes contra o pacote | `ActFourGameplayTest` e `MenuSmokeTest` aprovados usando somente classes de teste e o JAR no classpath. |
| Logs do pacote | `ActFourGameplayTest-pacote.log` e `MenuSmokeTest-pacote.log` na pasta da revisão. |
| Imagens do pacote | `target/validacao-docs-2026-11-11/previews-pacote/`, 25 PNGs. |
| Caminhos de imagens | 41 referências distintas do manifesto encontradas; 71 cabeçalhos PNG catalogados. |

`target/` é ignorado pelo Git e os artefatos são locais, não arquivos garantidos em um clone novo. O caminho temporário do pacote foi registrado em `diretorio-pacote.txt`. Os testes criaram saves temporários e a prévia usou `user.home` isolado.

A conferência do JAR não recompilou nem substituiu a distribuição. Também não registra nova execução de `mvn package`.

## 3. Reproduzir os testes

Os oito testes têm `main` e verificações próprias. `mvn test` isoladamente não comprova que foram chamados.

### 3.1 Maven e execução explícita no PowerShell

Na raiz, com Maven e JDK 21:

```powershell
mvn package
if ($LASTEXITCODE -ne 0) { throw 'Build falhou.' }

$classpathTeste = 'target/test-classes;target/biblioteca-das-almas-menu-1.0.0.jar'
$testes = @(
    'StoryLoaderTest', 'GameStateTest', 'OpeningGameplayTest',
    'ActOneGameplayTest', 'ActTwoGameplayTest',
    'ActThreeGameplayTest', 'ActFourGameplayTest', 'MenuSmokeTest'
)
foreach ($teste in $testes) {
    if ($teste -eq 'MenuSmokeTest') {
        java '-Djava.awt.headless=true' -cp $classpathTeste "br.com.bibliotecadasalmas.$teste" target/validacao-atual/previews
    } else {
        java '-Djava.awt.headless=true' -cp $classpathTeste "br.com.bibliotecadasalmas.$teste"
    }
    if ($LASTEXITCODE -ne 0) { throw "$teste falhou." }
}
```

Em Linux/macOS, o separador de classpath é `:`, em vez de `;`.

### Alternativa com javac e JAR do Gson

Disponibilizar o Gson **2.13.2** e ajustar o caminho; a dependência local em `target/validacao-story/lib` não acompanha um clone novo.

```powershell
$gsonJar = 'target/validacao-story/lib/gson-2.13.2.jar'
$saidaClasses = 'target/validacao-atual/classes'
if (-not (Test-Path -LiteralPath $gsonJar)) { throw 'Indique o JAR do Gson 2.13.2.' }
New-Item -ItemType Directory -Path $saidaClasses -Force | Out-Null
$fontesJava = @(Get-ChildItem -Path src/main/java,src/test/java -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
javac --release 21 -encoding UTF-8 -cp $gsonJar -d $saidaClasses $fontesJava
if ($LASTEXITCODE -ne 0) { throw 'Compilação falhou.' }
$classpathTeste = "$saidaClasses;$gsonJar;src/main/resources"
```

Executar depois o mesmo laço da seção anterior, mantendo o classpath manual. Esse procedimento verifica as fontes; ele não empacota um novo JAR.

### 3.3 Validar a distribuição

```powershell
java -jar dist/BibliotecaDasAlmas-Menu.jar --validate-story
```

Para testar o pacote, compilar **somente** `src/test/java` contra ele e usar as classes de teste mais o JAR no classpath. Incluir `target/classes` antes do JAR testaria as classes locais em seu lugar. Para provar independência da árvore de fontes, executar uma cópia em diretório temporário sem `src/main/resources` no classpath.

O arquivo distribuído usa Git LFS; um ponteiro textual não é executável. Nesse caso, executar `git lfs install` e `git lfs pull`. Os atalhos priorizam o nome acima e aceitam o nome Maven como alternativa quando presente.

## 4. Critérios por funcionalidade

| Casos | Evidência automatizada atual | Conferência adicional |
| --- | --- | --- |
| D01/N03 — JSON e regra secreta | `StoryLoaderTest` e `GameStateTest`. | Catálogos independentes e conteúdo visual. |
| R01/R02/R03 — finais | `ActFourGameplayTest`. | Legibilidade dos epílogos e apresentação dos desfechos. |
| R04 — pistas adiadas e revisita | Testes dos atos II–IV. | Percurso manual com mouse e cliques fora de sequência. |
| R05/R06 — reação à Fantasma e medalhão | `ActTwoGameplayTest`. | Clareza das opções na UI. |
| R07 — revisitas e duplicação | Testes de estado e atos. | Ações repetidas na interface interativa. |
| R08 — confirmar/cancelar | `OpeningGameplayTest` e `ActFourGameplayTest`. | Apresentação modal e foco real. |
| S01/S02 — saves | Testes de abertura e atos. | Retomada pelo menu em ambiente gráfico. |
| S05 — falha de gravação | `OpeningGameplayTest`. | Mensagem compreensível ao jogador. |
| V04 — pacote independente | Validação, prévia e dois testes contra cópia isolada. | Instalação em clone novo e execução interativa em outro computador. |

Os identificadores foram mantidos para ligação com os registros anteriores. Aprovação de casos não constitui medição de cobertura de 100%.

## 5. Pendências e limites

- Conferir foco do teclado, F11, redimensionamento e apresentação dos atos/finais em janela real.
- Conferir textos longos, todas as expressões e transições de fundo nos epílogos.
- Substituir o fundo compartilhado dos quatro ambientes que ainda usam o Salão.
- Atender às exigências acadêmicas ainda ausentes: tela cheia inicial sem bordas, Esc imediato, movimento por setas/WASD/diagonais, colisão e som pré-carregado.
- Conferir Git LFS e acesso de avaliação a partir de um clone novo.
- Revisar créditos, divisão da dupla e ferramentas utilizadas.

Não há áudio integrado nem testes de movimento/colisão. Gerar prévias sem janela não comprova esses requisitos. A interface automatizada cobre principalmente abertura e legado; os testes dos atos cobrem regras e fluxo do controlador.

## 6. Registros históricos herdados

Os registros a seguir documentam versões e execuções anteriores, inclusive relatos recebidos no commit `389433f`. Expressões como “atual”, “pendente”, “100%” e “completo” nesses registros pertencem à data/base original. A seção 1 e os limites da seção 5 prevalecem para a revisão presente. Afirmações históricas de cobertura ou atomicidade não são uma medição nova.

### Histórico 10 — Registro de execução desta revisão

| Campo | Registro |
| --- | --- |
| Data | 30/10/2026 |
| Código de referência | Commit `b7b76ce`; somente novos documentos locais em elaboração, sem mudanças nas fontes Java. |
| Ambiente | Windows, PowerShell, OpenJDK/Temurin 21.0.13, `javac` 21.0.13. |
| Procedimento executado | Compilação conjunta das fontes principais e de teste com `javac --release 21 -encoding UTF-8`; execução de `MenuSmokeTest` em modo sem janela. |
| Caso e resultado | **P01 aprovado**: processo encerrado com código 0 e mensagem de sucesso. |
| Evidências locais | Nove PNGs em `target/validacao-documentacao/previews/`, fora do versionamento. |
| Persistência usada | Diretório temporário criado pelo próprio teste, sem usar o save pessoal. |
| Limites | Sem revisão visual das nove imagens, sem teste manual de janela/tela cheia, sem execução com Maven e sem partidas do capítulo integrado. |

Imagens produzidas: `menu.png`, `intro.png`, `exploracao.png`, `inspecao.png`, `menu_com_salvamento.png`, `menu_antes_config.png`, `configuracoes.png`, `menu_depois_config.png` e `creditos.png`.

A primeira tentativa de compilação em duas etapas não resolveu as classes principais no classpath de teste deste ambiente. A validação acima utilizou a compilação conjunta. Isso não foi tratado como falha funcional do jogo e não exigiu alteração das fontes.

Para novas execuções, acrescentar um registro com versão e evidências, mantendo os resultados anteriores identificados. O histórico não deve ser sobrescrito como se todos os casos tivessem sido executados novamente.

### Histórico 11 — Registro de execução — Base GameState

| Campo | Registro |
| --- | --- |
| Data | 30/10/2026 |
| Código de referência | Base `a01adb6` com `GameState`, `GameStateTest` e integração local de `GameController.newGame()`. |
| Ambiente | Windows, PowerShell e JDK 21.0.13; sem Maven disponível. |
| Compilação | Fontes principais e testes compilados em conjunto com `javac --release 21 -encoding UTF-8`, para `target/validacao-gamestate/classes/`. |
| G01 | **Aprovado**: oito grupos de verificações e todas as 32 combinações das condições da reescrita. |
| P01 | **Aprovado após a alteração do controlador**: fluxo do protótipo e geração de nove PNGs em `target/validacao-gamestate/previews/`. |
| Persistência usada | Diretórios temporários dos testes, sem usar o save pessoal. O novo `GameState` ainda não é persistido. |
| Limites | Sem execução do roteiro JSON, lotes de efeitos, revisitas ou partidas completas; sem revisão visual das imagens ou teste manual de tela cheia. |

Comandos para reproduzir esse registro: usar a compilação conjunta da seção 3 com destino `target/validacao-gamestate/classes`; executar G01 com esse classpath e P01 com `target/validacao-gamestate/classes;src/main/resources`, indicando `target/validacao-gamestate/previews` para as imagens.

### Histórico 12 — Registro de execução — Carregador JSON

| Campo | Registro |
| --- | --- |
| Data e base | 30/10/2026, base `86e47f3` com a implementação local do carregador. |
| Ambiente | Windows, PowerShell, JDK 21.0.13 e Maven 3.9.14 localizado no cache de wrapper, fora do PATH. |
| Build | `mvn package` executado com esse Maven, JDK 21 e repositório de dependências em `target/maven-repository/`; **BUILD SUCCESS**. |
| L01 / D01 / N03 | **Aprovados**: leitura das 28 entradas/620 nós, conteúdo imutável, 44 amostras inválidas e 32 combinações comparando as três declarações do JSON com `GameState`. |
| G01 | **Aprovado**, sem alterações nas regras do estado. |
| P01 | **Aprovado**, com nove imagens em `target/validacao-story/previews/`; repetido usando o JAR Maven, com imagens em `target/validacao-story/previews-jar/`. |
| Pacote | O JAR `target/biblioteca-das-almas-menu-1.0.0.jar` inclui Gson e o roteiro. Os três testes passaram com `target/test-classes` e esse JAR, sem `src/main/resources` no classpath. |
| Fora da árvore de fontes | Uma cópia do JAR em pasta temporária executou `java -jar biblioteca.jar --validate-story`, confirmando 28 cenas e 620 nós sem janela ou acesso aos saves. |
| Roteiro | Movido da raiz para `src/main/resources/story/roteiro_completo.json`; SHA-256 anterior e posterior idênticos. Nenhuma fala, escolha ou condição foi modificada. |
| Limites | Sem executor de nós/efeitos, sem diálogos JSON na UI, sem verificação dos manifestos/PNGs externos e sem partidas completas. V04 continua pendente para o capítulo integrado. |

O build inicial encontrou outro `JAVA_HOME`; a validação foi concluída selecionando JDK 21 apenas no processo de build. O JDK também exigiu execução fora do sandbox para resolver o JAR no classpath. Nenhuma configuração global foi alterada.

SHA-256 preservado do consolidado: `95A23FE0CCF0AE8A4A8B6F52CAAAAF01B5D15276DF94AC75920BB1306ADE0DFA`.

### Histórico 13 — Registro de execução — Primeiro trecho jogável

| Campo | Registro |
| --- | --- |
| Base | `0770b50` com a integração local da abertura. |
| Escopo | Cenas 01–04, Entrada e Salão, oito expressões de Natasha, carta, Memória 1, escolhas da porta e livro mutável. |
| Ambiente | JDK 21.0.13 e Maven 3.9.14, com cache em `target/maven-repository/`. |
| Build | `mvn package` concluído; os métodos `main` dos testes foram executados explicitamente. |
| A01 | **Aprovado**: seis combinações da abertura, porta direta, retornos, retomadas, arquivos inválidos, formato legado, falha de gravação, efeitos atômicos e confirmação. |
| P01 | **Aprovado**: fluxo por mouse/teclado na EDT e 25 imagens em `target/validacao-opening/previews-final/`. |
| Regressões | `StoryLoaderTest` e `GameStateTest` aprovados, mantendo 28 entradas, 620 nós, 44 amostras inválidas e as 32 combinações secretas. |
| Pacote | Os quatro testes passaram usando uma cópia do JAR fora da árvore de fontes, com somente as classes de teste e o JAR no classpath. `--validate-story` também passou. |
| Saves | Pastas temporárias dos testes; nenhum save pessoal foi alterado. O formato narrativo 1 restaura a abertura sem reaplicar efeitos. |
| Conferência visual | Entrada, retrato, escolha da porta, inventário, diário e Salão conferidos por imagens renderizadas. Ajustados os espaços entre texto e ornamentos. |
| Limites | Sem janela interativa, teste de F11/foco real, Balcão ou atos posteriores. A confirmação final foi exercitada no executor isolado, sem liberar os finais na interface. |

As telas de consulta são fechadas na retomada e o texto da fala volta a ser revelado; progresso e consequências permanecem. O contexto de exploração da cena 20 não está integrado ou persistido por esse trecho.

O roteiro consolidado foi preservado. Os PNGs foram copiados dos pacotes existentes, sem edição. Os atalhos e o JAR antigo de `dist/` não foram substituídos; executar o JAR de `target/` para jogar a abertura atual.

### Histórico 14 — Registro de execução — Atos I, II e III integrados

| Campo | Registro |
| --- | --- |
| Data | 10/11/2026 |
| Escopo | Ato I (cenas 05–07 e Balcão de Rodion), Ato II (cenas 08–12 e Corredor dos Esquecidos), Ato III (cenas 13–19 e Arquivo Proibido). |
| Ambiente | Windows, PowerShell, JDK 21/27. |
| Testes de Ato | `ActOneGameplayTest`, `ActTwoGameplayTest`, `ActThreeGameplayTest` todos **Aprovados**. |
| Cobertura | 9 combinações no Ato I, 12 rotas no Ato II (incluindo R04, R05, R06, R07), 12 rotas no Ato III, recuperação de pistas adiadas, verificação rigorosa das 5 condições da reescrita (`canRewritePact()`), navegação nos 5 ambientes e persistência. |
| Regressões | `StoryLoaderTest`, `GameStateTest`, `OpeningGameplayTest` e `MenuSmokeTest` permanecem **Aprovados**. |

### Histórico 15 — Registro de execução — Ato IV e Epílogos integrados (Capítulo I Completo)

| Campo | Registro |
| --- | --- |
| Data | 10/11/2026 |
| Escopo | Ato IV (cenas 20–25 e Câmara dos Registros), Epílogos (três versões da cena 26), confrontos, cutscenes e desfechos. |
| Ambiente | Windows, PowerShell, OpenJDK 27 (`--release 21`), Gson 2.13.2. |
| Teste de Ato IV | `ActFourGameplayTest` **Aprovado** com código de saída 0. |
| Casos Oficiais Cobertos | **R01**: Final I — Queimar Livro (sacrifício de Natasha, libertação das almas, `almas_libertas = true`, `capitulo_1_concluido = true`).<br>**R02**: Final II — Preservar Livro (pacto inalterado, almas aprisionadas, Natasha sobrevive).<br>**R03**: Final III — Reescrever Pacto (5 condições verificadas estritamente por `canRewritePact()`, revelação de `final_secreto_revelado`, Escriba derrotado, `escriba_derrotado = true`, `almas_libertas = true`).<br>**R04 e S02**: Investigação adiada e revisita na cena 20 via `open_exploration`, salvamento em disco no ponto de exploração, coleta de pistas pendentes em múltiplos ambientes, devolução reparatória do medalhão, persistência de contexto e retomada exata na escolha da cena 20 sem reinício da história.<br>**R08**: Cancelamento e confirmação modal da decisão final na cena 25 (seleção de opção, cancelamento e manutenção na escolha sem iniciar epílogo).<br>**S01 e S02**: Salvamento e restauração íntegros durante diálogo na Câmara (c21), com escolha pendente em c25 e durante o epílogo em c26.<br>Cutscenes oficiais integradas: `escriba_primeira_aparicao`, `escriba_apagando_alma`, `escriba_confronto`, `final_queimar`, `final_preservar`, `final_reescrever` e `escriba_derrota_final`. |
| Regressão Completa | **8/8 suítes Aprovadas**: `StoryLoaderTest`, `GameStateTest`, `OpeningGameplayTest`, `ActOneGameplayTest`, `ActTwoGameplayTest`, `ActThreeGameplayTest`, `ActFourGameplayTest`, `MenuSmokeTest`. |
| Status do Capítulo | **Marco 2 Entregue**: Capítulo I jogável de ponta a ponta com todos os atos, rotas, desfechos e persistência. |
