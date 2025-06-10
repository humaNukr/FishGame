package com.naukma.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.naukma.bonuses.ClockBonus;
import com.naukma.bonuses.StarBonus;
import com.naukma.levels.FishSpawnData;

public class GameHUD {
    // Fonts
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont levelFont;

    // Game data - тільки базові дані без стану
    private int score;
    private int targetScore;
    private boolean isSprintActive = false;
    private boolean canSprint = true;

    // Current game level and timer
    private int currentGameLevel;
    private float gameTimer;
    private float maxLevelTime;
    private int targetFishCount;
    private boolean timerActive;

    // Stamina system
    private float stamina;
    private float maxStamina;

    // Bonus effects system
    private boolean scoreBonusActive = false;
    private float scoreBonusMultiplier = 1f;
    private float scoreBonusTimer = 0f;

    // UI elements
    private Array<Texture> fishIcons;
    private Array<Texture> bonusIcons;
    private Array<Integer> bonusCounts;
    private Texture heartIcon;
    private GlyphLayout glyphLayout;
    private Texture hudBackground;
    private Texture progressBarBg;
    private Texture progressBarFill;
    private Texture gameLogo;

    // Layout variables - all adaptive to screen size
    private float screenWidth;
    private float screenHeight;
    private float hudHeight;
    private float padding;
    private float iconSize;
    private float iconSpacing;
    private float progressBarHeight;
    private float bonusIconSize;
    private float logoSize;
    private float titleFontScale;
    private float scoreFontScale;
    private float levelFontScale;

    // Level up effect
    private float levelUpEffectTimer;
    private boolean showLevelUpEffect;
    private Color levelUpColor;

    // Constants
    private static final int MAX_SHARK_LEVEL = 3;
    private static final float LEVEL_UP_EFFECT_DURATION = 3f;
    private static final float SPRINT_DRAIN_TIME = 5f;
    private static final float SPRINT_REGEN_TIME = 20f;
    private static final float STAMINA_DRAIN_RATE = 100f / SPRINT_DRAIN_TIME;
    private static final float STAMINA_REGENERATION_RATE = 100f / SPRINT_REGEN_TIME;
    private static final float SPRINT_SPEED_MULTIPLIER = 1.3f;

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

        hudHeight = screenHeight * HUD_HEIGHT_RATIO;
        padding = Math.max(20f, Math.min(screenWidth, screenHeight) * PADDING_RATIO);
        iconSize = screenHeight * ICON_SIZE_RATIO;
        iconSpacing = screenHeight * ICON_SPACING_RATIO;
        progressBarHeight = Math.max(15f, screenHeight * PROGRESS_BAR_HEIGHT_RATIO);
        bonusIconSize = screenHeight * BONUS_ICON_RATIO;

        float baseScale = Math.min(screenWidth / 2560f, screenHeight / 1600f);
        titleFontScale = Math.max(2.0f, 3.0f * baseScale);
        scoreFontScale = Math.max(1.8f, 2.5f * baseScale);
        levelFontScale = Math.max(1.5f, 2.0f * baseScale);

