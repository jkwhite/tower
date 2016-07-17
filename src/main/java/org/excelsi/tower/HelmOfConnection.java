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
import org.excelsi.matrix.Bot;


public class HelmOfConnection extends PlateHelmet implements Affector {
    private Modifier _m = new Modifier();


    public float getLevelWeight() { return 0.8f; }

    public String getColor() {
        return "bright-blue";
    }

    public String getName() {
        if(isClassIdentified()) {
            return super.getName();
        }
        else {
            return "impractical-looking helmet";
        }
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 1f;
    }

    public int getRate() {
        return 15;
    }

    public int getPower() {
        return 5;
    }

    public int getFindRate() {
        return 5;
    }

    public void setStatus(Status s) {
        Status o = getStatus();
        super.setStatus(s);
        //Connected c = connectionFor(_original, getStatus());
        if(o!=getStatus()) {
            update();
        }
    }

    public Modifier getModifier() {
        return _m;
    }

    private NHBot _wearer;
    public void attach(NHBot b) {
        _wearer = b;
        update();
    }

    private void update() {
        int c = 0;
        switch(getStatus()) {
            case blessed:
                c = 2;
                break;
            case uncursed:
                c = 1;
                break;
            case cursed:
                c = -2;
                break;
        }
        _m.setConnected(c);
        connect(_wearer);
    }

    static void connect(NHBot b) {
        if(b!=null&&b.isPlayer()) {
            switch(b.getModifiedConnected()) {
                case negative:
                    N.narrative().print(b, "Your mind is going...");
                    break;
                case none:
                    N.narrative().print(b, "You feel grounded.");
                    break;
                case weak:
                    N.narrative().print(b, "A sudden burst of static races through your mind.");
                    break;
                case strong:
                    N.narrative().print(b, "You feel an enhanced sense of presence.");
                    break;
                case full:
                    N.narrative().print(b, "Your consciousness seeps into the roots of the world.");
                    break;
            }
        }
    }

    public void remove(NHBot b) {
        connect(b);
        _wearer = null;
    }
}
