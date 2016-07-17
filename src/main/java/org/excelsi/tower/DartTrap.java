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
import org.excelsi.matrix.Filter;


public class DartTrap extends Trap {
    private NHSpace _wall;
    private int _darts = Rand.om.nextInt(30)+10;


    public DartTrap() {
        super("dart", 50);
    }

    public Item[] getIngredients() {
        Item[] ings = new Item[] {new Dart(), new Rock(), new BallOfYarn()};
        ings[0].setCount(10);
        return ings;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public Maneuver getDifficulty() {
        return Maneuver.medium;
    }

    public void trigger(NHBot b) {
        if(_darts==0) {
            if(b.isPlayer()||b.getEnvironment().getVisibleBots().contains(b.getEnvironment().getPlayer())) {
                N.narrative().print(b, "You hear a nearby click.");
            }
            return;
        }
        if(_wall==null) {
            // find nearest wall that is not adjacent to this space
            _wall = (NHSpace) getSpace().closest(new Filter() {
                public boolean accept(MSpace s) { return s instanceof Wall && s.distance(getSpace())>1f && s.distance(getSpace())<12f && s.directionTo(getSpace()).isCardinal(); }
            }, true);
            if(_wall==null) {
                // somehow we're not in a room?!?
                //_wall = this;
                return;
            }
        }
        Dart d = new Dart();
        d.setCount(1);
        if(Rand.d100(4)) {
            d.addFragment(new Poison());
        }
        if(b.isPlayer()||b.getEnvironment().getPlayer().getEnvironment().getVisible().contains(getSpace())) {
            setHidden(false);
        }
        NHBot occ = getSpace().getOccupant();
        if(occ!=null) {
            N.narrative().print(b, "A little dart shoots out at "+Grammar.noun(b)+"!");
        }
        else {
            N.narrative().print(b, "A little dart shoots out!");
        }
        b.getEnvironment().getMechanics().resolve(new Inorganic("little dart"), _wall, _wall.directionTo(getSpace()),
            new Throw.MissileAttack(null, d), null);
    }

    private static class Attacker extends NPC {
        public Attacker() {
            setStrength(30);
            setAgility(100);
        }
    }
}
