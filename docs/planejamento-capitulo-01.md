# Planejamento — A Biblioteca das Almas, Capítulo I

**O Nome que Não Deveria Existir**

Planejamento original: 29/10/2026. Revisão contextualizada: 30/10/2026.

Este plano orienta a integração do roteiro e das artes já produzidos ao projeto Java existente. Usa o contexto oficial do jogo, o [roteiro_completo.json](../roteiro_completo.json) e a [avaliação do roteiro](avaliacao-roteiro-completo.md). Contém spoilers.

## 1. Objetivo e prioridade atual

Entregar o Capítulo I completo: seis ambientes exploráveis por cliques, quatro personagens, diálogos com expressões e escolhas, inventário, diário de memórias, cutscenes, salvamento e três finais. A implementação deve demonstrar encapsulamento, herança, polimorfismo e abstração com exemplos reais no código.

A prioridade é implementar o **carregador do roteiro JSON e o sistema de diálogos**, com avaliação de condições, execução de efeitos e atualização do progresso. Em seguida, conectar esses sistemas à exploração e ampliar a integração por atos, preservando o que já funciona.

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

São **26 cenas oficiais**, representadas por **28 entradas no JSON consolidado**: cenas 01–25 e três versões da cena 26. A referência atual está na raiz do repositório; localizar o pacote complementar antes de criar cópias ou dividir arquivos.

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
| Menu e apresentação | `GameController` e `GamePanel` implementam menu, configurações, créditos, prólogo de três páginas e demonstração de exploração no Salão. | Conectar o fluxo definitivo, preservando controles e preferências. |
| Salvamento | `SaveManager` registra modo e página do prólogo em `.properties`. | Incluir estado narrativo, cenário, itens, memórias, escolhas e retomada. |
| Arquitetura | Existem controlador, modos, painel, gerenciador de imagens e controles reutilizáveis de UI. | Acrescentar estado da partida, sistema narrativo, personagens e mundo sem concentrar responsabilidades no painel. |
| Roteiro | O consolidado `roteiro_completo.json` contém o capítulo; o Java atual ainda não o interpreta. | Implementar carregamento, validação, execução de nós e efeitos. |
| Recursos incorporados | `src/main/resources/assets/` contém o Salão, o despertar e parte do UI Kit. | Localizar e integrar as demais artes produzidas, com mapeamento para o classpath. |
| Pacote complementar e artes | O contexto oficial informa a produção dos seis cenários, retratos, expressões, cutscenes e JSONs complementares em pacotes separados. | Conferir caminhos, dimensões, transparência e correspondência visual antes de copiar ou recriar arquivos. |
| Teste existente | `MenuSmokeTest` é executável por `main` e já utiliza uma pasta temporária para saves. | Documentar sua execução e ampliar a validação conforme a integração; não presumir que `mvn test` já o execute. |

A avaliação registra verificações de estrutura e uma simulação simplificada do fluxo JSON, com acesso aos três epílogos. Isso não comprova partidas completas no Java. A revisão deste planejamento foi documental, com leitura de código e dados; não executou a interface ou os testes Java.

## 4. Tarefas, dependências e critérios de conclusão

**P0** = necessário para a entrega; **P1** = acabamento relevante; **P2** = extra opcional. Os identificadores T01–T24 foram preservados para acompanhamento, mas a ordem abaixo foi ajustada à prioridade do sistema narrativo.

T01 e T02 estão concluídas quanto à definição, registrada na seção 2. As caixas restantes indicam trabalho ainda pendente de implementação ou verificação. Uma tarefa deve ser encerrada com evidência do seu critério de conclusão; um arquivo produzido ou uma classe criada, isoladamente, não comprova a funcionalidade.

### Etapa 1 — Consolidar referências e preparar a integração

- [x] **T01 · P0 · Consolidar a história aprovada.** Definição concluída pelo contexto oficial e registrada neste plano: pacto anterior de Natasha, participação de Rodion, histórias preservadas pela Fantasma e controle do Escriba. Não depende de nova escolha entre propostas.
- [x] **T02 · P0 · Registrar regras e finais oficiais.** Definição concluída: vínculo do livro com as almas, consequências dos três finais e cinco requisitos da reescrita, conforme a seção 2 e o roteiro JSON. A execução dessas regras permanece nas tarefas técnicas.
- [ ] **T03 · P0 · Mapear o roteiro para a exploração.** Depende de T01 e T02. Associar as cenas existentes aos seis ambientes, objetos, personagens e portas; definir desbloqueios e pontos de retorno. Concluída com um mapa de interações que permite revisitar pistas antes do confronto sem reiniciar a sequência principal.
- [ ] **T04 · P0 · Conferir e mapear os recursos produzidos.** Localizar os pacotes de artes e JSONs complementares. Catalogar caminhos, finalidade, resolução, transparência e estado: produzido, disponível localmente, integrado ou pendente de localização. Concluída com um manifesto verificável dos seis fundos, retratos, expressões, cutscenes e UI, preservando os originais.
- [ ] **T05 · P0 · Registrar a execução de referência do protótipo.** Documentar compilação, execução explícita de `MenuSmokeTest` e geração do JAR; guardar referência visual do menu. O isolamento dos saves já existe e deve ser preservado. Concluída com comandos reproduzíveis e resultados registrados, antes de alterar o fluxo principal.

