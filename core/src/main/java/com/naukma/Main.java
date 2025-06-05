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
import com.naukma.LevelManager;
import com.naukma.BasicLevel;
import com.naukma.FishSpawnData;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        batch = new SpriteBatch();

        mainMenu = new MainMenu();
        levelManager = new LevelManager();
        gameOverMenu = new GameOverMenu();

        scrollingBackground = new ScrollingBackground("output.jpg");
        shark = new Texture(Gdx.files.internal("shark/frame_00.png"));
        pauseMenu = new PauseMenu();
        fishes = new Array<>();
        eatingShark = new EatingShark();
        swimmingShark = new SwimmingShark();
        bloodEffect = new BloodEffect();

        sharkWidth = shark.getWidth() * SHARK_SCALE;
        sharkHeight = shark.getHeight() * SHARK_SCALE;
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);

        tempVector = new Vector3();

        levelManager.startLevel(0);
        initializeCurrentLevel();

        showingMenu = false;
    }

    private void initializeCurrentLevel() {
        // Очищаємо попередні рибки
        for (SwimmingFish fish : fishes) {
            fish.dispose();
        }
        fishes.clear();

        BasicLevel currentLevel = levelManager.getCurrentLevel();

        // Створюємо рибок відповідно до поточного рівня
        // Тепер створюємо рибок з урахуванням фіксованої кількості
        createInitialFish();

        // Ініціалізуємо таймер з поточного рівня
        gameTimer = currentLevel.getTimeLimit();
        gameEnded = false;

        // Оновлюємо швидкість акули з рівня
        sharkSpeed = currentLevel.getSharkSpeed();
    }

    // Новий метод для створення початкових риб
    private void createInitialFish() {
        BasicLevel currentLevel = levelManager.getCurrentLevel();

        // Створюємо рибок до максимального ліміту або до досягнення фіксованих кількостей
        while (fishes.size < currentLevel.getMaxFishCount()) {
            FishSpawnData fishData = currentLevel.getRandomFish();
            if (fishData == null) {
                break; // Всі види риб досягли своїх лімітів
            }

            createFishFromData(fishData);
        }
    }


    private void createFishFromData(FishSpawnData fishData) {
        BasicLevel currentLevel = levelManager.getCurrentLevel();

        // Отримуємо швидкість і розмір з урахуванням фіксованих значень
        float speed = currentLevel.getFishSpeed(fishData);
        float scale = currentLevel.getFishScale(fishData);

        SwimmingFish fish = new SwimmingFish(
            fishData.path,
            fishData.frameCount,
            true,
            speed,
            scale,
            fishData.frameDuration
        );

        fish.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());

        // Зберігаємо тип рибки для відстеження
        fish.setFishType(fishData.path);

        // Реєструємо створення рибки в системі рівнів
        currentLevel.addFish(fishData.path);

        fishes.add(fish);
    }

    // Оновлений старий метод createFish
    private void createFish(String path, int frameCount) {
        BasicLevel currentLevel = levelManager.getCurrentLevel();

        // Спробуємо знайти відповідні дані для цього шляху
        FishSpawnData matchingData = null;
        for (FishSpawnData fishData : currentLevel.availableFish) {
            if (fishData.path.equals(path)) {
                matchingData = fishData;
                break;
            }
        }

        if (matchingData != null && currentLevel.canSpawnFish(matchingData)) {
            createFishFromData(matchingData);
        } else {
            // Fallback до старого способу, якщо не знайдено або досягнуто ліміт
            float speed = MathUtils.random(currentLevel.getMinFishSpeed(), currentLevel.getMaxFishSpeed());
            float scale = MathUtils.random(currentLevel.getMinFishScale(), currentLevel.getMaxFishScale());

            SwimmingFish fish = new SwimmingFish(
                path,
                frameCount,
                true,
                speed,
                scale,
                0.1f // значення за замовчуванням
            );

            fish.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());
            fish.setFishType(path);
            fishes.add(fish);
        }
    }

    @Override
    public void render() {
        // Перевірка: чи відображати меню програшу
        if (gameOverMenu.isActive()) {
            gameOverMenu.handleInput();

            if (gameOverMenu.shouldRestart()) {
                resetGame();
                gameOverMenu.resetFlags();
                return;
            }

            if (gameOverMenu.shouldReturnToMainMenu()) {
                showingMenu = true;
                mainMenu.setActive(true);
                gameOverMenu.resetFlags();
                return;
            }

            // Рендеримо фон гри + об'єкти
            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            scrollingBackground.render(batch);

            for (SwimmingFish fish : fishes) {
                if (scrollingBackground.isInView(fish.getX(), fish.getY(), fish.getWidth(), fish.getHeight())) {
                    fish.renderAt(batch, fish.getX(), fish.getY());
                }
            }

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

            bloodEffect.render(batch);
            levelManager.getCurrentLevel().renderLevelSpecific(batch);

            batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            drawHUD();

            // Рендеримо саме вікно програшу
            gameOverMenu.render(batch);
            batch.end();

            return;
        }

        // Обробка паузи
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            pauseMenu.setActive(isPaused);
            return;
        }

        // Ігрова логіка
        if (!isPaused && !gameEnded) {
            gameTimer -= Gdx.graphics.getDeltaTime();

            BasicLevel currentLevel = levelManager.getCurrentLevel();

            if (gameTimer <= 0) {
                gameTimer = 0;
                gameEnded = true;
                gameOverMenu.setGameOverReason("Time's up!");
                gameOverMenu.setActive(true);
            } else if (currentLevel.checkWinCondition(score, gameTimer, fishEaten)) {
                gameTimer = 0;
                gameEnded = true;
                currentLevel.setCompleted(true);
            } else if (currentLevel.checkLoseCondition(score, gameTimer, lives)) {
                gameTimer = 0;
                gameEnded = true;
                currentLevel.setFailed(true);
                gameOverMenu.setGameOverReason("Mission Failed!");
                gameOverMenu.setActive(true);
            }

            currentLevel.updateLevelLogic(Gdx.graphics.getDeltaTime(), sharkX, sharkY);
            handleInput(Gdx.graphics.getDeltaTime());
            eatingShark.update(Gdx.graphics.getDeltaTime());
            bloodEffect.update(Gdx.graphics.getDeltaTime());
            scrollingBackground.updateCamera(sharkX, sharkY, sharkWidth, sharkHeight);
            checkCollisions();

            for (SwimmingFish fish : fishes) {
                fish.update(Gdx.graphics.getDeltaTime());
            }

            // Видаляємо неактивних рибок (прикінці методу)
            for (int i = fishes.size - 1; i >= 0; i--) {
                SwimmingFish fish = fishes.get(i);
                if (!fish.isActive()) {
                    currentLevel.removeFish(fish.getFishType());
                    fish.dispose();
                    fishes.removeIndex(i);
                }
            }
        }

        // Рендер гри
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        scrollingBackground.render(batch);

        for (SwimmingFish fish : fishes) {
            if (scrollingBackground.isInView(fish.getX(), fish.getY(), fish.getWidth(), fish.getHeight())) {
                fish.renderAt(batch, fish.getX(), fish.getY());
            }
        }

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

        bloodEffect.render(batch);
        levelManager.getCurrentLevel().renderLevelSpecific(batch);

        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        drawHUD();

        if (isPaused) {
            pauseMenu.render(batch);
        }

        batch.end();
    }


    // Новий метод для спроби створення нових риб
    private void trySpawnNewFish() {
        BasicLevel currentLevel = levelManager.getCurrentLevel();

        // Перевіряємо чи потрібно створити нові рибки
        while (fishes.size < currentLevel.getMaxFishCount()) {
            FishSpawnData fishData = currentLevel.getRandomFish();
            if (fishData == null) {
                break; // Всі доступні типи риб досягли своїх лімітів
            }

            createFishFromData(fishData);
        }
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

            if (distance < EATING_DISTANCE && fishSize < sharkSize * SIZE_RATIO_THRESHOLD) {
                eatingShark.startEating();

                final float bloodX = fishFrontX;
                final float bloodY = fishFrontY;
                final String fishType = fish.getFishType();

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

                        // Отримуємо очки з даних рибки
                        BasicLevel currentLevel = levelManager.getCurrentLevel();
                        int fishScore = getFishScore(currentLevel, fishType);
                        score += fishScore;
                        fishEaten++;
                    }
                }, EATING_FRAME_DELAY);
            }
        }
    }

    // Новий метод для отримання очок за рибку
    private int getFishScore(BasicLevel currentLevel, String fishType) {
        for (FishSpawnData fishData : currentLevel.availableFish) {
            if (fishData.path.equals(fishType)) {
                return fishData.points;
            }
        }
        return 10; // значення за замовчуванням
    }

    private void handleInput(float delta) {
        tempVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        scrollingBackground.getCamera().unproject(tempVector);

        float mouseX = tempVector.x;
        float mouseY = tempVector.y;

        float dirX = mouseX - (sharkX + sharkWidth / 2);
        float dirY = mouseY - (sharkY + sharkHeight / 2);

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

        if (sharkX < 0) sharkX = 0;
        if (sharkX > scrollingBackground.getWorldWidth() - sharkWidth)
            sharkX = scrollingBackground.getWorldWidth() - sharkWidth;

        if (sharkY < 0) sharkY = 0;
        if (sharkY > scrollingBackground.getWorldHeight() - sharkHeight)
            sharkY = scrollingBackground.getWorldHeight() - sharkHeight;
    }

    private void drawHUD() {
        String scoreText = "Score: " + score;
        String livesText = "Lives: " + lives;
        String menuHint = "Esc - Menu";

        BasicLevel currentLevel = levelManager.getCurrentLevel();
        String levelText = "Level " + currentLevel.getLevelNumber() + ": " + currentLevel.getLevelName();


        font.draw(batch, scoreText, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, livesText, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
        font.draw(batch, menuHint, 20, Gdx.graphics.getHeight() - 60);
        font.draw(batch, levelText, 20, Gdx.graphics.getHeight() - 100);

        drawTimer();
    }

    private void drawTimer() {
        int minutes = (int) (gameTimer / 60);
        int seconds = (int) (gameTimer % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);

        Color originalColor = font.getColor();
        if (gameTimer <= 5 && gameTimer > 0) {
            font.setColor(Color.RED);
        } else if (gameTimer <= 0) {
            font.setColor(Color.SCARLET);
            timeText = "00:00";
        } else {
            font.setColor(Color.WHITE);
        }

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(font, timeText);
        float textWidth = layout.width;
        float x = (Gdx.graphics.getWidth() - textWidth) / 2;
        float y = 50;

        font.draw(batch, timeText, x, y);
        font.setColor(originalColor);
    }

    private void resetGame() {
        // Очищаємо рибок та скидаємо лічільники в рівні
        BasicLevel currentLevel = levelManager.getCurrentLevel();
        for (SwimmingFish fish : fishes) {
            currentLevel.removeFish(fish.getFishType());
            fish.dispose();
        }
        fishes.clear();

        score = 0;
        lives = 3;
        fishEaten = 0;
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;
        rotation = 0f;

        levelManager.startLevel(levelManager.getCurrentLevelIndex());
        initializeCurrentLevel();
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
        gameOverMenu.dispose(); // Додаємо очищення GameOverMenu

        if (mainMenu != null) {
            mainMenu.dispose();
        }
    }

    // Змінні класу
    private SpriteBatch batch;
    private ScrollingBackground scrollingBackground;
    private Texture shark;
    private Array<SwimmingFish> fishes;
    private EatingShark eatingShark;
    private SwimmingShark swimmingShark;
    private BloodEffect bloodEffect;

    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float sharkSpeed = 200;
    private float rotation = 0f;

    private static final float BLOOD_EFFECT_DELAY = 0.55f;
    private static final float SHARK_SCALE = 0.5f;
    private static final float EATING_DISTANCE = 75f;
    private static final float EATING_FRAME_DELAY = 0.2f;

    private BitmapFont font;
    private int score = 0;
    private int lives = 3;
    private int fishEaten = 0;

    private PauseMenu pauseMenu;
    private boolean isPaused = false;

    private Vector3 tempVector;

    private MainMenu mainMenu;
    private boolean showingMenu = true;

    private float gameTimer;
    private boolean gameEnded = false;

    private LevelManager levelManager;
    private GameOverMenu gameOverMenu; // Додаємо змінну GameOverMenu
}
