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
        shark = new Texture(Gdx.files.internal("sprite_0.png"));

        sharkWidth = shark.getWidth()*0.25f;
        sharkHeight = shark.getHeight()*0.25f;

        fish1 = new AnimatedFish(
            "first_fish/",  // шлях до кадрів в папці fish1
            15,            // кількість кадрів
            true,          // спрайт дивиться вліво
            1000,           // швидкість
            0.2f,         // масштаб
            0.05f         // тривалість кадру
        );

        // Початкова позиція по центру
        sharkX = (Gdx.graphics.getWidth() - sharkWidth) / 2f;
        sharkY = (Gdx.graphics.getHeight() - sharkHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());

        fish1.update(Gdx.graphics.getDeltaTime());
        if (!fish1.isActive()) {
            fish1.respawn();
        }

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fish1.render(batch);
        batch.draw(shark, sharkX, sharkY, sharkWidth/2, sharkHeight/2,
            sharkWidth, sharkHeight, 1, 1, rotation,
            0, 0, shark.getWidth(), shark.getHeight(),
            true, rotation > 90 && rotation < 270);
        drawHUD();
        batch.end();
    }

    private void handleInput(float delta) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        float dirX = mouseX - (sharkX + sharkWidth /2);
        float dirY = mouseY - (sharkY + sharkHeight /2);

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

            sharkX += moveX;
            sharkY += moveY;
        }

        // Обмеження межами екрану
        if (sharkX < 0) sharkX = 0;
        if (sharkY < 0) sharkY = 0;
        if (sharkX > Gdx.graphics.getWidth() - sharkWidth) sharkX = Gdx.graphics.getWidth() - sharkWidth;
        if (sharkY > Gdx.graphics.getHeight() - sharkHeight) sharkY = Gdx.graphics.getHeight() - sharkHeight;
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
        shark.dispose();
        fish1.dispose();
        font.dispose();
    }

    private SpriteBatch batch;
    private Texture background;
    private Texture shark;
    private AnimatedFish fish1;

    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float speed = 200;
    private float rotation = 0f;

    private BitmapFont font;
    private int score = 0;
    private int lives = 3;
}
