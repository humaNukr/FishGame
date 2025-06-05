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
import com.badlogic.gdx.utils.ObjectMap;

public class BasicLevel extends ApplicationAdapter {
    // Налаштування рівня
    protected int levelNumber;
    protected float timeLimit;
    protected int targetScore;
    protected int targetFishCount; // Кількість риб для перемоги
    protected int maxFishCount;
    protected int livesCount; // Кількість життів на рівні
    protected boolean isCompleted;
    protected boolean isFailed;

    // Налаштування складності
    protected float sharkSpeed;
    protected float minFishSpeed;
    protected float maxFishSpeed;
    protected float minFishScale;
    protected float maxFishScale;

    // Типи риб для цього рівня
    protected Array<FishSpawnData> availableFish;
    protected ObjectMap<String, Integer> currentFishCounts;

    // Ігрові об'єкти
    private SpriteBatch batch;
    private ScrollingBackground scrollingBackground;
    private Texture shark;
    private Array<SwimmingFish> fishes;
    private EatingShark eatingShark;
    private SwimmingShark swimmingShark;
    private BloodEffect bloodEffect;
    protected GameHUD gameHUD;

    // Позиція і характеристики акули
    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float rotation = 0f;

    // Константи
    private static final int TOTAL_FISH_TYPES = 8;
    private static final float BLOOD_EFFECT_DELAY = 0.55f;
    private static final float SHARK_SCALE = 0.5f;
    private static final float EATING_DISTANCE = 75f;
    private static final float EATING_FRAME_DELAY = 0.2f;

    // Додаткові змінні
    private BitmapFont font;
    private int score = 0;
    private int lives = 3; // Поточні життя гравця
    private PauseMenu pauseMenu;
    private boolean isPaused = false;
    private Vector3 tempVector;
    
    // Додаткові змінні для нової системи
    private Array<String> unlockedFishTypes; // Розблоковані типи рибок для акули

    public BasicLevel(int levelNumber) {
        this.levelNumber = levelNumber;
        this.availableFish = new Array<>();
        this.currentFishCounts = new ObjectMap<>();
        this.unlockedFishTypes = new Array<>();
        initializeLevel();
        this.lives = this.livesCount; // Ініціалізуємо життя з налаштувань рівня
        initializeUnlockedFishTypes();
    }

    // Конструктор за замовчуванням для Main
    public BasicLevel() {
        this(1);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Створюємо GameHUD з параметрами рівня
        gameHUD = new GameHUD();
        gameHUD.setCurrentGameLevel(levelNumber);
        
        // Встановлюємо параметри рівня в HUD
        int targetFish = getTargetFishCount(); // Отримуємо target з checkWinCondition
        gameHUD.setLevelParameters(timeLimit, targetFish);
        gameHUD.setCurrentLives(lives); // Встановлюємо поточні життя
        gameHUD.resetTimer();
        
        // Оновлюємо іконки рибок згідно з поточним рівнем
        gameHUD.updateLevelFishIcons(availableFish);

        scrollingBackground = new ScrollingBackground("output.jpg");
        shark = new Texture(Gdx.files.internal("shark/frame_00.png"));
        pauseMenu = new PauseMenu();
        fishes = new Array<>();
        eatingShark = new EatingShark();
        swimmingShark = new SwimmingShark();
        bloodEffect = new BloodEffect();

        // Створюємо рибок використовуючи дані рівня
        createInitialFishes();

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

        // Додаткові змінні для нової системи
        unlockedFishTypes = new Array<>();
        updateUnlockedFishTypes();
        
        // Встановлюємо видимі межі для всіх рибок (без HUD зверху)
        setVisibleBoundsForAllFish();
    }

