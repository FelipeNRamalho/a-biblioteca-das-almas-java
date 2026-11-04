# Contrato narrativo JSON — Capítulo I

Referência: 30/10/2026. Fonte conferida: [roteiro_completo.json](../src/main/resources/story/roteiro_completo.json), versão `1.0`.

Este documento descreve o formato existente e o comportamento do interpretador Java. **Carregamento, condições e execução dos sete tipos de nó estão implementados. A interface libera apenas as cenas 01–04.** Os atos posteriores ainda precisam de configuração de mundo, artes e validação integrada, sem alterar a história aprovada.

A base `GameState` mantém progresso e requisitos da reescrita. `NarrativeSession` aplica os efeitos do JSON em uma cópia, valida o destino e publica o lote na instância original. Marcas `effect/cena/nó[/opção]` impedem reaplicação. `GameController` coordena as pausas e a exploração da abertura; `StoryView` apresenta o estado sem conceder recompensas.

`story/trecho_inicial.json` é uma configuração parcial, separada do consolidado: fundos, expressões de Natasha, falantes, hotspots, saídas de cena, pausa antes do livro e referências de documentos. O texto da carta e da memória é consultado nos nós originais. Os manifestos completos informados no projeto continuam pendentes de integração.

A história oficial está em [rumos-da-historia.md](rumos-da-historia.md); as tarefas estão no [planejamento](planejamento-capitulo-01.md). Este contrato trata de dados e comportamento, sem substituir essas referências.

## 1. Fonte dos dados e identificadores

O consolidado atual tem dois campos na raiz: `metadata` e `scenes`. Contém 28 entradas de cena, que representam 26 cenas oficiais: cenas 01–25 e três versões da cena 26.

O arquivo foi movido da raiz para `src/main/resources/story/roteiro_completo.json`, sem alteração de conteúdo. Ele é a fonte única carregada por `/story/roteiro_completo.json`. Índice, cenas individuais e manifestos complementares ainda precisam ser conferidos; se forem adotados, definir uma única representação como entrada do jogo e validar sua consistência com o consolidado.

- IDs de cena são únicos no roteiro, por exemplo `c02_carta`.
- IDs de nó são únicos dentro da cena, por exemplo `n002`. A identidade completa é `c02_carta/n002`.
- IDs de opções são únicos dentro do nó de escolha. Algumas opções usam nomes como `revisitar` e `reescrever`.
- IDs de itens, memórias, locais e imagens são referências lógicas; não são caminhos de arquivo por convenção implícita.
- Preservar os IDs existentes. Mudanças futuras exigem avaliar referências e compatibilidade dos saves.

## 2. Metadados e cenas

| Campo de `metadata` | Uso |
| --- | --- |
| `id`, `chapter_title`, `version`, `language` | Identificação do conteúdo. A versão do roteiro não substitui a futura versão do formato de save. |
| `entry_scene` | Cena inicial: `c01_despertar`. |
| `scene_count` | Quantidade de entradas no consolidado: 28. |
| `cutscene_count` | Quantidade de nós de cutscene: 12. |
| `special_conditions.ending_secret_requires` | Regra das cinco condições oficiais da reescrita. |
| `ending_scenes` | IDs dos três epílogos. |

| Campo de cena | Contrato |
| --- | --- |
| `id`, `number`, `title`, `act` | Identificação, número narrativo, título e agrupamento. O número 26 aparece nos três epílogos; não usar `number` como chave única. |
| `location` | Ambiente inicial da cena; não representa todas as mudanças visuais de um epílogo. |
| `optional` | Identifica conteúdo investigativo opcional. Não significa saltar a cena automaticamente: o fluxo atual contém escolhas para investigar ou adiar. |
| `kind` | Classificação declarada do conteúdo, atualmente `story`. |
| `entry_node` | Nó inicial, existente em `nodes`. |
| `replay_policy` | `once` ou `repeat_if_interactable`, sujeito às regras da seção 7. |
| `nodes` | Nós da cena. Sua ordem no array não substitui `next`, condições e destinos explícitos. |

