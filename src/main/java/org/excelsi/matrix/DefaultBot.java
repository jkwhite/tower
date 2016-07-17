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
package org.excelsi.matrix;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;


public abstract class DefaultBot implements Bot {
    public static final long serialVersionUID = 1L;
    private Environment _e;
    private List<EnvironmentListener> _listeners;


    public final void setEnvironment(Environment e) {
        _e = e;
    }

    public void addListener(EnvironmentListener listener) {
        if(_listeners==null) {
            _listeners = new ArrayList<EnvironmentListener>();
        }
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeListener(EnvironmentListener listener) {
        if(_listeners==null||!_listeners.remove(listener)) {
            //throw new IllegalArgumentException("listener '"+listener+"' not on "+this);
            Logger.global.severe("listener '"+listener+"' not on "+this);
        }
    }

    public List<EnvironmentListener> getListeners() {
        return _listeners!=null?new ArrayList<EnvironmentListener>(_listeners):new ArrayList<EnvironmentListener>();
    }

    public Environment getEnvironment() {
        return _e;
    }
}