    protected void initializeLevel() {
        // За замовчуванням - базові налаштування
        timeLimit = 60f;
        targetScore = 200;
        targetFishCount = 15; // За замовчуванням 15 риб для перемоги
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 3; // За замовчуванням 3 життя
        sharkSpeed = 200f;
        minFishSpeed = 50f;
        maxFishSpeed = 250f;
        minFishScale = 0.1f;
        maxFishScale = 1f;

        // Стандартні рибки
        availableFish.add(new FishSpawnData("fish_01/", 15, 0.05f, 10, 0.1f));
        availableFish.add(new FishSpawnData("fish_02/", 15, 0.16f, 15, 0.1f));
        availableFish.add(new FishSpawnData("fish_03/", 15, 0.16f, 20, 0.1f));
    }

    private void createInitialFishes() {
        if (availableFish.size == 0) {
            // Якщо немає налаштованих рибок, створюємо стандартні
            createStandardFishes();
        } else {
            // Створюємо рибок з налаштувань рівня (тільки доступні типи)
            for (int i = 0; i < maxFishCount; i++) {
                createRandomLevelFish();
            }
        }
        
        // Після створення рибок встановлюємо їм видимі межі
        updateVisibleBoundsForAllFish();
    }

    private void createStandardFishes() {
        for (int i = 0; i < maxFishCount; i++) {
            int fishType = MathUtils.random(TOTAL_FISH_TYPES - 1);
            switch (fishType) {
                case 0: createFish("fish_01/", 15); break;
                case 1: createFish("fish_02/", 15); break;
                case 2: createFish("fish_03/", 15); break;
                case 3: createFish("fish_04/", 15); break;
                case 4: createFish("fish_05/", 15); break;
                case 5: createFish("fish_06/", 15); break;
                case 6: createFish("fish_07/", 15); break;
                case 7: createFish("fish_08/", 15); break;
                case 8: createFish("fish_09/", 9); break;
            }
        }
    }

    private void createRandomLevelFish() {
        // Створюємо рибку тільки з доступних типів для цього рівня
        if (availableFish.size == 0) return;
        
        FishSpawnData fishData = availableFish.random();
        if (fishData != null) {
            createFishFromDataWithBounds(fishData);
        }
    }

    private void createFishFromDataWithBounds(FishSpawnData fishData) {
        float speed = getFishSpeed(fishData);
        float scale = getFishScale(fishData);

        SwimmingFish fish = new SwimmingFish(
            fishData.path,
            fishData.frameCount,
            true,
            speed,
            scale,
            fishData.frameDuration
        );

        fish.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());
        
        // Встановлюємо видимі межі після створення gameHUD
        updateFishVisibleBounds(fish);
        
