# Testes do Capítulo I — A Biblioteca das Almas

Referência: 30/10/2026. Este documento contém procedimentos reproduzíveis, critérios de aceitação e um registro de execução. A história oficial está em [rumos-da-historia.md](rumos-da-historia.md); os comportamentos técnicos esperados estão no [contrato narrativo](contrato-narrativo-json.md).

**A abertura das cenas 01–04 tem testes de lógica e interface; o capítulo inteiro ainda depende de integração.** Um caso pendente não deve ser registrado como aprovado só porque o roteiro ou a imagem correspondente existe.

## 1. Estados dos casos e evidências

| Estado | Uso |
| --- | --- |
| **Aprovado** | Procedimento executado e resultado esperado observado na versão registrada. |
| **Reprovado** | Procedimento executado com divergência; registrar a falha e a evidência. |
| **Pendente** | Ainda não executado. Informar a funcionalidade necessária quando ela não estiver integrada. |

Cada execução deve registrar data, commit, estado local relevante, sistema/JDK, caso, passos, resultado e evidência. Imagens ajudam a conferir composição, mas sua geração automática não comprova uma revisão visual humana.

Preferir testes da lógica para requisitos, efeitos e persistência; usar testes de interface e partidas para apresentação e navegação. Não é necessário testar cada fala isoladamente nem criar testes que apenas copiem a implementação.

## 2. Preparação

1. Usar a raiz do repositório e JDK 21. Consultar o [README](../README.md) para execução geral.
2. Registrar o commit e alterações locais antes de validar uma entrega.
3. Separar saídas de compilação e evidências em uma pasta ignorada, como `target/validacao-documentacao/`.
4. Usar um diretório temporário de saves. `SaveManager(Path)` permite isso e `MenuSmokeTest` já o faz. Não apagar ou sobrescrever saves pessoais para preparar os testes.
5. Na futura bateria narrativa, criar estados de teste controlados por `GameState` ou fixtures isoladas. Não modificar o roteiro oficial ou saves pessoais para simular requisitos ausentes.

O teste existente renderiza sem abrir janela. Tela cheia, foco real do teclado e interação visual completa exigem validação manual em ambiente gráfico.

## 3. Procedimentos disponíveis — Carregador, estado e protótipo

Os quatro testes são executados por métodos `main`, com verificações próprias. `mvn test` isoladamente não comprova sua execução; o build compila os testes, mas eles devem ser chamados explicitamente.

### Compilação e execução com Maven

Conferir que `mvn -version` indica JDK 21, inclusive o `JAVA_HOME` usado pelo Maven. No PowerShell, a partir da raiz:

```powershell
mvn package
if ($LASTEXITCODE -ne 0) { throw 'Falha no build.' }

$classpathTeste = 'target/test-classes;target/biblioteca-das-almas-menu-1.0.0.jar'
java -cp $classpathTeste br.com.bibliotecadasalmas.StoryLoaderTest
if ($LASTEXITCODE -ne 0) { throw 'StoryLoaderTest falhou.' }
java -cp $classpathTeste br.com.bibliotecadasalmas.GameStateTest
if ($LASTEXITCODE -ne 0) { throw 'GameStateTest falhou.' }
java '-Djava.awt.headless=true' -cp $classpathTeste br.com.bibliotecadasalmas.OpeningGameplayTest
if ($LASTEXITCODE -ne 0) { throw 'OpeningGameplayTest falhou.' }
java '-Djava.awt.headless=true' -cp $classpathTeste br.com.bibliotecadasalmas.MenuSmokeTest target/validacao-opening/previews
if ($LASTEXITCODE -ne 0) { throw 'MenuSmokeTest falhou.' }
```

O Maven Shade inclui Gson e os recursos no JAR executável; não é necessário acrescentar `src/main/resources` ou um JAR externo ao classpath desse procedimento. O arquivo em `dist/` continua sendo a distribuição anterior do protótipo.

Para uma validação rápida do conteúdo do JAR, sem janela nem acesso a saves:

```powershell
java -jar target/biblioteca-das-almas-menu-1.0.0.jar --validate-story
if ($LASTEXITCODE -ne 0) { throw 'Roteiro inválido ou ausente do pacote.' }
```

