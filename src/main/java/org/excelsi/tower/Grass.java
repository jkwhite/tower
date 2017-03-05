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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class Grass extends Floor implements Combustible, Fertile {
    private static final long serialVersionUID = 1L;
    private int _type;
    private int _combustible = 0;


    public Grass() {
        this(Rand.om.nextInt(4));
    }

    public Grass(int type) {
        super(type%2==0?"green":"light-green");
        if(type<0||type>3) {
            throw new IllegalArgumentException("0<=type<4");
        }
        _type = type;
    }

    public String getModel() {
        return _type<2?"\"":"'";
    }

    public boolean isStretchy() {
        return true;
    }

    @Override public Orientation getOrientation() {
        return Orientation.upright;
    }

    @Override public Origin getOrigin() {
        return Origin.natural;
    }

    public boolean isCombustible() {
        return _combustible==0;
    }

    public int getCombustionTemperature() {
        return 200;
    }

    public String getCombustionPhrase() {
        return "ignites";
    }

    public void combust(Container c) {
        if(!hasParasite(Burning.class)) {
            boolean p = true;
            for(MSpace m:surrounding()) {
                if(m !=null && ((NHSpace)m).hasParasite(Burning.class)) {
                    p = false;
                    break;
                }
            }
            if(p) {
                N.narrative().print(this, "The grass ignites!");
            }
            addParasite(new Burning());
            _combustible = 200;
        }
    }

    public void update() {
        super.update();
        if(_combustible>0) {
            _combustible--;
        }
    }
}
