package com.naukma.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.naukma.ui.GameHUD;

public class SharkSprintHandler {
    private GameHUD gameHUD;
    private float baseSpeed;
    private float currentSpeed;
    private boolean isSprintActive;
    private static final float SPRINT_MULTIPLIER = 1.3f; // 30% збільшення швидкості
    private static final float STAMINA_DRAIN_RATE = 20f; // Витрата стаміни в секунду
    private static final float STAMINA_REGEN_RATE = 5f; // Відновлення стаміни в секунду

    public SharkSprintHandler(GameHUD gameHUD, float baseSpeed) {
        this.gameHUD = gameHUD;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.isSprintActive = false;
    }

    public void handleInput() {
        boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        if (shiftPressed && gameHUD.canSprint()) {
            gameHUD.startSprint();
        } else {
            gameHUD.stopSprint();
        }
    }

    public void updateSpeed() {
        isSprintActive = gameHUD.isSprintActive();
        currentSpeed = baseSpeed * gameHUD.getSpeedMultiplier();
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public boolean isSprintActive() {
        return isSprintActive;
    }
}
