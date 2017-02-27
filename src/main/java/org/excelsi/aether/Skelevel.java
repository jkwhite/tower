package org.excelsi.aether;


/**
 * Contains enough structural information about a level
 * to infer properties of preceding and succeeding levels.
 */
public class Skelevel {
    private int _w;
    private int _h;
    private Partition[] _partitions;


    public Skelevel(int w, int h, Partition... partitions) {
        _w = w;
        _h = h;
        _partitions = partitions;
    }

    public LevelRecipe createRecipe() {
        return new LevelRecipe().width(_w).height(_h).partitions(_partitions.length);
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