        logoSize = hudHeight - padding;
    }

    private void initializeFonts() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(titleFontScale * 1.2f);
        titleFont.setColor(1f, 1f, 0f, 1f);

        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(scoreFontScale * 1.2f);
        scoreFont.setColor(1f, 1f, 1f, 1f);

        levelFont = new BitmapFont();
        levelFont.getData().setScale(levelFontScale * 1f);
        levelFont.setColor(0f, 1f, 1f, 1f);

        glyphLayout = new GlyphLayout();
        levelUpColor = new Color(1f, 1f, 0f, 1f);
    }

    private void initializeVariables() {
        score = 0;
        targetScore = 0;

        currentGameLevel = 1;
        gameTimer = 60f;
        maxLevelTime = 60f;
        targetFishCount = 60;
        timerActive = true;

        levelUpEffectTimer = 0;
        showLevelUpEffect = false;

        maxStamina = 100f;
        stamina = maxStamina;

        fishIcons = new Array<>();
        bonusIcons = new Array<>();
        bonusCounts = new Array<>();

        for (int i = 0; i < 3; i++) {
            bonusCounts.add(0);
        }
    }

    private void loadTextures() {
        hudBackground = new Texture(Gdx.files.internal("hud_background.png"));
        gameLogo = new Texture(Gdx.files.internal("game_logo.png"));
        heartIcon = new Texture(Gdx.files.internal("heart.png"));
    }

    private void createProgressBarTextures() {
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        progressBarBg = new Texture(bgPixmap);
        bgPixmap.dispose();

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

    public void updateLevelFishIcons(Array<FishSpawnData> availableFish) {
        for (Texture texture : fishIcons) {
            texture.dispose();
        }
        fishIcons.clear();

        for (int i = 0; i < Math.min(availableFish.size, MAX_SHARK_LEVEL); i++) {
            FishSpawnData fishData = availableFish.get(i);
            String fishName = fishData.path.replace("/", "");
            String fishPath = fishName + "/frame_00.png";
            fishIcons.add(new Texture(Gdx.files.internal(fishPath)));
        }

        while (fishIcons.size < MAX_SHARK_LEVEL) {
            fishIcons.add(new Texture(Gdx.files.internal("fish_01/frame_00.png")));
        }
    }

    private void loadBonusIcons() {
        for (int i = 1; i <= 3; i++) {
            String bonusPath = String.format("bonus%d.png", i);
            bonusIcons.add(new Texture(Gdx.files.internal(bonusPath)));
        }
    }

    public void update(float deltaTime) {
        if (score < targetScore) {
            score = (int) Math.min(score + 1000f * deltaTime, targetScore);
        }

        if (showLevelUpEffect) {
            levelUpEffectTimer -= deltaTime;
            if (levelUpEffectTimer <= 0) {
                showLevelUpEffect = false;
            }
        }

        if (timerActive && gameTimer > 0) {
            gameTimer -= deltaTime;
            if (gameTimer < 0) {
                gameTimer = 0;
            }
        }

        updateStaminaSystem(deltaTime);
        updateBonusEffects(deltaTime);
        handleBonusInput();
    }

    private void updateStaminaSystem(float deltaTime) {
        if (isSprintActive && stamina > 0) {
            stamina = Math.max(0, stamina - STAMINA_DRAIN_RATE * deltaTime);

            if (stamina <= 0) {
                isSprintActive = false;
                canSprint = false;
            }
        } else {
            if (stamina < maxStamina) {
                stamina = Math.min(maxStamina, stamina + STAMINA_REGENERATION_RATE * deltaTime);

                if (stamina > 0) {
                    canSprint = true;
                }
            }
        }
    }

    private void updateBonusEffects(float deltaTime) {
        if (scoreBonusActive) {
            scoreBonusTimer -= deltaTime;
            if (scoreBonusTimer <= 0) {
                scoreBonusActive = false;
                scoreBonusMultiplier = 1f;
            }
        }
    }

    private void handleBonusInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            useStoredBonus(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            useStoredBonus(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            useStoredBonus(2);
        }
    }

    private void useStoredBonus(int bonusType) {
        if (bonusType < 0 || bonusType >= bonusCounts.size) return;

        int currentCount = bonusCounts.get(bonusType);
        if (currentCount <= 0) return;

        if (bonusType == 1 && scoreBonusActive) {
            return;
        }

        bonusCounts.set(bonusType, currentCount - 1);
        activateBonus(bonusType);
    }

    private void activateBonus(int bonusType) {
        switch (bonusType) {
            case 0:
                addScore(100);
                break;
            case 1:
                if (!scoreBonusActive) {
                    activateScoreBonus(StarBonus.getScoreMultiplier(), StarBonus.getEffectDuration());
                }
                break;
            case 2:
                addTime(ClockBonus.getTimeBonus());
                break;
        }
    }

    // Основний метод рендерингу з параметрами від рівня
    public void render(SpriteBatch batch, int currentSharkLevel, int currentFishEaten, int currentLives, int requiredFishForLevelUp) {
        if (screenWidth != Gdx.graphics.getWidth() || screenHeight != Gdx.graphics.getHeight()) {
            updateScreenDimensions();
            titleFont.getData().setScale(titleFontScale);
            scoreFont.getData().setScale(scoreFontScale);
            levelFont.getData().setScale(levelFontScale);
        }

        renderBackground(batch);
        renderGameLogo(batch);
        renderScore(batch);
        renderLevelIcons(batch, currentSharkLevel, currentFishEaten, requiredFishForLevelUp);
        renderAdditionalElements(batch);
        renderBonusSection(batch);
        renderLevelUpEffect(batch, currentSharkLevel);
        renderTimer(batch);
        renderLives(batch, currentLives);
    }

    private void renderBackground(SpriteBatch batch) {
        batch.draw(hudBackground,
        -screenWidth*0.02f,
        screenHeight - hudHeight+screenHeight*0.02f,
        screenWidth*1.04f,
        hudHeight);
    }

    private void drawTextWithOutline(SpriteBatch batch, BitmapFont font, String text, float x, float y, Color textColor, Color outlineColor) {
        Color originalColor = font.getColor();

        font.setColor(outlineColor);
        font.draw(batch, text, x - 2, y);
        font.draw(batch, text, x + 2, y);
        font.draw(batch, text, x, y - 2);
        font.draw(batch, text, x, y + 2);
        font.draw(batch, text, x - 1, y - 1);
        font.draw(batch, text, x + 1, y - 1);
        font.draw(batch, text, x - 1, y + 1);
        font.draw(batch, text, x + 1, y + 1);

        font.setColor(textColor);
        font.draw(batch, text, x, y);

        font.setColor(originalColor);
    }

    private void renderGameLogo(SpriteBatch batch) {
        float logoX = (screenWidth - logoSize) / 2;
        float logoY = screenHeight - hudHeight + padding/2;

        Color originalColor = batch.getColor();
        batch.setColor(1f, 1f, 1f, 1.0f);
        batch.draw(gameLogo, logoX, logoY, logoSize, logoSize);
        batch.setColor(originalColor);
    }

    private void renderScore(SpriteBatch batch) {
        String scoreText = String.format("Score: %,d", score);
        glyphLayout.setText(scoreFont, scoreText);

        float scoreX = padding*6f;
        float scoreY = screenHeight - hudHeight/2 + padding*1.5f;

        drawTextWithOutline(batch, scoreFont, scoreText, scoreX, scoreY,
                           new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));

        String levelText = String.format("Level: %d", currentGameLevel);
        glyphLayout.setText(levelFont, levelText);

        float levelX = scoreX;
        float levelY = scoreY - padding*2.5f;

        drawTextWithOutline(batch, levelFont, levelText, levelX, levelY,
                           new Color(0f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
    }

    private void renderLevelIcons(SpriteBatch batch, int currentSharkLevel, int currentFishEaten, int requiredFishForLevelUp) {
        float startX = screenWidth * 0.2f;
        float iconsY = screenHeight - hudHeight + padding * 3f;
        float fishSpacing = iconSize + Math.max(50f, iconSpacing * 3f);

        if (fishIcons.size == 0) return;

        for (int i = 0; i < MAX_SHARK_LEVEL && i < fishIcons.size; i++) {
            float x = startX + fishSpacing * i;

            boolean isLocked = i + 1 > currentSharkLevel;
            boolean isCurrent = i + 1 == currentSharkLevel;

            if (isLocked) {
                batch.setColor(0.3f, 0.3f, 0.3f, 0.5f);
            } else if (isCurrent) {
                float pulse = MathUtils.sin(System.currentTimeMillis() * 0.005f) * 0.3f + 0.7f;
                batch.setColor(1f, 1f, pulse, 1f);
            } else {
                batch.setColor(0.5f, 1f, 0.5f, 1f); // Завершені - зелені
            }

            batch.draw(fishIcons.get(i), x, iconsY, iconSize, iconSize);

            // Показуємо прогрес для поточного активного типу (включно з 3-м рівнем)
            if (isCurrent) {
                // Використовуємо передану кількість риб для левел апу
                renderProgressBar(batch, x, iconsY - progressBarHeight - padding, currentFishEaten, requiredFishForLevelUp);
            }

            batch.setColor(Color.WHITE);
        }
    }

    // Оновлений метод renderProgressBar - приймає поточний прогрес та ціль
    private void renderProgressBar(SpriteBatch batch, float x, float y, int currentFishEaten, int requiredFish) {
        float barWidth = iconSize;

        batch.draw(progressBarBg, x, y, barWidth, progressBarHeight);

        float fillWidth = ((float)currentFishEaten / requiredFish) * barWidth;
        if (fillWidth > 0) {
            batch.draw(progressBarFill, x, y, fillWidth, progressBarHeight);
        }

        String progressText = String.format("%d/%d fish", currentFishEaten, requiredFish);
        glyphLayout.setText(levelFont, progressText);
        float textX = x + (barWidth - glyphLayout.width) / 2;
        float textY = y + progressBarHeight + glyphLayout.height + padding/2;

        drawTextWithOutline(batch, levelFont, progressText, textX, textY,
                           new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
    }

    private void renderAdditionalElements(SpriteBatch batch) {
        String staminaText = "Stamina";
        glyphLayout.setText(levelFont, staminaText);

        float staminaX = screenWidth * 0.65f - glyphLayout.width/2;
        float staminaY = screenHeight - hudHeight/2 + glyphLayout.height/2;

        drawTextWithOutline(batch, levelFont, staminaText, staminaX, staminaY,
                           new Color(0f, 1f, 0.5f, 1f), new Color(0f, 0f, 0f, 1f));

        float staminaBarX = staminaX;
        float staminaBarY = staminaY - padding * 2.5f;
        float staminaBarWidth = glyphLayout.width;

        batch.draw(progressBarBg, staminaBarX, staminaBarY, staminaBarWidth, progressBarHeight);

        float staminaProgress = stamina / maxStamina;
        if (staminaProgress > 0) {
            batch.draw(progressBarFill, staminaBarX, staminaBarY, staminaBarWidth * staminaProgress, progressBarHeight);
        }

        if (scoreBonusActive) {
            String bonusText = String.format("STAR BONUS: x%.1f (%.0fs)", scoreBonusMultiplier, scoreBonusTimer);
            glyphLayout.setText(levelFont, bonusText);

            float bonusX = staminaBarX;
            float bonusY = staminaBarY - padding * 3f;

            float alpha = (float) (Math.sin(System.currentTimeMillis() * 0.01f) * 0.3f + 0.7f);
            Color bonusColor = new Color(1f, 1f, 0f, alpha);

            drawTextWithOutline(batch, levelFont, bonusText, bonusX, bonusY, bonusColor, new Color(0f, 0f, 0f, alpha));
        }
    }

    private void renderBonusSection(SpriteBatch batch) {
        if (bonusIcons.size == 0) return;

        float largeBonusSize = bonusIconSize * 1.3f;
        float bonusStartX = screenWidth * 0.75f;
        float bonusY = screenHeight - hudHeight/2 - largeBonusSize/2;

        for (int i = 0; i < bonusIcons.size; i++) {
            float bonusX = bonusStartX + i * (largeBonusSize + iconSpacing * 2);

            batch.draw(bonusIcons.get(i), bonusX, bonusY, largeBonusSize, largeBonusSize);

            if (i < bonusCounts.size) {
                String countText = "x" + bonusCounts.get(i);
                glyphLayout.setText(levelFont, countText);

                float countX = bonusX + (largeBonusSize - glyphLayout.width) / 2;
                float countY = bonusY + padding/4;

                drawTextWithOutline(batch, levelFont, countText, countX, countY,
                                   new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
            }
        }
    }

    private void renderLevelUpEffect(SpriteBatch batch, int currentSharkLevel) {
        if (showLevelUpEffect) {
            float alpha = MathUtils.sin(levelUpEffectTimer * 8) * 0.5f + 0.5f;

            String levelUpText = "LEVEL " + currentSharkLevel + " UNLOCKED!";
            glyphLayout.setText(titleFont, levelUpText);

            float x = (screenWidth - glyphLayout.width) / 2;
            float y = screenHeight / 2;

            Color animatedColor = new Color(1f, 1f, 0f, alpha);
            Color outlineColor = new Color(0f, 0f, 0f, alpha);

            drawTextWithOutline(batch, titleFont, levelUpText, x, y, animatedColor, outlineColor);
        }
    }

    private void renderTimer(SpriteBatch batch) {
        int minutes = (int)(gameTimer / 60);
        int seconds = (int)(gameTimer % 60);
        String timerText = String.format("Time: %02d:%02d", minutes, seconds);

        glyphLayout.setText(scoreFont, timerText);

        float timerX = screenWidth - glyphLayout.width - padding*6f;
        float timerY = screenHeight - hudHeight/2 + padding*3f;

        drawTextWithOutline(batch, scoreFont, timerText, timerX, timerY,
                           new Color(1f, 1f, 0f, 1f), new Color(0f, 0f, 0f, 1f));
    }

    private void renderLives(SpriteBatch batch, int currentLives) {
        float livesX = padding*6f;
        float livesY = screenHeight - hudHeight/2 + padding*1.3f;
        float heartSize = iconSize * 0.6f;

        for (int i = 0; i < currentLives; i++) {
            float heartX = livesX + i * (heartSize + iconSpacing/2);
            batch.draw(heartIcon, heartX, livesY, heartSize, heartSize);
        }
    }

    // Public API methods
    public void addScore(int points) {
        float finalPoints = points * scoreBonusMultiplier;
        targetScore += (int)finalPoints;
    }

    public void activateScoreBonus(float multiplier, float duration) {
        this.scoreBonusActive = true;
        this.scoreBonusMultiplier = multiplier;
        this.scoreBonusTimer = duration;
    }

    public void addTime(float timeToAdd) {
        gameTimer = Math.min(gameTimer + timeToAdd, maxLevelTime * 2);
    }

    public void addBonus(int bonusType, int amount) {
        if (bonusType >= 0 && bonusType < bonusCounts.size) {
            bonusCounts.set(bonusType, bonusCounts.get(bonusType) + amount);
        }
    }

    // Метод для тригеру level up ефекту
    public void triggerLevelUpEffect() {
        showLevelUpEffect = true;
        levelUpEffectTimer = LEVEL_UP_EFFECT_DURATION;
    }

    // Getters
    public int getScore() { return score; }
    public int getCurrentGameLevel() { return currentGameLevel; }
    public float getGameTimer() { return gameTimer; }
    public boolean isTimerActive() { return timerActive; }
    public float getHudHeight() { return hudHeight; }
    public float getTimeRemaining() { return gameTimer; }

    // Setters
    public void setCurrentGameLevel(int level) { this.currentGameLevel = level; }
    public void setTimerActive(boolean active) { this.timerActive = active; }
    public void setGameTimer(float timer) { this.gameTimer = timer; }

    public void setLevelParameters(float timeLimit, int fishTarget) {
        this.maxLevelTime = timeLimit;
        this.targetFishCount = fishTarget;
        this.gameTimer = timeLimit;
    }

    public void resetTimer() {
        this.gameTimer = maxLevelTime;
    }

    // Sprint methods
    public void startSprint() {
        if (canSprint && stamina > 0) {
            isSprintActive = true;
        }
    }

    public void stopSprint() {
        isSprintActive = false;
    }

    public boolean isSprintActive() {
        return isSprintActive;
    }

    public float getSpeedMultiplier() {
        return isSprintActive ? SPRINT_SPEED_MULTIPLIER : 1.0f;
    }

    public boolean canSprint() {
        return canSprint && stamina > 0;
    }

    public void dispose() {
        titleFont.dispose();
        scoreFont.dispose();
        levelFont.dispose();
        hudBackground.dispose();
        progressBarBg.dispose();
        progressBarFill.dispose();
        gameLogo.dispose();
        heartIcon.dispose();

        for (Texture icon : fishIcons) {
            icon.dispose();
        }

        for (Texture bonus : bonusIcons) {
            bonus.dispose();
        }
    }

    public int getSharkLevel() {
        return this.currentGameLevel;
    }

    public void addLife() {
        // Логіка додавання життя має бути в BasicLevel,
        // цей метод тут для сумісності
    }
}
