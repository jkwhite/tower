package org.excelsi.aether.ui;


import org.excelsi.aether.Item;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.NHSpace;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.Typed;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;


public class Spaces {
    public static Spatial createSpace(final SceneContext c, final Node lev, final NHSpace space) {
        if(!c.containsNode(space)) {
            final SpaceNode ms = (SpaceNode) c.getNodeFactory().createNode(space.getId(), space, c);
            Spaces.translate(space, ms);
            lev.attachChild(ms);
            c.addNode(ms);
            final Item[] items = space.getItem();
            if(items!=null) {
                for(int i=0;i<items.length;i++) {
                    ms.attachItem(c, items[i], i, false);
                }
            }
            return ms;
        }
        else {
            return c.getSpatial(space);
        }
    }

    public static Spatial createItem(final SceneContext c, final Item item) {
        final Spatial s = c.getNodeFactory().createNode(item.getId(), item, c);
        return s;
    }

    public static final Vector3f translation(final MSpace ms) {
        final MatrixMSpace mms = (MatrixMSpace) ms;
        return new Vector3f(UIConstants.HORIZ_RATIO*mms.getI(), UIConstants.HEIGHT_RATIO*((NHSpace)ms).getAltitude(), UIConstants.VERT_RATIO*mms.getJ());
    }

    public static final Spatial translate(final MSpace ms, final Spatial s) {
        final MatrixMSpace mms = (MatrixMSpace) ms;
        //s.setLocalTranslation(UIConstants.HORIZ_RATIO*mms.getI(), 0.0f, UIConstants.VERT_RATIO*mms.getJ());
        s.setLocalTranslation(UIConstants.HORIZ_RATIO*mms.getI(), UIConstants.HEIGHT_RATIO*((NHSpace)ms).getAltitude(), UIConstants.VERT_RATIO*mms.getJ());
        //if(((NHSpace)ms).getAltitude()!=0) System.err.println("alt: "+((NHSpace)ms).getAltitude());
        return s;
    }

    public static final void attachItem(final Spatial space, final Spatial item) {
        ((Node)space).attachChild(item);
    }

    public static Node findLevel(final SceneContext c, final NHBot b) {
        final Typed t = b.getEnvironment().getSpace().getContainer();
        return c.getNode(t.getId());
    }

    public static String format(final String c) {
        StringBuilder b = new StringBuilder();
        for(int i=0;i<c.length();i++) {
            b.append(internalFormat(""+c.charAt(i)));
        }
        return b.toString();
    }

    private static String internalFormat(final String c) {
        switch(c) {
            case "@":
                return "atsign";
            case "-":
                return "dash";
            case "&":
                return "ampersand";
            case ":":
                return "colon";
            case "!":
                return "bang";
            case "=":
                return "equals";
            case ")":
                return "rparen";
            case "(":
                return "lparen";
            case "]":
                return "rbrace";
            case "[":
                return "lbrace";
            case "%":
                return "percent";
            case "?":
                return "qmark";
            case ",":
                return "comma";
            case "*":
                return "asterisk";
            case "$":
                return "string";
            case "/":
                return "slash";
            case "+":
                return "plus";
            case ".":
                return "dot";
            case "#":
                return "hash";
            case "|":
                return "pipe";
            case "<":
                return "lessthan";
            case ">":
                return "greaterthan";
            case "~":
                return "tilde";
            case "^":
                return "caret";
            default:
                if(Character.isUpperCase(c.charAt(0))) {
                    return "_"+c.toLowerCase();
                }
                else {
                    return c;
                }
        }
    }

    private Spaces() {}
}
