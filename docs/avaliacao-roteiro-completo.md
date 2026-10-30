# Avaliação do roteiro completo — Capítulo I

Data da avaliação: 29/10/2026. Arquivo analisado: [roteiro_completo.json](../roteiro_completo.json), versão declarada `1.0`, capítulo **O Nome que Não Deveria Existir**.

## Conclusão

O roteiro é suficiente para continuar a implementação. Ele define o capítulo inteiro e desenvolve o rumo B apresentado no planejamento: Natasha participou do pacto, perdeu suas memórias e precisa enfrentar as consequências.

O arquivo pode servir como base narrativa para desenvolver o carregador de roteiro, o sistema de diálogos e a primeira sequência jogável. Para transformá-lo em exploração completa, ainda é necessário complementar os dados e as regras descritos abaixo.

## Verificações realizadas

- JSON válido, sem chaves duplicadas ou referências quebradas entre nós e cenas.
- 28 cenas narrativas distribuídas nos seis ambientes planejados.
- 620 nós, incluindo 543 falas, 12 pontos de escolha e 12 entradas de cutscene.
- Três finais alcançáveis em uma simulação do fluxo narrativo.
- Requisitos do final secreto consistentes nos metadados, na revelação da terceira possibilidade e na escolha final.
- As 28 combinações de personagem e expressão usadas possuem arquivos correspondentes por nome no acervo externo disponível.

A simulação considerou flags ausentes como falsas e os efeitos de inventário como operações que não bloqueiam o avanço. A ação `open_exploration` foi tratada como uma saída para um sistema externo, sem simular revisitas. Portanto, os resultados validam o fluxo narrativo sob essas premissas, não a execução dentro do jogo. A correspondência de expressões foi conferida por nome de arquivo, sem validação visual das imagens nesta análise.

## Complementos necessários para a exploração jogável

| Ponto | O que falta definir |
| --- | --- |
| Navegação e objetos clicáveis | O roteiro avança principalmente por `next_scene`. Mapear portas, personagens e objetos aos pontos clicáveis de cada ambiente, incluindo condições de acesso. |
| Retorno à investigação | A opção `open_exploration` existe em `c20_caminho_sem_retorno/n015`, antes do confronto. Definir a navegação e como retomar essa cena após investigar. |
| Repetição das cenas opcionais | `repeat_if_interactable` indica a intenção de permitir revisitas. Definir quando cada interação continua disponível e impedir recompensas repetidas ou o reinício indevido da sequência principal. |
| Inventário e memórias | Os identificadores existem. Acrescentar nomes de exibição, descrições e textos consultáveis no diário. |
| Apresentação visual | Ligar identificadores às imagens e definir mudanças de fundo dentro das cenas. Alguns epílogos chegam à Entrada, embora a localização declarada da cena continue sendo a Câmara. |

As cinco cenas marcadas como opcionais e repetíveis são `c02_carta`, `c07_segredos_balcao`, `c11_fotografia`, `c12_medalhao` e `c18_clausula`. Elas fazem parte do encadeamento principal atual; a opcionalidade é expressa pelas escolhas de investigar ou seguir. O sistema de exploração precisa preservar essa intenção ao permitir retornos.

## Ajuste concreto: medalhão

Em `c12_medalhao/n002`, a opção de devolver o medalhão executa `remove_item` para `medalhao_fantasma`, mas o objeto ainda não foi adicionado ao inventário nesse caminho.

Há duas soluções possíveis:

- Adicionar o medalhão ao encontrá-lo e removê-lo quando for devolvido.
- Tratar a devolução direta sem remoção do inventário, adicionando o objeto apenas quando Natasha escolher guardá-lo.

A solução também deve contemplar uma devolução posterior, caso o jogador tenha guardado o medalhão. Repetir a interação não deve duplicar o objeto nem permitir guardá-lo novamente após a devolução.

## Avaliação narrativa

A sequência de pistas funciona: carta → fotografia → pacto → confissão → confronto. A Fantasma participa da solução, e Rodion reconhece sua responsabilidade sem exigir perdão.

Dois refinamentos podem fortalecer as consequências das escolhas:

- **Reações posteriores às decisões.** As escolhas de postura e relacionamento alteram as respostas imediatas, mas os valores `postura_entrada`, `rodion_abordagem`, `fantasma_primeira_reacao` e `rodion_confissao_resposta` não são consultados posteriormente no roteiro. Algumas falas do confronto ou dos epílogos poderiam reconhecer essas decisões.
- **Custo do final secreto.** O encerramento deixa memórias irrecuperáveis, mas não esclarece se isso é consequência da reescrita ou do apagamento anterior. Explicitar essa diferença caso se queira um custo próprio para esse final. Essa é uma sugestão narrativa, não uma alteração já aprovada.

## Próximos passos recomendados

1. Manter `roteiro_completo.json` como base narrativa.
2. Completar os dados de exploração, recursos visuais, itens e memórias em arquivos separados.
3. Definir a semântica de revisitas, retomada e aplicação única dos efeitos.
4. Resolver o comportamento do medalhão.
5. Implementar o carregador e o sistema de diálogos, integrando primeiro o despertar, a carta, as portas e a chegada ao Salão.
6. Validar os três finais no jogo, incluindo retornos à investigação e salvamento/retomada.

Não é necessário reescrever a história para começar essa implementação. O roteiro não foi alterado durante a avaliação nem ao registrar este documento.
