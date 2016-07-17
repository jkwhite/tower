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


import org.excelsi.aether.*;


public class PieceOfChalk extends Tool implements WritingUtensil {
    private int _charges = Rand.om.nextInt(20)+5;


    public PieceOfChalk() {
    }

    public float getSize() {
        return 0.1f;
    }

    public float getWeight() {
        return 0.01f;
    }

    public String getColor() {
        return "white";
    }

    public int getFindRate() {
        return 10;
    }

    public void use(NHBot b) {
    }

    public int getCharges() {
        return _charges;
    }

    public void setCharges(int charges) {
        _charges = charges;
    }

    public boolean discharge(NHBot b, Container c) {
        if(_charges==0) {
            throw new IllegalStateException("no charges");
        }
        if(--_charges==0) {
            c.remove(this);
        }
        return true;
    }
}
