"""Hold new gameplay audio until its Canvas has completed a full paint.

Existing players continue while a frame is being prepared. MMAPI stays on its
worker; the presentation gate never holds a Canvas lock during native audio IO.
"""
def patch(name,text):
    def replace(old,new):
        nonlocal text
        assert text.count(old)==1,(name,old)
        text=text.replace(old,new)
    if name=='GameAudio.java':
        replace('private boolean menuMode;','''private boolean menuMode;
    private boolean visualGate,pictureReady=true;private int pictureTicket;
    public synchronized int presentationTicket(){return pictureTicket;}
    public synchronized void presentationDriven(){visualGate=true;pictureReady=false;pictureTicket++;notifyAll();}
    public synchronized void holdPresentation(){if(visualGate){pictureReady=false;pictureTicket++;notifyAll();}}
    public synchronized boolean presentationPending(){return visualGate&&!menuMode&&!pictureReady;}
    public synchronized void framePresented(){framePresented(pictureTicket);}
    public synchronized void framePresented(int ticket){if(ticket==pictureTicket){pictureReady=true;notifyAll();}}''')
        replace('menuMode=value;generation++;','menuMode=value;if(visualGate&&!value){pictureReady=false;pictureTicket++;}generation++;')
        replace('scene=value;track=1;','scene=value;if(visualGate){pictureReady=false;pictureTicket++;}track=1;')
        replace('track=value;notifyAll();','if(track!=value&&visualGate&&!menuMode){pictureReady=false;pictureTicket++;}track=value;notifyAll();')
        replace('if(count<queue.length) queue[count++]=cue;','if(visualGate&&!menuMode){pictureReady=false;pictureTicket++;}if(count<queue.length) queue[count++]=cue;')
        replace('boolean running,loop;','boolean running,loop,ready;')
        replace('running=active&&enabled;','running=active&&enabled;ready=!visualGate||loop||pictureReady;')
        replace('if(running && count>0)', 'if(running && ready && count>0)')
        replace('if(running!=wasActive) {','if(running!=wasActive && (!running||ready)) {')
        replace('if(running && !musicAttempted)', 'if(running && ready && !musicAttempted)')
        replace('(!running || count==0)', '(!running || !ready || count==0)')
        replace('wasActive==(active&&enabled))','(!ready||wasActive==(active&&enabled)))')
        replace('applied==generation &&','applied==generation && ready==(!visualGate||menuMode||pictureReady) && running==(active&&enabled) &&')
    if name=='PilotsMidlet.java':
        replace('this.audio=new GameAudio();','this.audio=new GameAudio();this.audio.presentationDriven();')
        replace('this.shown = true;','this.shown = true;this.audio.holdPresentation();this.repaint();')
        replace('if (bl) {\n                DebugHud.changed();','if (bl || this.audio.presentationPending()) {\n                DebugHud.changed();')
        # Debug instrumentation already wraps the real drawing, so failed drawing
        # never acknowledges a new frame. Cursor-only repaints cannot acknowledge it.
        replace('try{paintMeasured(graphics);}finally{','''int ticket=this.audio.presentationTicket();boolean complete=this.scene!=null&&this.view!=null&&graphics.getClipX()<=0&&graphics.getClipY()<=0&&graphics.getClipX()+graphics.getClipWidth()>=this.view.width&&graphics.getClipY()+graphics.getClipHeight()>=this.view.height;
            try{paintMeasured(graphics);}finally{''')
        replace('count,shown);}\n        }\n\n        private void paintMeasured',
                'count,shown);}\n            if(complete)this.audio.framePresented(ticket);\n        }\n\n        private void paintMeasured')
        for canvas in ('fridge','switchboard','basement'):
            replace('this.display.setCurrent(this.'+canvas+');','this.'+canvas+'.presentationAudio(this.canvas.audio);this.display.setCurrent(this.'+canvas+');')
    if name in ('FridgeCanvas.java','SwitchboardCanvas.java','BasementCanvas.java'):
        replace('private final Host host;','''private final Host host;
    private GameAudio presentationAudio;
    public void presentationAudio(GameAudio audio){presentationAudio=audio;audio.holdPresentation();}''')
        replace('try{paintMeasured(g);}finally{','''int ticket=presentationAudio==null?0:presentationAudio.presentationTicket();boolean complete=g.getClipX()<=0&&g.getClipY()<=0&&g.getClipX()+g.getClipWidth()>=getWidth()&&g.getClipY()+g.getClipHeight()>=getHeight();
        try{paintMeasured(g);}finally{''')
        replace('DebugHud.paint(g,getWidth(),getHeight(),0,0);}}\n    private void paintMeasured',
                'DebugHud.paint(g,getWidth(),getHeight(),0,0);}\n        if(complete&&presentationAudio!=null)presentationAudio.framePresented(ticket);\n    }\n    private void paintMeasured')
    return text