### Alternativa com javac e JAR do Gson

Disponibilizar previamente o JAR do Gson **2.13.2** e ajustar `$gsonJar` ao caminho real. Nesta validação, a dependência usada pelo procedimento manual foi colocada em `target/validacao-story/lib/`; esse diretório é temporário e não acompanha um clone novo.

```powershell
$gsonJar = 'target/validacao-story/lib/gson-2.13.2.jar'
if (-not (Test-Path -LiteralPath $gsonJar)) { throw 'Indique o caminho do JAR do Gson 2.13.2.' }
$saidaClasses = 'target/validacao-story/classes'
$saidaTeste = 'target/validacao-story/previews'
New-Item -ItemType Directory -Path $saidaClasses,$saidaTeste -Force | Out-Null

$fontesJava = @(Get-ChildItem -Path src/main/java,src/test/java -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
javac --release 21 -encoding UTF-8 -cp $gsonJar -d $saidaClasses $fontesJava
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }

$classpathTeste = "$saidaClasses;$gsonJar;src/main/resources"
java -cp $classpathTeste br.com.bibliotecadasalmas.StoryLoaderTest
if ($LASTEXITCODE -ne 0) { throw 'StoryLoaderTest falhou.' }
java -cp $classpathTeste br.com.bibliotecadasalmas.GameStateTest
if ($LASTEXITCODE -ne 0) { throw 'GameStateTest falhou.' }
java '-Djava.awt.headless=true' -cp $classpathTeste br.com.bibliotecadasalmas.OpeningGameplayTest
if ($LASTEXITCODE -ne 0) { throw 'OpeningGameplayTest falhou.' }
java '-Djava.awt.headless=true' -cp $classpathTeste br.com.bibliotecadasalmas.MenuSmokeTest $saidaTeste
if ($LASTEXITCODE -ne 0) { throw 'MenuSmokeTest falhou.' }
```

A compilação conjunta e a execução manual foram verificadas. Na execução realizada, a enumeração das fontes usou `rg --files`; o exemplo usa PowerShell para não exigir essa ferramenta. O JAR não é copiado para o repositório versionado.

### L01 — Carregamento e validação do roteiro real

Fonte: [StoryLoaderTest.java](../src/test/java/br/com/bibliotecadasalmas/StoryLoaderTest.java).

Verifica a leitura das 28 entradas e 620 nós, os sete tipos de nó, os seis ambientes, preservação de texto UTF-8, leitura com BOM, fontes de entrada, conteúdo imutável e criação independente de `GameState`. Avalia as condições dos metadados, da revelação e da decisão final nas 32 combinações e compara com `GameState.canRewritePact()`.

As 44 amostras inválidas desta versão cobrem sintaxe, chaves e IDs duplicados, campos desconhecidos, tipos indevidos, destinos inexistentes, opções sem saída ou com duas saídas, referências a memórias/locais/flags, regras secretas divergentes e ciclos automáticos locais ou entre cenas. As fixtures são cópias em memória ou arquivos temporários; o roteiro oficial não é alterado.

L01 não valida os PNGs ou o manifesto externo, não executa efeitos e não simula uma partida completa. A aceitação de referências é limitada às definições disponíveis no consolidado, conforme o contrato narrativo.

### G01 — Regras do estado em memória

Fonte: [GameStateTest.java](../src/test/java/br/com/bibliotecadasalmas/GameStateTest.java).

Verifica oito grupos: estado inicial e independência das partidas; inventário sem duplicatas e snapshots protegidos; sincronização das três memórias com suas flags nos dois sentidos; escolhas por cena/nó e interações concluídas; navegação condicionada ao desbloqueio; 32 combinações da reescrita; entradas inválidas sem alterações parciais; reinício por Novo Jogo sem perder preferências.

Definir explicitamente uma flag de memória como falsa remove também sua memória; o roteiro atual não usa essa operação para punir personalidade. A política de preservação das descobertas na execução continua pendente de integração.

### A01 — Abertura, retomada e executor

Fonte: [OpeningGameplayTest.java](../src/test/java/br/com/bibliotecadasalmas/OpeningGameplayTest.java).

