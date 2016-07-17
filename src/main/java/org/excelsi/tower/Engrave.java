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


public class Engrave extends ItemAction {
    private String _text;


    public Engrave(Item i, String text) {
        super("write with", i);
        text = text;
    }

    public Engrave() {
        super("write with", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                return i==null || i instanceof Wand || (i instanceof Tool && ((Tool)i).getWritingAbility()>0);
            }
        }, true);
    }

    public String getDescription() {
        return "Write on the ground.";
    }

    protected void act() {
        int writingAbility;
        String utensil;
        WritingUtensil chosen = (WritingUtensil) getItem();
        if(chosen!=null) {
            utensil = Grammar.nonspecific((Item)chosen);
            writingAbility = chosen.getWritingAbility();
        }
        else {
            writingAbility = 5;
            utensil = "your fingers";
        }
        if(_text==null) {
            _text = N.narrative().reply(getBot(), "What do you want to write?");
        }
        N.narrative().print(getBot(), "You write on the floor with "+utensil+".");
        if(chosen==null||chosen.discharge(getBot(), getBot().getInventory())) {
            Writing w = new Writing(_text, (short) writingAbility);
            getBot().getEnvironment().getMSpace().addParasite(w);
        }
        else {
        }
        _text = null;
    }
}
