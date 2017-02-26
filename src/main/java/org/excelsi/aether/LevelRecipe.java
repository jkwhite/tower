package org.excelsi.aether;


import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LevelRecipe {
    private int _w;
    private int _h;
    private int _partitions = 1;
    private String _name;
    private String _realm;
    private int _ordinal;
    private Random _r;
    private SpaceFactory _spaces = Spaces.identity();
    private List<Mixin<Level>> _mixins;


    public LevelRecipe() {
    }

    public LevelRecipe partitions(int partitions) {
        _partitions = partitions;
        return this;
    }

    public int getPartitions() {
        return _partitions;
    }

    public LevelRecipe mixin(Mixin<Level> m) {
        if(_mixins==null) {
            _mixins = new ArrayList<>();
        }
        _mixins.add(m);
        return this;
    }

    public List<Mixin<Level>> getMixins() {
        return _mixins==null?Collections.<Mixin<Level>>emptyList():_mixins;
    }

    public LevelRecipe spaces(SpaceFactory spaces) {
        _spaces = spaces;
        return this;
    }

    public SpaceFactory getSpaces() {
        return _spaces;
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

    public LevelRecipe realm(String realm) {
        _realm = realm;
        return this;
    }

    public String getRealm() {
        return _realm;
    }

    public LevelRecipe ordinal(int ordinal) {
        _ordinal = ordinal;
        return this;
    }

    public int getOrdinal() {
        return _ordinal;
    }
}
