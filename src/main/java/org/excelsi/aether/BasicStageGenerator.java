package org.excelsi.aether;


import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.NullMatrixMSpace;
import org.excelsi.aether.Ground;
import org.excelsi.aether.Level;


public class BasicStageGenerator implements StageGenerator {
    private final Spacemaker _sm;


    public BasicStageGenerator(final Spacemaker sm) {
        _sm = sm;
    }

    @Override public Stage generate(final LevelRecipe r) {
        final Level m = new Level(r.getWidth(), r.getHeight());
        m.setName(r.getName());
        m.setFloor(r.getOrdinal());
        _sm.build(r, m);
        for(Mixin a:r.getMixins()) {
            a.mix(m);
        }
        return m;
    }
}
