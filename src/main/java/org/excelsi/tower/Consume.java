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


/**
 * Eats a comestible or swallows a pill.
 */
public class Consume extends ItemAction implements SpaceAction {
    public Consume() {
        this(null);
    }

    public Consume(Item i) {
        super("eat", i);
    }

    public String getDescription() {
        return "Eat food or swallow a pill.";
    }

    public boolean isPerformable(NHBot b) {
        Item[] here = b.getEnvironment().getMSpace().getItem();
        for(int i=0;i<here.length;i++) {
            if(here[i] instanceof Comestible) {
                return true;
            }
        }
        return false;
    }

    public boolean accepts(Item i, NHBot b) {
        return i instanceof Comestible || i instanceof Pill;
    }

    public void act() {
        boolean onGround = false;
        Item chosen = getItem();
        Container container = getBot().getInventory();
        if(chosen==null) {
            Item[] here = getBot().getEnvironment().getMSpace().getItem();
            for(int i=0;i<here.length;i++) {
                if(here[i] instanceof Comestible) {
                    if(getBot().isLevitating()) {
                        N.narrative().print(getBot(), "There is "+Grammar.singular(here[i])+" here, but you cannot reach the ground.");
                        break;
                    }
                    else if(N.narrative().confirm(getBot(), "There is "+Grammar.singular(here[i])+" here. Do you want to eat it?")) {
                        chosen = here[i];
                        container = getBot().getEnvironment().getMSpace();
                        onGround = true;
                        break;
                    }
                }
            }
            if(chosen==null) {
                chosen = N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(), "eat", new ItemFilter() {
                    public boolean accept(Item i, NHBot bot) {
                        return i instanceof Comestible || i instanceof Pill;
                    }
                }), false);
            }
        }
        else {
            onGround = !getBot().getInventory().contains(chosen);
            if(!getBot().getInventory().contains(chosen)) {
                container = getBot().getEnvironment().getMSpace();
            }

            if(onGround&&getBot().isLevitating()) {
                N.narrative().print(getBot(), "There is "+Grammar.singular(chosen)+" here, but "+Grammar.noun(getBot())+" cannot reach the ground.");
                return;
                //TODO: should it count or not?
                //since only NPCs will go through this code path, seems like it
                //should count.
                //throw new ActionCancelledException();
            }
        }
        if(chosen instanceof Comestible) {
            final Comestible comestible = (Comestible) chosen;
            getBot().start(new Consuming(comestible, container));
        }
        else {
            N.narrative().printfm(getBot(), Grammar.start(getBot(), "swallow")+" "+Grammar.singular(chosen)+".");
            chosen.setClassIdentified(true);
            getBot().getInventory().consume(chosen);
            chosen.invoke(getBot());
        }
    }

    private Comestible consume(Comestible chosen, int eat, int nut, Container container) {
        boolean doomed = Hunger.Degree.degreeFor(getBot().getHunger()-nut)==Hunger.Degree.american;

        boolean readd = false;
        if(chosen.getCount()>1) {
            readd = true;
            chosen = (Comestible) container.split(chosen);
        }
        chosen.addConsumed(eat);
        if(readd) {
            container.add(chosen);
        }
        boolean partial = false;
        if(chosen.getModifiedNutrition()>0) {
            partial = true;
        }
        else {
            // TODO: this should never be false but sometimes it is, for monsters eating off the ground
            if(container.contains(chosen)) {
                container.consume(chosen);
            }
        }
        //System.err.println("ADDING NUT: "+nut);
        if(nut>0) {
            if(!partial&&!doomed) {
                if(chosen.getStatus()==Status.blessed) {
                    if(getBot().isPlayer()) {
                        if(Rand.om.nextBoolean()) {
                            N.narrative().print(getBot(), "That was the most delicious "+chosen.getShortName()+" ever.");
                        }
                        else {
                            N.narrative().print(getBot(), "Delicious!");
                        }
                    }
                }
                else {
                    N.narrative().print(getBot(), Grammar.start(getBot(), "finish")+" the "+chosen.getShortName()+".");
                    if(Hunger.Degree.degreeFor(getBot().getHunger())==Hunger.Degree.satiated) {
                        //N.narrative().print(getBot(), "Ahh.... ");
                    }
                }
            }
        }
        else if(nut==0) {
            if(!partial&&!doomed) {
                if(getBot().isPlayer()) {
                    N.narrative().print(getBot(), "That tasted like cardboard.");
                }
            }
        }
        else {
            //if(!doomed&&!getBot().isAfflictedBy(Nauseous.NAME)) {
            if(!doomed&&!getBot().isAfflictedBy(Delay.NAME)) {
                if(!getBot().isAfflictedBy(Nauseous.NAME)) {
                    if(getBot().isPlayer()) {
                        N.narrative().print(getBot(), "Ugghh... You feel nauseous.");
                    }
                    else {
                        N.narrative().print(getBot(), Grammar.start(getBot(), "look")+" sick.");
                    }
                }
                getBot().addAffliction(new Delay(new Nauseous(Rand.om.nextInt(25)+10), 1+Rand.om.nextInt(3)));
            }
        }
        if(nut!=0) {
            getBot().setHunger(getBot().getHunger()-nut);
        }
        chosen.invoke(getBot());
        return chosen;
    }

    static class Vomit extends Comestible {
        private boolean _nut = Rand.om.nextBoolean();

        public int getNutrition() { return _nut?Hunger.RATE/4:-1; }
        public String getColor() { return "puke-green"; }
        public float getWeight() { return 0.1f; }
        public float getSize() { return 0.1f; }
        public int score() { return 0; }
    }

    public class Consuming implements ProgressiveAction {
        private static final int RATE = Hunger.RATE/6;
        private Comestible _c;
        private Container _container;
        private boolean _first = true;


        public Consuming(Comestible c, Container container) {
            _c = c;
            _container = container;
            if(_c.getModifiedNutrition()>RATE) {
                N.narrative().print(getBot(), Grammar.start(getBot(), "start")+" eating the "+c.getShortName()+".");
            }
        }

        public int getInterruptRate() {
            return 100;
        }

        public Comestible getComestible() {
            return _c;
        }

        public void stopped() {
        }

        public void interrupted() {
            N.narrative().print(getBot(), Grammar.start(getBot(), "stop")+" eating.");
        }

        public String getExcuse() {
            return "eating "+Grammar.singular(_c);
        }

        public boolean isOnGround() {
            return _container instanceof NHSpace;
        }

        public boolean iterate() {
            Hunger.Degree deg = Hunger.Degree.degreeFor(getBot().getHunger());
            if(deg==Hunger.Degree.satiated
                && N.narrative().confirm(getBot(), "You're getting very full. Stop eating the "+_c.getShortName()+"?")) {
                return false;
            }
            else {
                if(!_first) {
                    if(deg==Hunger.Degree.satiated) {
                        if(Rand.om.nextBoolean()) {
                            N.narrative().print(getBot(), Grammar.start(getBot(), "force")+" another bite down.");
                        }
                        else {
                            N.narrative().print(getBot(), Grammar.start(getBot(), "choke")+" down "+Grammar.possessive(getBot())+" food.");
                        }
                    }
                    else {
                        //N.narrative().print(getBot(), Grammar.start(getBot(), "continue")+" eating the "+_c.getShortName()+".");
                    }
                }
                int eat = Math.min(Math.abs(_c.getModifiedNutrition()), RATE);
                int nut = eat;
                if(_c.getModifiedNutrition()<0) {
                    nut = -nut;
                }
                switch(_c.getStatus()) {
                    case blessed:
                        nut += nut/2;
                        break;
                    case cursed:
                        nut = Rand.d100(66)?-1:0;
                        break;
                }
                _c = consume(_c, eat, nut, _container);
                _first = false;
                return _c.getModifiedNutrition()>0;
            }
        }
    }
}
