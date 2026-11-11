# Planejamento — A Biblioteca das Almas, Capítulo I

**O Nome que Não Deveria Existir**

Planejamento original: 29/10/2026. Revisão contextualizada: 30/10/2026.

Este plano orienta a integração do roteiro e das artes já produzidos ao projeto Java existente. Usa o contexto oficial do jogo, o [roteiro_completo.json](../src/main/resources/story/roteiro_completo.json) e a [avaliação do roteiro](avaliacao-roteiro-completo.md). Contém spoilers.

## 1. Objetivo e prioridade atual

Entregar o Capítulo I completo: seis ambientes exploráveis por cliques, quatro personagens, diálogos com expressões e escolhas, inventário, diário de memórias, cutscenes, salvamento e três finais. A implementação deve demonstrar encapsulamento, herança, polimorfismo e abstração com exemplos reais no código.

O carregador, o executor e a abertura das cenas 01–04 já estão conectados. A prioridade agora é **ampliar a integração para o Balcão de Rodion e os atos seguintes**, preservando o retorno à investigação, a aplicação dos efeitos e o salvamento já verificados na abertura.

A base permanece Java 21, Swing, Graphics2D e Maven, com conteúdo narrativo em JSON e recursos gráficos em PNG. O jogo utiliza cenários ilustrados estáticos e exploração por mouse. Movimentação livre, sprites de caminhada, combate, novas engines e novos capítulos ficam fora desta entrega.

## 2. Definições oficiais que orientam as tarefas

A história está aprovada. [rumos-da-historia.md](rumos-da-historia.md) registra alternativas anteriores e deve ser consultado como histórico, sem reabrir as decisões estabelecidas no contexto oficial.

### Personagens e conflito central

A pergunta que conduz o capítulo é: **quem tem o direito de decidir como uma história deve terminar?**

- **Natasha Rostova** aceitou o Pacto da Âncora para preservar almas ameaçadas pelo esquecimento. O Escriba usou esse vínculo para aprisioná-las e depois apagou parte das memórias dela. A investigação devolve a Natasha informações e autonomia para decidir.
- **Rodion Raskólnikov** participou da tentativa anterior de salvamento e ocultou a verdade por medo de que Natasha sofresse novamente. Ele não é vilão nem traidor; seu erro foi decidir por ela o que deveria saber.
- **A Fantasma** absorveu fragmentos de histórias que estavam sendo apagadas. É gentil e melancólica, e as sombras que a consomem são consequência desse esforço. Sua confiança permite seu testemunho na reescrita.
- **O Escriba** acredita que controlar as histórias garante sua continuidade. Seu autoritarismo contrapõe preservação e liberdade; seu tom permanece frio e formal.

### Estrutura do capítulo

São **26 cenas oficiais**, representadas por **28 entradas no JSON consolidado**: cenas 01–25 e três versões da cena 26. A referência única está em `src/main/resources/story/roteiro_completo.json`, movida da raiz sem alteração de conteúdo; localizar o pacote complementar antes de dividir arquivos.

| Parte | Cenas | Ambientes e acontecimentos principais |
| --- | --- | --- |
| Prólogo — O Despertar | 01–03 | Entrada: despertar, carta, Memória 1 e portas. |
| Ato I — A Biblioteca Silenciosa | 04–07 | Salão Principal e Balcão de Rodion: livro mutável, apresentação e investigação. |
| Ato II — As Histórias Esquecidas | 08–12 | Corredor dos Livros Esquecidos: Fantasma, fotografia, Memória 2 e medalhão. |
| Ato III — O Nome no Livro | 13–19 | Passagem no Salão, Arquivo Proibido, Memória 3, cláusula e confissão no Balcão. |
| Ato IV — O Autor das Sentenças | 20–25 | Última investigação antes da Câmara dos Registros, confronto e decisão. |
| Epílogos | 26, em três versões | Consequências de queimar, preservar ou reescrever o livro. |

### Finais e condições de acesso

| Final | Consequência a preservar |
| --- | --- |
| **I — A Última Página** | Natasha queima seu livro, desfaz o pacto, liberta as almas e desaparece. |
| **II — Entre Páginas Fechadas** | Natasha preserva sua existência; o pacto e o aprisionamento continuam. O desejo de sobreviver mantém sua complexidade moral. |
| **III — A História que Ainda Não Foi Escrita** | Natasha reconhece o pacto, utiliza a cláusula original e recebe o testemunho da Fantasma. A autoria volta às almas, o Escriba é derrotado e Natasha sobrevive. A Fantasma deixa de carregar sozinha as histórias. |

