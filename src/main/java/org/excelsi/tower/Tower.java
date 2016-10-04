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


import java.util.List;
import java.util.ArrayList;
import org.excelsi.aether.*;
import org.excelsi.matrix.*;
import static org.excelsi.tower.TowerLevelGenerator.Partition;
import java.util.logging.Logger;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


public class Tower extends Game implements GameListener {
    public static final int LEVELS = 77;
    //public static final int LEVEL_WIDTH = 80;
    //public static final int LEVEL_HEIGHT = 24;
    public static final int LEVEL_WIDTH = 80*2;
    public static final int LEVEL_HEIGHT = 24*2;
    private static final int DEBUG = -1;

    private List<LParts> _partitions = new ArrayList<LParts>();

    public Tower() {
        Scores.setRecord(!Boolean.getBoolean("tower.creator"));
        addListener(this);
    }

    public void ascended(Game g) {
        greet();
    }

    public void descended(Game g) {
        greet();
    }

    protected void greet() {
        switch(getLevel()) {
            case 0:
                N.narrative().print((NHBot)null, "This place looks very, very old...");
                break;
            case 10:
                N.narrative().print((NHBot)null, "The air here is damp and smells of mildew.");
                break;
            case 19:
                N.narrative().print((NHBot)null, "A cold shiver runs up your spine.");
                break;
            case 58:
                N.narrative().print((NHBot)null, "The ground here looks somewhat unsteady...");
                break;
            case 70:
                N.narrative().print((NHBot)null, "You emerge onto a flat expanse covered in gray dust.");
                break;
            case 902:
                N.narrative().print((NHBot)null, "A forbidding plain of barren rock and dead grass stretches before you.");
                break;
            default:
        }
    }

    protected Level createLevel(int floor) {
        while(_partitions.size()<=1+floor) {
            int current = _partitions.size();
            int below = floor==1?1:_partitions.get(_partitions.size()-1).numAscending();
            int parts;
            ArrayList<Partition> ps = new ArrayList<Partition>();
            if(current==899) {
                ps.add(new Partition(0, 1));
            }
            else if(current>=900) {
                ps.add(new Partition(0, 0));
            }
            else {
                // always have at least one
                ps.add(new Partition(1, 1));
                if(current==24) {
                    ps.add(new Partition(1, 0));
                }
                else if(current==8||current==18||current==25||current==47||current==48||current==49) {
                    //ps.add(new Partition(1, 1));
                }
                else if(current>=26&&current<=28) {
                    ps.add(new Partition(current==28?0:1, 1));
                }
                else if(current==69||current==70||current==76) {
                    //ps.add(new Partition(1, 0));
                }
                else {
                    parts = below;
                    while(ps.size()<parts) {
                        ps.add(new Partition(1, 1));
                    }
                    if(Rand.d100(20)&&parts<3) {
                        ps.add(new Partition(1, 0));
                    }
                    int r = Rand.d100();
                    if(r<20&&parts<4) {
                        ps.get(Rand.om.nextInt(ps.size())).incAsc();
                    }
                    else if(r<40&&parts>1) {
                        if(Rand.d100(60)) {
                            ps.get(ps.size()-1).decAsc();
                        }
                        else {
                            ps.get(0).incDesc();
                            ps.remove(ps.size()-1);
                        }
                    }
                }
            }
            LParts lp = new LParts();
            lp.ps = (Partition[]) ps.toArray(new Partition[ps.size()]);
            _partitions.add(lp);
        }

        for(int times=0;times<100;times++) {
            try {
                if(floor==1) {
                    return createInitial();
                }
                LevelGenerator lg;
                if(floor==DEBUG) {
                    lg = new MinusWorldGenerator(LEVEL_WIDTH, LEVEL_HEIGHT);
                }
                else {
                    LParts parts = _partitions.get(floor);
                    lg = new TowerLevelGenerator(LEVEL_WIDTH, LEVEL_HEIGHT, parts.ps);
                }
                Level l = new Level(LEVEL_WIDTH, LEVEL_HEIGHT);
                l.setFloor(floor);
                if(floor==77) {
                    addMachine(l);
                    l.setName("The Machine");
                }
                else {
                    lg.generate(l, (MatrixMSpace) getPlayer().getEnvironment().getMSpace());
                }
                switch(floor) {
                    /*case 0:
                        N.narrative().print((NHBot)null, "This place looks very, very old...");
                        break;*/
                    case 9:
                        l.findRandomEmptySpace().setOccupant(getFactory().createBot("Roombor"));
                        N.narrative().print((NHBot)null, "You hear a strange whirring sound in the distance...");
                        break;
                    /*case 10:
                        N.narrative().print((NHBot)null, "The air here is damp and smells of mildew.");
                        break;
                    case 19:
                        N.narrative().print((NHBot)null, "A cold shiver runs up your spine.");
                        break;
                    case 58:
                        N.narrative().print((NHBot)null, "The ground here looks somewhat unsteady...");
                        break;
                    case 70:
                        N.narrative().print((NHBot)null, "You emerge onto a flat expanse covered in gray dust.");
                        break;
                    case 902:
                        N.narrative().print((NHBot)null, "A forbidding plain of barren rock and dead grass stretches before you.");
                        break;
                    default:*/
                }
                return l;
            }
            catch(IllegalStateException t) {
                // such as if an irreplaceaable space
                // tries to be replaced
                t.printStackTrace();
                Logger.global.fine(t.toString());
            }
            catch(IllegalArgumentException t) {
                t.printStackTrace();
                Logger.global.fine(t.toString());
            }
            catch(NullPointerException t) {
                t.printStackTrace();
                Logger.global.fine(t.toString());
            }
        }
        throw new Error("tried 100 times to create initial level");
    }

