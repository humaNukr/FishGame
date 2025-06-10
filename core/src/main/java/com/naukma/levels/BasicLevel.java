package com.naukma.levels;

import com.naukma.bonuses.Bonus;
import com.naukma.bonuses.BonusManager;
import com.naukma.bonuses.EatingShark;
import com.naukma.bonuses.ShellBonus;
import com.naukma.effects.BloodEffect;
import com.naukma.ui.GameHUD;
import com.naukma.ui.PauseMenu;
import com.naukma.ui.GameOverMenu;
import com.naukma.ui.VictoryWindow;
import com.naukma.entities.SharkSprintHandler;
import com.naukma.world.ScrollingBackground;
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
import com.naukma.entities.SwimmingFish;
import com.naukma.entities.SwimmingShark;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.Pixmap;

public class BasicLevel extends ApplicationAdapter {


    public BasicLevel(int levelNumber) {
        this.levelNumber = levelNumber;
        this.availableFish = new Array<>();
        this.currentFishCounts = new ObjectMap<>();
        this.eatenFishCounts = new ObjectMap<>();
        this.victoryWindow = new VictoryWindow();
        this.unlockedFishTypes = new Array<>();
        initializeLevel();
        this.lives = this.livesCount; // Ініціалізуємо життя з налаштувань рівня
        initializeUnlockedFishTypes();
    }


    @Override
    public void create() {
        batch = new SpriteBatch();

        // Створюємо GameHUD з параметрами рівня
        gameHUD = new GameHUD();
        gameHUD.setCurrentGameLevel(levelNumber);
        sprintHandler = new SharkSprintHandler(gameHUD, sharkSpeed);

        // Встановлюємо параметри рівня в HUD (без targetFishCount)
        gameHUD.setLevelParameters(timeLimit, -1); // -1 означає що не показуємо цільову кількість риб
        // Життя передаються в render методі
        gameHUD.resetTimer();

        // Оновлюємо іконки рибок згідно з поточним рівнем
        gameHUD.updateLevelFishIcons(availableFish);

        scrollingBackground = new ScrollingBackground("output.jpg");
        shark = new Texture(Gdx.files.internal("shark/frame_00.png"));
        pauseMenu = new PauseMenu();
        gameOverMenu = new GameOverMenu();
        fishes = new Array<>();
        eatingShark = new EatingShark();
        swimmingShark = new SwimmingShark(0.4f); // Початковий розмір для 1-го рівня
        bloodEffect = new BloodEffect();
        bonusEffect = new BloodEffect("bonus_effect.png"); // Ефект для бонусів

        // Створюємо рибок використовуючи дані рівня
        createInitialFishes();

        // Ініціалізуємо систему бонусів
        bonusManager = new BonusManager(levelNumber);
        bonusManager.setWorldBounds(scrollingBackground.getWorldWidth(), scrollingBackground.getWorldHeight());

        // Оновлюємо розмір акули одразу при створенні
        updateSharkSize();

        // Початкова позиція акули в світових координатах (центр світу)
        sharkX = (scrollingBackground.getWorldWidth() - sharkWidth) / 2f;
        sharkY = (scrollingBackground.getWorldHeight() - sharkHeight) / 2f;

        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);

        // Створюємо білий піксель для фонів
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Вектор для перетворення координат
        tempVector = new Vector3();

        // Додаткові змінні для нової системи
        unlockedFishTypes = new Array<>();
        updateUnlockedFishTypes();

