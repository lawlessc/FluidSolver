package lalwess.fluidsolver;

import android.content.res.Resources;
import android.graphics.Color;

import com.threed.jpct.*;



/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {

    public NPOTTexture processingTexture;



    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture density;
    public NPOTTexture pressure;
    public NPOTTexture diffusion;
    public NPOTTexture divergence;
    public NPOTTexture vorticity;


    public NPOTTexture outPutTexture = null; //if not a null we output texture to this.


    World world;

    World AddingWorld;
    Camera cama = null;
    World AdvectingWorld;
    Camera camb = null;
    World BoundaryWorld;
    Camera camc = null;
    World divergenceWorld;
    Camera camd = null;
    World gradientWorld;
    Camera came = null;
    World jacobiWorld;
    Camera camf = null;


    World postProcessWorld;
    //where our texture /object3d will exist to render a fullscreen quad;
    Camera cam = null;

    public int RenderMode = 0;  //Render Mode 0 is regular, 1 is glow, 2 is godrays
    Object3D theRenderspot = null;

    GLSLShader renderShader = null;
    GLSLShader loopingshader = null;


    GLSLShader addingShader = null;
    Object3D addingObj = null;

    GLSLShader advectingShader = null;
    Object3D  advectingObj = null;

    GLSLShader boundaryShader = null;
    Object3D  aboundaryObj = null;

    GLSLShader divergenceShader = null;
    Object3D  divergenceObj = null;

    GLSLShader gradientShader = null;
    Object3D gradientObj = null;

    GLSLShader jacobiShader = null;
    Object3D  jacobiObj = null;

   // GLSLShader splatShader = null;
   // GLSLShader gaussianSplatShader = null;
   // GLSLShader vorticityShader = null;
   // GLSLShader vorticityForceShader = null;













    PostProcessingRenderHook renderHook = null;
    TextureManager tm = TextureManager.getInstance();
    TextureInfo screens_ti;

    TextureInfo adding_ti;
    TextureInfo advecting_ti;
    TextureInfo boundary_ti;
    TextureInfo divergence_ti;
    TextureInfo gradient_ti;
    TextureInfo jacobi_ti;


    GLSLShader gameboy_shader  = null;


    Boolean textureCompression= false;
    Boolean textureFiltering= true;
    Boolean textureMipMap= false;

    int divRatio;


    public float SCALE =1.0f;
    public float TIMESTEP  = 0.125f;
    public float DISSIPATION = 0.99f;
    public float VELOCITY_DISSIPATION =0.99f;
    public int NUM_JACOBI_ITERATIONS =80;
    public float EPSILON =2.4414e-4f;
    public float CURL = 0.3f;
    public float VISCOSITY =0.001f;





    public void setOutPutTexture(NPOTTexture outPutTexture)
    {

        this.outPutTexture = outPutTexture;
    }


    public PostProcessHandler(World world, Resources res, FrameBuffer fb) {


      setUpCameras();
      setupTextures(fb.getWidth(),fb.getHeight());
      loadShaders(res);
      setupObjects();


        this.theRenderspot= Primitives.getPlane(4,10);

        divRatio=3;

        this.world = world;
        postProcessWorld = new World();
        cam = postProcessWorld.getCamera();
        cam.setPosition(-10, 0, 0);
        cam.lookAt(new SimpleVector(0, 0, 0));


        processingTexture = new NPOTTexture(fb.getWidth() , fb.getHeight(), RGBColor.GREEN);
        processingTexture.setFiltering(true);
        processingTexture.setMipmap(false);
        processingTexture.setTextureCompression(true);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("processingTexture", processingTexture);





        screens_ti = new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
       // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);

      ///  theRenderspot.setTransparency(3);     // theRenderspot.setTexture(screens_ti);

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



        if(outPutTexture == null) {
            fb.clear(Color.BLACK);
            postProcessWorld.renderScene(fb);//WAS POST PROCESS
            postProcessWorld.draw(fb);
            fb.display();

        }
        else
        {
            fb.setRenderTarget(outPutTexture);
            fb.clear(Color.BLACK);
            postProcessWorld.renderScene(fb);//WAS POST PROCESS
            postProcessWorld.draw(fb);
            fb.display();
        }
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



    public void setupTextures(int w, int h)
    {







        velocity = new NPOTTexture(w , h, RGBColor.GREEN);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("velocityTexture", velocity);

        density = new NPOTTexture(w , h, RGBColor.GREEN);
        density.setFiltering(textureFiltering);
        density.setMipmap(textureMipMap);
        density.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("densityTexture", density);

        pressure = new NPOTTexture(w , h, RGBColor.GREEN);
        pressure.setFiltering(textureFiltering);
        pressure.setMipmap(textureMipMap);
        pressure.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("pressureTexture", pressure);

        diffusion = new NPOTTexture(w, h, RGBColor.GREEN);
        diffusion.setFiltering(textureFiltering);
        diffusion.setMipmap(textureMipMap);
        diffusion.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("diffusionTexture", diffusion);

        divergence = new NPOTTexture(w , h, RGBColor.GREEN);
        divergence.setFiltering(textureFiltering);
        divergence.setMipmap(textureMipMap);
        divergence.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("divergenceTexture", divergence);

        vorticity = new NPOTTexture(w , h, RGBColor.GREEN);
        vorticity.setFiltering(textureFiltering);
        vorticity.setMipmap(textureMipMap);
        vorticity.setTextureCompression(textureCompression);//turning on texture compression eliminates the artifacts, no idea why lol
        tm.addTexture("vorticityTexture", vorticity);
    }

    public void setUpCameras()
    {


        AddingWorld = new World();
        cama=AddingWorld.getCamera();
        cama.setPosition(-10, 0, 0);
        cama.lookAt(new SimpleVector(0, 0, 0));


        AdvectingWorld = new World();
        camb=AdvectingWorld.getCamera();
        camb.setPosition(-10, 0, 0);
        camb.lookAt(new SimpleVector(0, 0, 0));


        BoundaryWorld = new World();
        camc=BoundaryWorld.getCamera();
        camc.setPosition(-10, 0, 0);
        camc.lookAt(new SimpleVector(0, 0, 0));


        divergenceWorld = new World();
        camd=divergenceWorld.getCamera();
        camd.setPosition(-10, 0, 0);
        camd.lookAt(new SimpleVector(0, 0, 0));

        gradientWorld = new World();
        came=gradientWorld.getCamera();
        came.setPosition(-10, 0, 0);
        came.lookAt(new SimpleVector(0, 0, 0));




        jacobiWorld = new World();
        camf=jacobiWorld.getCamera();
        camf.setPosition(-10, 0, 0);
        camf.lookAt(new SimpleVector(0, 0, 0));






    }

    public void loadShaders(Resources res)
    {

        renderShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));

        loopingshader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));




        addingShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



        advectingShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



        boundaryShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



        divergenceShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



        gradientShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));



        jacobiShader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.mainvert)),
                Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));







    }


    public void setupObjects()
    {

         adding_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);


        new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);

         advecting_ti   =new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
         boundary_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
         divergence_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
         gradient_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
         jacobi_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);





        addingObj =  Primitives.getPlane(4,10);
       advectingObj =  Primitives.getPlane(4,10);
       aboundaryObj = Primitives.getPlane(4,10);
       divergenceObj =  Primitives.getPlane(4,10);
       gradientObj =  Primitives.getPlane(4,10);
       jacobiObj =  Primitives.getPlane(4,10);
    }




}
