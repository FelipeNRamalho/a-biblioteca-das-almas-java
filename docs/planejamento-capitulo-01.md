# Planejamento — A Biblioteca das Almas, Capítulo I

Planejamento preparado em 29/10/2026 a partir do histórico fornecido e do projeto local. As tarefas abaixo ainda não foram implementadas. As opções de história estão em [rumos-da-historia.md](rumos-da-historia.md); nenhuma foi escolhida automaticamente.

## Objetivo da entrega

Entregar um capítulo com começo, desenvolvimento e encerramento: seis cenários exploráveis por cliques, quatro personagens, diálogos com escolhas, inventário, memórias, cutscenes, salvamento e três finais. Java 21, Swing e Graphics2D continuam sendo a base, com aplicação demonstrável de abstração, encapsulamento, herança e polimorfismo.

O visual confirmado é anime gótico ilustrado, com vestimentas vitorianas e pintura suave. O controle é principalmente pelo mouse. Combate, caminhada animada, novos capítulos e sistemas extensos de habilidades ficam fora desta entrega.

## Ponto de partida verificado

| Área | Situação observada | Trabalho restante |
| --- | --- | --- |
| Menu e apresentação | Código para menu, prólogo de três páginas, configurações, créditos e demonstração no Salão | Preservar esses fluxos durante a expansão |
| Salvamento | Registra modo e página do prólogo em `.properties` | Registrar cenário, itens, memórias, decisões e ponto narrativo |
| Arquitetura | `GameController`, `GameMode`, `GamePanel`, `AssetManager` e controles de UI | Separar estado da partida, mundo e conteúdo narrativo |
| Teste existente | `MenuSmokeTest`, executado por método `main` | Adaptar ao novo fluxo e integrar execução à validação; não presumir que `mvn test` já o execute |
| Recursos incorporados | Fundo do Salão, despertar de Natasha e parte da UI | Completar integração e validar imagens |
| Acervo externo informado | Arquivos de personagens, expressões, cutscenes e três finais foram localizados por nome | Inspecionar transparência, dimensões, conteúdo e correspondência com o roteiro |
| Seis fundos | Não foram localizados como conjunto nas duas raízes examinadas | Localizar os fundos; a existência relatada nas conversas não confirma disponibilidade local |

Esta conferência foi de arquivos e código. O jogo e seus testes não foram executados nesta etapa de planejamento.

## Decisões de história que vêm primeiro

1. Escolher a verdade central sobre Natasha: opção A, B ou C do documento de história.
2. Definir o vínculo anterior de Rodion com ela e o limite de sua responsabilidade.
3. Definir a identidade da Fantasma e o que ela sabe. A ideia de que absorveu sombras para proteger outras almas continua sendo proposta.
4. Definir a motivação do Escriba e os limites de seu poder.
5. Estabelecer por que o livro de Natasha afeta as outras almas e qual é o custo de reescrevê-lo.
6. Fechar as condições do final secreto e como o jogador descobrirá essas condições.

As três memórias, a confiança da Fantasma e a descoberta de uma regra de reescrita são uma sugestão de estrutura, ainda ajustável. O prazo de 20 dias veio do histórico: a distribuição abaixo é relativa e não estabelece uma nova data de entrega.

## Lista de tarefas

Prioridades: P0 = necessária ao capítulo; P1 = acabamento relevante; P2 = opcional. Todas começam pendentes. As dependências indicam o que precisa estar definido ou funcional antes de concluir a tarefa.

### Etapa 1 — História e recursos

- [ ] **T01 · P0 · Escolher a direção narrativa.** Dependência: escolha do usuário. Entrega: premissa e verdade central em uma página. Concluída quando Natasha, Rodion, Fantasma e Escriba tiverem motivações compatíveis com os três finais.
- [ ] **T02 · P0 · Escrever as regras da biblioteca.** Depende de T01. Definir memória versus registro, apagamento, prisão das almas, vínculo com o livro e reescrita. Concluída quando cada final decorrer dessas regras e o Escriba tiver limites claros.
- [ ] **T03 · P0 · Montar o mapa narrativo.** Depende de T02. Entrega: cinco atos, pistas, escolhas, retornos entre ambientes e condições dos finais. Concluída quando cada revelação tiver pistas anteriores e houver um caminho possível para cada final, sem bloqueios acidentais.
- [ ] **T04 · P0 · Inventariar os recursos.** Independente de T01–T03. Catalogar caminho, finalidade, resolução, transparência e estado de cada arte; conferir os seis fundos, retratos, cutscenes e UI. Concluída com um manifesto que distingue disponível, integrado e ausente. Preservar os originais.

