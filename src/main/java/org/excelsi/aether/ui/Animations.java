package org.excelsi.aether.ui;


import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.math.Spline.SplineType;


public class Animations {
    public static void move(final Spatial s, final Vector3f from, final Vector3f to, final float time) {
        final MotionPath p = new MotionPath();
        p.addWayPoint(from);
        p.addWayPoint(from.add(0f, 1f, 0f));
        p.addWayPoint(to.add(0f, 1f, 0f));
        p.addWayPoint(to);
        p.setPathSplineType(SplineType.Bezier);
        final MotionEvent e = new MotionEvent(s, p, time);
        e.play();
    }

    public static void lunge(final Spatial s, final Vector3f to, final float time) {
        final MotionPath p = new MotionPath();
        p.addWayPoint(UIConstants.ZERO);
        p.addWayPoint(to);
        p.addWayPoint(UIConstants.ZERO);
        p.setPathSplineType(SplineType.Linear);
        final MotionEvent e = new MotionEvent(s, p, time);
        e.play();
    }

    private Animations() {}
}
