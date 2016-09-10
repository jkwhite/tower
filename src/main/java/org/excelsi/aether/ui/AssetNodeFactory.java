package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.LodControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.export.binary.BinaryImporter;

import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Rand;
import org.excelsi.matrix.Typed;
import java.io.File;


public abstract class AssetNodeFactory<E extends Typed> extends Enloggened implements NodeFactory<E> {
    public enum Display { single, scatter };

    private final AssetManager _assets;


    public AssetNodeFactory(final AssetManager assets) {
        _assets = assets;
    }

    protected final AssetManager assets() {
        return _assets;
    }

    protected Spatial loadModel(final String model, final String color, Display display) {
        final String asset = String.format("/model/%s.lod.mesh.xml", Spaces.format(model));
        final Spatial n = assets().loadModel(asset);
        Nodes.center(n);
        n.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override public void visit(final Spatial child) {
                //System.err.println("child: "+child+", class: "+child.getClass());
                if(child instanceof Geometry) {
                    final Geometry g = (Geometry) child;
                    final LodControl c = new LodControl();
                    g.addControl(c);
                    //g.setLodLevel(5);
                    //final Mesh m = g.getMesh();
                    //System.err.println("lod: "+m.getNumLodLevels());
                }
            }
        });
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
            case scatter:
                Node sc = new Node();
                for(int i=0;i<4;i++) {
                    final Spatial cl = n.clone();
                    cl.setLocalScale(0.5f);
                    cl.setLocalTranslation(0.5f*UIConstants.HORIZ_RATIO*Rand.om.nextFloat(), 0f, 0.5f*UIConstants.VERT_RATIO*Rand.om.nextFloat());
                    sc.attachChild(cl);
                }
                return sc;
            case single:
            default:
                return n;
        }
    }
}