Percorre as seis combinações de investigar/adiar a carta com as três posturas da porta. Também verifica entrar sem abrir a carta, voltar para recuperá-la, a pausa antes do livro e o limite da cena 04. Recria o controlador em pontos de diálogo e exploração e compara todas as categorias de progresso, incluindo as marcas de efeitos já aplicados.

Exercita inventário modal, oito retratos legíveis pelo `ImageIO`, compatibilidade com `.properties`, saves truncados/incompatíveis/incoerentes e falha de escrita informada. Os fixtures de executor verificam rollback de um lote inválido, condicionais, evento, junção e confirmação/cancelamento. A avaliação dos nós finais nesses fixtures não equivale a uma partida completa na interface.

### P01 — Menu e abertura por mouse/teclado

Fonte: [MenuSmokeTest.java](../src/test/java/br/com/bibliotecadasalmas/MenuSmokeTest.java).

Percorre o trecho JSON usando os listeners reais de mouse e teclado de `GamePanel`, na thread de eventos Swing. Confere revelar/avançar, escolhas, hotspots, bloqueio modal, redimensionamento para 1600 × 1000, barras fora da área lógica, inventário, diário e Continuação com novo controlador. Configurações, créditos e a demonstração legada continuam cobertos. Um relógio fixo evita testes de texto dependentes de esperas.

Gera **25 imagens** da abertura e das telas legadas. A saída começa com `OK: menu e abertura JSON passaram`. Os saves são temporários; o argumento indica apenas a pasta das imagens. Tela cheia e foco real do teclado exigem os casos manuais abaixo. Os finais e os demais cenários não são cobertos como partidas jogáveis.

## 4. Verificações manuais do protótipo

Estado atual dos casos abaixo: **pendente**.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **P02 — Controles** | Abrir a aplicação em ambiente gráfico. Navegar no menu com mouse, setas e Enter; avançar o prólogo com Enter/Espaço; usar Esc. | A seleção respeita controles habilitados; o prólogo avança e Esc retorna ao menu ou fecha a inspeção aberta. |
| **P03 — Janela e tela cheia** | Redimensionar a janela, alternar F11 e interagir com botões nas diferentes posições. | A proporção é preservada, textos permanecem legíveis e o clique corresponde à área desenhada. |
| **P04 — Preferências** | Em uma execução com save isolado, mudar efeitos, velocidade e tela cheia; fechar e reabrir com o mesmo diretório. | As preferências são restauradas. Registrar qualquer falha de exibição ou gravação. |

Esses casos complementam P01. O teste sem janela não aciona nem comprova a alternância real de tela cheia.

## 5. Dados e sistema narrativo

**D01 e N03 aprovados em L01. N01 e N02 têm execução de base aprovada em A01/P01**, com a interface limitada às cenas 01–04. D02 tem mapeamento e leitura das artes da abertura verificados, mas permanece pendente para o catálogo completo.

G01 valida as operações individuais do estado e L01 compara as declarações secretas. A01 acrescenta lotes atômicos, efeitos repetidos, confirmação e retomada; P01 percorre a interface por eventos, sem abrir uma janela. A integração do resto do capítulo continua necessária.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **D01 — Estrutura do roteiro** | Carregar o consolidado. Conferir sintaxe, chaves duplicadas, IDs, referências e contagens. Usar cópias de teste com destinos ou tipos inválidos. | Aceita o roteiro válido; identifica arquivo, cena/nó e campo nas amostras inválidas. Não altera o original. |
| **D02 — Recursos referenciados** | Resolver locais, falantes, expressões e cutscenes pelo manifesto integrado; incluir uma referência inexistente em uma cópia de teste. | Carrega os recursos válidos e diagnostica a referência ausente, sem substituir silenciosamente por outra arte. |
| **N01 — Falas e nós automáticos** | Executar uma sequência com `line`, `join`, `condition`, `event`, `cutscene` e `end`. Revelar texto e avançar por clique. | Falas e cutscenes aguardam avanço; nós automáticos seguem sem cliques extras, sem travar em ciclos. Cliques não ativam hotspots atrás da apresentação. |
| **N02 — Efeitos e consistência** | Investigar a carta, consultar inventário e diário; repetir a interação e simular clique duplo. Usar um lote inválido em fixture separada. | Carta e memória aparecem uma vez, flags ficam coerentes e o lote inválido não aplica alterações parciais. |
| **N03 — Regra secreta** | Avaliar as 32 combinações booleanas das cinco flags oficiais em estados de teste. Comparar metadados, cena 24 e opção de reescrita da cena 25. | Apenas a combinação com cinco valores verdadeiros permite reescrever. As três declarações são equivalentes; postura e `final_secreto_revelado` não viram requisitos adicionais. |

