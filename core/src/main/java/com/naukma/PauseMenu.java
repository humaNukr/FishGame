package com.naukma;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class PauseMenu {
    private Texture menuBackground;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private final String[] menuItems = {"Resume", "Restart", "Exit"};
    private int selectedItem = 0;
    private float menuX, menuY;
    private float menuWidth, menuHeight;
    private final GlyphLayout layout;

    public PauseMenu() {
        menuBackground = new Texture(Gdx.files.internal("menu-bg.jfif"));
        titleFont = new BitmapFont();
        menuFont = new BitmapFont();

        titleFont.getData().setScale(3);
        menuFont.getData().setScale(2);

        titleFont.setColor(Color.GOLDENROD);
        menuFont.setColor(Color.BROWN);

        menuWidth = Gdx.graphics.getWidth() * 0.4f;
        menuHeight = Gdx.graphics.getHeight() * 0.6f;
        menuX = (Gdx.graphics.getWidth() - menuWidth) / 2;
        menuY = (Gdx.graphics.getHeight() - menuHeight) / 2;

        layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch) {
        // Малюємо фон меню (свиток)
        batch.draw(menuBackground, menuX, menuY, menuWidth, menuHeight);

        // Малюємо заголовок
        layout.setText(titleFont, "Paused");
        titleFont.draw(batch, "Paused",
            menuX + (menuWidth - layout.width) / 2,
            menuY + menuHeight - 50);

        // Малюємо пункти меню
        float itemY = menuY + menuHeight - 150;
        for (int i = 0; i < menuItems.length; i++) {
            layout.setText(menuFont, menuItems[i]);
            if (i == selectedItem) {
                menuFont.setColor(Color.GOLD);
            } else {
                menuFont.setColor(Color.BROWN);
            }
            menuFont.draw(batch, menuItems[i],
                menuX + (menuWidth - layout.width) / 2,
                itemY);
            itemY -= 60;
        }
    }

    public void dispose() {
        menuBackground.dispose();
        titleFont.dispose();
        menuFont.dispose();
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
}
