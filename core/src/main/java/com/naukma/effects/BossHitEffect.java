package com.naukma.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class BossHitEffect {

    private static final float EFFECT_SCALE = 0.1f; // Ефект буде більшим

    private Array<EffectParticle> activeParticles;
    private Animation<TextureRegion> animation;
    private float frameDuration;

    public BossHitEffect(String texturePath, int frameCount, float frameDuration) {
        this.frameDuration = frameDuration;
        Texture effectTexture = new Texture(texturePath);
        TextureRegion[][] tmp = TextureRegion.split(effectTexture, effectTexture.getWidth() / frameCount, effectTexture.getHeight());
        
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(tmp[0][i]);
        }
        
        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
        activeParticles = new Array<>();
    }

    public void spawn(float x, float y) {
        activeParticles.add(new EffectParticle(x, y));
    }

    public void update(float delta) {
        for (int i = activeParticles.size - 1; i >= 0; i--) {
            EffectParticle p = activeParticles.get(i);
            p.update(delta);
            if (p.isFinished()) {
                activeParticles.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (EffectParticle p : activeParticles) {
            p.render(batch);
        }
    }

    public void dispose() {
        if (animation != null && animation.getKeyFrames().length > 0) {
            animation.getKeyFrames()[0].getTexture().dispose();
        }
    }

    private class EffectParticle {
        private float x, y, stateTime;
        private boolean finished;

        EffectParticle(float x, float y) {
            this.x = x;
            this.y = y;
            this.stateTime = 0;
            this.finished = false;
        }

        void update(float delta) {
            stateTime += delta;
            if (animation.isAnimationFinished(stateTime)) {
                finished = true;
            }
        }

        void render(SpriteBatch batch) {
            if (!finished) {
                TextureRegion currentFrame = animation.getKeyFrame(stateTime);
                float width = currentFrame.getRegionWidth() * EFFECT_SCALE;
                float height = currentFrame.getRegionHeight() * EFFECT_SCALE;
                batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
            }
        }

        boolean isFinished() {
            return finished;
        }
    }
} 