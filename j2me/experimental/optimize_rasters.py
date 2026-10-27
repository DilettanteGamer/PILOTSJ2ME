"""Move nearest-neighbour column division out of the per-row pixel loop."""
def patch(name, text):
    if name == 'RasterCache.java':
        return text.replace('    public Image get() {', '    public Image peek() { return image; }\n    public Image get() {')
    if name != 'AnimationClip.java':
        return text
    old = 'private int[] row;'
    assert text.count(old) == 1
    text = text.replace(old, old + '\n    private int[] sampleColumns;')
    text = text.replace('raster.prepare(area*2);','if(tiled)FrameRasters.prepare(area*2);raster.prepare(area*2);')
    old = '''int[] rgb=new int[area];
                for(int yy=0;yy<rasterHeight;yy++) {'''
    assert text.count(old) == 1
    text = text.replace(old, '''int[] rgb=new int[area];
                sampleColumns=PixelSampling.columns(sampleColumns,width,drawWidth,rasterX,rasterWidth);
                for(int yy=0;yy<rasterHeight;yy++) {''')
    old = 'pixels[sourceRow+(rasterX+xx)*width/drawWidth]'
    assert text.count(old) == 1
    return text.replace(old, 'pixels[sourceRow+sampleColumns[xx]]')
