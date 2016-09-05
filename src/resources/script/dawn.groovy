import org.excelsi.aether.*
import org.excelsi.tower.*
import org.excelsi.sketch.*

$c.universe.colormap = Data.loadYaml('/data/colors.yaml')


//$c.universe.bots = [
foo = [
    new Patsy([
      common:'human',
      profession:'Traveler',
      //form: !org.excelsi.tower.Humanoid {},
      strength: 5,
      quickness: 5,
      agility: 5,
      constitution: 70,
      maxHp: 3,
      color: 'white',
      model: '@',
      selectionText: 'Summer took a turn cause you spent your wishes carelessly.',
      hidden: true,
      skills: [
        'unarmed': 30,
        'thrown': 30,
        'detect': 10
      ]
    ]),
    new NPC([
      common: 'construction worker',
      //form: !org.excelsi.tower.Humanoid {}
      strength: 70,
      quickness: 60,
      agility: 60,
      constitution: 90,
      maxHp: 70,
      maxLevel: 39,
      minLevel: 0,
      model: '@',
      color: 'gray',
      loot: 2,
      rarity: 10,
      skills: [
        'one-handed edged': 50,
        'two-handed edged': 50,
        'unarmored': 50
      ],
      /*
      wearing: 
        - !org.excelsi.tower.HardHat {}
      wielded: !org.excelsi.tower.Jackhammer {}
      */
      ai: new Brain([new SurvivalDaemon(), new FleeDaemon(), new WanderDaemon()])
      /*
        daemons: 
          //- !org.excelsi.aether.AttackDaemon {}
          //- !org.excelsi.tower.DigDaemon {}
          - !org.excelsi.aether.SurvivalDaemon {}
          - !org.excelsi.aether.FleeDaemon {}
          //- !org.excelsi.tower.HealDaemon {}
          //- !org.excelsi.tower.DeferenceDaemon {}
          //- !org.excelsi.tower.ChatDaemon
            vocalizations:
                - 'Work, work...'
                - "That ain't workin'."
      pack:
        - !org.excelsi.tower.BottleOfGlue {}
        - !org.excelsi.tower.Rock {}
        - !org.excelsi.tower.SmallStone {}
        - !org.excelsi.tower.Pick_Axe {}
        */
    ])
]

$c.universe.bots = Data.loadYaml('/bots.yaml')
$c.universe.threats = Data.loadYaml('/data/threats.yaml')

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
for(i=0;i<20;i++) {
    l1.findRandomNormalEmptySpace().occupant = $c.universe.createBot('miner')
}
l1.findRandomNormalEmptySpace().add(new Apple())
l1.getMatrix().getSpace(0,0).add(new Apple())
$c.pov = $c.universe.createBot({b -> 'Traveler'.equals(b.profession)})

//$c.state = new World()

//def world = new ScriptedState('world', 'world.groovy')
def title = new State() {
    String getName() { "title" }

    void run(Context c) {
        c.n.title("")
        c.n.choose(new SelectionMenu<Runnable>(
            new MenuItem<Runnable>("n", "New game", {
                //c.setState(new Prelude(Data.resource("prelude-text")));
                c.state = new World()
            }),
            new MenuItem<Runnable>("l", "Load game", null),
            new MenuItem<Runnable>("h", "High scores", { c.state = new HighScores() }),
            new MenuItem<Runnable>("q", "Quit", { c.state = new Quit() })
        ))()
    }
}

$c.state = title
