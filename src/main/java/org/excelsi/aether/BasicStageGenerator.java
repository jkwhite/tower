package org.excelsi.aether;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.NullMatrixMSpace;


public class BasicStageGenerator implements StageGenerator {
    private final Map<String,List<String>> _environs;
    private final Map<String,Ingredient> _ings;


    public BasicStageGenerator(final Map<String,List<String>> environs, final List<Ingredient> ings) {
        _environs = environs;
        _ings = new HashMap<>();
        for(Ingredient ing:ings) {
            _ings.put(ing.getName(), ing);
        }
    }

    @Override public Stage generate(final LevelRecipe r) {
        System.err.println("initial recipe: "+r);
        final Set<Ingredient> mix = new HashSet<>();
        for(final String i:r.getIngredients()) {
            final List<String> ings = _environs.get(i);
            if(ings!=null) {
                for(final String ing:ings) {
                    final Ingredient in = _ings.get(ing);
                    if(in!=null) {
                        mix.add(in);
                    }
                    else {
                        System.err.println("missing ingredient "+ing);
                    }
                }
            }
        }
        /*
        for(Ingredient i:_ing) {
            if(i.matches(r)) {
                i.mix(r);
            }
        }
        */
        for(Ingredient ing:mix) {
            ing.mix(r);
        }
        System.err.println("final recipe: "+r);
        final Level m = new Level(r.getWidth(), r.getHeight());
        m.setName(r.getName());
        m.setRealm(r.getRealm());
        m.setFloor(r.getOrdinal());
        m.setDisplayedFloor(r.getDisplayedOrdinal());
        r.getSpacemaker().build(r, m);
        for(Mixin a:r.getMixins()) {
            a.mix(m);
        }
        return m;
    }
}
