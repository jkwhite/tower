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


public class Rewiring extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.disks, 0));
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(final NHBot b) {
        boolean temporary = true;
        switch(getStatus()) {
            case blessed:
                if(!N.narrative().confirm(b, "Do you want to rewire yourself?")) {
                    throw new ActionCancelledException();
                }
                break;
            case cursed:
                temporary = false;
                break;
        }

        final String[] STATS = new String[]{"Strength", "Agility", "Constitution"};
        final int[] ORIG = new int[STATS.length];
        for(int i=0;i<STATS.length;i++) {
            try {
                ORIG[i] = ((Integer)b.getClass().getMethod("get"+STATS[i], new Class[0]).invoke(b)).intValue();
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }
        final int[] mod = new int[ORIG.length];
        mod[0] = ORIG[ORIG.length-1];
        for(int i=1;i<mod.length;i++) {
            mod[i] = ORIG[i-1];
        }
        for(int i=0;i<mod.length;i++) {
            try {
                b.getClass().getMethod("set"+STATS[i], Integer.TYPE).invoke(b, mod[i]);
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }
        N.narrative().print(b, Grammar.start(b, "feel")+" like a different person.");
        if(temporary) {
            b.addAffliction(new TemporalAffliction("one year twenty years forty years fifty years", Affliction.Onset.tick, Rand.om.nextInt(30)+20) {
                protected void finish() {
                    final int[] ORIG2 = new int[STATS.length];
                    for(int i=0;i<STATS.length;i++) {
                        try {
                            ORIG2[i] = ((Integer)getBot().getClass().getMethod("get"+STATS[i], new Class[0]).invoke(getBot())).intValue();
                        }
                        catch(Exception e) {
                            throw new Error(e);
                        }
                    }
                    for(int i=0;i<mod.length;i++) {
                        try {
                            getBot().getClass().getMethod("set"+STATS[i], Integer.TYPE).invoke(getBot(), ORIG2[i]);
                        }
                        catch(Exception e) {
                            throw new Error(e);
                        }
                    }
                    N.narrative().print(b, Grammar.start(getBot(), "feel")+" like "+Grammar.possessive(getBot())+"self again.");
                }

                protected void afflict() {
                }

                public String getStatus() {
                    return null;
                }

                public String getExcuse() {
                    return null;
                }
            });
        }
        return false;
    }

    public int getOccurrence() {
        return 20;
    }
}
