package br.com.fra.brasileiraoapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartidaGoogleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Informações do status da Partida
	private String statusPartida;
	private String tempoPartida;
	
	//Informações da equipe da casa
	private String nomeEquipeCasa;
	private String urlLogoEquipeCasa;
	private Integer placarEquipeCasa;
	private String golsEquipeCasa;
	private String placarEstendidoEquipeCasa;
	
	//Informações da equipe visitante
	private String nomeEquipeVisitante;
	private String urlLogoEquipeVisitante;
	private Integer placarEquipeVisitante;
	private String golsEquipeVisitante;
	private String placarEstendidoEquipeVisitante;
	
	public String getStatusPartida() {
		return statusPartida;
	}
	
	public String getPlacarEstendidoEquipeVisitante() {
		return placarEstendidoEquipeVisitante;
	}
	
	public void setPlacarEstendidoEquipeVisitante(String placarEstendidoEquipeVisitante) {
		this.placarEstendidoEquipeVisitante = placarEstendidoEquipeVisitante;
	}
	
	public String getGolsEquipeVisitante() {
		return golsEquipeVisitante;
	}
	
	public void setGolsEquipeVisitante(String golsEquipeVisitante) {
		this.golsEquipeVisitante = golsEquipeVisitante;
	}
	
	public Integer getPlacarEquipeVisitante() {
		return placarEquipeVisitante;
	}
	
	public void setPlacarEquipeVisitante(Integer placarEquipeVisitante) {
		this.placarEquipeVisitante = placarEquipeVisitante;
	}
	
	public String getUrlLogoEquipeVisitante() {
		return urlLogoEquipeVisitante;
	}
	
	public void setUrlLogoEquipeVisitante(String urlLogoEquipeVisitante) {
		this.urlLogoEquipeVisitante = urlLogoEquipeVisitante;
	}
	
	public String getNomeEquipeVisitante() {
		return nomeEquipeVisitante;
	}
	
	public void setNomeEquipeVisitante(String nomeEquipeVisitante) {
		this.nomeEquipeVisitante = nomeEquipeVisitante;
	}
	
	public String getPlacarEstendidoEquipeCasa() {
		return placarEstendidoEquipeCasa;
	}
	
	public void setPlacarEstendidoEquipeCasa(String placarEstendidoEquipeCasa) {
		this.placarEstendidoEquipeCasa = placarEstendidoEquipeCasa;
	}
	
	public String getGolsEquipeCasa() {
		return golsEquipeCasa;
	}
	
	public void setGolsEquipeCasa(String golsEquipeCasa) {
		this.golsEquipeCasa = golsEquipeCasa;
	}
	
	public Integer getPlacarEquipeCasa() {
		return placarEquipeCasa;
	}
	
	public void setPlacarEquipeCasa(Integer placarEquipeCasa) {
		this.placarEquipeCasa = placarEquipeCasa;
	}
	
	public String getUrlLogoEquipeCasa() {
		return urlLogoEquipeCasa;
	}
	
	public void setUrlLogoEquipeCasa(String urlLogoEquipeCasa) {
		this.urlLogoEquipeCasa = urlLogoEquipeCasa;
	}
	
	public String getNomeEquipeCasa() {
		return nomeEquipeCasa;
	}
	
	public void setNomeEquipeCasa(String nomeEquipeCasa) {
		this.nomeEquipeCasa = nomeEquipeCasa;
	}
	
	public String getTempoPartida() {
		return tempoPartida;
	}
	
	public void setTempoPartida(String tempoPartida) {
		this.tempoPartida = tempoPartida;
	}
	
	public void setStatusPartida(String statusPartida) {
		this.statusPartida = statusPartida;
	}


}