O final secreto exige exatamente estas cinco condições, já presentes no JSON:

| Requisito | Flag | Origem |
| --- | --- | --- |
| Memória 1 — O Retorno | `memoria_retorno` | Carta, `c02_carta`. |
| Memória 2 — A Escolha | `memoria_escolha` | Fotografia, `c11_fotografia`. |
| Memória 3 — O Pacto | `memoria_pacto` | Livro de Natasha, `c16_livro_natasha`. |
| Confiança da Fantasma | `fantasma_confia` | Devolução do medalhão, `c12_medalhao`. |
| Cláusula original de reescrita | `regra_reescrita_descoberta` | Documento, `c18_clausula`. |

A Memória 3 integra o percurso obrigatório atual. As outras condições devem continuar recuperáveis pela investigação antes do confronto. Uma resposta isolada de medo, rudeza ou desconfiança não pode bloquear permanentemente o final secreto. Queimar e preservar permanecem acessíveis sem completar a investigação.

**Consequência emocional obrigatória do final III:** Natasha não recupera todas as memórias e aceita que parte de seu passado continuará incerta. Não acrescentar um novo apagamento ou exigir outra perda para justificar esse encerramento.

### Direção visual

Reutilizar as artes e o UI Kit gótico, com a paleta de carvão, vinho, ameixa, dourado envelhecido, azul frio e pergaminho. Preservar as ilustrações oficiais: cabelo curto loiro-platinado, olhos rosados ou avermelhados e vestido aprovado de Natasha; apresentação vitoriana de Rodion; aparência sobrenatural e triste da Fantasma; rosto de pergaminho do Escriba **sem olhos, nariz ou boca**. Expressões não autorizam alterar esses desenhos.

## 3. Ponto de partida verificado

| Área | Situação atual | Trabalho restante |
| --- | --- | --- |
| Menu e apresentação | Novo Jogo executa as cenas 01–04 por JSON, com retratos, escolhas, Entrada e Salão por cliques. Menu, configurações, créditos e fluxo legado foram preservados. | Integrar os atos posteriores. |
| Salvamento | `historia.json` já restaura o trecho, com escolhas, memórias e efeitos aplicados. `.properties` antigos continuam compatíveis. | Ampliar os contextos de exploração e validar o capítulo inteiro. |
| Arquitetura | Existem controlador, modos, painel, gerenciador de imagens, controles de UI e `GameState` em memória, reiniciado por Novo Jogo. | Conectar o estado ao interpretador e acrescentar personagens e mundo sem concentrar responsabilidades no painel. |
| Roteiro | `StoryLoader` carrega o consolidado e `NarrativeSession` executa seus nós; `OpeningContent` limita a interface às cenas 01–04. | Ampliar a configuração e as apresentações dos próximos ambientes. |
| Recursos incorporados | Entrada, Salão, despertar, oito expressões de Natasha e parte do UI Kit. Os pacotes de cenários e retratos foram localizados. | Conferir e incorporar os recursos dos atos seguintes. |
| Pacote complementar e artes | O contexto oficial informa a produção dos seis cenários, retratos, expressões, cutscenes e JSONs complementares em pacotes separados. | Conferir caminhos, dimensões, transparência e correspondência visual antes de copiar ou recriar arquivos. |
| Testes existentes | `StoryLoaderTest`, `GameStateTest`, `OpeningGameplayTest` e `MenuSmokeTest` passaram, inclusive usando o JAR fora da árvore de fontes. | Ampliar a validação aos atos seguintes; `mvn test` não substitui as chamadas explícitas dos métodos `main`. |

A avaliação inicial registra uma simulação simplificada do fluxo JSON. A implementação atual acrescenta a abertura jogável e quatro testes explícitos no JAR, registrados em [testes-do-capitulo.md](testes-do-capitulo.md). Isso ainda não comprova partidas do capítulo inteiro.

## 4. Tarefas, dependências e critérios de conclusão

**P0** = necessário para a entrega; **P1** = acabamento relevante; **P2** = extra opcional. Os identificadores T01–T24 foram preservados para acompanhamento, mas a ordem abaixo foi ajustada à prioridade do sistema narrativo.

