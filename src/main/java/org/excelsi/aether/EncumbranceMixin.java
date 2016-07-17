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


import org.excelsi.matrix.*;


public class EncumbranceMixin implements Mixin {
    private static final long serialVersionUID = 1L;

    public boolean match(Class c) {
        return NHBot.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        NHBot b = (NHBot) o;
        new Encumbrance(b);
    }

    public enum Degree {
        unencumbered,
        burdened,
        stressed,
        strained,
        crushed;

        public static Degree degreeFor(NHBot b, Item extra, Modifier m) {
            float weight = 0;
            for(Item i:b.getInventory().getItem()) {
                float w = i.getModifiedWeight()*i.getCount();
                if(b.isEquipped(i)) {
                    if(w>0) {
                        w /= 10f;
                    }
                }
                else {
                    if(w<0) {
                        w = 0;
                    }
                }
                weight += w;
            }
            if(extra!=null) {
                weight += extra.getModifiedWeight()*extra.getCount();
            }
            int degree = (int) (2*weight/Math.max(1, b.getModifiedConstitution()));
            if(m!=null) {
                m.setQuickness(degree>=0?-degree*15:0);
            }
            Degree nd = Degree.degreeFor(degree);
            return nd;
        }

        public static Degree degreeFor(int deg) {
            switch(deg) {
                case 0:
                    return unencumbered;
                case 1:
                    return burdened;
                case 2:
                    return stressed;
                case 3:
                    return strained;
                case 4:
                    return crushed;
                default:
                    return deg<0?unencumbered:crushed;
            }
        }
    }

    static class Encumbrance implements java.io.Serializable {
        private EncumbranceAffliction _a;


        public Encumbrance(final NHBot b) {
            final Modifier m = new Modifier();
            b.addModifier(m);
            b.getInventory().addContainerListener(new ContainerAdapter() {
                public void itemDropped(Container space, Item item, int idx, boolean incremented) {
                    recalculate(b, m);
                }

                public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder) {
                    recalculate(b, m);
                }

                public void itemAdded(Container space, Item item, int idx, boolean incremented) {
                    recalculate(b, m);
                }

                public void itemTaken(Container space, Item item, int idx) {
                    recalculate(b, m);
                }

                public void itemDestroyed(Container space, Item item, int idx) {
                    recalculate(b, m);
                }
            });
            b.addListener(new NHEnvironmentAdapter() {
                public void attributeChanged(Bot bot, String attribute, Object newValue) {
                    if("constitution".equals(attribute)) {
                        recalculate(b, m);
                    }
                }

                public void equipped(NHBot b, Item i) {
                    recalculate(b, m);
                }

                public void unequipped(NHBot b, Item i) {
                    recalculate(b, m);
                }
            });
            //_a = new EncumbranceAffliction();
            b.addAffliction(new EncumbranceAffliction());
        }

        private void recalculate(NHBot b, Modifier m) {
            Degree nd = Degree.degreeFor(b, null, m);
            ((EncumbranceAffliction)b.getAffliction("encumbrance")).setDegree(nd);
            /*
            float weight = 0;
            for(Item i:b.getInventory().getItem()) {
                weight += i.getModifiedWeight()*i.getCount();
            }
            int degree = (int) (2*weight/Math.max(1, b.getConstitution()));
            Degree nd = Degree.degreeFor(degree);
            m.setQuickness(-degree*15);
            ((EncumbranceAffliction)b.getAffliction("encumbrance")).setDegree(nd);
            */
        }
    }

    static final class EncumbranceAffliction extends Affliction {
        private Degree _degree = Degree.unencumbered;


        public EncumbranceAffliction() {
            super("encumbrance", Onset.tick);
        }

        public String getExcuse() {
            return null;
        }

        public void beset() {
            if(_degree==Degree.crushed) {
                if(!getBot().isDead()) {
                    getNarrative().print(getBot(), Grammar.startToBe(getBot())+" being crushed!");
                }
                getBot().setHp(Math.max(0, getBot().getHp()-5));
                if(getBot().getHp()==0) {
                    getBot().die("Crushed to death");
                }
            }
        }

        public void setDegree(Degree d) {
            if(_degree!=d) {
                if(!getBot().isDead()) {
                    N.narrative().print(getBot(), Grammar.startToBe(getBot())+" "+d+".");
                }
                _degree = d;
            }
        }

        public String getStatus() {
            if(_degree==Degree.unencumbered) {
                return null;
            }
            return Grammar.first(_degree.toString());
        }

        public void compound(Affliction a) {
        }
    }
}
