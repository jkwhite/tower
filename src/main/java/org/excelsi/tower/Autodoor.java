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


public class Autodoor extends Doorway {
    private static final long serialVersionUID = 1L;
    private MSpaceListener _listener = getOpenListener();
    private MSpace _s1;
    private MSpace _s2;


    public Autodoor() {
        setColor("dark-blue");
    }

    public void setMatrix(Matrix m) {
        super.setMatrix(m);
        m.addListener(getFloorListener());
        refresh();
    }

    public void setVertical(boolean vertical) {
        super.setVertical(vertical);
        refresh();
    }

    private void refresh() {
        if(getMatrix()==null) {
            return;
        }
        if(_s1!=null) {
            _s1.removeMSpaceListener(_listener);
        }
        if(_s2!=null){
            _s2.removeMSpaceListener(_listener);
        }
        if(isVertical()) {
            _s1 = move(Direction.north);
            _s2 = move(Direction.south);
        }
        else {
            _s1 = move(Direction.east);
            _s2 = move(Direction.west);
        }
        if(_s1!=null) {
            _s1.addMSpaceListener(_listener);
        }
        if(_s2!=null) {
            _s2.addMSpaceListener(_listener);
        }
    }

    public void setOpen(boolean open) {
        if(!open||((_s1==null||_s1.isOccupied())||(_s2==null||_s2.isOccupied()))) {
            super.setOpen(open);
            if(_s1!=null&&_s1.isOccupied()) {
                ((NHEnvironment)_s1.getOccupant().getEnvironment()).unhide();
            }
            if(_s2!=null&&_s2.isOccupied()) {
                ((NHEnvironment)_s2.getOccupant().getEnvironment()).unhide();
            }
        }
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Board(), new Nail()};
        ing[0].setCount(3);
        ing[1].setCount(7);
        return ing;
    }

    public MSpaceListener getOpenListener() {
        return new MSpaceAdapter() {
            public void occupied(MSpace s, Bot b) {
                setOpen(true);
            }

            public void unoccupied(MSpace s, Bot b) {
                close();
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to!=Autodoor.this) {
                    if(to==_s1||to==_s2) {
                        setOpen(true);
                    }
                    else {
                        close();
                    }
                }
            }

            public void close() {
                if(!isOccupied()&&(_s1==null||!_s1.isOccupied())&&(_s2==null||!_s2.isOccupied())) {
                    setOpen(false);
                }
            }
        };
    }

    public MatrixListener getFloorListener() {
        return new MatrixListener() {
            public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
                for(MSpace s:spaces) {
                    if(s==_s1) {
                        _s1.removeMSpaceListener(_listener);
                        _s1 = null;
                        //s.addMSpaceListener(_listener);
                    }
                    else if(s==_s2) {
                        _s2.removeMSpaceListener(_listener);
                        _s2 = null;
                        //s.addMSpaceListener(_listener);
                    }
                    else if(s==Autodoor.this) {
                        m.removeListener(this);
                        if(_s1!=null) {
                            _s1.removeMSpaceListener(_listener);
                        }
                        if(_s2!=null) {
                            _s2.removeMSpaceListener(_listener);
                        }
                    }
                }
            }

            public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
                MSpace s1, s2;
                if(isVertical()) {
                    s1 = move(Direction.north);
                    s2 = move(Direction.south);
                }
                else {
                    s1 = move(Direction.east);
                    s2 = move(Direction.west);
                }
                for(MSpace s:spaces) {
                    if(s1==s) {
                        _s1 = s;
                        _s1.addMSpaceListener(_listener);
                    }
                    if(s2==s) {
                        _s2 = s;
                        _s2.addMSpaceListener(_listener);
                    }
                }
            }

            public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            }
        };
    }
}
