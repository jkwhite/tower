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

import org.excelsi.aether.Item;
import org.excelsi.aether.Rand;
import java.io.File;


public class ItemNodeFactory extends AssetNodeFactory<Item> {
    private static final Logger LOG = LoggerFactory.getLogger(SpaceNodeFactory.class);


    public ItemNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final Item i) {
        try {
            //final String model = String.format("/%s_%d_%d.blend", Spaces.format(s.getModel()), 6, 0);
            //final String model = "/Mesh2.mesh.xml";
            final Spatial n = loadModel(i.getModel(), "gray", Display.single);
            n.setLocalScale(2.0f);
            Nodes.center(n);
            final Node item = new Node();
            item.attachChild(n);
            return item;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new Node("blot");
    }
}
