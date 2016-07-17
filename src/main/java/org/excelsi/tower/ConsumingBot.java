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


/**
 * A bot that eats food.
 */
public class ConsumingBot extends BasicBot {
    private static final long serialVersionUID = 1L;


    public ConsumingBot() {
    }

    /*
    public boolean intercept(Attack a) {
        Runnable r = getAI().react(this, null, a);
        if(r!=null) {
            r.run();
        }
        return r!=null;
    }

    public void setEventSource(EventSource e) {
        super.setEventSource(e);
        if(getTemperament()==Temperament.hungry) {
            getAI().addDaemon(new Chaser(e));
            getAI().addDaemon(new Challenger(e));
        }
    }
    */
}
