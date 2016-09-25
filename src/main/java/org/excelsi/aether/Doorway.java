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


import org.excelsi.matrix.*;


public class Doorway extends DefaultNHSpace {
    private static final long serialVersionUID = 1L;

    private boolean _vertical;
    private boolean _open;
    private boolean _locked;
    private boolean _unlockable = true;


    public Doorway() {
        super("brown");
    }

    public Doorway(boolean vertical) {
        this(vertical, false);
    }

    public Doorway(boolean vertical, boolean open) {
        this(vertical, open, false);
    }

    public Doorway(boolean vertical, boolean open, boolean locked) {
        super("brown");
        _vertical = vertical;
        _open = open;
        _locked = !open&&locked; // open doors cannot be locked
    }

    public String getName() { return "door"; }

    public void setUnlockable(boolean unlockable) {
        _unlockable = unlockable;
    }

    public boolean isUnlockable() {
        return _unlockable;
    }

    public boolean isVertical() {
        return _vertical;
    }

    public void setVertical(boolean vertical) {
        _vertical = vertical;
    }

    public void setOpen(boolean open) {
        if(_open!=open) {
            boolean oopen = _open;
            if(isLocked()&&open) {
                setLocked(false);
            }
            _open = open;
            notifyAttr("open", oopen, _open);
        }
    }

    public boolean isOpen() {
        return _open;
    }

    public void setLocked(boolean locked) {
        if(locked!=_locked) {
            boolean olock = _locked;
            _locked = locked;
            notifyAttr("lock", olock, _locked);
            if(locked) {
                setOpen(false);
            }
            else if(!isUnlockable()) {
                setUnlockable(true);
            }
        }
    }

    public boolean isLocked() {
        return _locked;
    }

    @Override public boolean look(Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            c.n().print(c.actor(), "There is a"+(_locked?" locked":_open?"n open":" closed")+" door here.");
            return true;
        }
        return ret;
    }

    public String getModel() {
        return _open?_vertical?"|":"-":"+";
    }

    public boolean isWalkable() {
        return isOpen();
    }

    public boolean isTransparent() {
        return isOpen()?numItems()<8:false;
    }

    public boolean isRoom() {
        return true;
    }

    public boolean isPassageway() {
        return false;
    }

    public boolean push(MSpace pusher, Direction dir) {
        if(pusher instanceof Wall) {
            return false;
        }
        else {
            return super.push(pusher, dir);
        }
    }
}
