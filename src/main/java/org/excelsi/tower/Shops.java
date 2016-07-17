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
import org.excelsi.matrix.MSpace;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;


public class Shops implements Mixin {
    private List<Store> _types = new ArrayList<Store>();
    private static int _chance = 75;
    private static int _amount = 1;
    private static boolean _shuffle = true;


    public Shops() {
        _types.add(new Store(new InstanceofFilter(Helmet.class), "Haberdashery", "Cranium Toppers"));
        _types.add(new Store(new CategoryFilter("tool"), "Hardware", "Tools", "Toolery"));
        _types.add(new Store(new ItemFilter() { public boolean accept(Item i, NHBot b) { return i instanceof Comestible || i instanceof Potion; } }, "Sundry", "Sundries", "Sundrymart"));
        _types.add(new Store(new CategoryFilter("pill"), "Pharmacy", "Pharmacium"));
        _types.add(new Store(new ItemFilter() { public boolean accept(Item i, NHBot b) { return i instanceof Pill || i instanceof Potion; } }, "Apothecary", "Apothecarium"));
        _types.add(new Store(new Multifilter(new CategoryFilter("armor"), new CategoryFilter("weapon")), "Armory", "Arms"));
        _types.add(new Store(new CategoryFilter("gem"), "Jewelry", "Gemstones"));
        _types.add(new Store(new CategoryFilter("weapon"), "Weaponry", "Cut-Rate Arms", "Combat Outfitters"));
        _types.add(new Store(new InstanceofFilter(Item.class), "General Goods", "General Store", "One-Stop Shop", "Fastmart"));
        _types.add(new Store(new InstanceofFilter(Shoes.class), "Cobblery", "Shoery", "Shoetown"));
        _types.add(new Store(new InstanceofFilter(Scroll.class), "Scrolls", "Just Scrolls"));
        _types.add(new Store(new Multifilter(new InstanceofFilter(Junk.class), new SpecificFilter("jackhammer", "battery")), "Salvage", "The Rusty Wrench"));
        _types.add(new Store(new Multifilter(new InstanceofFilter(Light.class), new InstanceofFilter(Bow.class), new InstanceofFilter(Arrow.class), new InstanceofFilter(Bolt.class), new InstanceofFilter(Crossbow.class),
            new ItemFilter() { public boolean accept(Item i, NHBot b) { return i instanceof Armament && (Armor.LIGHT_LEATHER.equals(((Armament)i).getSkill()) || Armor.HEAVY_LEATHER.equals(((Armament)i).getSkill())); } }, new SpecificFilter("food ration", "tripe ration", "laminating kit", "reinforcing kit", "yarn")),
                    "Hunting Supplies", "Survival Gear", "The Last Stand", "Live Free Or Die"));
    }