        // Встановлюємо видимі межі для всіх рибок (без HUD зверху)
        setVisibleBoundsForAllFish();
        updateBonusVisibleBounds();
    }

    protected void initializeLevel() {
        // За замовчуванням - базові налаштування
        timeLimit = 60f;
        targetScore = 200;
        targetFishCount = -1; // Не показуємо цільову кількість риб
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
                    createFish("fish_09/", 9);
                    break;
            }
        }
    }

    private void createRandomLevelFish() {
        if (availableFish.size == 0) return;

        // Створюємо список доступних типів з урахуванням обмежень
        Array<FishSpawnData> availableForSpawn = new Array<>();
        for (FishSpawnData fishData : availableFish) {
            if (canSpawnFish(fishData)) {
                // Додаємо тип кілька разів відповідно до його ймовірності
                int probability = Math.max(1, (int) (fishData.spawnWeight * 100));
                for (int i = 0; i < probability; i++) {
                    availableForSpawn.add(fishData);
                }
            }
        }

        // Якщо немає доступних типів, беремо найпростіший
        if (availableForSpawn.size == 0 && availableFish.size > 0) {
            availableForSpawn.add(availableFish.get(0));
        }

        // Створюємо рибку якщо є доступні типи
        if (availableForSpawn.size > 0) {
            FishSpawnData fishData = availableForSpawn.random();
            if (fishData != null) {
                createFishFromDataWithBounds(fishData);
            }
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
            case "fish_03/":
                return 0.16f;
            default:
                return 0.05f;
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!isGameOver && !showGameOverEffect) {
                isPaused = !isPaused;
                pauseMenu.setActive(isPaused);
            }
            return;
        }

        if (!isPaused && !isGameOver && !showGameOverEffect) {
            handleInput(Gdx.graphics.getDeltaTime());
            sprintHandler.handleInput();
            sprintHandler.updateSpeed();
            gameHUD.update(Gdx.graphics.getDeltaTime());
            eatingShark.update(Gdx.graphics.getDeltaTime());
            bloodEffect.update(Gdx.graphics.getDeltaTime());
            bonusEffect.update(Gdx.graphics.getDeltaTime());

            // Оновлюємо камеру відносно позиції акули
            scrollingBackground.updateCamera(sharkX, sharkY, sharkWidth, sharkHeight);

            checkCollisions();
            updateFishes(Gdx.graphics.getDeltaTime());
            checkLevelConditions();

            // Оновлюємо систему бонусів
            bonusManager.update(Gdx.graphics.getDeltaTime());
            checkBonusCollisions();
            updateSharkSize(); // Оновлюємо розмір акули
        } else if (isPaused) {
            handlePauseMenu();
        } else if (showGameOverEffect) {
            updateGameOverEffect(Gdx.graphics.getDeltaTime());
        } else if (isGameOver) {
            handleGameOverMenu();
        }

        if (victoryAnimationActive) {
            float delta = Gdx.graphics.getDeltaTime();
            sharkX += victorySpeedX * delta;
            sharkY += victorySpeedY * delta;

            if (victorySpeedX > 0) {
                victorySpeedX += victoryAcceleration * delta;
            } else {
                victorySpeedX -= victoryAcceleration * delta;
            }
            victorySpeedY -= victoryAcceleration * delta * 1.5f;

            rotation = MathUtils.atan2(victorySpeedY, victorySpeedX) * MathUtils.radiansToDegrees;

            // Перевіряємо, чи акула вийшла за межі екрану
            if (sharkX > scrollingBackground.getWorldWidth() + sharkWidth || sharkX < -sharkWidth || sharkY > scrollingBackground.getWorldHeight() + sharkHeight || sharkY < -sharkHeight) {
                victoryAnimationActive = false;
                isVictory = true; // Тепер можна показувати вікно перемоги
            }
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

        batch.setProjectionMatrix(scrollingBackground.getCamera().combined);
        batch.begin();

        scrollingBackground.render(batch);
        bonusManager.render(batch);

        for (SwimmingFish fish : fishes) {
            if (fish.isActive() && scrollingBackground.isInView(fish.getX(), fish.getY(), fish.getWidth(), fish.getHeight())) {
                fish.renderAt(batch, fish.getX(), fish.getY());
            }
        }

        bloodEffect.render(batch);
        bonusEffect.render(batch);

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
        } else if (!isVictory) {
            swimmingShark.renderAt(batch, sharkX, sharkY, rotation, rotation > 90 && rotation < 270);
        }

        // Рендеримо HUD та меню
        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        if (isGameOver) {
            gameOverMenu.render(batch);
        } else if (isPaused) {
            pauseMenu.render(batch);
        } else {
            int currentSharkLevel = calculateSharkLevel();
            String activeFishType = getCurrentActiveFishType();
            int currentFishEaten = activeFishType != null ? getEatenFishCount(activeFishType) : 0;
            int requiredFishForLevelUp = getRequiredFishForActiveFishType();
            gameHUD.render(batch, currentSharkLevel, currentFishEaten, lives, requiredFishForLevelUp);
        }

        if (victoryWindow.isActive()) {
            victoryWindow.render(batch);
        }

        batch.end();

        // Оновлюємо логіку вікна перемоги поза рендерингом
        if (isVictory && !victoryAnimationActive) {
            if (!victoryWindow.isActive()) {
                victoryWindow.show(levelNumber, score);
            }
        }

        if (victoryWindow.isActive()) {
            victoryWindow.handleInput();
            victoryWindow.update(Gdx.graphics.getDeltaTime());
            if (victoryWindow.isNextLevelRequested()) {
                isCompleted = true;
                victoryWindow.setActive(false);
            } else if (victoryWindow.isMenuRequested()) {
                isFailed = true;
                victoryWindow.setActive(false);
            }
        }
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

            float sharkSize = sharkWidth * sharkHeight;
            float fishSize = fishWidth * fishHeight;
            float sizeRatio = fishSize / sharkSize;

            if (sizeRatio < 0.3f) {
                if (distance < BASE_EATING_DISTANCE * swimmingShark.getScale() && canEatFishType(fish.getFishType())) {
                    eatFish(fish, fishFrontX, fishFrontY);
                }
            } else if (sizeRatio > 0.7f) {
                if (rectsOverlap(sharkX, sharkY, sharkWidth, sharkHeight,
                    fishX, fishY, fishWidth * 0.6f, fishHeight * 0.6f)) {
                    takeDamage(fish, fishFrontX, fishFrontY);
                }
            }
        }
    }

    private boolean rectsOverlap(float x1, float y1, float w1, float h1,
                                 float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 &&
            x1 + w1 > x2 &&
            y1 < y2 + h2 &&
            y1 + h1 > y2;
    }

    private void eatFish(SwimmingFish fish, float fishX, float fishY) {
        if (isVictory || pendingVictory || victoryAnimationActive) {
            return;
        }

        int oldLevel = calculateSharkLevel();

        FishSpawnData data = getFishDataFor(fish);
        if (data != null) {
            gameHUD.addScore(data.points);
        }
        score = gameHUD.getScore();

        eatenFishCounts.put(fish.getFishType(), eatenFishCounts.get(fish.getFishType(), 0) + 1);

        bloodEffect.spawn(fishX, fishY);
        fish.setActive(false);

        eatingShark.startEating();
        updateUnlockedFishTypes();

        int newLevel = calculateSharkLevel();
        if (newLevel > oldLevel) {
            gameHUD.triggerLevelUpEffect();
        }
    }

    private void takeDamage(SwimmingFish fish, float fishX, float fishY) {
        if (isPaused || isGameOver || showGameOverEffect || isVictory || pendingVictory || victoryAnimationActive) {
            return;
        }
        lives--;
        bloodEffect.spawn(fishX, fishY);
        fish.setActive(false);
        if (lives < 0) {
            triggerGameOver("Out of Lives!");
        }
    }

    private void triggerGameOver(String reason) {
        showGameOverEffect = true;
        gameOverEffectTimer = 0f;
        gameOverMenu.setGameOverReason(reason);
        // Після закінчення ефекту буде показано меню
    }

    // Метод для перевірки чи може акула з'їсти цей тип рибки
    private boolean canEatFishType(String fishType) {
        return unlockedFishTypes.contains(fishType, false);
    }

    private void handleInput(float delta) {
        if (isPaused || isGameOver || showGameOverEffect || victoryAnimationActive) {
            return;
        }
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

        float sharkCurrentSpeed = sprintHandler.getCurrentSpeed();
        if (distance > 10) {
            float moveX = dirX / distance * sharkCurrentSpeed * delta;
            float moveY = dirY / distance * sharkCurrentSpeed * delta;

            sharkX += moveX;
            sharkY += moveY;
        }

        // Обмеження межами світу
        if (sharkX < 0) sharkX = 0;
        if (sharkX > scrollingBackground.getWorldWidth() - sharkWidth)
            sharkX = scrollingBackground.getWorldWidth() - sharkWidth;

        if (sharkY < 0) sharkY = 0;
        if (rotation > 200 && rotation < 340) {
            if (sharkY < sharkHeight / 1.2f) sharkY = sharkHeight / 1.2f;
        }
        if (rotation < 170 && rotation > 10) {
            if (sharkY > scrollingBackground.getWorldHeight() - gameHUD.getHudHeight() - sharkHeight * 1.2f)
                sharkY = scrollingBackground.getWorldHeight() - gameHUD.getHudHeight() - sharkHeight * 1.2f;
        }
        if (sharkY > scrollingBackground.getWorldHeight() - gameHUD.getHudHeight() - sharkHeight / 1.2f) {
            sharkY = scrollingBackground.getWorldHeight() - gameHUD.getHudHeight() - sharkHeight / 1.2f;
        }
    }

    private void checkLevelConditions() {
        if (isVictory || isGameOver || victoryAnimationActive) return; // Якщо вже є результат, нічого не робимо

        float timeRemaining = gameHUD.getTimeRemaining();

        // Перевіряємо умову перемоги
        if (checkWinCondition(score, timeRemaining, getTotalEatenFishCount())) {
            victoryAnimationActive = true;
            if (sharkX < scrollingBackground.getWorldWidth() / 2) {
                victorySpeedX = 200; // Початкова швидкість вправо
            } else {
                victorySpeedX = -200; // Початкова швидкість вліво
            }
            victorySpeedY = 300; // Початкова вертикальна швидкість для "сальто"
            return;
        }

        // Перевіряємо умову поразки
        if (checkLoseCondition(score, timeRemaining, lives)) {
            triggerGameOver("Out of Lives!");
        } else if (timeRemaining <= 0) {
            triggerGameOver("Time's Up!");
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
        gameHUD.setLevelParameters(timeLimit, -1); // Не показуємо цільову кількість риб
        // Життя передаються в render методі
        gameHUD.resetTimer();
        gameHUD.updateLevelFishIcons(availableFish);

        // Оновлюємо розблоковані типи та видимі межі після ресету
        initializeUnlockedFishTypes();
        updateVisibleBoundsForAllFish();
        updateBonusVisibleBounds();

        // Скидаємо систему бонусів
        if (bonusManager != null) {
            bonusManager.reset();
        }

        // Скидаємо стани Game Over
        isGameOver = false;
        showGameOverEffect = false;
        gameOverEffectTimer = 0f;
        gameOverMenu.setActive(false);

        // Скидаємо лічильник з'їдених риб
        eatenFishCounts.clear();

        isCompleted = false;
        isFailed = false;
        isVictory = false; // Скидаємо прапор перемоги
    }

    @Override
    public void dispose() {
        batch.dispose();
        scrollingBackground.dispose();
        shark.dispose();
        whitePixel.dispose();
        for (SwimmingFish fish : fishes) {
            fish.dispose();
        }
        font.dispose();
        pauseMenu.dispose();
        gameOverMenu.dispose();
        eatingShark.dispose();
        bloodEffect.dispose();
        bonusEffect.dispose(); // Очищуємо ефект бонусів
        gameHUD.dispose();
        swimmingShark.dispose();

        // Очищуємо систему бонусів
        if (bonusManager != null) {
            bonusManager.dispose();
        }

        // mainMenu тепер в Main.java
    }


    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        return calculateSharkLevel() > availableFish.size;
    }

    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        return lives < 0 || timeRemaining <= 0;
    }

    // Методи для роботи з Game Over

    public boolean shouldReturnToMainMenuFromGameOver() {
        return gameOverMenu != null && gameOverMenu.shouldReturnToMainMenu();
    }

    public boolean shouldExitGameFromGameOver() {
        return gameOverMenu != null && gameOverMenu.shouldExitGame();
    }

    public void resetGameOverFlags() {
        if (gameOverMenu != null) {
            gameOverMenu.resetFlags();
        }
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
    public int getLevelNumber() {
        return levelNumber;
    }

    public float getTimeLimit() {
        return timeLimit;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public int getMaxFishCount() {
        return maxFishCount;
    }

    public int getLivesCount() {
        return livesCount;
    }

    public int getCurrentLives() {
        return lives;
    }

    public float getSharkSpeed() {
        return sharkSpeed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public GameHUD getGameHUD() {
        return gameHUD;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public void setFailed(boolean failed) {
        this.isFailed = failed;
    }

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

    // Методи для роботи з з'їденими рибами
    public int getEatenFishCount(String fishPath) {
        return eatenFishCounts.get(fishPath, 0);
    }

    public ObjectMap<String, Integer> getAllEatenFishCounts() {
        return eatenFishCounts;
    }

    // Новий метод для підрахунку загальної кількості з'їдених риб
    private int getTotalEatenFishCount() {
        int total = 0;
        for (Integer count : eatenFishCounts.values()) {
            total += count;
        }
        return total;
    }

    // Метод для отримання поточного активного типу риби
    private String getCurrentActiveFishType() {
        // Перевіряємо всі типи риби послідовно (незалежно від sharkLevel)
        for (int i = 0; i < availableFish.size; i++) {
            String fishType = availableFish.get(i).path;
            int eatenCount = getEatenFishCount(fishType);
            int required = getFishUnlockRequirement(i);

            // Якщо цей тип ще не завершений - він активний
            if (eatenCount < required) {
                return fishType;
            }
        }

        // Якщо всі типи завершені - повертаємо останній
        if (availableFish.size > 0) {
            return availableFish.get(availableFish.size - 1).path;
        }

        return null;
    }

    // Новий метод для обчислення рівня акули на основі завершених типів риб
    protected int calculateSharkLevel() {
        int completedTypes = 0;

        // Підраховуємо скільки типів риб повністю завершено
        for (int i = 0; i < availableFish.size; i++) {
            String fishType = availableFish.get(i).path;
            int eatenCount = getEatenFishCount(fishType);
            int required = getFishUnlockRequirement(i);

            if (eatenCount >= required) {
                completedTypes++;
            } else {
                break; // Зупиняємося на першому незавершеному типі
            }
        }

        // Рівень = кількість завершених типів + 1
        return completedTypes + 1;
    }

    private void initializeUnlockedFishTypes() {
        // На початку акула може їсти тільки перший тип рибок рівня
        unlockedFishTypes.clear();
        if (availableFish.size > 0) {
            unlockedFishTypes.add(availableFish.get(0).path);
        }
    }

    private void updateUnlockedFishTypes() {
        // Оновлюємо доступні типи рибок на основі завершених попередніх типів
        unlockedFishTypes.clear();

        // Завжди додаємо перший тип (він доступний з початку)
        if (availableFish.size > 0) {
            unlockedFishTypes.add(availableFish.get(0).path);
        }

        // Додаємо наступні типи тільки якщо попередні завершені
        for (int i = 1; i < availableFish.size; i++) {
            String previousFishType = availableFish.get(i - 1).path;
            int previousEaten = getEatenFishCount(previousFishType);
            int previousRequired = getFishUnlockRequirement(i - 1);

            // Якщо попередній тип завершений - розблоковуємо наступний
            if (previousEaten >= previousRequired) {
                unlockedFishTypes.add(availableFish.get(i).path);
            } else {
                break; // Зупиняємося якщо попередній тип не завершений
            }
        }
    }

    // Метод для переозначення в підкласах - скільки риб треба з'їсти для розблокування наступного типу
    protected int getFishUnlockRequirement(int fishTypeIndex) {
        // Базове значення - можна переозначити в підкласах
        switch (fishTypeIndex) {
            case 0:
                return 10; // Після 10 перших риб розблоковується другий тип
            case 1:
                return 5;  // Після 5 других риб розблоковується третій тип
            case 2:
                return 3;  // Після 3 третіх риб (якщо є 4-й тип)
            default:
                return 5;
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
        // Спочатку видаляємо стару рибку з лічильника
        String oldFishType = fish.getFishType();
        if (oldFishType != null) {
            removeFish(oldFishType);
        }

        // Створюємо список доступних типів з урахуванням обмежень
        Array<FishSpawnData> availableForSpawn = new Array<>();
        for (FishSpawnData fishData : availableFish) {
            if (canSpawnFish(fishData)) {
                // Додаємо тип кілька разів відповідно до його ймовірності
                int probability = Math.max(1, (int) (fishData.spawnWeight * 100));
                for (int i = 0; i < probability; i++) {
                    availableForSpawn.add(fishData);
                }
            }
        }

        // Якщо немає доступних типів, намагаємося знайти хоча б один
        if (availableForSpawn.size == 0) {
            // Повертаємо найпростіший тип або перший доступний
            if (availableFish.size > 0) {
                availableForSpawn.add(availableFish.get(0));
            } else {
                // Якщо взагалі немає типів, залишаємо рибку неактивною
                fish.setActive(false);
                return;
            }
        }

        // Вибираємо випадковий тип з доступних
        FishSpawnData newFishData = availableForSpawn.random();
        if (newFishData != null) {
            float newSpeed = getFishSpeed(newFishData);
            float newScale = getFishScale(newFishData);

            // Змінюємо тип рибки
            fish.changeType(
                newFishData.path,
                newFishData.frameCount,
                newSpeed,
                newScale,
                newFishData.frameDuration
            );

            // Встановлюємо видимі межі
            updateFishVisibleBounds(fish);

            // Додаємо нову рибку до лічильника
            addFish(newFishData.path);
        } else {
            // Якщо не вдалося створити, деактивуємо рибку
            fish.setActive(false);
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

    private void updateBonusVisibleBounds() {
        if (bonusManager != null && gameHUD != null) {
            float visibleMinY = 0f;
            float visibleMaxY = Gdx.graphics.getHeight() - gameHUD.getHudHeight();
            bonusManager.setVisibleBounds(visibleMinY, visibleMaxY);
        }
    }

    private void updateGameOverEffect(float deltaTime) {
        gameOverEffectTimer += deltaTime;
        if (gameOverEffectTimer >= GAME_OVER_EFFECT_DURATION) {
            showGameOverEffect = false;
            isGameOver = true;
            gameOverMenu.setActive(true);
            gameOverEffectTimer = 0f;
        }
    }

    private void handleGameOverMenu() {
        gameOverMenu.handleInput();

        if (!gameOverMenu.isActive()) {
            isGameOver = false;
            showGameOverEffect = false;
            resetGame();
        }

        if (gameOverMenu.shouldRestart()) {
            resetGame();
            isGameOver = false;
            gameOverMenu.resetFlags();
        }

    }

    private void checkBonusCollisions() {
        if (eatingShark.isEating()) return;

        Bonus collectedBonus = bonusManager.checkCollisions(sharkX, sharkY, sharkWidth, sharkHeight);
        if (collectedBonus != null) {
            // Для ракушки - з'їдаємо тільки перлину, ракушка залишається
            if (collectedBonus instanceof ShellBonus) {
                ShellBonus shell = (ShellBonus) collectedBonus;
                if (shell.isOpen() && shell.hasPearl()) {
                    // Запускаємо анімацію їжі акули (з'їдаємо перлину)
                    eatingShark.startEating();

                    // Збираємо бонус (тільки перлину)
                    bonusManager.collectBonus(collectedBonus, gameHUD);

                    // Додаємо життя за перлину
                    lives++;

                    // Ефект бонусу для перлини
                    final float bonusX = collectedBonus.getX() + collectedBonus.getWidth() / 2;
                    final float bonusY = collectedBonus.getY() + collectedBonus.getHeight() / 2;

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            bonusEffect.spawn(bonusX, bonusY);
                        }
                    }, BLOOD_EFFECT_DELAY);
                }
            } else {
                // Для інших бонусів - з'їдаємо повністю
                eatingShark.startEating();
                bonusManager.collectBonus(collectedBonus, gameHUD);

                // Ефект бонусу
                final float bonusX = collectedBonus.getX() + collectedBonus.getWidth() / 2;
                final float bonusY = collectedBonus.getY() + collectedBonus.getHeight() / 2;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        bonusEffect.spawn(bonusX, bonusY);
                    }
                }, BLOOD_EFFECT_DELAY);
            }
        }
    }

    // Метод для отримання кількості риб потрібної для левел апу активного типу
    private int getRequiredFishForActiveFishType() {
        // Знаходимо індекс активного типу риби
        for (int i = 0; i < availableFish.size; i++) {
            String fishType = availableFish.get(i).path;
            int eatenCount = getEatenFishCount(fishType);
            int required = getFishUnlockRequirement(i);

            // Якщо цей тип ще не завершений - повертаємо його вимогу
            if (eatenCount < required) {
                return required;
            }
        }

        // Якщо всі типи завершені - повертаємо вимогу для останнього типу
        if (availableFish.size > 0) {
            return getFishUnlockRequirement(availableFish.size - 1);
        }

        return 10; // Значення за замовчуванням
    }

    private FishSpawnData getFishDataFor(SwimmingFish fish) {
        String fishType = fish.getFishType();
        if (fishType == null) return null;
        for (FishSpawnData data : availableFish) {
            if (data.path.equals(fishType)) {
                return data;
            }
        }
        return null;
    }

    private void updateSharkSize() {
        int level = calculateSharkLevel();
        float newScale;

        switch (level) {
            case 1:
                newScale = 0.5f;
                break;
            case 2:
                newScale = 1.3f;
                break;
            case 3:
                newScale = 1.7f;
                break;
            default:
                newScale = 0.6f;
                break;
        }

        if (swimmingShark.getScale() != newScale) {
            swimmingShark.setScale(newScale);
            // Оновлюємо розміри в BasicLevel, які використовуються для колізій
            this.sharkWidth = swimmingShark.getWidth();
            this.sharkHeight = swimmingShark.getHeight();
        }
    }

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
    protected ObjectMap<String, Integer> eatenFishCounts; // Лічильник з'їдених риб кожного типу

    // Ігрові об'єкти
    private SpriteBatch batch;
    private ScrollingBackground scrollingBackground;
    private Texture shark;
    private Array<SwimmingFish> fishes;
    private EatingShark eatingShark;
    private SwimmingShark swimmingShark;
    private BloodEffect bloodEffect;
    private BloodEffect bonusEffect; // Ефект для бонусів
    protected GameHUD gameHUD;

    // Позиція і характеристики акули
    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float rotation = 0f;

    // Константи
    private static final int TOTAL_FISH_TYPES = 8;
    private static final float BLOOD_EFFECT_DELAY = 0.55f;
    private static final float BASE_EATING_DISTANCE = 50f;

    // Додаткові змінні
    private BitmapFont font;
    private Texture whitePixel; // Білий піксель для фонів
    private int score = 0;
    private int lives = 3; // Поточні життя гравця
    private PauseMenu pauseMenu;
    private GameOverMenu gameOverMenu;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean showGameOverEffect = false;
    private float gameOverEffectTimer = 0f;
    private static final float GAME_OVER_EFFECT_DURATION = 2f; // 2 секунди
    private Vector3 tempVector;
    private SharkSprintHandler sprintHandler;

    // Додаткові змінні для нової системи
    private Array<String> unlockedFishTypes; // Розблоковані типи рибок для акули

    // Bonus system
    private BonusManager bonusManager;

    private boolean victoryAnimationActive = false;
    private float victorySpeedX = 0;
    private float victorySpeedY = 0;
    private float victoryAcceleration = 400f;

    // Додаємо поля для затримки перемоги (залишаємо тільки один раз!)
    private boolean pendingVictory = false;
    private boolean isVictory = false; // Новий прапор, що сигналізує про перемогу

    private VictoryWindow victoryWindow;
}
