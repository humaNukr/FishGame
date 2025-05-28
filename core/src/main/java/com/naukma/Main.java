package com.naukma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.jpg"));
        fish = new Texture(Gdx.files.internal("sprite_0.png"));

        fishWidth = fish.getWidth()*0.25f;
        fishHeight = fish.getHeight()*0.25f;

        // Початкова позиція по центру
        fishX = (Gdx.graphics.getWidth() - fishWidth) / 2f;
        fishY = (Gdx.graphics.getHeight() - fishHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(fish, fishX, fishY, fishWidth/2, fishHeight/2,
            fishWidth, fishHeight, 1, 1, rotation,
            0, 0, fish.getWidth(), fish.getHeight(),
            true,  rotation > 90 && rotation < 270);
        drawHUD();

        batch.end();
    }

    private void handleInput(float delta) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        float dirX = mouseX - (fishX + fishWidth/2);
        float dirY = mouseY - (fishY + fishHeight/2);

        // Розраховуємо кут в градусах (-180 до 180)
        float newRotation = (float)Math.atan2(dirY, dirX) * 180f / (float)Math.PI;


        if (newRotation < 0) {
            newRotation += 360;
        }

        rotation = newRotation;

        float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);

        if (distance > 10) {
            float moveX = dirX / distance * speed * delta;
            float moveY = dirY / distance * speed * delta;

            fishX += moveX;
            fishY += moveY;
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

        font.draw(batch, scoreText, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, livesText, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
    }


    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        fish.dispose();
        font.dispose();
    }

    private SpriteBatch batch;
    private Texture background;
    private Texture fish;

    private float fishX, fishY;
    private float fishWidth, fishHeight;
    private float speed = 200;
    private float rotation = 0f;

    private BitmapFont font;
    private int score = 0;
    private int lives = 3;
}
