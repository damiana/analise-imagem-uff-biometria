package com.me.biometria;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Biometria implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Sprite sprite, sprite2, spritecopia, sprite2copia, attsprite, reginteresse;
	private float h, w;
	private static BitmapFont bf;
	private byte imgcount = 1;
	private Sprite preto, branco;
	private Pixmap imgbinaria;
	private ShapeRenderer shaperenderer;
	
	protected void carregarImagens(String imagename){
		
		//carregando imagem de entrada
		Pixmap pix = new Pixmap(Gdx.files.internal(imagename));
		
		if (sprite != null){
			sprite.getTexture().dispose();
			sprite2.getTexture().dispose();
			spritecopia.getTexture().dispose();
			sprite2copia.getTexture().dispose();
			sprite = null;
			sprite2 = null;
			spritecopia = null;
			sprite2copia = null;
		}
		
		Format format = Format.RGBA8888; //converter de grayscale para rgba8888 para desenhar na tela
		if (pix.getFormat()!=format) { //converter se necessário
			Pixmap tmp = new Pixmap(pix.getWidth(), pix.getHeight(), format);
			tmp.drawPixmap(pix, 0, 0); //copiar pix to tmp
			pix.dispose();
			pix = tmp; //trocar valores
		}
		
		sprite = new Sprite(new Texture(pix));
		pix.dispose();
		pix = Processamento.getBinario(imagename, true);
		imgbinaria = pix;
		sprite2 = new Sprite(new Texture(pix));
		
		spritecopia = new Sprite(sprite);
		sprite2copia = new Sprite(sprite2);
		
		sprite.setPosition(w/2 - sprite.getWidth()/2, h/2 - sprite.getHeight()/2 + 150);
		sprite2.setPosition(w/2 - sprite.getWidth()/2, h/2 - sprite.getHeight()/2 + 150);
		spritecopia.setPosition(5, h/2 - spritecopia.getHeight()/2 - 140);
		sprite2copia.setPosition(w - sprite2copia.getWidth() - 5, h/2 - sprite2copia.getHeight()/2 - 140);
		
		short alturamax = 250;
		Pixmap tmp = new Pixmap(30, Processamento.quantosPixelsPretos(pix)*alturamax/(pix.getWidth()*pix.getHeight()), format);
		tmp.setColor(Color.BLACK);
		tmp.fill();
		preto = new Sprite(new Texture(tmp));
		preto.setPosition(660, 320);
		tmp.dispose();
		
		tmp = new Pixmap(30, Processamento.quantosPixelsBrancos(pix)*alturamax/(pix.getWidth()*pix.getHeight()) + 10, format);
		tmp.setColor(Color.WHITE);
		tmp.fill();
		branco = new Sprite(new Texture(tmp));
		branco.setPosition(700, 320);
		tmp.dispose();
		
		//pix.dispose();
		pix = null;
	}
	
	@Override
	public void create() {	
		shaperenderer = new ShapeRenderer();
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		bf = new BitmapFont(Gdx.files.internal("main.fnt"), Gdx.files.internal("main.png"), false);
		
		camera = new OrthographicCamera(w, h);
		camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();
		
		carregarImagens("1.jpg");
		
		attsprite = new Sprite(new Texture(Gdx.files.internal("att.png")));
		attsprite.setPosition(5, h - 70);
		
		reginteresse = new Sprite(new Texture(Gdx.files.internal("regint.png")));
		reginteresse.setPosition(5, h - 120);
		
		//input processor para tratar o clique do botão
		Gdx.input.setInputProcessor(
				new InputProcessor(){

					@Override
					public boolean keyDown(int keycode) {
						return false;
					}

					@Override
					public boolean keyUp(int keycode) {
						return false;
					}

					@Override
					public boolean keyTyped(char character) {
						return false;
					}

					@Override
					public boolean touchDown(int screenX, int screenY,
							int pointer, int button) {
						if ((screenX < 70) && (screenY < 60)){
							if (imgcount == 1) return false;
							imgcount--;
							if (imgcount == 13) imgcount = 12;
							carregarImagens(imgcount + ".jpg");
							return true;
						}
						else if ((screenX < 128) && (screenX > 70) && (screenY < 60)){
							if (imgcount == 30) return false;
							imgcount++;
							if (imgcount == 13) imgcount = 14;
							carregarImagens(imgcount + ".jpg");
							return true;
						}
						
						else if ((screenX < 128) && (screenY < 140)){
							sprite2copia.setTexture(new Texture(Processamento.getAreaInteresse(imgbinaria)));
							return true;
						}
						
						return false;
					}

					@Override
					public boolean touchUp(int screenX, int screenY,
							int pointer, int button) {
						return false;
					}

					@Override
					public boolean touchDragged(int screenX, int screenY,
							int pointer) {
						return false;
					}

					@Override
					public boolean mouseMoved(int screenX, int screenY) {
						return false;
					}

					@Override
					public boolean scrolled(int amount) {
						return false;
					}
					
				}
				
				);
		
		
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		
		
		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		sprite2.draw(batch, 0.4f);
		spritecopia.draw(batch);
		sprite2copia.draw(batch);
		attsprite.draw(batch);
		reginteresse.draw(batch);
		
		preto.draw(batch);
		branco.draw(batch);
		
		bf.draw(batch, imgcount + ".jpg", w - 10 - bf.getBounds(imgcount + "jpg").width, h - 10);
		
		batch.end();
		
		
		//desenhar retângulos vermelhos, área de interesse
		shaperenderer.begin(ShapeType.Rectangle);
		shaperenderer.setColor(Color.RED);
		if (Processamento.comeco != null){
			shaperenderer.rect(sprite2copia.getX() + Processamento.comeco.x, 
					sprite2copia.getHeight() + sprite2copia.getY() - Processamento.comeco.y - Processamento.janelay, 
					Processamento.janelax, Processamento.janelay);
			shaperenderer.rect(spritecopia.getX() + Processamento.comeco.x, 
					spritecopia.getHeight() + spritecopia.getY() - Processamento.comeco.y - Processamento.janelay, 
					Processamento.janelax, Processamento.janelay);
			shaperenderer.rect(sprite.getX() + Processamento.comeco.x, 
					sprite.getHeight() + sprite.getY() - Processamento.comeco.y - Processamento.janelay, 
					Processamento.janelax, Processamento.janelay);
		}		
		shaperenderer.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
