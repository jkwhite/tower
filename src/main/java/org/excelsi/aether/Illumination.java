package org.excelsi.aether;


public class Illumination implements Mixin<Level> {
    private final float _intensity;
    private final String _color;


    public Illumination(final float intensity, final String color) {
        _intensity = intensity;
        _color = color;
    }

    @Override public void mix(Level l) {
        l.putProperty(Keys.LIGHT_COLOR, _color);
        l.setLight(_intensity);
    }
}
