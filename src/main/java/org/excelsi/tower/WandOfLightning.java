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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class WandOfLightning extends Wand implements Createable {
    public Item[] getIngredients() {
        Item[] ing = new Item[]{new SteelTube(), new Battery(), new Nozzle(), new Potion(new Lightning()), new Wire(), new RollOfTape()};
        return ing;
    }

    public boolean accept(NHSpace s) { return true; }

    public String getCreationSkill() { return "gadgetry"; }

    public Maneuver getDifficulty() { return Maneuver.hard; }

    public void invoke(final NHBot b) {
        if(discharge(b)) {
            Direction chosen = getDirection();
            if(chosen==null) {
                chosen = N.narrative().direct(b, "Which direction?");
            }
            b.getEnvironment().face(chosen);
            int t = 221;
            if(getStatus()==Status.blessed) {
                t += 200;
            }
            final int temp = t;
            b.getEnvironment().project(chosen, new BoltAttack(new Lightning(temp), b, new Source("the lightning bolt")));
        }
    }

    public boolean isDirectable() {
        return true;
    }

    public int getFindRate() {
        return 7;
    }
}