Os ambientes referenciados são `entrada_biblioteca`, `salao_principal`, `balcao_rodion`, `corredor_esquecidos`, `arquivo_proibido` e `camara_registros`.

## 3. Tipos de nó

Todos os nós possuem `id` e `type`. Os campos abaixo descrevem o formato observado.

| Tipo | Campos específicos | Comportamento esperado |
| --- | --- | --- |
| `line` | `speaker`, `text`, `expression` quando presente, `next` | Exibir a fala. Primeiro avanço completa a revelação do texto; o seguinte segue para `next`. |
| `choice` | `prompt`, `options`, `confirmation` quando presente | Apresentar alternativas elegíveis e aguardar seleção; não avançar por clique na caixa de fala. |
| `condition` | `test`, `then`, `else` | Avaliar a condição e seguir automaticamente para um dos dois nós locais. |
| `event` | `effects`, `next` | Aplicar os efeitos e seguir automaticamente. |
| `join` | `next` | Unir ramos sem exibir uma fala adicional ou aguardar clique. |
| `cutscene` | `asset`, `next` | Exibir a ilustração e aguardar avanço para `next`. Não inferir duração ou reprodução automática: não há esses campos no formato atual. |
| `end` | `effects`, `next_scene` quando presente | Aplicar efeitos e concluir a cena. Seguir para outra cena quando indicado; epílogos encerram o capítulo sem `next_scene`. |

`next`, `then` e `else` apontam para nós da mesma cena. `next_scene` aponta para uma cena e começa em seu `entry_node`. No fluxo principal, o ambiente inicial deve estar disponível conforme o progresso ou o estado inicial; um destino indisponível exige diagnóstico, sem desbloqueio silencioso arbitrário.

Um nó automático não pode bloquear a interface em um ciclo infinito. O interpretador deve detectar ciclos sem espera ou limitar passos por atualização, informando o ID que impediu a continuação.

### Apresentação dos falantes

Os quatro personagens principais são `natasha`, `rodion`, `fantasma` e `escriba`. O roteiro também usa `natasha_memoria`, `rodion_memoria` e `voz_escriba`; o mapeamento visual deve resolver esses aliases sem criar personagens adicionais.

`narrador`, `carta`, `documento`, `inscricao`, `livro` e `registro` são fontes de texto. Não exigem retrato humano. O nome exibido e o uso de retrato dependem do mapeamento de apresentação. Uma voz não deve revelar antecipadamente um personagem apenas por compartilhar sua identidade visual.

Quando `expression` estiver presente, resolver o par falante/expressão no catálogo. Quando estiver ausente, usar a apresentação padrão explicitamente cadastrada, incluindo a possibilidade de não exibir retrato. Não carregar um PNG pela simples concatenação de um identificador desconhecido.

## 4. Escolhas, condições e confirmação

Cada opção possui `id`, `text` e exatamente um mecanismo de saída:

| Saída | Campos |
| --- | --- |
| Nó local | `next` |
| Outra cena | `next_scene` |
| Exploração | `action: "open_exploration"`, `location` e `resume_scene` |

`effects` é opcional e contém os efeitos da opção. `requires` ausente ou `null` permite selecioná-la sem condição adicional. O contrato de interface proposto é ocultar opções cujo requisito não seja satisfeito, evitando revelar a solução secreta antes da descoberta. Se nenhuma opção estiver elegível, emitir erro de fluxo em vez de exibir uma escolha impossível de concluir.

As condições presentes usam igualdade booleana de uma flag ou conjunção `all`:

```json
{
  "all": [
    { "flag": "memoria_retorno", "equals": true },
    { "flag": "fantasma_confia", "equals": true }
  ]
}
```

Esse fragmento demonstra a sintaxe; **não é a regra completa do final secreto**. `all` exige que todos os testes sejam verdadeiros. Uma flag conhecida, ainda não definida no progresso, vale `false`. Um ID desconhecido deve ser diagnosticado, para que um erro de digitação não se torne uma condição permanentemente falsa.

