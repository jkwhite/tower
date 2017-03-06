/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether;


public class Heightmap implements Mixin<Level> {
    private final float _coef;


    public Heightmap() {
        this(1f);
    }

    public Heightmap(final float coef) {
        _coef = coef;
    }

    public boolean match(Class c) {
        return c==Level.class;
    }

    public void mix(Level level) {
        final float[][] depths = new float[level.width()][level.height()];
        //mixBasin(level, depths);
        mixHills(level, depths, -_coef);
        for(int i=0;i<level.width();i++) {
            for(int j=0;j<level.height();j++) {
                NHSpace s = level.getSpace(i,j);
                if(s!=null) {
                    // algorithm will usually result in terrain
                    // above alt 0, so lower by 10 as a heuristic
                    int alt = -(int)depths[i][j];
                    s.setAltitude(alt);
                }
            }
        }
    }

    private void mixHills(final Level level, final float[][] depths, final float coef) {
        //final int tot = Rand.om.nextInt(20)+3;
        final int tot = (int)(Math.sqrt(level.width()*level.height())/10f*Rand.om.nextFloat()+3);
        System.err.println("making "+tot+" hills");
        for(int i=0;i<tot;i++) {
            final int cx = Rand.om.nextInt(level.width());
            final int cy = Rand.om.nextInt(level.height());
            final int rad = Rand.om.nextInt(15)+5;
            for(int q=0;q<level.width();q++) {
                for(int w=0;w<level.height();w++) {
                    float dist = Math.abs((float)Math.hypot(Math.abs(cx-q), Math.abs(2*(cy-w))));
                    float adj = rad-dist > 0 ? rad-dist : 0;
                    if(adj>0) {
                        adj *= coef;
                        //System.err.println("adj: "+adj);
                    }
                    depths[q][w] += adj;
                }
            }
        }
    }

    private void mixBasin(Level level, float[][] depths) {
        final int tot = Rand.om.nextInt(10)+3;
        for(int i=0;i<tot;i++) {
            final int cx = Rand.om.nextInt(level.width());
            final int cy = Rand.om.nextInt(level.height());
            final int rad = Rand.om.nextInt(35)+5;
            for(int q=0;q<level.width();q++) {
                for(int w=0;w<level.height();w++) {
                    float adj = rad - (float)Math.hypot(Math.abs(cx-q), Math.abs(2*(cy-w)));
                    if(adj>0) {
                        adj *= 2f;
                    }
                    depths[q][w] += adj;
                }
            }
        }
    }
}
