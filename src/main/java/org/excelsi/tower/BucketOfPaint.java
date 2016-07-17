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
package org.excelsi.tower;


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class BucketOfPaint extends Tool implements Useable, Combustible {
    private String _paintColor = "gray";
    private int _uses = 100;


    public float getLevelWeight() { return 0.2f; }

    public void setPaint(String color) {
        _paintColor = color;
        setName("bucket of "+_paintColor+" paint");
    }

    public String getObscuredName() {
        return "bucket of paint";
    }

    public void randomize() {
        super.randomize();
        if(_paintColor==null) {
            if(Universe.getUniverse()!=null&&Universe.getUniverse().getColormap()!=null) {
                String[] cols = Universe.getUniverse().getColormap().keySet().toArray(new String[0]);
                setPaint(cols[Rand.om.nextInt(cols.length)]);
            }
        }
    }

    public boolean equals(Object o) {
        return super.equals(o) && _paintColor.equals(((BucketOfPaint)o)._paintColor);
    }

    public void use(NHBot b) {
        if(_uses==0) {
            N.narrative().print(b, "This bucket is all used up.");
            throw new ActionCancelledException();
        }
        --_uses;
        Direction chosen = N.narrative().direct(b, "Which direction?");
        paint(b, chosen);
    }

    public int getFindRate() {
        return 2;
    }

    public float getWeight() {
        return _uses/20f;
    }

    public float getSize() {
        return 1f;
    }

    public void paint(NHBot b, Direction d) {
        NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d);
        if(s==null) {
            N.narrative().print(b, "You paint the darkness.");
            throw new ActionCancelledException();
        }
        s.setColor(_paintColor);
    }

    public boolean isCombustible() {
        return true;
    }

    public int getCombustionTemperature() {
        return 200;
    }

    public String getCombustionPhrase() {
        return "explodes";
    }

    public void combust(Container c) {
        c.consume(this);
        if(c instanceof NHSpace) {
            NHSpace s = (NHSpace) c;
        }
    }
}
