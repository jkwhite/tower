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


public abstract class Quality implements Fragment {
    private Item _owner;


    public Quality() {
    }

    public void apply(Fragment f) {
    }

    public boolean intercepts(Attack a) {
        return false;
    }

    public Runnable intercept(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public GrammarType getPartOfSpeech() {
        return GrammarType.adjective;
    }

    public void setIdentified(boolean id) {
    }

    public boolean isIdentified() {
        return true;
    }

    public void setClassIdentified(boolean id) {
    }

    public boolean isClassIdentified() {
        return true;
    }

    public int getOccurrence() {
        return 0;
    }

    public void setOwner(Item owner) {
        _owner = owner;
    }

    public Item getOwner() {
        return _owner;
    }
}
