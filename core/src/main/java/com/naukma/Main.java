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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        batch = new SpriteBatch();

        mainMenu = new MainMenu();
        gameHUD = new GameHUD();
        scrollingBackground = new ScrollingBackground("output.jpg");
        shark = new Texture(Gdx.files.internal("shark/frame_00.png"));
        pauseMenu = new PauseMenu();
        fishes = new Array<>();
        eatingShark = new EatingShark();
        swimmingShark = new SwimmingShark();
        bloodEffect = new BloodEffect();

        // Створюємо рибок у світових координатах
        for (int i = 0; i < MAX_FISH_COUNT; i++) {
            int fishType = MathUtils.random(TOTAL_FISH_TYPES - 1);
            switch (fishType) {
                case 0:
                    createFish("fish_01/", 15);
                    break;
                case 1:
                    createFish("fish_02/", 15);
                    break;
                case 2:
                    createFish("fish_03/", 15);
                    break;
                case 3:
                    createFish("fish_04/", 15);
                    break;
                case 4:
                    createFish("fish_05/", 15);
                    break;
                case 5:
                    createFish("fish_06/", 15);
                    break;
                case 6:
                    createFish("fish_07/", 15);
                    break;
                case 7:
                    createFish("fish_08/", 15);
                    break;
                case 8:
                    createFish("fish_09/", 15);
                    break;
            }
        }

        sharkWidth = shark.getWidth() * SHARK_SCALE;
        sharkHeight = shark.getHeight() * SHARK_SCALE;

        // Початкова позиція акули в світових координатах (центр світу)
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);

        // Вектор для перетворення координат
        tempVector = new Vector3();
    }

    private void createFish(String path, int frameCount) {
        float speed = MathUtils.random(MIN_FISH_SPEED, MAX_FISH_SPEED);
        float scale = MathUtils.random(MIN_FISH_SCALE, MAX_FISH_SCALE);
        float frameDuration;

        switch (path) {
            case "fish_01/":
                frameDuration = 0.05f;
                break;
            case "fish_02/":
                frameDuration = 0.05f;
                break;
            case "fish_03/":
                frameDuration = 0.16f;
                break;
            case "fish_04/":
                frameDuration = 0.05f;
                break;
            case "fish_05/":
                frameDuration = 0.05f;
                break;
            case "fish_06/":
                frameDuration = 0.05f;
                break;
            case "fish_07/":
                frameDuration = 0.05f;
                break;
            case "fish_08/":
                frameDuration = 0.05f;
                break;
            case "fish_09/":
                frameDuration = 0.05f;
                break;
            default:
                frameDuration = 0.1f;
                break;
        }

        SwimmingFish fish = new SwimmingFish(
            path,
            frameCount,
            true,
            speed,
            scale,
            frameDuration
        );

        fish.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());
        fishes.add(fish);
    }

    @Override
    public void render() {
        // Перевіряємо чи показуємо меню
        if (showingMenu) {
            mainMenu.handleInput();

            if (!mainMenu.isActive()) {
                showingMenu = false; // Переходимо до гри
            }

            // Рендеринг меню
            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            mainMenu.render(batch);
            batch.end();

            return; // Не виконуємо ігрову логіку
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            pauseMenu.setActive(isPaused); // Встановлюємо активність меню паузи
            return;
        }

        if (!isPaused) {
            handleInput(Gdx.graphics.getDeltaTime());
            eatingShark.update(Gdx.graphics.getDeltaTime());
            bloodEffect.update(Gdx.graphics.getDeltaTime());

            // Оновлюємо камеру відносно позиції акули
            scrollingBackground.updateCamera(sharkX, sharkY, sharkWidth, sharkHeight);

            checkCollisions();

            // Оновлюємо всіх рибок
            for (SwimmingFish fish : fishes) {
                fish.update(Gdx.graphics.getDeltaTime());

                if (!fish.isActive() && fishes.size < MAX_FISH_COUNT) {
                    String randomPath;
                    int frameCount;
                    int fishType = MathUtils.random(TOTAL_FISH_TYPES - 1);
                    switch (fishType) {
                        case 0:
                            randomPath = "fish_01/";
                            frameCount = 15;
                            break;
                        case 1:
                            randomPath = "fish_02/";
                            frameCount = 15;
                            break;
                        case 2:
                            randomPath = "fish_03/";
                            frameCount = 15;
                            break;
                        case 3:
                            randomPath = "fish_04/";
                            frameCount = 15;
                            break;
                        case 4:
                            randomPath = "fish_05/";
                            frameCount = 15;
                            break;
                        case 5:
                            randomPath = "fish_06/";
                            frameCount = 15;
                            break;
                        case 6:
                            randomPath = "fish_07/";
                            frameCount = 15;
                            break;
                        case 7:
                            randomPath = "fish_08/";
                            frameCount = 15;
                            break;
                        default:
                            randomPath = "fish_09/";
                            frameCount = 15;
                            break;
                    }
                    createFish(randomPath, frameCount);
                }
            }

            // Видаляємо неактивних рибок
            for (int i = fishes.size - 1; i >= 0; i--) {
                if (!fishes.get(i).isActive()) {
                    fishes.removeIndex(i);
                }
            }
        } else {
            // Обробляємо ввід для меню паузи
            pauseMenu.handleInput();

            // ОСНОВНІ ЗМІНИ: Перевіряємо стан меню паузи після обробки вводу
            if (!pauseMenu.isActive()) {
                // Якщо меню паузи стало неактивним, це означає що натиснули "Resume"
                isPaused = false;
            }

            if (pauseMenu.shouldRestart()) {
                resetGame();
                isPaused = false;
                pauseMenu.resetRestartFlag();
            }

            if (pauseMenu.shouldReturnToMainMenu()) {
                showingMenu = true;
                mainMenu.setActive(true);
                isPaused = false;
                pauseMenu.resetReturnFlag();
            }
        }

        // Рендеринг
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Малюємо скролінговий фон (він встановлює свою проекційну матрицю)
        scrollingBackground.render(batch);

        // Малюємо рибок у світових координатах
        for (SwimmingFish fish : fishes) {
            if (scrollingBackground.isInView(fish.getX(), fish.getY(), fish.getWidth(), fish.getHeight())) {
                fish.renderAt(batch, fish.getX(), fish.getY());
            }
        }

        // Малюємо акулу в світових координатах
        if (eatingShark.isEating()) {
            Texture currentSharkTexture = eatingShark.getCurrentTexture();
            batch.draw(currentSharkTexture,
                sharkX, sharkY,
                sharkWidth / 2, sharkHeight / 2,
                sharkWidth, sharkHeight,
                1, 1,
                rotation,
                0, 0,
                currentSharkTexture.getWidth(), currentSharkTexture.getHeight(),
                true, rotation > 90 && rotation < 270);
        } else {
            swimmingShark.renderAt(batch, sharkX, sharkY, rotation);
        }

        // Ефект крові у світових координатах
        bloodEffect.render(batch);

        // Перемикаємося на стандартну проекцію для HUD та меню
        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        if (isPaused) {
            pauseMenu.render(batch);
        }else{
            gameHUD.update(Gdx.graphics.getDeltaTime());
        }
        gameHUD.render(batch);
        batch.end();
    }

    private void checkCollisions() {
        if (eatingShark.isEating()) return;

        double rotationRad = Math.toRadians(rotation);
        float sharkCenterX = sharkX + sharkWidth / 2;
        float sharkCenterY = sharkY + sharkHeight / 2;
        float headDistance = sharkWidth / 2;
        float sharkHeadX = sharkCenterX + (float) (Math.cos(rotationRad) * headDistance);
        float sharkHeadY = sharkCenterY + (float) (Math.sin(rotationRad) * headDistance);

        for (SwimmingFish fish : fishes) {
            if (!fish.isActive()) continue;

            float fishX = fish.getX();
            float fishY = fish.getY();
            float fishWidth = fish.getWidth();
            float fishHeight = fish.getHeight();
            float fishFrontX = fish.isMovingRight() ? fishX + fishWidth * 0.75f : fishX + fishWidth * 0.25f;
            float fishFrontY = fishY + fishHeight / 2;

            float distance = (float) Math.sqrt(Math.pow(sharkHeadX - fishFrontX, 2) + Math.pow(sharkHeadY - fishFrontY, 2));
            float sharkSize = sharkWidth * sharkHeight;
            float fishSize = fishWidth * fishHeight;
            float SIZE_RATIO_THRESHOLD = 0.3f;

            boolean collision = distance < EATING_DISTANCE && fishSize < sharkSize * SIZE_RATIO_THRESHOLD;

            if (collision) {
                eatingShark.startEating();
                fish.setActive(false);
                gameHUD.addScore(10); // Add points when fish is eaten
                gameHUD.addFishEaten(); // Додаємо рибку для прогресу рівня

                final float bloodX = fishFrontX;
                final float bloodY = fishFrontY;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bloodEffect.spawn(bloodX, bloodY);
                    }
                }, BLOOD_EFFECT_DELAY);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        fish.setActive(false);
                        gameHUD.addScore(10); // Add points

                    }
                }, EATING_FRAME_DELAY);
            }
        }
    }


    private void handleInput(float delta) {
        // Конвертуємо координати миші в світові координати через камеру
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        scrollingBackground.getCamera().unproject(tempVector);

        float mouseX = tempVector.x;
        float mouseY = tempVector.y;

        float dirX = mouseX - (sharkX + sharkWidth / 2);
        float dirY = mouseY - (sharkY + sharkHeight / 2);

        // Розраховуємо кут
        float newRotation = (float) Math.atan2(dirY, dirX) * 180f / (float) Math.PI;

        if (newRotation < 0) {
            newRotation += 360;
        }

        rotation = newRotation;

        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (distance > 10) {
            float moveX = dirX / distance * sharkSpeed * delta;
            float moveY = dirY / distance * sharkSpeed * delta;

            sharkX += moveX;
            sharkY += moveY;
        }

        // Обмеження межами світу
        // Горизонтальні межі - не можна виїжджати за межі екрану
        if (sharkX < 0) sharkX = 0;
        if (sharkX > scrollingBackground.getWorldWidth() - sharkWidth)
            sharkX = scrollingBackground.getWorldWidth() - sharkWidth;

        // Вертикальні межі - можна рухатися по всьому світу
        if (sharkY < 0) sharkY = 0;
        if (sharkY > scrollingBackground.getWorldHeight() - sharkHeight)
            sharkY = scrollingBackground.getWorldHeight() - sharkHeight;
    }

    private void handleMenuSelection() {
        switch (pauseMenu.getSelectedItem()) {
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


    private void resetGame() {
        for (SwimmingFish fish : fishes) {
            fish.dispose();
        }
        fishes.clear();

        for (int i = 0; i < MAX_FISH_COUNT; i++) {
            int fishType = MathUtils.random(TOTAL_FISH_TYPES - 1);
            switch (fishType) {
                case 0:
                    createFish("fish_01/", 15);
                    break;
                case 1:
                    createFish("fish_02/", 15);
                    break;
                case 2:
                    createFish("fish_03/", 15);
                    break;
                case 3:
                    createFish("fish_04/", 15);
                    break;
                case 4:
                    createFish("fish_05/", 15);
                    break;
                case 5:
                    createFish("fish_06/", 15);
                    break;
                case 6:
                    createFish("fish_07/", 15);
                    break;
                case 7:
                    createFish("fish_08/", 15);
                    break;
                case 8:
                    createFish("fish_09/", 15);
                    break;
            }
        }

        score = 0;
        lives = 3;
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;
        rotation = 0f;
    }

    @Override
    public void dispose() {
        batch.dispose();
        scrollingBackground.dispose();
        shark.dispose();
        for (SwimmingFish fish : fishes) {
            fish.dispose();
        }
        font.dispose();
        pauseMenu.dispose();
        eatingShark.dispose();
        bloodEffect.dispose();
        gameHUD.dispose();
        swimmingShark.dispose();

        // Очищуємо головне меню
        if (mainMenu != null) {
            mainMenu.dispose();
        }
    }


    private SpriteBatch batch;
    private ScrollingBackground scrollingBackground;
    private Texture shark;
    private Array<SwimmingFish> fishes;
    private EatingShark eatingShark;
    private SwimmingShark swimmingShark;
    private BloodEffect bloodEffect;
    private GameHUD gameHUD;

    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float sharkSpeed = 200;
    private float rotation = 0f;

    private static final int TOTAL_FISH_TYPES = 8; // Всього типів риб
    private static final float BLOOD_EFFECT_DELAY = 0.55f;
    private static final int MAX_FISH_COUNT = 20;
    private static final float MAX_FISH_SPEED = 250;
    private static final float MIN_FISH_SPEED = 50;
    private static final float MIN_FISH_SCALE = 0.1f;
    private static final float MAX_FISH_SCALE = 1f;
    private static final float SHARK_SCALE = 0.5f;
    private static final float EATING_DISTANCE = 75f;
    private static final float EATING_FRAME_DELAY = 0.2f;

    private BitmapFont font;
    private int score = 0;
    private int lives = 3;

    private PauseMenu pauseMenu;
    private boolean isPaused = false;

    // Додаткові змінні для роботи з камерою
    private Vector3 tempVector;

    // Змінні для головного меню
    private MainMenu mainMenu;
    private boolean showingMenu = true;
}