Este contrato cobre `flag`, `equals` e `all`. Operadores como `any`, `not`, comparação numérica ou consulta a `set_value` não aparecem no roteiro atual e não devem ser presumidos como suportados.

Na cena 25, `confirmation` contém `title`, `message`, `confirm_label` e `cancel_label`. Selecionar uma alternativa abre a confirmação; somente confirmar aplica efeitos e muda de cena. Cancelar mantém o jogador na escolha, sem efeitos. Revalidar requisitos antes de confirmar.

### Exemplo real de opção

A opção abaixo existe em `c02_carta/n002`:

```json
{
  "id": "n002_op1",
  "text": "Examinar e guardar a carta",
  "next": "n004",
  "effects": [
    { "type": "give_item", "item": "carta_entrada" },
    { "type": "unlock_memory", "memory": "retorno" },
    { "type": "set_flag", "key": "carta_coletada", "value": true },
    { "type": "set_flag", "key": "memoria_retorno", "value": true }
  ]
}
```

Selecioná-la deve atualizar o estado antes da próxima fala, permitindo que inventário e diário reflitam a investigação.

## 5. Efeitos e consistência do estado

| Efeito existente | Campos | Semântica proposta |
| --- | --- | --- |
| `set_flag` | `key`, `value` booleano | Definir uma flag conhecida, sem confundir booleanos com strings. |
| `set_value` | `key`, `value` | Gravar uma decisão ou resultado. No conteúdo atual os valores são strings, como `postura_entrada` e `final_conquistado`. |
| `give_item` | `item` | Adicionar um item conhecido a uma coleção sem duplicatas. |
| `remove_item` | `item` | Remover o item se presente. Se já estiver ausente, conservar a ausência e registrar diagnóstico de inconsistência quando a sequência exigir posse. |
| `unlock_memory` | `memory` | Liberar uma memória conhecida, sem duplicatas, mantendo a flag correspondente coerente. |
| `unlock_location` | `location` | Liberar acesso ao ambiente, sem necessariamente navegar até ele. |

As memórias `retorno`, `escolha` e `pacto` correspondem às flags `memoria_retorno`, `memoria_escolha` e `memoria_pacto`. O contrato exige equivalência entre a memória liberada e sua flag; validar o lote completo de efeitos, sem expor estados intermediários incoerentes à interface ou ao save.

Os efeitos de uma escolha ou evento formam uma unidade: validar dados, calcular o novo estado, registrar a aplicação e avançar o cursor. Se um efeito for inválido, não aplicar apenas parte do lote. A implementação deve impedir que um clique duplo ou a leitura de um save repita essa unidade.

Itens conhecidos no roteiro: `carta_entrada`, `fotografia_sem_nome`, `medalhao_fantasma`, `copia_clausula` e `anotacoes_rodion`. Seus nomes e descrições devem vir de catálogo, não de texto fixo no interpretador.

### Pendência do medalhão

Em `c12_medalhao/n002`, a entrega imediata contém `remove_item` sem coleta anterior nesse caminho. A remoção tolerante proposta impede que essa ausência interrompa a história, mas não substitui a definição da coleta: adicionar ao encontrar e remover ao entregar, ou entregar diretamente sem passagem pelo inventário. Conferir o pacote antes de corrigir os dados.

A devolução posterior deve remover o medalhão guardado e definir `medalhao_devolvido` e `fantasma_confia`. Não permitir guardá-lo de novo após a devolução. Essa disponibilidade depende das interações do mundo, ainda pendentes de integração.

## 6. Regra dos finais

Os metadados, `c24_terceira_possibilidade/n001` e a opção `reescrever` de `c25_decisao/n004` declaram a mesma conjunção:

