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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.audio.Sound;

public class VictoryWindow {
    public BitmapFont titleFont;
    public BitmapFont textFont;
    public BitmapFont buttonFont;
    public GlyphLayout glyphLayout;

    private Texture backgroundTexture;
    public Texture buttonTexture;
    public Texture buttonHoverTexture;
    private Texture buttonDisabledTexture;

    private boolean active = false;
    private boolean nextLevelPressed = false;
    private boolean mainMenuPressed = false;
    private boolean shouldRestart = false;

    // Дані рівня
    private int levelNumber;
    private int currentScore;
    private int bestScore;
    private boolean isNewRecord = false;

    // Кнопки (як у MainMenu)
    private String[] buttonItems = {"NEXT LEVEL", "RESTART", "MAIN MENU"};
    public Rectangle[] buttonBounds;
    public int selectedItem = 0;
    private int prevSelectedItem = -1;

    // Анімація
    private float animationTimer = 0f;
    private float recordGlowTimer = 0f;

    // Система рекордів
    private static final String RECORDS_FILE = "records.json";
    private ObjectMap<String, Integer> levelRecords;

    private Sound clickSound;

    private boolean bossButtonVisible = false;
    private int bossButtonIndex = -1;

    public VictoryWindow() {
        initializeFonts();
        createTextures();
        loadRecords();
        glyphLayout = new GlyphLayout();
        initializeButtonBounds();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("button.wav"));
    }

    private void initializeFonts() {
        // Використовуємо той же шрифт і налаштування як у MainMenu
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HennyPenny.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // Налаштування для заголовка (як у MainMenu)
        parameter.size = 64;
        parameter.color = Color.CYAN;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // Налаштування для тексту
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        textFont = generator.generateFont(parameter);

        // Налаштування для кнопок (як у MainMenu)
        parameter.size = 25;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        buttonFont = generator.generateFont(parameter);

        generator.dispose();
    }

    private void createTextures() {
        // Використовуємо той же фон як у MainMenu
        try {
            backgroundTexture = new Texture(Gdx.files.internal("output.jpg"));
        } catch (Exception e) {
            // Створюємо запасний фон
            Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            bgPixmap.setColor(0f, 0f, 0f, 0.8f);
            bgPixmap.fill();
            backgroundTexture = new Texture(bgPixmap);
            bgPixmap.dispose();
        }

        createButtonTextures();
    }

    private void createButtonTextures() {
        // Звичайна кнопка
        Pixmap pixmap = new Pixmap(300, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.2f, 0.3f, 0.8f, 0.8f);
        pixmap.fill();
        pixmap.setColor(0.4f, 0.5f, 1f, 1f);
        pixmap.drawRectangle(0, 0, 300, 60);
        buttonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Кнопка при наведенні
        pixmap = new Pixmap(300, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.4f, 0.5f, 1f, 0.9f);
        pixmap.fill();
        pixmap.setColor(0.6f, 0.7f, 1f, 1f);
        pixmap.drawRectangle(0, 0, 300, 60);
        buttonHoverTexture = new Texture(pixmap);
        pixmap.dispose();

        // Відключена кнопка
        pixmap = new Pixmap(300, 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.6f);
        pixmap.fill();
        pixmap.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        pixmap.drawRectangle(0, 0, 300, 60);
        buttonDisabledTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initializeButtonBounds() {
        buttonBounds = new Rectangle[buttonItems.length];
        float startY = Gdx.graphics.getHeight() / 2 + 50; // Підняли кнопки вище
        float spacing = 80;
        float buttonWidth = 300;
        float buttonHeight = 60;

        for (int i = 0; i < buttonItems.length; i++) {
            float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
            float buttonY = startY - i * spacing - buttonHeight;
            buttonBounds[i] = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        }
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
        selectedItem = 0;
        nextLevelPressed = false;
        mainMenuPressed = false;
        shouldRestart = false;
        bossButtonVisible = (levelNumber >= 3);
        if (bossButtonVisible) {
            bossButtonIndex = buttonItems.length;
        } else {
            bossButtonIndex = -1;
        }

        // Оновлюємо межі кнопок
        initializeButtonBounds();
    }

    public void update(float deltaTime) {
        if (!active) return;

        animationTimer += deltaTime;
        recordGlowTimer += deltaTime;
    }

    public void handleInput() {
        if (!active) return;

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        prevSelectedItem = selectedItem;

        if (Gdx.input.justTouched()) {
            for (int i = 0; i < buttonBounds.length; i++) {
                if (buttonBounds[i].contains(mouseX, mouseY)) {
                    handleSelection(i);
                    if (clickSound != null) clickSound.play();
                    return;
                }
            }
        }

        boolean mouseHoverDetected = false;
        for (int i = 0; i < buttonBounds.length; i++) {
            if (buttonBounds[i].contains(mouseX, mouseY)) {
                selectedItem = i;
                mouseHoverDetected = true;
                break;
            }
        }
        if (selectedItem != prevSelectedItem && clickSound != null) clickSound.play();

        if (!mouseHoverDetected) {
            int totalButtons = buttonItems.length + (bossButtonVisible ? 1 : 0);
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedItem--;
                if (selectedItem < 0) selectedItem = totalButtons - 1;
                if (clickSound != null) clickSound.play();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedItem++;
                if (selectedItem >= totalButtons) selectedItem = 0;
                if (clickSound != null) clickSound.play();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                handleSelection(selectedItem);
                if (clickSound != null) clickSound.play();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handleSelection(2); // Main Menu
        }
    }

    private void handleSelection(int index) {
        // Для останнього рівня перша кнопка — це перехід до боса
        if (index == 0 && levelNumber >= 3) {
            if (bossListener != null) bossListener.onBossButtonPressed();
            return;
        }
        if (bossButtonVisible && index == bossButtonIndex) {
            // Викликаємо перехід до боса (старий варіант, залишаю для сумісності)
            if (bossListener != null) bossListener.onBossButtonPressed();
            return;
        }
        switch (index) {
            case 0: // NEXT LEVEL
                if (levelNumber < 3) {
                    nextLevelPressed = true;
                    active = false;
                }
                break;
            case 1: // RESTART
                shouldRestart = true;
                active = false;
                break;
            case 2: // MAIN MENU
                mainMenuPressed = true;
                active = false;
                break;
        }
    }

    public void render(SpriteBatch batch) {
        if (!active) return;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Фон (як у MainMenu)
        if (backgroundTexture != null) {
            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        // Заголовок (стиль MainMenu)
        String titleText = "LEVEL " + levelNumber + " COMPLETED!";
        glyphLayout.setText(titleFont, titleText);
        float titleX = (screenWidth - glyphLayout.width) / 2;
        float titleY = screenHeight - 100;
        titleFont.draw(batch, titleText, titleX, titleY);

        // Рахунок (опустили нижче)
        textFont.setColor(Color.YELLOW);
        String scoreText = "Your Score: " + String.format("%,d", currentScore);
        glyphLayout.setText(textFont, scoreText);
        float scoreX = (screenWidth - glyphLayout.width) / 2;
        float scoreY = screenHeight - 280;
        textFont.draw(batch, scoreText, scoreX, scoreY);

        // Рекорд
        if (isNewRecord) {
            textFont.setColor(Color.GREEN);
            String newRecordText = "NEW RECORD!";
            glyphLayout.setText(textFont, newRecordText);
            float newRecordX = (screenWidth - glyphLayout.width) / 2;
            float newRecordY = scoreY - 50;

            // Анімація для нового рекорду
            float scale = 1f + (float) Math.abs(Math.sin(recordGlowTimer * 5f)) * 0.1f;
            textFont.getData().setScale(scale);
            textFont.draw(batch, newRecordText, newRecordX, newRecordY);
            textFont.getData().setScale(1f);
        }

        textFont.setColor(Color.WHITE);
        String recordText = "Best Score: " + String.format("%,d", bestScore);
        glyphLayout.setText(textFont, recordText);
        float recordX = (screenWidth - glyphLayout.width) / 2;
        float recordY = isNewRecord ? scoreY - 100 : scoreY - 50;
        textFont.draw(batch, recordText, recordX, recordY);

        // Кнопки (стиль MainMenu)
        for (int i = 0; i < buttonItems.length; i++) {
            Rectangle bounds = buttonBounds[i];

            Texture currentButtonTexture;
            // Для останнього рівня перша кнопка активна і з іншим текстом
            if (i == 0 && levelNumber >= 3) {
                currentButtonTexture = (i == selectedItem) ? buttonHoverTexture : buttonTexture;
            } else if (i == 0 && levelNumber < 3) {
                currentButtonTexture = (i == selectedItem) ? buttonHoverTexture : buttonTexture;
            } else {
                currentButtonTexture = (i == selectedItem) ? buttonHoverTexture : buttonTexture;
            }

            batch.draw(currentButtonTexture, bounds.x, bounds.y, bounds.width, bounds.height);

            String item = buttonItems[i];
            // Заміна тексту для останнього рівня
            if (i == 0 && levelNumber >= 3) {
                item = "BOSS FIGHT";
            }
            glyphLayout.setText(buttonFont, item);
            float textX = bounds.x + (bounds.width - glyphLayout.width) / 2;
            float textY = bounds.y + (bounds.height + glyphLayout.height) / 2;

            if (i == selectedItem) {
                buttonFont.setColor(Color.YELLOW);
            } else {
                buttonFont.setColor(Color.WHITE);
            }

            buttonFont.draw(batch, item, textX, textY);
        }

        if (bossButtonVisible) {
            // Рендеримо кнопку "Бій з босом"
            // (Можна стилізувати окремо)
            // ...
        }
    }

    // Геттери
    public boolean isActive() { return active; }
    public boolean isNextLevelRequested() { return nextLevelPressed; }
    public boolean isMenuRequested() { return mainMenuPressed; }
    public boolean shouldRestart() { return shouldRestart; }
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            initializeButtonBounds();
        }
    }

    public void resetFlags() {
        nextLevelPressed = false;
        mainMenuPressed = false;
        shouldRestart = false;
    }

    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (textFont != null) textFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
        if (buttonDisabledTexture != null) buttonDisabledTexture.dispose();
        if (clickSound != null) clickSound.dispose();
    }

    // Додаю інтерфейс для обробки кнопки боса
    public interface BossListener {
        void onBossButtonPressed();
    }
    private BossListener bossListener;
    public void setBossListener(BossListener listener) {
        this.bossListener = listener;
    }

    public void setButtonItems(String[] items) {
        this.buttonItems = items;
    }
    public void reinitButtonBounds() {
        initializeButtonBounds();
    }
}
