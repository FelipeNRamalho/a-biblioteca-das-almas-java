# Avaliação do roteiro completo — A Biblioteca das Almas

**Capítulo I: O Nome que Não Deveria Existir**

Avaliação original: 29/10/2026. Revisão contextualizada: 30/10/2026.

Fonte narrativa analisada: [roteiro_completo.json](../roteiro_completo.json), versão declarada `1.0`.

Este documento avalia a coerência do roteiro com o contexto oficial do projeto e identifica o trabalho necessário para integrá-lo à aplicação Java existente. Contém spoilers dos três finais.

## 1. Parecer e escopo

O roteiro oferece uma base consistente para implementar o Capítulo I. A história, os quatro personagens, os seis ambientes e os três desfechos já estão definidos; a prioridade é transformar esse conteúdo em uma experiência jogável, preservando o menu, o prólogo e os recursos que já funcionam.

A experiência permanece a de uma visual novel gótica: cenários ilustrados estáticos, exploração por cliques, diálogos, escolhas e cutscenes, com Java 21, Swing, Graphics2D e Maven. A avaliação não propõe movimentação livre, combate ou adoção de uma engine externa.

Os documentos [rumos-da-historia.md](rumos-da-historia.md) e [planejamento-capitulo-01.md](planejamento-capitulo-01.md) registram uma etapa anterior de propostas. Onde ainda tratam a história e os requisitos do final secreto como decisões pendentes, devem ser lidos como histórico. O contexto oficial fornecido para esta revisão estabelece essas decisões e orienta este parecer.

**Situação atual:** o roteiro está estruturado em JSON, mas o capítulo completo ainda não está integrado ao jogo. A validade dos dados e a existência das artes, por si sós, não comprovam diálogos, exploração ou finais executáveis.

## 2. Coerência com a história oficial

A pergunta central — **quem tem o direito de decidir como uma história deve terminar?** — conecta a investigação de Natasha ao conflito com o Escriba. A progressão carta → fotografia → pacto → confissão → confronto permite reinterpretar pistas anteriores sem substituir a premissa inicial.

| Elemento | Avaliação e orientação para a integração |
| --- | --- |
| **Natasha Rostova** | O Pacto da Âncora nasceu de uma tentativa de preservar almas ameaçadas pelo esquecimento. Sua participação traz responsabilidade e consequências, sem torná-la culpada por todas as ações posteriores do Escriba. Recuperar autonomia é o centro de sua trajetória. |
| **Rodion Raskólnikov** | A confissão em `c19_confissao_rodion` reconhece sua ajuda anterior e sua decisão de ocultar a verdade por medo. Preservar essa ambiguidade: ele não é vilão nem traidor, e compreender sua motivação não obriga Natasha a perdoá-lo. |
| **A Fantasma** | As sombras são consequência de ter preservado fragmentos de histórias apagadas. Sua gentileza contrasta com a aparência assustadora; a confiança conquistada permite seu testemunho no final secreto. Ela participa da solução como personagem com vontade própria. |
| **O Escriba** | Defende o controle dos registros como garantia de continuidade. O confronto deve preservar seu tom frio, formal e autoritário e a oposição entre preservação compulsória e liberdade. |

As escolhas de postura já produzem respostas imediatas, mas `postura_entrada`, `rodion_abordagem`, `fantasma_primeira_reacao` e `rodion_confissao_resposta` são apenas gravadas, sem consultas posteriores no roteiro atual. Retomá-las em falas futuras pode fortalecer a percepção de consequência. Esse refinamento é opcional e não deve acrescentar condições ao final secreto.

### Os três desfechos devem ser preservados

