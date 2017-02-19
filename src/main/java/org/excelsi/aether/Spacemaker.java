package org.excelsi.aether;


import java.util.List;
import java.util.Random;

import org.excelsi.nausicaa.ca.Archetype;
import org.excelsi.nausicaa.ca.CA;
import org.excelsi.nausicaa.ca.ComputedRuleset;
import org.excelsi.nausicaa.ca.Palette;
import org.excelsi.nausicaa.ca.Plane;
import org.excelsi.nausicaa.ca.Rule;
import org.excelsi.nausicaa.ca.Initializer;
import org.excelsi.nausicaa.ca.Initializers;
import org.excelsi.nausicaa.ca.RandomInitializer;


@FunctionalInterface
public interface Spacemaker {
    void build(LevelRecipe r, Level l);

    public static Spacemaker expanse() {
        return (r,l)->{
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    l.setSpace(r.getSpaces().create(Ground.class), i, j);
                }
            }
        };
    }

    public static Spacemaker ca(List<Integer> dims, int seed, float zeroWeight, String incantation, List<Class<? extends NHSpace>> smap) {
        final Archetype a = new Archetype(dims.size()-1, 1, smap.size());
        final Rule rule = new ComputedRuleset(a).create(incantation);
        final Palette pal = Palette.grey(a.colors());
        final Random rand = new Random(seed);
        final Initializer init = new RandomInitializer(rand, seed, new RandomInitializer.Params(zeroWeight));
        final CA ca = new CA(rule, pal, init, rand, seed, dims.get(0), dims.get(1), dims.get(2), dims.size()==4?dims.get(3):0);

        return (r,l)->{
            final Plane p = ca.createPlane();
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    Class<? extends NHSpace> type = smap.get(p.getCell(i,j));
                    if(type!=null) {
                        l.setSpace(r.getSpaces().create(type), i, j);
                    }
                }
            }
        };
    }
}
