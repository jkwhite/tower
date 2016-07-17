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
import static org.excelsi.aether.Grammar.*;


public class ScrollOfFrost extends Scroll {
    static {
        new Cold(); // register cold
    }

    public int score() { return 50; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Opal()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        Item chosen = null;
        boolean rand = true;
        Item w = b.getWielded();
        if(w==null) {
            String part = null;
            Slot[] slots = b.getForm().getSlots(SlotType.hand);
            if(slots.length>0) {
                part = slots[0].getName();
            }
            if(part!=null) {
                N.narrative().print(b, first(possessive(b))+" "+Grammar.pluralize(part)+" twitch.");
            }
            return;
        }
        switch(getStatus()) {
            case blessed:
                if(b.isPlayer()) {
                    new Cold().setClassIdentified(true);
                }
                Cold f = new Cold(32-200);
                f.setUses(Math.max(5, b.getModifiedEmpathy()/8));
                w.addFragment(f);
                N.narrative().print(b, "An icy maelstrom surrounds "+possessive(b)+" "+w.getName()+"!");
                break;
            case uncursed:
                if(b.isPlayer()) {
                    new Cold().setClassIdentified(true);
                }
                f = new Cold();
                f.setUses(Math.max(3, b.getModifiedEmpathy()/10));
                w.addFragment(f);
                N.narrative().print(b, "An icy wind surrounds "+possessive(b)+" "+w.getName()+"!");
                break;
            case cursed:
                N.narrative().print(b, first(possessive(b))+" "+w.getName()+" is covered in a layer of sugary frosting! Yum!");
                //w.setColor("white");
                break;
        }
    }
}
