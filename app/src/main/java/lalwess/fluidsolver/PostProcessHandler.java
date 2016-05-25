package lalwess.fluidsolver;

import android.content.res.Resources;
import android.graphics.Color;

import com.threed.jpct.*;

import Entity_types.Sun;
import RenderHooks.PostProcessingRenderHook;

/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {

    public NPOTTexture glowTexture;
    //public NPOTTexture glowTextureLowP;
    public NPOTTexture glowTextureMidP;
    public NPOTTexture godRayTexture;


    public NPOTTexture mainTexture;

    //public Texture textureles;


    World world;
    World postProcessWorld;
    //where our texture /object3d will exist to render a fullscreen quad;
    Camera cam = null;

    public int RenderMode = 0;  //Render Mode 0 is regular, 1 is glow, 2 is godrays
    Object3D theRenderspot = null;

    GLSLShader renderShader = null;
    PostProcessingRenderHook renderHook = null;
    TextureManager tm = TextureManager.getInstance();
    TextureInfo screens_ti;

    GLSLShader gameboy_shader  = null;

    public SimpleVector sunScreenPos = new SimpleVector(0,0,0);
    public int doLightScattering =0;


    int divRatio;




    public PostProcessHandler(World world, Resources res, FrameBuffer fb) {


       textureles= new Texture(res.openRawResource(R.raw.textureless));

        this.theRenderspot= Primitives.getPlane(4,10);

        divRatio=3;

        this.world = world;
        postProcessWorld = new World();
        cam = postProcessWorld.getCamera();
        cam.setPosition(-10, 0, 0);
        cam.lookAt(new SimpleVector(0, 0, 0));

        renderShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.postprocess_vert)),
               Loader.loadTextFile(res.openRawResource(R.raw.postprocess_frag)));

        gameboy_shader  = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.gameboy_vert)),
                Loader.loadTextFile(res.openRawResource(R.raw.gameboy_frag)));
         //fb.setBlittingShader(gameboy_shader);

        glowTexture = new NPOTTexture(fb.getWidth()/2 , fb.getHeight()/2, RGBColor.GREEN);
        glowTexture.setFiltering(true);
        glowTexture.setMipmap(false);
        glowTexture.setTextureCompression(true);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("glowscene", glowTexture);




        glowTextureMidP = new NPOTTexture(fb.getWidth()/4 , fb.getHeight()/4, RGBColor.GREEN);
        glowTextureMidP.setFiltering(true);
        glowTextureMidP.setMipmap(false);
        glowTextureMidP.setTextureCompression(true);
        tm.addTexture("glowscenemidp", glowTextureMidP);






        godRayTexture = new NPOTTexture(fb.getWidth()/2 , fb.getHeight()/2, RGBColor.GREEN);
        godRayTexture.setFiltering(true);
        godRayTexture.setMipmap(false);
        godRayTexture.setTextureCompression(true);
        tm.addTexture("godrays", godRayTexture);




        mainTexture = new NPOTTexture(fb.getWidth(),fb.getHeight(), RGBColor.BLUE);
        mainTexture.setFiltering(true);
        mainTexture.setMipmap(false);
        mainTexture.setTextureCompression(true);
        tm.addTexture("mainprocess", mainTexture);





        screens_ti = new TextureInfo(TextureManager.getInstance().getTextureID("glowscene"));
        screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        //screens_ti.add(TextureManager.getInstance().getTextureID("glowscenelowp"), TextureInfo.MODE_ADD);
        screens_ti.add(TextureManager.getInstance().getTextureID("godrays"), TextureInfo.MODE_ADD);
        theRenderspot.setTexture(screens_ti);


      ///  theRenderspot.setTransparency(3);
        theRenderspot.setCulling(false);


       renderHook = new PostProcessingRenderHook(theRenderspot, renderShader,this);
       renderHook.setCurrentShader(renderShader);

        theRenderspot.setOrigin(new SimpleVector(0.01, 0, 0));
        theRenderspot.setShader(renderShader);
        theRenderspot.setRenderHook(renderHook);
       postProcessWorld.addObject(theRenderspot);


    }


    public void OnSurfaceChange( FrameBuffer fb) {








        glowTexture = new NPOTTexture(fb.getWidth()/2 , fb.getHeight()/2, RGBColor.GREEN);
        glowTexture.setFiltering(true);
        glowTexture.setMipmap(false);
        glowTexture.setTextureCompression(true);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("glowscene", glowTexture);




        glowTextureMidP = new NPOTTexture(fb.getWidth()/4 , fb.getHeight()/4, RGBColor.GREEN);
        glowTextureMidP.setFiltering(true);
        glowTextureMidP.setMipmap(false);
        glowTextureMidP.setTextureCompression(true);
        tm.addTexture("glowscenemidp", glowTextureMidP);





        godRayTexture = new NPOTTexture(fb.getWidth()/2 , fb.getHeight()/2, RGBColor.GREEN);
        godRayTexture.setFiltering(true);
        godRayTexture.setMipmap(false);
        godRayTexture.setTextureCompression(true);
        tm.addTexture("godrays", godRayTexture);





        mainTexture = new NPOTTexture(fb.getWidth(),fb.getHeight(), RGBColor.BLUE);
        mainTexture.setFiltering(true);
        mainTexture.setMipmap(false);
        mainTexture.setTextureCompression(true);
        tm.addTexture("mainprocess", mainTexture);





        screens_ti = new TextureInfo(TextureManager.getInstance().getTextureID("glowscene"));
        screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
       // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenelowp"), TextureInfo.MODE_ADD);
        screens_ti.add(TextureManager.getInstance().getTextureID("godrays"), TextureInfo.MODE_ADD);
        theRenderspot.setTexture(screens_ti);


        theRenderspot.setCulling(false);

        renderHook = new PostProcessingRenderHook(theRenderspot, renderShader,this);
        renderHook.setCurrentShader(renderShader);

        theRenderspot.setOrigin(new SimpleVector(0.01, 0, 0));
        theRenderspot.setShader(renderShader);
        theRenderspot.setRenderHook(renderHook);
        postProcessWorld.addObject(theRenderspot);
    }




    public void doPostProcess(FrameBuffer fb) {






        RenderMode = 1;//1
        fb.setRenderTarget(glowTexture);
        fb.clear();
    //    fb.clear(Color.BLACK);
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

        fb.setRenderTarget(glowTextureMidP);
        fb.clear();
        world.renderScene(fb);
        world.draw(fb);
        fb.display();



        //RENDERS A world with only the glow buffer.
        fb.setRenderTarget(mainTexture);
        fb.clear(Color.BLACK);
        postProcessWorld.renderScene(fb);//WAS POST PROCESS
        postProcessWorld.draw(fb);
        fb.display();
        fb.removeRenderTarget();

        RenderMode =0;


        fb.clear();
        world.renderScene(fb);
        world.draw(fb);
        fb.display();

        doBlit(fb);


        //RENDERS UI
       // Main.allGameObjects.INSTANCE.menumanager.uiRender(fb);
        //Main.allGameObjects.INSTANCE.menumanager.uiDraw(fb);

    }





    public void doBlit(FrameBuffer fb)
    {


       fb.blit(mainTexture, 0, 0, 0, fb.getHeight(),
               mainTexture.getWidth(), mainTexture.getHeight(), fb.getWidth(), -fb.getHeight(),100, true, null);

          ///
    }


    public void retroblit(FrameBuffer fb) {

         fb.setBlittingShader(gameboy_shader);
        fb.blit(mainTexture, 0, 0, 0, fb.getHeight(),
                fb.getWidth(), fb.getHeight(), fb.getWidth(), -fb.getHeight(), 100, true, null);

       fb.blit(textureles, 0, 0, 0, 0,
                0, 0, 0, 0,0
                , true, null);

        fb.blit(textureles, 0, 0, 0, 0,
                0, 0, 0, 0,0
                , true, null);

        fb.blit(textureles, 0, 0, 0, 0,
                0, 0, 0, 0,0
                , true, null);
           fb.setBlittingShader(null);
    }






}