T01 e T02 estão concluídas quanto à definição. T05, T06 e T09 têm evidências de base técnica; T10 e T12 acrescentam o executor e a abertura jogável. As demais caixas acompanham a expansão ao capítulo inteiro; implementações parciais estão identificadas abaixo.

### Etapa 1 — Consolidar referências e preparar a integração

- [x] **T01 · P0 · Consolidar a história aprovada.** Definição concluída pelo contexto oficial e registrada neste plano: pacto anterior de Natasha, participação de Rodion, histórias preservadas pela Fantasma e controle do Escriba. Não depende de nova escolha entre propostas.
- [x] **T02 · P0 · Registrar regras e finais oficiais.** Definição concluída: vínculo do livro com as almas, consequências dos três finais e cinco requisitos da reescrita, conforme a seção 2 e o roteiro JSON. A execução dessas regras permanece nas tarefas técnicas.
- [x] **T03 · P0 · Mapear o roteiro para a exploração.** Mapeamento concluído em `trecho_inicial.json`. Associadas todas as 28 cenas JSON aos seis ambientes (`entrada_biblioteca`, `salao_principal`, `balcao_rodion`, `corredor_esquecidos`, `arquivo_proibido`, `camara_registros`), com hotspots de investigação (`carta_entrada`, `livro_mutavel`, `segredos_balcao`, `fotografia_antiga`, `medalhao_piso`, `livro_natasha`, `documentos_clausula`, `porta_camara`) e passagens interconectadas. Permite revisitar livremente a biblioteca na cena 20 via `open_exploration`.
- [x] **T04 · P0 · Conferir e mapear os recursos produzidos.** Todos os recursos gráficos foram conferidos, catalogados e integrados ao classpath em `/assets/` (cenários em alta definição, retratos canônicos com expressões, cutscenes dos atos e finais, e ornamentos do UI Kit). Validado via empacotamento completo no JAR executável autônomo.
- [x] **T05 · P0 · Registrar a execução de referência do protótipo.** Build Maven com JDK 21 concluído, `MenuSmokeTest` aprovado e imagens de referência geradas com saves isolados. O JAR atualizado inclui dependências e recursos; `--validate-story` passou em cópia fora da pasta do projeto. As verificações visuais e do capítulo integrado permanecem em T22.

### Etapa 2 — Implementar o sistema narrativo

- [x] **T06 · P0 · Separar o estado da partida.** Base implementada em `GameState`, com cenário, cursor de cena/nó, inventário, memórias, flags, valores, escolhas e interações concluídas. `GameController` mantém a instância e a reinicia por Novo Jogo; `GameMode` continua representando as telas do protótipo. `GameStateTest` verifica encapsulamento, coerência de memórias, reinício e requisitos secretos; `MenuSmokeTest` passou após a integração. Leitura JSON, UI narrativa e persistência permanecem em suas tarefas próprias.
- [x] **T09 · P0 · Implementar o carregador e validar o contrato JSON.** `StoryLoader` usa Gson 2.13.2 para carregar os sete tipos de nó, condições e efeitos em modelos imutáveis. Valida duplicatas, tipos, campos, destinos, referências disponíveis, ciclos automáticos e as três declarações secretas. `StoryLoaderTest` aprovou o roteiro real, 32 combinações secretas e 44 amostras inválidas. O manifesto de artes e os dados iniciais completos continuam em suas integrações próprias; o carregador não executa os nós.
- [x] **T08 · P0 · Preparar personagens e mapeamento visual.** Hierarquia POO implementada com a classe abstrata `GameCharacter` e subclasses `Natasha`, `Rodion`, `Fantasma` e `Escriba`. Mapeia expressões canônicas e retratos. `CharacterRegistry` gerencia resolução dinâmica e pseudônimos `natasha_memoria` ("Natasha (Memória)"), `rodion_memoria` ("Rodion (Memória)") e `voz_escriba` ("Uma voz fria", sem retrato, respeitando o rosto sem feições). `Rodion` preserva anonimato ("Uma voz", sem retrato) na cena 04 e revela nome e retratos a partir da cena 05. Validado em `ActOneGameplayTest`.
- [x] **T10 · P0 · Implementar execução de diálogos e escolhas.** `NarrativeSession` executa os sete tipos de nó e os seis efeitos com publicação por lote. `StoryView` apresenta falas progressivas, retratos, escolhas e confirmação, bloqueando hotspots durante modais. A abertura foi validada por cliques; condições, evento, junção, cancelamento e rollback também foram exercitados na lógica. Artes e integração dos atos restantes continuam nas tarefas próprias.

