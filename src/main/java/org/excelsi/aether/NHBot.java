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


import org.excelsi.matrix.*;
import java.util.List;
import java.util.Map;


/**
 * NHBots inhabit the game grid.
 */
public interface NHBot extends Bot, Scoreable, Cloneable {
    /**
     * Creates a shallow copy of this NHBot.
     *
     * @return copy of this bot
     */
    NHBot clone();

    /**
     * Tests if this bot is controlled by a human.
     *
     * @return <code>true</code> if this bot is human-controlled
     */
    boolean isPlayer();

    /**
     * Gets this bot's name.
     *
     * @return name
     */
    String getName();

    /**
     * Sets this bot's name.
     *
     * @param name name
     */
    void setName(String name);

    /**
     * Gets this bot's profession.
     *
     * @return profession
     */
    String getProfession();

    /**
     * Sets this bot's profession.
     *
     * @param profession profession
     */
    void setProfession(String profession);

    /**
     * Gets this bot's remaining hit points.
     *
     * @return hit points
     */
    int getHp();

    /**
     * Sets this bot's remaining hit points. This value
     * is not capped to max hp.
     *
     * @param hp remaining hit points
     */
    void setHp(int hp);

    /**
     * Gets this bot's maximum hit points.
     *
     * @return max hit points
     */
    int getMaxHp();

    /**
     * Sets this bot's maximum hit points.
     *
     * @param maxHp max hit points
     */
    void setMaxHp(int maxHp);
    int getMp();
    void setMp(int mp);
    int getMaxMp();
    void setMaxMp(int maxMp);
    int getStrength();
    void setStrength(int strength);
    int getQuickness();
    void setQuickness(int quickness);
    int getEmpathy();
    void setEmpathy(int empathy);
    int getIntuition();
    void setIntuition(int intuition);
    int getPresence();
    void setPresence(int presence);
    int getAgility();
    void setAgility(int agility);
    int getConstitution();
    void setConstitution(int constitution);
    int getMemory();
    void setMemory(int memory);
    int getReasoning();
    void setReasoning(int reasoning);
    int getSelfDiscipline();
    void setSelfDiscipline(int selfdiscipline);
    int getWeight();
    void setWeight(int weight);
    void setStats(int[] stats);
    int[] getStats();
    int getStat(Stat stat);
    int getModifiedStat(Stat stat);
    void setStat(Stat stat, int value);
    int getModifiedStrength();
    int getModifiedQuickness();
    int getModifiedEmpathy();
    int getModifiedIntuition();
    int getModifiedPresence();
    int getModifiedAgility();
    int getModifiedConstitution();
    int getModifiedMemory();
    int getModifiedReasoning();
    int getModifiedSelfDiscipline();
    int getModifiedWeight();
    void setCommon(String common);
    String getCommon();
    void setGender(Gender g);
    Gender getGender();
    void setUnique(boolean unique);
    boolean isUnique();
    void setModel(String model);
    String getModel();
    void setColor(String color);
    String getColor();
    void setDead(boolean dead);
    boolean isDead();
    void setHunger(int hunger);
    int getHunger();
    void setHungerRate(int hungerRate);
    int getHungerRate();
    int getModifiedHungerRate();
    void setAirborn(boolean airborn);
    boolean isAirborn();
    void setAquatic(boolean aquatic);
    boolean isAquatic();
    void setLevitating(boolean levitating);
    boolean isLevitating();
    void setSlithering(boolean slithering);
    boolean isSlithering();
    void setRolling(boolean rolling);
    boolean isRolling();
    void setConfused(boolean confused);
    boolean isConfused();
    void setInvisible(int invisible);
    int getInvisible();
    boolean isInvisible();
    void setBlind(int blind);
    int getBlind();
    boolean isBlind();
    int getVision();
    void setVision(int vision);
    int getModifiedVision();
    int getModifiedNightvision();
    float getCandela();
    boolean isAudible();
    void setAudible(int audible);
    int getAudible();
    void setConnected(Connected connected);
    Connected getConnected();
    Connected getModifiedConnected();
    void setSociality(Sociality sociality);
    Sociality getSociality();
    void setTemperament(Temperament temperament);
    Temperament getTemperament();
    void setSize(Size s);
    Size getSize();
    void setMinLevel(int level);
    int getMinLevel();
    void setMaxLevel(int level);
    int getMaxLevel();
    void setRarity(int rarity);
    int getRarity();
    void setLoot(int loot);
    int getLoot();
    void setForm(Form f);
    Form getForm();