```json
{
  "all": [
    { "flag": "memoria_retorno", "equals": true },
    { "flag": "memoria_escolha", "equals": true },
    { "flag": "memoria_pacto", "equals": true },
    { "flag": "fantasma_confia", "equals": true },
    { "flag": "regra_reescrita_descoberta", "equals": true }
  ]
}
```

A validação deve detectar divergência entre essas três declarações. `final_secreto_revelado` sinaliza uma revelação, mas não é um sexto requisito. As escolhas de personalidade também não acrescentam condições.

Os epílogos são `c26_final_queimar`, `c26_final_preservar` e `c26_final_reescrever`. Seus nós finais gravam `final_conquistado` e `capitulo_1_concluido`, além dos efeitos específicos. Entrar no epílogo não deve marcar sua conclusão antes de chegar ao respectivo `end`.

## 7. Exploração, repetição e retomada

Na abertura, terminar a cena 01 libera a escadaria para cliques. Carta e portas são interações distintas. Terminar a carta devolve o controle à exploração, sem acionar automaticamente as portas. A cena 03 segue para a 04; após a apresentação do Salão, a execução pausa em `c04_livro_mutavel/n005` e aguarda o clique no livro. A marca `opening/salon_arrival` permite voltar à Entrada e retomar esse ponto sem repetir a chegada. O fim da cena 04 encerra o trecho disponível, sem iniciar a cena 05.

A carta não coletada continua disponível nas revisitas. Depois de coletada, seu hotspot desaparece e o inventário permite releitura; a memória é consultável no diário. Uma resposta de adiamento não recebe item nem memória. Diálogos, escolhas e telas de consulta bloqueiam hotspots atrás deles.

`once` significa que uma cena concluída não deve ser iniciada novamente como novo acontecimento. Isso não impede continuar uma cena suspensa ou restaurar sua posição a partir de um save.

`repeat_if_interactable` permite acionar novamente uma interação quando o estado do mundo justificar isso. Não significa repetir todas as falas ou efeitos sem condições. As cinco cenas marcadas assim são `c02_carta`, `c07_segredos_balcao`, `c11_fotografia`, `c12_medalhao` e `c18_clausula`.

O sistema deve distinguir cena visitada, descoberta efetivamente feita e interação ainda disponível. Recusar a carta não equivale a coletá-la; guardar o medalhão não equivale a devolvê-lo. Essas diferenças permitem revisitas justas.

A opção real `revisitar`, em `c20_caminho_sem_retorno/n015`, declara:

```json
{
  "id": "revisitar",
  "text": "Investigar a biblioteca antes do confronto",
  "action": "open_exploration",
  "location": "arquivo_proibido",
  "resume_scene": "c20_caminho_sem_retorno"
}
```

O contrato proposto é guardar também o nó chamador `n015` no estado de execução. Abrir exploração suspende a escolha; ao retornar, retomá-la com os requisitos recalculados. Esse nó de retorno é informação do estado, não um campo que já exista no JSON.

Uma investigação acionada durante a revisita recebe um contexto de retorno. Ao concluí-la, voltar à exploração; não executar seu `next_scene` histórico e reiniciar o capítulo pelo encadeamento principal. Preservar todos os efeitos válidos já obtidos.

## 8. Persistência e estado inicial

O estado inicial completo ainda precisa ser integrado a partir do pacote produzido. Ele deve declarar as flags e valores conhecidos, itens e memórias iniciais e ambientes inicialmente acessíveis, incluindo a Entrada. Inferir automaticamente que todo ambiente citado está desbloqueado não é válido.

O save narrativo deve registrar versão do formato, versão/ID do roteiro, cena e nó, ambiente, fase da interface, escolhas, flags, valores, inventário, memórias, locais liberados, interações concluídas e contexto de exploração suspenso, quando houver.

Definir se a gravação acontece no meio de uma fala ou em pontos seguros. Em qualquer caso, persistir conjuntamente o resultado dos efeitos e o cursor correspondente. Carregar não reaplica efeitos do ponto anterior. Novo Jogo cria um estado limpo, preservando apenas as preferências do jogador.

