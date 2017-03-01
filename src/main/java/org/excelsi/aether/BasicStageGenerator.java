package org.excelsi.aether;


import java.util.List;

import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.NullMatrixMSpace;


public class BasicStageGenerator implements StageGenerator {
    private final List<Ingredient> _ing;


    public BasicStageGenerator(final List<Ingredient> ing) {
        _ing = ing;
    }

    @Override public Stage generate(final LevelRecipe r) {
        System.err.println("initial recipe: "+r);
        for(Ingredient i:_ing) {
            if(i.matches(r)) {
                i.mix(r);
            }
        }
        System.err.println("final recipe: "+r);
        final Level m = new Level(r.getWidth(), r.getHeight());
        m.setName(r.getName());
        m.setFloor(r.getOrdinal());
        r.getSpacemaker().build(r, m);
        for(Mixin a:r.getMixins()) {
            a.mix(m);
        }
        return m;
    }
}