    private Level createInitial() {
        //Basis.print();
        NHBot[] playable = getFactory().getPlayable();
        final int add = 28;
        Level l = new Level(LEVEL_WIDTH, add+LEVEL_HEIGHT);
        l.setFloor(1);
        for(int i=0;i<l.width();i++) {
            l.setSpace(new Blank(false), i, LEVEL_HEIGHT);
        }
        Level.Room r = new Level.Room(l.width()/2, LEVEL_HEIGHT+3, playable.length+1, 4, l.width(), l.height());
        r.setSpecial(true);
        r.setWallClass(IrreplaceableWall.class);
        l.addRoom(r, true);
        l.setSpace(new Floor(), r.centerX(), r.getY2());
        //l.getSpace(r.centerX(), r.getY1()+1).replace(new Basin());
        //l.getSpace(r.centerX()+2, r.getY1()+1).replace(new Shrine());
        //l.getSpace(r.centerX(), r.getY1()+1).replace(new Altar());
        //l.getSpace(r.centerX(), r.getY1()+1).replace(new Tunnel(1, 30));
        //l.getSpace(r.centerX(), r.getY1()+1).setOccupant(getFactory().createBot("black reaver"));
        //l.setSpace(new Basin(false), r.getX2()-1, r.getY1()+1);
        final NHSpace pathSpace = (NHSpace)l.getSpace(r.centerX(), r.getY2());
        final Writing wr = new Writing("Choose a path", 0);
        ((NHSpace)pathSpace.move(Direction.north)).addParasite(wr);
        pathSpace.addMSpaceListener(new MSpaceAdapter() {
            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(from==pathSpace&&from.directionTo(to)!=Direction.south) {
                    NHSpace rep = (NHSpace) pathSpace.replace(new IrreplaceableWall());
                    rep.destroyLoot();
                    //N.narrative().print((NHBot)b, "The entrance closes behind you!");
                }
            }
        });

        l.setSpace(new Stairs(false), r.centerX(), add+LEVEL_HEIGHT-1);
        for(int i=add+LEVEL_HEIGHT-2;i>r.getY2();i--) {
            l.setSpace(new Ground(), r.centerX(), i);
        }

        Patsy player = (Patsy) getFactory().createBot(new BotFactory.Constraints() {
            public boolean accept(NHBot b) {
                return b instanceof Patsy && ((Patsy)b).isHidden();
            }
        });
        player.getInventory().setKeyed(true);
        // do this ahead of time so that magic 8-ball or other methods
        // can't change scoring status
        /*
        player.getInventory().add(new Sensor());
        player.getInventory().add(new Processor());
        Wire wires = new Wire();
        wires.setCount(8);
        player.getInventory().add(wires);
        Actuator actua = new Actuator();
        actua.setCount(8);
        player.getInventory().add(actua);
        ScrapMetal scrap = new ScrapMetal();
        scrap.setCount(8);
        player.getInventory().add(scrap);
        */
        Light light = new Lamp();
        light.setLit(true);
        player.getInventory().add(light);
        if(Boolean.getBoolean("tower.creator")) {
            player.getInventory().add(new Magic8_Ball());
            player.getInventory().add(new WandOfWishing());
            WandOfCreation wc = new WandOfCreation();
            wc.setBionic(true);
            player.getInventory().add(wc);
            //player.getInventory().add(new BookOfSands());
            player.getInventory().add(new DiskOfOdin());
            //player.getInventory().add(new ScrollOfTunneling());
            //player.getInventory().add(new Pick_Axe());
            //player.getInventory().add(new BallOfYarn());
            //player.getInventory().add(new Rock());
            //player.getInventory().add(new LaserWeaponsForLuddites());
            //player.getInventory().add(new ScrollOfEnchantWeapon());
            //player.getInventory().add(new ScrollOfMerging());
            //player.getInventory().add(new Potion(new Levitation()));
            //player.getInventory().add(new Potion(new Confusion()));
            //player.getInventory().add(new Potion(new Invisibility()));
            ScrollOfSummoning sum = new ScrollOfSummoning();
            sum.setCount(10);
            sum.setStatus(Status.blessed);
            player.getInventory().add(sum);
            Dart dart = new Dart();
            dart.setCount(10);
            player.getInventory().add(dart);
            //player.getInventory().add(new Potion(new Tunneling()));
            //ScrollOfMapping su = new ScrollOfMapping();
            //su.setCount(100);
            //player.getInventory().add(su);
            ScrollOfMapping soew = new ScrollOfMapping();
            //soew.setStatus(Status.cursed);
            soew.setCount(30);
            player.getInventory().add(soew);
        }

