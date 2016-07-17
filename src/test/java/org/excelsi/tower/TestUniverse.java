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
import java.io.File;
import java.net.MalformedURLException;
import java.io.IOException;


public class TestUniverse extends junit.framework.TestCase {
    public void testRun() throws MalformedURLException, IOException {
        Universe u = new Universe();
        Universe.setUniverse(u);
        // TODO: how to get directory?
        String cwd = System.getProperty("user.dir");
        cwd = cwd.substring(0, cwd.indexOf("/tower"));
        Data d = new Data(new java.net.URL[]{new File(cwd+"/tower/src/res").toURL()});
        YamlDataSource g = new YamlDataSource();
        g.populate(u, d);
        u.print();

        Game game = u.getGame();
        game.init();
        Patsy p = game.getPlayer();
        String prof = p.getProfession();

        DefaultNHBot.Forward a = new DefaultNHBot.Forward();
        a.setBot(p);
        a.perform();
        a.perform();
        a.perform();
        DefaultNHBot.Pickup pick = new DefaultNHBot.Pickup();
        pick.setBot(p);
        pick.perform();
        assertFalse("did not choose: "+prof, prof.equals(p.getProfession()));

        p.getEnvironment().ascend(null);
        p.getEnvironment().descend(null);
        p.getEnvironment().descend(null);
    }
}
