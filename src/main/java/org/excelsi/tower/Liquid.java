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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public abstract class Liquid extends Floor implements Immersion {
    private static final long serialVersionUID = 1L;
    private int _depth;
    private float _cycle;
    private boolean _new = true;
    private GearChange _g = new GearChange();


    public Liquid(String color) {
        this(color, 8, 0f);
    }

    public Liquid(String color, int depth, float cycle) {
        super(color);
        _depth = depth;
        //setDepth(depth);
        _cycle = cycle;
        addMSpaceListener(new MSpaceAdapter() {
            public void occupied(MSpace source, Bot b) {
                b.addListener(_g);
                soak(true);
            }

            public void unoccupied(MSpace source, Bot b) {
                b.removeListener(_g);
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to==Liquid.this) {
                    b.addListener(_g);
                    soak(!(from instanceof Liquid));
                }
                else {
                    b.removeListener(_g);
                }
            }
        });
    }

    public void setDepth(int depth) {
        _depth = depth;
    }

    public int getDepth() {
        return _depth;
    }

    public String getModel() {
        return "~";
    }

    public boolean isWalkable() {
        return true;
    }

    public boolean isTransparent() {
        return true;
    }

    public boolean isStretchy() {
        return true;
    }

    public float getCycle() {
        return _cycle;
    }

    public boolean isEmpty() {
        return false;
    }

    public int getOccupantDepth() {
        int d = super.getOccupantDepth();
        if(getOccupant().getModifiedWeight()<1/*&&d>0*/) {
            //d = 0;
            d -= getDepth();
        }
        return d;
    }

    public void update() {
        super.update();
        if(_new) {
            // ensure that liquid spreads at fairly constant rate
            _new = false;
            return;
        }
        if(Rand.om.nextBoolean()) {
            int alt = getModifiedDepth();
            int hei = getHeight();
            for(MSpace m:cardinal()) {
                if(m != null && !(m instanceof Liquid)) {
                    //if(((NHSpace)m).getDepth()>0) {
                    //if(((NHSpace)m).getModifiedDepth()>alt) {
                    if(((NHSpace)m).getHeight()<hei) {
                        Liquid l = spread();
                        l.setDepth(((NHSpace)m).getDepth());
                        m.replace(l);
                        break; // only allow one replace per turn
                    }
                    else if(m instanceof Whirlpool) {
                    }
                    else if(m instanceof Hole) {
                        m.replace(new Whirlpool());
                        break; // only allow one replace per turn
                    }
                }
                else if(m instanceof Liquid) {
                    if(getAltitude()!=((NHSpace)m).getAltitude()) {
                        int nalt = (getAltitude()+((NHSpace)m).getAltitude()-1)/2;
                        //System.err.println("TALT: "+getAltitude());
                        //System.err.println("OALT: "+((NHSpace)m).getAltitude());
                        //System.err.println("ALT: "+nalt);
                        /*
                        if(nalt<alt) {
                            if(--_depth<=0) {
                                replace(new Floor());
                                break;
                            }
                        }
                        else if(nalt>alt) {
                            ++_depth;
                        }
                        */
                        setAltitude(nalt);
                        break; // only allow one per turn
                    }
                }
            }
        }
    }

    /**
     * Invoked when the occupying bot should suffer the effects
     * of being immersed in this liquid.
     *
     * @param first <code>true</code> if the bot has just stepped
     * from dry land into liquid, <code>false</code> if the bot
     * was already in liquid at the time it moved to this
     * space
     */
    abstract protected void soak(boolean first);

    /**
     * Invoked when this liquid should spread to an adjacent
     * space by virtue of a change in depth for the adjacent
     * space.
     *
     * @return instance of liquid to occupy adjacent space
     */
    abstract protected Liquid spread();

    private class GearChange extends NHEnvironmentAdapter {
        public void equipped(NHBot b, Item i) {
            if(adjust(i)) {
                soak(true);
            }
        }

        public void unequipped(NHBot b, Item i) {
            if(adjust(i)) {
                soak(true);
            }
        }

        private boolean adjust(Item i) {
            return (i.getModifier()!=null&&i.getModifier().getWeight()!=0)||(i.getPackedModifier()!=null&&i.getPackedModifier().getWeight()!=0);
        }
    }
}
