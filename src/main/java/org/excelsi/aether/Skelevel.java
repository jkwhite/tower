package org.excelsi.aether;


import java.util.ArrayList;
import java.util.List;


/**
 * Contains enough structural information about a level
 * to infer properties of preceding and succeeding levels.
 */
public class Skelevel {
    private final List<Partition> _partitions = new ArrayList<>();


    public Skelevel() {
    }

    public Skelevel(List<Partition> partitions) {
        _partitions.addAll(partitions);
    }

    public Skelevel partition(Partition p) {
        _partitions.add(p);
        return this;
    }

    public List<Partition> getPartitions() {
        return _partitions;
    }

    public int numParts() {
        return _partitions.size();
    }

    public int numAscending() {
        int n = 0;
        for(Partition p:_partitions) {
            n += p.ascending();
        }
        return n;
    }

    public int numDescending() {
        int n = 0;
        for(Partition p:_partitions) {
            n += p.descending();
        }
        return n;
    }

    @Override public String toString() {
        return "Skelevel::{partitions:"+_partitions+"}";
    }

    public static class Partition implements java.io.Serializable {
        private int _a;
        private int _d;


        public Partition(int ascending, int descending) {
            _a = ascending;
            _d = descending;
        }

        public int ascending() {
            return _a;
        }

        public int descending() {
            return _d;
        }

        public void incAsc() {
            _a++;
        }

        public void incDesc() {
            _d++;
        }

        public void decAsc() {
            _a--;
        }

        public void decDesc() {
            _d--;
        }

        @Override public String toString() {
            return "Partition::{a:"+_a+", d:"+_d+"}";
        }
    }

    public static class Pattern implements java.io.Serializable {
        private Layout[] _layouts;


        public Pattern(Layout... layouts) {
            _layouts = layouts;
        }

        public Layout[] layouts() {
            return _layouts;
        }
    }

    public static class Layout implements java.io.Serializable {
        private int[][] _layout;


        public Layout(int[]... layout) {
            _layout = layout;
        }

        public int[][] layout() {
            return _layout;
        }
    }
}
