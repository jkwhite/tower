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
package org.excelsi.aether.ui;


import org.excelsi.aether.*;
import java.util.List;
import java.util.ArrayList;


public class InputQueue implements InputSource {
    private List<GameAction> _actions = new ArrayList<GameAction>();
    private List<String> _keys = new ArrayList<String>();
    private ActionSource _actionSource;
    private List<String> _recorded = new ArrayList<String>();
    private List<GameAction> _recordedActions = new ArrayList<GameAction>();
    private List<String> _last = new ArrayList<String>();
    private List<GameAction> _lastActions = new ArrayList<GameAction>();
    private boolean _playback = false;


    public InputQueue(ActionSource a) {
        _actionSource = a;
    }

    public GameAction actionFor(String key) {
        return _actionSource.actionFor(key);
    }

    public void checkpoint() {
        if(!_playback) {
            _last.clear();
            _last.addAll(_recorded);
            _recorded.clear();
            _lastActions.clear();
            _lastActions.addAll(_recordedActions);
            _recordedActions.clear();
        }
    }

    public void playback() {
        _playback = true;
        _recorded.clear();
        _recordedActions.clear();
    }

    public boolean isPlayback() {
        return _playback;
    }

    public void addAction(GameAction a, String key) {
        synchronized(_actions) {
            _actions.add(a);
            _keys.add(key);
            _actions.notify();
        }
    }

    public String nextKey() {
        if(_playback) {
            if(_last.size()>0) {
                String n = _last.remove(0);
                GameAction a = _lastActions.remove(0);
                _recordedActions.add(a);
                _recorded.add(n);
                return n;
            }
            else {
                _playback = false;
                checkpoint();
            }
        }
        synchronized(_actions) {
            while(_actions.isEmpty()) {
                try {
                    _actions.wait();
                }
                catch(InterruptedException e) {
                    throw new InputInterruptedException(e);
                }
            }
            GameAction ga = _actions.remove(0);
            _recordedActions.add(ga);
            String key = _keys.remove(0);
            _recorded.add(key);
            return key;
        }
    }

    public GameAction nextAction() {
        if(_playback) {
            if(_lastActions.size()>0) {
                GameAction a = _lastActions.remove(0);
                String key = _last.remove(0);
                _recordedActions.add(a);
                _recorded.add(key);
                return a;
            }
            else {
                _playback = false;
                checkpoint();
            }
        }
        synchronized(_actions) {
            while(_actions.isEmpty()) {
                try {
                    _actions.wait();
                }
                catch(InterruptedException e) {
                    throw new InputInterruptedException(e);
                }
            }
            String key = _keys.remove(0);
            GameAction ga = _actions.remove(0);
            if(ga!=null&&ga.isRecordable()) {
                _recorded.add(key);
                _recordedActions.add(ga);
            }
            return ga;
        }
    }

    public GameAction nextAction(long timeout) {
        if(_playback) {
            if(_lastActions.size()>0) {
                GameAction a = _lastActions.remove(0);
                String key = _last.remove(0);
                _recordedActions.add(a);
                _recorded.add(key);
                return a;
            }
            else {
                _playback = false;
                checkpoint();
            }
        }
        synchronized(_actions) {
            if(_actions.isEmpty()) {
                try {
                    _actions.wait(timeout);
                }
                catch(InterruptedException e) {
                    throw new InputInterruptedException(e);
                }
            }
            if(_actions.isEmpty()) {
                return null;
            }
            else {
                String key = _keys.remove(0);
                GameAction ga = _actions.remove(0);
                if(ga!=null&&ga.isRecordable()) {
                    _recorded.add(key);
                    _recordedActions.add(ga);
                }
                return ga;
            }
        }
    }
}
