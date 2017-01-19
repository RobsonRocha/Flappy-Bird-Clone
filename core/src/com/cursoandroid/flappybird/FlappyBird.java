package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] birds;
    private Texture screen;
    private Texture pipeBottom;
    private Texture pipeDown;
    private Texture gameOver;
    private Random random;
    private BitmapFont font;
    private BitmapFont message;
    private Circle birdCircle;
    private Rectangle rectanglePipeTop;
    private Rectangle rectanglePipeBottom;
    //private ShapeRenderer shape;
    private OrthographicCamera oc;
    private Viewport viewport;

    //Atributos de configuracao
    private float deviceWidth;
    private float deviceHeight;
    private int state = 0;// 0-> jogo não iniciado 1-> jogo iniciado 2-> Tela Game Over
    private int score = 0;

    private float variation = 0;
    private float speedToFall = 0;
    private float verticalInitPosition;
    private float positionMovementPipeHorizontal;
    private float heightBetweenPipes;
    private float deltaTime;
    private float randomHeightBetweenPipes;
    private boolean didScore = false;
    private final static float VIRTUAL_WIDTH = 768;
    private final static float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

        batch = new SpriteBatch();
        random = new Random();
        birdCircle = new Circle();

        /*rectanglePipeTop = new Rectangle();
        rectanglePipeBottom = new Rectangle();
        shape = new ShapeRenderer();*/
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        message = new BitmapFont();
        message.setColor(Color.WHITE);
        message.getData().setScale(3);

        birds = new Texture[3];
        birds[0] = new Texture("passaro1.png");
        birds[1] = new Texture("passaro2.png");
        birds[2] = new Texture("passaro3.png");

        screen = new Texture("fundo.png");
        pipeBottom = new Texture("cano_baixo.png");
        pipeDown = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        oc = new OrthographicCamera();
        oc.position.set(VIRTUAL_WIDTH /2, VIRTUAL_HEIGHT / 2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, oc) ;

        deviceWidth = VIRTUAL_WIDTH;//Gdx.graphics.getWidth();
        deviceHeight = VIRTUAL_HEIGHT;//Gdx.graphics.getHeight();
        verticalInitPosition = deviceHeight / 2;
        positionMovementPipeHorizontal = deviceWidth;
        heightBetweenPipes = 300;

    }

	@Override
	public void render () {

        oc.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variation += deltaTime * 10;
        if (variation > 2) variation = 0;

        if( state == 0 ){//Não iniciado

            if( Gdx.input.justTouched() ){
                state = 1;
            }

        }else {//Iniciado

            speedToFall++;
            if (verticalInitPosition > 0 || speedToFall < 0)
                verticalInitPosition = verticalInitPosition - speedToFall;

            if( state == 1 ){

                positionMovementPipeHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    speedToFall = -15;
                }

                //Verifica se o cano saiu inteiramente da tela
                if (positionMovementPipeHorizontal < -pipeDown.getWidth()) {
                    positionMovementPipeHorizontal = deviceWidth;
                    randomHeightBetweenPipes = random.nextInt(400) - 200;
                    didScore = false;
                }

                //Verifica pontuação
                if(positionMovementPipeHorizontal < 120 ){
                    if( !didScore){
                        score++;
                        didScore = true;
                    }
                }

            }else{//Tela game over -> 2

                if( Gdx.input.justTouched() ){

                    state = 0;
                    score = 0;
                    speedToFall = 0;
                    verticalInitPosition = deviceHeight / 2;
                    positionMovementPipeHorizontal = deviceWidth;

                }

            }

        }

        batch.setProjectionMatrix(oc.combined);

        batch.begin();

        batch.draw(screen, 0, 0, deviceWidth, deviceHeight);
        batch.draw(pipeDown, positionMovementPipeHorizontal, deviceHeight / 2 + heightBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(pipeBottom, positionMovementPipeHorizontal, deviceHeight / 2 - pipeBottom.getHeight() - heightBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(birds[(int) variation], 120, verticalInitPosition);
        font.draw(batch, String.valueOf(score), deviceWidth / 2, deviceHeight - 50);

        if( state == 2 ){
            batch.draw(gameOver, deviceWidth / 2 - gameOver.getWidth() / 2 , deviceHeight / 2 );
            message.draw(batch, "Toque para Reiniciar!", deviceWidth / 2 - 200, deviceHeight / 2 - gameOver.getHeight() / 2 );
        }

        batch.end();

        birdCircle.set(120 + birds[0].getWidth() / 2, verticalInitPosition + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
        rectanglePipeBottom = new Rectangle(
                positionMovementPipeHorizontal, deviceHeight / 2 - pipeBottom.getHeight() - heightBetweenPipes / 2 + randomHeightBetweenPipes,
                pipeBottom.getWidth(), pipeBottom.getHeight()
        );

        rectanglePipeTop = new Rectangle(
                positionMovementPipeHorizontal, deviceHeight / 2 + heightBetweenPipes / 2 + randomHeightBetweenPipes,
                pipeDown.getWidth(), pipeDown.getHeight()
        );

        //Desenhar formas
        /*shape.begin( ShapeRenderer.ShapeType.Filled);
        shape.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shape.rect(rectanglePipeBottom.x, rectanglePipeBottom.y, rectanglePipeBottom.width, rectanglePipeBottom.height);
        shape.rect(rectanglePipeTop.x, rectanglePipeTop.y, rectanglePipeTop.width, rectanglePipeTop.height);
        shape.setColor(Color.RED);
        shape.end();*/

        //Teste de colisão
        if( Intersector.overlaps(birdCircle, rectanglePipeBottom) || Intersector.overlaps(birdCircle, rectanglePipeTop)
                || verticalInitPosition <= 0 || verticalInitPosition >= deviceHeight){
            state = 2;
        }

	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
