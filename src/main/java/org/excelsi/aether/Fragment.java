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


public interface Fragment extends java.io.Serializable {
    public enum GrammarType { adjective, nounPhrase, phrase };

    GrammarType getPartOfSpeech();
    String getText();
    String getName();
    void setIdentified(boolean identified);
    boolean isIdentified();
    void setClassIdentified(boolean identified);
    boolean isClassIdentified();
    int getOccurrence();
    Modifier getModifier();
    int getPowerModifier();
    void setOwner(Item i);
    Item getOwner();
    void apply(Fragment f);
    boolean intercepts(Attack a);
    Performable intercept(NHBot attacker, NHBot defender, Attack a);
}
