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
import java.util.ArrayList;
import java.util.List;


public class Overlay {
    private NHSpace _current;
    private List<OverlayListener> _listeners = new ArrayList<OverlayListener>();


    public Overlay(NHSpace s) {
        _current = s;
    }

    public void addOverlayListener(OverlayListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeOverlayListener(OverlayListener listener) {
        if(!_listeners.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+this);
        }
    }

    public void move(Direction d) {
        NHSpace next = (NHSpace) _current.move(d, true);
        _current.setOverlay(null);
        next.setOverlay(this);
        NHSpace ocurrent = _current;
        _current = next;
        for(OverlayListener l:new ArrayList<OverlayListener>(_listeners)) {
            l.overlayMoved(this, ocurrent, _current);
        }
    }

    public NHSpace getSpace() {
        return _current;
    }

    public void remove() {
        _current.setOverlay(null);
        for(OverlayListener l:new ArrayList<OverlayListener>(_listeners)) {
            l.overlayRemoved(this);
        }
    }
}
