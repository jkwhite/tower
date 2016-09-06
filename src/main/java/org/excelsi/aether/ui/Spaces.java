package org.excelsi.aether.ui;


import org.excelsi.aether.Item;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;


public class Spaces {
    public static final Spatial createItem(final SceneContext c, final Item item) {
        final Spatial s = c.getNodeFactory().createNode(item.getId(), item);
        return s;
    }

    public static final Vector3f translation(final MSpace ms) {
        final MatrixMSpace mms = (MatrixMSpace) ms;
        return new Vector3f(UIConstants.HORIZ_RATIO*mms.getI(), 0.0f, UIConstants.VERT_RATIO*mms.getJ());
    }

    public static final Spatial translate(final MSpace ms, final Spatial s) {
        final MatrixMSpace mms = (MatrixMSpace) ms;
        s.setLocalTranslation(UIConstants.HORIZ_RATIO*mms.getI(), 0.0f, UIConstants.VERT_RATIO*mms.getJ());
        return s;
    }

    public static final void attachItem(final Spatial space, final Spatial item) {
        ((Node)space).attachChild(item);
    }

    public static String format(final String c) {
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