| Final | Resultado narrativo obrigatório |
| --- | --- |
| **I — A Última Página** | Natasha queima seu livro, desfaz o pacto, liberta as almas e desaparece. O encerramento mantém o caráter trágico do sacrifício. |
| **II — Entre Páginas Fechadas** | Natasha preserva sua existência, mas o pacto e o aprisionamento continuam. A narrativa reconhece o peso dessa escolha sem reduzir o desejo de sobreviver a uma falha moral simples. |
| **III — A História que Ainda Não Foi Escrita** | A cláusula original, o reconhecimento do pacto e o testemunho da Fantasma permitem devolver a autoria às almas. O Escriba é derrotado, as almas são libertadas, a Fantasma deixa de carregar sozinha as histórias e Natasha permanece viva. |

O final secreto já preserva a consequência emocional aprovada: Natasha **não recupera todas as memórias** e aceita que parte do passado permanecerá incerta. Isso aparece em `c24_terceira_possibilidade` e no diálogo final com Rodion em `c26_final_reescrever`. Não é necessário acrescentar uma nova perda ou atribuir à reescrita um novo apagamento. A libertação também não precisa eliminar instantaneamente todas as marcas da Fantasma.

## 3. Estrutura e verificações do JSON

A contagem correta é **26 cenas narrativas oficiais, representadas por 28 entradas no JSON consolidado**: as cenas 01 a 25 e três versões da cena 26. Isso não significa que a história ganhou duas cenas adicionais. O arquivo consolidado foi localizado na raiz do repositório; a divisão em arquivos individuais de cena ainda não foi conferida nesta revisão.

| Parte do capítulo | Cenas | Função narrativa |
| --- | --- | --- |
| Prólogo — O Despertar | 01–03 | Despertar, carta, Memória 1 e entrada na biblioteca. |
| Ato I — A Biblioteca Silenciosa | 04–07 | Livro mutável, encontro com Rodion e primeiras suspeitas. |
| Ato II — As Histórias Esquecidas | 08–12 | Encontro com a Fantasma, fotografia, Memória 2 e medalhão. |
| Ato III — O Nome no Livro | 13–19 | Passagem, registros, Memória 3, cláusula e confissão. |
| Ato IV — O Autor das Sentenças | 20–25 | Última oportunidade de investigar, confronto e decisão. |
| Epílogos | 26, em três versões | Consequências de queimar, preservar ou reescrever. |

Na revisão dos dados foram confirmados:

- JSON sintaticamente válido, com 28 identificadores de cena distintos e sem IDs de nó repetidos dentro de uma mesma cena.
- Referências `entry_node`, `next`, `then`, `else`, `next_scene` e `resume_scene` apontando para destinos existentes.
- **620 nós:** 543 de fala (`line`), 12 de escolha (`choice`), 12 de cutscene, 8 de condição, 18 de junção (`join`), 1 de evento e 26 de encerramento (`end`).
- Referências aos seis ambientes oficiais, de `entrada_biblioteca` a `camara_registros`.
- As mesmas cinco condições do final secreto nos metadados, na revelação da cena 24 e na opção de reescrita da cena 25.
- Os três epílogos alcançáveis em uma simulação simplificada do fluxo narrativo.

**Limites da verificação:** a simulação considerou flags ausentes como falsas, aplicou `set_flag` e avaliou condições e escolhas. Não simulou inventário, interface, confirmação da decisão final ou salvamento. `open_exploration` foi tratado como saída para um sistema externo, sem executar revisitas. Portanto, confirma a possibilidade de alcançar os finais sob essas premissas, sem validar uma partida completa no Java.

O JSON também referencia expressões e identificadores de cutscene. A correspondência visual, a transparência dos PNGs e a disponibilidade de todas as artes no pacote executável precisam ser validadas durante a integração. A conferência anterior por nomes de arquivos do acervo externo não substitui essa etapa.

## 4. Estado real da implementação

A leitura do código atual confirma a separação entre o protótipo e o conteúdo narrativo preparado:

