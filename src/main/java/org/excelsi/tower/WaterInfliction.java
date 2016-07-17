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


public class WaterInfliction extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.cups, 2));
    }

    public WaterInfliction() {
        setName("water");
    }

    public String getColor() {
        return "clear";
    }

    public boolean isReplaceable() {
        return true;
    }

    public void randomize() {
        int r = Rand.d100();
        if(r<=25) {
            setStatus(Status.cursed);
        }
        else if(r<=50) {
            setStatus(Status.blessed);
        }
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(b.isPlayer()) {
            switch(getStatus()) {
                case cursed:
                    N.narrative().print(b, "Ugh, sewer water!");
                    break;
                case blessed:
                    N.narrative().print(b, "You feel rejuvenated.");
                    break;
                case uncursed:
                    N.narrative().print(b, "You feel refreshed.");
                    break;
            }
        }
        return false;
    }

    public boolean apply(Item t, NHBot b) {
        if(t instanceof Potion && ((Potion)t).isEmpty()) {
            return super.apply(t, b);
        }
        Status newStatus = null;
        switch(getStatus()) {
            case blessed:
                switch(t.getStatus()) {
                    case blessed:
                        break;
                    case uncursed:
                        newStatus = Status.blessed;
                        break;
                    case cursed:
                        newStatus = Status.uncursed;
                        break;
                }
                break;
            case uncursed:
                break;
            case cursed:
                switch(t.getStatus()) {
                    case blessed:
                        newStatus = Status.uncursed;
                        break;
                    case uncursed:
                        newStatus = Status.cursed;
                        break;
                    case cursed:
                        break;
                }
                break;
        }
        boolean mod = false;
        if(newStatus!=null) {
            if(t.getCount()>1) {
                t = b.getInventory().split(t);
                t.setStatus(newStatus);
                b.getInventory().add(t);
            }
            else {
                t.setStatus(newStatus);
            }
            N.narrative().print(b, Grammar.first(Grammar.noun(t))+" glows "+newStatus.getColor()+" for a moment.");
            mod = true;
        }
        else {
            N.narrative().printf(b, "%n gets wet.", t);
        }
        for(Fragment f:t.getFragments()) {
            f.apply(this);
        }
        return true;
    }
}
