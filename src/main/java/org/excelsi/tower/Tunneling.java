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


import java.util.List;
import java.util.ArrayList;
import org.excelsi.aether.*;
import org.excelsi.matrix.MSpace;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Comparator;


public class Tunneling extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.disks, 2));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(getStatus()==Status.cursed) {
            b.addAffliction(new TunnelingAffliction());
            N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "feel")+" oddly displaced.");
        }
        else {
            tunnel(b);
        }
        return false;
    }

    private void tunnel(NHBot b) {
        int dist = dose(400); //Rand.om.nextInt(200)+20;
        MSpace m = b.getEnvironment().getMSpace();
        final MSpace orig = m;
        float max = 0;
        HashSet<MSpace> visited = new HashSet<MSpace>();
        List<MSpace> bfs = new ArrayList<MSpace>();
        bfs.add(orig);
        while(--dist>0&&bfs.size()>0) {
            MSpace test = bfs.remove(0);
            visited.add(test);
            MSpace[] sur = test.surrounding();
            for(MSpace next:sur) {
                if(next!=null&&next.isReplaceable()&&!visited.contains(next)) {
                    if(true||bfs.size()<3||Rand.om.nextBoolean()) {
                        bfs.add(next);
                    }
                    else {
                        bfs.add(Rand.om.nextInt(bfs.size()-1), next);
                    }
                    visited.add(next);
                }
            }
        }
        MSpace[] coll = (MSpace[]) visited.toArray(new MSpace[visited.size()]);
        Arrays.sort(coll, new Comparator<MSpace>() {
            public boolean equals(Object o) {
                return true;
            }

            public int compare(MSpace m1, MSpace m2) {
                float d = m1.distance(orig) - m2.distance(orig);
                return d>0?-1:d==0?0:1;
            }
        });
        for(int i=0;i<coll.length;i++) {
            MSpace t = coll[i];
            if(t.isWalkable()&&t!=orig&&!t.isOccupied()) {
                b.getEnvironment().getMSpace().moveOccupant(t);
                return;
            }
        }
        N.narrative().print(b, Grammar.start(b)+" almost "+Grammar.conjugate(b, "lose")+" "+Grammar.possessive(b)+" balance.");
    }

    public int getOccurrence() {
        return 50;
    }

    /**
     * TunnelingAffliction contains the effect of inflicting a cursed tunneling.
     */
    private class TunnelingAffliction extends TemporalAffliction {
        public TunnelingAffliction() {
            super("tunneling", Onset.tick, Rand.om.nextInt(100)+40);
        }

        protected void afflict() {
            if(Rand.d100(10)) {
                MSpace s = getBot().getEnvironment().getMSpace();
                tunnel(getBot());
                N.narrative().print(getBot(), Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), "hiccup")+".");
            }
        }

        protected void finish() {
            N.narrative().print(getBot(), Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), "stop")+" hiccuping.");
        }

        public String getStatus() {
            return null;
        }

        public String getExcuse() {
            return "spatially unstuck";
        }
    }
}
