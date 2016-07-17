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


public class Avian extends Mortal {
    public Avian() {
        this(new AvianCorpse());
    }

    public Avian(Corpse c) {
        super(c,
              new Claw(4),
              new Skin(),
              new Slot(SlotType.head, "head", 20),
              new Slot(SlotType.eyes, "eyes", 0),
              new Slot(SlotType.hand, "right wing", 0),
              new Slot(SlotType.hand, "left wing", 0),
              new Slot(SlotType.torso, "body", 45),
              new Slot(SlotType.back, "back", 5),
              new Slot(SlotType.leg, "right leg", 15),
              new Slot(SlotType.leg, "left leg", 15)
        );
    }

    private static class AvianCorpse extends Corpse {
        public int getFindRate() { return 40; }
        public float getSize() { return 5; }
    }
}
