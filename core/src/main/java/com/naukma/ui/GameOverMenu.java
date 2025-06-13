package com.naukma.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;

public class GameOverMenu {
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private BitmapFont reasonFont;
    private Texture menuBackground;
    private Texture buttonTexture;
    private Texture buttonHoverTexture;
    private Texture overlayTexture;

    private String[] menuItems = {"Restart", "Main Menu", "Exit Game"};
    private Rectangle[] buttonBounds;
    private int selectedItem = 0;
    private boolean isActive = false;
    private boolean shouldReturnToMainMenu = false;
    private boolean shouldRestart = false;
    private boolean shouldExitGame = false;

    private String gameOverReason = "Game Over!";

    private float menuX, menuY;
    private float menuWidth, menuHeight;
    private GlyphLayout glyphLayout;

    private Sound clickSound;

    private int prevSelectedItem = -1;

    public GameOverMenu() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HennyPenny.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Налаштування для заголовка
        parameter.size = 48;
        parameter.color = Color.RED;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // Налаштування для причини програшу
        parameter.size = 32;
        parameter.color = Color.ORANGE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.DARK_GRAY;
        reasonFont = generator.generateFont(parameter);

        // Налаштування для меню
        parameter.size = 24;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        menuFont = generator.generateFont(parameter);

        // ВАЖЛИВО: Звільняємо генератор після використання
        generator.dispose();

        glyphLayout = new GlyphLayout();

        // Розмір меню
        menuWidth = Gdx.graphics.getWidth() * 0.4f;
        menuHeight = Gdx.graphics.getHeight() * 0.7f;
        menuX = (Gdx.graphics.getWidth() - menuWidth) / 2;
        menuY = (Gdx.graphics.getHeight() - menuHeight) / 2;

        try {
            menuBackground = new Texture(Gdx.files.internal("menu-bg.jfif"));
        } catch (Exception e) {
            menuBackground = null;
        }

