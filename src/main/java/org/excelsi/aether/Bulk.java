package org.excelsi.aether;


import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public final class Bulk {
    private final Map<Integer,Stage> _levels = new HashMap<>();


    public void addLevel(Stage level) {
        _levels.put(level.getOrdinal(), level);
    }

    public Stage findLevel(final int ordinal) {
        return _levels.get(ordinal);
    }
}
