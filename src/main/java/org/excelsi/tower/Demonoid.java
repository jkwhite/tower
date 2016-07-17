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


public class Demonoid extends Mortal {
    public Demonoid() {
        this(new DemonoidCorpse());
    }

    public Demonoid(Corpse c) {
        super(c,
              new Claw(8),
              new Skin(),
              new Slot(SlotType.head, "head", 10),
              new Slot(SlotType.finger, "right finger", 0),
              new Slot(SlotType.finger, "left finger", 0),
              new Slot(SlotType.hand, "right hand", 0),
              new Slot(SlotType.hand, "left hand", 0),
              new Slot(SlotType.torso, "torso", 25),
              new Slot(SlotType.back, "back", 5),
              new Slot(SlotType.arm, "right arm", 15),
              new Slot(SlotType.arm, "left arm", 15),
              new Slot(SlotType.leg, "right leg", 15),
              new Slot(SlotType.leg, "left leg", 15),
              new Slot(SlotType.foot, "right foot", 0),
              new Slot(SlotType.foot, "left foot", 0),
              new Slot(SlotType.eyes, "eyes", 0)
        );
    }

    private static class DemonoidCorpse extends Corpse {
        public int getFindRate() { return 80; }
        public float getSize() { return 5; }

        public void invoke(NHBot b) {
            if(getSpirit()==null) {
                return;
            }
            boolean fire = false;
            if(getSpirit().getCommon().startsWith("acid ")||getSpirit().getCommon().startsWith("fire ")) {
                N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" stomach is on fire!");
                fire = true;
                for(int i=0;i<4;i++) {
                    new Fire().inflict(b, new Source("eating a demon corpse"));
                }
            }
            else if(getSpirit() instanceof TNPC) {
                for(Element e:((TNPC)getSpirit()).getElements()) {
                    switch(e) {
                        case fire:
                            if(!fire) {
                                N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" stomach is on fire!");
                                new Fire().inflict(b);
                            }
                            break;
                        case water:
                            if(b.isPlayer()) {
                                N.narrative().print(b, "Brain freeze!!!");
                            }
                            new Cold().inflict(b);
                            break;
                        case light:
                            if(b.isPlayer()) {
                                N.narrative().print(b, "You feel energized!");
                            }
                            b.setHp(b.getHp()+b.getHp()/2); // ignore max hits
                            break;
                        case earth:
                            break;
                        case plasma:
                            if(b.isPlayer()) {
                                N.narrative().print(b, "The plasma annihilates your body on contact.");
                                N.narrative().print(b, "Death is painless.");
                            }
                            b.die("Consumed by plasma");
                            break;
                    }
                }
            }
        }
    }
}
