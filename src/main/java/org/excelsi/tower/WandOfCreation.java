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
package org.excelsi.tower;


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.logging.Logger;


public class WandOfCreation extends Wand implements Useable {
    private Createable _create = new Ground();
    private int _power = 6;


    public void setStatus(Status s) {
        super.setStatus(s);
        if(_power>maxPower()) {
            _power = maxPower();
        }
    }

    public int score() {
        return 31415; // someone's churning the earth, someone's stirring the sky
    }

    public void invoke(NHBot b) {
        if(discharge(b)) {
            Direction chosen = getDirection();
            if(chosen==null) {
                chosen = N.narrative().direct(b, "Which direction?");
            }
            create(b, chosen);
        }
    }

    public void use(NHBot b) {
        Createable create = Build.selectType(b, "Where do you set the wand's dial?");
        if(create==null) {
            N.narrative().print(b, "This wand doesn't have that setting.");
            throw new ActionCancelledException();
        }
        else {
            N.narrative().print(b, "Click!");
        }
        _create = create;
        int maxp = maxPower();
        for(;;) {
            String p = N.narrative().reply(b, "How high do you set the wand's power?");
            try {
                int power = Integer.parseInt(p);
                if(power>0&&power<=maxp) {
                    _power = power;
                    return;
                }
                if(N.narrative().confirm(b, "That's out of this wand's recommended operating bounds! Set anyway?")) {
                    _power = power;
                    return;
                }
                else {
                    break;
                }
            }
            catch(NumberFormatException e) {
                N.narrative().clear();
            }
        }
    }

    public int getFindRate() {
        return 0;
    }

    public boolean isDirectable() {
        return true;
    }

    public boolean isWishable() {
        return false;
    }

    public void create(final NHBot b, Direction d) {
        MSpace s = b.getEnvironment().getMSpace().creator();
        if(_power>maxPower()&&Rand.d100(101-(int)(b.getModifiedEmpathy()*((float)maxPower()/_power)))) {
            N.narrative().print(b, "Ker-Zap! The wand shorts!");
            N.narrative().more();
            // TODO: handle types other than NHSpace
            Transform t = new Transform() {
                public void transform(Matrix m, int i, int j) {
                    MSpace old = m.getSpace(i, j);
                    try {
                        if(old==null) {
                            m.setSpace((MatrixMSpace)_create.getClass().newInstance(), i, j);
                        }
                        else {
                            old.replace((MatrixMSpace)_create.getClass().newInstance());
                        }
                    }
                    catch(Exception e) {
                        Logger.global.severe(e.toString());
                    }
                }
            };
            b.getEnvironment().getMSpace().bloom(t, maxPower()/6);
            if(!b.getEnvironment().getMSpace().isWalkable()) {
                N.narrative().more();
                N.narrative().print(b, "Urk... that didn't go so well.");
                String st = b.getEnvironment().getMSpace().getClass().getName();
                st = st.substring(1+st.lastIndexOf(".")).toLowerCase();
                b.die("Replaced by "+Grammar.nonspecific(st));
            }
        }
        else {
            if(!(_create instanceof NHSpace)) {
                s = b.getEnvironment().getMSpace();
            }
            for(int i=0;s!=null&&i<_power;i++) {
                s = s.move(d);
                if(s!=null) {
                    if(!s.isReplaceable()) {
                        break;
                    }
                    try {
                        if(_create instanceof NHSpace) {
                            s = s.replace((NHSpace)_create.getClass().newInstance());
                        }
                        else {
                            ((NHSpace)s).addParasite((Parasite)_create.getClass().newInstance());
                        }
                    }
                    catch(Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    private int maxPower() {
        int maxp = 33;
        switch(getStatus()) {
            case blessed:
                maxp = 100;
                break;
            case cursed:
                maxp = 6;
                break;
        }
        return maxp;
    }
}
