import org.excelsi.aether.*
import org.excelsi.tower.*
import org.excelsi.sketch.*


$c.universe.bots = [
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


$c.bulk.addLevel(
    new ExpanseLevelGenerator().generate(
        new LevelRecipe()
        .name("The Lower Reaches")
        .ordinal(1)
        .width(80)
        .height(24)
        .random(Rand.om))
)
for(i=0;i<20;i++) {
    $c.bulk.findLevel(1).findRandomNormalEmptySpace().occupant = $c.universe.createBot('construction worker')
}
$c.bulk.findLevel(1).findRandomNormalEmptySpace().add(new Apple())
$c.bulk.findLevel(1).getMatrix().getSpace(0,0).add(new Apple())


/*
if($c.n.confirm('Isomorphoze anisotropic apotropaganisms?'))
    $c.state = new Title()
else
    $c.state = new Quit()
*/
//$c.state = new World()

//def world = new ScriptedState('world.groovy')
def title = new State() {
    String getName() { "title" }

    void run(Context c) {
        c.n.title("")
        c.n.choose(new SelectionMenu<Runnable>(
            new MenuItem<Runnable>("n", "New game", {
                //c.setState(new Prelude(Data.resource("prelude-text")));
                c.setState(new World());
            }),
            new MenuItem<Runnable>("l", "Load game", null),
            new MenuItem<Runnable>("h", "High scores", { c.setState(new HighScores()); }),
            new MenuItem<Runnable>("q", "Quit", { c.setState(new Quit()); })
        ))()
    }
}

$c.state = title
