package com.me.biometria;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = "Biometria";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;
		
		new LwjglApplication(new Biometria(), cfg);
		
		/*
		cfg.title = "Comparação";
		cfg.useGL20 = true;
		cfg.width = 40;
		cfg.height = 10;
		
		
		new LwjglApplication(new Comparacao(), cfg);*/
		
	}
}