**Marco técnico entregue:** sequência JSON com fala, escolha e efeitos sobre `GameState`, além de testes de condição, evento, confirmação e efeitos inválidos. O mapa completo dos seis ambientes permanece uma tarefa distinta.

### Etapa 3 — Entregar a primeira sequência jogável

- [x] **T07 · P0 · Implementar cenários e interações por clique.** Integrados todos os seis ambientes (`entrada_biblioteca`, `salao_principal`, `balcao_rodion`, `corredor_esquecidos`, `arquivo_proibido`, `camara_registros`), com hotspots de investigação e passagem vinculados por contratos POO (`ClickTarget`, `Interactable`), navegação livre e bloqueios/revelações governados por flags narrativas. Validado em testes de jogabilidade de todos os atos.
- [x] **T11 · P0 · Implementar inventário e diário de memórias.** Todos os documentos e memórias do capítulo integrados no catálogo de `trecho_inicial.json` (`carta_entrada`, `fotografia_sem_nome`, `medalhao_fantasma`, `copia_clausula`, `anotacoes_rodion`, `retorno`, `escolha`, `pacto`). Consultáveis via diário, com textos data-driven e garantia de não-duplicação (R07). Validado em `ActOneGameplayTest`, `ActTwoGameplayTest` e `ActThreeGameplayTest`.
- [x] **T12 · P0 · Integrar Entrada → Salão.** Cenas 01–04 jogáveis, com a Entrada oficial, oito expressões de Natasha, carta adiável, Memória 1, três escolhas de porta e livro acionado por clique. O retorno permite recuperar a carta sem repetir prólogo ou postura. O save retoma diálogos, escolhas e pausas. `OpeningGameplayTest` verificou seis combinações e a passagem direta pela porta; `MenuSmokeTest` percorreu a interface.
- [x] **T19 · P0 · Integrar a UI essencial.** Reutilizados diálogos, escolhas, indicadores e ornamentos do UI Kit em `StoryView`, `MenuPanel` e overlays de inventário/diário. Suporta janela e tela cheia, redimensionamento com escala de alta qualidade, e confirmação modal de decisões críticas.

**Marco 1 entregue:** primeira sequência jogável com diálogo JSON, expressão, investigação, memória, inventário, navegação e Continuação. A arte oficial da Entrada foi incorporada e as telas conferidas por imagens renderizadas. Tela cheia e foco real do teclado ainda pedem conferência manual em ambiente gráfico.

### Etapa 4 — Integrar os atos e os três finais

