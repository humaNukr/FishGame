package com.naukma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.jpg"));
        shark = new Texture(Gdx.files.internal("sprite_0.png"));
        pauseMenu = new PauseMenu();
        fishes = new Array<>();
        animatedShark = new AnimatedShark();
        bloodEffect = new BloodEffect();

        for (int i = 0; i < MAX_FISH; i++) {
            // Випадково обираємо тип рибки
            int fishType = MathUtils.random(2);
            switch(fishType) {
                case 0:
                    createFish("first_fish/", 15);
                    break;
                case 1:
                    createFish("second_fish/", 8);
                    break;
                case 2:
                    createFish("third_fish/", 7);
                    break;
            }
        }

        sharkWidth = shark.getWidth()*SHARK_SCALE;
        sharkHeight = shark.getHeight()*SHARK_SCALE;


        // Початкова позиція по центру
        sharkX = (Gdx.graphics.getWidth() - sharkWidth) / 2f;
        sharkY = (Gdx.graphics.getHeight() - sharkHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);
    }

    private void createFish(String path, int frameCount) {
        float speed = MathUtils.random(MIN_FISH_SPEED, MAX_FISH_SPEED);
        float scale = MathUtils.random(MIN_FISH_SCALE, MAX_FISH_SCALE);
        float frameDuration = MathUtils.random(0.02f, 0.05f);
        AnimatedFish fish = new AnimatedFish(
            path,
            frameCount,
            true,
            speed,
            scale,
            frameDuration
        );
        fishes.add(fish);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (!isPaused) {
            handleInput(Gdx.graphics.getDeltaTime());
            animatedShark.update(Gdx.graphics.getDeltaTime());
            bloodEffect.update(Gdx.graphics.getDeltaTime());
            checkCollisions();
            // Update all fish
            for (AnimatedFish fish : fishes) {
                fish.update(Gdx.graphics.getDeltaTime());
                if (!fish.isActive() && fishes.size < MAX_FISH) {

                    String randomPath;
                    int frameCount;
                    int fishType = MathUtils.random(2);
                    switch(fishType) {
                        case 0:
                            randomPath = "first_fish/";
                            frameCount = 15;
                            break;
                        case 1:
                            randomPath = "second_fish/";
                            frameCount = 8;
                            break;
                        default:
                            randomPath = "third_fish/";
                            frameCount = 7;
                            break;
                    }
                    createFish(randomPath, frameCount);
                }
            }

            for (int i = fishes.size - 1; i >= 0; i--) {
                if (!fishes.get(i).isActive()) {
                    fishes.removeIndex(i);
                }
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                pauseMenu.moveUp();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                pauseMenu.moveDown();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                handleMenuSelection();
            }
        }

        // Rendering
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (AnimatedFish fish : fishes) {
            fish.render(batch);
        }
        bloodEffect.render(batch);
        Texture currentSharkTexture = animatedShark.getCurrentTexture();
        batch.draw(currentSharkTexture,
            sharkX, sharkY,
            sharkWidth/2, sharkHeight/2,
            sharkWidth, sharkHeight,
            1, 1,
            rotation,
            0, 0,
            currentSharkTexture.getWidth(), currentSharkTexture.getHeight(),
            true, rotation > 90 && rotation < 270);

        drawHUD();
        if (isPaused) {
            pauseMenu.render(batch);
        }
        batch.end();
    }

    private void checkCollisions() {
        if (animatedShark.isEating()) return;

        float sharkCenterX = sharkX + sharkWidth/2;
        float sharkCenterY = sharkY + sharkHeight/2;

        for (AnimatedFish fish : fishes) {
            if (!fish.isActive()) continue;

            float fishX = fish.getX();
            float fishY = fish.getY();
            float fishWidth = fish.getWidth();
            float fishHeight = fish.getHeight();
            float fishScale = fish.getScale();

            float fishCenterX = fishX + fishWidth/2;
            float fishCenterY = fishY + fishHeight/2;

            float distance = (float) Math.sqrt(
                Math.pow(sharkCenterX - fishCenterX, 2) +
                    Math.pow(sharkCenterY - fishCenterY, 2)
            );

            if (distance < EATING_DISTANCE && fishScale < SHARK_SCALE) {
                animatedShark.startEating();
                // Додаємо ефект крові
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bloodEffect.spawn(fishCenterX, fishCenterY);
                    }
                }, BLOOD_EFFECT_DELAY);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        fish.setActive(false);
                        score += 10;
                    }
                }, EATING_FRAME_DELAY);
            }
        }
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
            float moveX = dirX / distance * sharkSpeed * delta;
            float moveY = dirY / distance * sharkSpeed * delta;

            sharkX += moveX;
            sharkY += moveY;
        }

        // Обмеження межами екрану
        if (sharkX < 0) sharkX = 0;
        if (sharkY < 0) sharkY = 0;
        if (sharkX > Gdx.graphics.getWidth() - sharkWidth) sharkX = Gdx.graphics.getWidth() - sharkWidth;
        if (sharkY > Gdx.graphics.getHeight() - sharkHeight) sharkY = Gdx.graphics.getHeight() - sharkHeight;
    }

    private void handleMenuSelection() {
        switch(pauseMenu.getSelectedItem()) {
            case 0: // Resume
                isPaused = false;
                break;
            case 1: // Restart
                resetGame();
                isPaused = false;
                break;
            case 2: // Exit
                Gdx.app.exit();
                break;
        }
    }


    private void drawHUD() {
        String scoreText = "Score: " + score;
        String livesText = "Lives: " + lives;

        font.draw(batch, scoreText, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, livesText, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
    }

    private void resetGame() {
        for (AnimatedFish fish : fishes) {
            fish.dispose();
        }
        fishes.clear();

        for (int i = 0; i < MAX_FISH; i++) {
            int fishType = MathUtils.random(2);
            switch(fishType) {
                case 0:
                    createFish("first_fish/", 15);
                    break;
                case 1:
                    createFish("second_fish/", 8);
                    break;
                case 2:
                    createFish("third_fish/", 7);
                    break;
            }
        }

        score = 0;
        lives = 3;
        sharkX = (Gdx.graphics.getWidth() - sharkWidth) / 2f;
        sharkY = (Gdx.graphics.getHeight() - sharkHeight) / 2f;
        rotation = 0f;
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        shark.dispose();
        for (AnimatedFish fish : fishes) {
            fish.dispose();
        }
        font.dispose();
        pauseMenu.dispose();
        animatedShark.dispose();
        bloodEffect.dispose();
    }

    private SpriteBatch batch;
    private Texture background;
    private Texture shark;
    private Array<AnimatedFish> fishes;
    private AnimatedShark animatedShark;
    private BloodEffect bloodEffect;



    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float sharkSpeed = 200;
    private float rotation = 0f;

    private static final float BLOOD_EFFECT_DELAY = 0.55f; // Тривалість ефекту крові
    private static final int MAX_FISH = 10;
    private static final float MAX_FISH_SPEED = 250;
    private static final float MIN_FISH_SPEED = 50;
    private static final float MIN_FISH_SCALE = 0.1f;
    private static final float MAX_FISH_SCALE = 1f;
    private static final float SHARK_SCALE = 0.5f;
    private static final float EATING_DISTANCE = 50f; // Відстань на якій акула почне їсти
    private static final float EATING_FRAME_DELAY = 0.2f;// Затримка перед з'їданням риби


    private BitmapFont font;
    private int score = 0;
    private int lives = 3;

    private PauseMenu pauseMenu;
    private boolean isPaused = false;
}
