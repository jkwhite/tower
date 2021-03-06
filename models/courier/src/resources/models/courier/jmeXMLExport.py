#!BPY
""" Registration info for Blender menus:
Name: 'jME XML(.xml)'
Blender: 236
Group: 'Export'
Tip: 'Export to jME XML file format.'
"""
__version__ = "05/11/05"
__email__ = ('Author, hevee:gmx*de')
__url__ = ('www*jmonkeyengine*com')

__bpydoc__ = """\
This script exports Blender Objects to the jMonkeyEngine's xml format.
jMonkeyEngine is a 3d game engine written in java, check link.

Supported:<br>
  All Blender Objects can be exported to jMe Nodes with their location, <br>
  rotation, and size properties and their names.<br>
  Meshes (tri- and quad faces, subSurfs) can have:<br>
  * deformed vertex positions
  * Materials,<br>
  * UV-coordinates (not sticky), <br>
  * morph target animation (by vertex keys, armature... whatever)<br>
  exported.

Planned:<br>
  hierarchies, lights, cameras, spline paths, <br>
  object level animation (loc, rot, size).<br>
Known issues:<br>
  Currently, animations do not loop correctly in jME. This might be a jME <br>
  issue.<br>
Notes:<br>
  If you export animations, make sure you have only the animated mesh object<br>
  selected. Keyframes will otherwise be generated for every selected object,<br>
  resulting in quite a bunch of unneccessary data to be saved.<br>
  Also, check the generated keyframe text in a text window, to manually tweak<br>
  keyframe generation.<br>
  Animations for meshes with multiple materials or different settings of<br>
  "Twoside" for their faces are currently unsupported.<br>
"""

import Blender
from Blender.Mathutils import RotationMatrix

roundAmount = 7
EX_ANIMATIONS = False
EX_OBJECT_ANIMATIONS = False
EX_MATERIALS = True
EX_VERTEXCOLORS = True
EX_UVS = True
CURRENT_FILENAME = None
CONV_COORDS = True
unTriFaces = False
TextureStatesWarningShown = False
KEYFRAMES = 'jme keyframes'
ANIMHELPTEXT = \
'''This text contains the frame numbers to be exported by the jMonkeyEngine 
exporter. During export, keyframe information will be read from this text.
Any line starting with a non-numerical string will be ignored.
You can name keyframes. The name will be written to the xml file as the "name" 
property of the keyframepointintime. jMe won't care but you can have it there 
for personal reference. A name is written after the keyframe number, in the 
same line, separated by one or more whitespace characters (tab or space).\n'''
animPupShown = False

class jmeVertex2:
    "just keeps references to the index of this vertex, as well as it's face index"
    def __init__(self, oldIndex):
        self.oldIndex = oldIndex
    def setFace(self, faceIndex, indexInFace):
        self.faceIndex = faceIndex
        self.indexInFace = indexInFace
    def getVertIndex(self):
        return self.oldIndex
    def getFaceIndex(self):
        return self.faceIndex
    def getIndexInFace(self):
        return self.indexInFace
    def copy(self):
        ret = jmeVertex2(self.oldIndex)
        ret.setFace(self.faceIndex)
        return ret

class jmeVertex:
#---coords[x,y,z], normal[x,y,z], color[r,g,b,a], uv[u,v]---#000000#FFFFFF------
    global roundAmount
    def __init__(self, xyzCoords, xyzNormal):
        self.coords = xyzCoords
        self.normal = xyzNormal
        self.color = None
        self.uv = None
    def setRGBA(self, rgbaColor):
        self.color = rgbaColor
    def setUV(self, uvTexcoords):
        self.uv = uvTexcoords
    def copyNewColor(self, rgba):
        'creates a copy of this jmeVertex with changed color'
        ret = jmeVertex(self.coords, self.normal)
        ret.setRGBA(rgba)
        ret.setUV(self.uv)
        return ret
    def copyNewUV(self, uv):
        'creates a copy of this jmeVertex with changed uv coords'
        ret = jmeVertex(self.coords, self.normal)
        ret.setRGBA(self.color)
        ret.setUV(uv)
        return ret

class jmeMesh:
    #---name verts indices material texture---#000000#FFFFFF------------------------
    def __init__(self, name):
        self.name = name
        self.verts = []
        self.indices = []
        self.material = None
        self.texture = None
        self.oldIndices = {}
        self.newIndex = 0
    def appendVert(self, jmeVert, oldIndex):
        if not self.oldIndices.has_key(oldIndex):
            self.verts.append(jmeVert)
            self.oldIndices[oldIndex] = self.newIndex
            self.newIndex += 1
    def appendIndex(self, oldIndex):
##        if not self.oldIndices.has_key(oldIndex):
##            self.oldIndices[oldIndex] = self.newIndex
##            self.newIndex += 1
        self.indices.append(self.oldIndices[oldIndex])
    def setMaterial(self, jmeMat):
        self.material = jmeMat
    def setTexture(self, jmeTex):
        self.texture = jmeTex
    def setTwoSided(self, twosided):
        self.twosided = twosided
        if not twosided:
            self.name += '.nocull'
    def getVertByOldIndex(self, oldIndex):
        newIndex = self.oldIndices[oldIndex]
        return self.verts[newIndex]
    def toString(self, indentL, children = '', onlyVerts = False):
        global EX_MATERIALS
        ind = makeTag(indentL+1, 'index', ints2String('data', self.indices))
        (co, no, col, uv) = buildStringsFromJmeVerts(indent(indentL), self.verts)
        if not onlyVerts:
            matString = ''
            if self.material:
                matString += self.material.toString(indentL+1)
            if self.texture:
                matString += self.texture.toString(indentL+1)
            if self.twosided:
                matString += indent(indentL+1)+'<cullstate cull="none">'+indent(indentL+1)+'</cullstate>\n'

            meshStr = makeTag(indentL,'mesh', 'name="'+self.name+'"', co + no + col + uv + ind + matString + children)
        else:#only for keyframeController animation, don't output material and outer mesh tags
            meshStr = co + no + col + uv + ind + children
        return meshStr
        
class jmeMaterial:
    def __init__(self, blenderMat):
        self.diff = floats2String('diffuse', blenderMat.getRGBCol() + [1.0])#add alpha value of 1.0
        ambMult = lambda x : blenderMat.getAmb() * x # f(x) = x * ambient
        self.amb  = floats2String('ambient', map(ambMult, Blender.World.GetCurrent().getAmb())+[1.0])
        emMult = lambda x : blenderMat.getEmit() * x
        self.emi  = floats2String('emissive', map(emMult, blenderMat.getRGBCol())+[1.0])
        specMult = lambda x : blenderMat.getSpec()/2 * x
        self.spec = floats2String('specular', map(specMult, blenderMat.getSpecCol()) + [1.0])
        self.shiny = floats2String('shiny', [blenderMat.getHardness() / 511.0 * 128])#range 0-128, as in OpenGL
        self.alpha = floats2String('alpha', [blenderMat.getAlpha()])
    def toString(self, indentL):
                matString = makeTag(indentL, 'materialstate', self.diff+' '+self.amb+' '+self.emi+' '+self.spec+' '+self.shiny+' '+self.alpha)
                return matString
class jmeTexture:
    def __init__(self, blenderMTex):
        if blenderMTex.tex.type == Blender.Texture.Types.IMAGE:
            self.tname = blenderMTex.tex.image.filename
        else:
            self.tname = None
    def toString(self, indentL):
        if self.tname:
            texString = makeTag(indentL+1, 'texture', 'file="'+self.tname+'" texnum="0" wrap="4"')
            matString = makeTag(indentL, 'texturestate', ' ', texString)
            return matString
        else:
            return ''

def floats2String(attrName, floatList):
    'result will be: <attrName>=" f0 f1 f2...", fx being floats from the list'
    if not floatList:
        return ''
    ret = attrName+'="'
    for i in floatList:
        ret += str(round(i, roundAmount))+' '
    if len(floatList):
        ret = ret[:-1]
        ret += '"'
    else:
        ret = ''
    return ret

def ints2String(attrName, intList):
    'result will be: <attrName>=" i0 i1 i2...", ix being ints from the list'
    if not intList:
        return ''
    ret = attrName+'="'
    for i in intList:
        ret += str(i)+' '
    if len(intList):
        ret = ret[:-1]
        ret += '"'
    else:
        ret = ''
    return ret
    
def indent(level):
    return ' '*level
    
def makeTag(intIndent, strName, strAttributes, strChildren=''):
    ret = indent(intIndent)+'<'+strName+' '+strAttributes+'>\n'+strChildren+indent(intIndent)+'</'+strName+'>\n'
    return ret
    
