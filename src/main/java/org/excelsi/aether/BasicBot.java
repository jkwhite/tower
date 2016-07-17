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


import java.util.Iterator;
import java.util.Random;

import org.excelsi.matrix.*;
import java.util.EnumSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import static org.excelsi.aether.Brain.*;


/**
 * A bot that can attack and defend itself.
 */
public class BasicBot extends NPC {
    private static final long serialVersionUID = 1L;


    public BasicBot() {
        setAi(new Brain(new Daemon[]{
            new AttackDaemon(),
            new WanderDaemon(),
            new FollowDaemon(),
            new SurvivalDaemon(),
            new FleeDaemon()
        }));
    }
}
