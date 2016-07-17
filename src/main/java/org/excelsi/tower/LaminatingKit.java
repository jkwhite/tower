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
import org.excelsi.matrix.Direction;


public class LaminatingKit extends Tool {
    public int _charges = Rand.om.nextInt(8)+4;


    public LaminatingKit() {
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 1;
    }

    public void setCharges(int charges) {
        _charges = charges;
    }

    public int getCharges() {
        return _charges;
    }

    public String getColor() {
        return "white";
    }

    public void use(NHBot b) {
        if(_charges==0) {
            N.narrative().print(b, "This kit is out of laminating fluid.");
            return;
        }
        boolean fl = false;
        for(Item i:b.getInventory().getItem()) {
            if(i instanceof Laminatable) {
                fl = true;
            }
        }
        if(!fl) {
            N.narrative().print(b, Grammar.start(b, "have")+" nothing that can be laminated.");
            throw new ActionCancelledException();
        }
        for(;;) {
            Laminatable chosen = (Laminatable) N.narrative().choose(b, new ItemConstraints(
                        b.getInventory(), "laminate", new ItemFilter() {
                            public boolean accept(Item i, NHBot bot) {
                                return i instanceof Laminatable;
                            }
                        }), false);
            if(chosen.isLaminated()) {
                N.narrative().print(b, "That is already laminated.");
                //N.narrative().more();
            }
            else {
                laminate(chosen, b);
            }
            break;
        }
    }

    public void laminate(Laminatable chosen, final NHBot b) {
        if(_charges==0) {
            N.narrative().print(b, "This kit is out of laminating fluid.");
            return;
        }
        N.narrative().print(b, Grammar.start(b, "start")+" laminating "+Grammar.noun((Item)chosen)+".");
        final Laminatable lam = chosen;
        int deftime = 3;
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
                    Laminatable lam2;
                    if(lam.getCount()>1) {
                        lam2 = (Laminatable) b.getInventory().split((Item)lam);
                        lam2.setLaminated(true);
                        b.getInventory().add((Item)lam2);
                    }
                    else {
                        lam2 = lam;
                        lam2.setLaminated(true);
                    }
                    setCharges(getCharges()-1);
                    N.narrative().print(b, "Your work is done.");
                    //N.narrative().more();
                    N.narrative().clear();
                    N.narrative().print(b, Grammar.key(b.getInventory(), ((Item)lam2)));
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