        fishes.add(fish);
        addFish(fishData.path);
    }

    private void createFish(String path, int frameCount) {
        float speed = MathUtils.random(minFishSpeed, maxFishSpeed);
        float scale = MathUtils.random(minFishScale, maxFishScale);
        float frameDuration = getFrameDurationForPath(path);

        SwimmingFish fish = new SwimmingFish(path, frameCount, true, speed, scale, frameDuration);
        fish.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());
        
        // Встановлюємо видимі межі
        updateFishVisibleBounds(fish);
        
        fishes.add(fish);
    }

    private float getFrameDurationForPath(String path) {
        switch (path) {
            case "fish_03/": return 0.16f;
            default: return 0.05f;
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            pauseMenu.setActive(isPaused);
            return;
        }

        if (!isPaused) {
            handleInput(Gdx.graphics.getDeltaTime());
            eatingShark.update(Gdx.graphics.getDeltaTime());
            bloodEffect.update(Gdx.graphics.getDeltaTime());

            // Оновлюємо камеру відносно позиції акули
            scrollingBackground.updateCamera(sharkX, sharkY, sharkWidth, sharkHeight);

            checkCollisions();
            updateFishes(Gdx.graphics.getDeltaTime());
            updateLevelLogic(Gdx.graphics.getDeltaTime(), sharkX, sharkY);
            checkLevelConditions();
        } else {
            handlePauseMenu();
        }

        renderGame();
    }

    private void handlePauseMenu() {
        pauseMenu.handleInput();

        if (!pauseMenu.isActive()) {
            isPaused = false;
        }

        if (pauseMenu.shouldRestart()) {
            resetGame();
            isPaused = false;
            pauseMenu.resetRestartFlag();
        }

        // Логіка повернення до головного меню обробляється в Main.java
    }

    private void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Малюємо скролінговий фон
        scrollingBackground.render(batch);

        // Малюємо рибок у світових координатах
        for (SwimmingFish fish : fishes) {
            if (scrollingBackground.isInView(fish.getX(), fish.getY(), fish.getWidth(), fish.getHeight())) {
                fish.renderAt(batch, fish.getX(), fish.getY());
            }
        }

        // Малюємо акулу
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

        // Ефект крові
        bloodEffect.render(batch);

        // Спеціальний рендеринг рівня
        renderLevelSpecific(batch);

        // Перемикаємося на стандартну проекцію для HUD
        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        if (isPaused) {
            pauseMenu.render(batch);
        } else {
            gameHUD.update(Gdx.graphics.getDeltaTime());
        }
        gameHUD.render(batch);
        batch.end();
    }

    private void updateFishes(float deltaTime) {
        for (SwimmingFish fish : fishes) {
            fish.update(deltaTime);

            // Якщо рибка вийшла за межі екрану, змінюємо її тип та параметри
            if (!fish.isActive() && fishes.size < maxFishCount) {
                if (availableFish.size > 0) {
                    // Змінюємо тип рибки на випадковий з доступних для рівня
                    changeFinishedFishToNewType(fish);
                } else {
                    // Стандартна логіка створення рибок
                    String randomPath = "fish_0" + (MathUtils.random(8) + 1) + "/";
                    createFish(randomPath, 15);
                }
            }
        }

        // Видаляємо неактивних рибок
        for (int i = fishes.size - 1; i >= 0; i--) {
            SwimmingFish fish = fishes.get(i);
            if (!fish.isActive()) {
                // Перевіряємо чи рибка за межами екрану та намагаємося її перетворити
                if (fish.getX() < -fish.getWidth() * 3 || fish.getX() > scrollingBackground.getWorldWidth() + fish.getWidth() * 3) {
                    // Змінюємо тип замість видалення
                    if (availableFish.size > 0) {
                        changeFinishedFishToNewType(fish);
                    } else {
                        fish.dispose();
                        fishes.removeIndex(i);
                    }
                } else {
                    fish.dispose();
                    fishes.removeIndex(i);
                }
            }
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
            
            // Перевіряємо чи є колізія взагалі
            boolean hasCollision = distance < EATING_DISTANCE;
            
            if (hasCollision) {
                // Обчислюємо відносні розміри
                float sharkSize = sharkWidth * sharkHeight;
                float fishSize = fishWidth * fishHeight;
                float sizeRatio = fishSize / sharkSize;
                
                // Логіка залежно від розміру рибки відносно акули
                if (sizeRatio < 0.3f) {
                    // МАЛЕНЬКА РИБКА - акула її їсть (тільки якщо тип розблокований)
                    if (canEatFishType(fish.getFishType())) {
                        eatFish(fish, fishFrontX, fishFrontY);
                    }
                } else if (sizeRatio > 0.7f) {
                    // ВЕЛИКА РИБКА - віднімає життя
                    takeDamage(fish, fishFrontX, fishFrontY);
                }
                // СЕРЕДНЯ РИБКА (0.3f <= sizeRatio <= 0.7f) - просто проходить повз, нічого не робимо
            }
        }
    }
    
    private void eatFish(SwimmingFish fish, float fishX, float fishY) {
        eatingShark.startEating();
        fish.setActive(false);
        gameHUD.addScore(10);
        gameHUD.addFishEaten();

        final float bloodX = fishX;
        final float bloodY = fishY;

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
                gameHUD.addScore(10);
            }
        }, EATING_FRAME_DELAY);
        
        // Оновлюємо розблоковані типи після з'їдання рибки
        updateUnlockedFishTypes();
    }
    
    private void takeDamage(SwimmingFish fish, float fishX, float fishY) {
        // Віднімаємо життя
        lives--;
        
        // Оновлюємо життя в HUD
        if (gameHUD != null) {
            gameHUD.setCurrentLives(lives);
        }
        
        // Ефект крові при отриманні урону
        bloodEffect.spawn(fishX, fishY);
        
        // "Відштовхуємо" рибку щоб уникнути повторного урону
        fish.setActive(false);
        
        // Якщо життя закінчились - програш
        if (lives <= 0) {
            isFailed = true;
        }
    }
    
    // Метод для перевірки чи може акула з'їсти цей тип рибки
    private boolean canEatFishType(String fishType) {
        return unlockedFishTypes.contains(fishType, false);
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

        // Обмеження межами світу
        if (sharkX < 0) sharkX = 0;
        if (sharkX > scrollingBackground.getWorldWidth() - sharkWidth)
            sharkX = scrollingBackground.getWorldWidth() - sharkWidth;

        if (sharkY < 0) sharkY = 0;
        if (sharkY > scrollingBackground.getWorldHeight() - sharkHeight)
            sharkY = scrollingBackground.getWorldHeight() - sharkHeight;
    }

    private void checkLevelConditions() {
        float timeRemaining = gameHUD.getTimeRemaining(); // Тепер таймер йде до 0

        if (checkWinCondition(gameHUD.getScore(), timeRemaining, gameHUD.getFishEaten())) {
            isCompleted = true;
            // Можна додати логіку переходу на наступний рівень
        }

        if (checkLoseCondition(gameHUD.getScore(), timeRemaining, lives)) {
            isFailed = true;
            // Можна додати логіку кінця гри
        }
    }

    public void resetGame() {
        for (SwimmingFish fish : fishes) {
            fish.dispose();
        }
        fishes.clear();

        createInitialFishes();

        score = 0;
        lives = livesCount; // Скидаємо до кількості життів рівня
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;
        rotation = 0f;

        gameHUD.setCurrentGameLevel(levelNumber);
        gameHUD.setLevelParameters(timeLimit, targetFishCount);
        gameHUD.setCurrentLives(lives); // Оновлюємо життя в HUD
        gameHUD.resetTimer();
        gameHUD.updateLevelFishIcons(availableFish);
        
        // Оновлюємо розблоковані типи та видимі межі після ресету
        initializeUnlockedFishTypes();
        updateVisibleBoundsForAllFish();
        
        isCompleted = false;
        isFailed = false;
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

        // mainMenu тепер в Main.java
    }

    // Методи для переозначення в підкласах
    protected void updateLevelLogic(float deltaTime, float sharkX, float sharkY) {
        // Базовий рівень не має спеціальної логіки
    }

    protected void renderLevelSpecific(SpriteBatch batch) {
        // Базовий рівень не має спеціального рендерингу
    }

    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        return currentScore >= targetScore;
    }

    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        return timeRemaining <= 0 && currentScore < targetScore;
    }

    // Методи для роботи з рибками (з оригінального BasicLevel)
    public FishSpawnData getRandomFish() {
        if (availableFish.size == 0) return null;

        Array<FishSpawnData> availableForSpawn = new Array<>();
        for (FishSpawnData fishData : availableFish) {
            if (canSpawnFish(fishData)) {
                availableForSpawn.add(fishData);
            }
        }

        if (availableForSpawn.size == 0) return null;
        return availableForSpawn.random();
    }

    public boolean canSpawnFish(FishSpawnData fishData) {
        if (!fishData.hasFixedCount()) {
            return true;
        }
        int currentCount = getCurrentFishCount(fishData.path);
        return currentCount < fishData.fixedCount;
    }

    public int getCurrentFishCount(String fishPath) {
        return currentFishCounts.get(fishPath, 0);
    }

    public void addFish(String fishPath) {
        int currentCount = getCurrentFishCount(fishPath);
        currentFishCounts.put(fishPath, currentCount + 1);
    }

    public void removeFish(String fishPath) {
        int currentCount = getCurrentFishCount(fishPath);
        if (currentCount > 0) {
            currentFishCounts.put(fishPath, currentCount - 1);
        }
    }

    public float getFishSpeed(FishSpawnData fishData) {
        if (fishData.hasFixedSpeed()) {
            return fishData.fixedSpeed;
        } else {
            return MathUtils.random(minFishSpeed, maxFishSpeed);
        }
    }

    public float getFishScale(FishSpawnData fishData) {
        if (fishData.hasFixedScale()) {
            return fishData.fixedScale;
        } else {
            return MathUtils.random(minFishScale, maxFishScale);
        }
    }

    // Геттери
    public int getLevelNumber() { return levelNumber; }
    public float getTimeLimit() { return timeLimit; }
    public int getTargetScore() { return targetScore; }
    public int getMaxFishCount() { return maxFishCount; }
    public int getLivesCount() { return livesCount; }
    public int getCurrentLives() { return lives; }
    public float getSharkSpeed() { return sharkSpeed; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isFailed() { return isFailed; }
    public GameHUD getGameHUD() { return gameHUD; }

    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setFailed(boolean failed) { this.isFailed = failed; }

    public boolean shouldReturnToMainMenu() {
        return pauseMenu != null && pauseMenu.shouldReturnToMainMenu();
    }

    public void resetReturnToMainMenuFlag() {
        if (pauseMenu != null) {
            pauseMenu.resetReturnFlag();
        }
    }
    
    public Array<FishSpawnData> getAvailableFish() {
        return availableFish;
    }
    
    public int getTargetFishCount() {
        return targetFishCount;
    }

    private void initializeUnlockedFishTypes() {
        // На початку акула може їсти тільки перший тип рибок рівня
        unlockedFishTypes.clear();
        if (availableFish.size > 0) {
            unlockedFishTypes.add(availableFish.get(0).path);
        }
    }

    private void updateUnlockedFishTypes() {
        // Оновлюємо доступні типи рибок на основі рівня акули
        unlockedFishTypes.clear();
        
        // Акула може їсти стільки типів, скільки у неї рівень (максимум 3)
        int sharkLevel = gameHUD != null ? gameHUD.getSharkLevel() : 1;
        int maxUnlockableTypes = Math.min(sharkLevel, availableFish.size);
        
        for (int i = 0; i < maxUnlockableTypes; i++) {
            if (i < availableFish.size) {
                unlockedFishTypes.add(availableFish.get(i).path);
            }
        }
    }
    
    private void setVisibleBoundsForAllFish() {
        // Обчислюємо видимі межі (від 0 до висоти екрану мінус HUD)
        float screenHeight = Gdx.graphics.getHeight();
        float visibleMinY = 0f;
        float visibleMaxY = gameHUD != null ? screenHeight - gameHUD.getHudHeight() : screenHeight;
        
        // Встановлюємо межі для всіх існуючих рибок
        for (SwimmingFish fish : fishes) {
            fish.setVisibleBounds(visibleMinY, visibleMaxY);
        }
    }

    private void changeFinishedFishToNewType(SwimmingFish fish) {
        // Вибираємо випадковий тип з доступних для рівня
        FishSpawnData newFishData = availableFish.random();
        if (newFishData != null) {
            float newSpeed = getFishSpeed(newFishData);
            float newScale = getFishScale(newFishData);
            
            // Змінюємо тип рибки
            fish.changeType(newFishData.path, newFishData.frameCount, newSpeed, newScale, newFishData.frameDuration);
            
            // Встановлюємо видимі межі
            updateFishVisibleBounds(fish);
        }
    }

    private void updateFishVisibleBounds(SwimmingFish fish) {
        if (gameHUD != null) {
            float visibleMinY = 0f;
            float visibleMaxY = Gdx.graphics.getHeight() - gameHUD.getHudHeight();
            fish.setVisibleBounds(visibleMinY, visibleMaxY);
        }
    }
    
    private void updateVisibleBoundsForAllFish() {
        for (SwimmingFish fish : fishes) {
            updateFishVisibleBounds(fish);
        }
    }
}
