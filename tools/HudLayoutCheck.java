import de.nevergate.fahrzeuge.client.HudLayout;
public final class HudLayoutCheck {
 public static void main(String[] args) {
  int checks=0;
  for(int[] size:new int[][]{{640,480},{854,480},{1280,720},{1920,1009},{1920,1080},{2560,1440},{3840,2160}})
   for(int gui=1;gui<=8;gui++)for(double preference:new double[]{0.6,1,1.4,Double.NaN,Double.POSITIVE_INFINITY})for(boolean details:new boolean[]{false,true}) {
    int w=(int)Math.ceil(size[0]/(double)gui),h=(int)Math.ceil(size[1]/(double)gui);
    HudLayout l=HudLayout.fit(w,h,gui,preference,details);
    double left=l.x()*l.scale()*gui,top=l.y()*l.scale()*gui;
    double pw=l.width()*l.scale()*gui,ph=l.height()*l.scale()*gui;
    require(Double.isFinite(l.scale())&&l.scale()>0,"Invalid scale");
    require(left>=0&&top>=0&&left+pw<=w*gui+0.01&&top+ph<=h*gui+0.01,"HUD outside viewport");
    require(pw<=w*gui*0.28+0.01,"HUD too wide");
    require(ph<=h*gui*(details?0.46:0.16)+0.01,"HUD too tall");
    require((top+ph)/gui<=h-Math.max(32.0,56.0/gui)+0.01,"Overlaps hotbar");
    checks++;
   }
  double reference=HudLayout.fit(1920,1008,1,1,false).scale()*200;
  for(int gui:new int[]{2,3,4,6}) {
   HudLayout l=HudLayout.fit(1920/gui,1008/gui,gui,1,false);
   require(Math.abs(l.width()*l.scale()*gui-reference)<0.01,"HUD grows with Minecraft GUI scale");
  }
  System.out.println("PASS: "+checks+" viewport/scale/detail cases; GUI-scale independence checked");
 }
 static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