### Etapa 2 — Base do jogo

- [ ] **T05 · P0 · Consolidar a execução e a verificação atual.** Independente da história. Documentar compilação, execução do teste existente e geração do JAR; guardar uma referência visual do menu. Concluída quando houver uma sequência reproduzível e o teste usar uma pasta temporária para saves.
- [ ] **T06 · P0 · Separar o estado da partida.** Depende de T05. Introduzir `GameState` com cenário, inventário, memórias, flags e ponto narrativo. Evoluir `GameMode` e `GameController` sem duplicar gerenciadores. Concluída quando Novo Jogo reiniciar todo o estado e a renderização consultar o estado sem controlar as regras da história.
- [ ] **T07 · P0 · Criar cenários e interações.** Depende de T06. Implementar `Scene`, `Hotspot`, `Interactable`, portas e objetos examináveis. Concluída quando a mesma chamada de interação abrir diálogos, examinar objetos ou trocar de ambiente, e os cliques permanecerem alinhados ao redimensionar a janela.
- [ ] **T08 · P0 · Preparar personagens e recursos visuais.** Depende de T04 e T06. Criar `GameCharacter` e especializações com comportamento relevante, mapear expressões e ampliar o carregamento de recursos. Concluída quando os quatro personagens puderem ser apresentados de maneira consistente; os estados do Escriba devem respeitar seu rosto sem feições.

### Etapa 3 — Diálogos e primeira sequência jogável

- [ ] **T09 · P0 · Definir o formato dos dados narrativos.** Depende de T03 e T06. Especificar cenas, falas, escolhas, requisitos e eventos, com IDs estáveis. JSON é a proposta; escolher o leitor e documentar a dependência, se adotada. Concluída quando referências inexistentes e eventos inválidos forem detectados antes da partida.
- [ ] **T10 · P0 · Implementar diálogos e escolhas.** Depende de T07–T09. Mostrar nome, expressão, texto progressivo e respostas; processar condições e efeitos permitidos. Concluída quando um clique revelar o texto e outro avançar, escolhas produzirem consequências e diálogos bloquearem cliques no cenário atrás deles.
- [ ] **T11 · P0 · Implementar inventário e diário de memórias.** Depende de T06 e T09. Guardar itens sem duplicação, permitir releitura e registrar memórias sem expor revelações futuras. Concluída quando recolher novamente um objeto não repetir recompensas e o jogador puder consultar o que descobriu.
- [ ] **T12 · P0 · Tornar a Entrada jogável.** Depende de T03, T04, T07, T10 e T11. Integrar menu → despertar → carta → portas → Salão. Escrever a introdução e a carta conforme o rumo escolhido. Concluída quando o fluxo puder ser jogado inteiro, com a carta no inventário e retorno coerente entre ambientes.

**Marco 1:** uma pequena sequência completa já representa o jogo final. A carta é a interação confirmada para a Entrada; o espelho da proposta antiga não é requisito. Se faltar o fundo, uma imagem provisória identificada pode apoiar o desenvolvimento, mas a aprovação visual da etapa exige a arte correta.

### Etapa 4 — Desenvolvimento do capítulo

- [ ] **T13 · P0 · Integrar o Salão e Rodion.** Depende de T12 e T08. Implementar navegação central, balcão, primeiro diálogo e pistas nos protocolos. Concluída quando abordagens diferentes alterarem falas ou informações e o jogador entender seu próximo objetivo.
- [ ] **T14 · P0 · Integrar o Corredor e a Fantasma.** Depende de T13. Escrever e implementar o encontro, a escolha de confiança e a memória ligada a ela, conforme T03. Concluída quando a reação à aparência da Fantasma tiver consequência perceptível e a pista principal puder ser compreendida.
- [ ] **T15 · P0 · Integrar o Arquivo Proibido.** Depende de T14. Implementar acesso conforme o mapa narrativo, descoberta do livro e comparação de registros. Concluída quando a revelação esclarecer as pistas anteriores e o acesso ao confronto não exigir, por engano, os requisitos secretos.
- [ ] **T16 · P0 · Integrar a Câmara e os três finais.** Depende de T15. Implementar confronto verbal, `EndingManager`, escolha final, cutscenes e epílogos. Concluída quando queimar e preservar forem acessíveis no clímax e reescrever exigir exatamente as condições definidas em T03.
- [ ] **T17 · P0 · Revisar o roteiro no jogo.** Depende de T13–T16. Revisar falas, ritmo, continuidade, antecipação das revelações e personalidades. Concluída quando cada personagem tiver voz própria, nenhum conhecimento surgir antes de sua descoberta e os três epílogos responderem às decisões do jogador.

