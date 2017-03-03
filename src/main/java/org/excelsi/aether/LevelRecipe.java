package org.excelsi.aether;


import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LevelRecipe {
    private int _w;
    private int _h;
    private Skelevel _skel = new Skelevel().partition(new Skelevel.Partition(1,1));
    private String _name;
    private String _realm;
    private int _ordinal;
    private Random _r;
    private SpaceFactory _spaces = Spaces.identity();
    private Spacemaker _sm = Spacemaker.expanse();
    private List<Mixin<Level>> _mixins;
    private List<String> _ingredients = new ArrayList<>();


    public LevelRecipe() {
    }

    public LevelRecipe spacemaker(Spacemaker sm) {
        _sm = sm;
        return this;
    }

    public Spacemaker getSpacemaker() {
        return _sm;
    }

    public LevelRecipe ingredient(final String ing) {
        _ingredients.add(ing);
        return this;
    }

    public boolean requires(final String req) {
        return _ingredients.contains(req);
    }

    public List<String> getIngredients() {
        return _ingredients;
    }

    public LevelRecipe skel(Skelevel skel) {
        _skel = skel;
        return this;
    }

    public Skelevel getSkel() {
        return _skel;
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

    @Override public String toString() {
        return String.format("LevelRecipe::{name:\"%s\", realm:\"%s\", ordinal:%d, width:%d, height:%d, skel:%s, requirements:%s, mixins:%s}",
            _name, _realm, _ordinal, _w, _h, _skel.toString(), _ingredients, getMixins());
    }
}
