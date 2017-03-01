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

$c.bulk.stagemaker = new TowerStagemaker(
    new BasicStageGenerator([
        Ingredients.mixin('hills', new Heightmap()),
        Ingredients.i('small', { r -> r.width(30); r.height(30); })
    ])
)

/*
def l1 = 
    new ExpanseLevelGenerator().generate(
        new LevelRecipe()
        .name('Terra Obscura')
        .realm('Terra Obscura')
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
l1.getSpace(41,12).replace(new Stairs(true))
l1.light = 5f
*/

/*
def l2 = 
    new BasicStageGenerator(TowerLevelGenerator.spacemaker()).generate(
        new LevelRecipe()
        .name("The Lower Reaches")
        .realm("The Lower Reaches")
        .ordinal(2)
        .width(80)
        .height(24)
        .random(Rand.om))
$c.bulk.addLevel(l2)
*/

$c.pov = $c.universe.createBot({b -> 'Archeologist'.equals(b.profession)})
$c.pov.name = System.getProperty("user.name")
$c.pov.inventory.add(new Snowshoes())
$c.pov.inventory.add(new Pill(new Cyanide()))
$c.pov.inventory.add(new ScrollOfMapping(count:30))
$c.pov.inventory.add(new Book() {
    String getName() { "book entitled 'The Lava Gatherers'" }

    void invoke(NHBot b) {
        $c.state.setLevel(Context.c(), 
            new BasicStageGenerator().generate(
                new LevelRecipe()
                .spacemaker(Spacemaker.ca([160,160,150], 6, Rand.om.nextInt(), 0.6f, 'hi-i-u-ko',
                    Spacemaker.mapIndex([null, Ground, Ground, Ground, Ground, Ground]),
                    Spacemaker.mapColor(['black', 'dark-gray', 'gray', 'blue', 'black', 'purple'])
                ))
                .name("The Lava Gatherers")
                .realm("The Lava Gatherers")
                .ordinal(999)
                .width(160)
                .height(160)
                .random(Rand.om)
                .spaces(Spaces.modulator({ s ->
                    s.color = 'black';
                    if(s instanceof Blank) {
                        s.breakupAction = { m ->
                            m2 = m.replace(new Ground())
                            m2.color = 'black'
                            if(Rand.d100(50)) {
                                r = $c.universe.createItem(ItemFilter.named('lava rock'))
                                r.count = Rand.om.nextInt(5)+1
                                m2.add(r)
                            }
                            true
                        }
                    }
                }))
                .mixin(new Items($c.universe, 50, ItemFilter.named('lava rock')))
                .mixin(new Heightmap())
                .mixin(new Bots(BotFactory.exact('lava gatherer')))
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
