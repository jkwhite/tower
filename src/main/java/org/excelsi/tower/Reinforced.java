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


public class Reinforced implements Fragment {
    public static final String NAME = "reinforced";
    private int _power;
    private Item _owner;


    public Reinforced() {
        this(0);
    }

    public Reinforced(int power) {
        _power = power;
    }

    public void apply(Fragment f) {
    }

    public boolean intercepts(Attack a) {
        return false;
    }

    public Runnable intercept(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public Modifier getModifier() {
        return new Modifier();
    }

    public int getPowerModifier() {
        return _power;
    }

    public GrammarType getPartOfSpeech() {
        return GrammarType.adjective;
    }

    public String getText() {
        return "reinforced";
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

    public String getName() {
        return NAME;
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
