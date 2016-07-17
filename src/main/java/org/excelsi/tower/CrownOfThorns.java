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


public class CrownOfThorns extends LightLeatherHelmet implements Affector {
    private Regeneration _regen;


    public String getColor() {
        return "red";
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 0.5f;
    }

    public int getRate() {
        return 15;
    }

    public int getPower() {
        return 3;
    }

    public int getFindRate() {
        return 0;
    }

    public void setStatus(Status s) {
        super.setStatus(s);
        if(_regen!=null) {
            _regen.setAmount(regenFor(getStatus()));
        }
    }

    public void attach(NHBot b) {
        _regen = new Regeneration("crownofthorns", null, 30, regenFor(getStatus()));
        b.addAffliction(_regen);
    }

    public void remove(NHBot b) {
        b.removeAffliction("crownofthorns");
        _regen = null;
    }

    private static int regenFor(Status s) {
        switch(s) {
            case blessed:
                return 3;
            case uncursed:
                return 1;
            case cursed:
                return -1;
        }
        return 0;
    }
}
