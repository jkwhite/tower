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


public class Materials implements Mixin {
    public enum Type {
        leather(0, 100, "leather"),
        wood(0, 100, "wooden"),
        iron(10, 100, "iron"),
        steel(20, 100, "steel"),
        mithril(20, 10, "mithril"),
        arinyark(25, 4, "arinyark"),
        laen(30, 4, "laen"),
        eog(40, 3, "eog"),
        magmir(60, 2, "magmir")/*,
        mirror(25, 1, "mirror"),
        force(40, 1, "force")*/;

        private int _lweight;
        private int _occurrence;
        private String _adj;
        int getLevelWeight() {
            return _lweight;
        }

        int getOccurrence() {
            return _occurrence;
        }

        private Type(int lweight, int occurrence, String adj) {
            _lweight = lweight;
            _occurrence = occurrence;
            _adj = adj;
        }

        public String toAdjective() { return _adj; }
    }

    public boolean match(Class c) {
        return NPC.class.isAssignableFrom(c) || Armament.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        if(o instanceof NPC) {
            NPC b = (NPC) o;
            for(Item i:b.getInventory().getItem()) {
                if(i instanceof Armament&&!i.isNatural()&&!i.isUnique()) {
                    mixItem((Item)i);
                }
            }
        }
        else if(o instanceof Item) {
            mixItem((Item)o);
        }
    }

    public static Material[] extract(StringBuilder s) {
        List<Material> quals = new ArrayList<Material>();
        for(Type t:EnumSet.allOf(Type.class)) {
            String rep = t.toAdjective()+" ";
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
        return quals.toArray(new Material[quals.size()]);
    }

    public static Material create(Type t) {
        switch(t) {
            case eog:
                return new Eog();
            case mithril:
                return new Mithril();
            case arinyark:
                return new Arinyark();
            case laen:
                return new Laen();
            case magmir:
                return new Magmir();
            case iron:
                return new Iron();
            case steel:
                return new Steel();
            case wood:
                return new Wood();
            case leather:
                return new Leather();
            //case mirror:
                //return new Mirror();
            //case force:
                //return new Force();
        }
        throw new IllegalArgumentException("unsupported type "+t);
    }

    private static final List<Type> _types = new ArrayList<Type>(EnumSet.allOf(Type.class));
    public static Material random(int lev) {
        int times = 100;
        Type m = null;
        do {
            int i = Rand.om.nextInt(_types.size());
            Type t = _types.get(i);
            float chance = 101 - (t.getLevelWeight() - lev);
            chance *= t.getOccurrence();
            if(Rand.d100((int)chance)) {
                m = t;
            }
        } while(--times>0||m==null);
        return m!=null?create(m):null;
    }

    private void mixItem(Item a) {
        Item i = (Item) a;
        if(i.isUnique()) {
            return;
        }
        int r = Rand.d100();
        if(i instanceof Matter && !i.hasFragment(Material.class)) {
            int lev = Universe.getUniverse().getGame().getLevel();
            try {
                Material m = random(lev);
                if(m!=null) {
                    //System.err.println("ADDING: "+m);
                    i.addFragment(m);
                }
            }
            catch(IllegalArgumentException e) {
                java.util.logging.Logger.global.severe(e.toString());
            }
        }
    }
}
