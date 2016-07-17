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


import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.*;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.logging.Logger;
import com.jme.scene.state.RenderState;


public class PatsyNodeFactory extends DefaultNHBotNodeFactory {
    private NHEnvironmentAdapter _last = null;
    private NHBot _lastBot = null;

    private static float quadratic(float c) {
        //return .04f-.035f*Math.min(c,15)/15f;
        //return .08f-.035f*Math.min(c,15)/15f;
        //return 0.004f;
        //return 0.005f;

        //return 0.009f-(float)Math.min(0.008,c/3000f);

        c = (float) Math.sqrt(c);
        //return 0.069f-(float)Math.min(0.065,c/140f);
        return 0.069f-(float)Math.min(0.065,c/70f);

        //return 0.008f - .001f*Math.min(c,10)/10f;
        //return 0.1f - 0.1f*Math.min(c,10)/10f + 0.004f;
    }

    private static float linear(float c) {
        //return 0.01f-0.009f*Math.min(c,20)/20f;
        //return 0.2f-0.009f*Math.min(c,20)/20f;
        //return 0.005f;
        //return 0.006f;

        //return 0.019f-(float)Math.min(0.018,c/3000f);

        return 0.069f-(float)Math.min(0.065,c/140f);

        //return 0.005f - 0.001f*Math.min(c,20)/20f;
        //return 0.10f - 0.10f*Math.min(c,10)/10f + 0.005f;
    }

    public Light createSpotlight(Patsy patsy) {
        final SpotLight p = new SpotLight();
        p.setDirection(new Vector3f(1f, 1f, 1f));
        p.setAngle(60f);
        float min = 20f;
        float max = 80f;
        float can = patsy.getModifiedCandela();
        System.err.println("CANDELA: "+can);
        p.setAngle(Math.min(max, min+4*can));
        //p.setExponent(50f);
        p.setEnabled(true);
        ColorRGBA add = new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f);
        float[] cc = patsy.getModifiedCandelaColor();
        add = add.mult(new ColorRGBA(cc[0], cc[1], cc[2], cc[3]));
        p.setDiffuse(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f).add(add));
        p.setSpecular(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        p.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        p.setAttenuate(false);
        p.setQuadratic(0.02f);
        float quad = quadratic(patsy.getModifiedCandela());
        float lin = linear(patsy.getModifiedCandela());
        System.err.println("Q: "+quad);
        System.err.println("L: "+lin);
        p.setQuadratic(quad);
        p.setLinear(lin);
        p.setConstant(1f);
        //p.setShadowCaster(true);
        return p;
    }

    public static Light createPointlight(Patsy patsy) {
        final PointLight p = new PointLight();
        //p.setExponent(50f);
        p.setEnabled(true);
        ColorRGBA add = new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f);
        float[] cc = patsy.getModifiedCandelaColor();
        /*
        add = add.mult(new ColorRGBA(cc[0], cc[1], cc[2], cc[3]));
        p.setDiffuse(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f).add(add));
        p.setSpecular(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        p.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        */
        ColorRGBA mu = new ColorRGBA(cc[0], cc[1], cc[2], cc[3]);
        p.setDiffuse(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f).add(add).mult(mu));
        p.setSpecular(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f).mult(mu));
        p.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f).mult(mu));

        p.setAttenuate(true);
        p.setQuadratic(0.02f);
        float quad = quadratic(patsy.getModifiedCandela());
        //float lin = linear(patsy.getModifiedCandela());
        float lin = 0f;
        //quad = 0.0f;
        //quad = 0;
        //lin = 0;
        float constant = 0f;
        //System.err.println("Q: "+quad);
        //System.err.println("L: "+lin);
        p.setQuadratic(quad);
        p.setLinear(lin);
        p.setConstant(constant);
        //p.setShadowCaster(true);
        return p;
    }

    private LightNode createLightNode(Patsy patsy, Node n, Node parent) {
        LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        ls.setTwoSidedLighting(true);
        Light p = createPointlight(patsy);
        //Light p = createSpotlight(patsy);
        LightNode ln = new LightNode("lamp", ls);
        ln.setLocalTranslation(new Vector3f(0.0f, 15.0f, 1.0f).mult(0.7f));
        //ln.setLocalTranslation(new Vector3f(0f, 12f, 0f));
        //ln.setLocalRotation(new Quaternion(new float[]{FastMath.PI/2f, 0f, 0f}));
        ln.setLight(p);
        //parent.setRenderState(ls);
        n.setRenderState(ls);

        ln.setTarget(parent);
        n.attachChild(ln);

        p.setEnabled(!patsy.isBlind());
        return ln;
    }

    public Node createNode(String name, Object s, final Node parent) {
        final Node n = super.createNode(name, s, parent);
        LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        ls.setTwoSidedLighting(true);

        final Patsy patsy = (Patsy) s;
        final LightNode ln = createLightNode(patsy, n, parent);

        if(_last!=null) {
            try {
                EventQueue.getEventQueue().removeNHEnvironmentListener(_lastBot, _last);
            }
            catch(IllegalArgumentException e) {
                Logger.global.severe(e.getMessage());
            }
        }
        _last = new NHEnvironmentAdapter() {
            public void died(Bot b, MSource s) {
                for(int i=0;i<n.getChildren().size();i++) {
                    Spatial test = n.getChild(i);
                    if(test instanceof LightNode) {
                        ((LightNode)test).getLight().setEnabled(false);
                        break;
                    }
                }
                parent.updateRenderState();
            }

            public void itemModified(NHBot b, Item unused) {
                for(int i=0;i<n.getChildren().size();i++) {
                    Spatial test = n.getChild(i);
                    if(test instanceof LightNode) {
                        n.detachChild(test);
                        break;
                    }
                }
                LightNode ln = createLightNode((Patsy)b, n, parent);
                n.attachChild(ln);
                parent.updateRenderState();
            }

            public void equipped(NHBot b, Item unused) {
                itemModified(b, null);
            }

            public void unequipped(NHBot b, Item unused) {
                itemModified(b, null);
            }

            public void attributeChanged(Bot b, String attr, Object old) {
                if("blind".equals(attr)) {
                    boolean blind = ((Boolean)old).booleanValue();
                    for(int i=0;i<n.getChildren().size();i++) {
                        Spatial test = n.getChild(i);
                        if(test instanceof LightNode) {
                            LightNode l = (LightNode) test;
                            l.getLight().setEnabled(blind);
                            break;
                        }
                    }
                }
                else if("candela".equals(attr)) {
                    /*
                    if(!((NHBot)b).isBlind()) {
                        for(int i=0;i<n.getChildren().size();i++) {
                            Spatial test = n.getChild(i);
                            if(test instanceof LightNode) {
                                n.detachChild(test);
                                break;
                            }
                        }
                        LightNode ln = createLightNode((Patsy)b, n, parent);
                        n.attachChild(ln);
                        parent.updateRenderState();
                    }
                    */
                    //itemModified((NHBot)b, null);
                }
            }
        };
        _lastBot = (NHBot) s;

        EventQueue.getEventQueue().addNHEnvironmentListener((NHBot)s, _last);
        return n;
    }
}