**Marco 2:** capítulo completo, do menu aos três desfechos, ainda sujeito ao acabamento e à validação final.

### Etapa 5 — Persistência e apresentação

- [ ] **T18 · P0 · Ampliar e versionar o salvamento.** Depende de T06 e T09; concluir após T16. Salvar cenário, flags, itens, memórias, diálogo ou ponto seguro de retomada e final. Definir migração dos saves antigos e recuperação de arquivos inválidos. Concluída quando continuar preservar consequências sem duplicar efeitos e falhas de gravação forem comunicadas ao jogador. Pode avançar junto da integração dos atos.
- [ ] **T19 · P0 · Integrar a UI essencial.** Depende de T04, T10 e T11. Caixas, placa de nome, escolhas, avanço, inventário, memórias e retorno. Concluída quando textos forem legíveis, respostas longas couberem e nenhum elemento essencial depender só de hover ou cor.
- [ ] **T20 · P1 · Refinar apresentação e controles.** Depende de T17 e T19. Transições, feedback de clique, foco de teclado, volume se houver áudio e revisão de velocidade do texto. Concluída quando efeitos não dificultarem a leitura nem bloquearem a navegação. Música e efeitos sonoros dependem da disponibilidade de arquivos adequados.

### Etapa 6 — Validação e entrega

- [ ] **T21 · P0 · Validar regras e caminhos narrativos.** Depende de T16–T18. Testar três partidas completas, bloqueio do final secreto com cada requisito ausente, repetição de interações, retorno entre áreas e salvamento/retomada. Concluída com resultados registrados e sem caminhos presos involuntariamente.
- [ ] **T22 · P0 · Validar interface e recursos empacotados.** Depende de T19 e T21. Conferir proporção 16:9, diferentes tamanhos de janela, tela cheia, cliques, texto, retratos e imagens dentro do JAR. Concluída quando o pacote executar fora da árvore de fontes sem depender da pasta externa de artes.
- [ ] **T23 · P0 · Preparar a entrega acadêmica.** Depende de T22. Atualizar README, controles, instruções de execução, créditos confirmados e documento de POO. Concluída com JAR, roteiro de demonstração e exemplos reais de cada pilar ligados às classes implementadas.
- [ ] **T24 · P2 · Adicionar extras após a entrega essencial.** Depende de T23. Histórico completo de falas, modo automático, pular texto já lido, galeria de finais, mapa ilustrado e ornamentos adicionais. Escolher apenas o que couber no tempo restante. Uma imagem de ícone existente não torna a função obrigatória no MVP.

## Distribuição sugerida, caso ainda haja 20 dias disponíveis

| Período relativo | Foco | Resultado esperado |
| --- | --- | --- |
| Dias 1–3 | T01–T05 | História escolhida, mapa e recursos conferidos |
| Dias 4–6 | T06–T09 | Estado, mundo, personagens e dados preparados |
| Dias 7–9 | T10–T12 | Primeira sequência jogável |
| Dias 10–14 | T13–T16 e avanço de T18 | Capítulo até os três finais |
| Dias 15–17 | T17–T19; T20 se couber | Roteiro revisado, salvamento e UI integrados |
| Dias 18–20 | T21–T23 e correções | Pacote validado e apresentação |

É uma estimativa de organização, condicionada ao tempo diário disponível, à localização das artes e à definição do roteiro. Reestimar após o Marco 1. Se houver atraso, retirar T24, reduzir efeitos de T20 e encurtar falas opcionais; preservar os três finais, o fluxo completo, os testes e a demonstração de POO.

## Aplicação planejada dos quatro pilares

| Pilar | Aplicação | Evidência esperada |
| --- | --- | --- |
| Abstração | Contrato `Interactable` e base `GameCharacter` | Controlador aciona contratos sem conhecer todos os detalhes concretos |
| Herança | Personagens compartilham identidade e apresentação | Especializações reutilizam a base e participam das cenas |
| Polimorfismo | Porta, documento e personagem respondem à interação | Mesma operação troca cenário, examina pista ou abre conversa |
| Encapsulamento | Estado, inventário e memórias têm alterações controladas | Coleções internas protegidas, validações e prevenção de duplicatas |

Evitar uma classe por fala, expressão ou página. O conteúdo fica nos dados; as classes representam responsabilidades e comportamentos.

## Próxima decisão

Escolher a opção principal de história em [rumos-da-historia.md](rumos-da-historia.md). Com essa escolha, T01–T03 poderão transformar as sugestões em roteiro e critérios definitivos. A auditoria de recursos e a preparação técnica não dependem dessa decisão.
