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
import com.naukma.Main;

public class SettingsWindow {
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture sliderBg;
    private Texture sliderFill;
    private float volume; // 0.0 - 1.0
    private GlyphLayout glyphLayout;
    private boolean active = true;
    private boolean dragging = false;
    private Texture menuBackground;

    public SettingsWindow() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HennyPenny.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Фон головного меню
        menuBackground = new Texture(Gdx.files.internal("output.jpg"));

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.92f);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Слайдер фон
        Pixmap sliderBgPixmap = new Pixmap(300, 16, Pixmap.Format.RGBA8888);
        sliderBgPixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        sliderBgPixmap.fill();
        sliderBg = new Texture(sliderBgPixmap);
        sliderBgPixmap.dispose();
        // Слайдер заповнення
        Pixmap sliderFillPixmap = new Pixmap(300, 16, Pixmap.Format.RGBA8888);
        sliderFillPixmap.setColor(0.2f, 0.7f, 1f, 1f);
        sliderFillPixmap.fill();
        sliderFill = new Texture(sliderFillPixmap);
        sliderFillPixmap.dispose();

        glyphLayout = new GlyphLayout();
        // Початкове значення гучності з Main
        Main main = (Main) Gdx.app.getApplicationListener();
        this.volume = main.getMusicVolume();
    }

    public void handleInput() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float w = screenW * 0.55f;
        float h = screenH * 0.45f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f;
        float sliderW = w * 0.67f;
        float sliderH = Math.max(12, h * 0.07f);
        float sliderX = x + (w - sliderW) / 2;
        float sliderY = y + h * 0.5f - sliderH / 2;
        float knobRadius = Math.max(10, sliderH * 0.9f);

        int mouseX = Gdx.input.getX();
        int mouseY = (int)(screenH - Gdx.input.getY());
        boolean mouseOverSlider = mouseX >= sliderX && mouseX <= sliderX + sliderW && mouseY >= sliderY && mouseY <= sliderY + sliderH + knobRadius * 2;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && mouseOverSlider) {
            dragging = true;
            updateVolumeByMouse(mouseX, sliderX, sliderW);
        }
        if (dragging) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                updateVolumeByMouse(mouseX, sliderX, sliderW);
            } else {
                dragging = false;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main main = (Main) Gdx.app.getApplicationListener();
            main.getMainMenu().setShowSettings(false);
        }
    }

    private void updateVolumeByMouse(float mouseX, float sliderX, float sliderW) {
        float rel = (mouseX - sliderX) / sliderW;
        setVolume(Math.max(0f, Math.min(1f, rel)));
    }

    public void render(SpriteBatch batch) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float w = screenW * 0.55f;
        float h = screenH * 0.45f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f;
        // Малюємо фон головного меню
        batch.setColor(1, 1, 1, 1);
        batch.draw(menuBackground, 0, 0, screenW, screenH);
        // Додаємо затемнення як у MainMenu
        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(backgroundTexture, 0, 0, screenW, screenH);
        batch.setColor(1, 1, 1, 1);
        // Малюємо чорний напівпрозорий прямокутник
        batch.setColor(0, 0, 0, 0.92f);
        batch.draw(backgroundTexture, x, y, w, h);
        batch.setColor(1, 1, 1, 1);

        float fontSize = Math.max(24, screenH * 0.045f);
        font.getData().setScale(fontSize / 36f);

        font.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
        String title = "SETTINGS";
        glyphLayout.setText(font, title);
        font.draw(batch, title, x + (w - glyphLayout.width) / 2, y + h - h * 0.12f);

        String volumeText = String.format("Music Volume: %.0f%%", volume * 100);
        glyphLayout.setText(font, volumeText);
        font.draw(batch, volumeText, x + (w - glyphLayout.width) / 2, y + h * 0.7f);

        // Слайдер
        float sliderW = w * 0.67f;
        float sliderH = Math.max(12, h * 0.07f);
        float sliderX = x + (w - sliderW) / 2;
        float sliderY = y + h * 0.5f - sliderH / 2;
        batch.draw(sliderBg, sliderX, sliderY, sliderW, sliderH);
        batch.draw(sliderFill, sliderX, sliderY, sliderW * volume, sliderH);

        // Кружечок
        float knobRadius = Math.max(10, sliderH * 0.9f);
        float knobX = sliderX + sliderW * volume;
        float knobY = sliderY + sliderH / 2;
        batch.setColor(0.9f, 0.9f, 0.9f, 1f);
        batch.draw(backgroundTexture, knobX - knobRadius, knobY - knobRadius, knobRadius * 2, knobRadius * 2);
        batch.setColor(0.2f, 0.7f, 1f, 1f);
        batch.draw(sliderFill, knobX - knobRadius + 2, knobY - knobRadius + 2, knobRadius * 2 - 4, knobRadius * 2 - 4);
        batch.setColor(1, 1, 1, 1);

        String hint = "ESC to exit and save settings";
        glyphLayout.setText(font, hint);
        font.draw(batch, hint, x + (w - glyphLayout.width) / 2, y + h * 0.18f);
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        font.getData().setScale(1f);
    }

    public void setVolume(float v) {
        this.volume = v;
        Main main = (Main) Gdx.app.getApplicationListener();
        main.setMusicVolume(v);
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (sliderBg != null) sliderBg.dispose();
        if (sliderFill != null) sliderFill.dispose();
        if (menuBackground != null) menuBackground.dispose();
    }
} 