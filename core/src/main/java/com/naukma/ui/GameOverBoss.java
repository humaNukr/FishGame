package com.naukma.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class GameOverBoss extends GameOverMenu {

    public GameOverBoss() {
        super();
        this.gameOverReason = "Out of lives!";
        this.menuItems = new String[]{"Main Menu"};
        initializeButtonBounds();
    }

    @Override
    protected void initializeButtonBounds() {
        buttonBounds = new Rectangle[menuItems.length];
        float buttonWidth = 250;
        float buttonHeight = 50;
        float itemY = menuY + menuHeight - 320;

        for (int i = 0; i < menuItems.length; i++) {
            float buttonX = menuX + (menuWidth - buttonWidth) / 2;
            buttonBounds[i] = new Rectangle(buttonX, itemY - buttonHeight / 2, buttonWidth, buttonHeight);
            itemY -= 60;
        }
    }

    @Override
    protected void handleSelection(int itemIndex) {
        if (itemIndex == 0) { // Return to Main Menu
            shouldReturnToMainMenu = true;
            isActive = false;
        }
    }
} 