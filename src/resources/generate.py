#!/usr/bin/python


class m:
    def __init__(self, name, desc, shiny, d_red, d_green, d_blue, d_alpha):
        self.name = name
        self.desc = desc
        self.shiny = shiny
        self.d_red = d_red
        self.d_green = d_green
        self.d_blue = d_blue
        self.d_alpha = d_alpha

def mat(params):
    return '''
        Material {0.name} {0.desc}: Common/MatDefs/Light/Lighting.j3md {{
             MaterialParameters {{
                 Shininess: {0.shiny:1.2f}
                 UseMaterialColors : true
                 Ambient  : 0.0 0.0 0.0 1.0
                 Diffuse  : {0.d_red:1.2f} {0.d_green:1.2f} {0.d_blue:1.2f} {0.d_alpha:1.2f}
                 Specular : 1.0 1.0 1.0 1.0
             }}
        }}
        '''.format(params).strip()

shiny=32.0
colors = [
    m('aquamarine', 'x', shiny, 0.24, 0.63, 0.86, 1.0),
    m('black', 'x', shiny, 0, 0, 0, 1.0),
    m('blue', 'blue', shiny, 0.0, 0, 0.7, 1.0),
    m('bright-blue', 'x', shiny, 0.0, 0.0, 1.0, 1.0),
    m('bright-green', 'x', shiny, 0.0, 1.0, 0.0, 1.0),
    m('bright-red', 'x', shiny, 1.0, 0.0, 0.0, 1.0),
    m('brown', 'x', shiny, 0.35, 0.2, 0.0, 1.0),
    m('cyan', 'x', shiny, 0.0, 0.7, 0.7, 1.0),
    m('dark-blue', 'x', shiny, 0.0, 0.0, 0.3, 1.0),
    m('dark-brown', 'x', shiny, 0.25, 0.1, 0.0, 1.0),
    m('dark-gray', 'x', shiny, 0.3, 0.3, 0.3, 1.0),
    m('dark-green', 'x', shiny, 0.0, 0.3, 0.0, 1.0),
    m('gray', 'gray', shiny, 0.5, 0.5, 0.5, 1.0),
    m('green', 'green', shiny, 0.0, 0.7, 0, 1.0),
    m('grey', 'grey', shiny, 0.5, 0.5, 0.5, 1.0),
    m('indigo', 'x', shiny, 0.21, 0.23, 0.38, 1.0),
    m('lavender', 'x', shiny, 0.56, 0.38, 0.78, 1.0),
    m('light-blue', 'x', shiny, 0.5, 0.5, 1.0, 1.0),
    m('light-brown', 'x', shiny, 0.70, 0.4, 0.0, 1.0),
    m('light-gray', 'x', shiny, 0.85, 0.85, 0.85, 1.0),
    m('light-green', 'x', shiny, 0.5, 1.0, 0.5, 1.0),
    m('light-purple', 'x', shiny, 1.0, 0.0, 1.0, 1.0),
    m('magenta', 'x', shiny, 0.93, 0.21, 0.55, 1.0),
    m('maroon', 'x', shiny, 0.79, 0.01, 0.28, 1.0),
    m('orange', 'x', shiny, 1.0, 0.48, 0.0, 1.0),
    m('pink', 'x', shiny, 0.98, 0.66, 0.82, 1.0),
    m('purple', 'x', shiny, 0.7, 0.0, 0.7, 1.0),
    m('phthalo blue', 'x', shiny, 0.07, 0.36, 0.63, 1.0),
    m('red', 'red', shiny, 0.7, 0, 0, 1.0),
    m('sepia', 'x', shiny, 0.35, 0.26, 0.11, 1.0),
    m('silver', 'x', shiny, 0.7, 0.8, 0.8, 1.0),
    m('tan', 'x', shiny, 0.74, 0.59, 0.43, 1.0),
    m('turquoise', 'x', shiny, 0.20, 0.45, 0.58, 1.0),
    m('ultramarine', 'x', shiny, 0.18, 0.30, 0.86, 1.0),
    m('white', 'white', shiny, 1.0, 1.0, 1.0, 1.0),
    m('yellow', 'x', shiny, 1.0, 0.9, 0.45, 1.0),
    m('alien', 'x', shiny, 0.82, 0.93, 0.35, 1.0),
    m('camphor', 'x', shiny, 0.18, 0.43, 0.01, 1.0),
    m('clear', 'x', shiny, 0.5, 0.5, 1.0, 0.3),
    m('frozen', 'x', shiny, 0.72, 0.87, 0.98, 1.0),
    m('puke-green', 'x', shiny, 0.60, 0.64, 0.25, 1.0),
    m('salmon', 'x', shiny, 0.82, 0.5, 0.30, 1.0),
    m('translucent', 'x', shiny, 0.40, 0.40, 0.40, 0.40),
    m('translucent-lavender', 'x', shiny, 0.56, 0.38, 0.78, 0.7),
    m('translucent-orange', 'x', shiny, 1.0, 0.48, 0.0, 0.7),
    m('transparent', 'x', shiny, 0.3, 0.3, 0.3, 0.1),
    ]

for c in colors:
    w = open('material/'+c.name.replace(' ','_')+'.j3m', 'w')
    w.write(mat(c))
    w.close()
