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
import org.excelsi.matrix.Direction;


public class ReinforcingKit extends Tool {
    public ReinforcingKit() {
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 2.5f;
    }

    public String getColor() {
        return "light-gray";
    }

    public void use(NHBot b) {
        boolean fr = false, frm = false;
        for(Item i:b.getInventory().getItem()) {
            if(i instanceof Reinforcable) {
                fr = true;
            }
            if(i instanceof ReinforcingMaterial) {
                frm = true;
            }
        }
        if(!fr) {
            N.narrative().print(b, Grammar.start(b, "have")+" nothing that can be reinforced.");
            throw new ActionCancelledException();
        }
        if(!frm) {
            N.narrative().print(b, Grammar.start(b, "have")+" nothing that can be used for reinforcement.");
            throw new ActionCancelledException();
        }
        for(;;) {
            Reinforcable chosen = (Reinforcable) N.narrative().choose(b, new ItemConstraints(
                b.getInventory(), "reinforce", new InstanceofFilter(Reinforcable.class)), false);
            if(chosen.isReinforced()) {
                N.narrative().print(b, "That is already reinforced.");
                //N.narrative().more();
            }
            else {
                ReinforcingMaterial m = (ReinforcingMaterial) N.narrative().choose(b, new ItemConstraints(
                    b.getInventory(), "use for reinforcement", new InstanceofFilter(ReinforcingMaterial.class)), false);
                reinforce(chosen, m, b);
            }
            break;
        }
    }

    public void reinforce(Reinforcable chosen, final ReinforcingMaterial m, final NHBot b) {
        if(chosen.isReinforced()) {
            N.narrative().print(b, "That is already reinforced.");
        }
        else {
            N.narrative().print(b, Grammar.start(b, "start")+" reinforcing "+Grammar.noun((Item)chosen)+".");
            final Reinforcable rei = chosen;
            if(((Item)m).getStatus()==Status.cursed) {
                if(Rand.d100(20)) {
                    setStatus(getStatus().worse());
                }
            }
            int deftime = 7;
            switch(getStatus()) {
                case cursed:
                    deftime *= 4;
                    break;
                case blessed:
                    deftime = 1;
                    break;
            }
            final int t = deftime;
            b.start(new ProgressiveAction() {
                int time = t;
                public int getInterruptRate() {
                    return 100;
                }

                public boolean iterate() {
                    if(--time==0) {
                        N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" work is done.");
                        Reinforcable rei2;
                        if(rei.getCount()>1) {
                            rei2 = (Reinforcable) b.getInventory().split((Item)rei);
                            rei2.reinforce(m);
                            b.getInventory().add((Item)rei2);
                        }
                        else {
                            rei2 = rei;
                            rei2.reinforce(m);
                        }
                        b.getInventory().consume((Item)m);
                        if(m.getReinforcingStrength()<0) {
                            N.narrative().print(b, first(noun((Item)rei2))+" looks a little fragile...");
                        }
                        //N.narrative().more();
                        N.narrative().clear();
                        N.narrative().print(b, Grammar.key(b.getInventory(), ((Item)rei2)));
                        return false;
                    }
                    else {
                        return true;
                    }
                }

                public void stopped() {
                }

                public void interrupted() {
                    N.narrative().print(b, Grammar.start(b, "stop")+" laminating.");
                }

                public String getExcuse() {
                    return null;
                }
            });
        }
    }
}
