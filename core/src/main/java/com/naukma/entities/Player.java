package com.naukma.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {
    private final Animation<TextureRegion> eatingAnimation;
    private final Animation<TextureRegion> swimmingLeftAnimation;
    private final Animation<TextureRegion> swimmingRightAnimation;
    private Animation<TextureRegion> currentAnimation;

    private boolean isEating = false;
    private float stateTime = 0f;
    private float eatingStateTime = 0f;

    private boolean victoryAnimationActive = false;
    private float victoryAnimationTime = 0f;
    private float victorySpeedX = 0;
    private float victorySpeedY = 0;
    private float victoryAcceleration = 400f; // Прискорення для переможного руху

    public Player(int x, int y, float scale, TextureAtlas atlas, TextureAtlas.AtlasRegion... regions) {
        super(x, y);
        this.width = regions[0].getRegionWidth() * scale;
        this.height = regions[0].getRegionHeight() * scale;
        eatingAnimation = new Animation<>(0.1f, atlas.findRegions("shark-eat"), Animation.PlayMode.NORMAL);
        swimmingLeftAnimation = new Animation<>(0.1f, atlas.findRegions("shark-swim-left"), Animation.PlayMode.LOOP);
        swimmingRightAnimation = new Animation<>(0.1f, atlas.findRegions("shark-swim-right"), Animation.PlayMode.LOOP);
        currentAnimation = swimmingRightAnimation;
    }

    public void startVictoryAnimation() {
        victoryAnimationActive = true;
        victoryAnimationTime = 0f;
        if (x < Gdx.graphics.getWidth() / 2) {
            victorySpeedX = 200; // Початкова швидкість вправо
        } else {
            victorySpeedX = -200; // Початкова швидкість вліво
        }
        victorySpeedY = 300; 
    }

    public void update(float delta) {
        stateTime += delta;

        if (victoryAnimationActive) {
            victoryAnimationTime += delta;
            x += victorySpeedX * delta;
            y += victorySpeedY * delta;

            if (victorySpeedX > 0) {
                victorySpeedX += victoryAcceleration * delta;
            } else {
                victorySpeedX -= victoryAcceleration * delta;
            }
            victorySpeedY -= victoryAcceleration * delta * 1.5f; // Гравітація для "сальто"

            return; // Не обробляємо інший рух
        }

        if (isEating) {
            eatingStateTime += delta;
            if (eatingAnimation.isAnimationFinished(eatingStateTime)) {
                isEating = false;
                eatingStateTime = 0f;
            }
        }
    }

    public TextureRegion getFrame() {
        if (isEating) {
            return eatingAnimation.getKeyFrame(eatingStateTime, false);
        }
        return currentAnimation.getKeyFrame(stateTime, true);
    }

    public boolean isVictoryAnimationActive() {
        return victoryAnimationActive;
    }

    public void setX(float x) {
        this.x = x;
    }
} 