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
import java.util.*;


public class Engineering implements Talent {
    private List<String> _noticed = new ArrayList<String>();


    public String getName() { return "engineering"; }

    public final void apply(NHBot b, NHBot target) {
        if(target.isPlayer()||!(target.getForm() instanceof Mech)) {
            return;
        }
        Patsy p = (Patsy) b;
        Set<String> cat = p.getCatalogue();
        String name = target.getCommon();
        if(!cat.contains(name)) {
            cat.add(name);
            N.narrative().printf(b, "%n gives you an idea...", target);
        }
    }

    public final void apply(NHBot b, Item i) {
        if(b==null) {
            return;
        }
        if(i instanceof Corpse) {
            try {
                Corpse c = (Corpse) i;
                if(c.getSpirit()!=null) {
                    String n = "animatronic "+c.getSpirit().toString();
                    Patsy p = (Patsy) b;
                    Map<String,NHBot> anim = p.getAnim();
                    if(!anim.containsKey(n)) {
                        Wire wire = new Wire();
                        wire.setCount(3);
                        Processor processor = new Processor();
                        //NHBot copy = DefaultNHBot.copy(c.getSpirit());
                        NHBot copy = Universe.getUniverse().createBot(c.getSpirit().toString());
                        if(copy.getName()!=null) {
                            n = "A"+n.substring(1);
                            copy.setName(n);
                        }
                        else {
                            copy.setCommon(n);
                        }
                        Item corpse = Item.copy(c);
                        corpse.setCount(1);
                        copy.getInventory().setItem(new Item[]{
                            corpse,
                            wire,
                            processor});
                        //Item weapon = Item.copy((Item) copy.getForm().getNaturalWeapon());
                        //copy.setForm(new Mech(copy.getForm().getNaturalWeapon()));
                        copy.setColor("silver");
                        copy.setMaxHp(2*copy.getMaxHp());
                        copy.setStrength(2*copy.getStrength());
                        anim.put(n, copy);
                        N.narrative().printf(b, "%n gives you an idea...", c);
                    }
                }
            }
            catch(NoSuchBotException e) {
                // some kind of special bot, ignore
            }
        }
        else if(i instanceof Createable) {
            Createable cr = (Createable) i;
            Patsy p = (Patsy) b;
            String name = cr.getName();
            if(!p.getItemCatalogue().contains(name)) {
                p.getItemCatalogue().add(name);
            }
        }
    }
}
