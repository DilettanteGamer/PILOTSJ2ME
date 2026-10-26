"""Add diagnostics to the experimental sources without changing stable release code."""
def patch(name,text):
 if name=='PilotsMidlet.java':
  text=text.replace('if(n==-6||n==48)return;','if(n==-6){if(!bl){DebugHud.toggle();this.repaint();}return;}if(n==48)return;')
  text=text.replace('public synchronized void goToScene(int n) throws IOException {','public synchronized void goToScene(int n) throws IOException {\n            DebugHud.scene(n);')
  text=text.replace('public synchronized void goToScene(int n) throws IOException {','public synchronized void goToScene(int n) throws IOException {long debugStarted=System.currentTimeMillis();try{goToSceneMeasured(n);}finally{DebugHud.sceneCost(System.currentTimeMillis()-debugStarted);}}\n        private void goToSceneMeasured(int n) throws IOException {')
  text=text.replace('protected synchronized void showNotify() {','protected synchronized void showNotify() {\n            DebugHud.resume();')
  text=text.replace('this.serviceRepaints();','Canvas debugTarget;int debugHeight;\n                        synchronized(this){debugTarget=this.shown?(Canvas)this:PilotsMidlet.this.basement;debugHeight=debugTarget==this&&this.view!=null?this.view.height:debugTarget==null?0:debugTarget.getHeight();}\n                        if(debugTarget!=null){DebugHud.pulse(debugTarget,debugHeight);debugTarget.serviceRepaints();}')
  text=text.replace('synchronized void tick(long l) throws IOException {','synchronized void tick(long l) throws IOException {\n            long debugStarted=System.currentTimeMillis();try{tickMeasured(l);}finally{DebugHud.tick(System.currentTimeMillis()-debugStarted);}\n        }\n\n        private void tickMeasured(long l) throws IOException {')
  text=text.replace('if (bl) {\n                this.repaint();','if (bl) {\n                DebugHud.changed();this.repaint();')
  text=text.replace('protected synchronized void paint(Graphics graphics) {','''protected synchronized void paint(Graphics graphics) {
            long debugStarted=System.currentTimeMillis();try{paintMeasured(graphics);}finally{long now=System.currentTimeMillis();DebugHud.painted(now,now-debugStarted);int count=this.scene==null?0:this.scene.actors.length,shown=0;for(int i=0;i<count;i++)if(this.scene.actors[i]!=null&&this.scene.actors[i].visible)shown++;DebugHud.paint(graphics,getWidth(),this.view==null?getHeight():this.view.height,count,shown);}
        }

        private void paintMeasured(Graphics graphics) {''')
 if name=='HelpCanvas.java':
  text=text.replace('\"* — масштаб\"','\"* — масштаб\",\"Левая — счётчики\"')
 if name=='GameAudio.java':
  text=text.replace('reap();','reap();DebugHud.audioState(count,(effects[0]==null?0:1)+(effects[1]==null?0:1)+(effects[2]==null?0:1));')
  text=text.replace('if(count<queue.length) queue[count++]=cue;','if(count<queue.length) queue[count++]=cue;DebugHud.audioState(count,(effects[0]==null?0:1)+(effects[1]==null?0:1)+(effects[2]==null?0:1));')
  text=text.replace('private void startEffect(int location,int cue) {','private void startEffect(int location,int cue) {long debugStarted=System.currentTimeMillis();try{startEffectMeasured(location,cue);}finally{DebugHud.audio(System.currentTimeMillis()-debugStarted);}}\n    private void startEffectMeasured(int location,int cue) {')
 if name in ('FridgeCanvas.java','SwitchboardCanvas.java','BasementCanvas.java'):
  text=text.replace('protected void showNotify(){','protected void showNotify(){DebugHud.resume();').replace('protected void showNotify() {','protected void showNotify() {DebugHud.resume();')
  text=text.replace('protected void paint(Graphics g)', '''protected void paint(Graphics g){long debugStarted=System.currentTimeMillis();try{paintMeasured(g);}finally{long now=System.currentTimeMillis();DebugHud.painted(now,now-debugStarted);DebugHud.paint(g,getWidth(),getHeight(),0,0);}}
    private void paintMeasured(Graphics g)''')
  text=text.replace('public void input(int key){','public void input(int key){if(key==-6){DebugHud.toggle();repaint();return;}').replace('public void input(int key) {','public void input(int key) {if(key==-6){DebugHud.toggle();repaint();return;}')
  text=text.replace('protected void keyRepeated(int key){','protected void keyRepeated(int key){if(key==-6)return;').replace('protected void keyRepeated(int key) {','protected void keyRepeated(int key) {if(key==-6)return;')
 return text