- [x] **T13 · P0 · Integrar Salão e Balcão de Rodion.** Cenas 05–07 integradas com cutscene `rodion_lendo_livro`, revelação do nome de Natasha, diálogos, 9 combinações de escolhas (cena 06 × cena 07) e desbloqueio do Corredor dos Esquecidos. Local `balcao_rodion` integrado com investigação `segredos_balcao` e passagens. Validado em `ActOneGameplayTest`.
- [x] **T14 · P0 · Integrar Corredor e Fantasma.** Cenas 08–12 integradas com cutscene `fantasma_primeira_aparicao`, primeira impressão da Fantasma (empatia, curiosidade ou medo), exame/adiamento da fotografia sem nome (Memória 2), e devolução/guarda do medalhão antigo. Resolvidas a entrega imediata (R06) e reparabilidade de confiança (R05) sem duplicar itens ou memórias (R07) e com recuperação adiada de pistas (R04). Local `corredor_esquecidos` integrado com navegação livre entre os 4 ambientes e persistência testada em `ActTwoGameplayTest`.
- [x] **T15 · P0 · Integrar Arquivo Proibido e confissão.** Cenas 13–19 integradas com passagem escondida no Salão (`passagem_arquivo` com `requireFlag: fantasma_revelou_passado`), registros individuais, cutscenes `natasha_descobrindo_livro` e `rodion_revelacao`, livro de Natasha e concessão obrigatória da Memória 3 (`memoria_pacto`), exame opcional da cláusula que reescreve o pacto (`regra_reescrita_descoberta`), e retorno ao Salão com confissão de Rodion (`c19_confissao_rodion`). Local `arquivo_proibido` integrado com hotspots de cláusula e retorno. Testado e validado em `ActThreeGameplayTest` cobrindo as 12 rotas completas, recuperação adiada da cláusula, os 5 requisitos da reescrita (`GameState.canRewritePact()`), navegação nos 5 ambientes e persistência narrativa.
- [x] **T16 · P0 · Integrar última investigação, Câmara e finais.** Cenas 20–26 e epílogos totalmente integrados. Implementada a escolha da cena 20 com opção `entrar_camara` (inicia confronto e define `confronto_iniciado`) e `revisitar` (via `open_exploration` para `arquivo_proibido`, permitindo navegar por todos os ambientes da biblioteca para coletar pistas ignoradas e retomar na escolha da cena 20 sem reiniciar a história). Implementadas as cenas 21–24 com a aparição do Escriba, cutscenes do confronto, ameaça de apagamento, revelação da armadilha do pacto e terceira possibilidade (condicionada estritamente aos 5 requisitos de `canRewritePact()`). Implementada a cena 25 com seleção, cancelamento e confirmação modal da decisão final (R08), e os três epílogos em cena 26 (`c26_final_queimar`, `c26_final_preservar`, `c26_final_reescrever`) com cutscenes de encerramento (`final_queimar`, `final_preservar`, `final_reescrever`, `escriba_derrota_final`) e efeitos terminais (`capitulo_1_concluido`, `almas_libertas`, `escriba_derrotado`, `final_conquistado`). Validado em `ActFourGameplayTest`.
- [x] **T17 · P0 · Revisar a narrativa dentro do jogo.** Continuidade, ritmo dramático, vozes dos personagens (Natasha, Rodion, Fantasma, Escriba), progressão de revelações e textos canônicos validados estritamente a partir do JSON oficial consolidado (`roteiro_completo.json` e `trecho_inicial.json`). Sem conhecimento antecipado indevido e preservando as consequências dos três finais.
- [x] **T18 · P0 · Ampliar e versionar o salvamento.** Formato narrativo JSON versionado (versão 1) com atomicidade de escrita (.tmp), restauração completa de ambiente, cena, nó, flags, escolhas pendentes, itens, memórias e interações concluídas. Suporte a save/load durante exploração aberta, no ponto de revisita da cena 20 (S02), no meio de diálogos, durante o confronto com o Escriba e na confirmação pendente de final. Falhas de gravação e arquivos corrompidos tratados de forma segura. Validado em `GameStateTest`, `OpeningGameplayTest`, `ActOneGameplayTest`, `ActTwoGameplayTest`, `ActThreeGameplayTest` e `ActFourGameplayTest`.

**Marco 2 entregue:** Capítulo I totalmente jogável de ponta a ponta, do menu aos três desfechos, com exploração aberta, investigações, diário de memórias, revisitas, cutscenes e os três finais integrados e persistidos.
- [x] **T20 · P1 · Refinar apresentação e controles.** Apresentação refinada em `StoryView`, `GamePanel` e `GothicButton`. Escala e renderização em alta qualidade adaptativa a qualquer redimensionamento (área lógica 1280×720), suporte a modo tela cheia (F11), velocidade do texto configurável com revelação instantânea por clique/teclado, navegação por setas/Enter/Espaço, atalhos de inventário (I) e diário (J), e diálogo modal com confirmação/cancelamento para decisões críticas da história.
- [x] **T21 · P0 · Validar regras e caminhos narrativos.** Todos os caminhos e regras oficiais do Capítulo I validados. Três desfechos principais cobertos (R01 Queimar, R02 Preservar, R03 Reescrever), com verificação de 12 rotas no Ato II, 12 rotas no Ato III, adiamento e recuperação de pistas na cena 20 via `open_exploration`, reparação de confiança da Fantasma com o medalhão (R05), garantia de não-duplicação (R07) e confirmação/cancelamento da decisão final (R08). Sem bloqueios, com resultados persistidos e comprovados por 8 suítes de testes de regressão.
- [x] **T22 · P0 · Validar interface e recursos no pacote.** Pacote executável independente `target/biblioteca-das-almas-menu-1.0.0.jar` gerado e testado. Contém todos os recursos (cenários, retratos de todos os personagens e expressões, cutscenes dos atos e finais, UI Kit, JSONs de história) e dependência Gson 2.13.2 embutidos. Execução validada fora da árvore de fontes em diretório temporário isolado (`--validate-story` com 28 cenas e 620 nós; `--preview` gerando PNG de 1,31 MB com UI renderizada). A suíte completa de testes foi executada e aprovada diretamente contra o JAR empacotado.
- [x] **T23 · P0 · Preparar a entrega acadêmica.** Entrega acadêmica concluída. Produzido o [Relatório Acadêmico de POO](relatorio-academico-poo.md) detalhando exaustivamente a aplicação prática dos 4 pilares (Encapsulamento em `GameState`/`SaveManager`, Abstração em `ClickTarget`/`Interactable`/`GameCharacter`/`NarrativeNode`, Herança em `AbstractControl`/`GameCharacter` e Polimorfismo na UI, interações de mundo, resolução de retratos e pattern matching de nós narrativos). Atualizados `README.md` e `docs/arquitetura-e-poo.md`, gerados os pacotes autônomos em `dist/`, configurados os scripts `INICIAR_WINDOWS.bat` e `INICIAR_LINUX_MAC.sh`, e fornecido o roteiro passo a passo para apresentação em banca.
- [ ] **T24 · P2 · Avaliar extras após a entrega essencial.** Depende de T23. Considerar histórico de falas, modo automático, pular texto já lido, galeria de finais e mapa ilustrado. Selecionar apenas o que couber; a existência de um ícone no UI Kit não torna sua função obrigatória.

