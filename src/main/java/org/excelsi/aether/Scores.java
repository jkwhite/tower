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
package org.excelsi.aether;


import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;


public class Scores implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private static Boolean _record = null;
    private List<Score> _score;
    private int _max;


    public static void setRecord(boolean record) {
        if(_record==null) {
            _record = new Boolean(record);
        }
        else {
            throw new Error("value for score record is already set to "+record);
        }
    }

    public Scores(int max) {
        if(max<1) {
            throw new IllegalArgumentException("max<1");
        }
        setMax(max);
        _score = new ArrayList<Score>(max);
    }

    public void setMax(int max) {
        _max = max;
    }

    public int getMax() {
        return _max;
    }

    public int insert(NHBot b, String cause) {
        int place = -1;
        if(_record!=null&&_record.booleanValue()) {
            Score s = new Score(b, cause);
            int i = 0;
            while(i<_score.size()&&_score.get(i).compareTo(s)>=0) {
                i++;
            }
            place = i;
            if(i<_score.size()) {
                _score.add(i, s);
            }
            else {
                _score.add(s);
            }
            if(_score.size()>_max) {
                _score.remove(_score.size()-1);
            }
            if(place==_score.size()) {
                place = -1;
            }
        }
        return place;
    }

    public List<Score> getScores() {
        return new ArrayList<Score>(_score);
    }

    public String toString() {
        StringWriter b = new StringWriter();
        PrintWriter p = new PrintWriter(b);
        int rank = 0;
        for(Score s:_score) {
            p.printf("%3s %s\n", rank, s.toString());
        }
        return b.toString();
    }

    public static class Score implements java.io.Serializable, Comparable<Score> {
        private String _name;
        private String _prof;
        private int _score;
        private String _floor;
        private String _maxFloor;
        private String _areaName;
        private String _cause;


        public Score(NHBot b, String cause) {
            _name = b.getName();
            _prof = b.getProfession();
            _score = b.score();
            if(((Patsy)b).getLevel()!=null) {
                _floor = ((Patsy)b).getLevel().getDisplayedFloor();
                _maxFloor = ""+((Patsy)b).getMaxLevel();
                _areaName = ((Patsy)b).getLevel().getName();
            }
            _cause = cause;
        }

        public int compareTo(Score s) {
            return _score-s._score;
        }

        public int getScore() {
            return _score;
        }

        public String getName() {
            return _name;
        }

        public String getProfession() {
            return _prof;
        }

        public String getCause() {
            return _cause;
        }

        public String getFloor() {
            return _floor;
        }

        public String getMaxFloor() {
            return _maxFloor;
        }

        public String getAreaName() {
            return _areaName;
        }

        public String toString() {
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter(s);
            p.printf("%-10s %s the %-12s %50s %2s", _score, _name, _prof, _cause, _floor);
            return s.toString();
        }
    }
}
