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


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Form implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Map<SlotType, List<Slot>> _sections = new HashMap<SlotType, List<Slot>>();
    private Slot[] _slots;
    private Armament _naturalWeapon;
    private Armament _naturalArmor;


    public Form(Armament naturalWeapon, Armament naturalArmor, Slot... sections) {
        /*
        if(naturalWeapon==null) {
            throw new IllegalArgumentException("naturalWeapon must be non-null");
        }
        */
        if(sections==null||sections.length==0) {
            throw new IllegalArgumentException("must have at least one section");
        }
        _naturalWeapon = naturalWeapon;
        _naturalArmor = naturalArmor;
        _slots = sections;
        int total = 0;
        for(Slot s:sections) {
            if(s.isOccupied()) {
                throw new IllegalArgumentException("cannot start with occupied slot "+s+": "+s.getOccupant());
            }
            total += s.getHitPercentage();
            List<Slot> ss = _sections.get(s.getSlotType());
            if(ss==null) {
                ss = new ArrayList<Slot>(2);
                _sections.put(s.getSlotType(), ss);
            }
            ss.add(s);
        }
        if(total!=100) {
            throw new IllegalArgumentException("slot hit percentage totals "+total+", must total 100");
        }
    }

    public EnergySource getSustenance() {
        return EnergySource.comestibles;
    }

    public Habitat getHabitat() {
        return Habitat.terrestrial;
    }

    public String getComplain() {
        return "whimper";
    }

    public String getShout() {
        return "roar";
    }

    public final Slot[] getSlots(SlotType type) {
        // avoid iterator, avoid gc
        ArrayList ss = new ArrayList(2);
        for(int i=0;i<_slots.length;i++) {
            Slot s = _slots[i];
            if(s.getSlotType()==type) {
                ss.add(s);
            }
        }
        return (Slot[]) ss.toArray(new Slot[ss.size()]);
    }

    /**
     * Tests if this form has actual hands, as opposed to
     * claws or hooves or something.
     *
     * @return <code>true</code> if we're dealing with an advanced creature
     */
    public boolean hasOpposableThumb() {
        for(Slot s:getSlots(SlotType.hand)) {
            if(s.getName().endsWith("hand")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSlot(SlotType type) {
        for(int i=0;i<_slots.length;i++) {
            Slot s = _slots[i];
            if(s.getSlotType()==type) {
                return true;
            }
        }
        return false;
    }

    public final Slot getRandomSlot() {
        int tot = 0;
        for(int i=0;i<_slots.length;i++) {
            Slot s = _slots[i];
            tot += s.getHitPercentage();
        }
        int r = Rand.om.nextInt(tot);
        for(int i=0;i<_slots.length;i++) {
            Slot s = _slots[i];
            r -= s.getHitPercentage();
            if(r<=0) {
                return s;
            }
        }
        throw new Error("exhausted all slots for "+this);
    }

    public final Slot slotFor(Item item) {
        for(List<Slot> slots:_sections.values()) {
            for(int i=0;i<slots.size();i++) {
                Slot s = slots.get(i);
                if(item.equals(s.getOccupant())) {
                    return s;
                }
            }
        }
        return null;
    }

    public final boolean isEquipped(Item item) {
        for(List<Slot> slots:_sections.values()) {
            for(int i=0;i<slots.size();i++) {
                Slot s = slots.get(i);
                if(item.equals(s.getOccupant())) {
                    return true;
                }
            }
        }
        return false;
    }

    public final void equip(Item item) throws EquipFailedException {
        List<Slot> slots = _sections.get(item.getSlotType());
        if(slots==null) {
            // there's no body part that can fit this item. for example,
            // the would-be wearer is a mere blob and these are shoes.
            // blobs don't wear ehoes!!!
            throw new EquipFailedException("That doesn't fit.");
        }
        int free = 0;
        for(Slot s:slots) {
            free += s.isOccupied()?0:1;
        }
        if(free<item.getSlotCount()) {
            if(item.getSlotCount()==1&&item.getSlotType()!=SlotType.hand) {
                // TODO; centralize wield/wear elsewhere
                String verb = "wear";
                if(item.getSlotType()==SlotType.hand) {
                    verb = "wield";
                }
                throw new EquipFailedException("You're already "+verb+"ing something there.");
            }
            else {
                // should deal with pluralization here when there are creatures
                // with more than two arms/legs/heads/torsos/etc.
                String ind = "a free "+item.getSlotType().toString();
                int c = item.getSlotCount()-free;
                if(c>1) {
                    ind = c+" more free "+Grammar.pluralize(item.getSlotType().toString());
                }
                String sl = item.getSlotType().toString();
                throw new EquipFailedException("You need "+ind+".");
            }
        }
        int occ = 0;
        int i = 0;
        do {
            if(!slots.get(i).isOccupied()) {
                slots.get(i).occupy(item);
                occ++;
            }
            i++;
        } while(occ<item.getSlotCount());
    }

    public final void unequip(Item i) {
        boolean found = false;
        for(List<Slot> slots:_sections.values()) {
            for(Slot s:slots) {
                if(i.equals(s.getOccupant())) {
                    s.vacate();
                    found = true;
                }
            }
        }
        if(!found) {
            throw new IllegalArgumentException(i+" is not equipped");
        }
    }

    public Armament getNaturalWeapon() {
        return _naturalWeapon;
    }

    public void setNaturalWeapon(Armament naturalWeapon) {
        _naturalWeapon = naturalWeapon;
    }

    public Armament getNaturalArmor() {
        return _naturalArmor;
    }

    public void setNaturalArmor(Armament naturalArmor) {
        _naturalArmor = naturalArmor;
    }
}
