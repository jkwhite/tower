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
import static org.excelsi.aether.Brain.*;


public class ConsumeDaemon extends SearchDaemon {
    private Chemical _basic;


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    protected boolean accept(Item i) {
        if(i instanceof Comestible) {
            if(i instanceof Corpse) {
                Corpse co = (Corpse) i;
                if(co.getSpirit()!=null&&co.getSpirit().getCommon().equals(in.b.getCommon())) {
                    return false; // no cannibalism
                }
            }
            Comestible c = (Comestible) i;
            if(c.getNutrition()<=0) {
                return false;
            }
            return true;
        }
        return false;
    }

    protected int strengthFor(Item i) {
        int s = -1;
        if(in.b.getTemperament()==Temperament.hungry) {
            s = 4;
        }
        Hunger.Degree d = Hunger.Degree.degreeFor(in.b.getHunger());
        if(d!=Hunger.Degree.satiated&&d!=Hunger.Degree.normal) {
            s = 10;
        }
        return s;
    }

    protected void perform(Item i) {
        Consume c = new Consume(i);
        c.setBot(in.b);
        c.perform();
    }

    public Chemical getChemical() {
        return _basic;
    }
}

