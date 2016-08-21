package org.excelsi.aether;


import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.NullMatrixMSpace;
import org.excelsi.aether.Ground;
import org.excelsi.aether.Level;


public class ExpanseLevelGenerator implements StageGenerator {
    @Override public Stage generate(final LevelRecipe recipe) {
        //final Matrix m = new Matrix(recipe.getWidth(), recipe.getHeight());
        final Level m = new Level(recipe.getWidth(), recipe.getHeight());
        for(int i=0;i<recipe.getWidth();i++) {
            for(int j=0;j<recipe.getHeight();j++) {
                if(true||recipe.getRandom().nextBoolean()) {
                    //MatrixMSpace ms = new NullMatrixMSpace();
                    m.setSpace(new Ground(), i, j);
                }
            }
        }
        //return new MatrixLevel(recipe.getName(), recipe.getOrdinal(), m);
        m.setName(recipe.getName());
        m.setFloor(recipe.getOrdinal());
        return m;
    }
}
