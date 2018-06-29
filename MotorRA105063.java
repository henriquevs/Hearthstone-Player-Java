import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
/* Lab7 (Trabalho 2)
 * Nome: Henrique Vicente Souza
 * RA: 105063
 * 
 * Completude do Trabalho 2: 10 (De 0 a 10, escreva quanto você acha que "completou" das atividades propostas)
 * Escreva aqui (sucintamente) os tratamentos que você implementou: Tratamento dos erros de codigo 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 e 12.
 * Escreva aqui (sucintamente) o que faltou você implementar: --------
 * 
 * Obs: Este formulário não irá implicar em sua nota, apenas serve para facilitar o processo de correção.
 */

public class MotorRA105063 extends Motor {

	public MotorRA105063(Baralho deck1, Baralho deck2, ArrayList<Carta> mao1,
			ArrayList<Carta> mao2, Jogador jogador1, Jogador jogador2,
			int verbose, int tempoLimitado, PrintWriter saidaArquivo) {
		super(deck1, deck2, mao1, mao2, jogador1, jogador2, verbose,
				tempoLimitado, saidaArquivo);
		// TODO Auto-generated constructor stub
	}
	
	private int jogador; // 1== turno do jogador1, 2 == turno do jogador2
	private int turno;
	private int nCartasHeroi1;
	private int nCartasHeroi2;
	
	private Mesa mesa;
	
	// "Apontadores" - Assim pode-se programar genericamente os métodos para funcionar com ambos os jogadores
	private ArrayList<Carta> mao;
	private ArrayList<Carta> lacaios;
	private ArrayList<Carta> lacaiosOponente;
	
	// Array list usado para computar as jogadas
	private ArrayList<Carta> lacaiosQueAtacaram;
	
	// Array list usado para memorizar os lacaios que foram baixados no turno corrente
	private ArrayList<Carta> lacaiosTurnoAtual;
	
	// "Memória" - Para marcar Jogadas que só podem ser realizadas uma vez por turno.
	private boolean poderHeroicoUsado;
	private HashSet<Integer> lacaiosAtacaramID;

	@Override
	public GameStatus executarPartida() {
		vidaHeroi1 = vidaHeroi2 = 30;
		manaJogador1 = manaJogador2 = 1;
		nCartasHeroi1 = cartasIniJogador1; 
		nCartasHeroi2 = cartasIniJogador2;
		ArrayList<Jogada> movimentos = new ArrayList<Jogada>();
		int noCardDmgCounter1 = 1;
		int noCardDmgCounter2 = 1;
		turno = 1; // Contador de turnos
		
		for(int k = 0; k < 60; k++){
			imprimir("\n=== TURNO "+turno+" ===\n");
			// Atualiza mesa
			@SuppressWarnings("unchecked")
			ArrayList<Carta> lacaios1clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa1);
			@SuppressWarnings("unchecked")
			ArrayList<Carta> lacaios2clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa2);
			mesa = new Mesa(lacaios1clone, lacaios2clone, vidaHeroi1, vidaHeroi2, nCartasHeroi1+1, nCartasHeroi2, turno>10?10:turno, turno>10?10:(turno==1?2:turno));
			
			// atualizacao da quantidade de mana para o jogador
			this.manaJogador1 = (k>10)?10:k;
			if(turno == 1) // no primeiro turno o jogador 2 deve ter 2 de mana
				this.manaJogador2 = 2;
			else
				this.manaJogador2 = (turno>10)?10:turno;
			
			// Apontadores para jogador1
			mao = maoJogador1;
			lacaios = lacaiosMesa1;
			lacaiosOponente = lacaiosMesa2;
			jogador = 1;
			
			// Resetamos os ArrayLists que armazenam as jogadas atuais e os lacaios baixados no turno atual
			lacaiosQueAtacaram = new ArrayList<Carta> ();
			lacaiosTurnoAtual = new ArrayList<Carta> ();
			
			// Processa o turno 1 do Jogador1
			imprimir("\n----------------------- Começo de turno para Jogador 1:");
			long startTime, endTime, totalTime;
			
