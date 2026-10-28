"""Same indexed background pixels, at most 1024 scratch ints, fewer native calls."""
def patch(name,text):
    if name!='IndexedBackground.java':return text
    begin=text.index('        // A narrow exposed band')
    end=text.index('\n    public void paintPanel(',begin)
    text=text[:begin]+'''        // Limit scratch to 4 KiB at phone widths. A full 128x148 background
        // takes 19 driver calls instead of 148, without an extra Image or buffer.
        int lines=Math.max(1,Math.min(n4-n3,1024/n5));
        int capacity=n5*lines;
        if(row==null || row.length<capacity)row=new int[capacity];
        for(int y=n3;y<n4;y+=lines){
            int rows=Math.min(lines,n4-y),at=0;
            for(int yy=0;yy<rows;yy++){
                int source=(y+yy-viewport.top)*height/viewport.drawHeight*width;
                for(int x=n;x<n2;x++)row[at++]=palette[pixels[source+(x-viewport.left)*width/viewport.drawWidth]&255];
            }
            graphics.drawRGB(row,0,n5,n,y,n5,rows,false);
        }
    }
''' +text[end:]
    return text