| Área | Evidência no repositório | Trabalho restante |
| --- | --- | --- |
| Menu e prólogo | `GameController` controla menu, configurações, créditos, prólogo de três páginas e passagem para a demonstração de exploração. | Preservar esses fluxos e conectar Novo Jogo ao roteiro definitivo. |
| Exploração | `GamePanel` apresenta uma demonstração no Salão Principal, com inspeção de livro. | Implementar os seis ambientes, hotspots, desbloqueios e retornos. |
| Roteiro | `roteiro_completo.json` está na raiz; não foi localizado um carregador narrativo no Java atual. | Interpretar os nós e conectar condições, efeitos, escolhas e apresentação. |
| Salvamento | `SaveManager` grava modo e página do prólogo em `.properties`, além das preferências. | Persistir cenário, ponto narrativo, inventário, memórias, escolhas e estados. |
| Recursos visuais | `src/main/resources/assets/` contém o Salão, o despertar e parte do UI Kit. | Incorporar e mapear os demais fundos, personagens, expressões e cutscenes já produzidos. |
| Testes | `MenuSmokeTest` verifica o protótipo por um método `main`. | Validar o capítulo e suas rotas; o teste atual não comprova a execução da história completa. |

O contexto oficial informa que as artes e o pacote narrativo complementar já foram produzidos separadamente. A ausência de um recurso no classpath atual indica uma pendência de localização ou integração, sem justificar sua recriação automática.

## 5. Requisitos do final secreto e exploração justa

O final III exige **exatamente cinco condições**:

| Condição oficial | Estado usado no JSON | Origem |
| --- | --- | --- |
| Memória 1 — O Retorno | `memoria_retorno` | Carta da Entrada, `c02_carta`. |
| Memória 2 — A Escolha | `memoria_escolha` | Fotografia do Corredor, `c11_fotografia`. |
| Memória 3 — O Pacto | `memoria_pacto` | Livro de Natasha no Arquivo, `c16_livro_natasha`. |
| Confiança da Fantasma | `fantasma_confia` | Devolução do medalhão em `c12_medalhao`. |
| Cláusula original de reescrita | `regra_reescrita_descoberta` | Documento em `c18_clausula`. |

A Memória 3 integra o percurso obrigatório atual. Carta, fotografia, confiança e cláusula dependem de investigação ou decisão do jogador. As respostas de personalidade não são requisitos secretos; uma reação isolada de medo, desconfiança ou rudeza não deve bloquear permanentemente a confiança da Fantasma.

O roteiro prevê retorno antes do confronto em `c20_caminho_sem_retorno/n015`, por meio de `open_exploration`, com `resume_scene` apontando para a própria cena 20. Essa intenção ainda precisa ser convertida em navegação funcional entre os ambientes desbloqueados.

As cenas `c02_carta`, `c07_segredos_balcao`, `c11_fotografia`, `c12_medalhao` e `c18_clausula` estão marcadas como opcionais e `repeat_if_interactable`. Elas continuam no encadeamento por `next_scene`; a possibilidade de adiar a investigação aparece nas escolhas internas. A política de repetição, sozinha, não implementa o retorno.

Para preservar a progressão, as revisitas devem:

- Permitir recuperar pistas ignoradas e devolver o medalhão guardado antes de entrar no confronto.
- Conservar descobertas anteriores, sem duplicar itens, memórias ou recompensas.
- Retomar a exploração ou o ponto narrativo suspenso, sem executar novamente toda a sequência principal pelo `next_scene` original da cena revisitada.
- Manter o acesso aos finais I e II mesmo quando faltarem requisitos do final III.

## 6. Pendências concretas para a integração

### Medalhão e aplicação dos efeitos

Em `c12_medalhao/n002`, devolver o medalhão executa `remove_item` para `medalhao_fantasma`, embora ele ainda não tenha sido adicionado ao inventário nesse caminho. O impacto depende da implementação: uma remoção tolerante pode passar silenciosamente; uma validação estrita pode interromper a interação.

