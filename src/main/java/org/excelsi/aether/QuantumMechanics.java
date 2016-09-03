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
package org.excelsi.aether;


import java.util.ArrayList;
import java.util.List;
import org.excelsi.matrix.Direction;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import org.excelsi.matrix.MSpace;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Arrays;
import org.excelsi.matrix.Filter;


public class QuantumMechanics implements Mechanics {
    //private static final boolean SHOW_MECH = true;
    private static final boolean SHOW_MECH = Boolean.getBoolean("tower.showmech");
    private static final boolean OLD = Boolean.getBoolean("tower.narrative");

    private List<MechanicsListener> _listeners = new ArrayList<MechanicsListener>();


    public void addMechanicsListener(MechanicsListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeMechanicsListener(MechanicsListener listener) {
        if(!_listeners.remove(listener)) {
            throw new IllegalArgumentException(listener+" is not listening to "+this);
        }
    }

    // NO STAT GAIN
    public Outcome resolve(Context c, NHBot attacker, NHBot attackee, Attack a) {
        QOutcome outcome = new QOutcome();
        outcome.setAttacker(attacker);
        outcome.setDefender(attackee);
        outcome.setAttack(a);
        if(SHOW_MECH) {
            System.err.println(attacker+" "+Grammar.toBe(attacker)+" attacking "+attackee);
            System.err.println("base skill="+attacker.getSkill(a.getWeapon()));
        }
        if(attackee.intercept(c, a)) {
            outcome.setResult(Outcome.Result.intercept);
            return outcome;
        }
        int modqu = attackee.getModifiedQuickness();
        if(attackee.isInvisible()) {
            modqu *= 4;
        }
        if(attackee.getBlind()>0) {
            modqu /= 4;
        }
        if(attackee.isConfused()) {
            modqu /= 2;
        }
        int hitRate = lookup((attacker.getSkill(a.getWeapon())+a.getWeapon().getRate()
                +(100-modqu))/3);
        if(attacker.isInvisible()) {
            hitRate *= 4;
        }
        if(attacker.getBlind()>0) {
            hitRate /= 4;
        }
        if(attacker.isConfused()) {
            hitRate /= 2;
        }
        if(SHOW_MECH) {
            System.err.println("hitRate="+hitRate);
        }
        if(!attacker.isPlayer()&&a.getWeapon()==attacker.getWielded()) {
            //N.narrative().print(attacker, Grammar.start(attacker, "swing")+" "+Grammar.pronoun(attacker, (Item)attacker.getWielded())+"!");
            if(OLD) {
                N.narrative().printf(attacker, "%V %M!", attacker, "swing", attacker, (Item)attacker.getWielded());
            }
        }
        if(Rand.d100(hitRate)) {
            Slot s = attackee.getForm().getRandomSlot();
            Armament arm = (Armament) s.getOccupant();
            if(SHOW_MECH) {
                System.err.println("SELECTED ARMAMENT: "+arm);
            }
            if(arm==null) {
                arm = attackee.getForm().getNaturalArmor();
            }
            if(arm instanceof Interceptor && ((Interceptor)arm).intercepts(a)) {
                Performable r = ((Interceptor)arm).intercept(attacker, attackee, a);
                outcome.setResult(Outcome.Result.intercept);
                outcome.setCorollary(r);
                if(SHOW_MECH) {
                    System.err.println("COROLLARY: "+r);
                }
            }
            else {
                resolveHit(attacker, attackee, a, s, arm, outcome);
                outcome.setResult(Outcome.Result.hit);
            }
        }
        else {
            outcome.setResult(Outcome.Result.miss);
            //N.narrative().print(attacker, Grammar.start(attacker)+" "+Grammar.conjugate(attacker, "miss")+"!");
            if(OLD) {
                N.narrative().printf(attacker, "%V!", attacker, "miss");
            }
        }
        return outcome;
    }

    // STAT GAIN
    public Outcome[] resolve(Context c, NHBot attacker, Direction d, Attack a, Filter filter) {
        return resolve(c, attacker, attacker.getEnvironment().getMSpace(), d, a, filter);
    }

    // STAT GAIN
    public Outcome[] resolve(Context c, NHBot attacker, NHSpace origin, Direction d, Attack a, Filter filter) {
        if(a.getType()==Attack.Type.ball) {
            final NHSpace start = origin;
            Set<NHSpace> affected = new HashSet<NHSpace>(Math.max(200, a.getRadius()*a.getRadius()));
            List<NHSpace> frontier = new LinkedList<NHSpace>();
            List<NHBot> defenders = new ArrayList<NHBot>();
            frontier.add(start);
            if(start.isOccupied()) {
                defenders.add(start.getOccupant());
            }
            affected.add(start);
            while(frontier.size()>0) {
                NHSpace n = frontier.remove(0);
                for(MSpace sur:n.surrounding()) {
                    if(sur!=null&&!affected.contains(sur)&&(filter==null||filter.accept(sur))) {
                        if(start.distance(sur)<=a.getRadius()&&sur.isWalkable()) {
                            frontier.add((NHSpace)sur);
                            if(sur!=start&&sur.isOccupied()) {
                                defenders.add((NHBot)sur.getOccupant());
                            }
                            affected.add((NHSpace)sur);
                        }
                    }
                }
            }
            if(!a.affectsAttacker()&&defenders.contains(attacker)) {
                defenders.remove(attacker);
            }
            NHSpace[] sort = (NHSpace[]) affected.toArray(new NHSpace[affected.size()]);
            Arrays.sort(sort, new Comparator<NHSpace>() {
                public int compare(NHSpace n1, NHSpace n2) {
                    float f1 = start.distance(n1), f2 = start.distance(n2);
                    return f1>f2?1:f1<f2?-1:0;
                }
            });
            attackStarted(a, attacker, null, sort);
            List<Outcome> outcomes = new ArrayList<Outcome>(defenders.size());
            for(NHBot def:defenders) {
                Outcome o = resolve(c, attacker, def, a);
                attackEnded(a, attacker, def, o);
                if(o.getCorollary()!=null) {
                    o.getCorollary().perform(c);
                }
                outcomes.add(o);
            }
            attackEnded(a, attacker, null, null);
            Outcome[] results = (Outcome[]) outcomes.toArray(new Outcome[outcomes.size()]);
            statGain(results);
            return results;
        }
        else {
            int distance;
            if(a.getType()==Attack.Type.melee) {
                distance = 1;
            }
            else {
                if(a.isPhysical()) {
                    distance = a.getRadius();
                    //distance = attacker!=null?attacker.getModifiedStrength()/5:Integer.MAX_VALUE;
                    //if(a.getWeapon().toItem()!=null) {
                        //distance = Math.max(1, distance-(int)a.getWeapon().toItem().getWeight());
                    //}
                }
                else {
                    distance = 20;
                }
            }
            //Outcome[] results = resolve(attacker, d, a, attacker.getEnvironment().getMSpace(), distance);
            Outcome[] results = resolve(c, attacker, d, a, origin, distance);
            skillUp(attacker, a.getWeapon());
            statGain(results);
            return results;
        }
    }

    // NO STAT GAIN
    public Outcome[] resolve(final Context c, NHBot attacker, Direction d, Attack a, final NHSpace start, int distance) {
        List<NHSpace> spaces = new ArrayList<NHSpace>();
        NHSpace dest = start;
        boolean cont = d!=null;
        if(d==null) {
            spaces.add(dest);
        }
        else {
            while(distance-->0) { // post because must be able to throw at least 1
                spaces.add(dest);
                NHSpace next = (NHSpace) dest.move(d);
                if(next==null||!next.isWalkable()) {
                    cont = false;
                    break;
                }
                dest = next;
                if(dest.isOccupied()) {
                    spaces.add(dest);
                    break;
                }
            }
        }
        NHSpace[] path = (NHSpace[]) spaces.toArray(new NHSpace[spaces.size()]);
        for(int i=0;i<path.length-1;i++) {
            a.getWeapon().invoke(attacker, path[i], a);
        }
        QOutcome outcome;
        if(dest.isOccupied()) {
            if(dest.getOccupant()!=attacker||a.affectsAttacker()) {
                // attack
                attackStarted(a, attacker, dest.getOccupant(), path);
                outcome = (QOutcome) resolve(c, attacker, dest.getOccupant(), a);
                if(outcome.getCorollary()!=null) {
                    cont = false;
                }
                if(a.getType()==Attack.Type.missile) {
                    if(a.getWeapon().toItem()!=null) {
                        if(outcome.getResult()==Outcome.Result.miss) {
                            dest.add(a.getWeapon().toItem(), attacker, start);
                        }
                        else if(outcome.getResult()==Outcome.Result.hit && Rand.om.nextBoolean()) {
                            outcome.getDefender().getInventory().add(a.getWeapon().toItem());
                        }
                    }
                }
                // TODO
                /*
                if(outcome.getResult()==Outcome.Result.hit) {
                    //a.getWeapon().invoke(attacker, outcome.getDefender());
                }
                */
            }
            else {
                outcome = new QOutcome(a, attacker, null, Outcome.Result.miss, null);
            }
        }
        else if(a.getType()==Attack.Type.missile && a.getWeapon().toItem()!=null) {
            dest.add(a.getWeapon().toItem(), attacker, start);
            outcome = new QOutcome(a, attacker, null, Outcome.Result.miss, null);
        }
        else {
            attackStarted(a, attacker, null, path);
            outcome = new QOutcome(a, attacker, null, Outcome.Result.miss, null);
        }
        a.getWeapon().invoke(attacker, path[path.length-1], a);
        outcome.setPath(path);
        Outcome[] outcomes;
        if(distance>0&&cont&&a.getType()==Attack.Type.bolt) {
            Outcome[] adds = resolve(c, attacker, d, a, dest, distance);
            Outcome[] total = new Outcome[adds.length+1];
            total[0] = outcome;
            System.arraycopy(adds, 0, total, 1, adds.length);
            outcomes = total;
        }
        else {
            outcomes = new Outcome[]{outcome};
        }
        attackEnded(a, attacker, outcome.getDefender(), outcome);
        if(outcome.getCorollary()!=null) {
            outcome.getCorollary().perform(c);
        }
        skillUp(attacker, a.getWeapon());
        statGain(outcomes);
        return outcomes;
    }

    private void attackStarted(Attack a, NHBot attacker, NHBot defender, NHSpace[] path) {
        for(MechanicsListener l:getListeners()) {
            l.attackStarted(this, a, attacker, defender, path);
        }
    }

    private void attackEnded(Attack a, NHBot attacker, NHBot defender, Outcome o) {
        for(MechanicsListener l:getListeners()) {
            l.attackEnded(this, a, attacker, defender, o);
        }
    }

    private void skillUp(NHBot bot, Armament arm) {
        if(arm!=null&&arm.getSkill()!=null) {
            bot.skillUp(arm.getSkill());
        }
    }

    /**
     * Calculates and grants stat gains based on combat results.
     * Every outcome is assumed to have the same attacker, thus
     * attacker stat gains are only calculated for outcome[0].
     *
     * @param outcomes combat results
     */
    private void statGain(Outcome[] outcomes) {
        NHBot attacker = outcomes[0].getAttacker();
        if(true||attacker.isPlayer()) {
            // should calculate for all monsters but it's probably
            // not necessary.
            Attack a = outcomes[0].getAttack();
            Armament arm = a.getWeapon();
            boolean doHp = false;
            boolean doMp = false;
            if(arm.getStats()!=null) {
                //String[] stats = arm.getStats().split("/");
                for(Stat s:arm.getStats()) {
                    //Stat s = Stat.fromString(stat);
                    if(s==Stat.co||s==Stat.st||Rand.d100(33)) {
                        doHp = true;
                    }
                    increaseStat(attacker, s);
                }
            }
            if(doHp) {
                increaseHp(attacker);
            }
        }
        for(Outcome o:outcomes) {
            NHBot defender = o.getDefender();
            if(defender!=null&&defender.isPlayer()) {
                if(o.getResult()==Outcome.Result.miss) {
                    increaseStat(defender, Stat.qu);
                }
                else if(o.getResult()==Outcome.Result.hit) {
                    increaseStat(defender, Stat.co);
                    increaseHp(defender);
                }
            }
        }
    }

    private void increaseStat(NHBot bot, Stat s) {
        if(bot.getEnvironment()==null) {
            return;
        }
        if(Rand.d100()>bot.getStat(s)&&Rand.d100()<bot.getStatGainRate()) {
            bot.setStat(s, bot.getStat(s)+1);
            if(bot.isPlayer()||(bot.threat(bot.getEnvironment().getPlayer())==Threat.familiar&&bot.getEnvironment().getPlayer().getEnvironment().getVisibleBots().contains(bot))) {
                N.narrative().print(bot, Grammar.start(bot, "feel")+" "+s.getAdjective()+".");
            }
        }
    }

    private void increaseHp(NHBot bot) {
        if(bot.getEnvironment()==null||(!bot.isPlayer()&&bot.threat(bot.getEnvironment().getPlayer())!=Threat.familiar)) {
            return;
        }
        int chance = bot.getStatGainRate()/2;
        if(Rand.d100(chance)) {
            int mh = bot.getMaxHp();
            int inc = (int) Math.max(1f, mh*Rand.om.nextFloat()*0.04f+0.01f);
            bot.setMaxHp(inc+mh);
            bot.setHp(inc+bot.getHp());
            if(bot.isPlayer()||bot.threat(bot.getEnvironment().getPlayer())==Threat.familiar) {
                N.narrative().print(bot, Grammar.start(bot, "feel")+" healthier.");
            }
        }
    }

    /**
     * Computes a sigmoid centered at <code>rate=50</code>.
     *
     * @return percentage value in (0,100)
     */
    private int lookup(int rate) {
        double d = 1/(1+(Math.pow(Math.E, -rate/10f+5f)));
        if(SHOW_MECH) System.err.println("rate="+rate+", sig="+d);
        return (int) (100*d);
    }

    private void resolveHit(NHBot attacker, NHBot attackee, Attack a, Slot s, Armament armor, QOutcome outcome) {
        NHBot source = attackee!=null?attackee:attacker;
        //int modst = attacker!=null?attacker.getModifiedStrength():50;
        //int modag = attacker!=null?attacker.getModifiedAgility():50;
        if(a==null) {
            throw new IllegalArgumentException("attack is null");
        }
        Stat[] stats = a.getWeapon().getStats();
        int modag = attacker.getModifiedAgility();
        int modst = 0;
        if(stats!=null) {
            for(int i=0;i<stats.length;i++) {
                modst += attacker!=null?attacker.getModifiedStat(stats[i]):50;
            }
            modst /= stats.length;
        }
        else {
            modst = 50;
        }
        //int maxDmg = Math.max(1,(2*modst+modag)*(1+a.getWeapon().getModifiedPower())/300);
        int maxDmg = Math.max(1,(3*modst)*(1+a.getWeapon().getModifiedPower())/300);
        final int die = 6;
        int baseDmg = 0;
        // allow baseDmg to exceed maxDmg by at most 'die', then cap later
        for(int i=0;i<=maxDmg/die;i++) {
            baseDmg += (1+Rand.om.nextInt(die));
        }
        baseDmg = Math.min(baseDmg, maxDmg);
        //int baseDmg = 1+Rand.om.nextInt(Math.max(1,(2*modst+modag)*(1+a.getWeapon().getModifiedPower())/300));
        if(SHOW_MECH) {
            System.err.println("max="+maxDmg);
            System.err.println("weapon="+a.getWeapon());
            System.err.println("wpow="+a.getWeapon().getModifiedPower());
            System.err.println("modst="+modst);
            System.err.println("modag="+modag);
            System.err.println("baseDmg="+baseDmg);
        }
        int dmg = baseDmg;

        boolean offShield = false;
        boolean shatters = false;
        if(armor!=null) {
            if(SHOW_MECH) {
                System.err.println("armor="+armor);
            }
            // try for damage reduction
            int blockRate = lookup((armor.getRate()+attackee.getSkill(armor)+(100-modag))/3);
            if(SHOW_MECH) {
                System.err.println("blockRate="+blockRate);
            }
            if(Rand.om.nextInt(100)<=blockRate) {
                int modpow = armor.getModifiedPower();
                int skill = attackee.getSkill(armor);
                if(SHOW_MECH) {
                    System.err.println("reduction chance="+(modpow+skill)/2);
                }
                for(int i=0;i<baseDmg;i++) {
                    if(Rand.d100((modpow+skill)/2)) {
                        dmg--;
                    }
                }
                if(s.getSlotType()==SlotType.hand) {
                    // shield
                    armor.setHp(Math.max(0, armor.getHp()-dmg));
                    dmg = 0;
                    offShield = true;
                }
                else {
                    if(SHOW_MECH) {
                        System.err.println(armor+"armor wear="+(baseDmg-dmg));
                    }
                    armor.setHp(Math.max(0, armor.getHp()-(baseDmg-dmg)));
                }
                if(armor.getHp()==0&&armor.toItem()!=null) {
                    N.narrative().print(attackee, Grammar.first(Grammar.possessive(attackee, armor.toItem()))+" shatters!");
                    attackee.getInventory().destroy(armor.toItem());
                    shatters = true;
                }
            }
            skillUp(attackee, armor);
        }
        if(SHOW_MECH) {
            System.err.println("dmg="+dmg);
        }
        if(dmg>0) {
            if(a.getWeapon().getModifiedPower()>=0) {
                if(SHOW_MECH) {
                    if(OLD) {
                        N.narrative().print(source, Grammar.first(a.getSource().toString(a.getWeapon().getVerb()))+" "+Grammar.noun(attackee)+(SHOW_MECH?" for "+dmg:"")+".");
                    }
                }
                else {
                    if(OLD) {
                        N.narrative().printf(source, "%A %n.", a.getSource(), a.getWeapon().getVerb(), attackee);
                    }
                    if(attackee.isInvisible()) {
                        N.narrative().printf(source, "%A something invisible!", a.getSource(), a.getWeapon().getVerb());
                    }
                }
            }
            a.getWeapon().invoke(attacker, attackee, a);
            if(!attackee.isDead()) {
                attackee.setHp(Math.max(0, attackee.getHp()-dmg));
                // defender must stop actions when hit
                attackee.interrupt();
                if(attackee.getHp()<=0) {
                    if(!attackee.isPlayer()) {
                        if(OLD) {
                            N.narrative().print(attacker, Grammar.start(attackee)+" "+Grammar.conjugate(attackee, "die")+".");
                        }
                    }
                    if(attackee.getEnvironment()!=null&&attackee.getEnvironment().getMSpace()!=null) {
                        if(attacker.getEnvironment()!=null&&attacker.getEnvironment().getMSpace()!=null) {
                            Direction d = attacker.getEnvironment().getMSpace().directionTo(attackee.getEnvironment().getMSpace());
                            int dist = Rand.om.nextInt(Math.max(1, (int)((float)attacker.getModifiedStrength() / 25 + (float)dmg/25)));
                            while(--dist>0) {
                                MSpace f = attackee.getEnvironment().getMSpace().move(d);
                                if(f!=null&&f.isWalkable()&&!f.isOccupied()) {
                                    try {
                                        attackee.getEnvironment().move(d);
                                    }
                                    catch(IllegalArgumentException e) {
                                        java.util.logging.Logger.global.severe("failed to move doomed "+attackee+": "+e.toString());
                                    }
                                }
                                else {
                                    break;
                                }
                            }
                        }
                    }

                    int ob = Grammar.pov().getBlind();
                    Grammar.pov().setBlind(0);
                    String by = Grammar.nonspecific(attacker);
                    // check for pack
                    int same = 0;
                    if(attacker.getEnvironment()!=null&&attacker.getEnvironment().getMSpace()!=null) {
                        for(MSpace m:attacker.getEnvironment().getMSpace().surrounding()) {
                            if(m!=null&&m.isOccupied()&&attacker.getCommon().equals(((NHBot)m.getOccupant()).getCommon())) {
                                same++;
                            }
                        }
                    }
                    if(attackee.getEnvironment()!=null&&attackee.getEnvironment().getMSpace()!=null) {
                        for(MSpace m:attackee.getEnvironment().getMSpace().surrounding()) {
                            if(m!=null&&m.isOccupied()&&m.getOccupant()!=attacker&&attacker.getCommon().equals(((NHBot)m.getOccupant()).getCommon())) {
                                same++;
                            }
                        }
                    }
                    if(same>1) {
                        //by = Grammar.nonspecific(attacker.toPack());
                        by = Grammar.pluralize(attacker.toString());
                    }
                    String verb = attacker.getTemperament()==Temperament.hungry?"Eaten":"Killed";
                    attackee.die(new Source(attacker), verb+" by "+by);
                    Grammar.pov().setBlind(ob);
                }
            }
        }
        else {
            if(offShield) {
                if(!shatters) {
                    N.narrative().print(attacker, Grammar.first(Grammar.possessive(attacker))+" attack hits "+Grammar.possessive(attackee)+" shield!");
                    outcome.setBlocked(true);
                }
            }
            else {
                switch(Rand.om.nextInt(2)) {
                    case 0:
                        if(a.getType()==Attack.Type.missile && a.getWeapon().toItem()!=null) {
                            N.narrative().print(attacker, Grammar.start(attackee)+" "+Grammar.conjugate(attackee, "grab")+" "+Grammar.possessive(attacker, a.getWeapon().toItem())+" out of the air!");
                        }
                        else {
                            N.narrative().print(attacker, Grammar.start(attackee)+" "+Grammar.conjugate(attackee, "parry")+" "+Grammar.possessive(attacker)+" attack.");
                        }
                        break;
                    case 1:
                        N.narrative().print(attacker, Grammar.start(attackee)+" "+Grammar.conjugate(attackee, "dodge")+" "+Grammar.possessive(attacker)+" attack.");
                        break;
                }
            }
        }
        // do this here so the 'hit' narrative is printed before any infliction messages
        if(armor!=null&&a.getType()==Attack.Type.melee) {
            Attack counter = armor.invoke(attacker, attackee, a);
            if(counter!=null) {
                Slot[] ss = attacker.getForm().getSlots(SlotType.hand);
                Slot slot = null;
                if(ss.length>0) {
                    slot = ss[0];
                }
                else {
                    slot = attacker.getForm().getRandomSlot();
                }
                resolveHit(attackee, attacker, counter, slot, null, null);
            }

            // calc damage to weapon if any
            Armament at = a.getWeapon();
            if(at.toItem()!=null) {
                int chance = (10+(100-attacker.getSkill(at)))/2;
                int wear = 0;
                for(int i=0;i<dmg;i++) {
                    if(Rand.d100(15)&&Rand.d100(chance)) {
                        wear++;
                    }
                }
                if(SHOW_MECH) {
                    System.err.println("weapon wear="+wear);
                }
                at.setHp(Math.max(0, at.getHp()-wear));
                if(at.getHp()==0) {
                    N.narrative().print(attacker, Grammar.first(Grammar.possessive(attacker, at.toItem()))+" shatters!");
                    attacker.getInventory().destroy(at.toItem());
                }
            }
        }
    }

    private List<MechanicsListener> getListeners() {
        return new ArrayList<MechanicsListener>(_listeners);
    }

    private static class QOutcome implements Outcome {
        private Attack _a;
        private NHBot _attacker;
        private NHBot _defender;
        private Result _r;
        private NHSpace[] _path;
        private Performable _corollary;
        private boolean _blocked;


        public QOutcome() {
        }

        public QOutcome(Attack a, NHBot attacker, NHBot defender, Result r, NHSpace[] path) {
            _a = a;
            _attacker = attacker;
            _defender = defender;
            _r = r;
            _path = path;
        }

        public void setAttack(Attack a) {
            _a = a;
        }

        public Attack getAttack() {
            return _a;
        }

        public void setBlocked(boolean blocked) {
            _blocked = blocked;
        }

        public boolean isBlocked() {
            return _blocked;
        }

        public void setAttacker(NHBot attacker) {
            _attacker = attacker;
        }

        public NHBot getAttacker() {
            return _attacker;
        }

        public void setDefender(NHBot defender) {
            _defender = defender;
        }

        public NHBot getDefender() {
            return _defender;
        }

        public void setResult(Result r) {
            _r = r;
        }

        public Result getResult() {
            return _r;
        }

        public NHSpace getStartSpace() {
            return _path[0];
        }

        public NHSpace getEndSpace() {
            return _path[_path.length-1];
        }

        public NHSpace[] getPath() {
            return _path;
        }

        public void setPath(NHSpace[] path) {
            _path = path;
        }

        public void setCorollary(Performable r) {
            _corollary = r;
        }

        public Performable getCorollary() {
            return _corollary;
        }
    }
}
