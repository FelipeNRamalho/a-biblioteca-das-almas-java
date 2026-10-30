# Catálogo de recursos — A Biblioteca das Almas

Conferência: 30/10/2026. Fontes: `src/main/resources/`, referências do código Java e [roteiro_completo.json](../roteiro_completo.json).

O contexto oficial informa que cenários, personagens, expressões, cutscenes e UI foram produzidos em pacotes separados. Este catálogo distingue essa produção da disponibilidade no repositório e do uso no jogo. **Um arquivo localizado não comprova integração narrativa ou aprovação visual.**

## 1. Estados e limites da conferência

| Estado | Significado |
| --- | --- |
| **Produzido, informado** | Existência declarada no contexto oficial; caminho local ainda não confirmado nesta conferência. |
| **Disponível no classpath de fontes** | Arquivo presente em `src/main/resources/`. Sua inclusão no JAR precisa ser conferida no pacote gerado. |
| **Referenciado pelo protótipo** | O Java atual contém uso do recurso. Isso não comprova execução do capítulo completo. |
| **Disponível, sem uso localizado** | Arquivo presente, sem referência encontrada no código atual. |
| **Mapeamento pendente** | ID narrativo conhecido, sem associação local confirmada a um arquivo. Não significa que a arte deva ser recriada. |
| **Validação visual pendente** | Conteúdo, enquadramento ou transparência efetiva ainda precisam de inspeção visual. |

Dimensões e canais da seção 2 foram lidos dos cabeçalhos PNG. RGB indica ausência de canal alfa; RGBA indica sua presença, sem comprovar que há pixels transparentes. Esta etapa não inspecionou visualmente as imagens nem verificou o conteúdo do JAR distribuído.

O caminho recebido atualmente por `AssetManager.get` é relativo a `/assets/`: `menu/salao_principal.png` corresponde a `src/main/resources/assets/menu/salao_principal.png`.

## 2. PNGs incorporados ao projeto

Há **11 PNGs** em `src/main/resources/assets/`. Todos os caminhos desta tabela são relativos a essa pasta; as dimensões são largura × altura em pixels.

| Caminho | Dimensões | Canais | Uso verificado no código |
| --- | --- | --- | --- |
| `menu/salao_principal.png` | 1672 × 941 | RGB | Fundo do menu, telas auxiliares e demonstração de exploração em `GamePanel`. |
| `menu/natasha_despertar.png` | 1672 × 941 | RGB | Ilustração do prólogo demonstrativo em `GamePanel`. |
| `ui/dialogos/caixa_dialogo.png` | 2130 × 681 | RGBA | Caixa da inspeção do livro na demonstração. |
| `ui/escolhas/escolha_normal.png` | 1008 × 210 | RGBA | Textura de botão em `GothicButton`. |
| `ui/escolhas/escolha_hover.png` | 1007 × 211 | RGBA | Textura de hover ou seleção por teclado em `GothicButton`. |
| `ui/escolhas/escolha_selecionada.png` | 1009 × 212 | RGBA | Disponível, sem uso localizado. O estado selecionado atual usa `escolha_hover.png`. |
| `ui/ornamentos/canto_inferior_direito.png` | 461 × 312 | RGBA | Ornamentação das telas auxiliares em `GamePanel`. |
| `ui/ornamentos/canto_superior_esquerdo.png` | 498 × 277 | RGBA | Ornamentação das telas auxiliares em `GamePanel`. |
| `ui/ornamentos/divisor_flor_de_lis.png` | 1166 × 135 | RGBA | Divisor da tela de créditos. |
| `ui/ornamentos/divisor_rubi.png` | 1026 × 120 | RGBA | Divisor do menu e telas auxiliares. |
| `ui/ornamentos/emblema_rosa.png` | 281 × 326 | RGBA | Emblema do menu. |

As três texturas de escolha têm dimensões ligeiramente diferentes. Ao definir áreas de clique, usar uma geometria lógica consistente; não presumir que todos os arquivos têm o mesmo tamanho original.

Também existem imagens de apresentação fora de `resources/`, como [preview_menu.png](../preview_menu.png), [previews/intro.png](../previews/intro.png) e [previews/configuracoes.png](../previews/configuracoes.png). São registros do protótipo, sem substituir fundos ou cutscenes oficiais. Não devem ser confundidas com imagens geradas por uma execução de teste desta revisão.

## 3. Os seis ambientes oficiais

