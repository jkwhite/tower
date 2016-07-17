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


import java.util.List;
import java.util.ArrayList;
import org.excelsi.aether.*;
import org.excelsi.matrix.MSpace;


public class ScrollOfRegeneration extends Scroll {
    public int score() { return 30; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Aquamarine()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        int rate = 2;
        int time = 15;
        switch(getStatus()) {
            case blessed:
                rate = 5;
                time = 30;
            case uncursed:
                N.narrative().printf(b, "%V rejuvenated.", b, "feel");
                break;
            case cursed:
                N.narrative().printf(b, "%V decrepit.", b, "feel");
                rate = -2;
                break;
        }
        final int t = time;
        final int r = rate;
        Regeneration regen = new TimedRegeneration(t, "scroll", null, 2, rate);
        b.addAffliction(regen);
    }

    public void sacrifice(NHBot bot, Altar a) {
    }
}