No percurso normal, `memoria_pacto` é concedida obrigatoriamente na cena 16. Sua ausência deve ser testada diretamente na regra, sem criar uma rota narrativa que ignore a revelação principal.

## 6. Partidas completas e finais

Estado atual dos casos abaixo: **pendente de integração do capítulo**. Cada partida começa por Novo Jogo, com progresso isolado. A ordem detalhada das cenas está no roteiro e na direção narrativa oficial.

### R01 — Final I: A Última Página

**Preparação:** partida nova. Para comprovar que o final não depende da investigação completa, adiar carta e fotografia, guardar o medalhão e ignorar a cláusula. Seguir a progressão obrigatória, incluindo o livro de Natasha e a confissão de Rodion.

**Passos:** entrar na Câmara, acompanhar o confronto, selecionar queimar e confirmar. Avançar até o fim do epílogo.

**Resultado esperado:** as almas vinculadas são libertadas e Natasha desaparece. No encerramento, `final_conquistado` vale `queimar`, `capitulo_1_concluido` e `almas_libertas` são verdadeiros. A ausência das pistas opcionais não bloqueia esse caminho.

### R02 — Final II: Entre Páginas Fechadas

**Preparação:** outra partida nova com investigação incompleta, como em R01.

**Passos:** chegar à decisão final, selecionar preservar, confirmar e concluir o epílogo.

**Resultado esperado:** Natasha continua existindo, o pacto permanece e as almas continuam presas. `final_conquistado` vale `preservar` e `capitulo_1_concluido` é verdadeiro. `almas_libertas` e `escriba_derrotado` permanecem falsos no estado dessa partida nova. A narrativa mantém a complexidade do desejo de sobreviver.

### R03 — Final III: A História que Ainda Não Foi Escrita

**Preparação:** partida nova. Investigar a carta e a fotografia, devolver o medalhão, recuperar a memória do pacto e ler a cláusula. Conferir as cinco flags antes de iniciar o confronto.

**Passos:** entrar na Câmara, acompanhar a terceira possibilidade e o testemunho da Fantasma. Selecionar reescrever, confirmar e concluir o epílogo.

**Resultado esperado:** a autoria volta às almas, o Escriba é derrotado e Natasha sobrevive com memórias incompletas. A Fantasma deixa de carregar sozinha as histórias. Conferir quebra do halo e do rosto sem feições, sem acrescentar olhos, nariz ou boca. `final_conquistado` vale `reescrever`; `capitulo_1_concluido`, `almas_libertas` e `escriba_derrotado` são verdadeiros ao final.

A conclusão do capítulo só deve ser registrada no nó final do epílogo. Não marcar o caso como aprovado ao abrir sua primeira cutscene.

## 7. Revisitas, escolhas e confirmação

Estado atual dos casos abaixo: **pendente de integração**.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **R04 — Recuperar pistas ignoradas** | Em uma partida, adiar carta, fotografia e cláusula. Na cena 20, escolher investigar; visitar cada local desbloqueado, obter as pistas e retornar. | Atualiza memórias e requisitos e retoma a escolha da cena 20. Não reinicia atos pelo `next_scene` das cenas revisitadas. |
| **R05 — Confiança reparável** | Recuar diante da Fantasma, guardar o medalhão e avançar. Antes do confronto, retornar e devolvê-lo. | Remove o item guardado e define `medalhao_devolvido` e `fantasma_confia`. A reação inicial não bloqueia o final secreto. |
| **R06 — Entrega imediata** | Em partida nova, devolver o medalhão na primeira oportunidade, sem guardá-lo antes. | A entrega não falha por remover um item ainda ausente. A confiança é concedida e o item não permanece no inventário. |
| **R07 — Repetição após descoberta** | Revisitar carta, fotografia, cláusula e medalhão já devolvido. | Não duplica itens, memórias ou efeitos; não permite guardar o medalhão novamente nem apaga confiança conquistada. |
| **R08 — Cancelar a decisão final** | Na cena 25, selecionar uma opção e cancelar a confirmação; depois selecionar outra e confirmar. | Cancelar mantém a escolha sem efeitos. Somente a opção confirmada inicia seu epílogo. |

