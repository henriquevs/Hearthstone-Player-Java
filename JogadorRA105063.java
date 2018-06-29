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

import java.util.ArrayList;
//import java.util.Random;

// ATENÇÃO: Renomeie este arquivo e a classe, substituindo "xxxxxx" pelo RA do aluno. Linhas 10, 21 e 29.

/**
* Esta classe representa um Jogador para o jogo LaMa (Lacaios & Magias).
* @author Henrique Vicente Souza - RA:105063
*/
public class JogadorRA105063 extends Jogador {
	public ArrayList<Carta> lacaios;
	public ArrayList<Carta> lacaiosOponente;
	
	/**
	  * O método construtor do JogadorRA105063.
	  * 
	  * @param maoInicial Contém a mão inicial do jogador. Deve conter o número de cartas correto dependendo se esta classe Jogador que está sendo construída é o primeiro ou o segundo jogador da partida. 
	  * @param primeiro   Informa se esta classe Jogador que está sendo construída é o primeiro jogador a iniciar nesta jogada (true) ou se é o segundo jogador (false).
	  * @return            um objeto JogadorAleatorio
	  */
	public JogadorRA105063(ArrayList<Carta> maoInicial, boolean primeiro){
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
		
		// A partir deste ponto comeco a analisar e a fazer as jogadas
		
		// Caso tenhamos 7 lacaios na mesa atacamos com todos eles o heroi do oponente
		// Alem disso usamos a magia com a carta mais forte no heroi do oponente
		// Por fim, usamos o poder heroico caso ainda tenhamos mana
		if(( (primeiroJogador == true) && (lacaiosMeus.size() == 7) || ((primeiroJogador == false) && (lacaiosAdversario.size() == 7)))){ // Inicio do IF INICIAL
			
			// Ataco com todos os lacaios da mesa o heroi do oponente
			if(lacaiosMeus.size() > 0){ // Inicio do if
				for(int i = 0; i < lacaiosMeus.size(); i++){ // Inicio do for
					Carta meuLacaioVivo = lacaiosMeus.get(i);
					Jogada atk = new Jogada(TipoJogada.ATAQUE, meuLacaioVivo, null);
					minhasJogadas.add(atk);
				} // Fim do for
			} // Fim do if*/
			
			// Alem disso usamos a magia com a carta mais forte no heroi do oponente
			for(int i = 0; i < mao.size(); i++){ // Inicio do for
				Carta card = mao.get(i);
				// Se temos uma carta de magia RAIO (7 dano em alvo), a usamos contra o HEROI do oponente
				if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==24 || card.getID()==25) ){ // Inicio do if 2
					Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
					minhasJogadas.add(mag);
					minhaMana -= card.getMana();
					mao.remove(i);
					i--;
					break;
				} // Fim do if 2
				
				else{ // Inicio do else 1
					// Se temos uma carta de magia RAJADA CONGELANTE (3 dano em alvo), a usamos contra o HEROI do oponente
					if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==22 || card.getID()==23) ){ // Inicio do if 3
						Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
						minhasJogadas.add(mag);
						minhaMana -= card.getMana();
						mao.remove(i);
						i--;
						break;
					} // Fim do if 3
					
