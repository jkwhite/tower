#!/Applications/blender.app/Contents/MacOS/blender -P
#!/home/jkw/blender/blender -P

# """
# Name: 'Generate roguelike avatars'
# Blender: 233
# Group: 'Wizard'
# Tip: 'Generates ASCII characters for roguelike games'
# """

import Blender

from Blender import Text3d, Mesh, Scene, Object, Material, Curve

import sys, struct, string, types, math
from types import *

import os
from os import path

import jmeXMLExport
#import collada_export
#import md2_export


class glyph:
    def __init__(current, symbol, type, filename):
        current.symbol = symbol
        current.type = type
        current.filename = filename

    def damages(current):
        return [0]

class char(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'char', filename)

    def damages(current):
        return [0, 1, 2]

class arch(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'arch', filename)

class harch(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'harch', filename)

class uarch(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'uarch', filename)

class earth(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'earth', filename)

class item(glyph):
    def __init__(current, symbol, filename):
        glyph.__init__(current, symbol, 'item', filename)

glyphs = [
    char('@', 'atsign'),
    char('&', 'ampersand'),
    char('A', '_a'),
    char('B', '_b'),
    char('C', '_c'),
    char('D', '_d'),
    char('E', '_e'),
    char('F', '_f'),
    char('G', '_g'),
    char('H', '_h'),
    char('I', '_i'),
    char('J', '_j'),
    char('K', '_k'),
    char('L', '_l'),
    char('M', '_m'),
    char('N', '_n'),
    char('O', '_o'),
    char('P', '_p'),
    char('Q', '_q'),
    char('R', '_r'),
    char('S', '_s'),
    char('T', '_t'),
    char('U', '_u'),
    char('V', '_v'),
    char('W', '_w'),
    char('X', '_x'),
    char('Y', '_y'),
    char('Z', '_z'),
    char('a', 'a'),
    char('b', 'b'),
    char('c', 'c'),
    char('d', 'd'),
    char('e', 'e'),
    char('f', 'f'),
    char('g', 'g'),
    char('h', 'h'),
    char('i', 'i'),
    char('j', 'j'),
    char('k', 'k'),
    char('l', 'l'),
    char('m', 'm'),
    char('n', 'n'),
    char('o', 'o'),
    char('p', 'p'),
    char('q', 'q'),
    char('r', 'r'),
    char('s', 's'),
    char('t', 't'),
    char('u', 'u'),
    char('v', 'v'),
    char('w', 'w'),
    char('x', 'x'),
    char('y', 'y'),
    char('z', 'z'),
    char(':', 'colon'),
    char('&', 'ampersand'),
    item('!', 'bang'),
    item('=', 'equals'),
    item(')', 'rparen'),
    item('(', 'lparen'),
    item(']', 'rbrace'),
    item('[', 'lbrace'),
    item('%', 'percent'),
    item('?', 'qmark'),
    item(',', 'comma'),
    item('*', 'asterisk'),
    item('$', 'string'),
    item('/', 'slash'),
    item('+', 'iplus'),
    item('&', 'iampersand'),
    item('~', 'itilde'),
    earth('.', 'dot'),
    earth('#', 'hash'),
    earth('~', 'tilde'),
    uarch('\'', 'squote'),
    uarch('"', 'dquote'),
    arch('+', 'plus'),
    arch('-', 'dash'),
    arch('|', 'pipe'),
    harch('#', 'fhash'),
    harch('_', 'fuscore'),
    harch('=', 'fuequals'),
    uarch('?', 'aqmark'),
    uarch(']', 'frbrace'),
    uarch('>', 'greaterthan'),
    uarch('<', 'lessthan'),
    uarch('/', 'fslash'),
    uarch('\\', 'fbackslash'),
    uarch('^', 'carat'),
    uarch('0', '0')
]

oldscene = None
for c in glyphs:
    for dmg in c.damages():
        for res in [6, 4, 3, 2, 1, 0]:
            file = c.filename+'_'+`res`+'_'+`dmg`
            print 'generating '+c.symbol+' ...'
            scene = Blender.Scene.New(file)
            scene.makeCurrent()
            if oldscene is not None:
                Blender.Scene.Unlink(oldscene)
            text = Text3d.New('Text')
            fnt = Text3d.LoadFont('Courier.ttf')
            text.setFont(fnt)
            text.setText(c.symbol)
            text.setBevelAmount(2)
            text.setExtrudeBevelDepth(0.05)
            #text.setExtrudeDepth(0.3)
            if c.type=='arch':
                text.setExtrudeDepth(0.85)
                text.setBevelAmount(2)
                text.setExtrudeBevelDepth(0.03)
            elif c.type=='earth':
                text.setExtrudeDepth(0.14)
                text.setBevelAmount(2)
                text.setExtrudeBevelDepth(0.03)
            else:
                text.setExtrudeDepth(0.14)
            text.setWidth(1.0)
            text.setSize(4.8)
            if c.type=='char':
                text.setSize(6.4)
                text.setExtrudeDepth(0.18)
            elif c.type=='uarch':
                text.setExtrudeDepth(0.18)
            elif c.type=='harch':
                text.setExtrudeDepth(0.50)

            if res<1:
                text.setBevelAmount(0)
                text.setExtrudeBevelDepth(0)
                res = 1
            text.setDefaultResolution(res)

            ob = Blender.Object.New('Text', 'text'+file)
            ob.link(text)

            #scene.link(ob)
            scene.objects.link(ob)
            ob.makeDisplayList()

            m = Mesh.New('mesh'+file)
            m.getFromObject('text'+file)

            fin = Blender.Object.New('Mesh', 'mesh'+file)
            fin.link(m)
            #scene.link(fin)
            #scene.unlink(ob)
            scene.objects.link(fin)
            scene.objects.unlink(ob)
            fin.select(1)
            if c.type=='char' or c.type=='uarch':
                fin.RotX = 3.1415926/2.0
                fin.RotZ = 3.1415926
            s = 1
            fin.setSize(fin.SizeX*s, fin.SizeY*s, fin.SizeZ*s)
            Blender.Redraw()
            b = fin.getBoundBox()
            #print 'bound ', b
            if c.type=='char' or c.type=='uarch': # chars go upright
                fin.setLocation(-(b[4][0]+b[0][0])/2.0, 0, -b[0][2])
            elif c.type=='item' or c.type=='arch' or c.type=='harch': # items and arch are flat on the ground
                fin.setLocation(-(b[4][0]+b[0][0])/2.0, -(b[2][1]+b[0][1])/2.0, -b[0][2])
            elif c.type=='earth': # earth is below the ground
                fin.setLocation(-(b[4][0]+b[0][0])/2.0, -(b[2][1]+b[0][1])/2.0, -b[2][2])

            # must remove doubles in order to correctly recalc normals
            for v in m.verts:
                v.sel = 1
            m.update()
            if dmg==0:
                if res==6:
                    remamt=0.002
                else:
                    remamt=0.005
            elif dmg==1:
                remamt=0.1
            else:
                remamt=0.3
            r = m.remDoubles(remamt)
            print 'removed %d verts' % r
            # recalc normals because text->mesh conversion messes them up
            m.recalcNormals(0) # 0=outward, 1=inward

            Blender.Redraw()
            save = file+'.jme.xml'
            #save = file+'.dae'
            #save = file+'.md2'
            print 'writing '+save+'...'
            jmeXMLExport.main(save)
            #collada_export.main(save)
            #md2_export.save_md2(save)
            #scene.unlink(fin)
            scene.objects.unlink(fin)
            oldscene = scene

Blender.Quit()
