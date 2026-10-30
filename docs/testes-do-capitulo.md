# Testes do Capítulo I — A Biblioteca das Almas

Referência: 30/10/2026. Este documento contém procedimentos reproduzíveis, critérios de aceitação e um registro de execução. A história oficial está em [rumos-da-historia.md](rumos-da-historia.md); os comportamentos técnicos esperados estão no [contrato narrativo](contrato-narrativo-json.md).

**O teste do protótipo está disponível; os testes do capítulo completo dependem da integração.** Um caso pendente não deve ser registrado como aprovado só porque o roteiro ou a imagem correspondente existe.

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

## 3. Procedimento atual — Teste do protótipo

Caso **P01**. Fonte: [MenuSmokeTest.java](../src/test/java/br/com/bibliotecadasalmas/MenuSmokeTest.java).

O teste executa um método `main`, com verificações próprias. O `pom.xml` atual não configura esse método como teste automático do Maven; `mvn test` isoladamente não comprova sua execução.

### Opção com Maven disponível

No PowerShell, a partir da raiz:

```powershell
mvn test-compile
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }

$saidaTeste = 'target/validacao-documentacao/previews'
java '-Djava.awt.headless=true' -cp 'target/classes;target/test-classes;target/test-resources;src/main/resources' br.com.bibliotecadasalmas.MenuSmokeTest $saidaTeste
if ($LASTEXITCODE -ne 0) { throw 'MenuSmokeTest falhou.' }
```

Esse procedimento com Maven não foi executado nesta revisão, pois `mvn` não estava disponível no ambiente. O classpath acima corresponde ao projeto atual, sem dependências externas; atualizá-lo se a integração do leitor JSON acrescentar uma dependência necessária à execução.

### Alternativa com JDK 21, sem Maven

No PowerShell, compilar as fontes principais e o teste na mesma chamada:

```powershell
$saidaClasses = 'target/validacao-documentacao/classes'
$saidaTeste = 'target/validacao-documentacao/previews'
New-Item -ItemType Directory -Path $saidaClasses,$saidaTeste -Force | Out-Null

$fontesJava = @(Get-ChildItem -Path src/main/java,src/test/java -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
javac --release 21 -encoding UTF-8 -d $saidaClasses $fontesJava
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }

java '-Djava.awt.headless=true' -cp 'target/validacao-documentacao/classes;src/main/resources' br.com.bibliotecadasalmas.MenuSmokeTest $saidaTeste
if ($LASTEXITCODE -ne 0) { throw 'MenuSmokeTest falhou.' }
```

A compilação conjunta e a execução com esse destino e classpath foram verificadas nesta revisão. Na execução realizada, a lista de fontes foi obtida com `rg --files`; o comando acima usa a enumeração nativa do PowerShell para não exigir essa ferramenta.

O teste cria seu próprio diretório temporário de progresso e recebe apenas a pasta das imagens como argumento. Não define ou altera o diretório pessoal do usuário.

### Cobertura de P01

- Novo Jogo abre o prólogo e cria progresso no diretório isolado.
- Revelar e avançar as três páginas leva à demonstração de exploração.
- A opção de examinar o livro abre a inspeção e um clique a fecha.
- Continuar restaura o progresso básico na exploração.
- Configurações abre, alterna efeitos e retorna ao menu.
- Créditos abre corretamente.
- As telas são renderizadas e gravadas em nove PNGs.

A saída esperada contém `OK: todas as verificações do menu passaram.` O teste não cobre o interpretador JSON, os seis ambientes integrados, as memórias, o save narrativo ou os finais.

## 4. Verificações manuais do protótipo

Estado atual dos casos abaixo: **pendente**.

| Caso | Preparação e passos | Resultado esperado |
| --- | --- | --- |
| **P02 — Controles** | Abrir a aplicação em ambiente gráfico. Navegar no menu com mouse, setas e Enter; avançar o prólogo com Enter/Espaço; usar Esc. | A seleção respeita controles habilitados; o prólogo avança e Esc retorna ao menu ou fecha a inspeção aberta. |
| **P03 — Janela e tela cheia** | Redimensionar a janela, alternar F11 e interagir com botões nas diferentes posições. | A proporção é preservada, textos permanecem legíveis e o clique corresponde à área desenhada. |
| **P04 — Preferências** | Em uma execução com save isolado, mudar efeitos, velocidade e tela cheia; fechar e reabrir com o mesmo diretório. | As preferências são restauradas. Registrar qualquer falha de exibição ou gravação. |

Esses casos complementam P01. O teste sem janela não aciona nem comprova a alternância real de tela cheia.

## 5. Dados e sistema narrativo

Estado atual dos casos abaixo: **pendente**. Dependem do carregador, do estado narrativo e das ferramentas de teste da integração. As verificações simplificadas já descritas na avaliação não substituem esta bateria.

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

Commit sugerido: `docs: adiciona procedimentos e registro de testes do capitulo`.
