package com.me.biometria;

public class Cor {
	public int red, green, blue;
	
	//recebe um rgba de 32 bits
	Cor(int rgba){
		this.red = rgba & (255 << 24); //"e" binário e bit shift pegando apenas os bits do canal red da imagem
		this.green = rgba & (255 << 16);
		this.blue = rgba & (255 << 8);
	}
	
	public int getVariacaodeTom(){
		return (this.red + this.green + this.blue)/3;
	}
	
}
