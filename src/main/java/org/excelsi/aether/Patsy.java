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


import java.util.*;

import org.excelsi.matrix.*;


public class Patsy extends DefaultNHBot {
    private static final long serialVersionUID = 1L;
    private Level _level;
    private int _maxLevel = 1;
    private boolean _hidden = false;
    private boolean _debug = false;
    private boolean _autopickup = getBoolean("tower.autopickup", true);
    private String _autopickupItems = System.getProperty("tower.autopickupitems", "$");
    private String _packorder = System.getProperty("tower.packorder", "([%!,?+]*");
    private Map<String,Notch> _kills = new HashMap<String,Notch>();
    private Map<String,NHBot> _anim = new HashMap<String,NHBot>();
    private Set<String> _catalogue = new HashSet<String>();
    private Map<String,Set<String>> _catalogues = new HashMap<String,Set<String>>();
    private Set<String> _spaceCatalogue = new HashSet<String>();
    private Set<String> _itemCatalogue = new HashSet<String>();
    private List<Talent> _talents = new ArrayList<Talent>();
    private List<NHBot> _familiars = new ArrayList<NHBot>();
    private int _baseScore = 0;
    private String _selectionText;
    private String _death;
    private transient InputSource _inputSource;


    public static boolean getBoolean(String p, boolean def) {
        String s = System.getProperty(p);
        if(s==null) {
            return def;
        }
        return Boolean.getBoolean(p);
    }

    public Patsy() {
        Inventory.setPackorder(_packorder);
        addListener(new NHEnvironmentAdapter() {
            public void discovered(Bot b, List<MSpace> s) {
                for(int i=0;i<s.size();i++) {
                    if(s.get(i)==null) {
                        continue;
                    }
                    String n = s.get(i).getClass().getName();
                    if(!_spaceCatalogue.contains(n)) {
                        _spaceCatalogue.add(n);
                    }
                }
            }

            public void attacked(NHBot b, NHBot attacked) {
                if(attacked.isDead()) {
                    Notch n = _kills.get(attacked.getCommon());
                    if(n==null) {
                        n = new Notch(attacked.getCommon());
                        _kills.put(attacked.getCommon(), n);
                    }
                    n.increment();
                }
            }

            private Armament _last = null;
            public void attacked(NHBot b, Outcome outcome) {
                Armament arm = outcome.getAttack().getWeapon();
                if(arm!=_last) {
                    if(arm!=null&&outcome.getAttack().getType()==Attack.Type.melee&&arm.getType()==Armament.Type.missile) {
                        N.narrative().printf(Patsy.this, "%V bashing monsters with %p "+arm.toItem().getName()+".", Patsy.this, "begin", Patsy.this);
                    }
                }
                _last = arm;
            }

            public void noticed(Bot b, List<Bot> bots) {
                for(int i=0;i<bots.size();i++) {
                    analyze((NHBot)bots.get(i));
                }
            }
        });
    }

    public void setInputSource(final InputSource input) {
        _inputSource = input;
    }

    public void addFamiliar(NHBot b) {
        _familiars.add(b);
    }

    public void removeFamiliar(NHBot b) {
        _familiars.remove(b);
    }

    public List<NHBot> getFamiliars() {
        return _familiars;
    }

    public void setTalents(List<Talent> talents) {
        _talents = talents;
    }

    public List<Talent> getTalents() {
        return _talents!=null?_talents:new ArrayList<Talent>(0);
    }

    public Set<String> getCatalogue() {
        return _catalogue;
    }

    public Set<String> getCatalogue(String category) {
        Set<String> c = _catalogues.get(category);
        if(c==null) {
            c = new HashSet<String>();
            _catalogues.put(category, c);
        }
        return c;
    }

    public void setCatalogue(Set<String> cat) {
        _catalogue = cat;
    }

    public Set<String> getSpaceCatalogue() {
        return _spaceCatalogue;
    }

    public Set<String> getItemCatalogue() {
        return _itemCatalogue;
    }

    public Map<String,NHBot> getAnim() {
        return _anim;
    }

    public void analyze(NHBot b) {
        if(_talents!=null) {
            for(int i=0;i<_talents.size();i++) {
                _talents.get(i).apply(this, b);
            }
        }
    }

    public void analyze(Item it) {
        if(_talents!=null) {
            for(int i=0;i<_talents.size();i++) {
                _talents.get(i).apply(this, it);
            }
        }
    }

    public float getModifiedCandela() {
        return modifier().getCandela();
    }

    public float[] getModifiedCandelaColor() {
        return modifier().getCandelaColor();
    }

    public void setSelectionText(String selectionText) {
        _selectionText = selectionText.intern();
    }