        createTextures();
        initializeButtonBounds();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("button.wav"));
    }

    private void createTextures() {
        // Напівпрозорий фон для затемнення екрана
        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.8f); // Трохи темніше для драматичності
        pixmap.fill();
        overlayTexture = new Texture(pixmap);
        pixmap.dispose();

        // Звичайна кнопка (червонуватий відтінок для Game Over)
        pixmap = new Pixmap(250, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.8f, 0.2f, 0.3f, 0.8f); // Червонуватий фон
        pixmap.fill();
        pixmap.setColor(1f, 0.4f, 0.5f, 1f); // Світло-червона рамка
        pixmap.drawRectangle(0, 0, 250, 50);
        buttonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Кнопка при наведенні
        pixmap = new Pixmap(250, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0.4f, 0.5f, 0.9f);
        pixmap.fill();
        pixmap.setColor(1f, 0.6f, 0.7f, 1f);
        pixmap.drawRectangle(0, 0, 250, 50);
        buttonHoverTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initializeButtonBounds() {
        buttonBounds = new Rectangle[menuItems.length];
        float buttonWidth = 250;
        float buttonHeight = 50;
        float itemY = menuY + menuHeight - 320; // Опускаємо кнопки нижче через додатковий текст

        for (int i = 0; i < menuItems.length; i++) {
            float buttonX = menuX + (menuWidth - buttonWidth) / 2;
            buttonBounds[i] = new Rectangle(buttonX, itemY - buttonHeight/2, buttonWidth, buttonHeight);
            itemY -= 60;
        }
    }

    public void handleInput() {
        if (!isActive) return;

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        prevSelectedItem = selectedItem;

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
            // Клавіатура працює тільки якщо миша не навела на кнопку
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedItem--;
                if (selectedItem < 0) selectedItem = menuItems.length - 1;
                if (clickSound != null) clickSound.play();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedItem++;
                if (selectedItem >= menuItems.length) selectedItem = 0;
                if (clickSound != null) clickSound.play();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                handleSelection(selectedItem);
                if (clickSound != null) clickSound.play();
            }
        }

        // R для швидкого рестарту
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            handleSelection(0); // Restart
        }

        if (Gdx.input.justTouched()) {
            for (int i = 0; i < buttonBounds.length; i++) {
                if (buttonBounds[i].contains(mouseX, mouseY)) {
                    if (clickSound != null) clickSound.play();
                    handleSelection(i);
                    break;
                }
            }
        }
    }

    private void handleSelection(int itemIndex) {
        switch (itemIndex) {
            case 0: // Restart
                shouldRestart = true;
                isActive = false;
                break;
            case 1: // Main Menu
                shouldReturnToMainMenu = true;
                isActive = false;
                break;
            case 2: // Exit Game
                shouldExitGame = true;
                isActive = false;
                break;
        }
    }

    public void render(SpriteBatch batch) {
        if (!isActive) return;

        // Затемнюємо фон
        batch.draw(overlayTexture, 0, 0);

        // Малюємо фон меню
        if (menuBackground != null) {
            batch.draw(menuBackground, menuX, menuY, menuWidth, menuHeight);
        } else {
            // Якщо немає фонової текстури, створюємо простий фон з червонуватим відтінком
            Pixmap pixmap = new Pixmap((int)menuWidth, (int)menuHeight, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.3f, 0.2f, 0.2f, 0.9f); // Темно-червонуватий фон
            pixmap.fill();
            pixmap.setColor(0.6f, 0.4f, 0.4f, 1f);
            pixmap.drawRectangle(0, 0, (int)menuWidth, (int)menuHeight);
            Texture fallbackBg = new Texture(pixmap);
            batch.draw(fallbackBg, menuX, menuY, menuWidth, menuHeight);
            fallbackBg.dispose();
            pixmap.dispose();
        }

        // Малюємо заголовок "GAME OVER"
        glyphLayout.setText(titleFont, "GAME OVER");
        titleFont.draw(batch, "GAME OVER",
            menuX + (menuWidth - glyphLayout.width) / 2,
            menuY + menuHeight - 60);

        // Малюємо причину програшу
        glyphLayout.setText(reasonFont, gameOverReason);
        reasonFont.draw(batch, gameOverReason,
            menuX + (menuWidth - glyphLayout.width) / 2,
            menuY + menuHeight - 120);

        // Малюємо кнопки
        for (int i = 0; i < menuItems.length; i++) {
            Rectangle bounds = buttonBounds[i];

            // Малюємо кнопку з текстурою
            Texture currentButtonTexture = (i == selectedItem) ? buttonHoverTexture : buttonTexture;
            batch.draw(currentButtonTexture, bounds.x, bounds.y, bounds.width, bounds.height);

            // Малюємо текст на кнопці
            String item = menuItems[i];
            glyphLayout.setText(menuFont, item);
            float textX = bounds.x + (bounds.width - glyphLayout.width) / 2;
            float textY = bounds.y + (bounds.height + glyphLayout.height) / 2;

            if (i == selectedItem) {
                menuFont.setColor(Color.GOLD);
            } else {
                menuFont.setColor(Color.WHITE);
            }

            menuFont.draw(batch, item, textX, textY);
        }

        // Інструкції внизу меню
        menuFont.setColor(Color.LIGHT_GRAY);
        String instruction = "R - Restart | Mouse/Keys + ENTER";
        glyphLayout.setText(menuFont, instruction);
        float instrX = menuX + (menuWidth - glyphLayout.width) / 2;
        float instrY = menuY + 30;
        menuFont.draw(batch, instruction, instrX, instrY);
    }

    public void moveUp() {
        selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
    }

    public void moveDown() {
        selectedItem = (selectedItem + 1) % menuItems.length;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            selectedItem = 0; // Скидаємо на першу позицію при відкритті
            shouldReturnToMainMenu = false; // Скидаємо флаг
            shouldRestart = false; // Скидаємо флаг рестарту
            shouldExitGame = false; // Скидаємо флаг виходу
            initializeButtonBounds(); // Оновлюємо межі кнопок
        }
    }

    public boolean shouldReturnToMainMenu() {
        return shouldReturnToMainMenu;
    }

    public boolean shouldRestart() {
        return shouldRestart;
    }

    public boolean shouldExitGame() {
        return shouldExitGame;
    }

    public void resetFlags() {
        shouldReturnToMainMenu = false;
        shouldRestart = false;
        shouldExitGame = false;
    }

    public void setGameOverReason(String reason) {
        this.gameOverReason = reason;
    }

    public String getGameOverReason() {
        return gameOverReason;
    }

    public void show() {
        setActive(true);
    }

    public void hide() {
        setActive(false);
    }

    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (menuFont != null) menuFont.dispose();
        if (reasonFont != null) reasonFont.dispose();
        if (menuBackground != null) menuBackground.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
        if (clickSound != null) clickSound.dispose();
    }
}
