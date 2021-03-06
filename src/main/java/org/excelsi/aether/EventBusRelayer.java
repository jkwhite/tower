package org.excelsi.aether;


import java.util.List;

import org.excelsi.aether.EverythingAdapter;
import org.excelsi.aether.Container;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.NHSpace;
import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static org.excelsi.aether.Events.TOPIC_CHANGES;
import static org.excelsi.aether.Events.TOPIC_MECHANICS;


public class EventBusRelayer extends EverythingAdapter {
    private static final String DEBUG_EVENT = "knowledge";
    private static final Logger LOG = LoggerFactory.getLogger(EventBusRelayer.class);


    @Override public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
        post(TOPIC_CHANGES, new RemoveEvent<Level,NHSpace>(
            (Level)m,
            "space",
            (Level)m,
            (NHSpace)spaces[0]
        ));
    }

    @Override public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
        post(TOPIC_CHANGES, new AddEvent<Level,NHSpace>(
            (Level)m,
            "space",
            (Level)m,
            (NHSpace)spaces[0]
        ));
    }

    @Override public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
        post(TOPIC_MECHANICS, new MechanicsEvent(m, attack, attacker, defender, path));
    }

    @Override public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
        post(TOPIC_MECHANICS, new MechanicsEvent(m, attack, attacker, defender, outcome));
    }

    @Override public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
        post(TOPIC_CHANGES, new MoveEvent(source, (NHBot)b, (NHSpace)from, (NHSpace)to));
    }

    @Override public void faced(Bot b, Direction old, Direction d) {
        post(TOPIC_CHANGES, new OrientEvent(b.getEnvironment().getSpace(), (NHBot)b, old, d));
    }

    @Override public void died(Bot b, MSource s) {
        post(TOPIC_CHANGES, new BotAttributeChangeEvent<Boolean>(b.getEnvironment().getSpace(), b, "dead", false, true));
    }

    @Override public void occupied(MSpace s, Bot b) {
        post(TOPIC_CHANGES, new AddEvent<NHBot,NHSpace>(
            (NHBot)b,
            "bot",
            (NHBot)b,
            (NHSpace)s
        ));
    }

    @Override public void unoccupied(MSpace s, Bot b) {
        post(TOPIC_CHANGES, new RemoveEvent<NHBot,NHSpace>(
            (NHBot)b,
            "bot",
            (NHBot)b,
            (NHSpace)s
        ));
    }

    @Override public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.dropped));
    }

    @Override public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.added));
    }

    @Override public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.added));
    }

    @Override public void itemTaken(Container space, Item item, int idx) {
        post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.taken));
    }

    @Override public void itemDestroyed(Container space, Item item, int idx) {
        post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.destroyed));
    }

    @Override public void itemsDestroyed(Container container, Item[] items) {
        //post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.destroyed));
        throw new UnsupportedOperationException();
    }

    @Override public void equipped(NHBot b, Item i) {
        //post(TOPIC_CHANGES, new BotAttributeChangeEvent<Item>(b.getEnvironment().getSpace(), b, "equipped", null, i));
    }

    @Override public void unequipped(NHBot b, Item i) {
        //post(TOPIC_CHANGES, new BotAttributeChangeEvent<Item>(b.getEnvironment().getSpace(), b, "equipped", i, null));
    }

    @Override public void attributeChanged(Bot b, String attribute, Object newValue) {
        post(TOPIC_CHANGES, new BotAttributeChangeEvent(b.getEnvironment().getSpace(), b, attribute, null, newValue));
    }

    @Override public void forgot(Bot b, List<MSpace> s) {
        post(TOPIC_CHANGES, new SpaceKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "forgot", s));
    }

    @Override public void discovered(Bot b, List<MSpace> s) {
        post(TOPIC_CHANGES, new SpaceKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "discovered", s));
    }

    @Override public void seen(Bot b, List<MSpace> s) {
        post(TOPIC_CHANGES, new SpaceKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "seen", s));
    }

    @Override public void obscured(Bot b, List<MSpace> s) {
        post(TOPIC_CHANGES, new SpaceKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "obscured", s));
    }

    @Override public void noticed(Bot b, List<Bot> bots) {
        post(TOPIC_CHANGES, new BotKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "noticed", bots));
    }

    @Override public void missed(Bot b, List<Bot> bots) {
        post(TOPIC_CHANGES, new BotKnowledgeEvent(b, (Stage)b.getEnvironment().getSpace().getContainer(), (NHBot)b, "missed", bots));
    }

    @Override public void parasiteAdded(NHSpace s, Parasite p) {
        post(TOPIC_CHANGES, new AddEvent<NHSpace,Parasite>(s, "parasite", s, p));
    }

    @Override public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
        post(TOPIC_CHANGES, new AttributeChangeEvent<NHSpace,Parasite>(s, "parasite", s, p, attr, oldValue, newValue));
    }

    @Override public void parasiteRemoved(NHSpace s, Parasite p) {
        post(TOPIC_CHANGES, new RemoveEvent<NHSpace,Parasite>(s, "parasite", s, p));
    }

    @Override public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
        post(TOPIC_CHANGES, new AttributeChangeEvent<Level,NHSpace>(s, "space", (Level)s.getContainer(), s, attr, oldValue, newValue));
    }

    @Override public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
        Thread.dumpStack();
    }

    private static void post(final String topic, final Event e) {
        if(debug(e)) LOG.warn("posting to {}: {}", topic, e);
        EventBus.instance().post(topic, e);
    }

    private static boolean debug(Event e) {
        //return e instanceof SpaceKnowledgeEvent && ((SpaceKnowledgeEvent)e).getKind().equals("discovered");
        return false;
    }
}
