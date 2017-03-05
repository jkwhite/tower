package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Mesh;
import com.jme3.scene.control.LodControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.math.FastMath;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.scene.SceneGraphVisitor;
import jme3tools.optimize.LodGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Rand;
import org.excelsi.aether.Architecture;
import java.io.File;


public class SpaceNodeFactory extends AssetNodeFactory<NHSpace> {
    private static final Logger LOG = LoggerFactory.getLogger(SpaceNodeFactory.class);


    public SpaceNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final NHSpace s, final SceneContext sc) {
        if(!"".equals(s.getModel().trim())) {
            try {
                final Spatial n = loadModel(s.getModel(), s.getColor(), displayFor(s.getArchitecture()));
                switch(s.getOrientation()) {
                    case upright:
                        n.setLocalRotation(new Quaternion(new float[]{-FastMath.PI/2f, 0f, 0f}));
                        break;
                    default:
                    case natural:
                        break;
                }
                n.setLocalScale(3.0f);
                Nodes.centerBelow(n);
                final SpaceNode sp = new SpaceNode(s);
                sp.attachChild(n);
                return sp;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return new SpaceNode(s);
    }

    private static Spatial generateLod(final Geometry g) {
        final LodGenerator gen = new LodGenerator(g);
        return null;
    }

    private static Display displayFor(Architecture a) {
        switch(a) {
            case repeating:
                return Display.scatter;
            default:
            case structural:
                return Display.single;
        }
    }
}