def buildStringsFromJmeVerts(indentString, ListOfJmeVerts):
    def f2s(float):
        return str(round(float, roundAmount))+' '
    co = indentString+'<vertex data="'
    no = indentString+'<normal data="'
    col = indentString+'<color data="'
    uv = indentString+'<texturecoords data="'
    index = -1
    for v in ListOfJmeVerts:
        index += 1
        
        co += f2s(v.coords[0])+f2s(v.coords[1])+f2s(v.coords[2])
        no += f2s(v.normal[0])+f2s(v.normal[1])+f2s(v.normal[2])
        if EX_VERTEXCOLORS and v.color:
            col += f2s(v.color[0])+f2s(v.color[1])+f2s(v.color[2])+f2s(v.color[3])
        else:
            col = ''
        if EX_UVS and v.uv:
                uv += f2s(v.uv[0])+f2s(v.uv[1])
        else:
            uv = ''
    co += '">\n'+indentString+'</vertex>\n'
    no += '">\n'+indentString+'</normal>\n'
    if len(col):
        col += '">\n'+indentString+'</color>\n'
    if len(uv):
        uv += '">\n'+indentString+'</texturecoords>\n'
    return (co, no, col, uv)

def writeObjectAnimation(blenderObject, objectname ,indentL, children=''):
    start = Blender.Get('staframe')
    out = indent(indentL+1)+'<spatialtransformer numobjects="1">\n'
    oldframe = Blender.Get('curframe')

    out += indent(indentL+2)+'<stobj obnum="0" parnum="-1">\n'
    out += indent(indentL+3)+'<repeatobject ident="'+objectname+'" />\n'
    out += indent(indentL+2)+'</stobj>\n'
    keyframes = loadKeyframes()
        
    for i in keyframes.keys():
        fps = 1.0 / Blender.Scene.GetCurrent().getRenderingContext().framesPerSec()
        Blender.Set('curframe', i)
        kfname = keyframes[i]
        ob = Blender.Object.Get(objectname)
        
        mat = ob.getMatrix('worldspace')
        rotQ = mat.toQuat()
        rot = floats2String("rotvalues", [rotQ.x, rotQ.y, rotQ.z, rotQ.w])
        rot = makeTag(indentL+3, "sptrot", 'index="0" '+ rot)
        trans = floats2String("transvalues", mat.translationPart())
        trans = makeTag(indentL+3, "spttrans", 'index="0" '+ trans)
        scale = floats2String("scalevalues", ob.getSize())
        scale = makeTag(indentL+3, "sptscale", 'index="0" '+ scale)
        
        content = scale+rot+trans
        
        if len(kfname):
            out += indent(indentL+2)+'<spatialpointtime time="'+str((i-start)* fps)+'" name="'+kfname+'">\n'+content
        else:
            out += indent(indentL+2)+'<spatialpointtime time="'+str((i-start)* fps)+'">\n'+content
        out += indent(indentL+2)+'</spatialpointtime>\n'
        
    out += children+indent(indentL)+'</spatialtransformer>\n'
    Blender.Set('curframe', oldframe)
    return out

def writeMorphAnimation(nmeshObject, meshname,indentL, children=''):
    start = Blender.Get('staframe')
    out = indent(indentL)+'<keyframecontroller>\n'
    oldframe = Blender.Get('curframe')
    
    keyframes = loadKeyframes()
        
    for i in keyframes.keys():
        fps = 1.0 / Blender.Scene.GetCurrent().getRenderingContext().framesPerSec()
        Blender.Set('curframe', i)
        kfname = keyframes[i]
        nmesh = Blender.NMesh.GetRawFromObject(nmeshObject.getName())#get mesh with deformations applied
        mstr = writeMesh(nmesh, meshname,indentL+2, onlyVerts = True)#don't get materials, and mesh tags
        if len(kfname):
            out += indent(indentL+1)+'<keyframepointintime time="'+str((i-start)* fps)+'" name="'+kfname+'">\n'+mstr
        else:
            out += indent(indentL+1)+'<keyframepointintime time="'+str((i-start)* fps)+'">\n'+mstr
        out += indent(indentL+1)+'</keyframepointintime>\n'
        
    out += children+indent(indentL)+'</keyframecontroller>\n'
    Blender.Set('curframe', oldframe)
    return out
    
def writeObject(ob, indentL, children = ''):
    global EX_ANIMATIONS, EX_OBJECT_ANIMATIONS
    if ob.getType() == 'Mesh':
        mesh = Blender.NMesh.GetRawFromObject(ob.getName())
        meshname = ob.getData().name
        if len(mesh.verts):#some mesh objects might contain no vertices at all. skip the mesh, then, but keep their object
            if EX_ANIMATIONS:
                dataStr = writeMesh(mesh, meshname, indentL+1, writeMorphAnimation(ob, meshname, indentL+2))
            else:
                dataStr = writeMesh(mesh, meshname, indentL+1)
        else:
            dataStr = ''
    else:
        dataStr = ''
        
    mat = ob.getMatrix('worldspace')
    rotQ = mat.toQuat()
    rot = floats2String("rotation", [rotQ.x, rotQ.y, rotQ.z, rotQ.w])
    trans = floats2String("translation", mat.translationPart())
    scale = floats2String("scale", ob.getSize())
    
    sharedident = ''
    if EX_OBJECT_ANIMATIONS:
        dataStr += writeObjectAnimation(ob, ob.getName(),indentL+1)
        sharedident += 'sharedident="'+ob.getName()+'" '

    nodeStr = makeTag(indentL, 'node', sharedident+'name="'+ob.getName()+'" '+trans+' '+rot+' '+scale, dataStr+children)
    return nodeStr
    
def writeMesh(mesh, meshname, indentL = 0, children = '', onlyVerts = False):
    global roundAmount, unTriFaces
    global EX_ANIMATIONS,EX_MATERIALS, EX_VERTEXCOLORS, EX_UVS
    global CONV_COORDS
    
    jmeMeshes = {} # key format is (materialIndex, isDoubleSided)
    
    mindex = 0
    if EX_MATERIALS:
        materials = mesh.getMaterials()
    else:
        materials = []
        
    for m in materials:
        if len(materials) > 1:
            j = jmeMesh(meshname+'.'+m.getName())
        else:
            j = jmeMesh(meshname)
        if EX_MATERIALS:
            j.setMaterial(jmeMaterial(m))
        jmeMeshes[(mindex, None)] = j
        mindex += 1
    
    if not len(materials):# a blender NMesh can have no material at all
        jmeMeshes[(0, None)] = jmeMesh(meshname)

    numJmeVerts = [len(mesh.verts)] # to make this accessible from within processVertInFace, it cannot be an int, but must be a list. oh wel..
    #----faces processing (indices, colors, uvs)---#000000#FFFFFF-------------------------------------------
    def processVertInFace(face, vertindex):
        if EX_MATERIALS:
            matIndex = face.materialIndex
        else:
            matIndex = 0
        vert = face.v[vertindex]
    #----vertex processing (vertex coords, normals)---#000000#FFFFFF------------------------------------------
        twosided = face.mode & Blender.NMesh.FaceModes['TWOSIDE'] == Blender.NMesh.FaceModes['TWOSIDE']
        
#---make different materials and CullStates different meshes---#000000#FFFFFF---------------------------------
        if jmeMeshes.has_key((matIndex, twosided)):
            jmeMeshes[(matIndex, twosided)].appendVert(jmeVertex([vert.co[0], vert.co[1], vert.co[2]], [vert.no[0], vert.no[1], vert.no[2]]), vert.index)
        elif jmeMeshes.has_key((matIndex, None)): # matIndex exists, but twosided has not yet been set
            jmeMeshes[(matIndex, twosided)] = jmeMeshes.pop((matIndex, None))
            jmeMeshes[(matIndex, twosided)].appendVert(jmeVertex([vert.co[0], vert.co[1], vert.co[2]], [vert.no[0], vert.no[1], vert.no[2]]), vert.index)
        else:# matIndex exists, but not with this twosided value
            if EX_MATERIALS:
                newmesh = jmeMesh(meshname+'.'+materials[matIndex].name)
            else:
                newmesh = jmeMesh(meshname)
            newmesh.setMaterial(jmeMeshes[(matIndex, not twosided)].material)
            newmesh.setTexture(jmeMeshes[(matIndex, not twosided)].texture)
            jmeMeshes[(matIndex, twosided)] = newmesh
            jmeMeshes[(matIndex, twosided)].appendVert(jmeVertex([vert.co[0], vert.co[1], vert.co[2]], [vert.no[0], vert.no[1], vert.no[2]]), vert.index)
            
        retIndex = vert.index
        if(len(face.col) and EX_VERTEXCOLORS and mesh.hasVertexColours()):
            curCol = [  face.col[vertindex].r/255.0,\
                        face.col[vertindex].g/255.0,\
                        face.col[vertindex].b/255.0,\
                        face.col[vertindex].a/255.0]
            if not jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).color:
                jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).setRGBA(curCol)
            elif jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).color <> curCol:#create a new jmeVertex
                newvert = jmeMeshes[(matIndex,twosided)].getVertByOldIndex(retIndex).copyNewColor(curCol)

                numJmeVerts[0] += 1
                retIndex = numJmeVerts[0] - 1

                jmeMeshes[(matIndex,twosided)].appendVert(newvert, retIndex)
