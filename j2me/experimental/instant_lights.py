"""Expose the existing dark background at the first light-off stage; no fade-mask IO."""
def patch(name,text):
 if name!='HotelScene.java':return text
 old='t.y>=417 || hiddenGrid(t.id) || ladderMovie(t)'
 assert text.count(old)==1
 return text.replace(old,old+' || animals.world.scene==13 && t.clip>=205 && t.clip<=210')
