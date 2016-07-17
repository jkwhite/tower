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
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import static org.excelsi.tower.Shops.Building;


public class TowerLevelGenerator implements LevelGenerator, LevelGenerator.RoomModulator {
    private int _grass = 1;
    private Level _level;
    private int _width;
    private int _height;
    //private int _parts;
    private Partition[] _lparts;
    //private boolean _addStairs = true;
    private MatrixMSpace _player;
    private boolean[] _addAscendingStairs;
    private boolean[] _addDescendingStairs;
    private int _spacing = 1;
    private int _maxCells = 10;
    private boolean _mixin = true;
    private boolean _allowWallOverlap = false;
    private boolean _drawPassageways = true;
    private Map<Integer,Pattern[]> _patterns = new HashMap<Integer,Pattern[]>();
    private Pattern _town;

    public TowerLevelGenerator(int maxWidth, int maxHeight) {
        this(maxWidth, maxHeight, new Partition(1,1));
    }

    public TowerLevelGenerator(int maxWidth, int maxHeight, Partition... parts) {
        _width = maxWidth;
        _height = maxHeight;
        _lparts = parts;

        _patterns.put(1, new Pattern[]{
            new Pattern(
                new Layout(new int[]{0,0,_width,_height}))
        });

        _patterns.put(2, new Pattern[]{
            new Pattern(
                new Layout(new int[]{0,0,_width/2,_height}),
                new Layout(new int[]{_width/2+1,0,_width,_height})),
            new Pattern(
                new Layout(new int[]{_width/4+1,_height/4+1,3*_width/4,3*_height/4}),
                new Layout(new int[]{0,0,3*_width/4,_height/4},
                           new int[]{0,_height/4+1, _width/4, _height},
                           new int[]{_width/4+1,3*_height/4+1,_width,_height},
                           new int[]{3*_width/4+1,0,_width,3*_height/4}))
        });

        _patterns.put(3, new Pattern[]{
            new Pattern(
                new Layout(new int[]{0,0,_width/3,_height}),
                new Layout(new int[]{_width/3+1,0,2*_width/3,_height}),
                new Layout(new int[]{2*_width/3+1,0,_width,_height})),
            new Pattern(
                new Layout(new int[]{_width/4+1,_height/4+1,3*_width/4,3*_height/4}),
                new Layout(new int[]{0,0,_width/2,_height/4},
                           new int[]{0,_height/4+1,_width/4,3*_height/4},
                           new int[]{0,3*_height/4+1,_width/2,_height}),
                new Layout(new int[]{_width/2+1,0,_width,_height/4},
                           new int[]{3*_width/4+1,_height/4+1,_width,3*_height/4},
                           new int[]{_width/2+1,3*_height/4+1,_width,_height}))
        });

        _patterns.put(4, new Pattern[]{
            new Pattern(
                new Layout(new int[]{0,0,_width/2,_height/2}),
                new Layout(new int[]{_width/2+1,0,_width,_height/2}),
                new Layout(new int[]{0,_height/2+1,_width/2,_height}),
                new Layout(new int[]{_width/2+1,_height/2+1,_width,_height}))
        });

        /*
        _town =
            new Pattern(
                new Layout(new int[]{_width/4+2,_height/4+2,3*_width/4-2,3*_height/4-2}),
                new Layout(new int[]{10,0,_width-10,_height/4},
                           new int[]{10,3*_height/4+1, _width-10, _height})
                );
                */
        _town =
            new Pattern(
                new Layout(new int[]{10,4,_width-10,_height-4}));
    }

    public int width() {
        return _width;
    }

    public int height() {
        return _height;
    }

    public float getRoomSanity(Level level) {
        //return 1f;
        //return Math.max(0.2f, Rand.om.nextFloat());
        if(level.getFloor()==25) {
            return 0;
        }
        else {
            return Math.max(0.2f, 1f-level.getFloor()%10/10f);
        }
    }

    public int getPassagewaySanity(Level level) {
        return Math.max(2, 8-(level.getFloor()%10)/2);
    }

    public void setAddAscendingStairs(boolean... s) {
        _addAscendingStairs = s;
    }

    public void setAddDescendingStairs(boolean... s) {
        _addDescendingStairs = s;
    }

