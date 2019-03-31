import org.excelsi.aether.*
import org.excelsi.tower.*
import org.excelsi.sketch.*

int initialLevel = 101

$c.universe.colormap = Data.loadYaml('/data/colors.yaml')
$c.universe.items = Data.loadYaml('/items.yaml')
$c.universe.finds = Data.loadYaml('/data/finds.yaml')
$c.universe.bots = Data.loadYaml('/bots.yaml')
$c.universe.threats = Data.loadYaml('/data/threats.yaml')
$c.universe.actions = Data.loadYaml('/data/actions.yaml')
$c.universe.keymap = Data.loadYaml('/data/keys.yaml')

$c.bulk.stagemaker = new TowerStagemaker(
    new BasicStageGenerator(
        Data.loadYaml('/data/environs.yaml'),
        [
            Ingredients.mixin('hills', new Heightmap(0.5f)),
            Ingredients.mixin('foothills', new Heightmap()),
            Ingredients.mixin('mountains', new Heightmap(2f)),
            Ingredients.mixin('trees', new Stands(OrangeTree,AppleTree)),
            Ingredients.mixin('vines', new Vines()),
            Ingredients.mixin('scattered-items', new Items(Items.Strategy.area)),
            new Bots('bots'),
            Ingredients.mixin('green-litten', new Illumination(1f, 'light-green')),
            Ingredients.mixin('blue-litten', new Illumination(1f, 'light-blue')),
            Ingredients.mixin('red-litten', new Illumination(1f, 'light-red')),
            Ingredients.mixin('bright', new Illumination(2f, 'white')),
            Ingredients.mixin('dim', new Illumination(0.6f, 'light-gray')),
            Ingredients.mixin('dark', new Illumination(0.3f, 'dark-gray')),
            Ingredients.mixin('pitch-black', new Illumination(0f, 'black')),
            Ingredients.i('tiny', { r -> r.width(20); r.height(20) }),
            Ingredients.i('small', { r -> r.width(30); r.height(30) }),
            Ingredients.i('smallish', { r -> r.width(60); r.height(60) }),
            Ingredients.i('medium', { r -> r.width(80); r.height(80) }),
            Ingredients.i('large', { r -> r.width(160); r.height(160) }),
            Ingredients.i('huge', { r -> r.width(300); r.height(300) }),
            Ingredients.i('enormous', { r -> r.width(600); r.height(600) }),
            Ingredients.i('rooms', { r -> r.spacemaker(TowerLevelGenerator.spacemaker()) }),
            Ingredients.i('expanse', { r -> r.spacemaker(Spacemaker.expanse()) }),
            Ingredients.i('ruins', { r -> r.spacemaker(r.spacemaker.and(Spacemaker.modulator({ s -> 
                if(s instanceof Wall) {
                    if(Rand.d100(20)) {
                        s.replace(r.spaces.create(Ground))
                    }
                }
            })))}),
            Ingredients.i('base', { r -> r.spacemaker(
                Spacemaker.expanse(Ground,Grass)
                .and(Spacemaker.circle(150,-100,300,Wall,Ground,85))
                .and(Spacemaker.circle(150,-100,303,Wall,null,85))
                .and(Spacemaker.line(150,90,150,290,Floor,99))
                .and(Spacemaker.line(151,90,151,290,Floor,97))
                .and(Spacemaker.line(149,90,149,290,Floor,96))
                .and(Spacemaker.line(149,250,280,250,Floor,96))
                .and(Spacemaker.line(148,90,148,200,Wall,30))
                .and(Spacemaker.line(152,90,152,200,Wall,30))
                .and(TowerLevelGenerator.town(220,220,50,40,true,false))
                .and(TowerLevelGenerator.town(20,220,20,20,false,false))
                .and({ r2,l -> l.getSpace((int)(l.width()/2),(int)(l.height()/2-40)).replace(new Stairs(true)) })
                .and({ r2,l -> l.getSpace((int)(l.width()/2),(int)(l.height()/2-42)).addParasite(new CamphorTree()) })
            ) }),
            Ingredients.i('blight', { r -> 
                r.spaces(Spaces.modulator({ s ->
                    if(s.origin == Origin.natural) {
                        switch(Rand.om.nextInt(3)) {
                            case 0:
                                s.setColor("gray");
                                break;
                            case 1:
                                s.setColor("brown");
                                break;
                            default:
                                break;
                        }
                    }
                }))
            })
        ]
    ),
    initialLevel,
    Data.loadYaml('/data/tower.yaml')
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
$c.pov.inventory.add(new Diary(Data.resource('/script/diary-town.txt')))
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
                .mixin(new Bots.BMixin(BotFactory.exact('lava gatherer')))
            )
        )
        Context.c().n().print(b, "THe nights are cold on Oriab...")
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
        c.n.choose(null, new SelectionMenu<Runnable>(
            new MenuItem<Runnable>("n", "New game", {
                //c.state = new Prelude(Data.resource("/script/prelude-text"))
                c.state = new World(initialLevel)
            }),
            new MenuItem<Runnable>("l", "Load game", null),
            new MenuItem<Runnable>("h", "High scores", { c.state = new HighScores() }),
            new MenuItem<Runnable>("q", "Quit", { c.state = new Quit() })
        ))()
    }
}

$c.state = title
