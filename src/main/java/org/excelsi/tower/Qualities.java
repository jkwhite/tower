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
import org.excelsi.matrix.MSpace;
import java.util.*;


public class Qualities implements Mixin {
    public enum Type {
        crude, rudimentary, fine, superior;
    }
    private static int _chance = 20;


    public static void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return NPC.class.isAssignableFrom(c) || Armament.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        if(o instanceof NPC) {
            NPC b = (NPC) o;
            for(Item i:b.getInventory().getItem()) {
                if(i instanceof Armament&&!i.isNatural()&&!i.isUnique()) {
                    mixArmament((Armament)i);
                }
            }
        }
        else {
            mixArmament((Armament)o);
        }
    }

    public static Quality[] extract(StringBuilder s) {
        List<Quality> quals = new ArrayList<Quality>();
        for(Type t:EnumSet.allOf(Type.class)) {
            String rep = t.toString()+" ";
            int k = s.indexOf(rep);
            if(k>=0) {
                s.replace(k, k+rep.length(), "");
                try {
                    quals.add(create(t));
                    break; // only add one at most
                }
                catch(IllegalArgumentException e) {
                    java.util.logging.Logger.global.severe(e.toString());
                }
            }
        }
        return quals.toArray(new Quality[quals.size()]);
    }

    public static Quality create(Type t) {
        switch(t) {
            case crude:
                return new Crude();
            case rudimentary:
                return new Rudimentary();
            case fine:
                return new Fine();
            case superior:
                return new Superior();
        }
        throw new IllegalArgumentException("unsupported quality "+t);
    }

    private void mixArmament(Armament a) {
        Item i = (Item) a;
        if(i.isUnique()||i.isNatural()) {
            return;
        }
        int r = Rand.d100();
        Quality q = null;
        if(r<=5) {
            q = new Fine();
        }
        else if(r<=8) {
            q = new Superior();
        }
        else if(r<=13) {
            q = new Crude();
        }
        else if(r<=18) {
            q = new Rudimentary();
        }
        if(q!=null&&!i.hasFragment(Quality.class)) {
            i.addFragment(q);
        }
    }
}
