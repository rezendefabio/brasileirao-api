package br.com.fra.brasileiraoapi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.fra.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";

	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
	
	public static void main(String[] args) {
		String partidaPesquisa = "corinthiansxpalmeiras 08/08/2020";
		String url = BASE_URL_GOOGLE + partidaPesquisa + COMPLEMENTO_URL_GOOGLE;
		
		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obtemInformacoesPartida(url);
	}
	
	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		
		Document document = null;
		
		try {
			document = Jsoup.connect(url).get();
			
			String title = document.title();
			LOGGER.info("Titulo da Pagina {}", title.toUpperCase());
			
			StatusPartida statusPartida = obtemStatusPartida(document);
			if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);	
				LOGGER.info("Tempo da Partida: {}", tempoPartida);

				Integer placarEquipeCasa = recuperaPlacarEquipeCasa(document);
				LOGGER.info("Placar Equipe Casa: {}", placarEquipeCasa);

				Integer placarEquipeVisitante = recuperaPlacarEquipeVisitante(document);
				LOGGER.info("Placar Equipe Visitante: {}", placarEquipeVisitante);

				String golsEquipeCasa = recuperaGolsEquipeCasa(document);
				LOGGER.info("Gols Equipe Casa: {}", golsEquipeCasa);

				String golsEquipeVisitante = recuperaGolsEquipeVisitante(document);
				LOGGER.info("Gols Equipe Visitante: {}", golsEquipeVisitante);

				Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
				Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
				LOGGER.info("Placar Extendido equipe casa: {}", placarEstendidoEquipeCasa);
				LOGGER.info("Placar Extendido equipe visitante: {}", placarEstendidoEquipeVisitante);
			}
			
			String nomeEquipeCasa = obtemNomeEquipeCasa(document);
			String urlLogoEquipeCasa = obtemLogoEquipeCasa(document);
			LOGGER.info("Equipe da Casa: {}", nomeEquipeCasa);
			LOGGER.info("URL Logo Equipe Casa: {}", urlLogoEquipeCasa);


			String nomeEquipeVisitante = obtemNomeEquipeVisitante(document);
			String urlLogoEquipeVisitante = obtemLogoEquipeVisitante(document);
			LOGGER.info("Equipe Visitante: {}", nomeEquipeVisitante);
			LOGGER.info("URL Logo Equipe Visitante: {}", urlLogoEquipeVisitante);
			
			LOGGER.info("Status da Partida: {}", statusPartida.toString());
			
			
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE -> {}", e.getMessage());
			e.printStackTrace();
		}
		
		return partida;
		
		
	}
	
	public StatusPartida obtemStatusPartida(Document document) {
		//situações
		//1 - partida nao iniciada
		//2 - partida iniciada/jogo rolando/intervalo
		//3 - partida encerrada
		//4 - penalidades
		
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		
		if(!isTempoPartida) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIES;
			}
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		
		if(!isTempoPartida) { 
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		return statusPartida; 
	}
	
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		
		//Jogo rolando, ou intervalo ou penalidades
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		
		if (!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();		
		
		if (!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}		
		
		return corrigeTempoPartida(tempoPartida);
	}
	
	public String corrigeTempoPartida(String tempo) {
		String tempoPartida;
		if(tempo == null) {
			tempoPartida = "Partida não Iniciada";
		} else {
			tempoPartida = tempo.replace("'", " min");	
		}
		return tempoPartida;
	}
	
	public String obtemNomeEquipeCasa(Document document) {
		String nomeEquipeCasa;
		
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		nomeEquipeCasa = elemento.select("span").text();
		
		return nomeEquipeCasa;
	}

	public String obtemNomeEquipeVisitante(Document document) {
		String nomeEquipeVisitante;
		
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		nomeEquipeVisitante = elemento.select("span").text();
		
		return nomeEquipeVisitante;
	}
	
	public String obtemLogoEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
		
	}
	
	public String obtemLogoEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
		
	}

	public Integer recuperaPlacarEquipeCasa(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarStringInteger(placarEquipe);
	}
	
	public Integer recuperaPlacarEquipeVisitante(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarStringInteger(placarEquipe);
	}

	public String recuperaGolsEquipeCasa(Document document) {
		List<String> golsEquipe = new ArrayList<>();

		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__left-team]")
			.select("div[class=imso_gs__gs-r]");
		
			for (Element e : elementos) {
				String infoGol = e.select("div[class=imso_gs__gs-r]").text();
				golsEquipe.add(infoGol);
			}
		
		return String.join(", ", golsEquipe);

	}

	public String recuperaGolsEquipeVisitante(Document document) {
		List<String> golsEquipe = new ArrayList<>();

		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__right-team]")
			.select("div[class=imso_gs__gs-r]");
		
			
			elementos.forEach(item -> {
				String infoGol = item.select("div[class=imso_gs__gs-r]").text();
				golsEquipe.add(infoGol);
			});
		
		return String.join(", ", golsEquipe);

	}

	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select("div[class=imso_mh_s__psn-sc]").isEmpty();
		if (!isPenalidades) {
			String penalidades = document.select("div[class=imso_mh_s__psn-sc]").text();
			String penalidadesCompleta = penalidades.substring(0, 5).replace(" ", "");
			String[] divisao = penalidadesCompleta.split("-");
			return tipoEquipe.equals(CASA) ? formataPlacarStringInteger(divisao[0]) : formataPlacarStringInteger(divisao[1]);
		}

		return null;
	}

	public Integer formataPlacarStringInteger(String placar) {
		Integer valor;
		try {
			valor = Integer.parseInt(placar);
		} catch(Exception e) {
			valor = 0;
		}

		return valor;
	}
}
