#!/usr/bin/python
#!/Applications/blender.app/Contents/MacOS/blender -P
#!/home/jkw/blender/blender -P

# """
# Name: 'Generate roguelike avatars'
# Blender: 233
# Group: 'Wizard'
# Tip: 'Generates ASCII characters for roguelike games'
# """

#import bpy
import sys, os, struct, string, types
from os import path
from subprocess import call
#from types import TextCurve

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

test_glyphs = [
    char('@', 'atsign'),
    char('&', 'ampersand'),
]

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

for c in glyphs:
    for dmg in c.damages():
        for res in [6]:
            call(["./generate_glyph.py", "-c", c.symbol, "-e", "0.04", "-b", "0.02", "-B", "2", "-f", '/tmp/blot/'+c.filename, "-r", str(res), "-d", str(dmg)])
            call(["mv", "/tmp/blot/Mesh.mesh.xml", "model/"+c.filename+".mesh.xml"])
