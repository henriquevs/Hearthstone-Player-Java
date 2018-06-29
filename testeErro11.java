/* Autor: Henrique Vicente Souza - RA: 105063 - Data: 04 de Outubro de 2015
 * 
 * Este trabalho de computação, referente a disciplina MC336 / MC322, tem como objetivo a implementação de um
 * jogador para o jogo LaMa, uma adaptacao do jogo chamado HearthStone. Logo abaixo descrevo minha estrategia.
 * 
 * Inicialmente, faco uma verificacao se possuo 7 lacaios ja colocados na mesa. Esta condicao eh interessante
 * pois neste caso (que eh pouco provavel que ocorra, mas que nao possui probabilidade nula de ocorrer) uso
 * todos estes lacaios para atacar o heroi do oponente. Apos este passo, uso minha carta de magia (caso eu tenha
 * uma) para arrancar o maximo de pontos do heroi do oponente. Finalmente, caso ainda tenha alguma mana sobrando
 * uso o poder heroico tambem no heroi do oponente. O intuito deste caso especial verificado no inicio de meu
 * programa eh de aumentar a probabilidade de ganhar de alguem que utilize a estrategia de nao atacar meus
 * lacaios (como por exemplo no caso de alguem que queira somente atacar meu heroi).
 * Apos esse caso especial de entrada (sabendo que o limite maximo de lacaios que podem estar na mesa eh 7),
 * verifico se sou primeiro ou segundo jogador, com o intuito de utilizar corretamente os ID's de cada carta
 * (os ID's das cartas do primeiro e segundo jogadores sao diferentes entre si). Tanto para o caso de sermos
 * primeiro ou segundo jogadores, a estrategia eh a mesma: verifico se o poder de ataque de todos os lacaios
 * (menos que 7 lacaios) meus que estao na mesa eh superior ou igual a vida do heroi do oponente, em caso 
 * positivo finalizo a partida atacando com todos os meus lacaios o heroi do oponente. Em caso de nao possibilidade
 * de finalizacao da partida, tentamos baixar a sequencia de cartas (caso tenhamos mana): DRAGAO (segunda
 * maior defesa e primeiro maior ataque); MESTRE ESPADACHIM (carta com maior defesa); GIGANTE DE PEDRA (carta
 * com terceira maior defesa); GIGANTE DE GELO (segundo maior ataque); MAGO APRENDIZ (segundo maior ataque);
 * E assim por diante, o interesse eh ter o maximo de cartas possivel colocadas na mesa e a minha ideia foi
 * a de tentar colocar sempre aquelas que me dao mais possibilidades de vencer primeiro (que neste caso quer
 * dizer, as cartas com maior defesa, pois elas tem uma maior probabilidade de permanecer em jogo).
 * Entao, fazemos uma verificacao se o jogador adversario colocou um dragao na mesa ou nao, em caso positivo
 * tentamos usar a melhor carta de magia que tivermos na mao para atingi-lo (pois nesse caso arrancariamos
 * vida de seu dragao sem o risco de sofrer dano), em caso contrario (ou seja, caso o adversario nao tenha
 * um dragao na mesa, usamos toda a mana que resta para tentar baixar todos os lacaios que a mana permite e 
 * que estao na nossa mao). Finalmente, caso tenha sobrado mana e a magia ainda nao tenha sido usada nesta
 * rodada, usamos a magia e em seguida, atacamos com todos os lacaios da mesa o heroi do oponente. Uma ultima
 * tentativa de uso de mana, caso ainda a tenhamos apos executar todos as verificacoes dos casos anteriores,
 * efetuamos um ataque heroico contra o heroi do oponente.
 */
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.Random;

// ATENÇÃO: Renomeie este arquivo e a classe, substituindo "xxxxxx" pelo RA do aluno. Linhas 10, 21 e 29.

/**
* Esta classe representa um Jogador para o jogo LaMa (Lacaios & Magias).
* @author Henrique Vicente Souza - RA:105063
*/
public class testeErro11 extends Jogador {
	public ArrayList<Carta> lacaios;
	public ArrayList<Carta> lacaiosOponente;
	
	/**
	  * O método construtor do JogadorRA105063.
	  * 
	  * @param maoInicial Contém a mão inicial do jogador. Deve conter o número de cartas correto dependendo se esta classe Jogador que está sendo construída é o primeiro ou o segundo jogador da partida. 
	  * @param primeiro   Informa se esta classe Jogador que está sendo construída é o primeiro jogador a iniciar nesta jogada (true) ou se é o segundo jogador (false).
	  * @return            um objeto JogadorAleatorio
	  */
	public testeErro11(ArrayList<Carta> maoInicial, boolean primeiro){
		primeiroJogador = primeiro;
		
		mao = maoInicial;
		lacaios = new ArrayList<Carta>();
		lacaiosOponente = new ArrayList<Carta>();
		
	}
	
	/**
	  * Um método que processa o turno de cada jogador. Este método deve retornar as jogadas do Jogador correspondente para o turno atual (ArrayList de Jogada).
	  * 
	  * @param mesa   O "estado do jogo" imediatamente antes do início do turno corrente. Este objeto de mesa contém todas as informações 'públicas' do jogo (lacaios vivos e suas vidas, vida dos heróis, etc).
	  * @param cartaComprada   A carta que o Jogador recebeu neste turno (comprada do Baralho). Obs: pode ser null se o Baralho estiver vazio ou o Jogador possuir mais de 10 cartas na mão.
	  * @param jogadasOponente   Um ArrayList de Jogada que foram os movimentos utilizados pelo oponente no último turno, em ordem.
	  * @return            um ArrayList com as Jogadas decididas
	  */
	public ArrayList<Jogada> processarTurno (Mesa mesa, Carta cartaComprada, ArrayList<Jogada> jogadasOponente){
		int minhaMana;
		if(cartaComprada != null)
			mao.add(cartaComprada);
		if(primeiroJogador){
			minhaMana = mesa.getManaJog1();
			lacaios = mesa.getLacaiosJog1();
			lacaiosOponente = mesa.getLacaiosJog2();
		}
		else{
			minhaMana = mesa.getManaJog2();
			lacaios = mesa.getLacaiosJog2();
			lacaiosOponente = mesa.getLacaiosJog1();
		}
		
		ArrayList<Jogada> minhasJogadas = new ArrayList<Jogada>();
		
		/*#################################### INICIO DO MEU CODIGO ####################################*/
		
		// Meus lacaios
		ArrayList<Carta> lacaiosMeus = primeiroJogador ? mesa.getLacaiosJog1() : mesa.getLacaiosJog2();
		
		// Lacaios do adversario
		ArrayList<Carta> lacaiosAdversario = (!primeiroJogador) ? mesa.getLacaiosJog1() : mesa.getLacaiosJog2();
		System.out.println("MANA DO JOGADOR 2="+minhaMana);
		// A partir deste ponto comeco a analisar e a fazer as jogadas
		if(minhaMana >= 2){ // Inicio do if
			Jogada pod = new Jogada(TipoJogada.PODER, null, null);
			minhasJogadas.add(pod);
		} // Fim do if
		
		if(minhaMana >= 2){ // Inicio do if
			Jogada pod = new Jogada(TipoJogada.PODER, null, null);
			minhasJogadas.add(pod);
		} // Fim do if
		
		
		
		return minhasJogadas; // Retorna as jogadas efetuadas para o motor
	}	
}