| ID narrativo | Ambiente | Situação local conferida |
| --- | --- | --- |
| `entrada_biblioteca` | Entrada da Biblioteca | Fundo isolado ainda sem caminho confirmado. A ilustração do despertar está incorporada, mas não substitui automaticamente o cenário explorável. |
| `salao_principal` | Salão Principal | `menu/salao_principal.png` disponível e utilizado no protótipo. O mapeamento do ambiente no sistema narrativo ainda é pendente. |
| `balcao_rodion` | Balcão de Rodion | Arte produzida conforme o contexto; caminho local e integração pendentes. |
| `corredor_esquecidos` | Corredor dos Livros Esquecidos | Arte produzida conforme o contexto; caminho local e integração pendentes. |
| `arquivo_proibido` | Arquivo Proibido | Arte produzida conforme o contexto; caminho local e integração pendentes. |
| `camara_registros` | Câmara dos Registros | Arte produzida conforme o contexto; caminho local e integração pendentes. |

Conferir proporção, áreas relevantes e coordenadas de hotspots em cada fundo. A tela lógica atual é 1280 × 720; um arquivo com outra proporção pode sofrer recorte ao preencher a tela. Validar que portas, pistas e objetos não desapareçam nesse recorte.

## 4. Personagens e expressões referenciadas

Os quatro personagens e suas artes já foram produzidos segundo o contexto oficial. Não há PNGs de retratos em `src/main/resources/assets/personagens/` na árvore atual conferida. Os nomes abaixo são **expressões usadas no JSON**, sem equivaler a nomes de PNG confirmados.

| Personagem | Expressões usadas |
| --- | --- |
| `natasha` | `assustada`, `cansada`, `determinada`, `irritada`, `neutra`, `pensativa`, `provocativa`, `sarcastica`, `seria`, `surpresa`, `triste`, `vulneravel` |
| `rodion` | `desconfiado`, `melancolico`, `neutro`, `pensativo`, `reflexivo`, `serio` |
| `fantasma` | `assustada`, `neutra`, `sombras`, `sorriso`, `timida`, `triste` |
| `escriba` | `ameacador`, `condenando`, `derrotado`, `neutro` |

Há 28 combinações de personagem principal e expressão. Contando os identificadores de fala de memória e voz como aparecem no arquivo, são 31 pares distintos:

| Alias de fala | Expressão usada | Associação a definir no manifesto |
| --- | --- | --- |
| `natasha_memoria` | `neutra` | Identidade de Natasha em uma lembrança, com apresentação apropriada. |
| `rodion_memoria` | `neutro` | Identidade de Rodion em uma lembrança. |
| `voz_escriba` | `neutro` | Voz do Escriba; não pressupor retrato visível ou revelação antecipada. |

`narrador`, `carta`, `documento`, `inscricao`, `livro` e `registro` também falam no JSON. São fontes de texto e não exigem retratos de novos personagens.

### Critérios de conferência visual

- **Natasha:** preservar cabelo loiro-platinado curto, volumoso e ondulado, olhos rosados ou avermelhados, vestido vitoriano vinho/ameixa/preto e acessórios oficiais. Não alongar o cabelo para adequar uma expressão.
- **Rodion:** preservar cabelos escuros compridos parcialmente presos, óculos, olhos âmbar-avermelhados, trajes vitorianos escuros com dourado e gola clara.
- **Fantasma:** preservar aparência sobrenatural e triste, cabelos longos escuros com fios prateados, bandagens, vestido desgastado e sombras. Não reinterpretá-la como mal-intencionada.
- **Escriba:** preservar altura sobrenatural, halo com pontas, dedos semelhantes a penas e rosto de pergaminho **sem olhos, nariz ou boca**, inclusive nas expressões de ameaça e derrota.
- Conferir alinhamento, escala e transparência entre expressões, evitando saltos de posição ou fundos opacos ao trocar o PNG.

A conferência anterior por nomes do acervo externo, mencionada na avaliação, não substitui a localização dos caminhos e a inspeção das imagens nesta integração.

## 5. Cutscenes usadas no roteiro

São 12 nós de cutscene, com 12 IDs de imagem distintos. O ID `asset` precisa ser resolvido pelo manifesto; não é um caminho de arquivo.

