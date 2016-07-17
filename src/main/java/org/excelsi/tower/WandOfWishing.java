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
import java.util.ArrayList;
import java.util.HashSet;


public class WandOfWishing extends Wand {
    public WandOfWishing() {
        setCharges(3);
    }

    public int getFindRate() {
        return 1;
    }

    public boolean isWishable() {
        return false;
    }

    public void invoke(NHBot b) {
        if(discharge(b)) {
            int tries = 0;
            while(true) {
                String text = N.narrative().reply(b, "What do you wish for?");
                if(wish(b, Grammar.parseRequest(text))) {
                    break;
                }
                else if(++tries==3) {
                    Item it = Universe.getUniverse().createItem(new ItemFilter() {
                        public boolean accept(Item i, NHBot bot) {
                            return i.isWishable();
                        }
                    });
                    it.setCount(1);
                    it.setStatus(Status.uncursed);
                    if(b.isPlayer()) {
                        ((Patsy)b).analyze(it);
                    }
                    b.getInventory().add(it);
                    N.narrative().clear();
                    N.narrative().print(b, Grammar.key(b.getInventory(), it));
                    break;
                }
            }
        }
    }

    public boolean wish(NHBot b, Request req) {
        int count = req.getCount();
        String text = req.getNoun();
        Status status = req.getStatus();
        Quality q = null;
        Fragment mat = null;
        StringBuilder buf = new StringBuilder(text);
        Quality[] quals = Qualities.extract(buf);
        if(quals.length>0) {
            q = quals[0];
        }
        Material[] mats = Materials.extract(buf);
        if(mats.length>0) {
            mat = mats[0];
        }
        text = buf.toString();
        /*
        for(String s:text.split(" ")) {
            if(s.equals("fine")) {
                q = new Fine();
            }
            else if(s.equals("crude")) {
                q = new Crude();
            }
            else if(s.equals("mithril")) {
                mat = new Mithril();
            }
            else if(s.equals("eog")) {
                mat = new Eog();
            }
        }
        text = text.replaceFirst("mithril ", "");
        text = text.replaceFirst("eog ", "");
        text = text.replaceFirst("fine ", "");
        text = text.replaceFirst("crude ", "");
        */
        text = text.trim();
        final String match = text;
        Item it = Universe.getUniverse().createItem(new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                //System.err.println("comparing "+i.getTrueName()+" to "+match);
                return i.isWishable()&&i.getTrueName().equalsIgnoreCase(match);
            }
        }, false);
        if(it==null) {
            it = Universe.getUniverse().createItem(new ItemFilter() {
                public boolean accept(Item i, NHBot bot) {
                    //System.err.println("comparing "+i.toTrueString()+" to "+match);
                    return i.isWishable()&&i.toTrueString().endsWith(match);
                }
            }, false);
        }
        if(it==null) {
            it = Universe.getUniverse().createItem(new ItemFilter() {
                public boolean accept(Item i, NHBot bot) {
                    return i.isWishable()&&i.toTrueString().startsWith(match);
                }
            }, false);
        }
        if(it!=null) {
            if(it.getStackType()==Item.StackType.separate) {
                count = 1;
            }
            boolean creat = Boolean.getBoolean("tower.creator");
            if(!creat&&count>1) {
                if(100-20*(count-1)<0) {
                    count = 1;
                }
                else {
                    if(!Rand.d100(100 - 20*(count-1))) {
                        while(count>1&&Rand.d100(80)) {
                            count--;
                        }
                    }
                }
            }
            it.setCount(count);
            if(!creat) {
                if(q!=null&&mat!=null&&Rand.d100(15)) {
                    status = Status.cursed;
                }
            }
            it.setStatus(status);
            if((creat||Rand.d100(33))&&q!=null) {
                it.addFragment(q);
            }
            if((creat||Rand.d100(25))&&mat!=null) {
                it.removeFragment(Material.NAME);
                //System.err.println("ADDING MAT: "+mat);
                it.addFragment(mat);
            }
            if(!it.hasFragment(Material.NAME)) {
                //System.err.println("MIXING");
                new Materials().mix(it);
            }
            //System.err.println("FINAL: "+it.getFragments());
            b.getInventory().add(it);
            N.narrative().clear();
            N.narrative().print(b, Grammar.key(b.getInventory(), it));
            return true;
        }
        else {
            N.narrative().print(b, "The wand looks puzzled.");
        }
        return false;
    }
}