`StoryProgress` define o formato narrativo 1 em `historia.json`, gravado por `SaveManager` após as ações da abertura. Inclui roteiro/versão, local, cena/nó, estado ativo, confirmação pendente, escolhas, flags, valores, inventário, memórias, locais e interações concluídas. A restauração valida um candidato antes de substituir `GameState`, sem executar efeitos. A fala retomada reinicia sua revelação de texto; inventário e diário fecham.

A gravação usa arquivo temporário e substituição atômica quando suportada. Erros são exibidos na interface. Save inválido é preservado; não há queda silenciosa para outra partida. Saves antigos em `.properties` mantêm o fluxo demonstrativo quando não existe `historia.json`. Os contextos de retorno dos atos posteriores, incluindo `open_exploration` da cena 20, ainda precisam de persistência e integração próprias; não fazem parte desse formato liberado na interface.

## 9. Validação e diagnóstico

`StoryLoader` usa Gson 2.13.2 em modo estrito, sem conversão automática de strings em booleanos ou inteiros. `StrictJson` rejeita chaves duplicadas antes de montar a árvore; `StoryValidator` confere referências e rejeita ciclos formados somente por nós automáticos, inclusive entre cenas. Campos e operadores desconhecidos são recusados. Erros semânticos identificam fonte, cena, nó/opção e campo; erros sintáticos incluem o caminho JSON.

Sem `estado_inicial.json` e os catálogos complementares, o carregador infere flags de `set_flag`/`unlock_memory`, itens de `give_item` e locais de `scene.location`. Isso não substitui os futuros catálogos: um ID errado tanto na definição quanto no uso pode permanecer consistente. `OpeningContent` acrescenta a conferência das referências de imagens, expressões e hotspots das cenas 01–04; a validação visual e dos recursos dos outros atos continua pendente.

A validação específica do Capítulo I compara a regra dos metadados, `c24_terceira_possibilidade/n001` e `c25_decisao/n004`, aceitando mudanças de ordem na conjunção e exigindo as cinco condições oficiais. O carregador não exige que os PNGs estejam disponíveis para validar os dados narrativos.

Antes de disponibilizar o capítulo, verificar:

- Sintaxe JSON, chaves duplicadas, campos obrigatórios e tipos de valores.
- Unicidade de IDs e existência de entradas, nós e cenas referenciados.
- Exatamente uma saída por opção e uma condição com destinos válidos.
- Tipos de nó, ação e efeito reconhecidos, com parâmetros válidos.
- Referências a locais, itens, memórias, falantes, expressões e imagens nos catálogos.
- Contagens de metadados e igualdade das regras do final secreto.
- Possibilidade de avanço nas escolhas e ausência de ciclos automáticos sem espera.

O erro deve indicar arquivo, cena, nó/opção e campo afetado. Não tratar conteúdo desconhecido como avanço bem-sucedido nem esconder uma falha de recurso essencial com uma imagem de outro personagem.

As verificações estruturais não substituem os procedimentos de [testes do capítulo](testes-do-capitulo.md). O [catálogo de recursos](catalogo-de-recursos.md) registra a disponibilidade visual; a [arquitetura e POO](arquitetura-e-poo.md) define a separação de responsabilidades.

### API disponível

```java
Story story = new StoryLoader().loadDefault();
Story.Scene entry = story.entryScene();
NarrativeNode first = entry.node(entry.entryNode());
GameState state = story.newGameState();
boolean canRewrite = story.metadata().secretEndingRequires().matches(state);
```

Os tipos estão em `br.com.bibliotecadasalmas.narrative`, exceto `GameState`, em `core`. Também há `load(Path)` e `load(Reader, String)`; neste último, o chamador fecha o `Reader`. Cenas, nós, opções, listas de efeitos e condições são imutáveis. Consultar esses dados ou avaliar condições não avança o cursor, aplica efeitos ou grava progresso.

Commit sugerido: `docs: adiciona contrato do sistema narrativo json`.
