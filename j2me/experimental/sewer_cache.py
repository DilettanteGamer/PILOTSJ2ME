"""Expand sewer raster keys/partition within the unchanged shared pixel ceiling."""
def patch(name,text):
    def replace(old,new):
        nonlocal text
        assert text.count(old)==1,(name,old)
        text=text.replace(old,new)
    if name=='SewerScene.java':
        replace('new AnimatedActor(ids[i],"/scenes/07-"+ids[i]+".anm",0,0)', 'new AnimatedActor(ids[i],"/scenes/07-"+ids[i]+".anm",0,0,true)')
        replace('new AnimatedActor(id,"/scenes/07-"+id+".anm",0,0)', 'new AnimatedActor(id,"/scenes/07-"+id+".anm",0,0,true)')
    if name=='FrameRasters.java':
        replace('private static final int COUNT=64,LIMIT=32768;', 'private static final int COUNT=192;private static int LIMIT=32768,activeCount=64;')
        text=text.replace('i<COUNT','i<activeCount')
        replace(' public static int hits,stores,requests;', ''' public static int hits,stores,requests;
 public static synchronized void sewerMode(boolean enabled){int count=enabled?COUNT:64;if(activeCount==count)return;clear();for(int i=count;i<COUNT;i++)rasters[i]=null;activeCount=count;LIMIT=enabled?45056:32768;}''')
    if name=='PilotsMidlet.java':
        replace('RasterCache.budget(cachePixels);', 'RasterCache.budget(cachePixels);FrameRasters.sewerMode(n==7);')
    return text
