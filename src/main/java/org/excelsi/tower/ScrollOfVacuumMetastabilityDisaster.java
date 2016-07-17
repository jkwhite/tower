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
import java.util.ArrayList;


public class ScrollOfVacuumMetastabilityDisaster extends Scroll {
    public int score() { return 666; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new DiskOfOdin()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        if(getStatus()==Status.blessed) {
            if(!N.narrative().confirm(b, "Tweak alpha?")) {
                return;
            }
        }
        if(b.isAfflictedBy("metastability")) {
            b.removeAffliction("metastability");
            N.narrative().print(b, Grammar.start(b, "restabilize")+" the Tower.");
            return;
        }
        N.narrative().print(b, Grammar.start(b, "destabilize")+" the Tower's vacuum state.");
        N.narrative().more();
        N.narrative().print(b, "The Tower is collapsing!");
        final ArrayList<MSpace> spaces = new ArrayList<MSpace>();
        spaces.add(b.getEnvironment().getMSpace());
        b.addAffliction(new Affliction("metastability", Affliction.Onset.tick) {
            int num = 0;
            public void beset() {
                if(!spaces.isEmpty()) {
                    for(int i=0;i<num&&!spaces.isEmpty();i++) {
                        MSpace doomed = spaces.remove(0);
                        for(MSpace m:doomed.surrounding()) {
                            if(m!=null) {
                                if(!spaces.contains(m)) {
                                    spaces.add(m);
                                }
                            }
                        }
                        NHBot occ = (NHBot) doomed.getOccupant();
                        try {
                            doomed.replace(null);
                            if(occ!=null) {
                                if(occ.isPlayer()) {
                                    N.narrative().print(occ, "Gravity collapses around you.");
                                }
                                occ.die("Killed by an inhospitable universe.");
                            }
                        }
                        catch(IllegalStateException e) {
                            // space is not replaceable--ignore
                        }
                    }
                    num++;
                }
                else {
                    spaces.add(getBot().getEnvironment().getMSpace());
                    num = 0;
                }
            }

            public void compound(Affliction a) {
            }

            public String getStatus() {
                return "Doomed";
            }

            public String getExcuse() {
                return null;
            }
        });
    }

    public int getFindRate() {
        return 0;
    }
}
