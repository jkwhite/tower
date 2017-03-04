package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;

import com.jme3.math.ColorRGBA;

import org.excelsi.aether.Universe;


public class Materials {
    private static final Map<String,ColorRGBA> COLORS = new HashMap<>();


    public static String format(final String m) {
        return m.replace(' ','_');
    }

    public static ColorRGBA colorFor(final String name) {
        if(COLORS.isEmpty()) {
            for(String k:Universe.getUniverse().getColormap().keySet()) {
                String v = Universe.getUniverse().getColormap().get(k);
                if(v==null) {
                    throw new IllegalArgumentException("no such color '"+k+"'");
                }
                ColorRGBA conv;
                if(v.startsWith("#")) {
                    float[] rgba = new float[4];
                    for(int i=0;i<rgba.length;i++) {
                        rgba[i] = Integer.parseInt(v.substring(1+2*i, 1+2*i+2), 16)/255f;
                    }
                    conv = new ColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
                }
                else {
                    String[] dsotm = v.split(",");
                    conv = new ColorRGBA(Float.parseFloat(dsotm[0]),
                            Float.parseFloat(dsotm[1]),
                            Float.parseFloat(dsotm[2]),
                            Float.parseFloat(dsotm[3]));
                }
                COLORS.put(k, conv);
            }
        }
        return COLORS.get(name);
    }
}
