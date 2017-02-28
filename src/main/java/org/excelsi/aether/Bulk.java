package org.excelsi.aether;


import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public final class Bulk {
    private final Map<Integer,Stage> _levels = new HashMap<>();
    private Stagemaker _s = Stagemaker.expanse();


    public Bulk() {
    }

    public void setStagemaker(final Stagemaker s) {
        _s = s;
    }

    public Stagemaker getStagemaker() {
        return _s;
    }

    //public void addLevel(Stage level) {
        //_levels.put(level.getOrdinal(), level);
    //}

    public Stage findLevel(final int ordinal) {
        Stage s = _levels.get(ordinal);
        if(s==null) {
            s = createLevel(ordinal);
            _levels.put(ordinal, s);
        }
        return s;
    }

    private Stage createLevel(int ordinal) {
        return _s.createStage(ordinal);
    }
}
