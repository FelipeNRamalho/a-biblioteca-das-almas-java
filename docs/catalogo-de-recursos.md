# Catálogo de recursos — A Biblioteca das Almas

Conferência: **11/11/2026**, commit `389433f`. Fontes: arquivos em `src/main/resources/assets`, [manifesto do capítulo](../src/main/resources/story/trecho_inicial.json) e classes de `characters`.

## 1. Cobertura e limites

Há **71 PNGs** no classpath de fontes. A revisão conferiu os cabeçalhos PNG e os caminhos referenciados; RGBA indica presença de canal alfa, sem comprovar transparência efetiva em todos os pixels. Arquivo disponível, caminho cadastrado e imagem visualmente aprovada são estados diferentes.

| Grupo | PNGs |
| --- | --- |
| `menu` | 2 |
| `cenarios` | 1 |
| `ui` | 9 |
| `personagens/natasha` | 28 |
| `personagens/rodion` | 9 |
| `personagens/fantasma` | 9 |
| `personagens/escriba` | 10 |
| `cutscenes/finais` | 3 |

O manifesto resolve **12 cutscenes**, **seis locais** e **oito documentos**. Seus 41 caminhos distintos de imagem foram encontrados. `CharacterRegistry` acrescenta o mapeamento dos personagens; o controlador prioriza esses retratos e usa o manifesto como alternativa.

`AssetManager.get` recebe caminhos relativos a `/assets/`. O carregamento é sob demanda com cache, não pré-carregamento integral do acervo.

## 2. Ambientes e fundos usados

| Local lógico | Fundo atual | Situação |
| --- | --- | --- |
| `entrada_biblioteca` | `cenarios/entrada_biblioteca.png` | Fundo próprio e hotspots de carta/portas. |
| `salao_principal` | `menu/salao_principal.png` | Livro, Balcão, retorno e passagem condicional do Arquivo. |
| `balcao_rodion` | `menu/salao_principal.png` | Local integrado; fundo específico pendente. |
| `corredor_esquecidos` | `menu/salao_principal.png` | Fotografia e Fantasma integradas; fundo específico pendente. |
| `arquivo_proibido` | `menu/salao_principal.png` | Cláusula e acesso condicional à Câmara; fundo específico pendente. |
| `camara_registros` | `menu/salao_principal.png` | Confronto e epílogos configurados; fundo específico pendente. |

A presença de seis locais não equivale à presença de seis fundos distintos. Cutscenes substituem o fundo apenas durante seus nós. A mudança de fundo ao longo das falas finais ainda precisa de conferência visual.

## 3. Personagens, expressões e aliases

| Classe | Expressões cadastradas | Comportamento |
| --- | --- | --- |
| Natasha | 15 | Expressões em `personagens/natasha/expressoes/`; oito arquivos antigos continuam presentes para referências do manifesto. |
| Rodion | 6 | Nome “Uma voz” e sem retrato na cena 04; identidade padrão nas demais cenas. |
| Fantasma | 6 | Nome “A Fantasma” e retratos próprios. |
| Escriba | 5 | Retratos de rosto sem feições; não há regra que oculte todas as suas imagens. |

`natasha_memoria` e `rodion_memoria` reutilizam as identidades para retratos e recebem nomes de memória. `voz_escriba` não tem retrato. Narrador, carta, inscrição, livro, documento e registro são fontes textuais.

Preservar identidade, escala e alinhamento entre expressões. O Escriba deve conservar o rosto sem olhos, nariz ou boca; o nome de um arquivo não comprova esse conteúdo. Corpos inteiros e algumas artes alternativas estão disponíveis sem uso localizado na apresentação atual.

## 4. Inventário de PNGs

Dimensões lidas dos cabeçalhos. “Manifesto” e “Registro” indicam referências encontradas; não significam que cada arquivo foi inspecionado visualmente durante uma partida.