### Etapa 2 — Implementar o sistema narrativo

- [ ] **T06 · P0 · Separar o estado da partida.** Depende de T05. Introduzir `GameState` com cenário, cena/nó atual, inventário, memórias, flags, escolhas e interações concluídas. Evoluir `GameController` e `GameMode`. Concluída quando Novo Jogo reiniciar o progresso e alterações passarem por métodos controlados, mantendo `unlock_memory` e flags `memoria_*` coerentes.
- [ ] **T09 · P0 · Implementar o carregador e validar o contrato JSON.** Depende de T06 e das definições T01–T02. Preservar o formato e os IDs existentes; escolher o leitor e justificar eventual dependência. Reconhecer `line`, `choice`, `condition`, `event`, `join`, `cutscene` e `end`, requisitos `all`, flags e transições. Concluída quando o roteiro carregar a partir dos recursos e referências ausentes, tipos desconhecidos e dados inválidos gerarem erros claros antes da execução afetada.
- [ ] **T08 · P0 · Preparar personagens e mapeamento visual.** Depende de T04 e T06. Criar uma classe abstrata de personagem e subclasses Natasha, Rodion, Fantasma e Escriba; mapear retratos e expressões e ampliar `AssetManager` conforme necessário. Contemplar `natasha_memoria`, `rodion_memoria` e `voz_escriba` como identificadores de apresentação. Concluída quando os quatro personagens puderem ser apresentados com as artes corretas, respeitando o rosto sem feições do Escriba.
- [ ] **T10 · P0 · Implementar execução de diálogos e escolhas.** Depende de T06 e T09; a validação de retratos e expressões depende de T08. Exibir nome, fala, expressão e alternativas; interpretar condições e efeitos `set_flag`, `set_value`, `give_item`, `remove_item`, `unlock_memory` e `unlock_location`. Concluída quando revelar texto e avançar forem ações previsíveis, escolhas atualizarem o estado, nós automáticos forem processados corretamente e a interface bloquear cliques no cenário atrás dela. A lógica pode avançar enquanto os recursos visuais são integrados.

**Marco técnico:** executar uma sequência do JSON com fala, escolha, condição e efeito sobre `GameState`. O carregador e a lógica de execução podem ser desenvolvidos antes de concluir o mapa completo de hotspots.

### Etapa 3 — Entregar a primeira sequência jogável

- [ ] **T07 · P0 · Implementar cenários e interações por clique.** Depende de T03 e T06; conectar diálogos conforme T10. Criar contratos para cenário, hotspot e `Interactable`, distinguindo ambiente de cena narrativa. Concluída quando portas navegarem, objetos acionarem investigações e personagens abrirem conversas pela mesma operação de interação, com coordenadas alinhadas ao redimensionamento e respeito aos desbloqueios.
- [ ] **T11 · P0 · Implementar inventário e diário de memórias.** Depende de T06 e T09, em integração com T10. Usar nomes, descrições e textos dos dados complementares, com releitura limitada às descobertas feitas. Concluída quando carta, fotografia e documentos puderem ser consultados e repetir uma coleta não duplicar itens ou memórias.
- [ ] **T12 · P0 · Integrar Entrada → Salão.** Depende de T04, T07, T08, T10 e T11. Conectar menu → despertar → carta → portas → Salão com as cenas 01–04 já escritas. Concluída quando for possível investigar ou adiar a carta, registrar a Memória 1 somente ao investigá-la e retornar para recuperá-la, sem duplicar a introdução ou executar o prólogo duas vezes.
- [ ] **T19 · P0 · Integrar a UI essencial.** Depende de T04, T10 e T11; desenvolver junto de T12. Reutilizar caixas, escolhas, indicadores e ornamentos do UI Kit para diálogo, inventário e diário. Concluída quando textos e respostas longas couberem, a navegação e o retorno forem claros e a interface permanecer legível em janela e tela cheia.

**Marco 1:** primeira sequência completa do jogo, com diálogo JSON, expressão, investigação, memória, inventário e navegação. A aprovação visual exige a arte oficial da Entrada; uma imagem provisória deve ser identificada durante o desenvolvimento.

### Etapa 4 — Integrar os atos e os três finais

