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


public class Healing extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 1));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(b.getHp()<b.getMaxHp()) {
            int hp = Rand.om.nextInt(21)+10;
            String adverb = "";
            if(getStatus()==Status.blessed) {
                hp *= 2;
                adverb = " much";
            }
            else if(getStatus()==Status.cursed) {
                hp /= 2;
                adverb = " a little";
            }
            b.setHp(Math.min(b.getHp()+hp, b.getMaxHp()));
            N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "feel")+adverb+" better.");
        }
        else {
            int inc = (int) Math.max(1f, ((float)b.getMaxHp())*0.05f*Rand.om.nextFloat());
            if(getStatus()==Status.blessed) {
                inc *= 2;
            }
            else if(getStatus()==Status.cursed) {
                inc /= 2;
            }
            if(inc==0) {
                inc = 1;
            }
            b.setMaxHp(b.getMaxHp()+inc);
            b.setHp(b.getMaxHp());
            N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "feel")+" healthier.");
        }
        return true;
    }
}