# TODO: support sticky uvs (would anybody need them, anyway?)
        if(len(face.uv) and EX_UVS and mesh.hasFaceUV()):
            curUV = [face.uv[vertindex][0], face.uv[vertindex][1]]
            if not jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).uv:
                jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).setUV(curUV)
            elif jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).uv <> curUV:#create a new jmeVertex
                newvert = jmeMeshes[(matIndex, twosided)].getVertByOldIndex(retIndex).copyNewUV(curUV)

                numJmeVerts[0] += 1
                retIndex = numJmeVerts[0] - 1

                jmeMeshes[(matIndex, twosided)].appendVert(newvert, retIndex)
        return retIndex
        
    for f in mesh.faces:
        if EX_MATERIALS:
            matIndex = f.materialIndex
        else:
            matIndex = 0
        twosided = f.mode & Blender.NMesh.FaceModes['TWOSIDE'] == Blender.NMesh.FaceModes['TWOSIDE']
        if len(f.v) == 3:
            for vindex in range(3):#loop through the 3 vertices
                indx = processVertInFace(f, vindex)
                jmeMeshes[(matIndex, twosided)].appendIndex(indx)
##                indices.append(indx)
        elif len(f.v) == 4:# triangulate face
            for vindex in [0,1,2]:#loop through the first 3 vertices
                indx = processVertInFace(f, vindex)
                jmeMeshes[(matIndex, twosided)].appendIndex(indx)
##                indices.append(indx)
            for vindex in [2,3,0]:#loop through the last 3 vertices
                indx = processVertInFace(f, vindex)
                jmeMeshes[(matIndex, twosided)].appendIndex(indx)
##                indices.append(indx)

        else:#if len(f.v) < 3 or >4 show a message
            if not unTriFaces:#check if we have already shown this message...
                Blender.Draw.PupMenu("Export error%t|one or more faces have < 3 or >4 vertices.")
                unTriFaces = 1
    
    #----materials creation---#000000#FFFFFF-----------------------------------------
    if(EX_MATERIALS and not onlyVerts):
        matIndex = 0
        for m in mesh.getMaterials():#take only the first material... for now...
            if type(m) == Blender.Types.MaterialType:
                if jmeMeshes.has_key((matIndex, None)):
                    jmeMeshes[(matIndex, None)].setMaterial(jmeMaterial(m))
                elif jmeMeshes.has_key((matIndex, True)):
                    jmeMeshes[(matIndex, True)].setMaterial(jmeMaterial(m))
                elif jmeMeshes.has_key((matIndex, False)):
                    jmeMeshes[(matIndex, False)].setMaterial(jmeMaterial(m))
                
                mtex = m.getTextures()[0]
                if type(mtex) == Blender.Types.MTexType:
                    if jmeMeshes.has_key((matIndex, None)):
                        jmeMeshes[(matIndex, None)].setTexture(jmeTexture(mtex))
                    if jmeMeshes.has_key((matIndex, True)):
                        jmeMeshes[(matIndex, True)].setTexture(jmeTexture(mtex))
                    if jmeMeshes.has_key((matIndex, False)):
                        jmeMeshes[(matIndex, False)].setTexture(jmeTexture(mtex))
            matIndex += 1
    ret  = ''
    for key in jmeMeshes.keys():
        twosided = key[1]
        value = jmeMeshes[key]
        value.setTwoSided(twosided)
        ret += value.toString(indentL, children, onlyVerts = onlyVerts)
    return ret

def keyframesToBlenderText(keyframes):
    try:
        text = Blender.Text.Get(KEYFRAMES)
        text.clear()
    except NameError:# text doesn't exist, this means that keyframes have been generated
        text = Blender.Text.New(KEYFRAMES)
        text.write(ANIMHELPTEXT+"\n")
    text.set('follow_cursor', 0)#don't confuse the user by scrolling to the text's end
    ks = keyframes.keys()
    ks.sort()
    for i in ks:
        if len(keyframes[i]):
            text.write(str(i)+"\t"+keyframes[i]+"\n")
        else:
            text.write(str(i)+"\n")
    text.set('follow_cursor', 1)

def textToKFDict(blenderText):
    ret = {}
    for i in blenderText.asLines():
        if len(i):
            kfnum = i.split()[0]
            kfname = ''
            if len(i.split(None, 1)) >= 2:
                kfname = i.split(None, 1)[1]
            try:
                ret[int(kfnum)] = kfname#None as separator: behave as in split()
            except ValueError: #assume it's a comment...
                pass
    return ret

def loadKeyframes():
    global animPupShown
    ret = {}
    start = Blender.Get('staframe')
    end = Blender.Get('endframe')
    try:
        ret = textToKFDict(Blender.Text.Get(KEYFRAMES))
    except NameError:#a text named <KEYFRAMES> is not found, generate keyframes
        if not animPupShown:
            animPupShown = True
            Blender.Draw.PupMenu('Morph target animation keyframes generated%t|Check text "jme keyframes" in a text window')

        ret[start] = 'keyframe names go here'
# TODO: user should be able to specify a stepsize for keyframe generation.
        for i in range(start+3, end, 3):
            ret[i] = ''
        ret[end] = ''
        keyframesToBlenderText(ret)#save the generated keyframes in a text window
    return ret
    
def main(filename):
    global CURRENT_FILENAME,CONV_COORDS
    CURRENT_FILENAME = filename
    update_RegistryInfo()
    
    def buildObjectHierarchy(ObjectsList):
        '''build a top-down hierarchy of blender objects, which only support getParent()'''
        toplevel = {}
        hier = {}
        for o in ObjectsList:
            p = o.getParent()
            if p:
                if hier.has_key(p.getName()):
                    hier[p.getName()].append(o.getName())
                else:
                    hier[p.getName()] = [o.getName()]
            else:
                toplevel[o.getName()] = {}
                
        def recurse(cc):
            ret = {}
            if(hier.has_key(cc)):
                for i in hier[cc]:
                    ret[i] = recurse(i)
            return ret
        
        for t in toplevel.keys():
            if hier.has_key(t):
                for c in hier[t]:
                    toplevel[t][c] = recurse(c)
        return toplevel
    
    def writeObjectsHierarchy(oDict, indentL=0):
        ret = ''
        for i in oDict.keys():
            ret += writeObject( Blender.Object.Get(i), \
                                indentL, \
                                writeObjectsHierarchy(oDict[i], indentL+1))
        return ret

    print "starting export to jME XML file format..."
    print "-----------------------------------------"
    
    if Blender.Window.EditMode(): Blender.Window.EditMode(0)
    if filename.find('.xml', -4) <= 0: filename += '.xml' 
# TODO: determine if encoding="UTF-8" is always true, or how to find out about system's encoding
    result = '<?xml version="1.0" encoding="UTF-8" ?>'
    result += '<scene>\n'
# TODO: hierarchy is not used right now
##    oDict = buildObjectHierarchy(Blender.Object.Get())
##    result += writeObjectsHierarchy(oDict, 1)
    objects = ''
    obList = Blender.Object.GetSelected()
    step = 0.95 / len(obList)
    pos = 0.0
    for obj in Blender.Object.GetSelected():
        objects += writeObject(obj, 2)
        Blender.Window.DrawProgressBar(pos,'Exporting '+obj.getName())
        pos += step
# TODO: at present, coordinate system conversion is done by object rotation. this is bad.
    rotConversion = ''
    if CONV_COORDS:
        matConvert = RotationMatrix(90, 4, 'x') * RotationMatrix(180, 4, 'z')
        quat = matConvert.toQuat()
        rotConversion = floats2String('rotation', [quat.x, quat.y, quat.z, quat.w])
    result += makeTag(1, 'node', 'name="rootNode" '+rotConversion, objects)
    result += '</scene>'
    print "-----------------------------------------"
    print "done exporting to jME XML file format."

    file = open(filename, "w")
    Blender.Window.DrawProgressBar(0.96,'Writing xml file...')
    try:
        file.write(result)
    finally:
        file.close()
    Blender.Window.DrawProgressBar(1.0,'Done! jME Export finished.')

