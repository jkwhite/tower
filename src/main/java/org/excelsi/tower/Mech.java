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


public class Mech extends Form {
    public Mech(Armament naturalWeapon) {
        super(naturalWeapon, null,
              new Slot(SlotType.head, "head", 30),
              //new Slot(SlotType.eyes, "eyes", 0),
              new Slot(SlotType.torso, "body", 50),
              new Slot(SlotType.leg, "front right leg", 5),
              new Slot(SlotType.leg, "front left leg", 5),
              new Slot(SlotType.leg, "rear right leg", 5),
              new Slot(SlotType.leg, "rear left leg", 5)
        );
    }

    public Mech(Armament naturalWeapon, Slot... slots) {
        super(naturalWeapon, null, slots);
    }

    public Mech() {
        this(null);
    }

    public String getComplain() {
        return "boop";
    }

    public String getShout() {
        return "beep";
    }

    public EnergySource getSustenance() {
        return EnergySource.electricity;
    }
}
