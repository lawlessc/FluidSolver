package lalwess.fluidsolver;

import android.content.res.Resources;
import android.graphics.Color;

import com.threed.jpct.*;



/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {

    public NPOTTexture processingTexture;
   // public NPOTTexture mainTexture;


    World world;
    World postProcessWorld;
    //where our texture /object3d will exist to render a fullscreen quad;
    Camera cam = null;

    public int RenderMode = 0;  //Render Mode 0 is regular, 1 is glow, 2 is godrays
    Object3D theRenderspot = null;

    GLSLShader renderShader = null;
    GLSLShader loopingshader = null;

    PostProcessingRenderHook renderHook = null;
    TextureManager tm = TextureManager.getInstance();
    TextureInfo screens_ti;

    GLSLShader gameboy_shader  = null;


    int divRatio;



    public void loadShaders(Resources res)
    {

        renderShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
        Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));

        loopingshader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



    }



    public PostProcessHandler(World world, Resources res, FrameBuffer fb) {




        this.theRenderspot= Primitives.getPlane(4,10);

        divRatio=3;

        this.world = world;
        postProcessWorld = new World();
        cam = postProcessWorld.getCamera();
        cam.setPosition(-10, 0, 0);
        cam.lookAt(new SimpleVector(0, 0, 0));

      loadShaders(res);


        processingTexture = new NPOTTexture(fb.getWidth() , fb.getHeight(), RGBColor.GREEN);
        processingTexture.setFiltering(true);
        processingTexture.setMipmap(false);
        processingTexture.setTextureCompression(true);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("processingTexture", processingTexture);





//        mainTexture = new NPOTTexture(fb.getWidth(),fb.getHeight(), RGBColor.BLUE);
//        mainTexture.setFiltering(true);
//        mainTexture.setMipmap(false);
//        mainTexture.setTextureCompression(true);
//        tm.addTexture("mainprocess", mainTexture);





        screens_ti = new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
       // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
       // theRenderspot.setTexture(screens_ti);

      ///  theRenderspot.setTransparency(3);
        theRenderspot.setCulling(false);


       renderHook = new PostProcessingRenderHook(theRenderspot, renderShader,this);
       renderHook.setCurrentShader(renderShader);

        theRenderspot.setOrigin(new SimpleVector(0.01, 0, 0));
        theRenderspot.setShader(renderShader);
        theRenderspot.setRenderHook(renderHook);
       postProcessWorld.addObject(theRenderspot);


    }





    public void doPostProcess(FrameBuffer fb) {


        //RENDERS A world with only the glow buffer.
        fb.setRenderTarget(processingTexture);
        fb.clear(Color.BLACK);
        postProcessWorld.renderScene(fb);//WAS POST PROCESS
        postProcessWorld.draw(fb);
        fb.display();



        fb.removeRenderTarget();


        fb.clear(Color.BLACK);
        postProcessWorld.renderScene(fb);//WAS POST PROCESS
        postProcessWorld.draw(fb);
        fb.display();

//System.out.println("ARE WE THERE YET?");
//
//        fb.clear();
//        world.renderScene(fb);
//        world.draw(fb);
//        fb.display();
//        doBlit(fb);
    }





    public void doBlit(FrameBuffer fb)
    {

//
//       fb.blit(mainTexture, 0, 0, 0, fb.getHeight(),
//               mainTexture.getWidth(), mainTexture.getHeight(), fb.getWidth(), -fb.getHeight(),100, true, null);

    }





}