    Inventory getInventory();
    boolean isEquipped(Item i);
    void setPack(Item[] items);
    Item[] getPack();
    boolean isPacked(Item i);
    void setWielded(Item weapon) throws EquipFailedException;
    void setWielded(Item weapon, String msg) throws EquipFailedException;
    Item getWielded();
    void setQuivered(Item weapon) throws EquipFailedException;
    void setQuivered(Item weapon, String msg) throws EquipFailedException;
    Item getQuivered();
    void setWearing(Item[] wearing) throws EquipFailedException;
    Item[] getWearing();
    void wear(Item i) throws EquipFailedException;
    void wear(Item i, String msg) throws EquipFailedException;
    void takeOff(Item i) throws EquipFailedException;

    void afflict(Affliction.Onset onset);
    void addAffliction(Affliction a);
    List<Affliction> getAfflictions();
    void removeAffliction(Affliction a);
    void removeAffliction(String name);
    boolean isAfflictedBy(String affliction);
    Affliction getAffliction(String name);
    boolean allow(NHBotAction a);

    void addModifier(Modifier m);
    void removeModifier(Modifier m);
    List<Modifier> getModifiers();
    Modifier modifier();

    boolean isOccupied();
    void start(ProgressiveAction action);
    ProgressiveAction getAction();
    void interrupt();
    /** @deprecated */
    void die(String s);
    void die(Source s, String cause);
    void tick();
    int getSkill(String skill);
    int getSkill(Armament arm);
    void setSkill(Armament arm, int skill);
    void setSkill(String skill, int sk);
    void setSkills(Map<String, Integer> skills);
    void skillUp(String skill);
    void statGain(Stat s);
    Map<String, Integer> getSkills();
    public int getStatGainRate();

    /**
     * Polymorphs this bot into another bot. This involves
     * changing all physical and mental characteristics to
     * that of the specified bot. No references to the
     * target bot are retained (they are all deep copies).
     *
     * @param to bot to which to polymorph into
     */
    void polymorph(NHBot to);

    /**
     * Lets bot intercept an attack, for example to eat a thrown comestible.
     *
     * @param a attack against this bot
     * @return <code>true</code> if attack was intercepted
     */
    boolean intercept(Context c, Attack a);

    /**
     * Tests if this bot can occupy a space. This method can be used
     * if some particular type of bot can only occupy certain types
     * of spaces.
     *
     * @param s space to test
     * @return <code>true</code> if space can be occupied by this bot
     */
    boolean canOccupy(NHSpace s);

    /**
     * Gets this bot's threat level toward another bot.
     *
     * @param b target
     * @return threat level toward <code>b</code>
     */
    Threat threat(NHBot b);

    /**
     * Sets this bot's threat level toward another bot.
     *
     * @param b bot for which to change threat
     * @param t new threat level
     */
    void setThreat(NHBot b, Threat t);

    /**
     * Gets this bot's environment, if any.
     *
     * @return environment, or <code>null</code> if bot is not part of any environment
     */
    NHEnvironment getEnvironment();

    /**
     * Sets this bot's event source. Bots may listen
     * to events generated by this source and act on
     * them during <code>act()</code>.
     *
     * @param events event source
     */
    void setEventSource(EventSource events);

    /**
     * Gets the collective noun describing a group
     * of this bot. For example, "pack of wolves".
     *
     * @return collective noun
     */
    String toPack();

    /**
     * Whether or not changes to this bot's attributes should
     * result in noticable UI indicators.
     *
     * @return <code>true</code> if changes should be noticeable
     */
    boolean changesNoticably();
}
