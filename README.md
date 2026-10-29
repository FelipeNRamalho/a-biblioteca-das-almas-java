# A Biblioteca das Almas — Menu Inicial Java 2D

Protótipo jogável de **menu inicial funcional**, baseado no visual gótico-vitoriano do projeto. Desenvolvido em Java 21, Swing e Graphics2D, sem engine e sem bibliotecas externas.

## Planejamento do Capítulo I

- [Tarefas, dependências e critérios de conclusão](docs/planejamento-capitulo-01.md)
- [Sugestões de rumos para a história](docs/rumos-da-historia.md)

Esses documentos planejam a expansão do protótipo. As tarefas estão pendentes e as alternativas narrativas aguardam escolha.

## Executar (sem Maven)

1. Instale o **Java 21** (JRE/JDK).
2. No Windows, execute `INICIAR_WINDOWS.bat` ou abra o terminal na pasta do projeto e rode `java -jar dist/BibliotecaDasAlmas-Menu.jar`.
3. No Linux/macOS, execute `./INICIAR_LINUX_MAC.sh`.

A janela abre em 1280 × 720 e adapta os componentes a outros tamanhos, mantendo a proporção 16:9.

## Importar para IntelliJ / VS Code

Abra esta pasta como projeto **Maven** usando o `pom.xml`. Configure Java 21 e execute `br.com.bibliotecadasalmas.Game`. Maven **não é necessário** para executar o `.jar` que acompanha o ZIP.

## Funcionalidades implementadas

- **Novo Jogo:** exibe a cutscene de Natasha despertando; 3 trechos narrativos controlados por cliques/Enter/Espaço; em seguida abre uma **demonstração do Salão Principal**, usando um cenário existente.
- **Continuar:** retoma o prólogo ou o Salão Principal se existir progresso salvo. Sem progresso, fica visualmente desabilitado.
- **Configurações:** ativa/desativa partículas, alterna velocidade do texto e tela cheia (F11). As preferências são armazenadas localmente.
- **Créditos:** exibe créditos gerais, sem atribuir autoria a pessoas não informadas.
- **Sair:** fecha a janela.
- **Exploração demonstrativa:** permite examinar um livro e retornar ao menu; não pretende substituir a implementação completa do Capítulo I.
- **Teclado:** ↑/↓ para selecionar botões; Enter para confirmar; ESC para voltar; F11 para tela cheia.
- **Mouse:** hover/seleção dos botões; cliques dimensionados corretamente com a janela.

## Atenção à integração com o restante do jogo

A **imagem isolada da Entrada da Biblioteca** não foi localizada no acervo anexado ao menu. Por isso a cena pós-prólogo é identificada como **demonstração no Salão Principal**, sem fingir que o fluxo definitivo já está implementado. Ao incluir o cenário da Entrada, a primeira fase pode ser conectada ao mesmo `GameController`.

Os cenários, personagens, itens e diálogos completos poderão ser adicionados a `src/main/resources/assets/` e novos gerenciadores sem reescrever a renderização do menu.

## Estrutura e POO

- `Game`: inicializa a janela e tela cheia.
- `AssetManager`: encapsula cache e leitura de PNGs via classpath.
- `GameController`: controla estados de tela, avanço do prólogo e progresso.
- `GameSettings` / `SaveManager`: encapsulam preferências e salvamento.
- `ClickTarget`: contrato abstrato de interações visuais.
- `AbstractControl`: superclasse que implementa geometria e clique padrão.
- `GothicButton` / `TextOption`: herança + polimorfismo: ambos implementam os mesmos métodos de clique/desenho de maneiras diferentes.
- `GamePanel`: desenha tudo dinamicamente com Graphics2D.

### Salvamento local

O protótipo grava arquivos `.properties` em `~/.biblioteca-das-almas/` (`%USERPROFILE%\.biblioteca-das-almas` no Windows). Para testar o botão Continuar, inicie um novo jogo, retorne ao menu com ESC e selecione Continuar.

## Arte utilizada

- Fundo do menu e exploração: arquivo original do **Salão Principal** (imagem de cenário já existente no projeto).
- Intro: cutscene oficial **Natasha Despertando na Entrada**.
- Molduras, botões e ornamentos: **UI Kit** de A Biblioteca das Almas. A interface de texto é renderizada no Java, não incorporada ao PNG.

Nenhum arquivo de fonte foi incluído; o jogo usa fontes Serif nativas do Java.

## Prévia

`preview_menu.png` mostra a renderização real feita pelo Java do menu inicial, não uma ilustração conceitual.
