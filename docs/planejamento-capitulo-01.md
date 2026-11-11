# Planejamento do Capítulo I — A Biblioteca das Almas

Revisão: **11/11/2026**, commit `389433f`. O escopo narrativo está em [Rumos da história](rumos-da-historia.md); os resultados executados estão em [Testes do capítulo](testes-do-capitulo.md). Este plano distingue integração de lógica, acabamento visual e aprovação acadêmica.

## 1. Objetivo e situação atual

O Capítulo I apresenta 26 cenas narrativas, com três versões da cena 26: **28 entradas e 620 nós**. O manifesto `trecho_inicial.json` configura todo o percurso, seis ambientes lógicos, 12 cutscenes e oito documentos.

A atualização incorpora a hierarquia de personagens, retratos dos quatro interlocutores, hotspots condicionados por flags e testes dos quatro atos. Os oito programas de teste passaram na revisão atual. Isso não mede cobertura total nem conclui automaticamente o checklist do professor.

| Área | Situação atual | Trabalho restante |
| --- | --- | --- |
| Narrativa e regras | Quatro atos, escolhas, investigação e três finais integrados. | Conferência manual de ritmo, apresentação e rotas adicionais. |
| Mundo | Seis locais com navegação e condições de acesso. | Fundos específicos para Balcão, Corredor, Arquivo e Câmara. |
| Personagens | `GameCharacter`, quatro subclasses e registro integrados. | Conferir enquadramento de todas as expressões em execução. |
| Recursos | 71 PNGs, 12 cutscenes mapeadas e oito documentos. | Rever recursos disponíveis sem uso e créditos por arquivo. |
| Persistência | Formato 1 em `historia.json`, incluindo pontos dos atos posteriores. | Conferência manual de retomada e revisita; sem presumir contexto genérico além do cursor/marcas. |
| Distribuição | `dist/BibliotecaDasAlmas-Menu.jar`, via Git LFS; scripts com dois nomes aceitos. | Conferir instalação a partir de clone novo e acesso do professor. |
| Requisitos acadêmicos | POO documentada com exemplos existentes. | Tela cheia inicial, Esc imediato, setas/WASD/diagonais, áudio, colisão e hierarquia de referência. |

## 2. Regras narrativas preservadas

O objetivo é manter a história aprovada e os três finais:

- **Queimar:** Natasha se sacrifica e liberta as almas.
- **Preservar:** Natasha sobrevive e o ciclo continua.
- **Reescrever:** exige as memórias `retorno`, `escolha` e `pacto`, confiança da Fantasma e descoberta da cláusula. Natasha sobrevive com memórias incompletas.

Personalidade e `final_secreto_revelado` não criam requisitos adicionais. A escolha final tem confirmação/cancelamento. Revisitas devem preservar recompensas e não reiniciar acontecimentos concluídos.

## 3. Acompanhamento das tarefas T01–T24

Os identificadores foram mantidos para continuidade. “Implementado” descreve código e configuração; não representa aprovação visual ou acadêmica.

