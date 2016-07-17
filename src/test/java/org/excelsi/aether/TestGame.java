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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;


public class TestGame extends junit.framework.TestCase {
    public void testAscendDescend() {
        Game g = new GetMeats();
        final StringBuilder events = new StringBuilder();
        g.addListener(new GameListener() {
            public void ascended(Game g) {
                events.append("up");
            }

            public void descended(Game g) {
                events.append("down");
            }
        });
        g.init();
        g.tick(null);
        g.setLevel(1+g.getLevel());
        g.setLevel(g.getLevel()-1);
        assertEquals("didn't get events", "updown", events.toString());
    }

    public void testTime() throws InterruptedException {
        Game g = new GetMeats();
        TimeStream t = new TimeStream(g, new TInputSource(), N.narrative(), null);
        try {
            // this will fail, because there are no levels
            t.start();
            Thread.sleep(2000);
            assertFalse("didn't error out", t.isAlive());
        }
        finally {
            t.interrupt();
        }
    }

    private static class TInputSource implements InputSource {
        public String nextKey() {
            return null;
        }

        public boolean isPlayback() {
            return false;
        }

        public void playback() {
        }

        public void checkpoint() {
        }

        public GameAction nextAction() {
            return null;
        }

        public GameAction nextAction(long timeout) {
            return null;
        }

        public GameAction actionFor(String key) {
            return null;
        }
    }

    private static class GetMeats extends Game {
        protected Level createLevel(int floor) {
            Level l = new Level(LEVEL_WIDTH, LEVEL_HEIGHT);
            l.setFloor(floor);
            MSpace s = null;
            if(getPlayer()!=null) {
                s = getPlayer().getEnvironment().getMSpace();
            }
            else {
                setPlayer(new Patsy());
            }
            new SectionLevelGenerator(LEVEL_WIDTH, LEVEL_HEIGHT).
                generate(l, (MatrixMSpace) s);
            l.findRandomEmptySpace().setOccupant(getPlayer());
            return l;
        }
    }
}
