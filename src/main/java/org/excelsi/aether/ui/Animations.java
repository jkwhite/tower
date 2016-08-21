package org.excelsi.aether.ui;


import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.math.Spline.SplineType;


public class Animations {
    public static void move(final Spatial s, final Vector3f from, final Vector3f to) {
        final MotionPath p = new MotionPath();
        p.addWayPoint(from);
        p.addWayPoint(from.add(0f, 1f, 0f));
        p.addWayPoint(to.add(0f, 1f, 0f));
        p.addWayPoint(to);
        //final Vector3f mid = from.add(to).divideLocal(2f);
        //mid.y = 1f;
        //p.addWayPoint(mid);
        //p.addWayPoint(to);
        p.setPathSplineType(SplineType.Bezier);
        final MotionEvent e = new MotionEvent(s, p, 0.25f);
        e.play();
    }

    private Animations() {}
}