| ID da imagem | Cena/nó | Situação |
| --- | --- | --- |
| `natasha_despertando` | `c01_despertar/n001` | Há uma ilustração de despertar em `menu/natasha_despertar.png`; confirmar equivalência visual e cadastrar a associação. O interpretador ainda não faz esse vínculo. |
| `rodion_lendo_livro` | `c05_bibliotecario/n001` | Mapeamento local pendente. |
| `fantasma_primeira_aparicao` | `c09_fantasma_aparicao/n001` | Mapeamento local pendente. |
| `natasha_descobrindo_livro` | `c16_livro_natasha/n001` | Mapeamento local pendente. |
| `rodion_revelacao` | `c19_confissao_rodion/n001` | Mapeamento local pendente. |
| `escriba_primeira_aparicao` | `c21_escriba_aparicao/n001` | Mapeamento local pendente. |
| `escriba_apagando_alma` | `c22_apagamento/n010` | Mapeamento local pendente. |
| `escriba_confronto` | `c23_armadilha_pacto/n001` | Mapeamento local pendente. |
| `final_queimar` | `c26_final_queimar/n014` | Mapeamento local pendente. |
| `final_preservar` | `c26_final_preservar/n015` | Mapeamento local pendente. |
| `final_reescrever` | `c26_final_reescrever/n012` | Mapeamento local pendente. |
| `escriba_derrota_final` | `c26_final_reescrever/n020` | Mapeamento local pendente. |

O contexto oficial confirma a produção das principais cutscenes e finais. Localizar os pacotes antes de criar imagens substitutas. A presença de uma ilustração de final não comprova que seu ramo esteja executável.

Os finais preservar e reescrever também narram a chegada à Entrada. Além das cutscenes, definir mudanças de fundo dentro dos epílogos: `location: camara_registros` é a localização inicial, não uma instrução para manter o mesmo fundo até o fim.

## 6. Objetos, memórias e dados complementares

| Referência | Conteúdo conhecido | Pendência |
| --- | --- | --- |
| Itens | `carta_entrada`, `fotografia_sem_nome`, `medalhao_fantasma`, `copia_clausula`, `anotacoes_rodion` | Conferir nomes, descrições, textos consultáveis e eventuais ícones nos pacotes. Não presumir que todo item tenha um PNG próprio. |
| Memórias | `retorno`, `escolha`, `pacto` | Integrar os textos do diário e os estados de descoberta. |
| `estado_inicial.json` | Arquivo informado no pacote narrativo | Localizar e conferir valores iniciais antes da integração. |
| `assets.json` | Manifesto informado no pacote narrativo | Localizar, conferir estrutura e mapear os IDs existentes para arquivos reais. |
| `interacoes_mundo.json` | Arquivo informado para exploração | Localizar e conferir hotspots, coordenadas, condições e retomada. |
| `index.json` e cenas individuais | Organização informada no pacote narrativo | Localizar e conferir consistência com o consolidado da raiz. |

Esses arquivos complementares não foram localizados na árvore de recursos atual. Sua situação é de localização e integração pendentes, sem presumir que precisem ser reescritos.

Não foram encontrados arquivos de áudio nos formatos `.wav`, `.mp3` ou `.ogg` na árvore examinada fora de saídas de compilação. Música, efeitos e controles de volume dependem da disponibilidade e integração desses recursos.

## 7. Organização e atualização do manifesto

Destinos recomendados, sujeitos à conferência dos pacotes existentes:

```text
src/main/resources/
├── assets/
│   ├── menu/                 # Recursos atuais: preservar caminhos até migrar referências
│   ├── cenarios/             # Integração planejada
│   ├── personagens/          # Por personagem e expressão
│   ├── cutscenes/             # Por função narrativa ou cena
│   └── ui/                   # UI Kit já parcialmente incorporado
└── story/
    └── assets.json           # Integração do manifesto existente
```

O formato de `assets.json` ainda precisa ser conferido. Independentemente da estrutura adotada, o mapeamento deve permitir identificar ID lógico, categoria, caminho no classpath, personagem/expressão quando aplicável e referência narrativa. Dimensões podem ser calculadas a partir dos arquivos; evitar mantê-las manualmente em vários catálogos.

Após a integração, o manifesto será a referência operacional dos caminhos. Este documento deve registrar cobertura, critérios de conferência e pendências, apontando para o manifesto em vez de duplicar suas associações completas.

Para cada incorporação:

1. Localizar o original e registrar sua procedência no projeto e os créditos confirmados.
2. Conferir conteúdo, dimensões, transparência e aderência à referência oficial.
3. Copiar para a pasta adequada sem sobrescrever outro recurso válido por engano.
4. Cadastrar o ID no manifesto e verificar os usos no roteiro.
5. Conferir carregamento, proporção e alinhamento na interface e no JAR.
6. Atualizar a situação do recurso e registrar a evidência de validação.

Nenhuma arte foi criada, substituída ou movida para produzir este catálogo. Os procedimentos de execução estão em [testes-do-capitulo.md](testes-do-capitulo.md); a semântica dos IDs e da apresentação está no [contrato narrativo](contrato-narrativo-json.md).

Commit sugerido: `docs: cataloga recursos visuais e pendencias de integracao`.
