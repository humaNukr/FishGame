package com.naukma.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.naukma.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Drop Game");
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        config.setForegroundFPS(60);

        // Імпортуй свій головний клас гри сюди (той що extends ApplicationAdapter)
        new Lwjgl3Application(new Main(), config);
    }
}