    public List<Store> getTypes() {
        return _types;
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public static void setChance(int chance) {
        _chance = chance;
    }

    public static void setAmount(int amount) {
        _amount = amount;
    }

    public static void setShuffle(boolean shuffle) {
        _shuffle = shuffle;
    }

    public static boolean isShuffle() {
        return _shuffle;
    }

    public void mix(Object o) {
        if(Rand.d100(_chance)) {
            //System.err.println("GOT CHANCE");
            Level level = (Level) o;
            if(level.getFloor()>899) {
                return;
            }
            List<Level.Room> normals = level.normalRooms();
            //System.err.println("GENERATING FOR: "+normals);
            if(_shuffle) {
                Collections.shuffle(normals, Rand.om);
                Collections.shuffle(_types, Rand.om);
            }
            int typeIdx = 0;
            int roomIdx = 0;
            int drawn = 0;
            Level.Room shop = null;
            Doorway d = null;
            for(Level.Room r:normals) {
                shop = null;
                //System.err.println("CHECKING: "+r);
                if(r.width()<4||r.height()<4) {
                    //System.err.println("TOO SMALL");
                    continue;
                }
                boolean good = true;
                int doors = 0;
water:          for(int i=r.getX1();i<=r.getX2();i++) {
                    for(int j=r.getY1();j<=r.getY2();j++) {
                        NHSpace s = level.getSpace(i,j);
                        if(s==null) {
                            //System.err.println("NULL SPACE AT "+i+", "+j);
                            continue;
                        }
                        Class c = s.getClass();
                        if((c!=Floor.class && c!=TWall.class && c!=TDoorway.class && c!=ShopWall.class)
                            || (s.getParasites().size()!=0)) {
                            good = false;
                            //System.err.println("UNKNOWN SPACE OR PARASITES AT "+i+", "+j+": "+c);
                            break water;
                        }
                        if(c==TDoorway.class) {
                            doors++;
                            d = (Doorway) s;
                            d.setOpen(false);
                            if(doors>1) {
                                //System.err.println("TOO MANY DOORS AT "+i+", "+j+": "+c);
                                good = false;
                                break water;
                            }
                        }
                    }
                }
                if(doors==1&&good) {
                    shop = r;
                }
                if(shop==null) {
                    continue;
                }
                else {
                    //System.err.println("GOOD");
                    shop.setSpecial(true);
                    Building b = new Building(shop, d, _types.get(typeIdx));
                    b.draw(level);
                    drawn++;
                }
                if(++typeIdx==_types.size()||drawn==_amount) {
                    break;
                }
            }
        }
    }

    public static final class Building {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int door;
        public Doorway doorway;
        public String name;
        public ItemFilter filter;
        public Store store;


        public Building(Level.Room r, Doorway door, Store s) {
            this(r.getX1(), r.getY1(), r.getX2(), r.getY2(), -1, s);
            doorway = door;
        }

        public Building(int tx1, int ty1, int tx2, int ty2, int tdoor, Store s) {
            x1 = tx1;
            y1 = ty1;
            x2 = tx2;
            y2 = ty2;
            door = tdoor;
            filter = s.getFilter();
            store = s;
        }

        public void draw(Level level) {
            //System.err.println("DRAWING SHOP ("+x1+","+y1+")-("+x2+","+y2+")");
            final Building build = this;
            for(int i=build.x1;i<=build.x2;i++) {
                level.setSpace(new ShopWall(), i, build.y1);
                level.setSpace(new ShopWall(), i, build.y2);
            }
            for(int j=build.y1;j<=build.y2;j++) {
                level.setSpace(new ShopWall(), build.x1, j);
                level.setSpace(new ShopWall(), build.x2, j);
            }
            for(int i=build.x1+1;i<=build.x2-1;i++) {
                for(int j=build.y1+1;j<=build.y2-1;j++) {
                    level.setSpace(new ShopFloor(), i, j);
                    level.getSpace(i,j).destroyLoot();
                }
            }
            int minx = build.x1+1;
            int miny = build.y1+1;
            int maxx = build.x2;
            int maxy = build.y2;
            int sx1, sx2, sy1, sy2, x, y;
            boolean vert = false;
            Doorway sdoor = doorway;
            final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;

            if(sdoor!=null) {
                if(sdoor.getI()==build.x1) {
                    door = WEST;
                }
                else if(sdoor.getI()==build.x2) {
                    door = EAST;
                }
                else if(sdoor.getJ()==build.y1) {
                    door = NORTH;
                }
                else {
                    door = SOUTH;
                }
            }
            NHBot keeper = null;
            if(store.hasKeeper()) {
                keeper = Universe.getUniverse().createBot("shopkeeper");
                keeper.setName(store.createKeeper());
                if(keeper.getName().startsWith("Curia ")||keeper.getName().equals("Zyrzephia")||keeper.getName().startsWith("Celli ")||keeper.getName().startsWith("Fierie ")) {
                    keeper.setGender(Gender.female);
                }
                else {
                    keeper.setGender(Gender.male);
                }
                name = keeper.getName();
                if(name.indexOf(' ')>0&&!name.endsWith("Surly")&&!name.endsWith("Joe")&&!name.endsWith("Jim")) {
                    name = name.substring(0, name.indexOf(' '));
                }
            }
            String stname = store.getRandomName();
            if(stname.startsWith("The ")||"Shoetown".equals(name)||"Live Free Or Die".equals(name)) {
                name = stname;
            }
            else {
                name += "'s "+stname;
            }
            //System.err.println("CHOSEN: "+name);
            switch(build.door) {
                case EAST:
                    y = (build.y1+build.y2)/2;
                    sx1 = sx2 = build.x2-1;
                    sy1 = y-1;
                    sy2 = y+1;
                    sdoor = new SDoorway(false);
                    level.setSpace(sdoor, build.x2, y);
                    level.getSpace(build.x2+1, y).addParasite(new Sign(build.name, 0));
                    maxx--;
                    break;
                case WEST:
                    y = (build.y1+build.y2)/2;
                    sx1 = sx2 = build.x1+1;
                    sy1 = y-1;
                    sy2 = y+1;
                    sdoor = new SDoorway(false);
                    level.setSpace(sdoor, build.x1, y);
                    level.getSpace(build.x1-1, y).addParasite(new Sign(build.name, 0));
                    minx++;
                    break;
                case SOUTH:
                    vert = true;
                    x = (build.x1+build.x2)/2;
                    sy1 = sy2 = build.y2-1;
                    sx1 = x-1;
                    sx2 = x+1;
                    sdoor = new SDoorway(true);
                    level.setSpace(sdoor, x, build.y2);
                    level.getSpace(x, build.y2+1).addParasite(new Sign(build.name, 0));
                    maxy--;
                    break;
                case NORTH:
                    vert = true;
                    x = (build.x1+build.x2)/2;
                    sy1 = sy2 = build.y1+1;
                    sx1 = x-1;
                    sx2 = x+1;
                    sdoor = new SDoorway(true);
                    level.setSpace(sdoor, x, build.y1);
                    level.getSpace(x, build.y1-1).addParasite(new Sign(build.name, 0));
                    miny++;
                    break;
                default:
                    throw new IllegalArgumentException("no direction home");
            }
            if(store.hasKeeper()) {
                RFIDTransponder r1 = new RFIDTransponder(new PriceScanner(keeper), vert);
                RFIDTransponder r2 = new RFIDTransponder(new PriceScanner(keeper), vert, r1);
                level.setSpace(r1, sx1, sy1);
                level.setSpace(r2, sx2, sy2);
                r1.setActive(true);
                level.getSpace((build.x1+build.x2)/2, (build.y1+build.y2)/2).setOccupant(keeper);
            }

            int imax = Rand.om.nextInt(5)+7;
            List<MSpace> spaces = new ArrayList<MSpace>();
            for(int i=minx;i<maxx;i++) {
                for(int j=miny;j<maxy;j++) {
                    if(level.getSpace(i,j) instanceof Floor) {
                        spaces.add(level.getSpace(i,j));
                    }
                }
            }
            Collections.shuffle(spaces);
            for(MSpace space:spaces) {
                Item item = Universe.getUniverse().createItem(new ItemFilter() {
                    public boolean accept(Item i, NHBot bot) {
                        return build.filter.accept(i, bot);
                    }
                });
                if(item!=null) {
                    item.addFragment(new PriceTag(item));
                    ((NHSpace)space).add(item);
                    if(--imax==0) {
                        break;
                    }
                }
            }
            /*
down:       for(int i=minx;i<maxx;i++) {
                for(int j=miny;j<maxy;j++) {
                    if(level.getSpace(i,j) instanceof Floor) {
                        Item item = Universe.getUniverse().createItem(new ItemFilter() {
                            public boolean accept(Item i, NHBot bot) {
                                return build.filter.accept(i, bot);
                            }
                        });
                        if(item!=null) {
                            item.addFragment(new PriceTag(item));
                            level.getSpace(i,j).add(item);
                            if(--imax==0) {
                                break down;
                            }
                        }
                    }
                }
            }
            */
            store.modulate(this);
        }
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
                //N.narrative().more();
                if(!_shopkeeper.isDead()&&_shopkeeper.getBlind()==0&&
                    b.getEnvironment().getVisibleBots().contains(_shopkeeper)) {
                    _shopkeeper.getEnvironment().face(b);
                    N.narrative().print(b, Grammar.start(_shopkeeper, "eye")+" "+Grammar.noun(b)+" warily.");
                }
            }
        }
    }

    static class Sign extends Writing {
        public Sign(String text, int decay) {
            super(text, decay);
        }

        public void trigger(NHBot b) {
            MSpace last = b.getEnvironment().getLast();
            if(!(last instanceof SDoorway)) {
                super.trigger(b);
            }
        }
    }
}
