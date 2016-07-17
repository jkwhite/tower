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


public abstract class TechnicalManual extends Book implements Readable {
    private String _nt;

    public TechnicalManual(String title) {
        setName("book entitled '"+title+"'");
    }

    public String getName() {
        setClassIdentified(true);
        return super.getName();
    }

    public String getObscuredName() {
        return "book";
    }

    public int getFindRate() {
        return 10;
    }

    public final void invoke(NHBot b) {
        b.start(new Reading(b, this));
        N.narrative().printf(b, "You immerse yourself in study.");
    }

    public final void read(NHBot b) {
        if(!b.isPlayer()) {
            return;
        }
        Patsy p = (Patsy) b;
        for(Item item:getTaughtItems()) {
            item.setClassIdentified(true);
            p.getItemCatalogue().add(item.getName());
            N.narrative().printf(p, "You learn how to make "+Grammar.nonspecific(item.getName())+"!");
        }
        for(String skill:getTaughtSkills()) {
            N.narrative().printf(p, "You learn a few things about "+skill+".");
            p.skillUp(skill);
        }
        for(Talent talent:getTaughtTalents()) {
            N.narrative().printf(p, "Ah, so that's how "+talent.getName()+" works!");
            p.getTalents().add(talent);
        }
    }

    protected String[] getTaughtSkills() {
        return new String[0];
    }

    protected Item[] getTaughtItems() {
        return new Item[0];
    }

    protected Talent[] getTaughtTalents() {
        return new Talent[0];
    }
}
