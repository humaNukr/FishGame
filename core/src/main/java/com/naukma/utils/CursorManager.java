package com.naukma.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

public class CursorManager implements Disposable {

    private static CursorManager instance;
    private Cursor customCursor;
    private Pixmap cursorPixmap;

    private CursorManager() {
        // Private constructor for singleton
        try {
            cursorPixmap = new Pixmap(Gdx.files.internal("cursor (8).png"));
            
            int width = cursorPixmap.getWidth();
            int height = cursorPixmap.getHeight();

            // LibGDX cursor pixmaps on some backends need to be power-of-two
            if (!isPowerOfTwo(width) || !isPowerOfTwo(height)) {
                int newWidth = nextPowerOfTwo(width);
                int newHeight = nextPowerOfTwo(height);
                
                Pixmap resizedPixmap = new Pixmap(newWidth, newHeight, cursorPixmap.getFormat());
                // Draw the original pixmap onto the new resized one
                resizedPixmap.drawPixmap(cursorPixmap, 0, 0, width, height, 0, 0, newWidth, newHeight);
                
                // Dispose the original pixmap and replace it with the resized one
                cursorPixmap.dispose();
                cursorPixmap = resizedPixmap;
                
                Gdx.app.log("CursorManager", "Cursor image resized to " + newWidth + "x" + newHeight);
            }
            
            customCursor = Gdx.graphics.newCursor(cursorPixmap, 0, 0);
            Gdx.graphics.setCursor(customCursor);
            Gdx.app.log("CursorManager", "Custom cursor has been set successfully.");

        } catch (Exception e) {
            Gdx.app.error("CursorManager", "Failed to set custom cursor", e);
            if (cursorPixmap != null) {
                cursorPixmap.dispose();
                cursorPixmap = null;
            }
        }
    }

    public static void initialize() {
        if (instance == null) {
            instance = new CursorManager();
        }
    }

    @Override
    public void dispose() {
        if (customCursor != null) {
            // It's good practice to reset to the system cursor
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            customCursor.dispose();
            customCursor = null;
            Gdx.app.log("CursorManager", "Custom cursor disposed.");
        }
        if (cursorPixmap != null) {
            cursorPixmap.dispose();
            cursorPixmap = null;
        }
        instance = null;
    }

    public static void disposeInstance() {
        if (instance != null) {
            instance.dispose();
        }
    }
    
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
    
    private static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
} 