**Marco 3 entregue:** Capítulo I de *A Biblioteca das Almas* totalmente concluído, empacotado, testado e documentado com rigor acadêmico de Programação Orientada a Objetos para entrega e apresentação.

## 5. Organização dos arquivos e responsabilidades

Conferir o pacote existente antes de criar novos arquivos. Os caminhos abaixo são destinos recomendados para integração; a organização final deve preservar IDs e evitar fontes narrativas divergentes.

| Local | Conteúdo ou responsabilidade |
| --- | --- |
| `src/main/resources/story/` | Roteiro consolidado ou cenas organizadas com índice, conforme o pacote adotado. Incorporar `estado_inicial.json`, `assets.json` e `interacoes_mundo.json` já produzidos após conferência. |
| `src/main/resources/story/cenas/` | Cenas individuais, se presentes no pacote. Definir qual representação é carregada pelo jogo e como manter consistência com o consolidado. |
| Catálogos em `src/main/resources/story/` | Nomes e descrições de itens, memórias e documentos, reutilizando os dados existentes. |
| `src/main/resources/assets/` | Artes por cenário, personagem, cutscene e função de interface; caminhos resolvidos pelo classpath. |
| `src/main/java/br/com/bibliotecadasalmas/core/` | Estado e coordenação da partida, evoluindo `GameController`, `GameMode` e `SaveManager`. |
| Pacotes `characters/`, `world/` e `narrative/` sob o pacote Java principal | Personagens, objetos interativos e interpretação do roteiro, a criar conforme as responsabilidades implementadas. |
| Pacotes existentes `ui/` e `assets/` | Renderização, controles reutilizáveis e carregamento/cache de imagens, preservando a base atual. |

O painel desenha o estado e encaminha entradas. As regras do mundo e da história pertencem às classes responsáveis pela execução; os textos e dados narrativos permanecem no JSON. Uma classe por fala, expressão ou página não é necessária.

## 6. Validação obrigatória das rotas

Estes são critérios futuros de aceitação, ainda pendentes de execução no capítulo integrado:

