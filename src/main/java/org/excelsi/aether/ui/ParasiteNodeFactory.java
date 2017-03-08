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
import com.jme3.export.binary.BinaryImporter;
import com.jme3.scene.SceneGraphVisitor;
import jme3tools.optimize.LodGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.Parasite;
import org.excelsi.aether.Rand;
import org.excelsi.aether.Orientation;
import java.io.File;


public class ParasiteNodeFactory extends AssetNodeFactory<Parasite> {
    private static final Logger LOG = LoggerFactory.getLogger(SpaceNodeFactory.class);


    public ParasiteNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final Parasite p, final SceneContext c) {
        try {
            final Spatial n = loadModel(p.getModel(), p.getColor(), p.getArchitecture(), Orientation.upright);
            float scale = 1f;
            switch(p.getSize()) {
                case tiny:
                    scale = 0.4f;
                    break;
                case small:
                    scale = 0.7f;
                    break;
                case medium:
                default:
                    break;
                case large:
                    scale = 1.3f;
                    break;
                case huge:
                    scale = 2.0f;
                    break;
            }
            n.setLocalScale(3.0f*scale);
            Nodes.centerAbove(n);
            final Node par = new Node(name);
            par.attachChild(n);
            return par;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new Node("blot");
    }
}
