package org.excelsi.aether;


import java.util.Collection;
import java.util.List;


public final class D {
    public static boolean intersects(final List l1, final List l2) {
        if(l1==null || l2==null) {
            return false;
        }
        for(int i=0;i<l1.size();i++) {
            if(l2.contains(l1.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static float distance(final int x1, final int y1, final int x2, final int y2) {
        return (float)Math.hypot(x1-x2, y1-y2);
    }

    public static int distance2(final int x1, final int y1, final int x2, final int y2) {
        return (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    }

    private D() {}
}