def gui():
    global CURRENT_FILENAME
    if CURRENT_FILENAME and len(CURRENT_FILENAME):
        Blender.Window.FileSelector(main, "Export jME XML", CURRENT_FILENAME)
    else:
        Blender.Window.FileSelector(main, "Export jME XML")

def draw():
    global EX_ANIMATIONS,EX_MATERIALS, EX_VERTEXCOLORS, EX_UVS, CURRENT_FILENAME
    global CONV_COORDS

    # clearing screen
    Blender.BGL.glClearColor(0.7, 0.7, 0.7, 1)
    Blender.BGL.glClear(Blender.BGL.GL_COLOR_BUFFER_BIT)
    
    # background
    width, height = Blender.Window.GetAreaSize()
    x = width / 2 - 335 / 2
    y = height - 210
    if x < 10: x = 10
    if y < 200: y = 200
    Blender.BGL.glColor3f(0.9, 0.9, 0.9)
    Blender.BGL.glRectf(x - 10,y,width - x, y - 200)
    lh1 = 16
    # logo
    Blender.BGL.glColor3f(0.878,0.773,0.545)
    Blender.BGL.glRectf(x - 10, y + 14, width - x ,y-2)
    Blender.BGL.glColor3f(0.28,0.2,0)
    Blender.BGL.glRectf(x - 12, y + 16, width - x - 2,y)
    Blender.BGL.glRasterPos2d(x, y)
    Blender.BGL.glEnable(Blender.BGL.GL_BLEND)
    Blender.BGL.glBlendFunc(Blender.BGL.GL_SRC_ALPHA, Blender.BGL.GL_ONE_MINUS_SRC_ALPHA)
    Blender.BGL.glDrawPixels(64, 48, Blender.BGL.GL_RGBA, Blender.BGL.GL_BYTE, LOGO)
    Blender.BGL.glColor3f(0.2, 0.2, 0.2)
    Blender.BGL.glRasterPos2d(x + 55+1, y+4)
    Blender.Draw.Text("jME Exporter")
    Blender.BGL.glColor3f(1, 1, 1)
    Blender.BGL.glRasterPos2d(x + 55, y+4)
    Blender.Draw.Text("jME Exporter")
    
    # text
    Blender.BGL.glColor3f(0, 0, 0)
    y -= 25
    Blender.BGL.glRasterPos2d(x+10, y)
    Blender.Draw.Text("All selected Objects will be exported to jme Nodes.",'small')
    y -= lh1 - 3
    Blender.BGL.glRasterPos2d(x+10, y)
    Blender.Draw.Text("Only meshes will have data, every Node will have loc, rot and size.",'small')
    y -= lh1
    Blender.BGL.glRasterPos2d(x+10, y)
    Blender.Draw.Text("TextureStates will be created from the first Texture in",'small')
    y -= lh1 - 3
    Blender.BGL.glRasterPos2d(x+10, y)
    Blender.Draw.Text("the Object's Material.",'small')
    # Buttons
    y -= 40
    col2x = x+170
    
    Blender.Draw.Toggle("Export vertex colors", 6, col2x, y, 155, 20, EX_VERTEXCOLORS, '')
    Blender.Draw.Toggle("Export materials", 5, x, y, 155, 20, EX_MATERIALS, '')
    y -= 22
    Blender.Draw.Toggle("Export texture coordinates", 4, x, y, 155, 20, EX_UVS, '')
    Blender.Draw.Toggle("Skeletal animations", 3, col2x, y, 155, 20, EX_ANIMATIONS)
    y -= 22
    Blender.Draw.Toggle("Convert coordinate system", 7, x, y, 155, 20, CONV_COORDS, '')
    Blender.Draw.Toggle("Object animations", 8, col2x, y, 155, 20, EX_OBJECT_ANIMATIONS, 'early version...')

    y -= 40
    Blender.Draw.Button("Export", 2, x, y, 107, 25)
    Blender.Draw.Button("Cancel", 1, x+109, y, 107, 25)

def update_RegistryInfo():
    global EX_ANIMATIONS,EX_MATERIALS, EX_VERTEXCOLORS, EX_UVS, CURRENT_FILENAME, EX_OBJECT_ANIMATIONS
    global CONV_COORDS, animPupShown
    d = {}
    d['EX_ANIMATIONS'] = EX_ANIMATIONS
    d['EX_MATERIALS'] = EX_MATERIALS
    d['EX_VERTEXCOLORS'] = EX_VERTEXCOLORS
    d['EX_UVS'] = EX_UVS
    d['CURRENT_FILENAME'] = CURRENT_FILENAME
    d['KEYFRAMES'] = KEYFRAMES
    d['EX_OBJECT_ANIMATIONS'] = EX_OBJECT_ANIMATIONS
    d['animPupShown'] = animPupShown
    Blender.Registry.SetKey('jmeExport', d)

def load_RegistryInfo():
    global EX_ANIMATIONS,EX_MATERIALS, EX_VERTEXCOLORS, EX_UVS, CURRENT_FILENAME, EX_OBJECT_ANIMATIONS
    global CONV_COORDS, KEYFRAMES, animPupShown
    d = Blender.Registry.GetKey('jmeExport')
    if d:
        try:
            EX_ANIMATIONS = d['EX_ANIMATIONS']
            EX_MATERIALS = d['EX_MATERIALS']
            EX_VERTEXCOLORS = d['EX_VERTEXCOLORS']
            EX_UVS = d['EX_UVS']
            CURRENT_FILENAME = d['CURRENT_FILENAME']
            KEYFRAMES = d['KEYFRAMES']
            EX_OBJECT_ANIMATIONS = d['EX_OBJECT_ANIMATIONS']

            animPupShown = d['animPupShown']
        except:
            pass

def bevent(evt):
    global EX_ANIMATIONS,EX_MATERIALS, EX_VERTEXCOLORS, EX_UVS, CURRENT_FILENAME, EX_OBJECT_ANIMATIONS
    global CONV_COORDS
    global TextureStatesWarningShown
    global animPupShown

    if evt == 1: # Cancel
        Blender.Draw.Exit()
    elif evt == 2: # Export
        if not len(Blender.Object.GetSelected()):
            Blender.Draw.PupMenu("Error:%t|Nothing selected!")
            return
        update_RegistryInfo()
        Blender.Draw.Exit()
        gui()
    elif evt == 3:#ex anim
        loadKeyframes()
        EX_ANIMATIONS = not EX_ANIMATIONS
        Blender.Draw.Redraw(1)
    elif evt == 4:#ex uvs
        EX_UVS = not EX_UVS
        if EX_UVS and not EX_MATERIALS and not TextureStatesWarningShown:
            TextureStatesWarningShown = True
            Blender.Draw.PupMenu("Warning%t|TextureStates will only be created if Export Material is set. UVs will be created, though.")
        Blender.Draw.Redraw(1)
    elif evt == 5:#ex materials
        EX_MATERIALS = not EX_MATERIALS
        if EX_UVS and not EX_MATERIALS and not TextureStatesWarningShown:
            TextureStatesWarningShown = True
            Blender.Draw.PupMenu("Warning%t|TextureStates will only be created if Export Material is set. UVs will be created, though.")
        Blender.Draw.Redraw(1)
    elif evt == 6:#ex vertex colors
        EX_VERTEXCOLORS = not EX_VERTEXCOLORS
        Blender.Draw.Redraw(1)
    elif evt == 7:#CONV_COORDS
        CONV_COORDS = not CONV_COORDS
        Blender.Draw.Redraw(1)
    elif evt == 8:#ex objectanim
        loadKeyframes()
        EX_OBJECT_ANIMATIONS = not EX_OBJECT_ANIMATIONS
        Blender.Draw.Redraw(1)

def event(evt, val):
    if evt == Blender.Draw.ESCKEY and not val:
        Blender.Draw.Exit()

