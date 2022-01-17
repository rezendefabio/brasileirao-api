package br.com.fra.brasileiraoapi.util;

import br.com.fra.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final String BASE_URL_GOOGLE = "https://www.google.comr/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		partida.getGolsEquipeCasa();

	}

}
