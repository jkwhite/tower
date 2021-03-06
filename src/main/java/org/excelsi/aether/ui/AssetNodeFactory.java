package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.LodControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.math.FastMath;
import com.jme3.export.binary.BinaryImporter;

import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Architecture;
import org.excelsi.aether.Orientation;
import org.excelsi.aether.Rand;
import org.excelsi.matrix.Typed;
import java.io.File;


public abstract class AssetNodeFactory<E extends Typed> extends Enloggened implements NodeFactory<E> {
    //public enum Display { single, scatter };

    private final AssetManager _assets;


    public AssetNodeFactory(final AssetManager assets) {
        _assets = assets;
    }

    protected final AssetManager assets() {
        return _assets;
    }

    protected Spatial loadModel(final String model, final String color, Architecture display, final Orientation o) {
        if("".equals(model)) {
            return new Node();
        }
        final String asset = String.format("/model/%s.lod.mesh.xml", Spaces.format(model));
        final Spatial n = assets().loadModel(asset);
        Nodes.center(n);
        //Geometry fg = null;
        n.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override public void visit(final Spatial child) {
                //System.err.println("child: "+child+", class: "+child.getClass());
                if(child instanceof Geometry) {
                    final Geometry g = (Geometry) child;
                    //if(fg==null) {
                        //fg = g;
                    //}
                    final LodControl c = new LodControl();
                    g.addControl(c);
                    //g.setLodLevel(5);
                    //final Mesh m = g.getMesh();
                    //System.err.println("lod: "+m.getNumLodLevels());
                }
            }
        });
        switch(o) {
            case upright:
                n.setLocalRotation(new Quaternion(new float[]{FastMath.PI/2f, 0f, 0f}));
                break;
            default:
            case natural:
                break;
        }
        //Material mat = assets().loadMaterial("/material/m_gray.j3m");
        final String material = String.format("/material/%s.j3m", Materials.format(color));
        try {
            Material mat = assets().loadMaterial(material);
            n.setMaterial(mat);
        }
        catch(Exception e) {
            log().warn("failed loading '"+material+"': "+e, e);
            Material mat = assets().loadMaterial("/material/gray.j3m");
            n.setMaterial(mat);
        }
        //log().debug("loaded spatial "+n);
        switch(display) {
            case repeating:
                Node sc = new Node();
                for(int i=0;i<4;i++) {
                    final Spatial cl = n.clone();
                    cl.setLocalScale(0.4f);
                    cl.setLocalTranslation(0.3f*UIConstants.HORIZ_RATIO*Rand.om.nextFloat(), 0f, 0.3f*UIConstants.VERT_RATIO*Rand.om.nextFloat());
                    sc.attachChild(cl);
                }
                return sc;
            case random:
                Node nr = new Node();
                nr.setLocalRotation(new Quaternion(new float[]{0f, Rand.om.nextFloat()*FastMath.PI*2f, 0f}));
                n.setLocalTranslation(0.3f*UIConstants.HORIZ_RATIO*Rand.om.nextFloat(), 0f, 0.3f*UIConstants.VERT_RATIO*Rand.om.nextFloat());
                nr.attachChild(n);
                return nr;
            case structural:
            default:
                return n;
        }
    }
}
