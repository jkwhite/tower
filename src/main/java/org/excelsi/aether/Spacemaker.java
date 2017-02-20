package org.excelsi.aether;


import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public static Spacemaker ca(List<Integer> dims, int colors, int seed, float zeroWeight, String incantation,
        Function<Integer,Class<? extends NHSpace>> smap, BiConsumer<Integer,NHSpace> modulator) {
        final Archetype a = new Archetype(dims.size()-1, 1, colors);
        final Rule rule = new ComputedRuleset(a).create(incantation);
        final Palette pal = Palette.grey(a.colors());
        final Random rand = new Random(seed);
        final Initializer init = new RandomInitializer(rand, seed, new RandomInitializer.Params(zeroWeight));
        final CA ca = new CA(rule, pal, init, rand, seed, dims.get(0), dims.get(1), dims.get(2), dims.size()==4?dims.get(3):0);

        return (r,l)->{
            final Plane p = ca.createPlane();
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    final int idx = p.getCell(i,j);
                    Class<? extends NHSpace> type = smap.apply(idx);
                    if(type!=null) {
                        final NHSpace sp = r.getSpaces().create(type);
                        modulator.accept(idx, sp);
                        l.setSpace(sp, i, j);
                    }
                }
            }
        };
    }

    public static Function<Integer,Class<? extends NHSpace>> mapIndex(final List<Class<? extends NHSpace>> smap) {
        return (i)->{ return smap.get(i); };
    }

    public static BiConsumer<Integer,NHSpace> mapColor(final List<String> cmap) {
        return (i,s)->{ s.setColor(cmap.get(i)); };
    }
}