        player.setName(System.getProperty("tower.name", System.getProperty("user.name")));
        setPlayer(player);
        int top = add+LEVEL_HEIGHT-1;
        l.getSpace(r.centerX(), top).setOccupant(player);
        player.getEnvironment().face(Direction.north);
        while(--top>r.getY2()) {
            player.getEnvironment().forward();
        }
        //l.getSpace(r.centerX(), add+LEVEL_HEIGHT-4).replace(new Hidden(l.getSpace(r.centerX(), add+LEVEL_HEIGHT-4)));

        MSpace s = l.getSpace(r.getX1()+1, r.centerY());
        final ArrayList arr = new ArrayList();
        arr.add("a piece of diamonds and quicksilver moon");
            
        final List<MSpace> spaces = new ArrayList<MSpace>();
        for(NHBot b:playable) {
            final NHBot chosen = b;
            s = s.replace(new Floor() {
                public boolean isAutopickup() {
                    return arr.size()==1;
                }

                public boolean look(final Context c, boolean nothing, boolean lootOnly) {
                    if(isAutopickup()) {
                        N.narrative().print(this, ((Patsy)chosen).getSelectionText());
                        return true;
                    }
                    else {
                        return super.look(c, nothing, lootOnly);
                    }
                }
            });
            spaces.add(s);
            ((NHSpace)s).addLoot(b.getInventory());
            ((NHSpace)s).addContainerListener(new ContainerAdapter() {
                public void itemTaken(Container s, Item item, int idx) {
                    if(s.numItems()==0&&((MSpace)s).isOccupied()) {
                        if(arr.size()==0) {
                            return;
                        }
                        arr.remove(0);
                        for(MSpace m:spaces) {
                            if(m!=s) {
                                ((NHSpace)m).destroyLoot();
                            }
                        }
                        wr.getSpace().removeParasite(wr);
                        NHBot ch = (NHBot) ((NHSpace)s).getOccupant();
                        ch.polymorph(chosen);
                        ch.setSkills(chosen.getSkills());
                        N.narrative().print(ch, Grammar.start(ch, "feel")+" like "+Grammar.nonspecific(ch.getProfession().toLowerCase())+".");
                        for(Item i:ch.getInventory().getItem()) {
                            i.setIdentified(true);
                            i.setClassIdentified(true);
                        }
                        try {
                            if(chosen.getWielded()!=null) {
                                ch.setWielded(chosen.getWielded());
                            }
                            for(Item i:chosen.getWearing()) {
                                ch.wear(i);
                            }
                        }
                        catch(EquipFailedException e) {
                            throw new IllegalStateException("character selection failed: "+e.getMessage(), e);
                        }
                        s.removeContainerListener(this);
                    }
                }

                public void itemDestroyed(Container s, Item item, int idx) {
                    if(idx==0&&arr.size()==0) {
                        s.removeContainerListener(this);
                    }
                }
            });
            s = s.move(Direction.east);
        }
        LevelGenerator lg = new TowerLevelGenerator(LEVEL_WIDTH, LEVEL_HEIGHT);
        lg.generate(l, null);
        for(int j=r.getY1();j<=r.getY2();j++) {
            l.setSpace(new IrreplaceableWall(), r.getX1(), j);
            l.setSpace(new IrreplaceableWall(), r.getX2(), j);
        }
        MSpace stairs = null;
        for(MSpace sp:l.spaces()) {
            if(sp instanceof Stairs && ((Stairs)sp).isAscending()) {
                stairs = sp;
                break;
            }
        }
        List<MatrixMSpace>[] paths = l.criticalPath(l.getSpace(r.getX1()+1, r.centerY()),
                (MatrixMSpace)stairs, false, new Filter() {
                    public boolean accept(MSpace s) {
                        return s!=null&&(s instanceof Doorway||s.isWalkable()||
                            (s instanceof Hidden && (((Hidden)s).getRevealed() instanceof Doorway
                                                     || ((Hidden)s).getRevealed().isWalkable())));
                        }
                    }, 1, 1f);
        if(paths[0]==null) {
            throw new IllegalStateException("can't complete floor");
        }

