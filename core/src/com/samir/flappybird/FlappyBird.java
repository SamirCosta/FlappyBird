package com.samir.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameover;
	private Random numRandom;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle canoTopoRET;
	private Rectangle canoBaixoRET;
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDHT = 600;
	private final float VIRTUAL_HEIGHT = 1024;
	//private ShapeRenderer shapeRenderer;

	private float largCel;
	private float altCel;
	private int estado = 0;
	private int pontucao = 0;
	private boolean ponto;

	private float varia = 0;
	private float velQueda = 0;
	private float posIniVert;
	private float posCanoHori;
	private float espacoCanos;
	private float deltaTime;
	private float altCanosRand;

	@Override
	public void create () {
	batch = new SpriteBatch();
	numRandom = new Random();
	passaroCirculo = new Circle();
	/*canoBaixoRET = new Rectangle();
	canoTopoRET = new Rectangle();
	shapeRenderer = new ShapeRenderer();*/
	fonte = new BitmapFont();
	fonte.setColor(Color.WHITE);
	fonte.getData().setScale(6);

	mensagem = new BitmapFont();
	mensagem.setColor(Color.WHITE);
	mensagem.getData().setScale(3);

	passaro = new Texture[3];
	passaro[0] = new Texture("passaro1.png");
	passaro[1] = new Texture("passaro2.png");
	passaro[2] = new Texture("passaro3.png");

	fundo = new Texture("fundo.png");
	canoBaixo = new Texture("cano_baixo.png");
	canoTopo = new Texture("cano_topo.png");
	gameover = new Texture("game_over.png");

	camera = new OrthographicCamera();
	camera.position.set(VIRTUAL_WIDHT / 2,VIRTUAL_HEIGHT / 2,0);
	viewport = new StretchViewport(VIRTUAL_WIDHT, VIRTUAL_HEIGHT, camera);

	largCel = VIRTUAL_WIDHT;
	altCel = VIRTUAL_HEIGHT;
	posIniVert = altCel / 2;
	posCanoHori = largCel;
	espacoCanos = 300;

	}

	@Override
	public void render () {

		camera.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

	deltaTime =  Gdx.graphics.getDeltaTime() ;
	varia += deltaTime * 10;

		if (varia > 3) varia = 0;

	if (estado == 0){
		if (Gdx.input.justTouched()){
			estado = 1;
		}
	}else {

		velQueda ++;
		if (posIniVert > 0 || velQueda < 0)
			posIniVert -= velQueda;

		if (estado == 1){

		posCanoHori -= deltaTime * 200;

		if (Gdx.input.justTouched()) {
			velQueda = -15;
		}



		if (posCanoHori < -canoBaixo.getWidth()) {
			posCanoHori = largCel;
			altCanosRand = numRandom.nextInt(400) - 200;
			ponto = false;
		}

		if (posCanoHori < 120) {
			if (!ponto) {
				pontucao++;
				ponto = true;
			}
		}

		}else{
			if (Gdx.input.justTouched()){
				estado = 0;
				pontucao = 0;
				velQueda = 0;
				posIniVert = altCel / 2;
				posCanoHori = largCel;
			}
		}
	}
	batch.setProjectionMatrix(camera.combined);

	batch.begin();

	batch.draw(fundo, 0, 0, largCel, altCel);
	batch.draw(canoTopo, posCanoHori, altCel / 2 + espacoCanos/2 + altCanosRand);
	batch.draw(canoBaixo, posCanoHori, altCel / 2 - canoBaixo.getHeight() - espacoCanos/2 + altCanosRand);
	batch.draw(passaro[(int)varia], 120, posIniVert);
	fonte.draw(batch, String.valueOf(pontucao), largCel/2, altCel - 50);
	if(estado ==2){
		batch.draw(gameover,largCel /2 - gameover.getWidth()/2 , altCel / 2);
		mensagem.draw(batch, "Toque para reiniciar", largCel/2 - 200, altCel/2 - gameover.getHeight());
	}

	batch.end();

	passaroCirculo.set(120 + passaro[0].getWidth() / 2,
			posIniVert + passaro[0].getHeight() /2,
			passaro[0].getWidth() / 2);

	canoBaixoRET = new Rectangle(
		posCanoHori,altCel / 2 - canoBaixo.getHeight() - espacoCanos/2 + altCanosRand,
			canoBaixo.getWidth(), canoBaixo.getHeight()
	);

	canoTopoRET = new Rectangle(
		posCanoHori,altCel / 2 + espacoCanos/2 + altCanosRand,
			canoTopo.getWidth(),canoTopo.getHeight()
	);

	/*
	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
	shapeRenderer.rect(canoBaixoRET.x,canoBaixoRET.y,canoBaixoRET.width,canoBaixoRET.height);
	shapeRenderer.setColor(Color.RED);
	shapeRenderer.end();*/

	if (Intersector.overlaps(passaroCirculo, canoBaixoRET) || Intersector.overlaps(passaroCirculo, canoTopoRET)
			|| posIniVert <= 0 || posIniVert >= altCel){
		estado = 2;
	}

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}
}
