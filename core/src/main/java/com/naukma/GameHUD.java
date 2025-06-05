package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GameHUD {
    // Fonts
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont levelFont;

    // Game data
    private int score;
    private int targetScore;
    private int sharkLevel;
    private float experienceProgress;
    private float displayedProgress;
    private int fishEaten; // Кількість з'їдених рибок

    // Current game level and timer
    private int currentGameLevel; // Поточний рівень гри (1, 2, 3...)
    private float gameTimer; // Таймер гри в секундах
    private boolean timerActive; // Чи активний таймер

    // Stamina system
    private float stamina;
    private float maxStamina;
    private static final float STAMINA_REGENERATION_RATE = 20f; // відновлення в секунду

    // UI elements
    private Array<Texture> fishIcons;
    private Array<Texture> bonusIcons;
    private Array<Integer> bonusCounts; // Лічильники бонусів
    private GlyphLayout glyphLayout;
    private Texture hudBackground;
    private Texture progressBarBg;
    private Texture progressBarFill;
    private Texture gameLogo; // Логотип гри

    // Layout variables - all adaptive to screen size
    private float screenWidth;
    private float screenHeight;
    private float hudHeight;
    private float padding;
    private float iconSize;
    private float iconSpacing;
    private float progressBarHeight;
    private float bonusIconSize;
    private float logoSize; // Розмір логотипу
    private float titleFontScale;
    private float scoreFontScale;
    private float levelFontScale;

        // Level up effect
    private float levelUpEffectTimer;
    private boolean showLevelUpEffect;
    private Color levelUpColor;

    // Constants
    private static final int MAX_SHARK_LEVEL = 3;
    private static final int FISH_TO_LEVEL_UP = 20; // Кількість рибок для левелапу
    private static final float EXP_TO_LEVEL_UP = 200f; // Залишаємо для відображення але не використовуємо
    private static final float PROGRESS_ANIMATION_SPEED = 120f;
    private static final float SCORE_ANIMATION_SPEED = 1000f;
    private static final float LEVEL_UP_EFFECT_DURATION = 3f;

    // Screen size ratios for adaptive design
    private static final float HUD_HEIGHT_RATIO = 0.15f;
    private static final float PADDING_RATIO = 0.02f;
    private static final float ICON_SIZE_RATIO = 0.08f;
    private static final float ICON_SPACING_RATIO = 0.015f;
    private static final float PROGRESS_BAR_HEIGHT_RATIO = 0.012f;
    private static final float BONUS_ICON_RATIO = 0.05f;

    public GameHUD() {
        updateScreenDimensions();
        initializeFonts();
        initializeVariables();
        loadTextures();
        createProgressBarTextures();
        loadFishIcons();
        loadBonusIcons();
    }

    private void updateScreenDimensions() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        // Calculate adaptive sizes based on screen dimensions
        hudHeight = screenHeight * HUD_HEIGHT_RATIO;
        padding = Math.max(20f, Math.min(screenWidth, screenHeight) * PADDING_RATIO);
        iconSize = screenHeight * ICON_SIZE_RATIO;
        iconSpacing = screenHeight * ICON_SPACING_RATIO;
        progressBarHeight = Math.max(15f, screenHeight * PROGRESS_BAR_HEIGHT_RATIO);
        bonusIconSize = screenHeight * BONUS_ICON_RATIO;

        // Font scales based on screen size - збільшуємо мінімальні розміри
        float baseScale = Math.min(screenWidth / 2560f, screenHeight / 1600f);
        titleFontScale = Math.max(2.0f, 3.0f * baseScale); // Збільшено
        scoreFontScale = Math.max(1.8f, 2.5f * baseScale); // Збільшено
        levelFontScale = Math.max(1.5f, 2.0f * baseScale); // Збільшено

        // Logo size - на всю висоту HUD'у з невеликими відступами
        logoSize = hudHeight - padding;
    }

    private void initializeFonts() {
        // Поки що використовуємо стандартний шрифт, але з більшими розмірами
        // Для TTF потрібно додати FreeType dependency в build.gradle

        titleFont = new BitmapFont();
        titleFont.getData().setScale(titleFontScale * 1.2f); // Збільшуємо ще більше
        titleFont.setColor(1f, 1f, 0f, 1f); // Яскраво-жовтий

        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(scoreFontScale * 1.2f);
        scoreFont.setColor(1f, 1f, 1f, 1f); // Білий

        levelFont = new BitmapFont();
        levelFont.getData().setScale(levelFontScale * 1f);
        levelFont.setColor(0f, 1f, 1f, 1f); // Ціан

        glyphLayout = new GlyphLayout();
        levelUpColor = new Color(1f, 1f, 0f, 1f); // Яскраво-жовтий
    }

    private void initializeVariables() {
        score = 0;
        targetScore = 0;
        sharkLevel = 1;
        experienceProgress = 0;
        displayedProgress = 0;
        fishEaten = 0; // Ініціалізуємо лічильник рибок

        // Ініціалізуємо нові змінні
        currentGameLevel = 1; // За замовчуванням перший рівень
        gameTimer = 0f; // Початковий час
        timerActive = true; // Таймер активний

        levelUpEffectTimer = 0;
        showLevelUpEffect = false;

        // Ініціалізуємо stamina
        maxStamina = 100f;
        stamina = maxStamina;

        fishIcons = new Array<>();
        bonusIcons = new Array<>();
        bonusCounts = new Array<>();

        // Ініціалізуємо лічильники бонусів з 0
        for (int i = 0; i < 3; i++) {
            bonusCounts.add(0); // Дефолт 0 замість тестових значень
        }
    }

    private void loadTextures() {

        hudBackground = new Texture(Gdx.files.internal("hud_background.png"));
        gameLogo = new Texture(Gdx.files.internal("game_logo.png"));

    }

    private void createProgressBarTextures() {
        // Background
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        progressBarBg = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Fill with gradient
        Pixmap fillPixmap = new Pixmap(100, 1, Pixmap.Format.RGBA8888);
        for (int x = 0; x < 100; x++) {
            float ratio = x / 100f;
            fillPixmap.setColor(0.1f + ratio * 0.4f, 0.5f + ratio * 0.3f, 0.9f - ratio * 0.2f, 1f);
            fillPixmap.drawPixel(x, 0);
        }
        progressBarFill = new Texture(fillPixmap);
        fillPixmap.dispose();
    }

    private void loadFishIcons() {
        for (int i = 1; i <= MAX_SHARK_LEVEL; i++) {
            String fishPath = String.format("fish_%02d/frame_00.png", i);
            fishIcons.add(new Texture(Gdx.files.internal(fishPath)));
        }
    }

    private void loadBonusIcons() {
        for (int i = 1; i <= 3; i++) {
            String bonusPath = String.format("bonus%d.png", i);
            bonusIcons.add(new Texture(Gdx.files.internal(bonusPath)));
        }
    }

    public void update(float deltaTime) {
        // Update score animation
        if (score < targetScore) {
            score = (int) Math.min(score + SCORE_ANIMATION_SPEED * deltaTime, targetScore);
        }

        // Update progress animation
        if (displayedProgress < experienceProgress) {
            displayedProgress = Math.min(displayedProgress + PROGRESS_ANIMATION_SPEED * deltaTime,
                    experienceProgress);
        }

        // Update level up effect
        if (showLevelUpEffect) {
            levelUpEffectTimer -= deltaTime;
            if (levelUpEffectTimer <= 0) {
                showLevelUpEffect = false;
            }
        }

                // Update game timer
        if (timerActive) {
            gameTimer += deltaTime;
        }

        // Update stamina regeneration
        if (stamina < maxStamina) {
            stamina = Math.min(maxStamina, stamina + STAMINA_REGENERATION_RATE * deltaTime);
        }
    }

    public void render(SpriteBatch batch) {
        // Check if screen size changed and update accordingly
        if (screenWidth != Gdx.graphics.getWidth() || screenHeight != Gdx.graphics.getHeight()) {
            updateScreenDimensions();
            titleFont.getData().setScale(titleFontScale);
            scoreFont.getData().setScale(scoreFontScale);
            levelFont.getData().setScale(levelFontScale);
        }

        renderBackground(batch);
        renderGameLogo(batch);
        renderScore(batch);
        renderLevelIcons(batch);
        renderAdditionalElements(batch);
        renderBonusSection(batch);
        renderLevelUpEffect(batch);
        renderTimer(batch);
    }

    private void renderBackground(SpriteBatch batch) {
        batch.draw(hudBackground,
        -screenWidth*0.02f,
        screenHeight - hudHeight+screenHeight*0.02f,
        screenWidth*1.04f,
        hudHeight);
    }

    // Метод для рендерингу тексту з контуром
    private void drawTextWithOutline(SpriteBatch batch, BitmapFont font, String text, float x, float y, Color textColor, Color outlineColor) {
        Color originalColor = font.getColor();

        // Рендеримо контур (8 напрямків)
        font.setColor(outlineColor);
        font.draw(batch, text, x - 2, y);     // ліворуч
        font.draw(batch, text, x + 2, y);     // праворуч
        font.draw(batch, text, x, y - 2);     // вниз
        font.draw(batch, text, x, y + 2);     // вгору
        font.draw(batch, text, x - 1, y - 1); // діагональ лів-вниз
        font.draw(batch, text, x + 1, y - 1); // діагональ прав-вниз
        font.draw(batch, text, x - 1, y + 1); // діагональ лів-вгору
        font.draw(batch, text, x + 1, y + 1); // діагональ прав-вгору

        // Рендеримо основний текст
        font.setColor(textColor);
        font.draw(batch, text, x, y);

        // Відновлюємо оригінальний колір
        font.setColor(originalColor);
    }


    private void renderGameLogo(SpriteBatch batch) {
        // Розміщуємо великий логотип по центру зверху HUD'у
        float logoX = (screenWidth - logoSize) / 2;

        // Позиція зверху HUD'у з невеликим відступом
        float logoY = screenHeight - hudHeight + padding/2;

        // Рендеримо логотип
        Color originalColor = batch.getColor();
        batch.setColor(1f, 1f, 1f, 1.0f); // Повна непрозорість для великого логотипу
        batch.draw(gameLogo, logoX, logoY, logoSize, logoSize);
        batch.setColor(originalColor); // Відновлюємо оригінальний колір
    }

    private void renderScore(SpriteBatch batch) {
        String scoreText = String.format("Score: %,d", score);
        glyphLayout.setText(scoreFont, scoreText);

        // Розміщуємо score зліва від логотипу, в тій же висоті
        float scoreX = padding*6f;
        float scoreY = screenHeight - hudHeight/2 + padding*1.5f;

        // Рендеримо з чорним контуром
        drawTextWithOutline(batch, scoreFont, scoreText, scoreX, scoreY,
                           new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));

        // Додаємо відображення поточного рівня гри під score
        String levelText = String.format("Level: %d", currentGameLevel);
        glyphLayout.setText(levelFont, levelText);

        float levelX = scoreX;
        float levelY = scoreY - padding*2.5f; // Розміщуємо під score з відступом

        drawTextWithOutline(batch, levelFont, levelText, levelX, levelY,
                           new Color(0f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f)); // Ціан з чорним контуром
    }

    private void renderLevelIcons(SpriteBatch batch) {
        // Починаємо з 25% ширини екрану зліва
        float startX = screenWidth * 0.25f;

        // Піднімаємо рибок оптимально для видимості всіх елементів
        float iconsY = screenHeight - hudHeight + padding * 3f;

        for (int i = 0; i < MAX_SHARK_LEVEL; i++) {
            float x = startX + (iconSize + iconSpacing) * i;
            boolean isLocked = i + 1 > sharkLevel;
            boolean isCurrent = i + 1 == sharkLevel;


            if (isLocked) {
                batch.setColor(0.4f, 0.4f, 0.4f, 0.6f);
            } else if (isCurrent) {

                float pulse = MathUtils.sin(System.currentTimeMillis() * 0.005f) * 0.2f + 0.8f;
                batch.setColor(1f, 1f, pulse, 1f);
            }

            // Draw fish icon
            batch.draw(fishIcons.get(i), x, iconsY, iconSize, iconSize);

            // Draw progress bar for current level (опускаємо трішки нижче)
            if (isCurrent && !isMaxLevel()) {
                renderProgressBar(batch, x, iconsY - progressBarHeight - padding);
            }

            batch.setColor(Color.WHITE);
        }
    }

    private void renderProgressBar(SpriteBatch batch, float x, float y) {
        float barWidth = iconSize;

        // Draw background
        batch.draw(progressBarBg, x, y, barWidth, progressBarHeight);

        // Draw fill
        float fillWidth = (displayedProgress / FISH_TO_LEVEL_UP) * barWidth;
        if (fillWidth > 0) {
            batch.draw(progressBarFill, x, y, fillWidth, progressBarHeight);
        }

        // Draw progress text з контуром - збільшуємо відстань до шкали
        String progressText = String.format("%d/%d fish", (int)displayedProgress, FISH_TO_LEVEL_UP);
        glyphLayout.setText(levelFont, progressText);
        float textX = x + (barWidth - glyphLayout.width) / 2;
        float textY = y + progressBarHeight + glyphLayout.height + padding/2; // Додаємо відступ між текстом і шкалою

        drawTextWithOutline(batch, levelFont, progressText, textX, textY,
                           new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
    }

    private void renderAdditionalElements(SpriteBatch batch) {
        // STAMINA element (лівіше від бонусів, щоб не налазило)
        String staminaText = "Stamina";
        glyphLayout.setText(levelFont, staminaText);

        // Розміщуємо на 65% ширини екрану (бонуси на 75%, тому маємо запас)
        float staminaX = screenWidth * 0.65f - glyphLayout.width/2;

        // Центруємо по вертикалі відносно HUD'у
        float staminaY = screenHeight - hudHeight/2 + glyphLayout.height/2;

        drawTextWithOutline(batch, levelFont, staminaText, staminaX, staminaY,
                           new Color(0f, 1f, 0.5f, 1f), new Color(0f, 0f, 0f, 1f)); // Зелений

        // Прогрес бар для STAMINA (під текстом)
        float staminaBarX = staminaX;
        float staminaBarY = staminaY - padding * 2.5f; // Опускаємо нижче
        float staminaBarWidth = glyphLayout.width;

        batch.draw(progressBarBg, staminaBarX, staminaBarY, staminaBarWidth, progressBarHeight);

        // Заповнення бару залежно від поточної stamina
        float staminaProgress = stamina / maxStamina;
        if (staminaProgress > 0) {
            batch.draw(progressBarFill, staminaBarX, staminaBarY, staminaBarWidth * staminaProgress, progressBarHeight);
        }
    }

    private void renderBonusSection(SpriteBatch batch) {
        if (bonusIcons.size == 0) return;

        // Збільшуємо розмір бонусів
        float largeBonusSize = bonusIconSize * 1.3f;

        float bonusStartX = screenWidth * 0.75f;

        // Центруємо бонуси по вертикалі відносно HUD'у
        float bonusY = screenHeight - hudHeight/2 - largeBonusSize/2;

        for (int i = 0; i < bonusIcons.size; i++) {
            float bonusX = bonusStartX + i * (largeBonusSize + iconSpacing * 2);

            // Рендеримо бонус іконку збільшеною
            batch.draw(bonusIcons.get(i), bonusX, bonusY, largeBonusSize, largeBonusSize);

            if (i < bonusCounts.size) {
                String countText = "x" + bonusCounts.get(i);
                glyphLayout.setText(levelFont, countText);

                float countX = bonusX + (largeBonusSize - glyphLayout.width) / 2;
                // Піднімаємо текст ближче до іконки
                float countY = bonusY + padding/4;

                drawTextWithOutline(batch, levelFont, countText, countX, countY,
                                   new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
            }
        }
    }

    private void renderLevelUpEffect(SpriteBatch batch) {
        if (showLevelUpEffect) {
            float alpha = MathUtils.sin(levelUpEffectTimer * 8) * 0.5f + 0.5f;

            String levelUpText = "LEVEL " + sharkLevel + " UNLOCKED!";
            glyphLayout.setText(titleFont, levelUpText);

            float x = (screenWidth - glyphLayout.width) / 2;
            float y = screenHeight / 2;

            Color animatedColor = new Color(1f, 1f, 0f, alpha);
            Color outlineColor = new Color(0f, 0f, 0f, alpha);

            drawTextWithOutline(batch, titleFont, levelUpText, x, y, animatedColor, outlineColor);
        }
    }

    private void renderTimer(SpriteBatch batch) {
        // Форматуємо час у вигляді хв:сек
        int minutes = (int)(gameTimer / 60);
        int seconds = (int)(gameTimer % 60);
        String timerText = String.format("Time: %02d:%02d", minutes, seconds);


        glyphLayout.setText(scoreFont, timerText);

        // Розміщуємо у правій частині HUD'у
        float timerX = screenWidth - glyphLayout.width - padding*6f;
        float timerY = screenHeight - hudHeight/2 + padding*3f;

        // Рендеримо з чорним контуром
        drawTextWithOutline(batch, scoreFont, timerText, timerX, timerY,
                           new Color(1f, 1f, 0f, 1f), new Color(0f, 0f, 0f, 1f)); // Жовтий з чорним контуром
    }

    // Game logic methods
    public void addScore(int points) {
        targetScore += points;
    }

    // Новий метод для додавання з'їденої рибки
    public void addFishEaten() {
        fishEaten++;
        experienceProgress = fishEaten; // Використовуємо experienceProgress для анімації
        displayedProgress = Math.min(displayedProgress, experienceProgress); // Оновлюємо відображення
        checkLevelUp();
    }

    private void checkLevelUp() {
        if (fishEaten >= FISH_TO_LEVEL_UP && sharkLevel < MAX_SHARK_LEVEL) {
            levelUp();
        }
    }

    private void levelUp() {
        fishEaten = 0; // Скидаємо лічильник рибок
        experienceProgress = 0;
        displayedProgress = 0;
        sharkLevel++;
        showLevelUpEffect = true;
        levelUpEffectTimer = LEVEL_UP_EFFECT_DURATION;
    }

    // Методи для керування stamina
    public void useStamina(float amount) {
        stamina = Math.max(0, stamina - amount);
    }

    public void restoreStamina(float amount) {
        stamina = Math.min(maxStamina, stamina + amount);
    }

    public boolean hasStamina(float amount) {
        return stamina >= amount;
    }

    public float getStamina() {
        return stamina;
    }

    public float getMaxStamina() {
        return maxStamina;
    }

    public float getStaminaPercentage() {
        return stamina / maxStamina;
    }

    // Методи для керування бонусами
    public void addBonus(int bonusType, int amount) {
        if (bonusType >= 0 && bonusType < bonusCounts.size) {
            bonusCounts.set(bonusType, bonusCounts.get(bonusType) + amount);
        }
    }

    public void useBonus(int bonusType, int amount) {
        if (bonusType >= 0 && bonusType < bonusCounts.size) {
            int currentCount = bonusCounts.get(bonusType);
            bonusCounts.set(bonusType, Math.max(0, currentCount - amount));
        }
    }

    public int getBonusCount(int bonusType) {
        if (bonusType >= 0 && bonusType < bonusCounts.size) {
            return bonusCounts.get(bonusType);
        }
        return 0;
    }

    // Getters
    public int getScore() { return score; }
    public int getSharkLevel() { return sharkLevel; }
    public float getExperience() { return experienceProgress; }
    public int getFishEaten() { return fishEaten; }
    public boolean isMaxLevel() { return sharkLevel >= MAX_SHARK_LEVEL; }

    // New getters and setters for game level and timer
    public int getCurrentGameLevel() { return currentGameLevel; }
    public float getGameTimer() { return gameTimer; }
    public boolean isTimerActive() { return timerActive; }

    public void setCurrentGameLevel(int level) {
        this.currentGameLevel = level;
    }

    public void resetTimer() {
        this.gameTimer = 0f;
    }

    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    public void setGameTimer(float timer) {
        this.gameTimer = timer;
    }

    public void dispose() {
        titleFont.dispose();
        scoreFont.dispose();
        levelFont.dispose();
        hudBackground.dispose();
        progressBarBg.dispose();
        progressBarFill.dispose();
        gameLogo.dispose();

        for (Texture icon : fishIcons) {
            icon.dispose();
        }

        for (Texture bonus : bonusIcons) {
            bonus.dispose();
        }
    }
}
