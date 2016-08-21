package org.excelsi.aether.ui;


import java.util.ResourceBundle;

import org.lwjgl.opengl.Display;


public final class Resources {
    public static ResourceBundle jfxResources() {
        return new ListResource(new Object[][]{
            {"screen_width", Integer.toString(Display.getWidth())},
            {"screen_height", Integer.toString(Display.getHeight())}
        });
    }

    private Resources() {}
}
