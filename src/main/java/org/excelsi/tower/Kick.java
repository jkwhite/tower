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
import org.excelsi.matrix.MSpace;


public class Kick extends DefaultNHBotAction implements SpaceAction {
    public String getDescription() {
        return "Kick an object or creature.";
    }

    public boolean isPerformable(NHBot b) {
        for(MSpace m:b.getEnvironment().getMSpace().surrounding()) {
            if(m instanceof Doorway || m.isOccupied() || ((NHSpace)m).getParasites().size()>0) {
                return true;
            }
        }
        return false;
    }

    public void perform() {
        final Direction d = N.narrative().direct(getBot(), "Which direction?");
        NHSpace sp = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);
        getBot().getEnvironment().face(d);
        boolean wasOccupied = sp.isOccupied();
        getBot().getEnvironment().project(d, new Attack() {
            public Type getType() { return Type.melee; }
            public NHBot getAttacker() { return getBot(); }
            public Source getSource() { return new Source(getBot()); }
            public boolean isPhysical() { return true; }
            public boolean affectsAttacker() { return false; }
            public int getRadius() { return 1; }
            public Armament getWeapon() {
                return new Armament() {
                    public Type getType() { return Type.melee; }
                    public int getPower() { return 4; }
                    public int getModifiedPower() { return 4; }
                    public int getRate() { return 70; }
                    public int getHp() { return 0; }
                    public void setHp(int hp) {}
                    public String getVerb() { return "kick"; }
                    public Stat[] getStats() { return new Stat[]{Stat.st, Stat.st, Stat.ag }; }
                    public String getSkill() { return "unarmed"; }
                    public String getAudio() { return "hit_crushing"; }
                    public String getColor() { return null; }
                    public String getModel() { return null; }
                    public Item toItem() { return null; }
                    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
                        int dist = 1+attacker.getModifiedStrength()/defender.getModifiedStrength();
                        if(dist>0) {
                            MSpace m = defender.getEnvironment().getMSpace().move(d);
                            int moves = 0;
                            while(--dist>=0&&m!=null&&m.isWalkable()&&!m.isOccupied()) {
                                m = m.move(d);
                                moves++;
                            }
                            while(moves-->0) {
                                defender.getEnvironment().move(d);
                            }
                        }
                        return null;
                    }
                    public void invoke(NHBot attacker, NHSpace s, Attack a) { }
                };
            }
        });
        if(sp instanceof Doorway) {
            Doorway door = (Doorway) sp;
            if(Rand.d100(getBot().getStrength())) {
                NHSpace floor = (NHSpace) door.replace(new Floor());
                //N.narrative().printf(getBot(), "Crash! The door splinters asunder.");
                N.narrative().printf(floor, "Crash! The door splinters asunder.");
                Board boards = new Board();
                boards.setColor(door.getColor());
                boards.setCount(Rand.om.nextInt(3)+1);
                if(Rand.d100(75)) {
                    floor.add(boards);
                }
                Nail nails = new Nail();
                nails.setCount(Rand.om.nextInt(6)+2);
                if(Rand.d100(75)) {
                    floor.add(nails);
                }
                getBot().getEnvironment().unhide();
            }
            else {
                N.narrative().printf(door, "Thud!");
            }
        }
        else if(sp.isWalkable()) {
            boolean hitPar = false;
            for(Parasite p:sp.getParasites()) {
                if(p.isMoveable()) {
                    NHSpace m = (NHSpace) sp.move(d);
                    if(m!=null&&m.isWalkable()&&m.getDepth()>=0) {
                        sp.moveParasite(p, m);
                        return;
                    }
                }
                hitPar = true;
            }
            NHBot occ = sp.getOccupant();
            if(occ!=null) {
            }
            else {
                if(!wasOccupied&&!hitPar) {
                    N.narrative().printf(getBot(), "%V the air.", getBot(), "kick");
                    //N.narrative().print(getBot(), Grammar.start(getBot(), "kick")+" the air.");
                }
                if(hitPar) {
                    N.narrative().printf(getBot(), "Thud!");
                }
            }
        }
        else {
            N.narrative().printf(getBot(), "Thud!");
        }
    }
}
