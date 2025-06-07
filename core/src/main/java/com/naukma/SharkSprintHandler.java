package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class SharkSprintHandler {
    private GameHUD gameHUD;
    private float baseSpeed;
    private float currentSpeed;

    public SharkSprintHandler(GameHUD hud, float sharkBaseSpeed) {
        this.gameHUD = hud;
        this.baseSpeed = sharkBaseSpeed;
        this.currentSpeed = baseSpeed;
    }

    public void handleInput() {
        // Check if Shift is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            gameHUD.startSprint();
        } else {
            gameHUD.stopSprint();
        }
    }

    public void updateSpeed() {
        currentSpeed = baseSpeed * gameHUD.getSpeedMultiplier();
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setBaseSpeed(float speed) {
        this.baseSpeed = speed;
    }
}