        //l.findRandomEmptySpace().setOccupant(getFactory().createBot("knight of Cydonia"));
        return l;
    }

    private void addMachine(final Level l) {
        final int w = l.width()/2;
        final int h = l.height()/2;
        l.addRoom(new Level.Room(w, h, 16, 2, l.width(), l.height()), true);
        l.addRoom(new Level.Room(w-1, h-4, 2, 2, l.width(), l.height()), true);
        l.addRoom(new Level.Room(w+1, h-4, 2, 2, l.width(), l.height()), true);
        l.addRoom(new Level.Room(w, h, 8, 8, l.width(), l.height()), true);
        l.addRoom(new Level.Room(w, h+5, 8, 2, l.width(), l.height()), true);
        l.setSpace(new Stairs(false), w+3, h+5);
        l.setSpace(new Doorway(true, false), w, h+4);
        l.setSpace(new Floor(), w+4, h);
        l.setSpace(new Floor(), w-4, h);
        l.setSpace(new Floor(), w-1, h-4);
        l.setSpace(new Floor(), w+1, h-4);
        final Electromagnet e = new Electromagnet();
        l.setSpace(e, w-7, h);
        l.setSpace(new Electromagnet(e), w+7, h);
        l.setSpace(new Altar(), w, h);
        //l.setSpace(new Exit(), w, h);

        l.setSpace(new Switch("EMF", "tarnished", new Switch.Toggle() {
            public void toggle(boolean on) {
                e.setActive(on);
            }
        }), w-1, h-4);

        l.setSpace(new Switch("Higgs Embigulator", "worn", new Switch.Toggle() {
            public void toggle(boolean on) {
                NHSpace m = l.getSpace(w, h);
                if(on) {
                    //m.addParasite(new Higgs());
                    for(MSpace sp:l.spaces()) {
                        if(sp!=null) {
                            ((NHSpace)sp).addParasite(new Higgs());
                        }
                    }
                    for(Item i:m.getItem()) {
                        if(i instanceof DiskOfOdin) {
                            //m.destroy(i);
                            m.destroyLoot();
                            Level end = getFloor(1000);
                            LongTunnel e = (LongTunnel) end.getSpace(end.width()/2, end.height()-5).replace(new LongTunnel(end.getFloor(), l.getFloor()));
                            m.replace(new LongTunnel(l.getFloor(), end.getFloor(), e));
                        }
                    }
                }
                else {
                    for(MSpace sp:l.spaces()) {
                        if(sp!=null) {
                            for(Parasite p:((NHSpace)sp).getParasites()) {
                                if(p instanceof Higgs) {
                                    ((NHSpace)sp).removeParasite(p);
                                }
                            }
                        }
                    }
                }
            }
        }), w+1, h-4);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Variegated.writeStatic(out);
        out.writeBoolean(Boolean.getBoolean("tower.creator"));

        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Variegated.readStatic(in);
        boolean creator = in.readBoolean();
        if(Boolean.getBoolean("tower.creator")!=creator) {
            throw new Error("value of tower.creator has changed since save");
        }

        in.defaultReadObject();
    }

    private static class Higgs extends Parasite {
        public void trigger(NHBot b) {
        }

        public boolean notice(NHBot b) {
            return true;
        }

        public void attacked(Armament a) {
        }

        public void update() {
        }

        public String getModel() { return "-"; }
        public String getColor() { return "translucent"; }
        public int getHeight() { return 0; }
        public boolean isMoveable() { return false; }
    }

    private static class LParts implements java.io.Serializable {
        public Partition[] ps;

        public int numParts() {
            return ps.length;
        }

        public int numAscending() {
            int n = 0;
            for(Partition p:ps) {
                n += p.ascending();
            }
            return n;
        }

        public int numDescending() {
            int n = 0;
            for(Partition p:ps) {
                n += p.descending();
            }
            return n;
        }
    }
}
