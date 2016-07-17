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
import static org.excelsi.aether.Brain.*;


public class RoomborDaemon extends SearchDaemon {
    private Chemical _basic;
    private boolean _messages = true;
    private boolean _retain = true;


    public RoomborDaemon() {
        super(1);
    }

    public void setMessages(boolean m) {
        _messages = m;
    }

    public boolean isMessages() {
        return _messages;
    }

    public void setRetain(boolean retain) {
        _retain = retain;
    }

    public boolean isRetain() {
        return _retain;
    }

    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public void poll() {
        if(_messages&&Rand.d100(10)) {
            N.narrative().print(in.b, in.b+" hungers!");
        }
        super.poll();
    }

    protected boolean accept(Item i) {
        return true;
    }

    protected int strengthFor(Item i) {
        return 10;
    }

    private DefaultNHBot.Pickup _p = new DefaultNHBot.Pickup();
    protected void perform(Item i) {
        if(_retain) {
            _p.setBot(in.b);
            _p.setItem(i);
            _p.perform();
        }
        else {
            in.b.getEnvironment().getMSpace().destroy(i);
        }
        N.narrative().print(in.b, Grammar.start(in.b, "consume")+" "+Grammar.noun(i)+".");
    }

    public Chemical getChemical() {
        return _basic;
    }
}
