package com.github.cm.model;

import java.util.ArrayList;
import java.util.List;

public class Campo {

	private final int LINHA;
	private final int COLUNA;
	
	private boolean aberto;
	private boolean minado;
	private boolean marcado;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();
	
	public Campo(int linha, int coluna) {
		this.LINHA = linha;
		this.COLUNA = coluna;
	}
	
	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(CampoEvento evento) {
		observadores.stream()
			.forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	boolean adicionarVizinho(Campo vizinho) {
		//Se os dois forem diferentes significa que é diagonal.
		boolean linhaDiferente = this.LINHA != vizinho.LINHA;
		boolean colunaDiferente = this.COLUNA != vizinho.COLUNA;
		
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(this.LINHA - vizinho.LINHA);
		int deltaColuna = Math.abs(this.COLUNA - vizinho.COLUNA);
		
		int deltaGeral = deltaLinha + deltaColuna;
		
		if(deltaGeral == 1 && !diagonal) {
			
			vizinhos.add(vizinho);
			return true;
		} else if(deltaGeral == 2 && diagonal) {
			
			vizinhos.add(vizinho);
			return true;
		} else {
			
			return false;
		}
	}
	
	public void alternarMarcacao() {
		
		if(!aberto) {
			marcado = !marcado;
			
			if(marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
		
	}
	
	public boolean abrir() {
		
		if(!aberto && !marcado) {
			if(minado) {
				
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);
			
			if(vizinhancaSegura()) {
				
				vizinhos.forEach(v -> v.abrir());
				
			}
			
			return true;
			
		}else {
			
			return false;
			
		}
		
	}
	
	public boolean vizinhancaSegura() {
		
		return vizinhos.stream().noneMatch(v -> v.minado);
		
	}
	
	void minar() {

		minado = true;
		
	}
	
	public boolean isMinado() {
		
		return minado;
		
	}
	
	public boolean isMarcado() {
		
		return marcado;
		
	}
	
	void setAberto(boolean aberto) {
		
		this.aberto = aberto;
		
		if(aberto) {
			
			notificarObservadores(CampoEvento.ABRIR);
		}
	}
	
	public boolean isAberto() {
		return aberto;
	}
	public boolean isFechado() {
		return !isAberto();
	}

	public int getLINHA() {
		return LINHA;
	}

	public int getCOLUNA() {
		return COLUNA;
	}
	
	boolean objetivoAlcancado() {
		
		//objetivo do campo alcançado, ou ele tá desvendado ou tá com flag.
		
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		
		return desvendado || protegido;
		
	}
	
	public int minasNaVizinhanca() {
		
		return (int) vizinhos.stream().filter(v -> v.minado).count();
		
	}
	
	void reiniciar() {
		this.aberto = false;
		this.marcado = false;
		this.minado = false;
		notificarObservadores(CampoEvento.REINICIAR);
		
	}
}