| Caminho relativo a `assets/` | Dimensões | Canais | Referência localizada |
| --- | --- | --- | --- |
| `cenarios/entrada_biblioteca.png` | 1672 × 941 | RGB | Manifesto |
| `cutscenes/finais/Final_01_Queimar_Livro.png` | 1672 × 941 | RGB | Manifesto |
| `cutscenes/finais/Final_02_Preservar_Livro.png` | 1672 × 941 | RGB | Manifesto |
| `cutscenes/finais/Final_03_Reescrever_Historia.png` | 1672 × 941 | RGB | Manifesto |
| `menu/natasha_despertar.png` | 1672 × 941 | RGB | Manifesto |
| `menu/salao_principal.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/escriba/corpo_inteiro/Escriba_Corpo_Inteiro.png` | 1024 × 1536 | RGBA | Sem uso localizado |
| `personagens/escriba/cutscenes/Escriba_Apagando_Alma.png` | 1671 × 941 | RGB | Manifesto |
| `personagens/escriba/cutscenes/Escriba_Confronto.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/escriba/cutscenes/Escriba_Derrota_Final_Secreto.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/escriba/cutscenes/Escriba_Primeira_Aparicao.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/escriba/expressoes/Escriba_Ameacador.png` | 1536 × 1024 | RGBA | Registro de personagens |
| `personagens/escriba/expressoes/Escriba_Condenando.png` | 1536 × 1024 | RGBA | Registro de personagens |
| `personagens/escriba/expressoes/Escriba_Derrotado.png` | 1024 × 1536 | RGBA | Registro de personagens |
| `personagens/escriba/expressoes/Escriba_Neutro.png` | 1536 × 1024 | RGBA | Registro de personagens |
| `personagens/escriba/expressoes/Escriba_Rindo.png` | 1536 × 1024 | RGBA | Registro de personagens |
| `personagens/fantasma/corpo_inteiro/Fantasma_Corpo_Inteiro.png` | 1024 × 1536 | RGBA | Sem uso localizado |
| `personagens/fantasma/cutscenes/Fantasma_Primeira_Aparicao_Corredor.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/fantasma/cutscenes/Fantasma_Visao_Capela.png` | 1672 × 941 | RGB | Sem uso localizado |
| `personagens/fantasma/expressoes/Fantasma_Assustada.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/fantasma/expressoes/Fantasma_Neutra.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/fantasma/expressoes/Fantasma_Sombras.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/fantasma/expressoes/Fantasma_Sorriso.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/fantasma/expressoes/Fantasma_Timida.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/fantasma/expressoes/Fantasma_Triste.png` | 1133 × 1388 | RGBA | Registro de personagens |
| `personagens/natasha/assustada.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/cansada.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/corpo_inteiro/Natasha_Corpo_Inteiro_Original.png` | 1024 × 1536 | RGB | Sem uso localizado |
| `personagens/natasha/corpo_inteiro/Natasha_Corpo_Inteiro_Transparente.png` | 1024 × 1536 | RGBA | Sem uso localizado |
| `personagens/natasha/cutscenes/Natasha_Descobrindo_Proprio_Livro.png` | 1672 × 941 | RGB | Manifesto |
| `personagens/natasha/cutscenes/Natasha_Despertando_Entrada.png` | 1672 × 941 | RGB | Sem uso localizado |
| `personagens/natasha/cutscenes/Natasha_Ilustracao_Biblioteca.png` | 1672 × 941 | RGB | Sem uso localizado |
| `personagens/natasha/determinada.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/expressoes/Natasha_Assustada.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Cansada.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Chorando.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Determinada.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Envergonhada.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Feliz.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Irritada.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Neutra.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Pensativa.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Provocativa.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Sarcastica.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Seria.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Surpresa.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Triste.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/expressoes/Natasha_Vulneravel.png` | 768 × 768 | RGBA | Registro de personagens |
| `personagens/natasha/neutra.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/pensativa.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/seria.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/surpresa.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/natasha/vulneravel.png` | 768 × 768 | RGBA | Manifesto |
| `personagens/rodion/corpo_inteiro/Rodion_Corpo_Inteiro.png` | 1024 × 1536 | RGBA | Sem uso localizado |
| `personagens/rodion/cutscenes/Rodion_Lendo_Livro.png` | 1920 × 1080 | RGB | Manifesto |
| `personagens/rodion/cutscenes/Rodion_Revelacao.png` | 1920 × 1080 | RGB | Manifesto |
| `personagens/rodion/expressoes/Rodion_Desconfiado.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `personagens/rodion/expressoes/Rodion_Melancolico.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `personagens/rodion/expressoes/Rodion_Neutro.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `personagens/rodion/expressoes/Rodion_Pensativo.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `personagens/rodion/expressoes/Rodion_Reflexivo.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `personagens/rodion/expressoes/Rodion_Serio.png` | 1254 × 1254 | RGBA | Registro de personagens |
| `ui/dialogos/caixa_dialogo.png` | 2130 × 681 | RGBA | Interface |
| `ui/escolhas/escolha_hover.png` | 1007 × 211 | RGBA | Interface |
| `ui/escolhas/escolha_normal.png` | 1008 × 210 | RGBA | Interface |
| `ui/escolhas/escolha_selecionada.png` | 1009 × 212 | RGBA | Sem uso localizado |
| `ui/ornamentos/canto_inferior_direito.png` | 461 × 312 | RGBA | Interface |
| `ui/ornamentos/canto_superior_esquerdo.png` | 498 × 277 | RGBA | Interface |
| `ui/ornamentos/divisor_flor_de_lis.png` | 1166 × 135 | RGBA | Interface |
| `ui/ornamentos/divisor_rubi.png` | 1026 × 120 | RGBA | Interface |
| `ui/ornamentos/emblema_rosa.png` | 281 × 326 | RGBA | Interface |

## 5. Documentos, memórias e cutscenes

Os oito documentos consultáveis são `carta_entrada`, `fotografia_sem_nome`, `medalhao_fantasma`, `copia_clausula`, `anotacoes_rodion` e as memórias `retorno`, `escolha`, `pacto`. Seus textos são recuperados de nós do roteiro. Cada documento só fica disponível se o estado contiver o item ou a memória correspondente.

Os 12 IDs de cutscene são `natasha_despertando`, `rodion_lendo_livro`, `fantasma_primeira_aparicao`, `natasha_descobrindo_livro`, `rodion_revelacao`, `escriba_primeira_aparicao`, `escriba_apagando_alma`, `escriba_confronto`, `final_queimar`, `final_preservar`, `final_reescrever` e `escriba_derrota_final`. Todos possuem caminho no manifesto; evitar duplicar esse mapeamento em um segundo JSON.

Os complementos `assets.json`, `estado_inicial.json`, `interacoes_mundo.json`, índice e cenas individuais informados no histórico não estão integrados como entradas operacionais. O jogo usa `roteiro_completo.json` e `trecho_inicial.json`; a ausência de outro manifesto não implica ausência dos mapeamentos atuais.

## 6. Distribuição, procedência e pendências

`dist/BibliotecaDasAlmas-Menu.jar` é o pacote atual, com recursos e Gson, gerenciado por Git LFS. O arquivo materializado nesta revisão tem **122.162.585 bytes**. Clones que tenham apenas o ponteiro precisam baixar o conteúdo com `git lfs pull`.

Não foram encontrados arquivos de áudio na árvore de fontes. Áudio pré-carregado, som de colisão e volume continuam pendentes.

O histórico registra a Entrada proveniente de `cenarios-jogo.zip` e oito retratos antigos de `poses-natasha.zip`, preservando os PNGs. O commit atual acrescenta os demais recursos; créditos e procedência de cada pacote devem ser completados pela dupla, sem presumir autoria por nome de pasta.

As imagens da raiz e de `previews/` são registros anteriores. As novas imagens de teste ficam em `target/validacao-docs-2026-11-11/`; detalhes e limites estão em [Testes do capítulo](testes-do-capitulo.md).

Próximas conferências: fundos específicos dos quatro ambientes, textos longos, enquadramento das expressões, transições dos epílogos e créditos. Consultar [planejamento](planejamento-capitulo-01.md).
