package org.excelsi.aether;


import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.Id;


public class Skelevel extends Id implements Level {
    private LevelRecipe _recipe;
    private LevelGenerator _gen;
    private Stage _level;


    public Skelevel(final LevelGenerator gen, final LevelRecipe r) {
        _gen = gen;
        _recipe = r;
    }

    @Override public String getObjectType() {
        return "level";
    }

    @Override public String getName() {
        return _recipe.getName();
    }

    @Override public int getOrdinal() {
        return _recipe.getOrdinal();
    }

    @Override public Matrix getMatrix() {
        if(!hasLevel()) {
            setLevel(_gen.generate(_recipe));
        }
        return getLevel().getMatrix();
    }

    @Override public void tick(final Context c) {
    }

    public LevelRecipe getRecipe() {
        return _recipe;
    }

    public boolean hasLevel() {
        return _level!=null;
    }

    public Stage getLevel() {
        return _level;
    }

    public void setLevel(final Stage level) {
        _level = level;
    }
}