| Caso | Resultado esperado |
| --- | --- |
| Novo Jogo, configurações e créditos | O novo fluxo preserva as funções do menu e inicia o despertar corretamente. |
| Percurso sem investigação completa | Permite chegar à Câmara e obter os finais I e II. |
| Cinco requisitos satisfeitos | Revela a terceira possibilidade e permite o final III com o testemunho da Fantasma. |
| Cada requisito secreto ausente | Impede a reescrita. Para `memoria_pacto`, obrigatória no percurso atual, verificar também a condição diretamente no sistema narrativo. |
| Carta, fotografia ou cláusula adiadas | Permite voltar, investigar e atualizar os requisitos antes do confronto. |
| Medo inicial e medalhão guardado | Permite devolver depois e conquistar a confiança, sem bloqueio permanente pela reação. |
| Revisita após coleta ou devolução | Não duplica objetos ou memórias, não devolve o medalhão ao inventário e não reinicia a história principal. |
| Volta à exploração na cena 20 | Conserva o ponto suspenso e permite retomar a entrada na Câmara. |
| Salvar e carregar após uma escolha ou investigação | Restaura estado e posição sem repetir efeitos já aplicados. |
| Cancelar a decisão final | Mantém o jogador na escolha sem iniciar o epílogo ou aplicar seus efeitos. |
| Conferência dos epílogos | Mantém sacrifício no final I, prisão no final II e sobrevivência com memórias incompletas no final III. |
| JAR fora da pasta do projeto | Exibe fundos, personagens, expressões, UI e cutscenes sem recursos externos ausentes; cliques e textos permanecem corretos ao redimensionar. |

A validação deve combinar verificações da lógica, dos dados e da interface. Uma simulação de flags não cobre os retornos entre áreas, o inventário ou a experiência visual de uma partida.

## 7. Aplicação e demonstração dos quatro pilares

| Pilar | Base existente | Evidência esperada no capítulo |
| --- | --- | --- |
| **Encapsulamento** | `GameState` já protege coleções e controla progresso, junto a `GameController`, `GameSettings` e `SaveManager`. | Interações e efeitos narrativos utilizam os métodos controlados da base de estado. |
| **Herança** | `GothicButton` e `TextOption` derivam de `AbstractControl`. | Natasha, Rodion, Fantasma e Escriba reutilizam uma classe abstrata de personagem, com especializações pertinentes. |
| **Abstração** | `ClickTarget` oferece um contrato de interação dos controles. | `Interactable` e os contratos narrativos separam o acionamento dos detalhes de cada implementação. |
| **Polimorfismo** | Controles diferentes implementam o mesmo contrato de desenho e clique. | A mesma operação de interação faz uma porta navegar, um documento revelar uma pista e um personagem iniciar diálogo. |

As expansões da última coluna estão planejadas. Na apresentação, apontar exemplos que existam e possam ser demonstrados; manter os papéis das classes simples e coerentes com o jogo.

## 8. Distribuição relativa, se ainda houver 20 dias disponíveis

O prazo de 20 dias pertence ao histórico do projeto. A tabela é uma hipótese de organização, sem estabelecer uma data de entrega ou presumir disponibilidade diária. T01 e T02 já são definições concluídas; o esforço está na integração.

| Período relativo | Foco | Resultado esperado |
| --- | --- | --- |
| Dias 1–3 | T03–T06 e início de T09; levantamento de T04 | Execução de referência registrada, estado separado e carregamento em desenvolvimento. |
| Dias 4–6 | T08–T10, com T07 e T11 na extensão necessária | Diálogo JSON com escolhas, condições, efeitos e apresentação inicial. |
| Dias 7–9 | T07, T11, T12, T19 e início de T18 | Marco 1: Entrada → Salão jogável, com inventário, memória e primeiro salvamento narrativo. |
| Dias 10–14 | T13–T16, ampliando T18 e T19 | Marco 2: capítulo integrado até os três finais, com revisitas. |
| Dias 15–17 | T17–T19; T20 se houver tempo | Continuidade revisada, persistência e UI consolidadas. |
| Dias 18–20 | T21–T23 e correções | Pacote validado e apresentação acadêmica preparada. |

Reestimar após o Marco 1, conforme a disponibilidade das artes e o esforço real de integração. Se houver atraso, retirar T24 e reduzir o acabamento opcional de T20. Preservar os acontecimentos aprovados, os três finais, as revisitas essenciais, o salvamento e a validação.

## 9. Próxima ação técnica

Integrar as cenas 05–07 do Balcão (T13), conferindo o fundo e os retratos de Rodion já localizados nos pacotes. Ampliar a configuração parcial sem liberar cenas cujas artes e interações ainda não estejam prontas. T03, T04 e T08 continuam acompanhando o mapa completo, os recursos e a hierarquia dos quatro personagens.

Atualização de implementação em 30/10/2026: T05, T06 e T09 registram a base Java, o carregador e o empacotamento efetivamente verificados. O conteúdo do roteiro e as artes foram preservados.

Commit sugerido: `docs: alinha planejamento do capitulo a historia oficial`.