Recomenda-se registrar a coleta ao encontrar o objeto e removê-lo na devolução. Outra solução coerente é tratar a entrega imediata sem passagem pelo inventário e adicionar o item apenas quando Natasha escolher guardá-lo. Em ambos os casos, definir a devolução posterior, impedir duplicações e impedir que o objeto volte a ser guardado após `medalhao_devolvido`.

Memórias também aparecem como efeitos `unlock_memory` e flags `memoria_*`. O estado da partida deve manter essas representações coerentes para que o diário e a disponibilidade do final secreto nunca discordem.

### Contrato do interpretador narrativo

O carregador precisa preservar os identificadores existentes e executar os tipos presentes no arquivo: `line`, `choice`, `condition`, `event`, `join`, `cutscene` e `end`. Seu contrato deve definir:

- Avaliação de `requires`, `all` e testes de flags, com valores iniciais explícitos.
- Aplicação dos efeitos `set_flag`, `set_value`, `give_item`, `remove_item`, `unlock_memory` e `unlock_location` no momento correto.
- Distinção entre avançar um nó, trocar de cena narrativa e abrir a exploração.
- Tratamento de `once` e `repeat_if_interactable`, sem reaplicar efeitos indevidamente ao carregar uma partida.
- Confirmação e cancelamento da decisão final previstos em `c25_decisao`, antes de iniciar o epílogo.

Enquanto uma fala, escolha ou cutscene estiver ativa, cliques não devem alcançar hotspots atrás da interface. Completar a exibição do texto e avançar para a fala seguinte precisam ter comportamentos previsíveis.

### Dados complementares e recursos visuais

Antes de criar arquivos, localizar o pacote narrativo e os PNGs já produzidos. Os caminhos abaixo são destinos de integração recomendados, ainda sujeitos à conferência desse material:

| Destino | Responsabilidade |
| --- | --- |
| `src/main/resources/story/` | Roteiro e arquivos complementares; incorporar `index.json` e cenas individuais se essa organização for adotada. Evitar versões divergentes do consolidado e das cenas. |
| `story/estado_inicial.json` | Flags, valores e condições iniciais da partida. |
| `story/interacoes_mundo.json` | Hotspots, coordenadas, portas, condições de acesso e cenas acionadas. |
| `story/assets.json` | Mapeamento de ambientes, retratos, expressões e cutscenes para recursos do classpath. |
| Catálogo de itens e diário em `story/` | Nomes, descrições e textos consultáveis, liberados conforme as descobertas. Reutilizar esses dados caso já existam no pacote. |
| `src/main/resources/assets/` | PNGs organizados por cenário, personagem, cutscene e função da interface. |

O mapeamento deve contemplar falantes como `natasha_memoria`, `rodion_memoria` e `voz_escriba`, sem pressupor que cada identificador representa um personagem adicional. Também é necessário definir mudanças de fundo dentro das cenas: os epílogos podem chegar à Entrada mesmo declarando a Câmara como localização inicial.

Reutilizar o UI Kit gótico e preservar as ilustrações oficiais. Natasha mantém cabelo curto loiro-platinado e seu vestido aprovado; a Fantasma permanece sobrenatural e triste; o Escriba tem rosto de pergaminho **sem olhos, nariz ou boca**. Seus estados visuais devem expressar ameaça ou derrota sem acrescentar feições humanas.

## 7. Evolução da arquitetura e demonstração de POO

A implementação pode crescer a partir das classes atuais, mantendo conteúdo narrativo em JSON e regras de execução no Java. A escolha de um leitor JSON deve ser justificada pela necessidade do carregador, sem introduzir uma engine ou dependências alheias ao escopo.