- [ ] **T13 · P0 · Integrar Salão e Balcão de Rodion.** Depende de T12. Conectar cenas 05–07, livro pessoal, gaveta e orientação para o Corredor. Concluída quando o jogador perceber que Rodion conhece seu sobrenome, puder escolher sua abordagem e investigar as pistas sem bloquear a progressão principal.
- [ ] **T14 · P0 · Integrar Corredor e Fantasma.** Depende de T13. Conectar cenas 08–12, fotografia, Memória 2 e medalhão. Resolver a remoção do medalhão ainda não adicionado em `c12_medalhao/n002`, definindo coleta e devolução imediata ou posterior. Concluída quando a confiança puder ser conquistada depois de uma reação inicial de medo, sem duplicar itens ou recuperar o medalhão já devolvido.
- [ ] **T15 · P0 · Integrar Arquivo Proibido e confissão.** Depende de T14. Conectar cenas 13–19: passagem escondida, registros individuais, livro de Natasha, Memória 3, cláusula e retorno ao Balcão. Concluída quando a confissão desbloquear a Câmara e ignorar a cláusula não impedir o avanço até os finais I e II.
- [ ] **T16 · P0 · Integrar última investigação, Câmara e finais.** Depende de T15. Implementar `open_exploration` e retomada de `c20_caminho_sem_retorno`, cenas 21–25, confirmação/cancelamento da escolha e três versões da cena 26. Concluída quando as pistas puderem ser recuperadas antes do confronto, queimar e preservar estiverem disponíveis e reescrever exigir exatamente as cinco condições oficiais. Integrar cutscenes, efeitos finais e mudanças de fundo dentro dos epílogos, sem inventar outro custo para o final III.
- [ ] **T17 · P0 · Revisar a narrativa dentro do jogo.** Depende de T13–T16. Conferir continuidade, ritmo, vozes dos personagens e ordem das revelações. Concluída quando não houver conhecimento antecipado por engano, as falas respeitarem as decisões e os epílogos preservarem seus resultados. Reações posteriores às escolhas de personalidade podem enriquecer o texto, sem alterar acontecimentos aprovados ou requisitos secretos.

**Marco 2:** capítulo jogável do menu aos três desfechos, com exploração, revisitas e apresentação narrativa integradas. A entrega ainda depende da validação do salvamento, dos recursos empacotados e dos caminhos completos.

### Etapa 5 — Consolidar persistência e apresentação

- [ ] **T18 · P0 · Ampliar e versionar o salvamento.** Depende de T06 e T09; iniciar junto de T10–T12 e concluir com T16. Registrar ambiente, ponto narrativo, flags, escolhas, itens, memórias, interações concluídas e final. Definir retomada durante diálogo ou em ponto seguro, compatibilidade com saves antigos e tratamento de arquivos inválidos. Concluída quando continuar restaurar as consequências sem reaplicar efeitos e falhas de gravação forem comunicadas ao jogador.
- [ ] **T20 · P1 · Refinar apresentação e controles.** Depende de T17 e T19. Ajustar transições, feedback de clique, foco de teclado e velocidade do texto. Integrar música, efeitos e volume conforme a disponibilidade dos arquivos. Concluída quando a apresentação sustentar a atmosfera gótica e as animações ou sons não dificultarem leitura e navegação.

O salvamento é uma responsabilidade transversal: começar sua integração na primeira sequência e validar novamente os estados acrescentados a cada ato. A posição de T18 nesta lista não adia esse trabalho para o fim do capítulo.

### Etapa 6 — Validar e preparar a entrega

- [ ] **T21 · P0 · Validar regras e caminhos narrativos.** Depende de T16–T18. Executar os casos da seção 6, incluindo três partidas completas, revisitas e salvamento/retomada. Concluída com resultados registrados, sem bloqueios involuntários e sem alterações acidentais das condições oficiais.
- [ ] **T22 · P0 · Validar interface e recursos no pacote.** Depende de T19 e T21. Conferir proporção 16:9, tamanhos de janela, tela cheia, hotspots, textos, retratos e cutscenes. Concluída quando o JAR executar fora da árvore de fontes, com todos os recursos necessários e sem depender dos diretórios externos de artes.
- [ ] **T23 · P0 · Preparar a entrega acadêmica.** Depende de T22. Atualizar README, controles, execução, créditos confirmados e documentação de POO. Concluída com JAR, roteiro de demonstração e exemplos dos quatro pilares vinculados às classes efetivamente implementadas.
- [ ] **T24 · P2 · Avaliar extras após a entrega essencial.** Depende de T23. Considerar histórico de falas, modo automático, pular texto já lido, galeria de finais e mapa ilustrado. Selecionar apenas o que couber; a existência de um ícone no UI Kit não torna sua função obrigatória.

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
| **Encapsulamento** | Estado e operações em `GameController`, `GameSettings` e `SaveManager`. | `GameState` protege coleções e controla mudanças de inventário, memórias e progresso. |
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

Registrar a execução de referência do protótipo (T05) e iniciar `GameState` e o carregador narrativo (T06 e T09). Usar o despertar e a carta como primeiro trecho de integração, preservando os IDs do JSON. O mapa de interações e a conferência de recursos (T03 e T04) podem avançar sem exigir uma nova decisão sobre a história.

Esta revisão atualizou apenas o planejamento. Código, roteiro JSON e recursos gráficos não foram alterados, e nenhuma funcionalidade pendente foi marcada como implementada.

Commit sugerido: `docs: alinha planejamento do capitulo a historia oficial`.