    public String getSelectionText() {
        return _selectionText;
    }

    public void setDeath(String death) {
        _death = death;
    }

    public String getDeath() {
        return _death;
    }

    public void addScore(int score) {
        _baseScore += score;
    }

    public String getPackorder() {
        return _packorder;
    }

    public void setPackorder(String packorder) {
        _packorder = packorder;
        Inventory.setPackorder(packorder);
    }

    public boolean isAutopickup() {
        return _autopickup;
    }

    public void setAutopickup(boolean autopickup) {
        _autopickup = autopickup;
    }

    public String getAutopickupItems() {
        return _autopickupItems;
    }

    public void setAutopickupItems(String autopickupItems) {
        _autopickupItems = autopickupItems;
    }

    public void setDebug(boolean debug) {
        _debug = debug;
    }

    public boolean isDebug() {
        return _debug;
    }

    public void setEventSource(EventSource events) {
    }

    public void setLevel(Level level) {
        Level ol = _level;
        _level = level;
        if(_level.getFloor()>_maxLevel) {
            setMaxLevel(_level.getFloor());
        }
        //System.err.println("MOVED TO LEVEL: "+level);
        //new Exception().printStackTrace();
        notifyListeners("level", ol);
    }

    public Level getLevel() {
        return _level;
    }

