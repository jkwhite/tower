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


public class Attach extends Unarmed {
    private int _leechPower;


    public Attach(int leechPower) {
        _leechPower = leechPower;
        setIdentified(true);
    }

    public int getLeechPower() {
        return _leechPower;
    }

    public void setLeechPower(int power) {
        _leechPower = power;
    }

    public Attack invoke(final NHBot attacker, final NHBot defender, Attack a) {
        super.invoke(attacker, defender, a);
        for(Item i:defender.getWearing()) {
            if(i instanceof Deterrent) {
                N.narrative().print(defender, Grammar.start(attacker, "draw")+" back!");
                return null;
            }
        }
        final Leech leech = new Leech(_leechPower, attacker, defender);
        attacker.start(leech);

        final EnvironmentAdapter mover = new EnvironmentAdapter() {
            void move(Bot b, MSpace to) {
                b.getEnvironment().move(((NHEnvironment)b.getEnvironment()).getMSpace().directionTo(to));
                b.getEnvironment().face(defender);
            }

            public void moved(Bot b, MSpace from, MSpace to) {
                if(((NHBot)attacker).isDead()) {
                    defender.removeListener(this);
                    return;
                }
                if(from==to) {
                    return;
                }
                //System.err.println("FROM: "+from);
                //System.err.println("TO: "+to);
                if(from.isAdjacentTo(to)) {
                    //System.err.println("ADJACENT");
                    if(from.isOccupied()) {
                        //System.err.println("FROM IS OCC");
                        // more than one leech
                        boolean found = false;
                        MSpace t = attacker.getEnvironment().getMSpace().move(from.directionTo(to));
                        if(t!=null&&!t.isOccupied()&&t.isWalkable()) {
                            move(attacker, t);
                            found = true;
                        }
                        else {
                            for(MSpace m:attacker.getEnvironment().getMSpace().surrounding()) {
                                if(m!=null&&m.isWalkable()&&!m.isOccupied()) {
                                    move(attacker, m);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if(!found) {
                            attacker.start(null);
                        }
                    }
                    else {
                        //System.err.println("SIMPLE MOVE");
                        move(attacker, from);
                    }
                }
                else {
                    //System.err.println("NOT ADJACENT");
                    attacker.start(null);
                }
            }
        };
        attacker.addListener(new NHEnvironmentAdapter() {
            public void died(Bot b) {
                defender.removeListener(mover);
                attacker.removeListener(this);
            }

            public void actionStopped(NHBot b, ProgressiveAction a) {
                defender.removeListener(mover);
                attacker.removeListener(this);
            }
        });
        defender.addListener(mover);

        return null;
    }

    public int getRate() {
        return 99;
    }

    public int getPower() {
        return 0;
    }

    public float getWeight() {
        return 0;
    }

    public float getSize() {
        return 0;
    }

    public String getVerb() {
        return "attach to";
    }

    public String getName() {
        return "absorb";
    }

    public static class Leech implements ProgressiveAction {
        private NHBot _leecher;
        private NHBot _victim;
        private boolean _first = true;
        private int _power;


        public Leech(int power, NHBot leecher, NHBot victim) {
            _power = power;
            _leecher = leecher;
            _victim = victim;
        }

        public int getInterruptRate() {
            return 30;
        }

        public boolean iterate() {
            if(_victim.isDead()) { // killed by some other means
                return false;
            }
            int amt = Math.min(_power, _victim.getHp());
            _victim.setHp(_victim.getHp()-amt);
            _leecher.setHp(_leecher.getHp()+amt);
            int extra = _leecher.getHp()-_leecher.getMaxHp();
            if(extra>0) {
                _leecher.setHp(_leecher.getMaxHp());
                _leecher.setHunger(_leecher.getHunger()-5*extra);
            }
            if(_victim.isPlayer()||_first) { // might get annoying otherwise
                N.narrative().print(_victim, Grammar.first(Grammar.possessive(_victim))+" life essence is being leeched!");
                _first = false;
            }
            if(_victim.getHp()==0) {
                _victim.die("Leeched to death by "+Grammar.nonspecific(_leecher));
            }
            boolean stop = _victim.isDead();
            if(!stop) {
                if(_leecher.isPlayer()) {
                    stop = ! N.narrative().confirm(_leecher, "Continue leeching?");
                }
                else {
                    stop = Hunger.Degree.degreeFor(_leecher.getHunger()) == Hunger.Degree.satiated;
                    if(stop) {
                        //System.err.println("STOPPING FROM SATIATION");
                    }
                }
            }
            return !stop;
        }

        public void interrupted() {
        }

        public void stopped() {
        }

        public String getExcuse() {
            return "leeching from "+Grammar.nonspecific(_victim);
        }
    }
}
