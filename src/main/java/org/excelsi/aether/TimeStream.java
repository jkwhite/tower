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


public class TimeStream extends Thread {
    private Game _g;
    private InputSource _input;
    private Narrative _n;
    private GameAction _initial;
    private Throwable _error;
    private boolean _rts = System.getProperty("tower.time", "turn").equals("real");


    public TimeStream(Game g, InputSource input, Narrative n, GameAction initial) {
        super("timestream");
        _g = g;
        _input = input;
        _n = n;
        _initial = initial;
    }

    public Throwable getError() {
        return _error;
    }

    public void run() {
        if(_initial!=null) {
            try {
                _input.nextKey();
                EventQueue.getEventQueue().postback(_initial);
            }
            catch(InputInterruptedException e) {
            }
        }
        try {
            N.set(_n, _g.getPlayer());
            GameAction a = null;
            while(!isInterrupted()) {
                if(a==null||a.isRecordable()) {
                    _input.checkpoint();
                }
                if(_rts) {
                    a = _input.nextAction(500);
                }
                else {
                    a = _input.nextAction();
                }
                if(a==null&&!_rts) {
                    continue;
                }
                _g.tick(a);
            }
        }
        catch(QuitException expected) {
        }
        catch(InputInterruptedException e) {
        }
        catch(Throwable t) {
            _error = t;
            t.printStackTrace();
            _n.print((NHBot)null, "You shift your weight and the Tower collapses! Ah, such is life.");
            _n.print((NHBot)null, "Epitaph: "+t.getClass().getName()+": "+t.getMessage());
            try {
                _n.quit("Killed by a collapsing tower.", false);
            }
            catch(QuitException expected) {
            }
        }
    }
}
