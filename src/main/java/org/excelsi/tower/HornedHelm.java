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


public class HornedHelm extends PlateHelmet implements Affector {
    public float getLevelWeight() { return 0.6f; }

    public String getColor() {
        return "light-gray";
    }

    public float getSize() {
        return 5;
    }

    public float getWeight() {
        return 3f;
    }

    public int getRate() {
        return 60;
    }

    public int getPower() {
        return 15;
    }

    public int getFindRate() {
        return 15;
    }

    public Modifier getModifier() {
        Modifier m = new Modifier();
        m.setPresence(50);
        return m;
    }

    public void attach(NHBot b) {
        switch(getStatus()) {
            case blessed:
            case uncursed:
                N.narrative().print(b, Grammar.start(b, "look")+" intimidating!");
                break;
            case cursed:
                N.narrative().print(b, "These horns are made of plastic.");
                N.narrative().print(b, Grammar.start(b, "look")+" ridiculous...");
                break;
        }
    }

    public void remove(NHBot b) {
    }
}
