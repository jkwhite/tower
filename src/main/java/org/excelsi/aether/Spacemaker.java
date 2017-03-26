package org.excelsi.aether;


import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Consumer;

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

    default public Spacemaker and(Spacemaker m) {
        return (r,l)->{ this.build(r,l); m.build(r,l); };
    }

    public static Spacemaker circle(int x, int y, int rad, Class<? extends NHSpace> line, Class<? extends NHSpace> fill, int chance) {
        return (r,l)->{
            final int minI = Math.max(0,x-rad);
            final int minJ = Math.max(0,y-rad);
            final int maxI = Math.min(x+rad,r.getWidth());
            final int maxJ = Math.min(y+rad,r.getHeight());
            final int rad2 = rad*rad;
            final int rad21 = (1+rad)*(1+rad);
            for(int i=minI;i<=maxI;i++) {
                for(int j=minJ;j<=maxJ;j++) {
                    final float d = D.distance2(x, y, i, j);
                    if(d>=rad2 && d <= rad21) {
                        if(line!=null && Rand.d100(chance)) {
                            l.setSpace(r.getSpaces().create(line), i, j);
                        }
                    }
                    else if(d<rad2) {
                        if(fill!=null && Rand.d100(chance)) {
                            l.setSpace(r.getSpaces().create(fill), i, j);
                        }
                    }
                }
            }
        };
    }

    public static Spacemaker line(int x1, int y1, int x2, int y2, Class<? extends NHSpace> line, int chance) {
        return (r,l)->{
            if(x1==x2&&y1==y2) {
                return;
            }
            float dist = D.distance(x1,y1,x2,y2);
            for(float p=0;p<=1f;p+=1f/dist) {
                float i = ((x2*p)+(x1*(1f-p)));
                float j = ((y2*p)+(y1*(1f-p)));
                int ii = (int) i;
                int ij = (int) j;
                if(ii>=0 && ii<l.width() && ij>=0 && ij<l.height()) {
                    if(Rand.d100(chance)) {
                        l.setSpace(r.getSpaces().create(line), ii, ij);
                    }
                }
            }
        };
    }

    public static Spacemaker nothing() {
        return (r,l)->{
        };
    }

    public static Spacemaker expanse() {
        return (r,l)->{
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    l.setSpace(r.getSpaces().create(Ground.class), i, j);
                }
            }
        };
    }

    public static Spacemaker expanse(Class<? extends NHSpace>... spaces) {
        return (r,l)->{
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    l.setSpace(r.getSpaces().create(spaces[Rand.om.nextInt(spaces.length)]), i, j);
                }
            }
        };
    }

    public static Spacemaker modulator(final Consumer<NHSpace> m) {
        return (r,l)->{
            for(int i=0;i<r.getWidth();i++) {
                for(int j=0;j<r.getHeight();j++) {
                    m.accept(l.getSpace(i,j));
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
                    if(type==null) {
                        type = Blank.class;
                    }
                    final NHSpace sp = r.getSpaces().create(type);
                    modulator.accept(idx, sp);
                    l.setSpace(sp, i, j);
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