			// Copia de movimentos da mão (do contrário pode referenciar cartas do campo do outro jogador e confundi-lo).
			@SuppressWarnings("unchecked")
			ArrayList<Jogada> cloneMovimentos1 = (ArrayList<Jogada>) UnoptimizedDeepCopy.copy(movimentos);
			
			startTime = System.nanoTime();
			if( baralho1.getCartas().size() > 0){
				if(nCartasHeroi1 >= 10){
					movimentos = jogador1.processarTurno(mesa, null, cloneMovimentos1);
					comprarCarta(); // carta descartada
				}
				else
					movimentos = jogador1.processarTurno(mesa, comprarCarta(), cloneMovimentos1);
			}
			else{
				imprimir("Fadiga: O Herói 1 recebeu "+noCardDmgCounter1+" de dano por falta de cartas no baralho. (Vida restante: "+(vidaHeroi1-noCardDmgCounter1)+").");
				vidaHeroi1 -= noCardDmgCounter1++;
				if( vidaHeroi1 <= 0){
					// Jogador 2 venceu
					imprimir("O jogador 2 venceu porque o jogador 1 recebeu um dano mortal por falta de cartas ! (Dano : " +(noCardDmgCounter1-1)+ ", Vida Herói 1: "+vidaHeroi1+")");
					return null;
				}
				movimentos = jogador1.processarTurno(mesa, null, cloneMovimentos1);
			}
			endTime = System.nanoTime();
			totalTime = endTime - startTime;
			if( tempoLimitado!=0 && totalTime > 3e8){ // 300ms
				// Jogador 2 venceu, Jogador 1 excedeu limite de tempo
				return null;
			}
			else
				imprimir("Tempo usado em processarTurno(): "+totalTime/1e6+"ms");

			// Começa a processar jogadas do Jogador 1
			this.poderHeroicoUsado = false;
            this.lacaiosAtacaramID = new HashSet<Integer>();
			for(int i = 0; i < movimentos.size(); i++){
				GameStatus status = processarJogada (movimentos.get(i));
				if(status != null){
					// Erro foi detecado.
					return status;
				}
			}
			
			if( vidaHeroi2 <= 0){
				// Jogador 1 venceu
				// return null;
				return new GameStatus(0, 1, null, null, ""); // Fim de jogo normal (jogador 1 venceu)
			}
			
