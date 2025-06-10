package com.naukma.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class VictoryWindow {
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private BitmapFont buttonFont;
    private GlyphLayout glyphLayout;
    
    private Texture backgroundTexture;
    private Texture victoryBgTexture;
    private Texture buttonTexture;
    private Texture buttonHoverTexture;
    
    private boolean active = false;
    private boolean nextLevelPressed = false;
    private boolean mainMenuPressed = false;
    private boolean shouldRestart = false;
    
    // Дані рівня
    private int levelNumber;
    private int currentScore;
    private int bestScore;
    private boolean isNewRecord = false;
    
    // Кнопки
    private float nextButtonX, nextButtonY, nextButtonWidth, nextButtonHeight;
    private float menuButtonX, menuButtonY, menuButtonWidth, menuButtonHeight;
    private float restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight;
    private boolean nextButtonHovered = false;
    private boolean menuButtonHovered = false;
    private boolean restartButtonHovered = false;
    
    // Анімація
    private float animationTimer = 0f;
    private float recordGlowTimer = 0f;
    
    // Система рекордів
    private static final String RECORDS_FILE = "records.json";
    private ObjectMap<String, Integer> levelRecords;
    
    public VictoryWindow() {
        initializeFonts();
        createTextures();
        loadRecords();
        glyphLayout = new GlyphLayout();
        
        // Розміри кнопок
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        nextButtonWidth = 250f;
        nextButtonHeight = 50f;
        menuButtonWidth = 250f;
        menuButtonHeight = 50f;
        restartButtonWidth = 250f;
        restartButtonHeight = 50f;
        
        // Позиції кнопок
        nextButtonX = (screenWidth - nextButtonWidth) / 2f;
        nextButtonY = screenHeight * 0.45f;
        
        restartButtonX = (screenWidth - restartButtonWidth) / 2f;
        restartButtonY = screenHeight * 0.35f;

        menuButtonX = (screenWidth - menuButtonWidth) / 2f;
        menuButtonY = screenHeight * 0.25f;
    }
    
    private void initializeFonts() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.5f);
        titleFont.setColor(1f, 1f, 0f, 1f); // Золотий
        
        textFont = new BitmapFont();
        textFont.getData().setScale(2.0f);
        textFont.setColor(1f, 1f, 1f, 1f); // Білий
        
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.8f);
        buttonFont.setColor(1f, 1f, 1f, 1f); // Білий
    }
    
    private void createTextures() {
        // Напівпрозорий фон (залишимо як запасний)
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0f, 0f, 0f, 0.8f);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Завантажуємо фон з файлу
        victoryBgTexture = new Texture(Gdx.files.internal("victory_bg.jpeg"));
        
        // Кнопка (звичайна)
        Pixmap buttonPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        buttonPixmap.setColor(0.2f, 0.4f, 0.8f, 0.9f);
        buttonPixmap.fill();
        buttonTexture = new Texture(buttonPixmap);
        buttonPixmap.dispose();
        
        // Кнопка (при наведенні)
        Pixmap hoverPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        hoverPixmap.setColor(0.3f, 0.5f, 1f, 0.9f);
        hoverPixmap.fill();
        buttonHoverTexture = new Texture(hoverPixmap);
        hoverPixmap.dispose();
    }
    
    private void loadRecords() {
        levelRecords = new ObjectMap<>();
        
        try {
            FileHandle file = Gdx.files.local(RECORDS_FILE);
            if (file.exists()) {
                Json json = new Json();
                ObjectMap<String, Integer> loaded = json.fromJson(ObjectMap.class, file);
                if (loaded != null) {
                    levelRecords.putAll(loaded);
                }
            }
        } catch (Exception e) {
            System.out.println("Помилка завантаження рекордів: " + e.getMessage());
            levelRecords.clear();
        }
    }
    
    private void saveRecords() {
        try {
            Json json = new Json();
            String jsonString = json.toJson(levelRecords);
            FileHandle file = Gdx.files.local(RECORDS_FILE);
            file.writeString(jsonString, false);
        } catch (Exception e) {
            System.out.println("Помилка збереження рекордів: " + e.getMessage());
        }
    }
    
    public void show(int levelNumber, int score) {
        this.levelNumber = levelNumber;
        this.currentScore = score;
        
        // Отримуємо попередній рекорд
        String levelKey = "level_" + levelNumber;
        this.bestScore = levelRecords.get(levelKey, 0);
        
        // Перевіряємо новий рекорд
        if (score > bestScore) {
            isNewRecord = true;
            bestScore = score;
            levelRecords.put(levelKey, score);
            saveRecords();
        } else {
            isNewRecord = false;
        }
        
        active = true;
        animationTimer = 0f;
        recordGlowTimer = 0f;
        nextLevelPressed = false;
        mainMenuPressed = false;
        shouldRestart = false;
    }
    
    public void update(float deltaTime) {
        if (!active) return;
        
        animationTimer += deltaTime;
        recordGlowTimer += deltaTime;
        
        // Оновлення позицій миші для hover ефектів
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Інвертуємо Y
        
        nextButtonHovered = levelNumber < 3 && isPointInButton(mouseX, mouseY, nextButtonX, nextButtonY, nextButtonWidth, nextButtonHeight);
        restartButtonHovered = isPointInButton(mouseX, mouseY, restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight);
        menuButtonHovered = isPointInButton(mouseX, mouseY, menuButtonX, menuButtonY, menuButtonWidth, menuButtonHeight);
    }
    
    public void handleInput() {
        if (!active) return;
        
        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            
            // Кнопка "Next Level" активна тільки якщо це не 3-й рівень
            if (levelNumber < 3 && isPointInButton(mouseX, mouseY, nextButtonX, nextButtonY, nextButtonWidth, nextButtonHeight)) {
                nextLevelPressed = true;
                active = false;
            } else if (isPointInButton(mouseX, mouseY, restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight)) {
                shouldRestart = true;
                active = false;
            } else if (isPointInButton(mouseX, mouseY, menuButtonX, menuButtonY, menuButtonWidth, menuButtonHeight)) {
                mainMenuPressed = true;
                active = false;
            }
        }
        
        // Клавіші
        if (levelNumber < 3 && (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
            nextLevelPressed = true;
            active = false;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mainMenuPressed = true;
            active = false;
        }
    }
    
    private boolean isPointInButton(float x, float y, float buttonX, float buttonY, float buttonW, float buttonH) {
        return x >= buttonX && x <= buttonX + buttonW && y >= buttonY && y <= buttonY + buttonH;
    }
    
    public void render(SpriteBatch batch) {
        if (!active) return;
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Фон
        batch.setColor(Color.WHITE); // Скидаємо колір, щоб фон не був прозорим
        batch.draw(victoryBgTexture, 0, 0, screenWidth, screenHeight);
        
        // Заголовок
        String titleText = "LEVEL " + levelNumber + " COMPLETED!";
        glyphLayout.setText(titleFont, titleText);
        
        float titleX = (screenWidth - glyphLayout.width) / 2f;
        float titleY = screenHeight * 0.75f;
        
        // Анімований заголовок
        float titleScale = 1f + (float) Math.sin(animationTimer * 3f) * 0.1f;
        titleFont.getData().setScale(3.5f * titleScale);
        
        drawTextWithOutline(batch, titleFont, titleText, titleX, titleY, 
            new Color(1f, 1f, 0f, 1f), new Color(0f, 0f, 0f, 1f));
        
        titleFont.getData().setScale(3.5f); // Відновлюємо розмір
        
        // Рахунок
        String scoreText = "Your Score: " + String.format("%,d", currentScore);
        glyphLayout.setText(textFont, scoreText);
        float scoreX = (screenWidth - glyphLayout.width) / 2f;
        float scoreY = screenHeight * 0.6f;
        
        drawTextWithOutline(batch, textFont, scoreText, scoreX, scoreY, 
            new Color(1f, 1f, 1f, 1f), new Color(0f, 0f, 0f, 1f));
        
        // Рекорд
        String recordText = "Best Score: " + String.format("%,d", bestScore);
        glyphLayout.setText(textFont, recordText);
        float recordX = (screenWidth - glyphLayout.width) / 2f;
        float recordY = screenHeight * 0.55f;
        
        if (isNewRecord) {
            // Анімація для нового рекорду
            float recordScale = 1f + (float) Math.abs(Math.sin(recordGlowTimer * 5f)) * 0.1f;
            textFont.getData().setScale(2.0f * recordScale);
            
            drawTextWithOutline(batch, textFont, "NEW RECORD!", 
                (screenWidth - glyphLayout.width) / 2f, recordY + 40, 
                Color.YELLOW, Color.RED);
            
            textFont.getData().setScale(2.0f); // Відновлюємо розмір
        }
        
        drawTextWithOutline(batch, textFont, recordText, recordX, recordY, 
            Color.WHITE, Color.BLACK);
        
        // Кнопки
        if (levelNumber < 3) {
            renderButton(batch, nextButtonX, nextButtonY, nextButtonWidth, nextButtonHeight, "Next Level", nextButtonHovered);
        }
        renderButton(batch, restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight, "Restart", restartButtonHovered);
        renderButton(batch, menuButtonX, menuButtonY, menuButtonWidth, menuButtonHeight, "Main Menu", menuButtonHovered);
    }
    
    private void renderButton(SpriteBatch batch, float x, float y, float width, float height, 
                              String text, boolean hovered) {
        
        Texture buttonTex = hovered ? buttonHoverTexture : buttonTexture;
        batch.draw(buttonTex, x, y, width, height);
        
        glyphLayout.setText(buttonFont, text);
        float textX = x + (width - glyphLayout.width) / 2f;
        float textY = y + (height + glyphLayout.height) / 2f;
        
        Color textColor = hovered ? new Color(1f, 1f, 1f, 1f) : new Color(0.9f, 0.9f, 0.9f, 1f);
        drawTextWithOutline(batch, buttonFont, text, textX, textY, textColor, new Color(0f, 0f, 0f, 1f));
    }
    
    private void drawTextWithOutline(SpriteBatch batch, BitmapFont font, String text, float x, float y, 
                                     Color textColor, Color outlineColor) {
        Color originalColor = font.getColor();
        
        // Контур
        font.setColor(outlineColor);
        font.draw(batch, text, x - 2, y);
        font.draw(batch, text, x + 2, y);
        font.draw(batch, text, x, y - 2);
        font.draw(batch, text, x, y + 2);
        font.draw(batch, text, x - 1, y - 1);
        font.draw(batch, text, x + 1, y - 1);
        font.draw(batch, text, x - 1, y + 1);
        font.draw(batch, text, x + 1, y + 1);
        
        // Основний текст
        font.setColor(textColor);
        font.draw(batch, text, x, y);
        
        font.setColor(originalColor);
    }
    
    // Геттери
    public boolean isActive() { return active; }
    public boolean isNextLevelRequested() { return nextLevelPressed; }
    public boolean isMenuRequested() { return mainMenuPressed; }
    public boolean shouldRestart() { return shouldRestart; }
    public void setActive(boolean active) { this.active = active; }
    
    public void resetFlags() {
        nextLevelPressed = false;
        mainMenuPressed = false;
        shouldRestart = false;
    }
    
    public void dispose() {
        titleFont.dispose();
        textFont.dispose();
        buttonFont.dispose();
        backgroundTexture.dispose();
        victoryBgTexture.dispose();
        buttonTexture.dispose();
        buttonHoverTexture.dispose();
    }
} 