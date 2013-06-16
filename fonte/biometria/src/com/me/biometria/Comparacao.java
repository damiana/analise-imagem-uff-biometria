package com.me.biometria;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Pixmap;

public class Comparacao implements ApplicationListener{
	private static byte qntdimagens = 5;
	private static Pixmap[] pixmap = new Pixmap[qntdimagens];
	private static int[] acerto = new int[qntdimagens];
	private static final String diretorioimagens = "C:\\Users\\Érick\\Documents\\ibiometria\\";
	
	
	public void main (){
		
		//imagem no banco de dados
		pixmap[0] = Processamento.getBinario(diretorioimagens + "0.jpg", false);
		
		//imagens a serem comparadas
		for (int i=1; i<pixmap.length; i++){
			System.out.println("Avaliando a imagem " + i + ".jpg ->");
			pixmap[i] = Processamento.getAreaInteresseCortadaBinaria(diretorioimagens + i + ".jpg", false);
		}
		
		//separar o menor acerto entre as comparações com as imagens acima
		int menoracerto = 999999, indice = 0;
		for (int i=1; i<pixmap.length; i++){
			System.out.println("Comparando com a imagem " + i + ".jpg ...");
			acerto[i] = comparar(pixmap[0], pixmap[i]);
			if (acerto[i] < menoracerto) {
				menoracerto = acerto[i];
				indice = i;
			}
		}
		
		
		System.out.println("0.jpg <-> " + indice + ".jpg, Taxa de erro: " + menoracerto);
	}
	
	
	
	
	public int comparar(Pixmap px1, Pixmap px2){
		final byte tamanhojanela = 3;
		boolean incrementar;
		
		int taxaerro = 0; 
		
		
		int menorsubtaxa = 99999999;
		
		
		for (int i=50; i<px1.getHeight() - px2.getHeight(); i++){//percorrer imagem original a ser comparada
			for (int j=40; j<px1.getWidth() - px2.getWidth() - 40; j++){
				taxaerro = 0;
				
				for (int i2=0; i2<px2.getHeight(); i2 ++){//andar com a ROI em cima da imagem original e verificar compatibilidade
					for (int j2=0; j2<px2.getWidth(); j2++){
						
						
						//taxa de erro absoluta entre cada pixel
						if (px1.getPixel(j + j2, i + i2) != px2.getPixel(j2, i2)){
							taxaerro ++;
							
							
							//taxa de erro por janela
							incrementar = true;
							for (int h=(-1*tamanhojanela); h<tamanhojanela; h++){
								for (int f=(-1*tamanhojanela); f<tamanhojanela; f++){
									if ((px1.getPixel(j + j2, i+ i2)!= -1) && (px2.getPixel(j2 + h, i2 + f) != -1)) 
										incrementar = false;
								}
							}
							if (incrementar) taxaerro +=2;

							
						}
						
						
					}
				}
				
				//retornar a menor taxa de erro dentre todas as áreas avaliadas pela comparação da ROI com a imagem original
				if (menorsubtaxa > taxaerro) {
					menorsubtaxa = taxaerro;
				}
				
			}
			System.out.println("Porcentagem da comparação: " + i*100/(px1.getHeight() - px2.getHeight()) + "%");
		}
	
		
		
		return menorsubtaxa;
	}


	
	
	
	
	
	
	
	
	@Override
	public void create() {
		main();
	}


	@Override
	public void resize(int width, int height) {
		
	}


	@Override
	public void render() {
		
	}


	@Override
	public void pause() {
		
	}


	@Override
	public void resume() {
		
	}


	@Override
	public void dispose() {
		
	}
	
	
	
	
	
}
