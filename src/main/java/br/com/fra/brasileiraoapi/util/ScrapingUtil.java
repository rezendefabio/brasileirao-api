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

	private static final String DIV_PARTIDA_ANDAMENTO = "div[class=imso_mh__lv-m-stts-cont]";
	private static final String DIV_PARTIDA_ENCERRADA = "span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]";

	private static final String NOME_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String NOME_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";

	private static final String LOGO_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String LOGO_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String IMG_ITEM_LOGO = "img[class=imso_btl__mh-logo]";

	private static final String PLACAR_EQUIPE_CASA = "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
	private static final String PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]";

	private static final String GOLS_EQUIPE_CASA = "div[class=imso_gs__tgs imso_gs__left-team]";
	private static final String GOLS_EQUIPE_VISITANTE = "div[class=imso_gs__tgs imso_gs__right-team]";

	private static final String DIV_ITEM_GOL = "div[class=imso_gs__gs-r]";
	private static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]"; 
	
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
	private static final String PENALTIES = "Pênaltis";

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

				Integer placarEquipeCasa = recuperaPlacarEquipe(document, PLACAR_EQUIPE_CASA);
				LOGGER.info("Placar Equipe Casa: {}", placarEquipeCasa);

				Integer placarEquipeVisitante = recuperaPlacarEquipe(document, PLACAR_EQUIPE_VISITANTE);
				LOGGER.info("Placar Equipe Visitante: {}", placarEquipeVisitante);

				String golsEquipeCasa = recuperaGolsEquipe(document, GOLS_EQUIPE_CASA);
				LOGGER.info("Gols Equipe Casa: {}", golsEquipeCasa);

				String golsEquipeVisitante = recuperaGolsEquipe(document, GOLS_EQUIPE_VISITANTE);
				LOGGER.info("Gols Equipe Visitante: {}", golsEquipeVisitante);

				Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
				Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
				LOGGER.info("Placar Extendido equipe casa: {}", placarEstendidoEquipeCasa);
				LOGGER.info("Placar Extendido equipe visitante: {}", placarEstendidoEquipeVisitante);
			}
			
			String nomeEquipeCasa = obtemNomeEquipe(document, NOME_EQUIPE_CASA);
			String urlLogoEquipeCasa = obtemLogoEquipe(document, LOGO_EQUIPE_CASA);
			LOGGER.info("Equipe da Casa: {}", nomeEquipeCasa);
			LOGGER.info("URL Logo Equipe Casa: {}", urlLogoEquipeCasa);


			String nomeEquipeVisitante = obtemNomeEquipe(document, NOME_EQUIPE_VISITANTE);
			String urlLogoEquipeVisitante = obtemLogoEquipe(document, LOGO_EQUIPE_VISITANTE);
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
		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		
		if(!isTempoPartida) {
			String tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains(PENALTIES)) {
				statusPartida = StatusPartida.PARTIDA_PENALTIES;
			}
		}
		
		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
		
		if(!isTempoPartida) { 
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		return statusPartida; 
	}
	
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		
		//Jogo rolando, ou intervalo ou penalidades
		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		
		if (!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
		}
		
		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();		
		
		if (!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ENCERRADA).first().text();
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
	
	public String obtemNomeEquipe(Document document, String itemHtml) {
		String nomeEquipe;
		
		Element elemento = document.selectFirst(itemHtml);
		nomeEquipe = elemento.select("span").text();
		
		return nomeEquipe;
	}
	
	public String obtemLogoEquipe(Document document, String itemHtml) {
		Element elemento = document.selectFirst(itemHtml);
		String urlLogo = "https:" + elemento.select(IMG_ITEM_LOGO).attr("src");
		
		return urlLogo;
		
	}
	
	public Integer recuperaPlacarEquipe(Document document, String itemHtml) {
		String placarEquipe = document.selectFirst(itemHtml).text();
		return formataPlacarStringInteger(placarEquipe);
	}

	public String recuperaGolsEquipe(Document document, String itemHtml) {
		List<String> golsEquipe = new ArrayList<>();

		Elements elementos = document.select(itemHtml).select(DIV_ITEM_GOL);
		
			for (Element e : elementos) {
				String infoGol = e.select(DIV_ITEM_GOL).text();
				golsEquipe.add(infoGol);
			}
		
		return String.join(", ", golsEquipe);

	}

	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select(DIV_PENALIDADES).isEmpty();
		if (!isPenalidades) {
			String penalidades = document.select(DIV_PENALIDADES).text();
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
