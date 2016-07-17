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


public class Lockpick extends Tool implements Useable {
    public void use(NHBot b) {
        Direction chosen = N.narrative().direct(b, "Which direction?");
        pick(b, chosen);
    }

    public StackType getStackType() {
        return StackType.stackable;
    }

    public int getFindRate() {
        return 5;
    }

    public float getWeight() {
        return 0.01f;
    }

    public float getSize() {
        return 0.05f;
    }

    public void pick(final NHBot b, Direction d) {
        NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d);
        if(s==null||!(s instanceof Doorway)) {
            N.narrative().print(b, "There is nothing to unlock there.");
            throw new ActionCancelledException();
        }
        final Doorway door = (Doorway) s;
        if(door.isOpen()) {
            N.narrative().print(b, "The door is already open!");
            throw new ActionCancelledException();
        }
        else if(!door.isLocked()) {
            N.narrative().print(b, "On closer inspection, that door is not locked.");
        }
        else if(!door.isUnlockable()) {
            N.narrative().print(b, "Something is stuck in the lock.");
        }
        else {
            N.narrative().print(b, Grammar.start(b, "start")+" picking the lock.");
            b.start(new ProgressiveAction() {
                int time = 10;
                public int getInterruptRate() { return 100; }
                public void stopped() { }
                public String getExcuse() { return "picking a lock"; }

                public void interrupted() {
                    N.narrative().print(b, Grammar.start(b, "stop")+" picking the lock.");
                }

                public boolean iterate() {
                    if(--time==0) {
                        int sk = b.getSkill("lockpicking");
                        if(getStatus()==Status.cursed) {
                            N.narrative().print(b, "The lockpick breaks off in the lock!");
                            b.getInventory().consume(Lockpick.this);
                            door.setUnlockable(false);
                        }
                        else if(Rand.d100(sk+(getStatus()==Status.blessed?30:0))) {
                            N.narrative().print(b, "Click! The door opens.");
                            door.setLocked(false);
                            door.setOpen(true);
                            b.getEnvironment().unhide();
                            b.skillUp("lockpicking");
                        }
                        else {
                            if(Rand.d100(40)) {
                                N.narrative().print(b, "The lockpick breaks! No luck...");
                                b.getInventory().consume(Lockpick.this);
                            }
                            else {
                                N.narrative().print(b, "No luck...");
                            }
                            b.skillUp("lockpicking");
                        }
                        return false;
                    }
                    return true;
                }
            });
        }
    }
}
