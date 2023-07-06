import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
class Jogador {
    private String nome;
    private Tabuleiro tabuleiroProprio;
    private Tabuleiro tabuleiroOponente;
    private List<Navio> navios;

    public Jogador(String nome) {
        this.nome = nome;
        tabuleiroProprio = new Tabuleiro();
        tabuleiroOponente = new Tabuleiro();
        navios = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public Tabuleiro getTabuleiroProprio() {
        return tabuleiroProprio;
    }

    public Tabuleiro getTabuleiroOponente() {
        return tabuleiroOponente;
    }

    public List<Navio> getNavios() {
        return navios;
    }

    public void posicionarNavios() {
        Scanner scanner = new Scanner(System.in);

        List<Navio> navios = new ArrayList<>();
        navios.add(new Navio("Porta-Aviões", 5, 'P'));
        navios.add(new Navio("Navios-Tanque", 4, 'N'));
        navios.add(new Navio("Contratorpedeiros", 3, 'C'));
        navios.add(new Navio("Submarinos", 2, 'S'));

        for (Navio navio : navios) {
            System.out.println("\nTabuleiro do(a) " + nome);
            tabuleiroProprio.exibir();

            System.out.println("\nPosicione o " + navio.getNome() + " (" + navio.getTamanho() + " quadrados)");

            for (int i = 0; i < navio.getTamanho(); i++) {
                boolean posicaoValida = false;
                int linha = 0;
                int coluna = 0;
                while (!posicaoValida) {
                    System.out.print("Digite a linha para a posição " + (i + 1) + ": ");
                    linha = scanner.nextInt();
                    System.out.print("Digite a coluna para a posição " + (i + 1) + ": ");
                    coluna = scanner.nextInt();
                    if (verificarPosicaoValida(navio, linha, coluna)) {
                        posicaoValida = true;
                    } else {
                        System.out.println("Posição inválida. Tente novamente.");
                    }
                }

                Posicao posicao = new Posicao(linha, coluna);
                navio.adicionarPosicao(posicao);
                tabuleiroProprio.marcarPosicao(linha, coluna, navio.getMarcador());
            }

            this.navios.add(navio);
        }

        System.out.println("\nTabuleiro do(a) " + nome);
        tabuleiroProprio.exibir();
    }

    public boolean verificarPosicaoValida(Navio navio, int linha, int coluna) {
        // Verifica se a posição está dentro do tabuleiro
        if (linha < 0 || linha >= 10 || coluna < 0 || coluna >= 10) {
            return false;
        }

        // Verifica se a posição já está ocupada por outro navio
        for (Navio n : navios) {
            for (Posicao pos : n.getPosicoes()) {
                int linhaNavio = pos.getLinha();
                int colunaNavio = pos.getColuna();
                if (linha == linhaNavio && coluna == colunaNavio) {
                    return false;
                }
            }
        }

        // Verifica se o navio ficará alinhado corretamente
        int tamanho = navio.getTamanho();
        if (tamanho > 1) {
            boolean alinhadoHorizontalmente = true;
            boolean alinhadoVerticalmente = true;

            // Verifica alinhamento horizontal
            for (int i = 1; i < tamanho; i++) {
                if (linha >= 0 && linha < 10 && coluna + i >= 0 && coluna + i < 10) {
                    if (tabuleiroProprio.getMatriz()[linha][coluna + i] != '-') {
                        alinhadoHorizontalmente = false;
                        break;
                    }
                } else {
                    alinhadoHorizontalmente = false;
                    break;
                }
            }

            // Verifica alinhamento vertical
            for (int i = 1; i < tamanho; i++) {
                if (linha + i >= 0 && linha + i < 10 && coluna >= 0 && coluna < 10) {
                    if (tabuleiroProprio.getMatriz()[linha + i][coluna] != '-') {
                        alinhadoVerticalmente = false;
                        break;
                    }
                } else {
                    alinhadoVerticalmente = false;
                    break;
                }
            }

            return alinhadoHorizontalmente || alinhadoVerticalmente;
        }

        return true;
    }

    public boolean verificarEmbarcacaoDestruida(int linha, int coluna) {
        for (Navio navio : navios) {
            boolean todasPosicoesAtingidas = true;
            for (Posicao posicao : navio.getPosicoes()) {
                int linhaNavio = posicao.getLinha();
                int colunaNavio = posicao.getColuna();
                if (linha == linhaNavio && coluna == colunaNavio) {
                    tabuleiroOponente.marcarPosicao(linhaNavio, colunaNavio, '*');
                }
                if (tabuleiroOponente.getMatriz()[linhaNavio][colunaNavio] != '*') {
                    todasPosicoesAtingidas = false;
                    break;
                }
            }
            if (todasPosicoesAtingidas) {
                return true;
            }
        }
        return false;
    }

    public boolean todosNaviosDestruidos() {
        for (Navio navio : navios) {
            for (Posicao posicao : navio.getPosicoes()) {
                int linha = posicao.getLinha();
                int coluna = posicao.getColuna();
                if (tabuleiroOponente.getMatriz()[linha][coluna] != '*') {
                    return false;
                }
            }
        }
        return true;
    }

    public void realizarJogada(Jogador oponente, int linha, int coluna) {
        char marcador = oponente.getTabuleiroProprio().getMatriz()[linha][coluna];
        if (marcador == 'N' || marcador == 'P' || marcador == 'C' || marcador == 'S') {
            marcador = '*';

            System.out.println("Embarcação atingida.");

            // Verificar se uma única embarcação foi destruída
            if (oponente.verificarEmbarcacaoDestruida(linha, coluna)) {
                System.out.println("Uma embarcação foi destruída!");
            }

            // Verificar se todos os navios do oponente foram destruídos
            if (oponente.todosNaviosDestruidos()) {
                System.out.println("\nParabéns, " + getNome() + "! Você destruiu todos os navios do(a) " + oponente.getNome() + " e venceu o jogo.");
                // Marcar o jogo como finalizado, se necessário
                // jogoFinalizado = true;
            }

        } else {
            marcador = '#';

            System.out.println("Tiro na água.");
        }
        tabuleiroOponente.marcarPosicao(linha, coluna, marcador);
    }

}