LOGO =  Blender.BGL.Buffer(Blender.BGL.GL_BYTE, [48, 64*4],\
[[127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 123, 0, 127, 127, 123, 0, 127, 126, 127, 0, 127, 125, 127, 0, 127, 125, 127, 0, 127, 126, 126, 0, 125, 125, 124, 0, 127, 127, 125, 0, 122, 122, 122, 0, 123, 122, 123, 0, 127, 126, 127, 0, 127, 127, 127, 0, 126, 126, 124, 0, 120, 121, 121, 0, 124, 124, 124, 0, 126, 126, 126, 1, 122, 122, 122, 9, 118, 117, 117, 19, 110, 109, 108, 25, 100, 99, 95, 36, 106, 103, 96, 49, 102, 98, 89, 66, 99, 94, 85, 73, 97, 91, 79, 84, 98, 92, 78, 87, 100, 93, 81, 81, 101, 96, 88, 67, 104, 101, 95, 51, 102, 101, 99, 36, 111, 112, 111, 26, 121, 121, 121, 10, 126, 127, 124, 0, 127, 125, 123, 0, 127, 125, 123, 0, 127, 127, 126, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 125, 126, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 123, 0, 127, 127, 123, 0, 127, 126, 127, 0, 127, 125, 127, 0, 127, 125, 127, 0, 127, 126, 126, 0, 125, 125, 124, 0, 127, 127, 125, 0, 122, 122, 122, 0, 123, 122, 124, 0, 127, 127, 127, 0, 127, 127, 127, 0, 121, 122, 121, 7, 108, 109, 109, 26, 105, 104, 100, 46, 102, 98, 89, 71, 98, 92, 79, 91, 97, 89, 73, 104, 99, 89, 70, 110, 103, 91, 68, 119, 106, 95, 67, 124, 108, 96, 67, 127, 109, 95, 67, 127, 110, 96, 67, 126, 110, 97, 67, 126, 110, 96, 67, 127, 109, 95, 67, 126, 106, 93, 67, 124, 102, 90, 67, 119, 95, 86, 67, 111, 91, 85, 73, 95, 101, 100, 98, 47, 126, 125, 124, 0, 127, 126, 124, 0, 127, 127, 126, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 125, 126, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 123, 0, 127, 127, 123, 0, 127, 126, 127, 0, 127, 125, 127, 0, 127, 125, 127, 0, 127, 127, 126, 0, 125, 125, 124, 0, 124, 125, 125, 1, 117, 117, 116, 17, 108, 106, 104, 38, 99, 95, 86, 78, 93, 86, 72, 100, 96, 87, 66, 117, 102, 89, 63, 127, 108, 95, 66, 127, 112, 98, 68, 127, 114, 100, 70, 127, 114, 100, 70, 126, 113, 99, 70, 126, 112, 99, 69, 127, 112, 99, 69, 127, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 127, 112, 99, 69, 126, 114, 100, 70, 126, 114, 100, 69, 127, 98, 86, 61, 127, 84, 80, 73, 93, 113, 112, 111, 25, 126, 127, 127, 0, 127, 126, 127, 0, 127, 126, 127, 0, 127, 126, 127, 0, 127, 126, 127, 0, 127, 126, 127, 0, 126, 126, 127, 0, 126, 126, 127, 0, 126, 125, 126, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 123, 0, 127, 127, 123, 0, 127, 126, 127, 0, 127, 126, 127, 0, 127, 126, 127, 0, 127, 127, 127, 0, 109, 109, 108, 32, 98, 94, 86, 73, 101, 93, 74, 103, 104, 93, 67, 120, 108, 95, 67, 126, 110, 97, 67, 126, 113, 99, 69, 126, 113, 99, 69, 126, 113, 99, 70, 126, 112, 99, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 113, 99, 70, 125, 105, 93, 65, 127, 92, 84, 67, 109, 101, 100, 98, 50, 126, 126, 126, 1, 127, 126, 126, 0, 127, 126, 125, 0, 127, 126, 125, 0, 127, 126, 125, 0, 126, 125, 124, 0, 121, 121, 120, 0, 126, 125, 126, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 123, 0, 127, 127, 123, 0, 126, 127, 126, 0, 123, 123, 123, 7, 111, 109, 105, 40, 98, 91, 79, 88, 101, 89, 65, 125, 112, 98, 67, 127, 113, 99, 70, 127, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 127, 114, 100, 69, 127, 95, 82, 58, 127, 88, 85, 79, 77, 121, 122, 123, 4, 122, 123, 123, 0, 122, 123, 123, 0, 122, 123, 123, 0, 123, 124, 121, 0, 119, 119, 118, 0, 126, 125, 125, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 122, 121, 123, 0, 122, 121, 120, 0, 127, 127, 124, 0, 127, 127, 124, 0, 123, 124, 124, 5, 97, 94, 87, 72, 101, 91, 67, 119, 110, 96, 68, 126, 113, 99, 69, 126, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 127, 112, 99, 69, 126, 114, 100, 70, 127, 87, 77, 58, 124, 105, 104, 106, 30, 121, 122, 124, 0, 118, 118, 119, 0, 119, 119, 120, 0, 123, 124, 121, 0, 119, 119, 118, 0, 126, 125, 125, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 123, 123, 123, 0, 119, 119, 120, 0, 123, 123, 124, 0, 123, 122, 123, 6, 94, 90, 80, 86, 105, 91, 64, 127, 113, 100, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 70, 126, 109, 96, 67, 125, 104, 92, 64, 126, 112, 99, 69, 126, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 113, 100, 70, 126, 105, 92, 64, 127, 86, 83, 75, 91, 116, 117, 117, 11, 115, 114, 115, 6, 119, 119, 120, 1, 124, 125, 122, 0, 120, 120, 119, 0, 127, 126, 126, 0, 122, 121, 121, 0, 122, 122, 121, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 127, 127, 0, 112, 112, 112, 22, 96, 93, 88, 61, 101, 97, 93, 58, 85, 83, 80, 72, 101, 89, 64, 124, 115, 101, 70, 127, 112, 99, 69, 127, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 70, 126, 112, 99, 70, 126, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 113, 99, 70, 126, 113, 99, 70, 126, 108, 95, 66, 126, 78, 68, 49, 124, 109, 97, 68, 126, 113, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 126, 114, 100, 71, 126, 81, 72, 55, 123, 67, 59, 45, 96, 79, 71, 54, 89, 86, 81, 72, 71, 98, 98, 95, 47, 108, 109, 108, 24, 125, 125, 125, 2, 125, 124, 124, 0, 123, 123, 122, 0, 127, 126, 126, 0, 126, 125, 125, 0, 124, 126, 125, 0, 121, 121, 121, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [125, 125, 127, 0, 125, 125, 127, 0, 125, 125, 127, 0, 125, 125, 127, 0, 125, 125, 127, 0, 125, 125, 127, 0, 125, 125, 127, 0, 127, 127, 127, 0, 88, 87, 84, 70, 90, 78, 52, 127, 111, 97, 67, 127, 90, 79, 54, 127, 78, 69, 49, 125, 88, 78, 55, 125, 98, 86, 61, 126, 105, 93, 65, 125, 110, 97, 69, 125, 110, 97, 69, 125, 104, 92, 65, 125, 109, 96, 68, 126, 113, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 127, 113, 100, 70, 126, 107, 94, 66, 126, 94, 82, 59, 125, 112, 98, 70, 125, 110, 97, 67, 126, 76, 68, 48, 124, 108, 96, 67, 125, 113, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 116, 102, 72, 125, 84, 75, 54, 125, 60, 46, 14, 127, 79, 59, 16, 127, 74, 55, 13, 127, 68, 52, 13, 127, 68, 53, 21, 127, 72, 61, 37, 109, 84, 77, 67, 77, 105, 103, 100, 35, 117, 116, 117, 18, 126, 126, 126, 1, 125, 126, 126, 0, 121, 121, 122, 0, 124, 125, 126, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 126, 126, 0, 127, 127, 127, 0, 114, 115, 114, 21, 81, 75, 64, 104, 108, 95, 66, 127, 115, 101, 71, 125, 108, 96, 67, 126, 98, 86, 61, 126, 86, 76, 55, 124, 83, 73, 52, 124, 87, 77, 56, 124, 86, 76, 55, 124, 83, 74, 53, 124, 107, 94, 66, 125, 114, 99, 70, 127, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 113, 99, 70, 126, 114, 100, 70, 127, 114, 100, 70, 126, 108, 95, 66, 126, 95, 84, 60, 125, 73, 63, 46, 124, 88, 78, 56, 125, 115, 101, 71, 125, 92, 81, 58, 124, 80, 71, 50, 125, 114, 100, 70, 126, 112, 99, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 70, 126, 114, 101, 71, 126, 67, 58, 39, 126, 64, 48, 14, 127, 78, 59, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 58, 16, 127, 76, 57, 15, 127, 72, 54, 16, 127, 70, 56, 25, 118, 72, 62, 41, 103, 91, 87, 81, 64, 114, 114, 115, 21, 122, 123, 124, 0, 125, 126, 127, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0, 125, 127, 125, 0],\
 [127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 127, 0, 112, 113, 112, 25, 52, 45, 33, 124, 86, 74, 53, 126, 107, 94, 66, 126, 111, 98, 69, 126, 115, 101, 71, 126, 116, 101, 72, 126, 115, 101, 71, 126, 115, 101, 71, 126, 115, 101, 71, 126, 113, 99, 70, 126, 111, 98, 69, 126, 109, 95, 67, 127, 104, 92, 65, 125, 101, 89, 63, 126, 96, 84, 60, 125, 90, 80, 57, 125, 79, 69, 49, 125, 78, 69, 49, 124, 94, 83, 59, 125, 110, 97, 68, 126, 114, 100, 71, 126, 112, 98, 69, 126, 107, 94, 66, 125, 111, 97, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 126, 115, 102, 71, 126, 80, 71, 51, 125, 49, 38, 15, 127, 77, 58, 17, 127, 77, 59, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 58, 16, 127, 73, 54, 13, 127, 64, 49, 18, 127, 75, 67, 54, 95, 108, 107, 105, 33, 123, 123, 123, 7, 126, 127, 126, 0, 125, 127, 126, 0, 125, 127, 126, 0],\
 [127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 127, 0, 109, 109, 106, 34, 53, 41, 17, 127, 70, 61, 41, 127, 86, 76, 55, 124, 79, 70, 50, 125, 84, 75, 53, 126, 90, 79, 56, 125, 92, 81, 57, 124, 91, 81, 56, 125, 89, 78, 56, 126, 84, 73, 54, 125, 81, 73, 53, 125, 82, 72, 52, 125, 90, 79, 56, 124, 94, 83, 59, 125, 99, 87, 62, 126, 103, 91, 64, 125, 106, 93, 65, 126, 111, 98, 69, 126, 113, 99, 70, 126, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 126, 112, 99, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 115, 102, 71, 125, 92, 81, 59, 125, 52, 42, 21, 126, 38, 28, 11, 127, 48, 37, 13, 127, 66, 50, 16, 127, 71, 54, 16, 127, 69, 52, 16, 127, 70, 53, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 16, 127, 73, 55, 15, 127, 70, 55, 25, 116, 83, 78, 69, 79, 120, 121, 122, 8, 126, 127, 125, 0, 126, 127, 125, 0],\
 [126, 127, 126, 0, 126, 127, 126, 0, 126, 127, 126, 0, 127, 127, 126, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 127, 0, 119, 119, 119, 13, 79, 73, 59, 91, 70, 52, 10, 127, 67, 54, 26, 127, 101, 89, 64, 124, 112, 99, 69, 126, 104, 91, 64, 126, 101, 89, 63, 126, 100, 88, 62, 126, 100, 88, 62, 126, 102, 90, 64, 125, 105, 92, 65, 126, 110, 97, 69, 125, 114, 100, 70, 126, 114, 100, 70, 126, 113, 99, 70, 126, 112, 99, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 69, 126, 112, 99, 69, 126, 112, 99, 69, 126, 112, 99, 69, 127, 112, 99, 69, 127, 112, 99, 69, 126, 112, 99, 69, 126, 112, 99, 69, 126, 112, 98, 69, 126, 112, 99, 69, 126, 112, 99, 70, 126, 113, 100, 70, 126, 111, 98, 69, 126, 82, 71, 50, 125, 56, 43, 18, 127, 74, 56, 16, 127, 71, 54, 17, 127, 45, 35, 13, 127, 49, 37, 13, 127, 62, 46, 15, 127, 63, 48, 15, 127, 69, 52, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 78, 58, 17, 127, 69, 52, 12, 127, 70, 63, 50, 99, 115, 115, 115, 19, 127, 127, 125, 0],\
 [124, 126, 127, 0, 124, 126, 127, 0, 124, 126, 127, 0, 125, 127, 127, 0, 127, 127, 124, 0, 127, 127, 123, 0, 127, 127, 126, 0, 118, 118, 118, 15, 79, 72, 61, 90, 69, 53, 17, 126, 77, 59, 16, 127, 72, 55, 14, 127, 61, 50, 28, 126, 99, 87, 63, 124, 115, 101, 71, 126, 113, 99, 70, 126, 113, 99, 70, 126, 113, 99, 70, 126, 113, 99, 70, 126, 112, 99, 69, 127, 103, 89, 64, 124, 111, 97, 69, 125, 113, 99, 70, 126, 112, 99, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 70, 126, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 114, 101, 71, 126, 114, 101, 71, 125, 101, 89, 64, 125, 69, 60, 40, 126, 44, 34, 16, 127, 73, 56, 15, 127, 77, 59, 17, 127, 77, 59, 17, 127, 74, 56, 17, 127, 58, 43, 14, 127, 53, 41, 14, 127, 65, 48, 15, 127, 74, 56, 17, 127, 78, 59, 18, 127, 78, 59, 17, 127, 78, 59, 17, 127, 76, 58, 17, 127, 76, 57, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 66, 49, 11, 127, 76, 72, 65, 85, 123, 123, 124, 2],\
 [124, 126, 127, 0, 124, 126, 127, 0, 124, 126, 127, 0, 125, 126, 127, 0, 127, 127, 125, 0, 122, 122, 122, 7, 99, 97, 94, 47, 65, 54, 32, 116, 75, 56, 13, 127, 75, 57, 17, 127, 71, 54, 17, 127, 73, 56, 17, 127, 70, 52, 15, 127, 47, 38, 20, 126, 86, 76, 55, 125, 112, 99, 70, 126, 113, 100, 71, 127, 112, 99, 70, 127, 112, 99, 70, 127, 111, 98, 68, 127, 74, 65, 47, 126, 108, 96, 67, 126, 78, 69, 49, 124, 93, 82, 58, 126, 114, 100, 70, 126, 112, 98, 69, 126, 112, 98, 69, 126, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 113, 100, 70, 127, 112, 99, 69, 127, 107, 93, 66, 127, 98, 86, 61, 127, 81, 70, 47, 127, 61, 50, 27, 126, 60, 46, 17, 127, 74, 56, 16, 127, 61, 46, 15, 127, 53, 40, 14, 127, 75, 57, 17, 127, 77, 58, 18, 127, 76, 58, 17, 127, 77, 59, 17, 127, 75, 58, 17, 127, 65, 49, 16, 127, 55, 41, 13, 127, 53, 40, 12, 127, 59, 45, 16, 127, 59, 45, 14, 127, 51, 38, 12, 127, 71, 54, 17, 127, 78, 58, 17, 127, 76, 58, 16, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 15, 127, 58, 47, 22, 126, 112, 112, 113, 24],\
 [125, 127, 127, 0, 125, 127, 127, 0, 125, 127, 127, 0, 127, 127, 127, 0, 120, 120, 121, 11, 84, 78, 66, 81, 68, 53, 22, 120, 75, 56, 15, 127, 77, 59, 17, 127, 73, 54, 17, 127, 62, 46, 16, 127, 65, 49, 14, 127, 56, 42, 15, 127, 40, 30, 12, 127, 57, 45, 19, 127, 70, 60, 39, 127, 100, 88, 62, 127, 113, 100, 70, 127, 114, 101, 71, 127, 114, 100, 70, 127, 68, 59, 43, 127, 104, 91, 65, 127, 83, 73, 52, 126, 50, 44, 32, 126, 113, 98, 69, 126, 113, 99, 69, 127, 112, 99, 69, 127, 112, 99, 70, 127, 112, 99, 70, 127, 112, 99, 70, 127, 113, 100, 70, 127, 113, 100, 70, 127, 115, 101, 71, 127, 115, 102, 72, 127, 111, 97, 68, 127, 97, 85, 60, 127, 81, 74, 56, 127, 88, 84, 77, 127, 59, 54, 46, 127, 63, 47, 13, 127, 74, 56, 16, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 57, 17, 127, 59, 45, 16, 127, 50, 39, 13, 127, 73, 56, 17, 127, 78, 59, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 73, 55, 16, 127, 71, 53, 16, 127, 71, 54, 16, 127, 73, 56, 16, 127, 77, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 58, 16, 127, 55, 43, 17, 127, 109, 109, 108, 31],\
 [127, 127, 124, 0, 127, 127, 125, 0, 123, 124, 125, 5, 99, 97, 91, 51, 72, 60, 37, 112, 74, 54, 13, 127, 78, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 59, 17, 127, 76, 58, 18, 127, 62, 47, 15, 127, 50, 38, 14, 127, 69, 53, 16, 127, 79, 60, 18, 127, 46, 33, 8, 127, 47, 47, 46, 127, 91, 87, 79, 127, 79, 72, 59, 127, 84, 74, 55, 127, 83, 74, 56, 127, 89, 78, 56, 127, 101, 89, 62, 127, 72, 63, 46, 124, 94, 83, 58, 124, 102, 89, 62, 127, 100, 88, 63, 127, 102, 90, 64, 127, 101, 90, 64, 127, 99, 87, 64, 127, 98, 88, 66, 127, 92, 83, 65, 127, 60, 53, 39, 127, 38, 33, 24, 127, 60, 58, 54, 127, 102, 102, 101, 127, 118, 118, 119, 127, 127, 127, 127, 127, 115, 115, 116, 127, 61, 51, 34, 127, 74, 55, 13, 127, 76, 59, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 78, 59, 17, 127, 69, 52, 17, 127, 51, 39, 14, 127, 57, 43, 15, 127, 67, 50, 14, 127, 73, 55, 15, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 72, 55, 15, 127, 52, 38, 8, 127, 55, 51, 43, 104, 119, 119, 120, 12],\
 [127, 127, 126, 0, 120, 120, 121, 10, 85, 80, 73, 75, 69, 54, 24, 122, 74, 56, 14, 127, 75, 57, 17, 127, 74, 55, 17, 127, 71, 54, 15, 127, 67, 50, 16, 127, 62, 47, 15, 127, 56, 42, 15, 127, 56, 43, 14, 127, 73, 57, 17, 127, 79, 60, 17, 127, 60, 44, 13, 127, 33, 26, 16, 127, 100, 101, 101, 127, 122, 122, 123, 127, 42, 42, 43, 127, 78, 78, 78, 127, 76, 74, 69, 127, 57, 45, 14, 127, 68, 53, 21, 127, 67, 53, 22, 126, 67, 52, 22, 127, 66, 51, 19, 127, 62, 54, 39, 127, 97, 95, 92, 127, 100, 99, 94, 127, 105, 103, 100, 127, 112, 111, 109, 127, 107, 107, 106, 127, 22, 22, 22, 127, 0, 0, 0, 127, 27, 27, 28, 127, 118, 118, 118, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 93, 92, 91, 127, 59, 45, 15, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 56, 16, 127, 48, 37, 16, 126, 58, 53, 45, 101, 60, 50, 31, 115, 58, 43, 13, 127, 67, 50, 12, 127, 75, 57, 16, 127, 76, 58, 17, 127, 77, 59, 17, 127, 77, 59, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 75, 57, 17, 127, 62, 46, 10, 127, 45, 35, 13, 127, 60, 58, 52, 88, 109, 109, 109, 25, 126, 125, 125, 0],\
 [123, 122, 123, 7, 77, 71, 60, 92, 70, 52, 11, 127, 67, 51, 15, 127, 53, 40, 14, 127, 58, 44, 14, 127, 60, 46, 14, 127, 64, 48, 16, 127, 68, 51, 15, 127, 72, 55, 16, 127, 76, 57, 17, 127, 78, 59, 17, 127, 78, 59, 17, 127, 63, 47, 15, 127, 55, 41, 12, 127, 64, 54, 34, 127, 120, 120, 121, 127, 113, 113, 113, 127, 89, 90, 90, 127, 104, 104, 104, 127, 60, 47, 25, 127, 77, 58, 15, 127, 78, 58, 17, 127, 77, 58, 17, 127, 78, 58, 17, 127, 76, 57, 12, 127, 67, 57, 39, 127, 123, 124, 125, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 94, 94, 94, 127, 8, 8, 8, 127, 2, 2, 2, 127, 101, 101, 101, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 72, 69, 65, 127, 64, 47, 11, 127, 78, 59, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 57, 14, 127, 58, 47, 24, 125, 108, 108, 110, 20, 117, 117, 120, 5, 99, 99, 100, 35, 79, 77, 73, 72, 58, 51, 38, 101, 57, 48, 26, 110, 57, 46, 21, 118, 58, 46, 20, 119, 58, 46, 19, 119, 54, 43, 19, 119, 48, 41, 24, 109, 63, 61, 58, 86, 106, 107, 107, 31, 124, 124, 123, 0, 125, 125, 124, 0, 124, 124, 124, 0],\
 [95, 93, 92, 50, 65, 51, 18, 125, 77, 58, 16, 127, 76, 57, 17, 127, 75, 57, 17, 127, 76, 57, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 78, 59, 17, 127, 66, 50, 17, 127, 43, 33, 13, 127, 74, 56, 17, 127, 67, 51, 17, 127, 72, 67, 58, 127, 89, 86, 81, 127, 79, 73, 65, 127, 65, 53, 29, 127, 74, 55, 15, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 78, 58, 15, 127, 61, 48, 24, 127, 111, 112, 114, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 125, 125, 125, 127, 82, 82, 82, 127, 69, 69, 69, 127, 122, 122, 122, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 111, 112, 113, 127, 59, 49, 27, 127, 76, 57, 15, 127, 77, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 72, 54, 12, 127, 58, 50, 34, 117, 113, 113, 115, 8, 118, 117, 119, 0, 117, 117, 118, 0, 124, 126, 125, 2, 116, 116, 115, 15, 107, 107, 107, 24, 96, 95, 94, 33, 95, 94, 92, 36, 96, 94, 92, 36, 95, 95, 93, 35, 105, 104, 105, 24, 119, 118, 120, 7, 125, 125, 125, 0, 120, 120, 119, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [86, 83, 80, 63, 66, 50, 12, 127, 77, 59, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 77, 58, 17, 127, 75, 57, 16, 127, 40, 30, 9, 127, 42, 32, 12, 127, 71, 54, 14, 127, 76, 58, 16, 127, 75, 56, 15, 127, 71, 53, 12, 127, 75, 56, 15, 127, 61, 47, 14, 127, 67, 51, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 78, 58, 17, 127, 64, 48, 12, 127, 90, 89, 87, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 72, 65, 52, 127, 72, 53, 9, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 59, 17, 127, 57, 41, 8, 127, 76, 73, 68, 79, 126, 127, 127, 0, 126, 126, 125, 0, 123, 123, 122, 0, 125, 126, 125, 0, 122, 123, 121, 0, 120, 120, 120, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [111, 111, 111, 25, 59, 48, 29, 120, 69, 51, 10, 127, 76, 58, 17, 127, 76, 58, 16, 127, 76, 58, 17, 127, 75, 57, 17, 127, 75, 56, 15, 127, 74, 55, 16, 127, 66, 49, 12, 127, 55, 40, 8, 127, 48, 37, 16, 126, 55, 50, 42, 102, 70, 69, 68, 77, 58, 52, 41, 104, 52, 41, 19, 125, 57, 43, 12, 127, 59, 44, 11, 127, 50, 39, 11, 127, 50, 37, 14, 127, 73, 55, 17, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 17, 127, 72, 53, 11, 127, 62, 56, 46, 127, 120, 120, 121, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 124, 124, 126, 127, 65, 57, 41, 127, 75, 55, 13, 127, 77, 58, 17, 127, 77, 58, 17, 127, 74, 55, 15, 127, 44, 34, 17, 127, 102, 101, 102, 39, 127, 127, 127, 0, 127, 127, 126, 0, 127, 127, 126, 0, 127, 127, 126, 0, 122, 122, 121, 0, 120, 120, 120, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 126, 0, 111, 111, 111, 25, 68, 65, 59, 87, 55, 47, 32, 104, 53, 45, 28, 107, 53, 45, 28, 107, 52, 44, 29, 107, 56, 49, 37, 102, 55, 49, 39, 99, 64, 62, 57, 87, 93, 93, 92, 49, 109, 109, 109, 20, 124, 123, 124, 0, 127, 127, 127, 0, 118, 119, 118, 0, 110, 111, 111, 15, 100, 100, 100, 36, 96, 96, 96, 44, 79, 78, 74, 72, 62, 51, 24, 117, 72, 54, 14, 127, 77, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 78, 58, 18, 127, 64, 46, 11, 127, 76, 73, 68, 127, 125, 126, 126, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 84, 84, 82, 127, 59, 45, 15, 127, 78, 59, 17, 127, 77, 58, 17, 127, 73, 55, 15, 127, 48, 36, 12, 126, 84, 82, 81, 66, 127, 127, 127, 0, 127, 127, 126, 0, 126, 127, 125, 0, 126, 127, 125, 0, 127, 127, 125, 0, 121, 122, 120, 0, 120, 120, 120, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 124, 0, 122, 122, 122, 7, 109, 109, 110, 18, 105, 105, 105, 21, 106, 106, 106, 21, 105, 106, 105, 21, 113, 113, 114, 16, 115, 115, 116, 14, 120, 120, 120, 7, 127, 127, 127, 0, 120, 120, 119, 0, 122, 120, 121, 0, 126, 125, 125, 0, 116, 116, 115, 0, 122, 122, 121, 0, 127, 127, 127, 0, 126, 126, 126, 0, 124, 124, 125, 4, 105, 104, 102, 36, 65, 57, 41, 107, 63, 46, 8, 127, 76, 58, 16, 127, 77, 59, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 76, 58, 17, 127, 77, 58, 16, 127, 60, 47, 21, 127, 93, 93, 92, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 100, 100, 102, 127, 57, 47, 24, 127, 75, 57, 14, 127, 76, 58, 17, 127, 74, 55, 15, 127, 48, 35, 8, 127, 71, 68, 65, 81, 122, 121, 122, 8, 126, 125, 124, 0, 125, 126, 124, 0, 125, 126, 124, 0, 125, 126, 124, 0, 125, 126, 124, 0, 121, 122, 121, 0, 120, 120, 120, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 120, 119, 118, 0, 121, 120, 120, 0, 126, 125, 125, 0, 115, 116, 115, 0, 122, 122, 120, 0, 127, 127, 126, 0, 124, 124, 124, 0, 125, 124, 123, 0, 126, 126, 126, 0, 121, 122, 122, 10, 85, 84, 81, 63, 61, 52, 33, 108, 62, 47, 14, 127, 73, 55, 14, 127, 77, 58, 18, 127, 76, 58, 17, 127, 77, 58, 17, 127, 77, 57, 16, 127, 57, 44, 16, 127, 85, 83, 78, 127, 120, 120, 121, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 126, 126, 127, 127, 116, 117, 117, 127, 100, 99, 97, 127, 76, 71, 60, 127, 62, 48, 21, 127, 77, 58, 17, 127, 73, 55, 17, 127, 45, 33, 10, 127, 43, 31, 11, 125, 74, 72, 68, 80, 124, 124, 125, 5, 125, 125, 125, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 125, 125, 125, 0, 121, 122, 121, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 120, 119, 118, 0, 121, 120, 120, 0, 126, 125, 125, 0, 115, 116, 115, 0, 122, 122, 120, 0, 127, 127, 126, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 127, 126, 127, 0, 126, 127, 126, 0, 108, 109, 108, 22, 80, 77, 72, 71, 58, 50, 31, 113, 63, 47, 11, 127, 76, 57, 16, 127, 77, 58, 17, 127, 77, 58, 17, 127, 76, 57, 16, 127, 63, 49, 18, 127, 72, 66, 53, 127, 93, 92, 88, 127, 104, 102, 102, 127, 97, 95, 91, 127, 83, 79, 72, 127, 68, 60, 43, 127, 65, 51, 21, 127, 72, 54, 16, 127, 78, 59, 16, 127, 68, 52, 16, 127, 42, 30, 9, 127, 31, 24, 12, 127, 80, 78, 75, 69, 121, 122, 122, 9, 127, 127, 126, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 126, 0, 123, 123, 122, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 120, 119, 118, 0, 121, 120, 120, 0, 126, 125, 125, 0, 115, 116, 115, 0, 122, 122, 120, 0, 127, 127, 126, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 126, 126, 127, 0, 127, 127, 125, 0, 127, 127, 127, 0, 127, 127, 127, 0, 120, 120, 121, 8, 87, 86, 84, 61, 58, 52, 36, 104, 61, 48, 20, 122, 68, 51, 15, 127, 72, 54, 13, 127, 53, 39, 14, 127, 63, 48, 14, 127, 65, 49, 12, 127, 66, 49, 13, 127, 68, 50, 13, 127, 71, 54, 14, 127, 74, 57, 15, 127, 72, 54, 17, 127, 65, 49, 16, 127, 49, 36, 12, 127, 32, 23, 7, 127, 51, 46, 38, 105, 106, 106, 105, 34, 126, 125, 126, 0, 124, 124, 124, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 125, 0, 127, 127, 126, 0, 123, 123, 122, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 120, 119, 118, 0, 121, 120, 120, 0, 126, 125, 125, 0, 115, 116, 115, 0, 122, 122, 120, 0, 127, 127, 126, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 125, 125, 126, 0, 126, 126, 125, 0, 125, 125, 124, 0, 125, 125, 123, 0, 127, 127, 125, 0, 127, 127, 126, 1, 114, 114, 113, 18, 99, 97, 96, 44, 77, 72, 64, 79, 65, 56, 37, 112, 57, 44, 14, 127, 64, 48, 15, 127, 60, 45, 14, 127, 61, 46, 14, 127, 62, 46, 15, 127, 60, 46, 15, 127, 62, 47, 16, 127, 62, 47, 16, 127, 53, 39, 10, 127, 37, 29, 12, 127, 74, 72, 68, 77, 114, 114, 114, 20, 127, 126, 126, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 126, 126, 0, 124, 123, 123, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 120, 119, 118, 0, 121, 120, 121, 0, 126, 125, 125, 0, 122, 122, 122, 0, 124, 124, 124, 0, 125, 125, 125, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 125, 125, 125, 0, 122, 122, 122, 2, 119, 119, 119, 1, 121, 121, 121, 0, 122, 122, 121, 0, 119, 120, 119, 5, 116, 116, 116, 17, 105, 104, 102, 33, 55, 52, 47, 101, 46, 33, 8, 127, 67, 50, 14, 127, 72, 55, 18, 127, 78, 59, 18, 127, 79, 59, 17, 127, 79, 59, 18, 127, 74, 56, 14, 127, 52, 39, 12, 127, 57, 53, 46, 98, 99, 100, 100, 39, 126, 127, 126, 0, 127, 127, 123, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 125, 0, 127, 125, 126, 0, 124, 122, 123, 0, 114, 114, 114, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 126, 0, 123, 122, 121, 0, 122, 122, 122, 0, 124, 124, 123, 0, 124, 124, 124, 0, 123, 123, 123, 0, 123, 123, 123, 0, 123, 123, 123, 0, 123, 123, 123, 0, 123, 123, 123, 0, 123, 123, 123, 0, 125, 125, 125, 0, 111, 111, 111, 25, 91, 91, 91, 56, 98, 98, 98, 44, 88, 88, 88, 58, 66, 65, 66, 81, 53, 52, 49, 101, 46, 41, 31, 117, 44, 35, 19, 123, 52, 46, 31, 111, 72, 69, 62, 85, 63, 55, 41, 115, 72, 55, 15, 127, 73, 55, 15, 127, 59, 43, 9, 127, 43, 32, 12, 127, 68, 66, 61, 84, 117, 117, 118, 15, 125, 126, 126, 0, 124, 125, 124, 0, 125, 126, 124, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 125, 0, 126, 125, 126, 0, 124, 123, 123, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 125, 0, 125, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 121, 121, 121, 0, 113, 113, 113, 7, 109, 109, 109, 13, 116, 116, 116, 11, 118, 118, 118, 9, 120, 120, 120, 8, 119, 118, 118, 14, 112, 112, 113, 16, 105, 105, 106, 27, 79, 74, 66, 79, 50, 39, 14, 127, 50, 41, 20, 117, 51, 46, 36, 101, 73, 72, 70, 72, 106, 107, 108, 29, 126, 126, 124, 0, 126, 127, 123, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 125, 125, 125, 0, 123, 123, 123, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [127, 127, 124, 0, 127, 127, 122, 0, 124, 124, 124, 0, 117, 117, 117, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 120, 119, 120, 0, 120, 120, 121, 0, 122, 122, 122, 0, 127, 127, 125, 0, 125, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 121, 121, 121, 0, 116, 116, 116, 0, 115, 115, 115, 0, 122, 122, 122, 0, 121, 121, 121, 0, 120, 120, 120, 2, 101, 101, 101, 46, 76, 76, 75, 77, 59, 59, 59, 93, 67, 64, 58, 84, 80, 77, 71, 67, 97, 97, 97, 33, 112, 112, 112, 15, 115, 116, 116, 2, 120, 121, 121, 0, 124, 124, 122, 0, 125, 126, 123, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 124, 124, 124, 0, 125, 125, 125, 0, 123, 123, 123, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 115, 115, 115, 0, 117, 116, 117, 0, 120, 120, 121, 0, 123, 123, 123, 0, 120, 120, 118, 0, 124, 124, 123, 0, 124, 124, 124, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\
 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]])

if(__name__ == "__main__"):
    load_RegistryInfo()
    Blender.Draw.Register(draw,event,bevent)