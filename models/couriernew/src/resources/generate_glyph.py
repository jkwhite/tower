#!/Applications/blender.app/Contents/MacOS/blender -P
#!/home/jkw/blender/blender -P

# """
# Name: 'Generate roguelike avatars'
# Blender: 233
# Group: 'Wizard'
# Tip: 'Generates ASCII characters for roguelike games'
# """

import bpy, sys, getopt, os, mathutils, struct, string, types
from mathutils import Vector
from os import path

def main(argv):
  try:
    opts, args = getopt.getopt(argv,"P:c:e:f:B:b:r:d:",[])
  except getopt.GetoptError as a:
    print('generate_glyph.py -c glyph -e extrude -f filename -b bevel -r res -d dmg', a)
    sys.exit(2)
  for opt, arg in opts:
    if opt == '-h':
      print('generate_glyph.py -c glyph -e extrude -f filename -b bevel -r res -d dmg')
      sys.exit()
    elif opt in ("-c"):
      glyph = arg
    elif opt in ("-e"):
      extrude = arg
    elif opt in ("-b"):
      bevel = arg
    elif opt in ("-B"):
      bevel_resolution = arg
    elif opt in ("-r"):
      res = arg
    elif opt in ("-d"):
      dmg = arg
    elif opt in ("-f"):
      filename = arg

  #bpy.ops.scene.new
  # Delete all old objects, so we start with a clean slate.
  scn = bpy.context.scene
  for ob in scn.objects:
    scn.objects.active = ob
    #print("Delete", ob, bpy.context.object)
    bpy.ops.object.mode_set(mode='OBJECT')
    scn.objects.unlink(ob)
    del ob
  bpy.ops.font.open(filepath="/Library/Fonts/Courier New.ttf")
  file = filename+'_'+str(res)+'_'+str(dmg)
  print('generating '+file+' ...')
  bpy.ops.object.text_add(location=(0,0,0))
  txt = bpy.context.object
  curve = txt.data
  curve.body = glyph
  curve.font = bpy.data.fonts['CourierNewPSMT']
  curve.size = 1.5
  curve.extrude = float(extrude)
  curve.bevel_depth = float(bevel)
  curve.bevel_resolution = float(bevel_resolution)
  bpy.ops.object.convert(target='MESH')
  bpy.ops.mesh.uv_texture_add()
  #bpy.ops.wm.save_as_mainfile(filepath=file+'.blend', check_existing=False, compress=True)
  bpy.ops.ogre.export(EX_SCENE=False, EX_lodLevels=0, filepath=file+'.ogre.xml')


if __name__ == "__main__":
   main(sys.argv[1:])
