package org.excelsi.aether.ui;


import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;

import com.jmex.bui.*;
import com.jmex.bui.event.*;
import com.jmex.bui.background.*;
import com.jmex.bui.layout.*;
import com.jmex.bui.util.Dimension;

import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


public class ScoreContainer extends BContainer {
    private Scores _scores;
    private int _rank;
    private Scores.Score _last;


    public ScoreContainer(Scores scores, int rank, Scores.Score last) {
        super(new TableLayout(6,5,20));
        _scores = scores;
        _rank = rank;
        _last = last;

        BContainer p = this;
        p.add(new BLabel(" "), "sellabel");
        p.add(new BLabel("Rank", "sellabel"));
        p.add(new BLabel("Score", "sellabel"));
        p.add(new BLabel("Name", "sellabel"));
        p.add(new BLabel(" ", "sellabel"));
        p.add(new BLabel("Level", "sellabel"));
        int modrank = _rank;
        List<Scores.Score> score = _scores.getScores();
        int orig = score.size();
        if(rank==-1&&_last!=null) {
            modrank = score.size();
            score.add(_last);
        }
        for(int i=0;i<score.size();i++) {
            Scores.Score s = score.get(i);
            String r = i<orig?((1+i)+""):" ";
            String sel = " ";
            String lab = "";
            if(modrank==i) {
                sel = "*";
                lab = "sel";
            }
            p.add(new BLabel(sel, lab+"label"));
            p.add(new BLabel(r, lab+"rlabel"));
            p.add(new BLabel(s.getScore()+"", lab+"rlabel"));
            p.add(new BLabel(s.getName()+" the "+s.getProfession(), lab+"label"));
            p.add(new BLabel(s.getCause(), lab+"label"));
            String floor = s.getFloor();
            if(!s.getMaxFloor().equals(floor)) {
                floor = floor+" (max "+s.getMaxFloor()+")";
            }
            floor = s.getAreaName()+", Lv. "+floor;
            p.add(new BLabel(floor, lab+"rlabel"));
        }
    }
}