					else{ // Inicio do else 2
						// Se temos uma carta de magia MININOVA (7 dano em area), a usamos contra o HEROI do oponente (incluindo seu dragao)
						if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==28 || card.getID()==29) ){ // Inicio do if 4
							Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
							minhasJogadas.add(mag);
							minhaMana -= card.getMana();
							mao.remove(i);
							i--;
							break;
						} // Fim do if 4
						
						else{ // Inicio do else 3
							// Se temos uma carta de magia ONDA DE CHOQUE (1 dano em area), a usamos contra o HEROI do oponente (incluindo seu dragao)
							if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==26 || card.getID()==27) ){ // Inicio do if 5
								Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
								minhasJogadas.add(mag);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 5
						} // Fim do else 3
					} // Fim do else 2
				} // Fim do else 1
			} // Fim do for*/
			
			// Por fim, usamos o poder heroico caso ainda tenhamos mana
			if(minhaMana > 2){ // Inicio do if
				Jogada pod = new Jogada(TipoJogada.PODER, null, null);
				minhasJogadas.add(pod);
			} // Fim do if
			
		} // Fim do IF INICIAL
		
		// Comeco do IF PRINCIPAL
		if(( (primeiroJogador == true) && (lacaiosMeus.size() < 7) || ((primeiroJogador == false) && (lacaiosAdversario.size() < 7)))){
			
			
			//################################# JOGADOR 1 #################################
			
			Carta trapaca = new Carta(8, "DRAGAO TRAPACEIRO", TipoCarta.LACAIO, 1000, 1, 30, 0);
			Jogada dropDragao = new Jogada(TipoJogada.LACAIO, trapaca, null);
			//mao.add(trapaca);
			//minhasJogadas.add(dropDragao);
			
			/*if( minhaMana > 3){
				Carta trapacaMAGIA = new Carta(8, "MAGIA MAROTA", TipoCarta.MAGIA, TipoMagia.ALVO, 0, 31);
				Jogada dropMAGIA = new Jogada(TipoJogada.MAGIA, trapacaMAGIA, null);
				mao.add(trapacaMAGIA);
				minhasJogadas.add(dropMAGIA);
			}*/
			
			// ERROS TESTADOS (OK): ERRO 1, 2, 3, 7, 8, 5, 
			
			if(primeiroJogador == true){ // Comeco do if (SOU JOGADOR 1)
				
				int totalAtaqueLacaiosMeus = 0; // Variavel usada para contar o total de ataque dos meus lacaios na mesa
				for(int i = 0; i < lacaiosMeus.size(); i++){ // Inicio do for
					totalAtaqueLacaiosMeus += lacaiosMeus.get(i).getAtaque(); // Contamos...
				} // Fim do for
				
				// Se a soma dos ataques dos lacaios colocados na mesa no turno anterior for igual
				// a vida do heroi do oponente, finalizamos a partida atacando com todos os lacaios
				// o heroi do oponente
				if(totalAtaqueLacaiosMeus >= mesa.getVidaHeroi2() && mesa.getLacaiosJog1().size() > 0){ // Inicio do if
					for(int i = 0; i < mesa.getLacaiosJog1().size(); i++){ // Inicio do for
						Carta meuLacaioVivo = mesa.getLacaiosJog1().get(i); // Para cada lacaio meu na mesa
						Jogada atk = new Jogada(TipoJogada.ATAQUE, meuLacaioVivo, null); // Ataco heroi do oponente
						minhasJogadas.add(atk); // Atualizo minhas jogadas
					} // Fim do for
				} // Fim do if
				
				// Se nao estamos em siatuacao de finalizar a partida, devemos decidir o que fazer
				else{ // Inicio do ELSE
					
					// Caso tenhamos mana para baixar o DRAGAO (7/7) e ele esteja na mao, baixamos ele na mesa
					// Baixamos SEGUNDA carta de maior defesa e PRIMEIRA de maior ataque
					if(mesa.getManaJog1() == 7){ // Inicio do if
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==20 || card.getID()==21) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o MESTRE ESPADACHIM (3/9) e ele esteja na mao, baixamos ele na mesa
					// Baixamos a PRIMEIRA carta de maior defesa
					if(mesa.getManaJog1() == 5){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==16 || card.getID()==17) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o GIGANTE DE PEDRA (4/5) e ele esteja na mao, baixamos ele na mesa
					// GIGANTE DE PEDRA = terceiro com maior defesa
					// senao, baixamos o GIGANTE DE GELO (5/4)
					// GIGANTE DE GELO = segundo com maior ataque
					if(mesa.getManaJog1() == 4){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==12 || card.getID()==13 || card.getID()==14 || card.getID()==15) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o MAGO APRENDIZ (5/1) e ele esteja na mao, baixamos ele na mesa
					// MAGO APRENDIZ = segundo com maior ataque
					// senao, baixamos o MESTRE ORC (4/2)
					// MESTRE ORC = terceiro com maior ataque 
					if(mesa.getManaJog1() == 3){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==10 || card.getID()==11 || card.getID()==8 || card.getID()==9) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o GNOMO (2/1) e ele esteja na mao, baixamos ele na mesa
					// MAGO APRENDIZ = segundo com maior ataque
					// senao, baixamos o RECRUTA (1/2)
					// MESTRE ORC = terceiro com maior ataque
					if(mesa.getManaJog1() == 1){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==0 || card.getID()==1 || card.getID()==2 || card.getID()==3) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					boolean temDragaoOponenteNaMesa = false; // Booleano que informa se o oponente tem um dragao na mesa
					int indiceDragaoOponente = -1; // Indice da posicao do dragao do oponente na mesa
					for(int i=0; i<mesa.getLacaiosJog2().size(); i++){ // Inicio do for
						// Oponente tem dragao na mesa
						if(mesa.getLacaiosJog2().get(i).getID() == 120 || mesa.getLacaiosJog2().get(i).getID() == 121){ // Inicio do if
							temDragaoOponenteNaMesa = true;
							indiceDragaoOponente = i; // Guarda o indice do dragao do oponente
						} // Fim do if
					} // Fim do for
					
					// Se houver mana disponivel para alguma magia, o jogador devera utilizar esta carta de magia contra o dragao do oponente
					// Se a magia for de alvo, devera mirar no heroi do oponente (qualquer carta magia que a mana permita)
					// Deve-se utilizar no maximo uma MAGIA
					boolean flagUsouMagia = false; // informa se ja se usou magia no turno corrente
					if(temDragaoOponenteNaMesa){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							Carta dragaoOponente = mesa.getLacaiosJog2().get(indiceDragaoOponente); // Seta o dragao do oponente como alvo
							
							// Se temos uma carta de magia RAIO (7 dano em alvo), a usamos contra o dragao do oponente
							if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==24 || card.getID()==25) ){ // Inicio do if 2
								Jogada mag = new Jogada(TipoJogada.MAGIA, card, dragaoOponente);
								minhasJogadas.add(mag);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								flagUsouMagia = true; // informa que usou MAGIA
								break;
							} // Fim do if 2
							
							else{ // Inicio do else 1
								// Se temos uma carta de magia RAJADA CONGELANTE (3 dano em alvo), a usamos contra o dragao do oponente
								if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==22 || card.getID()==23) ){ // Inicio do if 3
									Jogada mag = new Jogada(TipoJogada.MAGIA, card, dragaoOponente);
									minhasJogadas.add(mag);
									minhaMana -= card.getMana();
									mao.remove(i);
									i--;
									flagUsouMagia = true; // informa que usou MAGIA
									break;
								} // Fim do if 3
								
								else{ // Inicio do else 2
									// Se temos uma carta de magia MININOVA (7 dano em area), a usamos contra todos os lacaios do oponente (incluindo seu dragao)
									if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==28 || card.getID()==29) ){ // Inicio do if 4
										Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
										minhasJogadas.add(mag);
										minhaMana -= card.getMana();
										mao.remove(i);
										i--;
										flagUsouMagia = true; // informa que usou MAGIA
										break;
									} // Fim do if 4
									
									else{ // Inicio do else 3
										// Se temos uma carta de magia ONDA DE CHOQUE (1 dano em area), a usamos contra todos os lacaios do oponente (incluindo seu dragao)
										if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==26 || card.getID()==27) ){ // Inicio do if 5
											Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
											minhasJogadas.add(mag);
											minhaMana -= card.getMana();
											mao.remove(i);
											i--;
											flagUsouMagia = true; // informa que usou MAGIA
											break;
										} // Fim do if 5
									} // Fim do else 3
								} // Fim do else 2
							} // Fim do else 1
						} // Fim do for
					} // Fim do if 1
					else{
						// Se houver mana disponivel para alguma carta de lacaio, devemos baixar na mesa
					    // esta carta de lacaio (qualquer carta de lacaio que a mana permita)
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && card.getMana() <= minhaMana){ // Inicio do if
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if
						} // Fim do for
					}
					
					if(flagUsouMagia == false){ // Se ainda nao usou a magia, podemos usa-la para atacar o heroi do oponente (dano em alvo) ou todos os lacaios do oponente (dano em area)
						// Se houver mana disponivel para alguma magia, o jogador devera utilizar esta carta de magia.
						// Se a magia for de alvo, devera mirar no heroi do oponente (qualquer carta magia que a mana permita)
						// Deve-se utilizar no maximo uma magia
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana){ // Inicio do if
								Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
								minhasJogadas.add(mag);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if
						} // Fim do for
					}
					
					if(lacaiosMeus.size() > 0){ // Inicio do if
						for(int i = 0; i < lacaiosMeus.size(); i++){ // Inicio do for
							Carta meuLacaioVivo = lacaiosMeus.get(i);
							meuLacaioVivo.setAtaque(200);
							Jogada atk = new Jogada(TipoJogada.ATAQUE, trapaca, trapaca);
							minhasJogadas.add(atk);
							//minhasJogadas.add(atk); // TODO: ERRO CODIGO 7 (OK)
						} // Fim do for
					} // Fim do if*/
					
					// Se apos todas as jogadas tivermos mana o suficiente para o ataque heroico, o utilizamos contra o heroi do oponente
					if(minhaMana >= 2){ // Inicio do if
						Jogada pod = new Jogada(TipoJogada.PODER, null, null);
						minhasJogadas.add(pod);
					} // Fim do if

				} // Fim do ELSE
				
			} // Fim do if (SOU JOGADOR 1)

			//################################# JOGADOR 2 #################################
			
			else{ // Comeco do else (SOU JOGADOR 2)
				
				int totalAtaqueLacaiosMeus = 0; // Variavel usada para contar o total de ataque dos meus lacaios na mesa
				for(int i = 0; i < lacaiosMeus.size(); i++){ // Inicio do for
					totalAtaqueLacaiosMeus += lacaiosMeus.get(i).getAtaque(); // Contamos...
				} // Fim do for
				
				// Se a soma dos ataques dos lacaios colocados na mesa no turno anterior for igual
				// a vida do heroi do oponente, finalizamos a partida atacando com todos os lacaios
				// o heroi do oponente
				if(totalAtaqueLacaiosMeus >= mesa.getVidaHeroi1() && mesa.getLacaiosJog2().size() > 0){ // Inicio do if
					for(int i = 0; i < mesa.getLacaiosJog2().size(); i++){ // Inicio do for
						Carta meuLacaioVivo = mesa.getLacaiosJog2().get(i); // Para cada lacaio meu na mesa
						Jogada atk = new Jogada(TipoJogada.ATAQUE, meuLacaioVivo, null); // Ataco heroi do oponente
						minhasJogadas.add(atk); // Atualizo minhas jogadas
					} // Fim do for
				} // Fim do if
				
				// Se nao estamos em siatuacao de finalizar a partida, devemos decidir o que fazer
				else{ // Inicio do ELSE 1
					
					// Caso tenhamos mana para baixar o DRAGAO (7/7) e ele esteja na mao, baixamos ele na mesa
					// Baixamos SEGUNDA carta de maior defesa e PRIMEIRA de maior ataque
					if(mesa.getManaJog2() == 7){ // Inicio do if
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==120 || card.getID()==121) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o MESTRE ESPADACHIM (3/9) e ele esteja na mao, baixamos ele na mesa
					// Baixamos a PRIMEIRA carta de maior defesa
					if(mesa.getManaJog2() == 5){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==116 || card.getID()==117) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o GIGANTE DE PEDRA (4/5) e ele esteja na mao, baixamos ele na mesa
					// GIGANTE DE PEDRA = terceiro com maior defesa
					// senao, baixamos o GIGANTE DE GELO (5/4)
					// GIGANTE DE GELO = segundo com maior ataque
					if(mesa.getManaJog2() == 4){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==112 || card.getID()==113 || card.getID()==114 || card.getID()==115) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o MAGO APRENDIZ (5/1) e ele esteja na mao, baixamos ele na mesa
					// MAGO APRENDIZ = segundo com maior ataque
					// senao, baixamos o MESTRE ORC (4/2)
					// MESTRE ORC = terceiro com maior ataque 
					if(mesa.getManaJog2() == 3){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==110 || card.getID()==111 || card.getID()==108 || card.getID()==109) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					// Caso tenhamos mana para baixar o MAGO APRENDIZ (5/1) e ele esteja na mao, baixamos ele na mesa
					// MAGO APRENDIZ = segundo com maior ataque
					// senao, baixamos o MESTRE ORC (4/2)
					// MESTRE ORC = terceiro com maior ataque
					if(mesa.getManaJog2() == 1){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && (card.getID()==100 || card.getID()==101 || card.getID()==102 || card.getID()==103) && card.getMana() <= minhaMana){ // Inicio do if 2
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if 2
						} // Fim do for
					} // Fim do if 1
					
					boolean temDragaoOponenteNaMesa = false; // Booleano que informa se o oponente tem um dragao na mesa
					int indiceDragaoOponente = -1; // Indice da posicao do dragao do oponente na mesa
					for(int i=0; i<mesa.getLacaiosJog1().size(); i++){ // Inicio do for
						// Oponente tem dragao na mesa
						if(mesa.getLacaiosJog1().get(i).getID() == 20 || mesa.getLacaiosJog1().get(i).getID() == 21){ // Inicio do if
							temDragaoOponenteNaMesa = true;
							indiceDragaoOponente = i; // Guarda o indice do dragao do oponente
						} // Fim do if
					} // Fim do for
					
					// Se houver mana disponivel para alguma magia, o jogador devera utilizar esta carta de magia contra o dragao do oponente
					// Se a magia for de alvo, devera mirar no heroi do oponente (qualquer carta magia que a mana permita)
					// Deve-se utilizar no maximo uma MAGIA
					boolean flagUsouMagia = false; // informa se ja se usou magia no turno corrente
					if(temDragaoOponenteNaMesa){ // Inicio do if 1
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							Carta dragaoOponente = mesa.getLacaiosJog1().get(indiceDragaoOponente); // Seta o dragao do oponente como alvo
							
							// Se temos uma carta de magia RAIO (7 dano em alvo), a usamos contra o dragao do oponente
							if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==124 || card.getID()==125) ){ // Inicio do if 2
								Jogada mag = new Jogada(TipoJogada.MAGIA, card, dragaoOponente);
								minhasJogadas.add(mag);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								flagUsouMagia = true; // informa que usou MAGIA
								break;
							} // Fim do if 2
							
							else{ // Inicio do else 1
								// Se temos uma carta de magia RAJADA CONGELANTE (3 dano em alvo), a usamos contra o dragao do oponente
								if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==122 || card.getID()==123) ){ // Inicio do if 3
									Jogada mag = new Jogada(TipoJogada.MAGIA, card, dragaoOponente);
									minhasJogadas.add(mag);
									minhaMana -= card.getMana();
									mao.remove(i);
									i--;
									flagUsouMagia = true; // informa que usou MAGIA
									break;
								} // Fim do if 3
								
								else{ // Inicio do else 2
									// Se temos uma carta de magia MININOVA (3 dano em area), a usamos contra todos os lacaios do oponente (incluindo seu dragao)
									if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==128 || card.getID()==129) ){ // Inicio do if 4
										Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
										minhasJogadas.add(mag);
										minhaMana -= card.getMana();
										mao.remove(i);
										i--;
										flagUsouMagia = true; // informa que usou MAGIA
										break;
									} // Fim do if 4
									
									else{ // Inicio do else 3
										// Se temos uma carta de magia ONDA DE CHOQUE (1 dano em area), a usamos contra todos os lacaios do oponente (incluindo seu dragao)
										if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana && (card.getID()==126 || card.getID()==127) ){ // Inicio do if 5
											Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
											minhasJogadas.add(mag);
											minhaMana -= card.getMana();
											mao.remove(i);
											i--;
											flagUsouMagia = true; // informa que usou MAGIA
											break;
										} // Fim do if 5
									} // Fim do else 3
								} // Fim do else 2
							} // Fim do else 1
						} // Fim do for
					} // Fim do if 1
					
					// Se nao tem dragao na mesa, devemos atacar o heroi do oponente com todos os nossos lacaios
					else{ // Inicio do else
						// Se houver mana disponivel para alguma carta de lacaio, devemos baixar na mesa
					    // esta carta de lacaio (qualquer carta de lacaio que a mana permita)
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.LACAIO && card.getMana() <= minhaMana){ // Inicio do if
								Jogada lac = new Jogada(TipoJogada.LACAIO, card, null);
								minhasJogadas.add(lac);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if
						} // Fim do for
					} // Fim do else
					
					if(flagUsouMagia == false){
						// Se houver mana disponivel para alguma magia, o jogador devera utilizar esta carta de magia.
						// Se a magia for de alvo, devera mirar no heroi do oponente (qualquer carta magia que a mana permita)
						// Deve-se utilizar no maximo uma magia
						for(int i = 0; i < mao.size(); i++){ // Inicio do for
							Carta card = mao.get(i);
							if(card.getTipo() == TipoCarta.MAGIA && card.getMana() <= minhaMana){ // Inicio do if
								Jogada mag = new Jogada(TipoJogada.MAGIA, card, null);
								minhasJogadas.add(mag);
								minhaMana -= card.getMana();
								mao.remove(i);
								i--;
								break;
							} // Fim do if
						} // Fim do for*/
					}
					
					// ##################### Inicio dos ataques com lacaios ######################
					//int numberLacaiosMeusQueAtacaram = 0; // informa quantos lacaios meus ja atacaram algo (lacaio ou heroi do oponente)
					
					
					if(lacaiosMeus.size() > 0){ // Inicio do if
						for(int i = 0; i < lacaiosMeus.size(); i++){ // Inicio do for
							Carta meuLacaioVivo = lacaiosMeus.get(i);
							Jogada atk = new Jogada(TipoJogada.ATAQUE, meuLacaioVivo, null);
							minhasJogadas.add(atk);
						} // Fim do for
					} // Fim do if*/
					
					// Se apos todas as jogadas tivermos mana o suficiente para o ataque heroico, o utilizamos contra o heroi do oponente
					if(minhaMana >= 2){ // Inicio do if
					Jogada pod = new Jogada(TipoJogada.PODER, null, null);
					minhasJogadas.add(pod);
					} // Fim do if
				}
			} // Fim do else (SOU JOGADOR 2)
		} // Fim do IF PRINCIPAL
		
		return minhasJogadas; // Retorna as jogadas efetuadas para o motor
	}	
}