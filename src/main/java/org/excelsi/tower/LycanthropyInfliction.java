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
import java.util.List;
import java.util.ArrayList;


public class LycanthropyInfliction extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.disks, 4));
    }

    private String _beast;

    public LycanthropyInfliction() {
        setName("lycanthropy");
    }

    public LycanthropyInfliction(String beast) {
        this();
        setBeast(beast);
    }

    public void setBeast(String beast) {
        _beast = beast;
    }

    public String getBeast() {
        return _beast;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean equals(Object o) {
        if(super.equals(o)) {
            LycanthropyInfliction lo = (LycanthropyInfliction)o;
            if(lo.getBeast()!=null&&getBeast()!=null) {
                return lo.getBeast().equals(getBeast());
            }
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() ^ (getBeast()!=null?getBeast().hashCode():0);
    }

    public boolean inflict(final NHBot b) {
        String beast = getBeast();
        if(beast==null) {
            beast = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                public boolean accept(NHBot b) {
                    return true;
                }
            }).getCommon();
        }
        if(Rand.d100(25)&&!b.isAfflictedBy(Lycanthropy.NAME)) {
            b.addAffliction(new Lycanthropy(beast));
            if(b.isPlayer()) {
                N.narrative().print(b, "A momentary vision clouds your gaze.");
            }
        }
        return true;
    }

    public int getOccurrence() {
        return 20;
    }
}
