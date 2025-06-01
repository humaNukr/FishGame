package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MainMenu {
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private BitmapFont levelFont;
    private Texture backgroundTexture;
    private Texture buttonTexture;
    private Texture buttonHoverTexture;
    private Texture buttonDisabledTexture;
    private Texture levelButtonTexture;
    private Texture levelButtonSelectedTexture;

    private String[] menuItems = {"START GAME", "SETTINGS", "EXIT"};
    private String[] levelItems = {"1 LEVEL", "2 LEVEL", "3 LEVEL"};
    private Rectangle[] buttonBounds;
    private Rectangle[] levelButtonBounds;
    private int selectedItem = 0;
    private int selectedLevel = -1; // -1 означає, що рівень не обрано
    private boolean isActive = true;

    private GlyphLayout glyphLayout;

    public MainMenu() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HennyPenny.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // Налаштування для заголовка
        parameter.size = 64; // розмір шрифта
        parameter.color = Color.CYAN;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // Налаштування для меню
        parameter.size = 15;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        menuFont = generator.generateFont(parameter);

        // Налаштування для рівнів
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        levelFont = generator.generateFont(parameter);

        generator.dispose();

//        titleFont = new BitmapFont();
//        titleFont.getData().setScale(4);
//        titleFont.setColor(Color.CYAN);
//
//        menuFont = new BitmapFont();
//        menuFont.getData().setScale(2.5f);
//        menuFont.setColor(Color.WHITE);
//
//        levelFont = new BitmapFont();
//        levelFont.getData().setScale(2f);
//        levelFont.setColor(Color.WHITE);

        glyphLayout = new GlyphLayout();

        try {
            backgroundTexture = new Texture(Gdx.files.internal("output.jpg"));
        } catch (Exception e) {
            backgroundTexture = null;
        }

        createButtonTextures();
        createLevelButtonTextures();

        initializeButtonBounds();
        initializeLevelButtonBounds();
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

    private void createLevelButtonTextures() {
        // Кнопка рівня
        Pixmap pixmap = new Pixmap(180, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.4f, 0.2f, 0.8f);
        pixmap.fill();
        pixmap.setColor(0.2f, 0.6f, 0.3f, 1f);
        pixmap.drawRectangle(0, 0, 180, 50);
        levelButtonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Обрана кнопка рівня
        pixmap = new Pixmap(180, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.2f, 0.7f, 0.2f, 0.9f);
        pixmap.fill();
        pixmap.setColor(0.4f, 0.9f, 0.4f, 1f);
        pixmap.drawRectangle(0, 0, 180, 50);
        levelButtonSelectedTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initializeButtonBounds() {
        buttonBounds = new Rectangle[menuItems.length];
        float startY = Gdx.graphics.getHeight() / 2 - 50;
        float spacing = 80;
        float buttonWidth = 300;
        float buttonHeight = 60;

        for (int i = 0; i < menuItems.length; i++) {
            float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
            float buttonY = startY - i * spacing - buttonHeight;
            buttonBounds[i] = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        }
    }

    private void initializeLevelButtonBounds() {
        levelButtonBounds = new Rectangle[levelItems.length];
        float buttonWidth = 180;
        float buttonHeight = 50;
        float totalWidth = buttonWidth * levelItems.length + 20 * (levelItems.length - 1);
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2;
        float buttonY = Gdx.graphics.getHeight() / 2 + 100;

        for (int i = 0; i < levelItems.length; i++) {
            float buttonX = startX + i * (buttonWidth + 20);
            levelButtonBounds[i] = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        }
    }

    public void handleInput() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Обробка кліків по кнопках рівнів
        if (Gdx.input.justTouched()) {
            for (int i = 0; i < levelButtonBounds.length; i++) {
                if (levelButtonBounds[i].contains(mouseX, mouseY)) {
                    selectedLevel = i;
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

        if (!mouseHoverDetected) {
            // Клавіатура працює тільки якщо миша не навела на кнопку
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedItem--;
                if (selectedItem < 0) selectedItem = menuItems.length - 1;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedItem++;
                if (selectedItem >= menuItems.length) selectedItem = 0;
            }
        }

        // Обробка вибору рівня клавіатурою
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectedLevel = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectedLevel = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectedLevel = 2;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedItem >= 0) {
            handleSelection(selectedItem);
        }

        if (Gdx.input.justTouched()) {
            for (int i = 0; i < buttonBounds.length; i++) {
                if (buttonBounds[i].contains(mouseX, mouseY)) {
                    handleSelection(i);
                    break;
                }
            }
        }
    }

    private void handleSelection(int itemIndex) {
        switch (itemIndex) {
            case 0: // START GAME
                if (selectedLevel >= 0) { // Перевіряємо, чи обрано рівень
                    isActive = false;
                }
                break;
            case 1: // SETTINGS
                // Код для налаштувань
                break;
            case 2: // EXIT
                Gdx.app.exit();
                break;
        }
    }

    public void render(SpriteBatch batch) {
        if (backgroundTexture != null) {
            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1f, 1f, 1f, 1f);
        }

        String title = "CHEW & LIVE";
        glyphLayout.setText(titleFont, title);
        float titleX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2;
        float titleY = Gdx.graphics.getHeight() - 100;
        titleFont.draw(batch, title, titleX, titleY);

        // Заголовок для вибору рівня
        levelFont.setColor(Color.CYAN);
        String levelTitle = "CHOOSE LEVEL:";
        glyphLayout.setText(levelFont, levelTitle);
        float levelTitleX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2;
        float levelTitleY = Gdx.graphics.getHeight() / 2 + 190;
        levelFont.draw(batch, levelTitle, levelTitleX, levelTitleY);

        // Малюємо кнопки рівнів
        for (int i = 0; i < levelItems.length; i++) {
            Rectangle bounds = levelButtonBounds[i];

            Texture currentTexture = (i == selectedLevel) ? levelButtonSelectedTexture : levelButtonTexture;
            batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);

            String item = levelItems[i];
            glyphLayout.setText(levelFont, item);
            float textX = bounds.x + (bounds.width - glyphLayout.width) / 2;
            float textY = bounds.y + (bounds.height + glyphLayout.height) / 2;

            if (i == selectedLevel) {
                levelFont.setColor(Color.YELLOW);
            } else {
                levelFont.setColor(Color.WHITE);
            }

            levelFont.draw(batch, item, textX, textY);
        }

        // Повідомлення про обраний рівень
        if (selectedLevel >= 0) {
            levelFont.setColor(Color.GREEN);
            String selectedText = "SELECTED: " + levelItems[selectedLevel];
            glyphLayout.setText(levelFont, selectedText);
            float selectedX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2;
            levelFont.draw(batch, selectedText, selectedX, Gdx.graphics.getHeight() / 2 + 40);
        } else {
            levelFont.setColor(Color.RED);
            String warningText = "CHOOSE LEVEL FIRST!";
            glyphLayout.setText(levelFont, warningText);
            float warningX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2;
            levelFont.draw(batch, warningText, warningX, Gdx.graphics.getHeight() / 2 + 40);
        }

        // Малюємо основні кнопки меню
        for (int i = 0; i < menuItems.length; i++) {
            Rectangle bounds = buttonBounds[i];

            Texture currentButtonTexture;
            if (i == 0 && selectedLevel < 0) { // START GAME відключена, якщо рівень не обрано
                currentButtonTexture = buttonDisabledTexture;
            } else {
                currentButtonTexture = (i == selectedItem) ? buttonHoverTexture : buttonTexture;
            }

            batch.draw(currentButtonTexture, bounds.x, bounds.y, bounds.width, bounds.height);

            String item = menuItems[i];
            glyphLayout.setText(menuFont, item);
            float textX = bounds.x + (bounds.width - glyphLayout.width) / 2;
            float textY = bounds.y + (bounds.height + glyphLayout.height) / 2;

            if (i == 0 && selectedLevel < 0) { // START GAME відключена
                menuFont.setColor(Color.GRAY);
            } else if (i == selectedItem) {
                menuFont.setColor(Color.YELLOW);
            } else {
                menuFont.setColor(Color.WHITE);
            }

            menuFont.draw(batch, item, textX, textY);
        }

        // Інструкції внизу
        menuFont.setColor(Color.LIGHT_GRAY);
        menuFont.getData().setScale(1.5f);
        String instruction = "Mouse/Keys + ENTER";
        glyphLayout.setText(menuFont, instruction);
        float instrX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2;
        menuFont.draw(batch, instruction, instrX, 50);
        menuFont.getData().setScale(2.5f);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            // Оновлюємо межі кнопок при повторному відкритті меню
            initializeButtonBounds();
            initializeLevelButtonBounds();
        }
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int level) {
        if (level >= 0 && level < levelItems.length) {
            this.selectedLevel = level;
        }
    }

    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (menuFont != null) menuFont.dispose();
        if (levelFont != null) levelFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
        if (buttonDisabledTexture != null) buttonDisabledTexture.dispose();
        if (levelButtonTexture != null) levelButtonTexture.dispose();
        if (levelButtonSelectedTexture != null) levelButtonSelectedTexture.dispose();
    }
}
