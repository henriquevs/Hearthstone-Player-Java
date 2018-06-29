import java.util.ArrayList;

// Classe Main para ser utilizada no Laboratório 4.
// ATENÇÃO: Modifique o nome da classe 'JogadorCompXXXXX' substituindo XXXXX pelo seu RA.

public class Main {
	public static void main(String[] args) {
		Baralho baralho1 = new Baralho(MotorRA105063.gerarListaCartasPadrao(0));
		Baralho baralho2 = new Baralho(MotorRA105063.gerarListaCartasPadrao(1));
		Baralho.setDeterministico(false);
		umaPartida(baralho1, baralho2, 1);
	}
	
	private static void umaPartida(Baralho baralho1, Baralho baralho2, int verbosidade){
		// Embaralha os Baralhos
		baralho1.embaralhar();
		baralho2.embaralhar();
		
		// Declara estruturas que armazenam as cartas da mão
		ArrayList<Carta> Mao1, Mao2;
		Mao1 = new ArrayList<Carta>();
		Mao2 = new ArrayList<Carta>();
		
		// Adiciona cartas à mão de cada jogador com o número de cartas correspondente (para o primeiro Jogador é Motor.cartasIniJogador1 cartas, e para o segundo Jogador é Motor.cartasIniJogador2).
		for(int i = 0; i < MotorRA105063.cartasIniJogador1; i++){
			Mao1.add(baralho1.getCartas().get(0));
			baralho1.getCartas().remove(0);
		}
		for(int i = 0; i < MotorRA105063.cartasIniJogador2; i++){
			Mao2.add(baralho2.getCartas().get(0));
			baralho2.getCartas().remove(0);
		}
		
		// Inicializa uma cópia para ser enviada ao Jogador das cartas da mão
		@SuppressWarnings("unchecked")
		ArrayList<Carta> mao1clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(Mao1);
		@SuppressWarnings("unchecked")
		ArrayList<Carta> mao2clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(Mao2);
		
		// Inicializa os Jogadores devidamente (com as cartas da mão e o argumento 'primeiro').
		int cheatingOption = 1; // Teste as opções {0, 1, 2, 3}.
		JogadorRA105063 jogA = new JogadorRA105063(mao1clone, true);
		//testeErro4 jogA = new testeErro4(mao2clone, true);
		testeErro4 jogB = new testeErro4(mao2clone, false);
		//testeErro5 jogB = new testeErro5(mao2clone, false);
		//JogadorAleatorio jogB = new JogadorAleatorio(mao2clone, false);
		
		// O Motor é construído
		MotorRA105063 partida = new MotorRA105063(baralho1, baralho2, Mao1, Mao2, jogA, jogB, verbosidade, 1, null);
		
		// Executa a partida sem dizer quem venceu
		partida.executarPartida();
		System.out.println("A partida se encerrou.");
	}
}