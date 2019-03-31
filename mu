org:  excelsi.org
proj: tower
ver:  2.0-beta1
lang: Java
type:
    - app
    #- web
uses:
    - models/couriernew
    - audio/default
    - nausicaa-1.0.jar
    - jme-3.2-*.jar
    - guava-12.0.jar
    #- groovy-all-2.1.9.jar
    #- groovy-all-2.4.7.jar
    - groovy-2.5.6.jar
    - jfx-2.dev.2016-04-30_075140-ccbd413.jar
    #- lemur-1.8.2-SNAPSHOT.jar
    #- tonegod.gui.jar
    - lemur-1.12.0.jar
    - lemur-proto-1.10.0.jar
    - snakeyaml-1.7.jar
    - slf4j-api-1.7.7.jar
    - slf4j-log4j12-1.7.7.jar
    - log4j-1.2.15.jar
#main: org.excelsi.aether.ui.jfx.JfxMain
main: org.excelsi.aether.ui.lemur.LemurMain
#sysargs: -Djavafx.verbose=true -Dprism.verbose=true -Dprism.lcdtext=false -Dprism.text=t2k
