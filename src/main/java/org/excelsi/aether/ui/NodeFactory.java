/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether.ui;


import com.jme.renderer.ColorRGBA;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.state.RenderState;

//import com.jmex.model.XMLparser.JmeBinaryReader;
//import com.jmex.model.XMLparser.XMLtoBinary;
import org.excelsi.jmex.model.*;

import java.io.*;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public abstract class NodeFactory implements Factory {
    /** cache of color mappings */
    static Map<String, ColorRGBA> _colors;
    /** cache of color mappings */
    private static String _font = System.getProperty("tower.font");
    /** cache of model class to node factory */
    private static Map<Class, NodeFactory> _factories = new HashMap<Class, NodeFactory>();
    /** whether or not damage models should be loaded */
    private static boolean _damage = true;
    /** whether or not LOD models should be loaded */
    private static boolean _lod = true;


    public static void setDamage(boolean damage) {
        _damage = damage;
    }

    public static void setLOD(boolean lod) {
        _lod = lod;
    }

    public abstract Node createNode(String name, Object o, Node parent);
    public abstract void lock(Node n);
    public void updateColor(Node n, Object o) {
        MaterialState ms = (MaterialState) n.getRenderState(RenderState.RS_MATERIAL);
        if(ms!=null) {
            String color = null;
            if(o instanceof NHBot) {
                color = ((NHBot)o).getColor();
            }
            else if(o instanceof NHSpace) {
                color = ((NHSpace)o).getColor();
            }
            else if(o instanceof Parasite) {
                color = ((Parasite)o).getColor();
            }
            colorize(color, ms, n);
            n.updateRenderState();
            return;
        }
        if(n.getChildren()!=null) {
            for(Spatial s:n.getChildren()) {
                if("wielded".equals(s.getName())||s.getName().startsWith("wear-")) {
                    continue;
                }
                if(s instanceof Node) {
                    updateColor((Node)s, o);
                }
            }
        }
    }

    public static final long totalCachedTypes() {
        return _models.size();
    }

    public static final long totalCachedSpatials() {
        long t = 0;
        for(Object o:_models.values()) {
            Node n = (Node) o;
            t += countSpatials(n);
        }
        return t;
    }

    public static long countSpatials(Spatial s) {
        long t = 1;
        if(s instanceof Node&&((Node)s).getChildren()!=null) {
            for(Spatial c:((Node)s).getChildren()) {
                t += countSpatials(c);
            }
        }
        return t;
    }

    public static void setFont(String font) {
        _font = font;
    }

    public static String getFont() {
        return _font;
    }

    public static ColorRGBA ambientColorFor(String name) {
        return colorFor(name).mult(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
    }

    public static ColorRGBA emissiveColorFor(String name) {
        //return colorFor(name).mult(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        return new ColorRGBA(0, 0, 0, 1);
        //return colorFor(name);
    }

    public static ColorRGBA colorFor(String name) {
        if(_colors == null) {
            _colors = new HashMap<String, ColorRGBA>();
            for(String k:Universe.getUniverse().getColormap().keySet()) {
                String v = Universe.getUniverse().getColormap().get(k);
                ColorRGBA conv;
                if(v.startsWith("#")) {
                    float[] rgba = new float[4];
                    for(int i=0;i<rgba.length;i++) {
                        rgba[i] = Integer.parseInt(v.substring(1+2*i, 1+2*i+2), 16)/255f;
                    }
                    conv = new ColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
                }
                else {
                    String[] dsotm = v.split(",");
                    conv = new ColorRGBA(Float.parseFloat(dsotm[0]),
                            Float.parseFloat(dsotm[1]),
                            Float.parseFloat(dsotm[2]),
                            Float.parseFloat(dsotm[3]));
                }
                _colors.put(k, conv);
            }
        }
        return _colors.get(name);
    }

    /**
     * Gets a UI factory for a model object. The factory class must be in a "ui" subpackage
     * and have the word "NodeFactory" appended to the classname. For example, the factory
     * for the class "org.excelsi.tower.Water" would be "org.excelsi.tower.ui.WaterNodeFactory".
     * If the model class is an inner class, the factory name must correspond to the outer
     * class name plus inner class name with no separating '$'. For example,
     * "org.excelsi.tower.Electromagnet$EMF" would have a factory named
     * "org.excelsi.tower.ui.ElectromagnetEMFNodeFactory".
     * <p/>
     * If no factory is found for the given class, each superclass is tried before giving
     * up. The discovered factory class must extend NodeFactory. 
     *
     * @param s object for which to find factory
     * @return factory or <code>null</code> if no factory is found
     */
    static NodeFactory getFactory(Object s) {
        try {
            Class c = s.getClass();
            NodeFactory nf = _factories.get(c);
            if(nf==null) {
                do {
                    String name = c.getName();
                    String cname = name.substring(1+name.lastIndexOf('.'));
                    cname = cname.replace("$", "");
                    //if(cname.indexOf('$')>=0) {
                        //cname = "."+cname.substring(1+cname.indexOf('$'));
                    //}
                    name = name.substring(0,name.lastIndexOf('.'))+".ui."+cname+"NodeFactory";
                    try {
                        Class fc = s.getClass().getClassLoader().loadClass(name);
                        nf = (NodeFactory) fc.newInstance();
                        _factories.put(s.getClass(), nf);
                        break;
                    }
                    catch(ClassNotFoundException e) {
                    }
                }
                while((c=c.getSuperclass())!=null);
            }
            if(nf==null) {
                throw new IllegalArgumentException("no factory for "+s.getClass());
            }
            return nf;
        }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static Map _models = new HashMap();

    /** 
     * Loads the specified model from resources. Models should use the Y axis
     * for "up" and the X axis for "forward". For "wall"-type models,
     * use a height of 2.0. Loaded models and their nodes
     * are cached, therefore it is ok to use this method as many times as necessary.
     * 
     * @param name name of model resource, ie <code>name</code>.md3
     * @return node containing model
     */
    static Node loadModel(NHBot bot) {
        String name = bot.getModel();
        String color = bot.getColor();
        //name = Universe.getUniverse().modelFor(name);
        //if(name==null) {
            //throw new IllegalArgumentException("no charmap for model '"+bot.getModel()+"'");
        //}
        Node node = null;
        Node parent = null;
        if(_damage) {
            Node[] dmgs = new Node[]{
                loadModel(name, null, 1f, 0),
                loadModel(name, null, 1f, 1),
                loadModel(name, null, 1f, 2),
            };
            SwitchNode damages = new SwitchNode("dmg");
            for(Node d:dmgs) {
                damages.attachChild(d);
            }
            damages.setActiveChild(0);
            node = damages;
            parent = new Node("weapon");
            parent.attachChild(node);
        }
        else {
            Node child = loadModel(name, null, 1f, -1);
            node = new Node("p");
            node.attachChild(child);
            parent = new Node("weapon");
            parent.attachChild(node);
        }
        if(color!=null) {
            MaterialState m = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            colorize(color, m, node);
        }
        return parent;
    }

    private static void colorize(String color, MaterialState m, Node node) {
        ColorRGBA c = colorFor(color);
        if(c == null) {
            throw new IllegalArgumentException("no such color '"+color+"'");
        }
        //m.setDiffuse(c);
        //m.setAmbient(c.mult(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
        m.setDiffuse(c.mult(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
        //m.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        m.setAmbient(c.mult(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f)));
        //m.setSpecular(c);
        //m.setSpecular(new ColorRGBA(1f, 1f, 1f, 1f));
        m.setSpecular(ColorRGBA.white);
        //m.setShininess(128f);
        //m.setShininess(1f);
        m.setShininess(3f);
        m.setEnabled(true);
        if(!SHOW_LOD) {
            node.setRenderState(m);
        }
    }

    public static Node loadModel(Item it, String nodeName) {
        String name = it.getModel();
        String color = it.getColor();
        Modifier md = it.getModifier();
        if(md!=null) {
            String mcolor = md.getColor();
            if(mcolor!=null) {
                color = mcolor;
            }
        }
        if(name==null) {
            throw new IllegalArgumentException("null model for '"+it.getName()+"'");
        }
        if(color==null) {
            throw new IllegalArgumentException("null color for '"+it.getName()+"'");
        }
        Node n = loadModel(name, color, it.getShininess());
        MaterialState m = (MaterialState) n.getRenderState(RenderState.RS_MATERIAL);
        //m.setShininess(it.getShininess());
        //m.setShininess(1f);
        n.setName(nodeName);
        n.setLocalScale(0.60f+(float)Math.log10(1+it.getSize()/4f));
        for(Fragment f:it.getFragments()) {
            String fname = f.getClass().getName();
            String cname = fname.substring(1+fname.lastIndexOf('.'));
            cname = cname.replace("$", "");
            fname = fname.substring(0,fname.lastIndexOf('.'))+".ui."+cname+"Modifier";
            try {
                Class fc = it.getClass().getClassLoader().loadClass(fname);
                UIModifier mod = (UIModifier) fc.newInstance();
                mod.modify(f, n);
            }
            catch(ClassNotFoundException e) {
                //Logger.global.fine(e.toString());
            }
            catch(Exception e) {
                Logger.global.severe(e.toString());
            }
        }
        return n;
    }

    public static Node loadModel(String name) {
        return loadModel(name, "gray", 1f);
    }

    private static final boolean SHOW_LOD = Boolean.getBoolean("tower.showlod");
    private static int[] _levels = null;

    private static synchronized int[] getLevels() {
        if(_levels==null) {
            //final int[] progression = {6, 4, 3, 2, 1, 0};
            final int[] progression = {6, 6, 4, 4, 3, 1};
            //final int[] progression = {1, 1, 1, 1, 1, 1};
            //final int[] progression = {6, 3, 2, 0};
            _levels = new int[progression.length];
            int detail = Integer.getInteger("tower.detail", 0);
            for(int i=0;i<progression.length;i++) {
                int d = Math.max(0, progression[i]-detail);
                if(d==5) d=4; // there is no 5
                _levels[i] = d;
            }
        }
        return _levels;
    }

    public static Node loadModel(String name, String color) {
        return loadModel(name, color, 1f, 0);
    }

    public static Node loadModel(String name, String color, float shininess) {
        return loadModel(name, color, shininess, 0);
    }

    static Node loadModel(String name, String color, float shininess, int damage) {
        return loadModel(name, color, shininess, damage, false);
    }

    static Node loadModel(final String model, String color, float shiny, int damage, boolean container) {
        String name = Universe.getUniverse().modelFor(model);
        if(name==null) {
            throw new IllegalArgumentException("no charmap for model '"+model+"'");
        }
        Node node = null;
        if(!_lod) {
            node = internalLoadModel(name);
            compact(node);
            return node;
        }

        Node[] nodes;
        int[] levs = getLevels();
        nodes = new Node[levs.length];
        for(int i=0;i<levs.length;i++) {
            nodes[i] = internalLoadModel(name+"_"+levs[i]+"_"+damage);
        }
        final int[] dists;
        // characters get higher res than architecture
        if(container) {
            dists = new int[]{13, 17, 24, 45, 60, 10000};
        }
        else {
            dists = new int[]{15, 20, 29, 55, 80, 10000};
        }
        DistanceSwitchModel dsm = new DistanceSwitchModel(levs.length);
        dsm.setModelDistance(0, 0, dists[0]);
        for(int i=1;i<levs.length;i++) {
            dsm.setModelDistance(i, dists[i-1], dists[i]);
        }

        Node lod = new NHDiscreteLodNode(model, dsm);
        for(Node d:nodes) {
            lod.attachChild(d);
        }
        compact(lod);
        //Node lod = nodes[0];
        Node n;
        if(container) {
            n = new NHSpaceNode("cp");
        }
        else {
            n = new Node("p");
        }
        if(color!=null) {
            String key = color+shiny;
            MaterialState mat = _mats.get(key);
            if(mat==null) {
                mat = createMaterial(color, shiny);
                _mats.put(key, mat);
            }
            if(!SHOW_LOD) {
                n.setRenderState(mat);
            }
        }
        n.attachChild(lod);
        return n;
    }
    private static Map<String,MaterialState> _mats = new HashMap<String,MaterialState>();

    public static MaterialState createMaterial(String initialColor, float shininess) {
        ColorRGBA c = colorFor(initialColor);
        if(c == null) {
            throw new IllegalArgumentException("no such color '"+initialColor+"'");
        }
        return createMaterial(c, shininess);
    }

    public static MaterialState createMaterial(ColorRGBA c, float shininess) {
        MaterialState m = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        m.setDiffuse(c.mult(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
        m.setAmbient(c.mult(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f)));
        m.setSpecular(ColorRGBA.white);
        m.setSpecular(c.mult(new ColorRGBA(2.0f, 2.0f, 2.0f, 1.0f)));
        m.setSpecular(new ColorRGBA(0.4f, 0.4f, 0.4f, 0.7f));
        m.setShininess(shininess);
        //m.setShininess(128f);
        m.setEnabled(true);
        return m;
    }

    static void compact(Spatial n) {
        n.setName("");
        if(n instanceof SharedMesh) {
            SharedMesh sm = (SharedMesh) n;
            sm.setDefaultColor(ColorRGBA.white);
        }
        if(n instanceof Node && ((Node)n).getChildren()!=null) {
            for(Spatial s:((Node)n).getChildren()) {
                compact(s);
            }
        }
    }

    static Node internalLoadModel(String name) {
        return internalLoadModel(name, null);
    }
    
    /** only enable for debugging purposes, very memory-intensive */
    private static final boolean _noShared = Boolean.getBoolean("tower.nosharednode");
    static Node internalLoadModel(String name, String color) {
        if(name==null) {
            throw new IllegalArgumentException("null name");
        }
        String key = name+"::"+color;
        Node cc = (Node) _models.get(key);
        if(cc == null||_noShared) {
            try {
                String res = _font==null?"models/"+name+".jme":"models/"+_font+"/"+name+".jme";
                URL objFile = Thread.currentThread().getContextClassLoader().getResource(res);
                if(objFile==null) {
                    Logger.global.warning("no ui for '"+res+"'");
                    cc = new Node("blot");
                    _models.put(key, cc);
                }
                else {
                    JmeBinaryReader jbr=new JmeBinaryReader();
                    jbr.setProperty("texurl",new File(".").toURL());
                    jbr.setProperty("bound","box");
                    Node r=jbr.loadBinaryFormat(new BufferedInputStream(objFile.openStream(), 65536));
                    Logger.global.fine("loaded model "+name);

                    if(color!=null) {
                        MaterialState m = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
                        ColorRGBA c = colorFor(color);
                        if(c == null) {
                            throw new IllegalArgumentException("no such color '"+color+"'");
                        }
                        m.setDiffuse(c.mult(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
                        m.setAmbient(c.mult(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f)));
                        m.setSpecular(ColorRGBA.white);
                        m.setShininess(80);
                        m.setEnabled(true);
                        r.setRenderState(m);
                    }

                    //r.lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
                    //r.lockBounds();
                    //r.lockShadows();
                    if(!_noShared) {
                        _models.put(key, r);
                    }
                    cc = r;
                }
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        Node n;
        if(!_noShared) {
            n = new SharedNode("s", cc);
            n.lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
            //n.lockBounds();
            n.lockShadows();
        }
        else {
            n = cc;
        }
        return n;
    }

    public static void batchConvert(File dir) {
        System.err.println("converting "+dir+"...");
        XMLtoBinary converter2 = new XMLtoBinary();
        for(File f:dir.listFiles(new java.io.FileFilter() { public boolean accept(File f) { return f.getName().endsWith(".jme.xml"); } })) {
            try {
                String out = f.toString();
                out = out.substring(0, out.lastIndexOf('.'));
                System.err.println(f+" => "+out);
                BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(out));
                converter2.sendXMLtoBinary(new BufferedInputStream(new FileInputStream(f)), buf);
                buf.close();
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        for(File f:dir.listFiles()) {
            if(f.isDirectory()) {
                batchConvert(f);
            }
        }
    }

    public static void main(String[] args) {
        batchConvert(new File(args[0]));
    }
}