| Pilar | Evidência atual | Expansão planejada para o capítulo |
| --- | --- | --- |
| **Encapsulamento** | `GameController`, `SaveManager` e `GameSettings` concentram estado e operações. | `GameState` com campos privados e métodos controlados para progresso, inventário e memórias. |
| **Herança** | `GothicButton` e `TextOption` derivam de `AbstractControl`. | Classe abstrata de personagem com subclasses Natasha, Rodion, Fantasma e Escriba, com responsabilidades claras. |
| **Abstração** | `ClickTarget` define o contrato dos controles clicáveis. | Contrato `Interactable` para objetos do mundo e separação entre dados narrativos, execução e renderização. |
| **Polimorfismo** | Os controles respondem ao mesmo contrato de desenho e interação. | Livros, portas, personagens e objetos respondem a uma interação comum: investigar, navegar ou conversar. |

Essas expansões ainda não estão implementadas. Na apresentação acadêmica, os exemplos devem apontar para classes e comportamentos efetivos, distinguindo-os das intenções de arquitetura.

O `GameController` pode coordenar o fluxo, enquanto o sistema narrativo interpreta os dados e o `GamePanel` renderiza o estado. Ampliar `SaveManager` para restaurar o ponto narrativo e os efeitos já aplicados, definindo também como tratar os saves antigos do protótipo.

## 8. Ordem recomendada de desenvolvimento

1. **Implementar o carregador e o sistema de diálogos.** Validar referências, apresentar falas, retratos e expressões, processar escolhas, condições e efeitos com o estado encapsulado.
2. **Concluir a primeira sequência jogável.** Integrar menu → despertar → carta → portas → Salão, incluindo a Memória 1 e o adiamento da coleta. Esse é o primeiro marco verificável da integração.
3. **Integrar exploração, inventário e diário.** Incorporar os seis cenários, hotspots e desbloqueios progressivamente, com revisitas e devolução posterior do medalhão.
4. **Ampliar o salvamento durante a integração.** Restaurar progresso e escolhas sem duplicar efeitos ou perder pistas já descobertas.
5. **Conectar cutscenes, confronto e finais.** Aplicar as cinco condições oficiais e preservar as consequências de cada epílogo.
6. **Validar o capítulo completo e ajustar a apresentação.** Conferir rotas, legibilidade, redimensionamento, recursos no JAR e transições; integrar áudio conforme os arquivos disponíveis.

## 9. Critérios de validação do capítulo

Os cenários abaixo são verificações a executar após a integração. Não representam testes já aprovados no jogo atual.

| Cenário de validação | Resultado esperado |
| --- | --- |
| Iniciar pelo menu | Entrar no despertar e seguir a sequência oficial, preservando configurações, créditos e controles existentes. |
| Jogar sem completar a investigação | Chegar à Câmara e poder queimar ou preservar o livro. |
| Completar as cinco condições | Ver a terceira possibilidade, selecionar reescrever e obter o epílogo aprovado. |
| Verificar cada requisito ausente | A regra de reescrita recusa cada combinação incompleta. Para a Memória 3, obrigatória no fluxo atual, verificar também a condição diretamente. |
| Adiar carta, fotografia ou cláusula | Retornar antes do confronto, investigar e atualizar corretamente os requisitos. |
| Reagir com medo e guardar o medalhão | Poder construir a confiança depois, pela devolução, sem bloqueio permanente pela reação inicial. |
| Repetir uma interação concluída | Não duplicar itens ou memórias, não recuperar o medalhão devolvido e não reiniciar atos já concluídos. |
| Salvar e continuar | Restaurar ponto narrativo, cenário, itens, memórias e escolhas, sem reaplicar efeitos. |
| Cancelar a decisão final | Permanecer na escolha, sem executar efeitos ou iniciar um epílogo. |
| Executar o JAR fora da pasta de fontes | Carregar os recursos integrados, manter hotspots alinhados ao redimensionar e apresentar textos legíveis. |

Esta revisão alterou apenas a avaliação. O roteiro JSON, o código Java e as artes foram preservados. Não houve execução da interface ou dos testes Java nesta etapa; as verificações realizadas foram de arquivos, código e fluxo narrativo simplificado.

Commit sugerido: `docs: contextualiza avaliacao do roteiro com a historia oficial`.
