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

    public static Props props() {
        return new Props(
            Display.getWidth(),
            Display.getHeight()
        );
    }

    static public class Props {
        public final int width;
        public final int height;


        public Props(int w, int h) {
            width = w;
            height = h;
        }
    }

    private Resources() {}
}
