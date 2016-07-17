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
import java.util.Arrays;


public abstract class Inflicter extends Item {
    public Inflicter(Infliction... inflictions) {
        if(inflictions!=null) {
            for(Infliction i:inflictions) {
                addFragment(i);
            }
        }
    }

    public void addFragment(Fragment f) {
        if(f instanceof Infliction) {
            for(Infliction i:getInflictions()) {
                if(i.isReplaceable()) {
                    removeFragment(i);
                    break;
                }
            }
            Infliction inf = (Infliction) f;
            if(inf.getStatus()!=getStatus()) {
                setStatus(inf.getStatus());
            }
        }
        super.addFragment(f);
    }

    public List<Infliction> getInflictions() {
        ArrayList<Infliction> infs = new ArrayList<Infliction>(2);
        for(Fragment f:getFragments()) {
            if(f instanceof Infliction) {
                infs.add((Infliction)f);
            }
        }
        return infs;
    }

    public List<Infliction> removeInflictions() {
        List<Infliction> is = getInflictions();
        for(Infliction i:is) {
            removeFragment(i);
        }
        return is;
    }

    protected void inflict(NHBot b) {
        for(Infliction i:getInflictions()) {
            if(b.isPlayer()) {
                i.setClassIdentified(true);
            }
            if(!i.inflict(b)) {
                if(!i.isPermanent()) {
                    removeFragment(i.getName());
                }
            }
        }
    }

    protected void inflict(NHBot b, boolean remove) {
        for(Infliction i:getInflictions()) {
            if(b.isPlayer()) {
                i.setClassIdentified(true);
            }
            i.inflict(b);
            if(remove&&!i.isPermanent()) {
                removeFragment(i.getName());
            }
        }
    }

    protected void inflict(NHSpace s) {
        for(Infliction i:getInflictions()) {
            if(!i.inflict(s)) {
                if(!i.isPermanent()) {
                    removeFragment(i.getName());
                }
            }
        }
    }

    protected void inflict(NHSpace s, boolean remove) {
        for(Infliction i:getInflictions()) {
            i.inflict(s);
            if(remove&&!i.isPermanent()) {
                removeFragment(i.getName());
            }
        }
    }

    public void setStatus(Status s) {
        super.setStatus(s);
        for(Infliction i:getInflictions()) {
            i.setStatus(s);
        }
    }
}
