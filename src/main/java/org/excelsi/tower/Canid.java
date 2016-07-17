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
import org.excelsi.matrix.*;


public class Canid extends Mortal {
    static {
        Extended.addCommand("howl", new Howl());
    }

    public Canid() {
        super(new CanidCorpse(),
              new Bite(10),
              new LightHide(),
              new Slot(SlotType.head, "head", 20),
              new Slot(SlotType.eyes, "eyes", 0),
              new Slot(SlotType.finger, "right claw", 0),
              new Slot(SlotType.finger, "left claw", 0),
              new Slot(SlotType.hand, "front right paw", 0),
              new Slot(SlotType.hand, "front left paw", 0),
              new Slot(SlotType.torso, "torso", 20),
              new Slot(SlotType.back, "back", 0),
              new Slot(SlotType.leg, "front right leg", 15),
              new Slot(SlotType.leg, "front left leg", 15),
              new Slot(SlotType.leg, "rear right leg", 15),
              new Slot(SlotType.leg, "rear left leg", 15),
              new Slot(SlotType.foot, "rear right paw", 0),
              new Slot(SlotType.foot, "rear left paw", 0)
        );
    }

    public String getComplain() {
        return "whine";
    }

    public String getShout() {
        return "bark";
    }

    public static class Howl extends DefaultNHBotAction implements SpaceAction {
        public static boolean canActivate(NHBot b) {
            return b.getHp()<=b.getMaxHp()/4;
        }

        public String getDescription() {
            return "Howl, if in canine form.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getForm() instanceof Canid || "werewolf".equals(b.getCommon());
        }

        public void perform() {
            String c = getBot().getCommon();
            if("werewolf".equals(c)) {
                N.narrative().print(getBot(), Grammar.start(getBot(), "howl")+".");
                if(canActivate(getBot()) && Rand.d100(50)) {
                    int count = 0;
                    for(MSpace m:getBot().getEnvironment().getMSpace().surrounding()) {
                        if(m!=null&&!m.isOccupied()&&m.isWalkable()) {
                            m.setOccupant(Universe.getUniverse().createBot("wolf"));
                            if(++count==3) {
                                break;
                            }
                        }
                    }
                }
            }
            else if(getBot().getForm() instanceof Canid) {
                N.narrative().print(getBot(), Grammar.start(getBot(), "howl")+".");
            }
            else {
                N.narrative().print(getBot(), "On second thought, that would be silly.");
            }
        }
    }
}