R04 deve ser executado com requisitos suficientes para que a coleta posterior das pistas realmente complete as cinco condições. Registrar quais estavam ausentes antes e quais foram obtidas no retorno.

## 8. Salvamento e reinício

Estado atual dos casos narrativos abaixo: **pendente de integração**. O teste P01 cobre apenas o save básico do protótipo.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **S01 — Retomada após efeitos** | Investigar carta ou fotografia, salvar no ponto previsto, encerrar e continuar com o mesmo diretório isolado. | Restaura cenário, cursor, item, memória e flags, sem repetir os efeitos. |
| **S02 — Retomada em revisita** | Na cena 20, abrir exploração, recuperar uma pista, salvar e continuar. Voltar ao confronto. | Preserva a pista e o contexto suspenso; retoma a escolha correta sem reiniciar a história. |
| **S03 — Novo Jogo** | Após uma partida com descobertas ou final, iniciar outra usando o mesmo diretório isolado. | Reinicia progresso, itens, memórias e estados de final, mantendo preferências. Não herda `almas_libertas` do final anterior. |
| **S04 — Save antigo ou inválido** | Usar cópias de teste de um save básico antigo e de um arquivo inválido. Tentar continuar. | Aplica a política documentada de compatibilidade ou recuperação, com mensagem compreensível. Não inventa progresso nem destrói outra partida. |
| **S05 — Falha de gravação** | Usar uma implementação de teste ou destino controlado que simule falha de escrita. | Comunica a falha, sem afirmar salvamento concluído ou deixar estado parcialmente persistido. |

Se o jogo adotar somente pontos seguros de gravação, registrar quais são e ajustar os passos de S01/S02. Não exigir um save no meio de uma fala sem antes implementar essa política.

## 9. Interface e distribuição

Estado atual dos casos do capítulo integrado: **pendente**.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **V01 — Composição e expressões** | Percorrer os quatro personagens, suas expressões, memórias e fontes de texto. Conferir em janela e tela cheia. | Escala e alinhamento coerentes, transparência correta, nomes legíveis e identidade visual preservada. Vozes e documentos não mostram retratos indevidos. |
| **V02 — Hotspots e camadas** | Em cada um dos seis ambientes, clicar em portas, pistas e personagens; repetir com diálogo, escolha e cutscene ativos. Redimensionar a janela. | Navegação respeita desbloqueios, cliques acompanham a imagem e apresentações ativas bloqueiam interações atrás delas. |
| **V03 — Epílogos e fundos** | Executar os três finais e observar as transições, incluindo a chegada à Entrada nos finais que a narram. | Fundos e cutscenes correspondem ao texto; a localização inicial da cena não impede mudanças internas. |
| **V04 — Pacote independente** | Gerar o pacote atualizado, copiá-lo para uma pasta de teste fora da árvore de fontes e executar seguindo o README. | Carrega todos os recursos do capítulo sem acessar os diretórios externos de artes ou fontes. |

Para V04, registrar a origem do artefato: o JAR de `dist/` é uma distribuição do protótipo e não comprova que o código recém-integrado foi empacotado. Conferir o JAR gerado pelo build atual, incluindo dependências necessárias caso o leitor JSON use biblioteca externa.

O [catálogo de recursos](catalogo-de-recursos.md) lista os arquivos conhecidos e as pendências de mapeamento. O teste de pacote deve usar esse inventário como referência e registrar os recursos ausentes.

## 10. Registro de execução desta revisão

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

## 11. Registro de execução — Base GameState

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

## 12. Registro de execução — Carregador JSON

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

## 13. Registro de execução — Primeiro trecho jogável

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

Commit sugerido: `feat: conecta abertura jogavel ao roteiro json`.
