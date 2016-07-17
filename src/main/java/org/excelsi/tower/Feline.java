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


public class Feline extends Mortal {
    public Feline() {
        super(new FelineCorpse(),
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
        return "meow";
    }

    public String getShout() {
        return "meow";
    }

    private static class FelineCorpse extends Corpse {
        public int getFindRate() { return 80; }

        public void setSpirit(NHBot spirit) {
            super.setSpirit(spirit);
        }
    }
}