			// Atualiza mesa
			@SuppressWarnings("unchecked")
			ArrayList<Carta> lacaios1clone2 = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa1);
			@SuppressWarnings("unchecked")
			ArrayList<Carta> lacaios2clone2 = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa2);
			mesa = new Mesa(lacaios1clone2, lacaios2clone2, vidaHeroi1, vidaHeroi2, nCartasHeroi1, nCartasHeroi2+1, turno>10?10:turno, turno>10?10:(turno==1?2:turno));
			
			// Apontadores para jogador2
			mao = maoJogador2;
			lacaios = lacaiosMesa2;
			lacaiosOponente = lacaiosMesa1;
			jogador = 2;
			
			// Processa o turno 1 do Jogador2
			imprimir("\n\n----------------------- Começo de turno para Jogador 2:");
			
			// Copia de movimentos da mão.
			@SuppressWarnings("unchecked")
			ArrayList<Jogada> cloneMovimentos2 = (ArrayList<Jogada>) UnoptimizedDeepCopy.copy(movimentos);
			
			startTime = System.nanoTime();

			
			if( baralho2.getCartas().size() > 0){
				if(nCartasHeroi2 >= 10){
					movimentos = jogador2.processarTurno(mesa, null, cloneMovimentos2);
					comprarCarta(); // carta descartada
				}
				else
					movimentos = jogador2.processarTurno(mesa, comprarCarta(), cloneMovimentos2);
			}
			else{
				imprimir("Fadiga: O Herói 2 recebeu "+noCardDmgCounter2+" de dano por falta de cartas no baralho. (Vida restante: "+(vidaHeroi2-noCardDmgCounter2)+").");
				vidaHeroi2 -= noCardDmgCounter2++;
				if( vidaHeroi2 <= 0){
					// Vitoria do jogador 1
					imprimir("O jogador 1 venceu porque o jogador 2 recebeu um dano mortal por falta de cartas ! (Dano : " +(noCardDmgCounter2-1)+ ", Vida Herói 2: "+vidaHeroi2 +")");
					return null;
				}
				movimentos = jogador2.processarTurno(mesa, null, cloneMovimentos2);
			}
						
			endTime = System.nanoTime();
			totalTime = endTime - startTime;
			if( tempoLimitado!=0 && totalTime > 3e8){ // 300ms
				// Limite de tempo pelo jogador 2. Vitoria do jogador 1.
			}
			else
				imprimir("Tempo usado em processarTurno(): "+totalTime/1e6+"ms");
			
			this.poderHeroicoUsado = false; // poder heroico ainda nao foi usado
			
            this.lacaiosAtacaramID = new HashSet<Integer>();
			for(int i = 0; i < movimentos.size(); i++){
				GameStatus status = processarJogada (movimentos.get(i));
				if(status != null){
					// Erro foi detecado.
					return status;
				}
			}
			if( vidaHeroi1 <= 0){
				// Vitoria do jogador 2
				// return null;
				return new GameStatus(0, 2, null, null, ""); // Fim de jogo normal (jogador 2 venceu)
			}
			turno++;
		}
		
		// Nunca vai chegar aqui dependendo do número de rodadas
		imprimir("Erro: A partida não pode ser determinada em mais de 60 rounds. Provavel BUG.");
		return null;
	}

	@Override
	protected GameStatus processarJogada(Jogada umaJogada) {
		// TODO Auto-generated method stub
		Mesa mesaAntesJogada = gerarMesaAtual();
		switch(umaJogada.getTipo()){
		case ATAQUE:
			
			// TODO: Um ataque foi realizado... quem atacou? quem foi atacado? qual o dano? o alvo morreu ou ficou com quanto de vida? Trate o caso do herói como alvo também.		
			
			// Erro Codigo 5 = Atacar com lacaio invalido de origem do ataque (tentar atacar com um lacaio que nao possui)
			// Verificamos se o lacaio que o jogador quer usar para atacar esta ou nao no ArrayList
			// Quando o valor do indice retornado eh -1, significa que o ArrayList nao contem o elemento
			if(lacaios.indexOf(umaJogada.getCartaJogada()) == -1){ // Inicio do if
				// Imprime a mensagem de erro
				String msg_error = "ERRO CODIGO 5 (Atacar com lacaio invalido de origem do ataque) : ID (invalido)="+umaJogada.getCartaJogada().getID()+ " da carta lacaio que iria atacar.";
				imprimir(msg_error);
				return new GameStatus(5, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Recuperamos a carta que o jogador colocou na mesa e comparamos com a que foi inicialmente
			// fornecida ao motor (verificamos para o caso de um jogador tentar enganar o motor, atraves
			// da modificacao do poder de ataque de seu lacaio) 
			Carta atacante = lacaios.get(lacaios.indexOf(umaJogada.getCartaJogada()));
			
			// Erro Codigo 6 = Atacar com um lacaio que foi baixado neste turno
			// Caso em que o lacaio ja foi baixado no turno corrente => o ataque nao pode ocorrer!
			if(lacaiosTurnoAtual.contains(atacante) == true){ // Inicio do if
				String msg_error = "ERRO CODIGO 6 (Atacar com um lacaio que foi baixado no turno atual) : ID da carta lacaio que iria atacar="+umaJogada.getCartaJogada().getID()+".";
				imprimir(msg_error);
				return new GameStatus(6, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Erro Codigo 7 = Atacar com um lacaio mais de uma vez por turno
			// Caso em que o jogador tenta usar um mesmo lacaio para atacar mais de uma vez => o ataque nao pode ocorrer!
			if(lacaiosQueAtacaram.contains(atacante) == true){ // Inicio do if
				String msg_error = "ERRO CODIGO 7 (Atacar com um lacaio mais de uma vez por turno) : ID da carta lacaio que iria atacar="+umaJogada.getCartaJogada().getID()+".";
				imprimir(msg_error);
				return new GameStatus(7, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Ataque contra o heroi do oponente
			if(umaJogada.getCartaAlvo() == null){ // Inicio do if
				
				imprimir("JOGADA DE ATAQUE: O lacaio id="+umaJogada.getCartaJogada().getID()+" (dano="+atacante.getAtaque()+") atacou o heroi "+((jogador==1)?2:1)+" (vida antes="+((jogador==1)?this.vidaHeroi2:this.vidaHeroi1)+" => vida depois="+((jogador==1)?this.vidaHeroi2-atacante.getAtaque():this.vidaHeroi1-atacante.getAtaque())+")");
				
				// Situacao normal de jogo (quando nenhum jogagor viola as regras)
				if(this.jogador == 1){ // Inicio do if 2
					// Se formos o primeiro jogador
					this.vidaHeroi2 -= atacante.getAtaque();
					// Se o heroi do oponente (heroi 2) nao tem mais vida, a partida acabou
					if(this.vidaHeroi2 <= 0){ // Inicio do if 3
						return new GameStatus(0, 1, null, null, ""); // Fim de jogo normal (jogador 1 venceu)
					} // Fim do if 3
				}// Fim do if 2
				else{ // Inicio do else
					// Se nao somos o primeiro jogador, entao somos o segundo
					this.vidaHeroi1 -= atacante.getAtaque();
					// Se o heroi do oponente (heroi 1) nao tem mais vida, a partida acabou
					if(this.vidaHeroi1 <= 0){ // Inicio do if 4
						return new GameStatus(0, 2, null, null, ""); // Fim de jogo normal (jogador 2 venceu)
					} // Fim do if 4
				} // Fim do else
				
			} // Fim do if
			// Ataque contra um lacaio do oponente
			else{ // Inicio do else
				
				imprimir("JOGADA DE ATAQUE: O lacaio id="+umaJogada.getCartaJogada().getID()+" atacou o lacaio id="+umaJogada.getCartaAlvo().getID());
				
				// PROBLEMA: Caso em que o oponente nao possui lacaios na mesa
				if(lacaiosOponente.indexOf(umaJogada.getCartaAlvo()) != -1){ // Inicio do if
					
					// Recuperamos a carta "lacaio" do oponente que recebera o ataque
					Carta atacado = lacaiosOponente.get(lacaiosOponente.indexOf(umaJogada.getCartaAlvo()));
					
					// Atualizamos a vida do lacaio atacado
					atacado.setVidaMesa(atacado.getVidaMesa() - atacante.getAtaque());
					// Atualizamos a vida do lacaio que atacou
					atacante.setVidaMesa(atacante.getVidaMesa() - atacado.getAtaque());
					// Verificamos se o lacaio atacado morreu, neste caso ele eh retirado do ArrayList
					if(atacado.getVidaMesa() <= 0){ // Inicio do if
						lacaiosOponente.remove(atacado);
					} // Fim do if
					if(atacante.getVidaMesa() <= 0){ // Inicio do if 2
						lacaios.remove(atacante);
					} // Fim do if 2
					
				} // Fim do if
				
				// Erro Codigo 8 = Atacar com um lacaio um alvo invalido
				// Caso em que o jogador tenta atacar um lacaio que o oponente nao tem => o ataque nao pode ocorrer!
				else{ // Inicio do else
					String msg_error = "ERRO CODIGO 8 (Atacar com um lacaio um alvo invalido) : ID da carta lacaio que iria atacar="+umaJogada.getCartaJogada().getID() +" e ID (invalido) do alvo="+umaJogada.getCartaAlvo().getID()+".";
					imprimir(msg_error);
					return new GameStatus(8, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do else
				
			} // Fim do else
			// Guardamos na "memoria" os lacaios que atacaram
			lacaiosQueAtacaram.add(atacante); // O lacaio que atacou eh adicionado ao ArrayList
						
			break;
		case LACAIO:
			int lacaioID = umaJogada.getCartaJogada().getID();
			imprimir("JOGADA: O lacaio_id="+lacaioID+" ("+umaJogada.getCartaJogada().getNome()+") foi baixado para a mesa.");
			if(mao.contains(umaJogada.getCartaJogada())){
				Carta lacaioBaixado = mao.get(mao.indexOf(umaJogada.getCartaJogada()));
				
				// Erro Codigo 2 = Realizar uma jogada que o limite de mana não permite 
				// Caso em que o jogador tenta realizar uma jogada sem ter mana suficiente para tal
				if((this.manaJogador1 < lacaioBaixado.getMana() && jogador == 1) || (this.manaJogador2 < lacaioBaixado.getMana() && jogador == 2)){ // Inicio do if
					String msg_error = "ERRO CODIGO 2 (Realizar uma jogada que o limite de mana não permite) : A jogada do tipo LACAIO custaria " +lacaioBaixado.getMana()+ " de mana, porem soh ha " +( (jogador==1)?this.manaJogador1 : this.manaJogador2)+  " de mana disponivel.";
					imprimir(msg_error);
					return new GameStatus(2, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do if
				
				// Erro Codigo 4 = Baixar um lacaio já tendo sete outros lacaios em mesa 
				if(lacaios.size() >= 7){ // Inicio do if
					String msg_error = "ERRO CODIGO 4 (Baixar um lacaio já tendo sete outros lacaios em mesa) : A carta "+lacaioBaixado.getNome()+ "com ID="+umaJogada.getCartaJogada().getID() +" seria utilizada incorretamente.";
					imprimir(msg_error);
					return new GameStatus(4, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do if
				
				// Erro Codigo 9 = Tentar usar uma carta de lacaio como uma magia 
				// Caso em que o jogador tenta baixar uma magia no lugar de um lacaio
				if(lacaioBaixado.getTipo() != TipoCarta.LACAIO){ // Inicio do if
					String msg_error = "ERRO CODIGO 9 (Tentar usar uma carta de lacaio como uma magia) : A carta "+lacaioBaixado.getNome()+ " com ID="+umaJogada.getCartaJogada().getID() +" seria utilizada incorretamente.";
					imprimir(msg_error);
					return new GameStatus(9, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do if
				
				// Adiciono o lacaio baixado na lista de lacaios do turno atual 
				lacaiosTurnoAtual.add(lacaioBaixado);
				
				lacaios.add(lacaioBaixado); // lacaio adicionado à mesa
				mao.remove(umaJogada.getCartaJogada()); // lacaio retirado da mão
				
				// Atualiza a mana do jogador
				if(jogador == 1){ // caso de ser o primeiro jogador
					this.manaJogador1 -= lacaioBaixado.getMana();
				}
				else{ // caso de ser o primeiro jogador
					this.manaJogador2 -= lacaioBaixado.getMana();
				}
				
			}
			else{
				String erroMensagem = "ERRO CODIGO 1: Tentou-se baixar o lacaio_id="+lacaioID+", porém esta carta não existe na mao. IDs de cartas na mao: ";
				for(Carta card : mao){
					erroMensagem += card.getID() + ", ";
				}
				
				// Erro Codigo 1 = Baixar lacaio ou usar magia sem possuir a carta na mão
				imprimir(erroMensagem);
				GameStatus erroCartaNaoExistente = new GameStatus(1, jogador==1?2:1, umaJogada, mesaAntesJogada, erroMensagem);
				return erroCartaNaoExistente;
			}
			// Obs: repare que este código funcionará tanto para o jogador1 ou jogador2. Como ?
			// Obs2: está faltando a verificação do limite de 7 lacaios na mesa.
			break;
		case MAGIA:
			// TODO: Uma magia foi usada... é de área ou alvo? Se de alvo, qual o alvo ?
			
			int magiaID = umaJogada.getCartaJogada().getID();
			imprimir("JOGADA: A magia_id="+magiaID+" foi baixada na mesa.");
			
			if(mao.contains(umaJogada.getCartaJogada())){
				Carta magiaBaixada = mao.get(mao.indexOf(umaJogada.getCartaJogada()));
				
				// Erro Codigo 2 = Realizar uma jogada que o limite de mana não permite 
				// Caso em que o jogador tenta realizar uma jogada sem ter mana suficiente para tal
				if((this.manaJogador1 < magiaBaixada.getMana() && jogador == 1) || (this.manaJogador2 < magiaBaixada.getMana() && jogador == 2)){ // Inicio do if
					String msg_error = "ERRO CODIGO 2 (Realizar uma jogada que o limite de mana não permite) : A jogada do tipo MAGIA custaria " +magiaBaixada.getMana()+ " de mana, porem soh ha " +( (jogador==1)?this.manaJogador1 : this.manaJogador2)+  " de mana disponivel.";
					imprimir(msg_error);
					return new GameStatus(2, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do if
				
				// Erro Codigo 3 = Tentar usar uma carta de magia como uma de lacaio 
				// Caso em que o jogador tenta baixar um lacaio no lugar de uma magia
				if(magiaBaixada.getTipo() != TipoCarta.MAGIA){ // Inicio do if
					String msg_error = "ERRO CODIGO 3 (Tentar usar uma carta de magia como uma de lacaio) : A carta "+magiaBaixada.getNome()+ " com ID="+umaJogada.getCartaJogada().getID() + " e mana="+umaJogada.getCartaJogada().getMana()+ " seria utilizada incorretamente.";
					imprimir(msg_error);
					return new GameStatus(3, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
				} // Fim do if
				
				// Processamento da Magia (computar o dano no oponente)
				// Caso da utilizacao de uma magia de alvo
				if(magiaBaixada.getMagiaTipo() == TipoMagia.ALVO){ // Inicio do if externo
					// Caso de ataque no heroi do oponente
					
					// Ataque de MAGIA contra o heroi do oponente
					if(umaJogada.getCartaAlvo() == null){ // Inicio do if
						// Situacao normal de jogo (quando nenhum jogagor viola as regras)
						if(this.jogador == 1){ // Inicio do if 2
							// Se formos o primeiro jogador
							this.vidaHeroi2 -= magiaBaixada.getMagiaDano();
							// Se o heroi do oponente (heroi 2) nao tem mais vida, a partida acabou
							if(this.vidaHeroi2 <= 0){ // Inicio do if 3
								return new GameStatus(0, 1, null, null, ""); // Fim de jogo normal (jogador 1 venceu)
							} // Fim do if 3
						}// Fim do if 2
						else{ // Inicio do else
							// Se nao somos o primeiro jogador, entao somos o segundo
							this.vidaHeroi1 -= magiaBaixada.getMagiaDano();
							// Se o heroi do oponente (heroi 1) nao tem mais vida, a partida acabou
							if(this.vidaHeroi1 <= 0){ // Inicio do if 4
								return new GameStatus(0, 2, null, null, ""); // Fim de jogo normal (jogador 2 venceu)
							} // Fim do if 4
						} // Fim do else
					}
					else{ // Inicio do else 2
						
						// PROBLEMA: Caso em que o oponente nao possui lacaios na mesa
						if(lacaiosOponente.indexOf(umaJogada.getCartaAlvo()) != -1){ // Inicio do if

							// Recuperamos a carta "lacaio" do oponente que recebera o ataque
							Carta atacado = lacaiosOponente.get(lacaiosOponente.indexOf(umaJogada.getCartaAlvo()));
							
							// Atualizamos a vida do lacaio atacado
							atacado.setVidaMesa(atacado.getVidaMesa() - magiaBaixada.getMagiaDano());
							
							// Verificamos se o lacaio atacado morreu, neste caso ele eh retirado do ArrayList
							if(atacado.getVidaMesa() <= 0){ // Inicio do if
								lacaiosOponente.remove(atacado);
							} // Fim do if
						}
						// Erro Codigo 10 = Usar uma magia de alvo em um alvo inválido
						// Caso em que o jogador tenta atacar um lacaio que o oponente nao tem => o ataque nao pode ocorrer!
						else{ // Inicio do else
							String msg_error = "ERRO CODIGO 10 (Usar uma magia de alvo em um alvo inválido) : ID da MAGIA que iria ser utilizada="+umaJogada.getCartaJogada().getID() +"e ID (invalido) do alvo="+umaJogada.getCartaAlvo().getID()+".";
							imprimir(msg_error);
							return new GameStatus(10, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
						} // Fim do else
					} // Fim do else 2
				}// Fim do if externo
				
				if(magiaBaixada.getMagiaTipo() == TipoMagia.AREA){
					
					// Diminuimos a vida de todos os lacaios do oponente (uso da MAGIA de area)
					for(int i=0; i<lacaiosOponente.size(); i++){
						// Recuperamos a carta "lacaio" do oponente que recebera o ataque
						Carta atacado = lacaiosOponente.get(i);
						
						// Atualizamos a vida do lacaio atacado
						atacado.setVidaMesa(atacado.getVidaMesa() - magiaBaixada.getMagiaDano());
						
						// Verificamos se o lacaio atacado morreu, neste caso ele eh retirado do ArrayList
						if(atacado.getVidaMesa() <= 0){ // Inicio do if
							// Para cada lacaio do oponente que removemos da mesa, devemos atualizar
							// corretamente o contador do loop "i"
							// Ex: caso removamos o segundo elemento do arrayList (segundo lacaio da lista de
							// lacaios do oponente), o terceiro ocupara o lugar do segundo, o quarto do terceiro,
							// e assim por diante.. Isso faz com que deixemos de analisar um lacaio do oponente
							// (caso exista) na proxima iteracao do loop
							i--;
							lacaiosOponente.remove(atacado);
						} // Fim do if
					}
						
					// Ataque de MAGIA contra o heroi do oponente
					// Situacao normal de jogo (quando nenhum jogagor viola as regras)
					if(this.jogador == 1){ // Inicio do if 2
						// Se formos o primeiro jogador
						this.vidaHeroi2 -= magiaBaixada.getMagiaDano();
						// Se o heroi do oponente (heroi 2) nao tem mais vida, a partida acabou
						if(this.vidaHeroi2 <= 0){ // Inicio do if 3
							return new GameStatus(0, 1, null, null, ""); // Fim de jogo normal (jogador 1 venceu)
						} // Fim do if 3
					}// Fim do if 2
					else{ // Inicio do else
						// Se nao somos o primeiro jogador, entao somos o segundo
						this.vidaHeroi1 -= magiaBaixada.getMagiaDano();
						// Se o heroi do oponente (heroi 1) nao tem mais vida, a partida acabou
						if(this.vidaHeroi1 <= 0){ // Inicio do if 4
							return new GameStatus(0, 2, null, null, ""); // Fim de jogo normal (jogador 2 venceu)
						} // Fim do if 4
					} // Fim do else
				}
									
				mao.remove(umaJogada.getCartaJogada()); // lacaio retirado da mão
				
				// Atualiza a mana do jogador
				if(jogador == 1){ // caso de ser o primeiro jogador
					this.manaJogador1 -= magiaBaixada.getMana();
				}
				else{ // caso de ser o primeiro jogador
					this.manaJogador2 -= magiaBaixada.getMana();
				}
				
			}
			else{
				String erroMensagem = "Erro: Tentou-se baixar o magia_id="+magiaID+", porém esta carta não existe na mao. IDs de cartas na mao: ";
				for(Carta card : mao){
					erroMensagem += card.getID() + ", ";
				}
				
				// Erro Codigo 1 = Baixar lacaio ou usar magia sem possuir a carta na mão
				imprimir(erroMensagem);
				GameStatus erroCartaNaoExistente = new GameStatus(1, jogador==1?2:1, umaJogada, mesaAntesJogada, erroMensagem);
				return erroCartaNaoExistente;
			}
			
			break;
		case PODER:
			// TODO: O poder heroico foi usado... Foi no outro heroi ou em qual lacaio ?
			
			// Erro Codigo 2 = Realizar uma jogada que o limite de mana não permite 
			// Caso em que o jogador tenta realizar uma jogada sem ter mana suficiente para tal
			if((this.manaJogador1 < 2 && jogador == 1) || (this.manaJogador2 < 2 && jogador == 2)){ // Inicio do if
				String msg_error = "ERRO CODIGO 2 (Realizar uma jogada que o limite de mana não permite) : A jogada do tipo PODER HEROICO custaria 2 de mana, porem soh ha " +( (jogador==1)?this.manaJogador1 : this.manaJogador2)+  " de mana disponivel.";
				imprimir(msg_error);
				return new GameStatus(2, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Erro Codigo 11 = Usar o poder heroico mais de uma vez por turno
			// Caso em que o jogador tenta usar o poder heroico mais de uma vez no mesmo turno
			if(this.poderHeroicoUsado == true){ // Inicio do if
				// O poder heroico soh pode ser usado 1 vez por turno
				String msg_error = "ERRO CODIGO 11 (Usar o poder heroico mais de uma vez por turno) : id do alvo que seria atacado="+umaJogada.getCartaAlvo().getID()+".";
				imprimir(msg_error);
				return new GameStatus(11, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Erro Codigo 12 = Usar o poder heroico em um alvo invalido
			if(umaJogada.getCartaAlvo() != null && !lacaiosOponente.contains(umaJogada.getCartaAlvo())){ // Inicio do if
				String msg_error = "ERRO CODIGO 12 (Usar o poder heroico em um alvo invalido) : id do alvo (invalido) que seria atacado="+umaJogada.getCartaAlvo().getID()+".";
				imprimir(msg_error);
				return new GameStatus(12, (jogador==1)?2:1, umaJogada, mesaAntesJogada, msg_error); // Fim de jogo com erro
			} // Fim do if
			
			// Atualiza a mana do jogador
			if(jogador == 1){ // caso de ser o primeiro jogador
				this.manaJogador1 -= 2;
			}
			else{ // caso de ser o primeiro jogador
				this.manaJogador2 -= 2;
			}
			
			// Atualizamos a flag que informa se uma jogada de poder heroico ja foi usada
			this.poderHeroicoUsado = true;
			
			// Caso ataquemos o heroi do oponente, devemos atualizar a sua vida
			if(umaJogada.getCartaAlvo() == null){ // Inicio do if
				if(jogador == 1){ // Inicio do if 2
					this.vidaHeroi2 -= 1;
				} // Fim do if 2
				else{ // Inicio do else
					this.vidaHeroi1 -= 1;
				} // Fim do else
			} // Fim do if
			else{ // Caso de ataque heroico contra um lacaio do oponente
				// Verificamos se o oponente tem o lacaio alvo que desejamos atacar
				if(lacaiosOponente.contains(umaJogada.getCartaAlvo())){ // Inicio do if
					// Recuperamos a carta "lacaio" do oponente que recebera o ataque
					Carta atacado = lacaiosOponente.get(lacaiosOponente.indexOf(umaJogada.getCartaAlvo()));
					
					// Atualizamos a vida do lacaio atacado
					atacado.setVidaMesa(atacado.getVidaMesa() - 1);
					
					// Verificamos se o lacaio atacado morreu, neste caso ele eh retirado do ArrayList
					if(atacado.getVidaMesa() <= 0){ // Inicio do if
						lacaiosOponente.remove(atacado);
					} // Fim do if
					
				} // Fim do if
			}
			break;
		default:
			break;
		}
		return null;
	}
	
	private Mesa gerarMesaAtual(){
		// Atualiza mesa
		@SuppressWarnings("unchecked")
		ArrayList<Carta> lacaios1clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa1);
		@SuppressWarnings("unchecked")
		ArrayList<Carta> lacaios2clone = (ArrayList<Carta>) UnoptimizedDeepCopy.copy(lacaiosMesa2);
		Mesa currentMesa = new Mesa(lacaios1clone, lacaios2clone, vidaHeroi1, vidaHeroi2, maoJogador1.size(), maoJogador2.size(), turno>10?10:turno, turno>10?10:(turno==1?2:turno));
		return currentMesa;
	}
	
	private Carta comprarCarta(){
		if(this.jogador == 1){
			if(baralho1.getCartas().size() <= 0)
				throw new RuntimeException("Não há mais cartas no baralho para serem compradas.");
			Carta nova = baralho1.comprarCarta();
			mao.add(nova);
			nCartasHeroi1++;
			return (Carta) UnoptimizedDeepCopy.copy(nova);
		}
		else{
			if(baralho2.getCartas().size() <= 0)
				throw new RuntimeException("Não há mais cartas no baralho para serem compradas.");
			Carta nova = baralho2.comprarCarta();
			mao.add(nova);
			nCartasHeroi2++;
			return (Carta) UnoptimizedDeepCopy.copy(nova);
		}
	}

}