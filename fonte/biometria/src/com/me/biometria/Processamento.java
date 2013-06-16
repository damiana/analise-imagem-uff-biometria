package com.me.biometria;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class Processamento {
	//classe que encapsula todos os processamentos modularizados
	
	
	protected static Pixmap getBinario(String texturepath, boolean imageminterna){
		//imagem de recebimento
		Pixmap px;
		if (imageminterna) px = new Pixmap(Gdx.files.internal(texturepath));
		else px = new Pixmap(Gdx.files.absolute(texturepath));
		//imagem binária a ser gerada e retornada
		Pixmap pxretorno = new Pixmap(px.getWidth(), px.getHeight(), Format.RGBA8888);
		//pintando a imagem de retorno de branco
		pxretorno.setColor(Color.WHITE);
		pxretorno.fill();
		

		//pré-processamento colocando as cores altas e baixas totalmente brancas para separar os contornos
		short limiteinferior = 20, limitesuperior = 210; //variando de 0 à 255
		for (int i=0; i<px.getHeight(); i++){
			for (int j=0; j<px.getWidth(); j++){
				if (Math.abs(px.getPixel(j, i)) < limiteinferior || Math.abs(px.getPixel(j, i)) > limitesuperior){
					px.drawPixel(j, i, 0xFFFFFFFF);
				}
			}
		}
		
		//valor ímpar        \/
		byte tamanhojanela = 9; //tamanho da janela de análise na horizontal, as linhas ficam mais grossas ao aumentar
		byte vtom = 3; //sensibilidade da mudança de tom, quanto maior, a imagemc precisa variar mais tom para que haja uma representação binária na janela que percorre a imagem original
		
		
		//excluir pixels
		byte excluiry = 20, excluirx = 10;
		//criar a imagem binária percorrendo a imagem original
		for (int i=excluiry; i<px.getHeight() - excluiry; i++){
			for (int j=excluirx; j<px.getWidth() - tamanhojanela - excluirx; j++){
				
				int pxmeio = px.getPixel(j + (int)(tamanhojanela/2), i);
				int variacao1 = px.getPixel(j, i) - pxmeio;
				int variacao2 = px.getPixel(j + tamanhojanela - 1, i) - pxmeio;
				if ((Math.abs(variacao1) > vtom) &&
						((Math.abs(variacao2)) > vtom) &&
						(!isPositivo(variacao1) && !isPositivo(variacao2))){ //aqui pode ser == também
					pxretorno.drawPixel(j + (int)(tamanhojanela/2), i, 0x000000FF);
				}
			}
		}
		vtom = 4;
		tamanhojanela = 9;
		//aplicar a janela na vertical também, apenas repetindo o código acima
		for (int i=excluiry; i<px.getHeight() - excluiry - tamanhojanela; i++){
			for (int j=excluirx; j<px.getWidth() - excluirx; j++){
				
				int pxmeio = px.getPixel(j, i + (int)(tamanhojanela/2));
				int variacao1 = px.getPixel(j, i) - pxmeio;
				int variacao2 = px.getPixel(j, i + tamanhojanela - 1) - pxmeio;
				if ((Math.abs(variacao1) > vtom) &&
						((Math.abs(variacao2)) > vtom) &&
						(isPositivo(variacao1) && isPositivo(variacao2))){ //aqui pode ser == também
					pxretorno.drawPixel(j , i + (int)(tamanhojanela/2), 0x000000FF);
				}
			}
		}
		
		
		//limpar alguns ruídos
		byte c = 4, caux = c; //tamanho da janela para limpeza dos ruídos
		short mincontagem = 7; //ter pelo menos 'mincontagem' pixels pretos na janela (c*2)*(c*2)
		while (caux >= 1){
			if (caux == 1) {c = 3; mincontagem = 7;}else c = caux;
			byte contar = 0;
			for (int i=excluiry; i<pxretorno.getHeight(); i++){
				for (int j=excluirx; j<pxretorno.getWidth(); j++){
					//for das janelas
					for (int h=c*-1; h<c; h++){
						for (int f=c*-1; f<c; f++){
							if (Math.abs(pxretorno.getPixel(j + f, i + h)) > 200){
								contar ++;
							}
						}
					}
					if (contar < mincontagem) pxretorno.drawPixel(j, i, 0xFFFFFFFF);
					contar = 0;
				}
			}
			caux--;
			mincontagem --;
		}
		
		px.dispose();
		return pxretorno;
	}
	
	
	private static boolean isPositivo(int n){
		if (n > 0) return true;
		return false;
	}
	
	protected static int quantosPixelsPretos(Pixmap px){
		int cont = 0;
		for (int i = 0; i < px.getHeight(); i++){
			for (int j = 0; j < px.getWidth(); j++){
				if (px.getPixel(i, j) != -1) cont ++;
			}
		}
		
		return cont;
	}
	protected static int quantosPixelsBrancos(Pixmap px){
		int cont = 0;
		for (int i = 0; i < px.getHeight(); i++){
			for (int j = 0; j < px.getWidth(); j++){
				if (px.getPixel(i, j) == -1) cont ++;
			}
		}
		
		return cont;
	}
	
	protected static Pixmap clonarPixmap(Pixmap px){
		Pixmap px2 = new Pixmap(px.getWidth(), px.getHeight(), px.getFormat());
		for (int j=0; j<px.getWidth(); j++){
			for(int i=0; i<px.getHeight(); i++){
				px2.drawPixel(j, i, px.getPixel(j, i));
			}
		}
		return px2;
	}
	
	//variaveis para desenhar as linhas da região de interesse
	protected static Vector2 comeco;
	
	protected static int janelax = 265, janelay = 165;
	protected static Pixmap getAreaInteresse(Pixmap pxr){
		ArrayList<Vector3> custos = new ArrayList<Vector3>();
		Pixmap px = clonarPixmap(pxr);
		
		for (int j=0; j<px.getWidth() - janelax; j++){
			for (int i=0; i<px.getHeight() - janelay; i++){
				//for da janela, pixel por pixel
				int contagem = 0; //quantos pixels pretos
				for (int j2=j; j2 < j+janelax; j2++){
					for (int i2=i; i2 < i+janelay; i2++){
						if (px.getPixel(j2, i2) != -1) contagem++;
					}
				}
				custos.add(new Vector3(j, i, contagem));
			}
			System.out.println("Processando: " + (int)(j*100/(px.getWidth()-janelax)) + "%");
		}
		
		//pegar a janela ótima
		Vector3 otima = custos.get(0);
		for(int i=0; i<custos.size(); i++){
			if (otima.z < custos.get(i).z) otima = custos.get(i);
		}
		
		//deletar se quiser, passar pra uma variável externa os pontos de começo da área de interesse
		comeco = new Vector2(otima.x, otima.y);
		//deletar se quiser \
		
		//deletar qualquer área diferente da de interesse
		for(int j=0; j<px.getWidth();j++){
			for(int i=0; i<px.getHeight(); i++){
				if (!((j > otima.x) && (j < (otima.x + janelax))
						&& (i > otima.y) && (i < (otima.y + janelay)))){
					px.drawPixel(j, i, 0xFFFFFFFF);
				}
			}
		}
		
		return px;
	}
	
	public static Pixmap getAreaInteresseCortada(Pixmap px){
		Pixmap processado = getAreaInteresse(px);
		Pixmap saida = new Pixmap(janelax, janelay, Format.RGBA8888);
		
		int primeirox = 9999, primeiroy = 9999;
		for (int i=0; i<processado.getHeight(); i++){
			for (int j=0; j<processado.getWidth(); j++){
				if (processado.getPixel(j, i) != -1){
					if (primeirox > j) primeirox = j;
					if (primeiroy > i) primeiroy = i;
				}
			}
		}
		
		for (int i=0; i<saida.getHeight(); i++){
			for (int j=0; j<saida.getWidth(); j++){
				if (processado.getPixel(primeirox + j, primeiroy + i) != -1)
					saida.drawPixel(j, i, 0x00000000);
				else saida.drawPixel(j, i, 0xFFFFFFFF);
			}
		}
		
		return saida;
	}

	
	public static Pixmap getAreaInteresseCortadaBinaria(String texturepath, boolean imageminterna){
		return getAreaInteresseCortada(getBinario(texturepath, imageminterna));		
	}
}