    public void setMaxLevel(int level) {
        _maxLevel = level;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setHidden(boolean hidden) {
        _hidden = hidden;
    }

    public boolean isHidden() {
        return _hidden;
    }

    public boolean isPlayer() {
        return true;
    }

    public int score() {
        int score = super.score() + _baseScore;
        if(getLevel()!=null) {
            score += 50*_maxLevel;
        }
        for(Notch n:_kills.values()) {
            score += 5*n.getCount();
        }
        return score;
    }

    public Map<String,Notch> getKills() {
        return _kills;
    }

    public void die(Source s, String cause) {
        if(Boolean.getBoolean("tower.creator")) {
            if(!N.narrative().confirm(this, "Die?")) {
                N.narrative().print(this, "Carry on!");
                setHp(getMaxHp());
                return;
            }
        }
        super.die(s, cause);
        if(!cause.endsWith(".")) {
            // add afflictions
            StringBuilder b = new StringBuilder(cause);
            List<String> msgs = new ArrayList<String>();
            for(Affliction a:getAfflictions()) {
                String d = a.getExcuse();
                if(d!=null) {
                    msgs.add(d);
                }
            }
            if(msgs.size()==0) {
                //b.append(".");
            }
            else {
                b.append(", while ");
                for(int i=0;i<msgs.size();i++) {
                    boolean sp = false;
                    b.append(msgs.get(i));
                    if(i<msgs.size()-1&&msgs.size()>2) {
                        b.append(", ");
                        sp = true;
                    }
                    if(i==msgs.size()-2) {
                        if(!sp) {
                            b.append(" ");
                        }
                        b.append("and ");
                    }
                }
            }
            b.append(".");
            cause = b.toString();
        }
        setDeath(cause);
        //N.narrative().more();
        N.narrative().print(this, "You die...");
        //N.narrative().more();
        N.narrative().quit(cause, false);
    }

    public void act() {
    }

    public void act(final Context c) {
        final GameAction a = _inputSource.nextAction(500);
        if(a!=null) {
            a.perform(c);
        }
    }

    public String toString() {
        return "You";
    }

    public String toPack() {
        return "lilting of patsies";
    }

    public static class Left extends DefaultNHBotAction implements Director {
        public void perform() {
            getBot().getEnvironment().turnLeft();
            throw new ActionCancelledException(); // turning does not count
        }

        public Direction getDirection() {
            return getBot().getEnvironment().getFacing().left();
        }

        public String getDescription() {
            return "Turns left.";
        }
    }

    public static class Right extends DefaultNHBotAction implements Director {
        public void perform() {
            getBot().getEnvironment().turnRight();
            throw new ActionCancelledException(); // turning does not count
        }

        public Direction getDirection() {
            return getBot().getEnvironment().getFacing().right();
        }

        public String getDescription() {
            return "Turns right.";
        }
    }

    public static class StrafeLeft extends MoveAction {
        public StrafeLeft() {
            super(false);
        }

        public Direction getDirection() {
            return getBot().getEnvironment().getFacing().left().left();
        }
    }

    public static class StrafeRight extends MoveAction {
        public StrafeRight() {
            super(false);
        }

        public Direction getDirection() {
            return getBot().getEnvironment().getFacing().right().right();
        }
    }

    public abstract static class RepeatMoveAction extends MoveAction {
        public String getDescription() {
            return "Move "+getDirection()+" until something interesting happens.";
        }

        public boolean isRepeat() {
            NHSpace c = getBot().getEnvironment().getMSpace();
            MSpace m = c.move(getDirection());
            int gr = 0;
            if(m!=null) {
                for(MSpace sur:m.cardinal()) {
                    if(sur instanceof Ground) {
                        gr++;
                    }
                }
            }
            return gr<3&&m!=null&&m.getClass()==c.getClass()&&m.isWalkable()&&!m.isOccupied()&&c.numItems()==0;
        }
    }

    public static class Northward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.north;
        }
    }

    public static class Southward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.south;
        }
    }

    public static class Eastward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.east;
        }
    }

    public static class Westward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.west;
        }
    }

    public static class Northeastward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.northeast;
        }
    }

    public static class Northwestward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.northwest;
        }
    }

    public static class Southwestward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.southwest;
        }
    }

    public static class Southeastward extends RepeatMoveAction {
        public Direction getDirection() {
            return Direction.southeast;
        }
    }

    public static class North extends MoveAction implements Director {
        public Direction getDirection() {
            return Direction.north;
        }
    }

    public static class South extends MoveAction implements Director {
        public Direction getDirection() {
            return Direction.south;
        }
    }

    public static class East extends MoveAction implements Director {
        public Direction getDirection() {
            return Direction.east;
        }
    }

    public static class West extends MoveAction implements Director {
        public Direction getDirection() {
            return Direction.west;
        }
    }

    public static class Northeast extends MoveAction {
        public Direction getDirection() {
            return Direction.northeast;
        }
    }

    public static class Northwest extends MoveAction {
        public Direction getDirection() {
            return Direction.northwest;
        }
    }

    public static class Southeast extends MoveAction {
        public Direction getDirection() {
            return Direction.southeast;
        }
    }

    public static class Southwest extends MoveAction {
        public Direction getDirection() {
            return Direction.southwest;
        }
    }

    public static class Rest extends DefaultNHBotAction implements Director, SpaceAction {
        public boolean isPerformable(NHBot b) {
            return true;
        }

        public String getDescription() {
            return "Do nothing.";
        }

        public Direction getDirection() {
            return null;
        }

        public void perform() {
            getBot().afflict(Affliction.Onset.move);
            //getBot().getEnvironment().unhide();
        }
    }

    public static class Look extends DefaultNHBotAction {
        public String getDescription() {
            return "Describe objects in the surrounding area.";
        }

        public void perform() {
            N.narrative().look(getBot(), getBot().getEnvironment().getMSpace());
        }
    }

    public static class Save extends AbstractGameAction {
        public String getDescription() {
            return "Save and quit the game.";
        }

        public void perform() {
            N.narrative().save();
        }

        public String toString() {
            return "Save";
        }
    }

    public static class Exit extends DefaultNHBotAction {
        public String getDescription() {
            return "Abandon the game.";
        }

        public void perform() {
            N.narrative().clear();
            if(N.narrative().confirm(getBot(), "Really quit?")) {
                String disp = ((Patsy)getBot()).getLevel().getDisplayedFloor();
                if(disp.equals("??")) {
                    disp = "";
                }
                else {
                    disp = " on level "+disp;
                }
                String part = ((Patsy)getBot()).getLevel().getName();
                if(part.startsWith("The ")) {
                    part = Character.toLowerCase(part.charAt(0))+part.substring(1);
                }
                //N.narrative().quit("Quit in "+part+disp+".", false);
                N.narrative().quit("Gave up.", false);
            }
            else {
                throw new ActionCancelledException();
            }
        }
    }

    public static class InventoryAction extends AbstractGameAction implements SpaceAction {
        public String getDescription() {
            return "Display inventory.";
        }

        public boolean isPerformable(NHBot b) {
            return true;
        }

        public void perform() {
            N.narrative().showInventory();
            throw new ActionCancelledException();
        }

        public String toString() {
            return "Inventory";
        }
    }

    public static class Skills extends AbstractGameAction {
        public String getDescription() {
            return "Display skills.";
        }

        public void perform() {
            N.narrative().showSkills();
            throw new ActionCancelledException();
        }

        public String toString() {
            return "Skills";
        }
    }

    public static class Ascend extends DefaultNHBotAction implements Director, SpaceAction {
        public String getDescription() {
            return "Go up a staircase.";
        }

        public Direction getDirection() {
            return Direction.up;
        }

        public boolean isPerformable(NHBot b) {
            NHSpace s = b.getEnvironment().getMSpace();
            Climbable cl = null;
            if(s instanceof Climbable) {
                cl = (Climbable) s;
            }
            else {
                for(Parasite p:s.getParasites()) {
                    if(p instanceof Climbable) {
                        cl = (Climbable) p;
                        break;
                    }
                }
            }
            if(cl!=null) {
                return cl.isAscending();
            }
            else {
                return false;
            }
        }

        public void perform() {
            NHSpace s = getBot().getEnvironment().getMSpace();
            Climbable cl = null;
            if(s instanceof Climbable) {
                cl = (Climbable) s;
            }
            else {
                for(Parasite p:s.getParasites()) {
                    if(p instanceof Climbable) {
                        cl = (Climbable) p;
                        break;
                    }
                }
            }
            if(cl!=null) {
                if(cl.isAscending()) {
                    if(getBot().isLevitating()) {
                        N.narrative().print(getBot(), "You float up the "+cl.getName()+".");
                    }
                    else {
                        //N.narrative().print(getBot(), "You ascend.");
                    }
                    getBot().getEnvironment().ascend(cl.findEndpoint(getBot().getEnvironment().getFloor(1+getBot().getEnvironment().getLevel())));
                    LookHere lh = new LookHere();
                    lh.setLootOnly(true);
                    lh.setBot(getBot());
                    lh.perform();
                }
                else {
                    N.narrative().print(getBot(), "You are already at the top of this "+cl.getName()+".");
                    throw new ActionCancelledException();
                }
            }
            else {
                if(Boolean.getBoolean("tower.creator")) {
                    getBot().getEnvironment().ascend(null);
                }
                else {
                    N.narrative().print(getBot(), "There are no stairs here.");
                }
            }
        }
    }

    public static class Descend extends DefaultNHBotAction implements Director, SpaceAction {
        public String getDescription() {
            return "Go down a staircase.";
        }

        public Direction getDirection() {
            return Direction.down;
        }

        public boolean isPerformable(NHBot b) {
            NHSpace s = b.getEnvironment().getMSpace();
            Climbable cl = null;
            if(s instanceof Climbable) {
                cl = (Climbable) s;
            }
            else {
                for(Parasite p:s.getParasites()) {
                    if(p instanceof Climbable) {
                        cl = (Climbable) p;
                        break;
                    }
                }
            }
            if(cl!=null) {
                return cl.isDescending();
            }
            else {
                return false;
            }
        }

        public void perform() {
            NHSpace s = getBot().getEnvironment().getMSpace();
            Climbable cl = null;
            if(s instanceof Climbable) {
                cl = (Climbable) s;
            }
            else {
                for(Parasite p:s.getParasites()) {
                    if(p instanceof Climbable) {
                        cl = (Climbable) p;
                        break;
                    }
                }
            }
            if(cl!=null) {
                if(cl.isDescending()) {
                    /* TODO
                    if(!getBot().allow(this)) {
                        return;
                    }
                    */
                    if(getBot().isLevitating()) {
                        N.narrative().print(getBot(), "You cannot reach the ground.");
                        return;
                    }
                    if(((Patsy)getBot()).getLevel().getFloor()==1) {
                        if(N.narrative().confirm(getBot(), "Exit the Tower?")) {
                            ((Patsy)getBot()).setDeath("Cowardly turned back.");
                            N.narrative().quit("Cowardly turned back.", false);
                        }
                        else {
                            throw new ActionCancelledException();
                        }
                    }
                    //N.narrative().print(getBot(), "You descend.");
                    //getBot().getEnvironment().descend();
                    getBot().getEnvironment().descend(cl.findEndpoint(getBot().getEnvironment().getFloor(getBot().getEnvironment().getLevel()-1)));
                    LookHere lh = new LookHere();
                    lh.setLootOnly(true);
                    lh.setBot(getBot());
                    lh.perform();
                }
                else {
                    N.narrative().print(getBot(), "You are already at the bottom of this "+cl.getName()+".");
                    throw new ActionCancelledException();
                }
            }
            else {
                if(Boolean.getBoolean("tower.creator")) {
                    getBot().getEnvironment().descend(null);
                }
                else {
                    N.narrative().print(getBot(), "There are no stairs here.");
                }
            }
        }
    }

    public static class Previous extends DefaultNHBotAction {
        public String getDescription() {
            return "Display narrative in reverse-chronological order.";
        }

        public void perform() {
            N.narrative().previous();
            throw new ActionCancelledException();
        }
    }

    static public final class Notch implements java.io.Serializable {
        private String _common;
        private int _count;


        public Notch(String common) {
            _common = common;
        }

        public void increment() {
            _count++;
        }

        public int getCount() {
            return _count;
        }

        public String toString() {
            String s = _count+" "+_common;
            return _count>1?s+Grammar.pluralize(s):s;
        }
    }
}
