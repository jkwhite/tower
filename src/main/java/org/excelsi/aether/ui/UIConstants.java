package org.excelsi.aether.ui;


import com.jme3.math.Vector3f;


public class UIConstants {
    public static final String QUEUE_JME = "jme";
    public static final String QUEUE_JFX = "jfx";

    public static final float SCALE = 2.1f;
    public static final float HORIZ_RATIO = SCALE;
    public static final float VERT_MULT = 1.8f;
    public static final float VERT_RATIO = VERT_MULT*SCALE;
    public static final float STACK_HEIGHT = 0.30f;
    public static final Vector3f UP = new Vector3f(0,1,0);
    public static final Vector3f ZERO = new Vector3f(0,0,0);

    public static final float INC = (float) Math.PI/4f;
    public static final float[] ROTATIONS = {0f, INC, 2f*INC, 3f*INC, 4f*INC, 5f*INC, 6f*INC, 7f*INC};

    private UIConstants() {}
}
