package com.naukma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture fish;

    private float fishX, fishY;
    private float fishWidth, fishHeight;
    private float speed = 200; // пікселів за секунду

    private BitmapFont font;
    private int score = 0;
    private int lives = 3;

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(Gdx.graphics.getDisplayMode().refreshRate);
        Gdx.graphics.setVSync(true);
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.jpg"));
        fish = new Texture(Gdx.files.internal("sprite_0.png"));

        fishWidth = fish.getWidth()*0.3f;
        fishHeight = fish.getHeight()*0.3f;

        // Початкова позиція по центру
        fishX = (Gdx.graphics.getWidth() - fishWidth) / 2f;
        fishY = (Gdx.graphics.getHeight() - fishHeight) / 2f;

        font = new BitmapFont(); // системний шрифт
        font.getData().setScale(2); // збільшення розміру
        font.setColor(Color.WHITE);
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(fish, fishX, fishY);

        drawHUD();

        batch.end();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            fishY += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            fishY -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            fishX -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            fishX += speed * delta;
        }

        // Обмеження межами екрану
        if (fishX < 0) fishX = 0;
        if (fishY < 0) fishY = 0;
        if (fishX > Gdx.graphics.getWidth() - fishWidth) fishX = Gdx.graphics.getWidth() - fishWidth;
        if (fishY > Gdx.graphics.getHeight() - fishHeight) fishY = Gdx.graphics.getHeight() - fishHeight;
    }

    private void drawHUD() {
        String scoreText = "Score: " + score;
        String livesText = "Lives: " + lives;

        font.draw(batch, scoreText, 20, Gdx.graphics.getHeight() - 20); // зверху зліва
        font.draw(batch, livesText, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20); // зверху справа
    }


    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        fish.dispose();
        font.dispose();
    }
}
