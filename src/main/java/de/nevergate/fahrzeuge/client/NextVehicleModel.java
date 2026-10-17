package de.nevergate.fahrzeuge.client;
import de.nevergate.fahrzeuge.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Dev12's separate SUV, low sports coupe, four-door taxi and tractor cab meshes. */
public final class NextVehicleModel {
 private final ModelPart root;private final VehicleSpec spec;private final float width,length,front,rear,roof;
 private final java.util.Map<NgCityEntity,float[]> states=new java.util.WeakHashMap<>();
 private static CubeListBuilder cube(float x,float y,float z,float w,float h,float d){return CubeListBuilder.create().addBox(x,y,z,w,h,d);}
 private static void add(PartDefinition p,String n,CubeListBuilder b){p.addOrReplaceChild(n,b,PartPose.ZERO);}
 public NextVehicleModel(VehicleSpec s){spec=s;width=s==VehicleSpec.TRACTOR?19:s==VehicleSpec.SPORT?15:15.4F;length=s.length*8;front=s==VehicleSpec.TRACTOR?-30:s==VehicleSpec.SPORT?-23:-24;rear=s==VehicleSpec.TRACTOR?26:s==VehicleSpec.SPORT?22:23;roof=s==VehicleSpec.TRACTOR?-26:s==VehicleSpec.OFFROAD?-7:s==VehicleSpec.SPORT?5:-1;
  var mesh=new MeshDefinition();var p=mesh.getRoot();if(s==VehicleSpec.TRACTOR)tractor(p);else passenger(p);
  float wheelY=s==VehicleSpec.OFFROAD?18:18.7F,radius=s==VehicleSpec.OFFROAD?6:5.2F;
  for(int side:new int[]{-1,1})for(int axle=0;axle<(s==VehicleSpec.TRACTOR?3:2);axle++){float z=axle==0?front:axle==1?rear:rear-12;String n="wheel"+side+"_"+axle;var w=p.addOrReplaceChild(n,CubeListBuilder.create(),PartPose.offset(side*(width-.2F),wheelY,z));for(int i=0;i<8;i++)w.addOrReplaceChild("rubber"+i,cube(-1.8F,-radius,-2.3F,3.6F,2.2F,4.6F),PartPose.rotation(i*(float)Math.PI/4,0,0));w.addOrReplaceChild("rim",cube(-1.9F,-3,-3,3.8F,6,6),PartPose.ZERO);w.addOrReplaceChild("hub",cube(-2,-1.2F,-1.2F,4,2.4F,2.4F),PartPose.ZERO);}
  add(p,"taxi",cube(-8,roof-4,-3,16,3,6));add(p,"coupling",cube(-6,9.5F,11,12,1.5F,12).addBox(-1,9,12,2,2,7));add(p,"latch",cube(-1,0,-.5F,5,1,1));
  root=LayerDefinition.create(mesh,128,128).bakeRoot();
 }
 private void passenger(PartDefinition p){boolean sport=spec==VehicleSpec.SPORT,off=spec==VehicleSpec.OFFROAD;float cowl=sport?12:9,back=sport?15:23,wind=sport?-10:-15;
  var body=cube(-width,17,-length,2*width,1.2F,2*length).addBox(-width,roof,wind,2*width,1.2F,back-wind).addBox(-width,cowl,-length,2*width,1.2F,wind+length).addBox(-width,12,back,2*width,5,length-back);
  for(int side:new int[]{-1,1}){float x=side<0?-width:width-1.2F;body.addBox(x,cowl,-length,1.2F,6,front-5+length).addBox(x,cowl,rear+5,1.2F,6,length-rear-5);body.addBox(x,roof+1,wind,1.2F,cowl-roof,1).addBox(x,roof+1,back-1,1.2F,cowl-roof,1);}
  if(sport)for(int side:new int[]{-1,1})body.addBox(side<0?-width:width-1.2F,cowl,3,1.2F,17-cowl,back-3);
  add(p,"body",body);add(p,"trim",cube(-width-1,18,-length-1,2*width+2,2,2).addBox(-width-1,18,length-1,2*width+2,2,2).addBox(-width-1,18,front+5,1,2,rear-front-10).addBox(width,18,front+5,1,2,rear-front-10));
  var glass=cube(-width+1,roof+1,wind-.1F,2*width-2,cowl-roof-1,.18F).addBox(-width+1,roof+1,back+.05F,2*width-2,cowl-roof-1,.18F);if(sport)for(int side:new int[]{-1,1})glass.addBox(side<0?-width-.04F:width-.14F,roof+2,4,.18F,cowl-roof-2,back-5);add(p,"glass",glass);
  add(p,"seats",cube(-11,14,-8,8,2,10).addBox(3,14,-8,8,2,10).addBox(-11,sport?7:5,0,8,sport?7:9,2).addBox(3,sport?7:5,0,8,sport?7:9,2));
  if(!sport)add(p,"rearSeats",cube(-12,14,10,24,2,9).addBox(-12,5,18,24,9,2));else add(p,"rearSeats",CubeListBuilder.create());
  add(p,"dash",cube(-width+1,cowl,wind,2*width-2,3,4).addBox(4,cowl-1,wind+3,6,1,3));
  for(int i=0;i<4;i++){boolean right=(i&1)==0,rearDoor=i>=2;float z=rearDoor?3:wind,depth=rearDoor?back-3:3-wind;float x=right?width-1.1F:-width;var panel=rearDoor&&sport?CubeListBuilder.create():cube(x,cowl,z,1.1F,17-cowl,depth).addBox(x,roof+1,z,1.1F,.7F,depth).addBox(x,roof+1,z+depth-1,1.1F,cowl-roof,1);add(p,"door"+i,panel);add(p,"doorGlass"+i,rearDoor&&sport?CubeListBuilder.create():cube(right?width-.14F:-width-.04F,roof+2,z+1,.18F,cowl-roof-2,depth-2));}
  add(p,"head",cube(-width+2,cowl+1,-length-.25F,7,3,.35F).addBox(width-9,cowl+1,-length-.25F,7,3,.35F));add(p,"tail",cube(-width+1,13,length+.1F,4,3,.35F).addBox(width-5,13,length+.1F,4,3,.35F));
  if(off){add(p,"rack",cube(-width+2,roof-2,-7,2*width-4,1,22).addBox(-width+2,roof-3,-7,1,2,22).addBox(width-3,roof-3,-7,1,2,22));}else add(p,"rack",CubeListBuilder.create());
 }
 private void tractor(PartDefinition p){add(p,"body",cube(-19,-26,-43,38,2,28).addBox(-19,-24,-43,2,20,2).addBox(17,-24,-43,2,20,2).addBox(-19,-24,-16,38,34,2).addBox(-19,1,-44,38,15,3));
  add(p,"trim",cube(-18,17,-43,36,3,87).addBox(-20,15,-46,40,3,3));add(p,"glass",cube(-17,-22,-43.2F,34,20,.2F));add(p,"seats",cube(-14,7,-29,10,3,10).addBox(4,7,-29,10,3,10).addBox(-14,-4,-21,10,11,2).addBox(4,-4,-21,10,11,2));add(p,"rearSeats",CubeListBuilder.create());add(p,"dash",cube(-17,-1,-41,34,6,6));
  for(int i=0;i<4;i++){boolean right=i%2==0;add(p,"door"+i,i>=2?CubeListBuilder.create():cube(right?17:-19,-4,-40,2,19,24).addBox(right?17:-19,-24,-40,2,1,24).addBox(right?17:-19,-24,-17,2,20,1));add(p,"doorGlass"+i,i>=2?CubeListBuilder.create():cube(right?18.9F:-19.1F,-22,-39,.18F,18,21));}
  add(p,"head",cube(-16,6,-44.3F,7,3,.4F).addBox(9,6,-44.3F,7,3,.4F));add(p,"tail",cube(-15,17,43,5,2,.4F).addBox(10,17,43,5,2,.4F));add(p,"rack",CubeListBuilder.create());
 }
 private void part(String n,PoseStack p,MultiBufferSource b,ResourceLocation t,int l,int o,float r,float g,float blue){root.getChild(n).render(p,b.getBuffer(RenderType.entityCutoutNoCull(t)),l,o,r,g,blue,1);}
 public void render(NgCityEntity c,PoseStack p,MultiBufferSource b,int light,int overlay,float partial){
  float[] a=states.computeIfAbsent(c,k->new float[]{0,0,0,0,0,-1});if(a[5]!=c.tickCount){for(int i=0;i<4;i++)a[i]=net.minecraft.util.Mth.approach(a[i],c.systems.mechanics.door(i)?1:0,.12F);a[4]+=c.getDriveSpeed()*3;a[5]=c.tickCount;}
  var col=VehicleTextures.paint(c.color());part("body",p,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);part("trim",p,b,VehicleTextures.RUBBER,light,overlay,.1F,.12F,.13F);part("seats",p,b,VehicleTextures.FABRIC,light,overlay,.16F,.17F,.19F);part("rearSeats",p,b,VehicleTextures.FABRIC,light,overlay,.16F,.17F,.19F);part("dash",p,b,VehicleTextures.RUBBER,light,overlay,.07F,.09F,.1F);
  root.getChild("glass").render(p,b.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT)),light,overlay,.2F,.3F,.35F,.3F);
  for(int i=0;i<4;i++){boolean right=i%2==0;float z=spec==VehicleSpec.TRACTOR?-40:i>=2?3:spec==VehicleSpec.SPORT?-10:-15;float x=right?width:-width;p.pushPose();p.translate(x/16,0,z/16);p.mulPose(Axis.YP.rotation((right?1:-1)*a[i]*1.1F));p.translate(-x/16,0,-z/16);part("door"+i,p,b,VehicleTextures.PAINT,light,overlay,col[0],col[1],col[2]);root.getChild("doorGlass"+i).render(p,b.getBuffer(RenderType.entityTranslucent(VehicleTextures.PAINT)),light,overlay,.2F,.3F,.35F,.3F);p.popPose();}
  if(c.systems.upgrades.rack())part("rack",p,b,VehicleTextures.METAL,light,overlay,.3F,.34F,.35F);
  part("head",p,b,VehicleTextures.LENS,c.systems.expansion.lightMode()>=2?LightTexture.FULL_BRIGHT:light,overlay,.95F,.97F,.9F);part("tail",p,b,VehicleTextures.LENS,c.isBraking()||c.lightsOn()?LightTexture.FULL_BRIGHT:light,overlay,c.isBraking()?1:.55F,.02F,.02F);
  if(spec==VehicleSpec.TAXI&&c.systems.mechanics.taxiEnabled()){part("taxi",p,b,VehicleTextures.LENS,LightTexture.FULL_BRIGHT,overlay,c.systems.mechanics.taxiFree()?.9F:1,c.systems.mechanics.taxiFree()?.85F:.18F,.08F);taxiText(c,p,b);}
  if(spec==VehicleSpec.TRACTOR){part("coupling",p,b,VehicleTextures.METAL,light,overlay,.22F,.25F,.26F);p.pushPose();p.translate(.48,.5,1.05);p.mulPose(Axis.YP.rotation(c.systems.attached()?0:1));part("latch",p,b,VehicleTextures.METAL,light,overlay,c.systems.attached()?.1F:.9F,c.systems.attached()?.8F:.25F,.08F);p.popPose();}
  for(int side:new int[]{-1,1})for(int axle=0;axle<(spec==VehicleSpec.TRACTOR?3:2);axle++){var w=root.getChild("wheel"+side+"_"+axle);w.xRot=a[4];w.yRot=axle==0?Math.max(-.4F,Math.min(.4F,(c.getYRot()-c.yRotO)*.14F)):0;p.pushPose();w.translateAndRotate(p);for(int i=0;i<8;i++)w.getChild("rubber"+i).render(p,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.RUBBER)),light,overlay,.06F,.07F,.075F,1);w.getChild("rim").render(p,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.65F,.68F,.7F,1);w.getChild("hub").render(p,b.getBuffer(RenderType.entityCutoutNoCull(VehicleTextures.METAL)),light,overlay,.3F,.34F,.36F,1);p.popPose();}
 }
 private void taxiText(NgCityEntity c,PoseStack p,MultiBufferSource b){var font=net.minecraft.client.Minecraft.getInstance().font;String text=c.systems.mechanics.taxiFree()?"TAXI FREI":"BESETZT";for(boolean front:new boolean[]{true,false}){p.pushPose();p.translate(0,(roof-3.6F)/16,(front?-3.15:3.15)/16);if(!front)p.mulPose(Axis.YP.rotationDegrees(180));p.scale(.012F,.012F,.012F);font.drawInBatch(text,-font.width(text)/2F,0,0xff171a14,false,p.last().pose(),b,net.minecraft.client.gui.Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);p.popPose();}}
}
