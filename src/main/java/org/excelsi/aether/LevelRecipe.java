package org.excelsi.aether;


import java.util.Random;


public class LevelRecipe {
    private int _w;
    private int _h;
    private String _name;
    private int _ordinal;
    private Random _r;


    public LevelRecipe() {
    }

    public LevelRecipe random(Random r) {
        _r = r;
        return this;
    }

    public Random getRandom() {
        return _r;
    }

    public LevelRecipe width(int w) {
        _w = w;
        return this;
    }

    public int getWidth() {
        return _w;
    }

    public LevelRecipe height(int h) {
        _h = h;
        return this;
    }

    public int getHeight() {
        return _h;
    }

    public LevelRecipe name(String name) {
        _name = name;
        return this;
    }

    public String getName() {
        return _name;
    }

    public LevelRecipe ordinal(int ordinal) {
        _ordinal = ordinal;
        return this;
    }

    public int getOrdinal() {
        return _ordinal;
    }
}