    public void generate(final Level level, MatrixMSpace player) {
        _level = level;
        int f = level.getFloor();
        _level.setPartitions(_lparts.length);
        _player = player;
        boolean follow = true;
        Shops.setAmount(1);
        Shops.setChance(50);
        Shops.setAmount(1);
        Shops.setChance(101);
        Shops.setShuffle(true);
        Copses.setChance(10);
        _spacing = 1;
        _mixin = true;
        _maxCells = 400;
        _allowWallOverlap = false;
        _drawPassageways = true;
        WaterMixin.setChance(8);
        WaterMixin.setSurround(false);
        WaterMixin.setAmount(Rand.om.nextInt(2)+1);
        WaterMixin.setPassageways(false);
        WaterMixin.setWholeRoomChance(67);
        WaterMixin.setSurroundChance(20);
        Pits.setInitialChance(50);
        Pits.setSubsequentChance(25);
        Coinage.setChance(30);
        BotMixin.setCoefficient(1);
        Fabricators.setChance(2);
        Secrets.setGroundChance(1);
        Secrets.setDoorChance(10);
        Vines.setInitialChance(10);
        Farming.setChance(10);
        _grass = 1;
        if(f<10) {
            WaterMixin.setChance(30);
            _grass = 5;
            if(f==9) {
                //N.narrative().print((NHBot)null, "You hear a strange whirring sound in the distance...");
            }
            if(f==0) {
                level.setName("Terran Subterraine");
            }
            else if(f==1) {
                level.setName("Base of the Tower");
            }
            else {
                level.setName("The Lower Reaches");
            }
            if(f>1) {
                level.setLight(Rand.d100(10)?0.1f:Rand.d100(30)?Math.min(.5f,Rand.om.nextFloat()):1.0f);
            }
            else if(f==0) {
                level.setLight(0f);
            }
            else {
                level.setLight(1f);
            }
            //level.setLight(0.2f);
            //level.setLight(0f);
        }
        else if(f<20) {
            // water
            level.setLight(0f);
            Farming.setChance(0);
            if(f!=15) {
                // break in middle
                WaterMixin.setChance(101);
                WaterMixin.setAmount(100);
                WaterMixin.setSurround(true);
                WaterMixin.setPassageways(Rand.d100(50));
                level.setName("The Old Waterway");
            }
            else {
                level.setName("Center Ground");
            }
            if(f==10) {
                //N.narrative().print((NHBot)null, "The air here is damp and smells of mildew.");
            }
            if(f==19) {
                //N.narrative().print((NHBot)null, "A cold shiver runs up your spine.");
                level.setName("Beset by Creatures of the Deep");
                WaterMixin.setPassageways(false);
                WaterMixin.setSurroundChance(80);
                WaterMixin.setWholeRoomChance(101);
                BotMixin.setCoefficient(2f);
            }
        }
        else if(f<30) {
            // battles, weapons, lightning
            level.setLight(1f);
            Farming.setChance(0);
            BotMixin.setCoefficient((level.getFloor()%10)/2+1);
            Vines.setInitialChance(30);
            Copses.setChance(0);
            level.setName("The Disputed Zone");
            if(f==25) {
                level.setName("The Town");
                follow = false;
                _spacing = 0;
                _maxCells = 50;
                _mixin = false;
                _allowWallOverlap = true;
                _drawPassageways = false;
                _grass = 0;
                runParts();
                generateTown(level, "guard");
            }
            else {
                //runParts();
                //generateBattlefield(level);
                //Pits.setInitialChance(90);
                //Pits.setSubsequentChance(75);
                //mixin(level);
                //follow = false;
            }
        }
        else if(f==30) {
            level.setLight(0f);
            WaterMixin.setChance(0);
            Farming.setChance(0);
            level.setLight(0f);
            addLab(level);
            level.setName("The Lab");
            BotMixin.setCoefficient(0.0f);
            Secrets.setGroundChance(0);
            Secrets.setDoorChance(0);
            mixin(level);
            follow = false;
        }
        else if(f<40) {
            // magic, fire
            level.setLight(0.5f);
            if(f<36) {
                level.setName("The Upper Reaches");
                Coinage.setChance(95);
            }
            else {
                level.setName("The Ionosphere");
            }
            level.setLight(Rand.d100(10)?0.0f:1.0f);
        }
        else if(f<48) {
            // $, grass
            _grass = 50;
            level.setLight(1f);
            Coinage.setChance(70);
            WaterMixin.setChance(25);
            Vines.setInitialChance(20);
            Farming.setChance(30);
            level.setName("The Shifting Planes");
        }
        else if(f==48) {
            level.setName("The Gate");
            level.setLight(1f);
            follow = false;
            int wid = 15;
            float dec = 2f;
            for(int j=level.height()/2;j<level.height();j++) {
                for(int i=level.width()/2-wid/2;i<level.width()/2+wid/2;i++) {
                    level.setSpace(new Floor(), i, j);
                    Grass g = new Grass();
                    g.setColor("light-brown");
                    level.setSpace(g, i, level.height()-1-j);
                }
                if(j>=level.height()-4) {
                    wid = Math.max(2, wid - (int) dec);
                    dec *= 2f;
                }
            }
            level.setSpace(new Stairs(false), level.width()/2, level.height()-1);
            level.setSpace(new Stairs(true), level.width()/2, 0);
            for(int i=0;i<level.width();i++) {
                if(i!=level.width()/2) {
                    level.setSpace(new IrreplaceableWall(), i, level.height()/2);
                }
                else {
                    Gate g = new Gate();
                    g.setOpen(false);
                    g.setLocked(true);
                    level.setSpace(g, i, level.height()/2);
                }
            }
            String p = Universe.getUniverse().getGame().getPlayer().getProfession();
            String nemesis = null;
            String nemcom = null;
            String sidekick = null;
            if("Priest".equals(p)) {
                nemesis = "Longinus";
            }
            else if("Barbarian".equals(p)) {
                List<NHBot> army = new ArrayList<NHBot>(64);
                for(int i=0;i<level.width();i++) {
                    for(int j=0;j<level.height();j++) {
                        MSpace s = level.getSpace(i,j);
                        if(s!=null&&s.getClass()==Floor.class) {
                            level.getSpace(i,j).setOccupant(Universe.getUniverse().createBot(new BotFactory.Constraints() {
                                public boolean accept(NHBot b) {
                                    return Universe.getUniverse().getGame().getPlayer().threat(b)==Threat.kos&&b.getMinLevel()>19&&b.getMaxLevel()<50;
                                }
                            }));
                            army.add(level.getSpace(i,j).getOccupant());
                        }
                    }
                }
                for(int i=0;i<army.size();i++) {
                    for(int j=0;j<army.size();j++) {
                        if(i==j) {
                            continue;
                        }
                        army.get(i).setThreat(army.get(j), Threat.friendly);
                        army.get(j).setThreat(army.get(i), Threat.friendly);
                    }
                }
            }
            else if("Ranger".equals(p)) {
                for(int i=level.width()/2-3;i<level.width()/2+3;i++) {
                    for(int j=level.height()/2+1;j<=level.height()/2+2;j++) {
                        level.getSpace(i,j).setOccupant(Universe.getUniverse().createBot("urbanite"));
                    }
                }
            }
            else if("Tinkerer".equals(p)) {
                nemcom = "warmech";
            }
            else if("Alchemist".equals(p)) {
                nemesis = "Monster";
            }
            else if("Farmer".equals(p)) {
                nemcom = "corporate farmer";
                sidekick = "corporate lawyer";
            }
            final String nem = nemesis;
            final String nemc = nemcom;
            if(nem!=null||nemcom!=null) {
                level.getSpace(level.width()/2,level.height()/2+1).setOccupant(Universe.getUniverse().createBot(new BotFactory.Constraints() {
                    public boolean accept(NHBot b) {
                        return nem!=null?nem.equals(b.getName()):nemc.equals(b.getCommon());
                    }
                }));
            }
            if(sidekick!=null) {
                final String side = sidekick;
                BotFactory.Constraints bc = new BotFactory.Constraints() {
                    public boolean accept(NHBot b) {
                        return side.equals(b.getCommon());
                    }
                };
                level.getSpace(level.width()/2-1,level.height()/2+1).setOccupant(Universe.getUniverse().createBot(bc));
                level.getSpace(level.width()/2+1,level.height()/2+1).setOccupant(Universe.getUniverse().createBot(bc));
            }
        }
        else if(f==49) {
            level.setLight(0.3f);
            level.setName("The Old Town");
            follow = false;
            _spacing = 0;
            _maxCells = 50;
            _mixin = false;
            _allowWallOverlap = true;
            _drawPassageways = false;
            runParts();
            WaterMixin.setChance(101);
            WaterMixin.setAmount(1);
            new WaterMixin().mix(level);
            for(int i=0;i<level.width();i++) {
                for(int j=0;j<level.height();j++) {
                    NHSpace m = (NHSpace) level.getSpace(i,j);
                    if(m instanceof Wall && m.isReplaceable()) {
                        if(Rand.d100(8)) {
                            m.replace(new Ground());
                        }
                    }
                }
            }
            generateTown(level, "troglodyte");
            for(int i=0;i<level.width();i++) {
                for(int j=0;j<level.height();j++) {
                    NHSpace m = (NHSpace) level.getSpace(i,j);
                    if(m!=null && m.getClass()==Floor.class) {
                        if(!m.isSpecial() && m.getParasites().size()==0) {
                            if(Rand.d100(60)) {
                                m.replace(new Grass());
                            }
                            else if(Rand.d100(10)) {
                                Item it = Universe.getUniverse().createItem(new ItemFilter() { public boolean accept(Item i, NHBot b) { return true; } }, false);
                                it.setStatus(Status.cursed);
                                m.add(it);
                            }
                        }
                    }
                }
            }
        }
        else {
            // arcane
            WaterMixin.setAmount(1);
            WaterMixin.setChance(101);
            Coinage.setChance(70);
            Farming.setChance(0);
            level.setLight(1f);
            if(f==55) {
                Fabricators.setChance(2);
            }
            if(f<=57) {
                level.setName("Van Allen's Land");
            }
            else if(f<65) {
                level.setName("The Great Void");
            }
            else if(f<70) {
                level.setName("Approaching Mare Tranquilitatis");
                Secrets.setGroundChance(0);
                Secrets.setDoorChance(0);
            }
            else if(f==70) {
                level.setName("Mare Tranquilitatis");
                level.setName("The Lunar Surface");
                level.setLight(1f);
                int mx = level.width()/2, my = level.height()/2;
                for(int i=0;i<level.width();i++) {
                    for(int j=0;j<level.height();j++) {
                        if(Math.hypot((mx-i)/2, my-j)<my) {
                            if(Rand.d100(50)) {
                                level.setSpace(new Floor(), i, j);
                            }
                            else {
                                level.setSpace(new Ground() {
                                    public boolean isTransparent() { return true; }
                                }, i, j);
                            }
                        }
                    }
                }
                MSpace m = level.findRandomEmptySpace();
                m.replace(new Stairs(true));
                m = level.findRandomEmptySpace();
                m.replace(new Stairs(false));
                follow = false;
                //new BotMixin(9).mix(level);
                //new Items(9).mix(level);
                //new BotMixin(7).mix(level);
                //N.narrative().print((NHBot)null, "The ground here is covered in a gray dust.");
            }
            else if(f<77) {
                Secrets.setGroundChance(0);
                Secrets.setDoorChance(0);
                level.setName("The Lunar Subterraine");
                level.setLight(0.0f);
            }
            else if(f==77) {
                Secrets.setGroundChance(0);
                Secrets.setDoorChance(0);
                level.setName("The Machine");
                level.setLight(1f);
            }
            else if(f<900) {
                Farming.setChance(0);
                level.setName("Inside The Machine");
                level.setLight(Rand.om.nextFloat());
            }
            else {
                follow = false;
                // special
                level.setDisplayedFloor("??");
                setAddAscendingStairs(false);
                setAddDescendingStairs(false);
                switch(f) {
                    case 900:
                        level.setName("The Plane of Fire");
                        runParts();
                        for(int i=0;i<level.width();i++) {
                            for(int j=0;j<level.height();j++) {
                                if((level.getSpace(i,j)!=null&&Rand.d100(50))||level.getSpace(i,j) instanceof Water||Rand.d100(8)) {
                                    level.setSpace(new Magma(8, 0f), i, j);
                                }
                                else if(level.getSpace(i,j) instanceof Ground) {
                                    level.setSpace(new Ground() {
                                        public String getColor() { return "dark-gray"; }
                                        public boolean isTransparent() { return true; }
                                    }, i, j);
                                }
                            }
                        }
                        for(int k=0;k<1;k++) {
                            for(int i=1;i<level.width()-1;i++) {
                                for(int j=1;j<level.height()-1;j++) {
                                    int t = 0;
                                    for(int x=i-1;x<=i+1;x++) {
                                        for(int y=j-1;y<=j+1;y++) {
                                            if(x==i&&y==j) {
                                                continue;
                                            }
                                            MSpace ms = level.getSpace(x,y);
                                            if(ms instanceof Magma) {
                                                Magma ma = (Magma) ms;
                                                 if(ma.getDepth()>4) {
                                                     t += 2;
                                                 }
                                                 else {
                                                    t++;
                                                 }
                                            }
                                        }
                                    }
                                    if(t>5&&t<12) {
                                        level.setSpace(new Magma(t/2, 0f), i, j);
                                    }
                                }
                            }
                        }
                        for(int i=1;i<level.width()-1;i++) {
                            for(int j=1;j<level.height()-1;j++) {
                                if(level.getSpace(i,j) instanceof Magma) {
                                    int t = 0;
                                    for(int x=i-1;x<=i+1;x++) {
                                        for(int y=j-1;y<=j+1;y++) {
                                            if(x==i&&y==j) {
                                                continue;
                                            }
                                            MSpace ms = level.getSpace(x,y);
                                            if(ms instanceof Magma) {
                                                t++;
                                            }
                                        }
                                    }
                                    ((Magma)level.getSpace(i,j)).setDepth(t>6?8:4);
                                }
                            }
                        }
                        new BotMixin(9).mix(level);
                        new Items(9).mix(level);
                        break;
                    case 901:
                        level.setName("The Aethereal Void");
                        for(int i=0;i<level.width();i++) {
                            for(int j=0;j<level.height();j++) {
                                level.setSpace(new Floor() {
                                    public String getModel() { return " "; }
                                }, i, j);
                            }
                        }
                        new BotMixin(14).mix(level);
                        new Items(4).mix(level);
                        break;
                    case 902:
                        level.setName("The Spent Fields");
                        int mx = level.width()/2, my = level.height()/2;
                        for(int i=0;i<level.width();i++) {
                            for(int j=0;j<level.height();j++) {
                                if(Math.hypot((mx-i)/2, my-j)<my) {
                                    if(Rand.d100(33)) {
                                        if(Rand.d100(50)) {
                                            level.setSpace(new Floor(), i, j);
                                        }
                                        else {
                                            level.setSpace(new Ground() {
                                                public boolean isTransparent() { return true; }
                                            }, i, j);
                                        }
                                    }
                                    else {
                                        level.setSpace(new Grass() {
                                            public String getColor() { return Rand.om.nextBoolean()?"brown":"light-brown"; }
                                        }, i, j);
                                    }
                                }
                            }
                        }
                        new BotMixin(7).mix(level);
                        break;
                    case 903:
                        level.setName("The Plane of Despair");
                        //runParts();
                        //for(int i=0;i<level.width();i++) {
                            //for(int j=0;j<level.height();j++) {
                                //MSpace m = level.getSpace(i,j);
                                //if(m instanceof Floor) {
                                    //if(m.move(Direction.north) instanceof Floor
                                        //&& m.move(Direction.north).move(Direction.north) instanceof Floor) {
                                        //m.replace(new Wall());
                                    //}
                                    //if(m.move(Direction.west) instanceof Floor
                                        //&& m.move(Direction.west).move(Direction.west) instanceof Floor) {
                                        //m.replace(new Wall());
                                    //}
                                //}
                            //}
                        //}
                        for(int i=0;i<level.width()-1;i++) {
                            for(int j=0;j<level.height();j+=3) {
                                level.setSpace(new Wall(), i, j);
                            }
                        }
                        for(int i=0;i<level.width();i+=3) {
                            for(int j=0;j<level.height()-2;j++) {
                                level.setSpace(new Wall(), i, j);
                            }
                        }
                        for(int i=0;i<level.width()-1;i++) {
                            for(int j=0;j<level.height()-2;j++) {
                                if(level.getSpace(i,j)==null) {
                                    level.setSpace(new Water(4, Status.cursed), i, j);
                                }
                            }
                        }
                        MSpace[] path = level.getSpace(1,1).path(level.getSpace(level.width()-3, level.height()-4),
                            false, new Filter() {
                                public boolean accept(MSpace s) {
                                    MatrixMSpace sp = (MatrixMSpace) s;
                                    if(sp.getI()<0||sp.getJ()<0||sp.getI()>level.width()-1||sp.getJ()>level.height()-3) {
                                        return false;
                                    }
                                    for(MSpace m:sp.cardinal()) {
                                        if(!(m instanceof Wall)) {
                                            return true;
                                        }
                                    }
                                    return false;
                                    //return sp instanceof NullMatrixMSpace
                                        //|| MatrixMSpace.this.getClass().isAssignableFrom(sp.getClass());
                                }
                            }, 0.8f);
                        for(MSpace m:path) {
                            if(m instanceof Wall) {
                                Wall w = (Wall) m;
                                TDoorway td = new TDoorway();
                                td.setVertical(w.getModel().equals("-"));
                                m.replace(td);
                            }
                        }
                        new BotMixin(20).mix(level);
                        break;
                    case -888:
                        level.setName("The Labyrinth");
                        int x = 0;
                        int y = 0;
                        while(y<level.height()/2) {
                            for(int i=x;i<level.width()-x;i++) {
                                level.setSpace(new IrreplaceableWall(), i, y);
                                level.setSpace(new IrreplaceableWall(), i, level.height()-y-1);
                            }
                            for(int j=y;j<level.height()-y;j++) {
                                level.setSpace(new IrreplaceableWall(), x, j);
                                level.setSpace(new IrreplaceableWall(), level.width()-x-1, j);
                            }
                            y += 2;
                            x += 4;
                        }
                        int top = 1;
                        for(int i=2;i<level.width()/2&&top<level.height()/2;i+=4) {
                            boolean top1 = Rand.om.nextBoolean();
                            boolean top2 = Rand.om.nextBoolean();
                            int min = top1?top-1:top+1;
                            int max = top1?level.height()-top-1:level.height()-top;
                            for(int j=min+1;j<max;j++) {
                                level.setSpace(new IrreplaceableWall(), i, j);
                            }
                            level.setSpace(new IrreplaceableWall(), i+1, top1?top+1:level.height()-top);
                            min = top2?top:top+1;
                            max = top2?level.height()-top-1:level.height()-top;
                            for(int j=min;j<max;j++) {
                                level.setSpace(new IrreplaceableWall(), level.width()-i-1, j);
                            }
                            level.setSpace(new IrreplaceableWall(), level.width()-i-1, top2?top:level.height()-top);
                            top += 2;
                        }
                        for(int i=0;i<level.width();i++) {
                            for(int j=0;j<level.height();j++) {
                                if(level.getSpace(i,j)==null) {
                                    level.setSpace(new Floor(), i, j);
                                }
                            }
                        }
                        //int push = 4;
                        //for(int k=0;k<3;k++) {
                            //int min = 0;
                            //int max = level.height()-1;
                            //int x = max*k;
                            //while(min<max-1) {
                                //for(int i=min;i<=max;i++) {
                                    //level.setSpace(new IrreplaceableWall(), push+x+i, min);
                                    //level.setSpace(new IrreplaceableWall(), push+x+i, max);
                                    //level.setSpace(new IrreplaceableWall(), push+x+min, i);
                                    //level.setSpace(new IrreplaceableWall(), push+x+max, i);
                                //}
                                //min+=2;
                                //max-=2;
                            //}
                            //max = level.height()-1;
                            //for(int i=0;i<max;i++) {
                                //for(int j=0;j<level.height();j++) {
                                    //if(level.getSpace(push+x+i,j)==null) {
                                        //level.setSpace(new Floor(), push+x+i, j);
                                    //}
                                //}
                            //}
                        //}
                        break;
                    case 1000:
                        level.setName("The End of the Tower");
                        final int w = level.width(), h = level.height();

                        Level.Room r = new Level.Room(w/2, h-5, 4, 6, w, h);
                        r.setDoors(false);
                        r.setWalled(false);
                        r.setFloorClass(Grass.class);
                        level.addRoom(r, true);
                        r = new Level.Room(w/2, 6, 10, 5, w, h);
                        r.setDoors(false);
                        r.setWalled(false);
                        r.setFloorClass(Sakura.class);
                        level.addRoom(r, true);
                        for(int j=4;j<h-4;j++) {
                            if(level.getSpace(w/2, j)==null) {
                                level.setSpace(new Grass(), w/2, j);
                            }
                        }
                        WandOfCreation wand = new WandOfCreation();
                        wand.setBionic(true);
                        NHSpace sp = (NHSpace) level.getSpace(w/2, 6);
                        sp.add(wand);
                        sp.add(new ScrapOfPaper(
                            "--ust cultivate our garden.' may be the best possible answer. My path is chosen. "+
                            "There is no why, thus I shall create one."
                        ));

                        level.getEventSource().addContainerListener(new ContainerAdapter() {
                            public void itemTaken(Container space, Item item, int idx) {
                                if(item instanceof WandOfCreation) {
                                    NHBot ch = ((NHSpace)space).getOccupant();
                                    if(ch.isPlayer()) {
                                        ch.setProfession("Creator");
                                        N.narrative().print(ch, "A wave of primordial awareness washes over you, then is gone.");
                                        N.narrative().more();
                                    }
                                }
                            }
                        });
                        break;
                    default:
                        level.setName("The Last Planes");
                        List<Class> structs = new ArrayList<Class>();
                        for(Class c:Universe.getUniverse().getStructures()) {
                            if(MatrixMSpace.class.isAssignableFrom(c)) {
                                structs.add(c);
                            }
                        }
                        for(int i=0;i<level.width();i++) {
                            for(int j=0;j<level.height();j++) {
                                if(Rand.d100(80)) {
                                    try {
                                        level.setSpace((MatrixMSpace)structs.get(Rand.om.nextInt(structs.size())).newInstance(), i, j);
                                    }
                                    catch(Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            }
                        }
                        new BotMixin(42).mix(level);
                        new Items(111).mix(level);
                        new Traps().mix(level);
                        new Coinage().mix(level);
                        break;
                }
            }
        }
        if(follow) {
            runParts();
        }

        if(level.getName().equals("Van Allen's Land")) {
            for(int i=0;i<level.width();i++) {
                for(int j=0;j<level.height();j++) {
                    NHSpace s = level.getSpace(i,j);
                    if(s!=null) {
                        if(s.getClass()==Ground.class) {
                            s = (NHSpace) s.replace(new Floor());
                        }
                        s.setColor(Rand.om.nextBoolean()?"magenta":"purple");
                    }
                }
            }
            float x, y;
            float dx, dy, d2x, d2y;
            switch(Rand.om.nextInt(4)) {
                case 0:
                    x=0; y=level.height()-4;
                    dx=1; d2x=0;
                    dy=-1; d2y=0.027f;
                    break;
                case 1:
                    x=0; y=4;
                    dx=1; d2x=0;
                    dy=1; d2y=-0.027f;
                    break;
                default:
                case 2:
                    x=20; y=0;
                    dx=-0.6f; d2x=0.015f;
                    dy=0.15f; d2y=0f;
                    break;
            }
            for(;;) {
                if(x<0&&x>=level.width()||y<0||y>=level.height()) {
                    break;
                }
                int ix = (int) x, iy = (int) y;
                for(int i=ix-1;i<=ix+1;i++) {
                    for(int j=iy-1;j<=iy+1;j++) {
                        if(i<0||i>=level.width()||j<0||j>=level.height()) {
                            continue;
                        }
                        MSpace ms = level.getSpace(i,j);
                        if(ms==null||(ms instanceof Unsteady && !ms.isWalkable())) {
                            Unsteady u = new Unsteady(!(i==ix&&j==iy));
                            if(i==ix&&j==iy) {
                                u.setColor("magenta");
                            }
                            else {
                                u.setColor("purple");
                            }
                            level.setSpace(u, i, j);
                        }
                    }
                }
                x += dx;
                y += dy;
                dx += d2x;
                dy += d2y;
            }
        }
        else if(level.getName().equals("The Upper Reaches")||level.getName().equals("The Great Void")) {
            boolean tgv = level.getName().equals("The Great Void");
            float[][] depths = null;
            if(tgv) {
                depths = new float[level.width()][level.height()];
                int tot = Rand.om.nextInt(3)+2;
                for(int i=0;i<tot;i++) {
                    int cx = Rand.om.nextInt(level.width());
                    int cy = Rand.om.nextInt(level.height());
                    int rad = Rand.om.nextInt(15)+5;
                    for(int q=0;q<level.width();q++) {
                        for(int w=0;w<level.height();w++) {
                            float adj = rad - (float)Math.hypot(Math.abs(cx-q), Math.abs(2*(cy-w)));
                            if(adj>0) {
                                adj *= 2f;
                            }
                            //if(adj>0) {
                                depths[q][w] += adj;
                            //}
                        }
                    }
                }
            }
            for(int i=0;i<level.width();i++) {
                for(int j=0;j<level.height();j++) {
                    MSpace m = level.getSpace(i,j);
                    if(m==null&&i>0&&j>0&&i<level.width()-1&&j<level.height()-1) {
                        if(Rand.d100(30)) {
                            if(tgv&&Rand.d100(10)) {
                                //level.setSpace(new Unsteady(true), i, j);                              
                                continue;
                            }
                            //f.setDepth((int)depths[i][j]);
                            level.setSpace(new Floor(), i, j);
                        }
                    }
                    else if(m instanceof Ground) {
                        if(tgv) {
                            //m.replace(new Unsteady());
                            Unsteady u = new Unsteady();
                            //u.setDepth(i/10);
                            //u.setDepth((int)depths[i][j]);
                            m.replace(u);
                        }
                        else {
                            m.replace(new Floor());
                        }
                    }
                    else if(m instanceof Wall&&!(m instanceof ShopWall)) {
                        if(Rand.d100(30)) {
                            m.replace(new Floor());
                        }
                    }
                    else if(m instanceof Floor && !(m instanceof Liquid) && tgv) {
                        //m.replace(new Unsteady());
                        Unsteady u = new Unsteady();
                        //u.setDepth(i/10);
                        //u.setDepth((int)depths[i][j]);
                        m.replace(u);
                    }
                    if(tgv) {
                        NHSpace rep = (NHSpace) level.getSpace(i,j);
                        if(rep!=null) {
                            // algorithm will usually result in terrain
                            // above alt 0, so lower by 10 as a heuristic
                            int alt = -(int)depths[i][j]-10;
                            rep.setAltitude(alt);
                            //System.err.println("SET ALT "+alt+" ("+rep.getAltitude()+")");
                        }
                    }
                }
            }
            //System.err.println("DONE ALTITUDE");
        }
        else if(level.getName().equals("The Shifting Planes")) {
            for(int x=0;x<level.width();x++) {
                for(int y=0;y<level.height();y++) {
                    MSpace m = level.getSpace(x,y);
                    if(m instanceof Ground) {
                        level.setSpace(new Grass(), x, y);
                    }
                }
            }

            float cycle = 0f;
            for(int x=0;x<level.width();x++) {
                for(int y=0;y<level.height();y++) {
                    int count = 0;
                    MSpace m = level.getSpace(x,y);
                    for(int i=x-1;i<=x+1;i++) {
                        for(int j=y-1;j<=y+1;j++) {
                            if(i>=0&&j>=0&&i<level.width()&&j<level.height()) {
                                MSpace t = level.getSpace(i,j);
                                if(t instanceof Grass || t instanceof Ground || t instanceof Floor) {
                                    if(!(t instanceof Water)) {
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    if(count<=1||m==null) {
                        //if(!(m instanceof Floor)&&!(m instanceof Wall)&&!(m instanceof Stairs)) {
                        Water w = new Water(count==0?8:4, y%2==0?1f:0f);
                        if(m==null) {
                            level.setSpace(w, x, y);
                        }
                        else if(m instanceof Blank) {
                            NHSpace bl = (NHSpace) m;
                            level.setSpace(w, x, y);
                            w.setLoot(bl.getLoot());
                        }
                    }
                    else if(count>7) {
                        if(m==null) {
                            level.setSpace(new Water(4, y%2==0?1f:0f), x, y);
                        }
                        else if(m instanceof Grass) {
                            level.getSpace(x,y).addParasite(new CamphorTree());
                        }
                    }
                }
            }
        }
        else if(level.getName().equals("Approaching Mare Tranquilitatis")||level.getName().equals("Inside The Machine")) {
            boolean chrom = level.getFloor()>77;
            for(int i=1;i<level.width()-1;i++) {
                for(int j=1;j<level.height()-1;j++) {
                    MatrixMSpace m = (MatrixMSpace) level.getSpace(i,j);
                    if(m instanceof Ground) {
                        for(int x=i-1;x<=i+1;x++) {
                            for(int y=j-1;y<=j+1;y++) {
                                if(level.getSpace(x,y)==null) {
                                    level.setSpace(chrom?new ChromaticWall():new Wall(),x,y);
                                }
                            }
                        }
                        m.replace(new Floor());
                    }
                    else if(m instanceof Wall) {
                        if(chrom) {
                            m.replace(new ChromaticWall());
                        }
                    }
                }
            }
        }
        else if(level.getName().equals("The Lunar Subterraine")) {
            for(int i=1;i<level.width()-1;i++) {
                for(int j=1;j<level.height()-1;j++) {
                    NHSpace m = (NHSpace) level.getSpace(i,j);
                    if(m!=null) {
                        if(m instanceof Doorway) {
                            m.setColor("dark-gray");
                        }
                        else {
                            m.setColor("black");
                        }
                    }
                }
            }
        }
    }

    private void runParts() {
        Level level;
        int astairindex = 0, dstairindex = 0;
        Pattern[] ps = _patterns.get(_lparts.length);
        Pattern p = ps[Rand.om.nextInt(ps.length)];
        if(_level.getFloor()==25||_level.getFloor()==49) {
            p = _town;
        }
        for(int i=0;i<_lparts.length;i++) {
            if(i==0) {
                level = _level;
            }
            else {
                level = new Level(_level.width(), _level.height());
                level.setFloor(_level.getFloor());
                if(level.getFloor()==28) {
                    addFFL1(level);
                }
            }
            if(i==0&&level.getFloor()>=20&&level.getFloor()<30) {
                Shops.setChance(0);
            }
            else {
                Shops.setChance(50);
            }
            SectionLevelGenerator g = new SectionLevelGenerator(p.layouts()[i].layout());
            g.setCurrentPartition(i);
            g.setAscendingStairIndex(astairindex);
            g.setDescendingStairIndex(dstairindex);
            g.setAddAscendingStairs(_lparts[i].ascending());
            g.setAddDescendingStairs(_lparts[i].descending());
            g.setRoomSanity(getRoomSanity(_level));
            g.setPassagewaySanity(getPassagewaySanity(_level));
            g.setMixin(_mixin);
            g.setCellSpacing(_spacing);
            g.setMaxCells(_maxCells);
            g.setAllowWallOverlap(_allowWallOverlap);
            g.setDrawPassageways(_drawPassageways);
            g.setRoomModulator(this);
            g.generate(level, _player);
            if(level.getFloor()==24&&i==1) {
                MSpace sta = level.findAll(Stairs.class).get(0);
                NHSpace s = (NHSpace)level.findDistantEmptySpace(level.normalRooms(), (MatrixMSpace)sta);
                s.add(new BookOfSands());
            }
            if((level.getFloor()>=20&&level.getFloor()<25)||
                level.getFloor()>25&&level.getFloor()<30) {
                if(i==0) {
                    generateBattlefield(level);
                }
            }
            if(i>0) {
                _level.union(level);
            }
            astairindex = g.getAscendingStairIndex();
            dstairindex = g.getDescendingStairIndex();
        }
    }

    protected void generateBattlefield(final Level level) {
        generateTown(level, null);
        List<MSpace> nullify = new ArrayList<MSpace>();
        for(int i=0;i<level.width();i++) {
            for(int j=0;j<level.height();j++) {
                MSpace s = level.getSpace(i,j);
                if(s!=null) {
                    if(s instanceof BotMixin.SpawnSpace) {
                        continue;
                    }
                    int sur = 0;
                    for(MSpace sp:s.cardinal()) {
                        if(sp==null) {
                            sur++;
                        }
                    }
                    if(sur==2) {
                        nullify.add(s);
                        continue;
                    }
                    else if(s instanceof Wall) {
                        if(Rand.d100(85)) {
                            level.setSpace(new Floor(), i, j);
                        }
                    }
                    else if(s instanceof Ground) {
                        if(Rand.d100(80)) {
                            level.setSpace(new Pit(), i, j);
                        }
                        else {
                            level.setSpace(new Floor(), i, j);
                        }
                    }
                    else if(s instanceof Doorway) {
                        level.setSpace(new Floor(), i, j);
                    }
                }
            }
        }
        for(MSpace s:nullify) {
            s.replace(null);
        }
    }

    protected void generateTown(final Level level, final String guardian) {
        final int min = 9;
        final int max = level.width()-10;
        for(Level.Room r:level.normalRooms()) {
            List<Level.Room.Connector> cs = Arrays.asList(r.getConnectors());
            Collections.shuffle(cs, Rand.om);
            for(Level.Room.Connector c:cs) {
                if(c.away()[0]<=min||c.away()[0]>=max||c.away()[1]<=4||c.away()[1]>=level.height()-4) {
                    continue;
                }
                if(level.getSpace(c.away()[0], c.away()[1])==null) {
                    TDoorway td = new TDoorway();
                    td.setVertical(c.isVertical());
                    level.setSpace(td, c.coord()[0], c.coord()[1]);
                    break;
                }
            }
        }
        // first, remove stairways from buildings and place outside
        List<MSpace> stairs = level.findAll(Stairs.class);
        for(MSpace m:stairs) {
            m.replace(new Floor());
        }
        for(MSpace m:stairs) {
            boolean found = false;
            do {
                int i = Rand.om.nextInt(max-min)+min;
                int j = Rand.om.nextInt(level.height());
                if(level.getSpace(i,j)!=null) {
                    continue;
                }
up:             for(int x=i-2;x<=i+2;x++) {
                    for(int y=j-2;y<=j+2;y++) {
                        if(level.getSpace(x,y) instanceof Wall) {
                            level.setSpace((MatrixMSpace)m, i, j);
                            found = true;
                            break up;
                        }
                    }
                }
            } while(!found);
        }


        // for each room,
        //   create path to other room,
        //   fill with floor.
        //   and draw single row of floor around this room
        //   then fill null border spaces with walls
        Filter f = new Filter() {
            public boolean accept(MSpace s) {
                MatrixMSpace sp = (MatrixMSpace) s;
                if(sp.getI()<min||sp.getJ()<0||sp.getI()>max||sp.getJ()>_height) {
                    return false;
                }
                return true;
            }
        };
        for(Level.Room r1:level.normalRooms()) {
            for(Level.Room r2:level.normalRooms()) {
                if(r1==r2) {
                    continue;
                }
                MSpace start = level.getSpace(r1.centerX(), r1.centerY());
                MSpace end = level.getSpace(r2.centerX(), r2.centerY());
                MSpace[][] paths = start.paths(end, true, 1, f, 1f, null);
                for(MSpace m:paths[Rand.om.nextInt(1)]) {
                    if(m.isNull()) {
                        m.replace(new Floor());
                    }
                }
            }
        }

        for(int i=min+1;i<max-1;i++) {
            for(int j=1;j<level.height()-1;j++) {
                int tot = 0;
                if(level.getSpace(i,j)!=null) {
                    continue;
                }
                for(int x=i-1;x<=i+1;x++) {
                    for(int y=j-1;y<=j+1;y++) {
                        MSpace q = level.getSpace(x,y);
                        if(q instanceof Wall || q instanceof Stairs) {
                            tot++;
                        }
                    }
                }
                if(tot>0) {
                    level.setSpace(new Floor(), i, j);
                }
            }
        }
        for(int i=min;i<max;i++) {
            for(int j=1;j<level.height()-1;j++) {
                if(level.getSpace(i,j)!=null) {
                    continue;
                }
                int totf = 0;
                int totn = 0;
                for(int x=i-1;x<=i+1;x++) {
                    for(int y=j-1;y<=j+1;y++) {
                        if(level.getSpace(x,y) instanceof Floor) {
                            totf++;
                        }
                        else if(level.getSpace(x,y)==null) {
                            totn++;
                        }
                    }
                }
                if(totf>0&&totn>0) {
                    level.setSpace(guardian!=null?new IrreplaceableWall():new Wall(), i, j);
                }
            }
        }
        if(guardian!=null) {
            Shops.setShuffle(false);
            Shops.setChance(101);
            Shops.setAmount(100);
            Shops s = new Shops();
            if(level.getFloor()==25) {
                s.getTypes().add(0,
                    new Store(new CategoryFilter("we're all sinking in our own mud"), null, "The Clocktower") {
                        public boolean hasKeeper() { return false; }

                        public void modulate(Building build) {
                            MSpace s = level.getSpace((build.x1+build.x2)/2, (build.y1+build.y2)/2);
                            s.replace(new Stairs(true, 1));
                        }
                    });
                s.getTypes().add(0,
                    new Store(new Multifilter(new CategoryFilter("scroll"), new CategoryFilter("book")), null, "The Library") {
                        public void modulate(Building build) {
                            MSpace s = level.getSpace((build.x1+build.x2)/2, (build.y1+build.y2)/2);
                            s.replace(new Stairs(false, 1));
                        }
                    });
            }
            s.mix(level);
        }
        if(guardian!=null) {
            for(int i=0;i<10;i++) {
                MSpace rs = level.findRandomEmptySpace();
                if(rs!=null) {
                    rs.setOccupant(Universe.getUniverse().createBot(guardian));
                }
            }
            BotMixin.setCoefficient(0.1f);
        }
        BotMixin bm = new TBotMixin();
        bm.setWandering(false);
        bm.mix(level);
    }

    public void modulate(Level.Room room) {
        room.setDoorClass(TDoorway.class);
        room.setWallClass(TWall.class);
        if(_level.getName().equals("Van Allen's Land")) {
            room.setRounded(true);
        }
        if(_level.getName().equals("The Ionosphere")||_level.getName().equals("The Great Void") || _level.getName().equals("Van Allen's Land")) {
            room.setWalled(false);
        }
        else if(_level.getName().equals("Approaching Mare Tranquilitatis")) {
            room.setDoorClass(Autodoor.class);
        }
        else if(_level.getName().equals("Inside The Machine")) {
            room.setDoorClass(Autodoor.class);
            room.setWallClass(ChromaticWall.class);
        }
        if(Rand.d100(_grass)) {
            room.setWalled(false);
            room.setFloorClass(Grass.class);
            if(_level.getName().equals("The Shifting Planes")) {
                room.setRounded(true);
            }
        }
        if(_level.getFloor()>70&&_level.getFloor()<77) {
            room.setDoors(false);
            room.setRounded(true);
            room.setWalled(false);
            if(Rand.d100(40)) {
                //room.setFloorClass(Sakura.class);
                room.setWalled(false);
            }
        }
        if(_level.getFloor()==25) {
            room.setDoors(true);
        }
    }

    private void mixin(Level level) {
        AbstractLevelGenerator.mixin(level);
    }

    private void addLab(Level l) {
        final String[] labels = new String[]{"51", "50", "49", "48", "47", "46", "45", "44"};
        int lcount = 0;
        int width=80, height=24;
        for(int i=0;i<4;i++) {
            for(int j=0;j<2;j++) {
                //int cx = (1+j)*6+j;
                int cx = j==0?5:11;
                int cy = (1+i)*5+j;
                Level.Room r = new Level.Room(cx, cy, 4, 3, width, height);
                l.addRoom(r, true);
                if(lcount==0) {
                    r.setSpecial(true);
                    l.getSpace(cx,cy).setOccupant(Universe.getUniverse().createBot("Number 51"));
                }
                if(j==0) {
                    l.setSpace(new Doorway(false, false, lcount==0), cx+2,cy);
                }
                else {
                    l.setSpace(new Doorway(false, false), cx-2,cy);
                }
                Ground g = new Ground();
                l.setSpace(g, 8, cy);
                g.addParasite(new Writing(labels[lcount++], 0));
            }
        }
        int i = 5;
        int j = 8;
        for(;i<height-5;i++) {
            if(l.getSpace(j,i)==null) {
                l.setSpace(new Ground(), j, i);
            }
        }
        for(;j<width-10;j++) {
            l.setSpace(new Ground(), j, i);
            l.setSpace(new Ground(), j, 9);
        }
        l.setSpace(new Stairs(false), j-1, i);
        l.setSpace(new Stairs(true), j-1, 9);
        l.addRoom(new Level.Room(21, height/2+2, 12, 8, width, height), true);
        String paper =
            "--is what haunts me as I wander the Tower. I've been around "+
            "long enough to decipher some of its secrets, but in doing so "+
            "I have found nothing but frustration. For when the answers come, "+
            "they are nothing but hollow, mechanical explanations. I have yet to "+
            "discover a single clue as to 'why' of the Tower.";
        ((NHSpace)l.getSpace(24, height/2)).add(new ScrapOfPaper(paper));

        String notes =
            "3 Mior\n \n"+
            "The Disk -\n"+
            "No. 50 is making great progress; I suspect the crystallized essense "+
            "of his flesh will finally produce a gem of the necessary quality. "+
            "If so then I have wasted some time with No. 51, but so it goes."+
            "\n \n"+
            "The Machine -\n"+
            "I've spent more time verifying the plans are correct than on the "+
            "design itself. Tomorrow I will begin scouting construction sites. "+
            "Nothing in the immediate vicinity is useable; I may search in higher climes.";
        ((NHSpace)l.getSpace(21, height/2+2)).add(new Notebook(notes));

        l.setSpace(new Doorway(true, false), 21, i-1);
        l.setSpace(new Doorway(true, false), 21, 10);
        l.addRoom(new Level.Room(31, height/2+4, 8, 4, width, height), true);
        l.getSpace(21, height/2+2).setOccupant(Universe.getUniverse().createBot("Blue Cadet 3"));
        l.addRoom(new Level.Room(31, height/2, 8, 4, width, height), true);
        l.setSpace(new Doorway(true, false), 31, i-1);
        l.setSpace(new Doorway(true, false), 31, 14);
        l.setSpace(new Doorway(true, false), 31, 10);
        l.setSpace(new Doorway(false, false), 27, 12);
        l.setSpace(new Doorway(false, false), 27, i-3);

        int ym = height/2+2;
        final Pit burn = new Pit();
        burn.setColor("red");
        final int bu = 16;
        l.getSpace(bu, ym).replace(burn);
        for(int x=bu-1;x<=bu+1;x++) {
            for(int y=ym-1;y<=ym+1;y++) {
                if(x==bu&&y==ym) {
                    continue;
                }
                if(x==bu+1&&y==ym) {
                    l.getSpace(x,y).replace(new TDoorway());
                }
                else {
                    l.getSpace(x,y).replace(new Wall());
                }
            }
        }
        Switch sw = new Switch("Incinerator", "", new Switch.Toggle() {
            Burning _burn;
            public void toggle(boolean on) {
                if(on) {
                    _burn = new Burning(Integer.MAX_VALUE);
                    burn.addParasite(_burn);
                }
                else {
                    try {
                        burn.removeParasite(_burn);
                    }
                    catch(IllegalStateException e) {
                    }
                }
            }
        });
        l.getSpace(bu, ym+2).replace(sw);
    }

    private void addFFL1(Level l) {
        Level.Room r = new Level.Room(l.width()-4, l.height()-7, 6, 7, l.width(), l.height());
        r.setSpecial(true);
        l.addRoom(r, true);
        NHSpace s = (NHSpace) l.getSpace(r.getX1()+2, r.getY1()+2);
        Corpse c = new Humanoid().toCorpse();
        c.setName("dead child");
        c.setSize(2f);
        c.setNutrition(-1);
        s.add(c);
        s = (NHSpace) l.getSpace(r.getX1()+4, r.getY1()+4);
        c = new Humanoid().toCorpse();
        c.setName("dead child");
        c.setNutrition(-1);
        c.setSize(2f);
        s.add(c);

        s = (NHSpace) l.getSpace(r.getX1()+4, r.getY1()+6);
        c = new Humanoid().toCorpse();
        c.setName("dead woman");
        c.setNutrition(-1);
        c.setSize(5f);
        s.add(c);
        s.add(new Diary("Aug. 6th\n \nWe finally found shelter in a small room."
            +" I constantly hear soldiers and monsters in the hallway, and"
            +" I don't know how long the door will keep them out."
            +" I gave the children the last of the food. Kenji went to look for supplies."
            +" All I can do is pray he returns. I have no strength left..."
            +"\n \nCreator, please look after the children.") {
            public void invoke(NHBot b) {
                super.invoke(b);
                b.getInventory().add(new NuclearBomb());
                b.getInventory().consume(this);
                N.narrative().print(b, "The diary crumbles in your hands.");
            }
        });
    }

    public static class PriceScanner extends RFID {
        private NHBot _shopkeeper;


        public PriceScanner(NHBot shopkeeper) {
            _shopkeeper = shopkeeper;
        }

        public void trigger(NHBot b) {
            Gold g = new Gold();
            for(Item i:b.getInventory().getItem()) {
                if(i instanceof Gold) {
                    g = (Gold) i;
                }
            }
            boolean ranout = false;
            for(Item i:b.getInventory().getItem()) {
                PriceTag p = (PriceTag) i.getFragment(PriceTag.NAME);
                if(p!=null) {
                    if(g.getCount()-p.getAmount()>=0) {
                        g.setCount(g.getCount()-p.getAmount());
                        // TODO: hack to generate event
                        b.getInventory().remove(g);
                        b.getInventory().add(g);
                        N.narrative().print(b, Grammar.nonspecific(i));
                        i.removeFragment(p);
                    }
                    else {
                        ranout = true;
                    }
                }
            }
            if(ranout) {
                N.narrative().print(b, "Your funds have run dry!");
                N.narrative().more();
                if(!_shopkeeper.isDead()&&_shopkeeper.getBlind()==0) {
                    N.narrative().print(b, Grammar.start(_shopkeeper, "eye")+" "+Grammar.noun(b)+" warily.");
                }
            }
        }
    }

    public static class Partition implements java.io.Serializable {
        private int _a;
        private int _d;


        public Partition(int ascending, int descending) {
            _a = ascending;
            _d = descending;
        }

        public int ascending() {
            return _a;
        }

        public int descending() {
            return _d;
        }

        public void incAsc() {
            _a++;
        }

        public void incDesc() {
            _d++;
        }

        public void decAsc() {
            _a--;
        }

        public void decDesc() {
            _d--;
        }
    }

    private static class Pattern implements java.io.Serializable {
        private Layout[] _layouts;


        public Pattern(Layout... layouts) {
            _layouts = layouts;
        }

        public Layout[] layouts() {
            return _layouts;
        }
    }

    private static class Layout implements java.io.Serializable {
        private int[][] _layout;


        public Layout(int[]... layout) {
            _layout = layout;
        }

        public int[][] layout() {
            return _layout;
        }
    }
}
