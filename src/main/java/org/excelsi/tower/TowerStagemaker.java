package org.excelsi.tower;


import java.util.ArrayList;
import java.util.List;

import org.excelsi.aether.BasicStageGenerator;
import org.excelsi.aether.LevelRecipe;
import org.excelsi.aether.Rand;
import org.excelsi.aether.Spacemaker;
import org.excelsi.aether.Stairs;
import org.excelsi.aether.Stage;
import org.excelsi.aether.Stagemaker;
import org.excelsi.aether.Skelevel;
import static org.excelsi.aether.Skelevel.Partition;
import static org.excelsi.aether.Skelevel.Layout;


public class TowerStagemaker implements Stagemaker {
    private List<LParts> _partitions = new ArrayList<LParts>();


    @Override public Stage createStage(final int ordinal) {
        ensureCapacity(ordinal);
        LevelRecipe r = createRecipe(ordinal, _partitions.get(ordinal));
        for(int i=0;i<100;i++) {
            try {
                return createStage(r);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("giving up after 100 tries to make a level");
    }

    private Stage createStage(final LevelRecipe r) {
        final Spacemaker sm = r.getOrdinal()==1?
            Spacemaker.expanse().and((r2,l)->{l.getSpace(l.width()/2,l.height()/2).replace(new Stairs(true));})
            :TowerLevelGenerator.spacemaker();
        final BasicStageGenerator g = new BasicStageGenerator(sm);
        return g.generate(r);
    }

    private LevelRecipe createRecipe(final int ordinal, final LParts lparts) {
        return new LevelRecipe()
            .name("Terra Obscura")
            .realm("Terra Obscura")
            .ordinal(ordinal)
            .width(20)
            .height(20)
            .random(Rand.om);
    }

    private void ensureCapacity(final int ordinal) {
        while(_partitions.size()<=1+ordinal) {
            int current = _partitions.size();
            int below = ordinal==1?1:_partitions.get(_partitions.size()-1).numAscending();
            int parts;
            ArrayList<Partition> ps = new ArrayList<Partition>();
            if(current==899) {
                ps.add(new Partition(0, 1));
            }
            else if(current>=900) {
                ps.add(new Partition(0, 0));
            }
            else {
                // always have at least one
                ps.add(new Partition(1, 1));
                if(current==24) {
                    ps.add(new Partition(1, 0));
                }
                else if(current==8||current==18||current==25||current==47||current==48||current==49) {
                    //ps.add(new Partition(1, 1));
                }
                else if(current>=26&&current<=28) {
                    ps.add(new Partition(current==28?0:1, 1));
                }
                else if(current==69||current==70||current==76) {
                    //ps.add(new Partition(1, 0));
                }
                else {
                    parts = below;
                    while(ps.size()<parts) {
                        ps.add(new Partition(1, 1));
                    }
                    if(Rand.d100(20)&&parts<3) {
                        ps.add(new Partition(1, 0));
                    }
                    int r = Rand.d100();
                    if(r<20&&parts<4) {
                        ps.get(Rand.om.nextInt(ps.size())).incAsc();
                    }
                    else if(r<40&&parts>1) {
                        if(Rand.d100(60)) {
                            ps.get(ps.size()-1).decAsc();
                        }
                        else {
                            ps.get(0).incDesc();
                            ps.remove(ps.size()-1);
                        }
                    }
                }
            }
            LParts lp = new LParts();
            lp.ps = (Partition[]) ps.toArray(new Partition[ps.size()]);
            _partitions.add(lp);
        }
    }

    private static class LParts implements java.io.Serializable {
        public Partition[] ps;

        public int numParts() {
            return ps.length;
        }

        public int numAscending() {
            int n = 0;
            for(Partition p:ps) {
                n += p.ascending();
            }
            return n;
        }

        public int numDescending() {
            int n = 0;
            for(Partition p:ps) {
                n += p.descending();
            }
            return n;
        }
    }
}
