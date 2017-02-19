import org.excelsi.aether.*
import org.excelsi.tower.*
import org.excelsi.sketch.*

$c.universe.colormap = Data.loadYaml('/data/colors.yaml')

$c.universe.items = Data.loadYaml('/items.yaml')
$c.universe.finds = Data.loadYaml('/data/finds.yaml')
$c.universe.bots = Data.loadYaml('/bots.yaml')
$c.universe.threats = Data.loadYaml('/data/threats.yaml')
$c.universe.actions = Data.loadYaml('/data/actions.yaml')
$c.universe.keymap = Data.loadYaml('/data/keys.yaml')

def l1 = 
    new ExpanseLevelGenerator().generate(
        new LevelRecipe()
        .name("The Lower Reaches")
        .realm("The Lower Reaches")
        .ordinal(1)
        .width(80)
        .height(24)
        .random(Rand.om))
$c.bulk.addLevel(l1)
for(i=0;i<10;i++) {
    //l1.findRandomNormalEmptySpace().occupant = $c.universe.createBot('miner')
    l1.findRandomNormalEmptySpace().occupant = $c.universe.createBot('fabricatorbot')
    l1.findRandomNormalEmptySpace().occupant = $c.universe.createBot('scavengerbot')
}
l1.findRandomNormalEmptySpace().add(new Apple())
l1.getMatrix().getSpace(0,0).add(new Apple())
$c.pov = $c.universe.createBot({b -> 'Archeologist'.equals(b.profession)})
$c.pov.name = System.getProperty("user.name")
$c.pov.inventory.add(new ScrollOfSummoning())
$c.pov.inventory.add(new Book() {
    String getName() { "book entitled 'The Lava Gatherers'" }

    void invoke(NHBot b) {
        //System.err.println("********** RUN*********");
        //Thread.dumpStack();
        $c.state.setLevel(Context.c(), 
            new BasicStageGenerator(Spacemaker.ca([40,12,100], 42, 0.6f, 'hi-i-u-ko', [null, null, Ground, null, Ground, null, null])).generate(
                new LevelRecipe()
                .name("The Lava Gatherers")
                .realm("The Lava Gatherers")
                .ordinal(999)
                .width(40)
                .height(12)
                .random(Rand.om)
                .spaces(Spaces.modulator({ s -> s.color = 'black' }))
                .mixin(new Items($c.universe, 10, ItemFilter.named('lava rock')))
            )
        )
        Context.c().n().print(b, "Another time, another space")
    }
})
$c.pov.inventory.add(new Book() {
    String getName() { "book entitled 'The Lower Reaches'" }

    void invoke(NHBot b) { $c.state.setLevel($c, $c.bulk.findLevel(1)) }
})

//$c.state = new World()

//def world = new ScriptedState('world', 'world.groovy')
def title = new State() {
    String getName() { "title" }

    void run(Context c) {
        c.n.title("")
        c.n.choose(new SelectionMenu<Runnable>(
            new MenuItem<Runnable>("n", "New game", {
                //c.state = new Prelude(Data.resource("/script/prelude-text"))
                c.state = new World()
            }),
            new MenuItem<Runnable>("l", "Load game", null),
            new MenuItem<Runnable>("h", "High scores", { c.state = new HighScores() }),
            new MenuItem<Runnable>("q", "Quit", { c.state = new Quit() })
        ))()
    }
}

$c.state = title