| Tarefa | Situação em 11/11 | Evidência / conclusão necessária |
| --- | --- | --- |
| T01 — Consolidar a história | Definida | Direção narrativa e consolidado preservados. |
| T02 — Registrar regras e finais | Implementado | Condições nos dados, `canRewritePact` e testes de finais. |
| T03 — Mapear roteiro e exploração | Implementado; conferir apresentação | Seis locais, hotspots e saídas no manifesto; validar manualmente as revisitas. |
| T04 — Conferir recursos | Parcial | 71 PNGs presentes; quatro fundos específicos ainda ausentes da configuração. Ver [catálogo](catalogo-de-recursos.md). |
| T05 — Execução de referência | Registrada | Compilação e oito testes atuais em [Testes do capítulo](testes-do-capitulo.md). |
| T06 — Separar o estado | Implementado | `GameState`, consultas imutáveis, operações e regras testadas. |
| T07 — Cenários e interações | Implementado na lógica; visual parcial | `Interactable`, geometria lógica e condições `requireFlag`/`hideWhenFlag`; fundos compartilhados. |
| T08 — Personagens e apresentação | Implementado; conferir todas as artes | `GameCharacter`, Natasha, Rodion, Fantasma, Escriba e `CharacterRegistry`. |
| T09 — Carregar e validar JSON | Implementado | `StoryLoaderTest`, 28 entradas, 620 nós e referências tipadas. |
| T10 — Diálogos e escolhas | Implementado | `NarrativeSession`, `StoryView`, efeitos em lote e confirmação. |
| T11 — Inventário e diário | Implementado | Cinco itens e três memórias catalogados por referências aos nós do roteiro. |
| T12 — Entrada → Salão | Implementado e testado | Seis combinações da abertura, carta adiada, retorno e save. |
| T13 — Salão/Balcão | Implementado e testado | `ActOneGameplayTest`: nove combinações e identidade de Rodion. |
| T14 — Corredor/Fantasma | Implementado e testado | `ActTwoGameplayTest`: 12 rotas, fotografia, medalhão e confiança. |
| T15 — Arquivo/confissão | Implementado e testado | `ActThreeGameplayTest`: 12 rotas, pacto, cláusula e confissão. |
| T16 — Câmara/finais | Implementado e testado | `ActFourGameplayTest`: três finais, revisita e confirmação. |
| T17 — Revisar narrativa no jogo | Conferência manual pendente | Os testes de estado não comprovam ritmo e legibilidade de todas as falas. |
| T18 — Persistência versionada | Implementado; conferir interação real | Save de diálogo, escolhas e atos posteriores testado; não há campo genérico de contexto suspenso. |
| T19 — UI essencial | Implementado; acabamento pendente | Nome centralizado, retrato atrás da caixa, prompt interno e consultas. Conferir textos longos. |
| T20 — Refinar apresentação/controles | Parcial | F11, escala e revelação de texto existentes; áudio e requisitos de movimento continuam pendentes. |
| T21 — Validar caminhos | Casos automatizados aprovados | Oito programas aprovados; não há medição de cobertura de 100% nem declaração de toda combinação possível. |
| T22 — Validar pacote e interface | Verificação técnica registrada; manual pendente | JAR atual com LFS; ver evidências e limites em [Testes do capítulo](testes-do-capitulo.md). |
| T23 — Preparar entrega acadêmica | Documentos atualizados; aceite pendente | Matrizes, exemplos de POO e créditos da equipe disponíveis; falta conferir requisitos, acesso e autoria por recurso. |
| T24 — Extras | Opcional, sem implementação comprovada | Histórico, modo automático, galeria de finais e mapa só após concluir a entrega essencial. |

## 4. Próximas ações e critérios de aceite

| Prioridade | Ação | Critério de aceite |
| --- | --- | --- |
| P0 | Resolver diferenças com o guia do professor | Atender ou registrar aceite da adaptação de estrutura/hierarquia e mecânicas. Ver [matriz acadêmica](documentacao-academica.md#4-atendimento-às-orientações-do-professor). |
| P0 | Adequar janela e teclado exigidos | Inicialização sem bordas no monitor e Esc imediato; setas/WASD/diagonais conforme exigência aplicável. |
| P0 | Integrar áudio e colisão exigidos | `Clip` pré-carregado, bounding boxes e remoção por laço reverso quando houver lista de entidades. |
| P0 | Conferir a entrega em ambiente gráfico e clone novo | JAR LFS real, recursos disponíveis, foco do teclado, retomada, três finais e acesso do professor. |
| P1 | Completar fundos de ambientes | Quatro fundos específicos cadastrados e hotspots conferidos no enquadramento final. |
| P1 | Revisar todos os diálogos e retratos | Nome, prompt, texto e expressões legíveis em janela e tela cheia, sem cortes. |
| P0 | Registrar autoria e divisão da dupla | Créditos por recurso, ferramentas usadas e responsabilidades individuais verificáveis. |

## 5. Organização do projeto e da dupla

`src/main/java` contém os pacotes `core`, `narrative`, `characters`, `world`, `ui` e `assets`. O roteiro e a configuração ficam em `src/main/resources/story`; as imagens em `assets`. O build sai em `target/`, ignorado pelo Git. A distribuição LFS fica em `dist/`.

A dupla é Felipe Nunes Ramalho e Juliana Karla Camargo da Silva. A divisão individual de autoria deve ser registrada sem inferir responsabilidades a partir desta tabela. Recomenda-se uma branch por funcionalidade, commits claros e revisão cruzada.

O documento do professor indica entregas entre **23/11 e 27/11** e prova em **27/11**, sem ano no trecho fornecido. Confirmar o calendário da turma. O antigo plano relativo de 20 dias não estabelece disponibilidade ou novo prazo.

As avaliações de outubro são registros históricos. Para a situação técnica atual, usar este plano e os resultados de 11/11/2026.
