package org.excelsi.aether;


import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.NullMatrixMSpace;
import org.excelsi.aether.Ground;
import org.excelsi.aether.Level;


public class ExpanseLevelGenerator implements StageGenerator {
    @Override public Stage generate(final LevelRecipe r) {
        final Level m = new Level(r.getWidth(), r.getHeight());
        m.setName(r.getName());
        m.setFloor(r.getOrdinal());
        for(int i=0;i<r.getWidth();i++) {
            for(int j=0;j<r.getHeight();j++) {
                if(true||r.getRandom().nextBoolean()) {
                    m.setSpace(r.getSpaces().create(Ground.class), i, j);
                }
            }
        }
        for(Mixin a:r.getMixins()) {
            a.mix(m);
        }
        return m;
    }
}
