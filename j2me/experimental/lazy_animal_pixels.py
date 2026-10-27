"""Keep source animation metadata current; fetch AnimalScene pixels on first use."""
def patch(name,text):
 def replace(old,new):
  nonlocal text
  assert text.count(old)==1,(name,old)
  text=text.replace(old,new)
 if name=='AnimationClip.java':
  replace('private byte[] encoded;', '''private byte[] encoded;
    private boolean deferred,pixelsReady;
    private IOException pixelFailure;''')
  replace('''    public AnimationClip(String string) throws IOException {
        this.resource = string;''','''    public AnimationClip(String string) throws IOException { this(string,false); }
    public AnimationClip(String string,boolean defer) throws IOException {
        deferred=defer;this.resource = string;''')
  for signature in ('public void restart() throws IOException {','public void seek(int target) throws IOException {','public boolean advance() throws IOException {'):
   replace(signature,signature+'\n        if(pixelFailure!=null)throw pixelFailure;')
  replace('if (this.pixels == null || this.pixels.length < n * n2) {','if (!(deferred&&tiled) && (this.pixels == null || this.pixels.length < n * n2)) {')
  replace('if(tiled){tileImage=input.readInt();TileStore.fill(tileImage,pixels,n,n2);}', 'if(tiled){tileImage=input.readInt();pixelsReady=false;if(!deferred){TileStore.fill(tileImage,pixels,n,n2);pixelsReady=true;}}')
  replace('''    public boolean contains(int x,int y) {
        x-=offsetX;y-=offsetY;
        return pixels!=null && x>=0 && y>=0 && x<width && y<height && (pixels[y*width+x]&255)!=transparent;
    }''','''    private boolean ensurePixels() {
        if(pixelFailure!=null)return false;
        if(!tiled || pixelsReady)return pixels!=null;
        try {
            int size=width*height;if(pixels==null || pixels.length<size)pixels=new byte[size];
            TileStore.fill(tileImage,pixels,width,height);pixelsReady=true;return true;
        } catch(IOException failure) {pixelFailure=failure;return false;}
    }
    public boolean contains(int x,int y) {
        x-=offsetX;y-=offsetY;
        return x>=0 && y>=0 && x<width && y<height && ensurePixels() && (pixels[y*width+x]&255)!=transparent;
    }''')
  replace('if (pixels == null) return;', 'if (pixels == null && !(deferred&&tiled)) return;')
  replace('''        raster.clear();
        int area=rasterWidth*rasterHeight;''','''        if(!ensurePixels())return;
        raster.clear();
        int area=rasterWidth*rasterHeight;''')
 if name=='AnimatedActor.java':
  replace('''    public AnimatedActor(int n, String string, int n2, int n3) throws IOException {
        this.id = n;''','''    public AnimatedActor(int n,String string,int n2,int n3) throws IOException {this(n,string,n2,n3,false);}
    public AnimatedActor(int n,String string,int n2,int n3,boolean deferred) throws IOException {
        this.id = n;''')
  replace('this.clip = new AnimationClip(string);','this.clip = new AnimationClip(string,deferred);')
 if name=='AnimalScene.java':
  replace('new AnimatedActor(id,"/scenes/04-"+id+".anm",t.x,t.y)','new AnimatedActor(id,"/scenes/04-"+id+".anm",t.x,t.y,true)')
 return text
