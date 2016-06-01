package lalwess.fluidsolver;

import android.content.res.Resources;
import android.graphics.Color;

import com.threed.jpct.*;



/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {

    //public NPOTTexture processingTexture;

    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture density;
//    public NPOTTexture pressure;
//    public NPOTTexture diffusion;
//    public NPOTTexture divergence;
//    public NPOTTexture vorticity;


    public NPOTTexture outPutTexture = null; //if not a null we output texture to this.

     World FillWorld;
     Camera fillcam = null;


    World impulseWorld;
    Camera cama = null;
    World AdvectingWorld;
    Camera camb = null;
   // World BoundaryWorld;
   // Camera camc = null;
    World divergenceWorld;
    Camera camd = null;
    World gradientWorld;
    Camera came = null;
    World jacobiWorld;
    Camera camf = null;




    World FinalWorld = null;//depending on visualisation mode we set this as the final world.



    //World postProcessWorld;
    //where our texture /object3d will exist to render a fullscreen quad;
    Camera cam = null;

    public int RenderMode = 0;  //Render Mode 0 is regular, 1 is glow, 2 is godrays
  //  Object3D theRenderspot = null;

    GLSLShader renderShader = null;
    GLSLShader loopingshader = null;



    GLSLShader fillingShader = null;
    Object3D fillingObj = null;

    GLSLShader impulseShader = null;
    Object3D impulseObj = null;

    GLSLShader advectingShader = null;
    Object3D  advectingObj = null;

   // GLSLShader boundaryShader = null;
   // Object3D  aboundaryObj = null;

    GLSLShader divergenceShader = null;
    Object3D  divergenceObj = null;

    GLSLShader gradientShader = null;
    Object3D gradientObj = null;

    GLSLShader jacobiShader = null;
    Object3D  jacobiObj = null;

    GLSLShader subtractingShader = null;
    Object3D subtractingObj = null;


    TextureManager tm = TextureManager.getInstance();
  //  TextureInfo screens_ti;


    TextureInfo fill_ti;

    TextureInfo adding_ti;
    TextureInfo advecting_ti;
   // TextureInfo boundary_ti;
    TextureInfo divergence_ti;
    TextureInfo gradient_ti;
    TextureInfo jacobi_ti;


    Boolean textureCompression= false;
    Boolean textureFiltering= true;
    Boolean textureMipMap= false;
    Boolean firstRun = true;



    String VELOCITY_TEXTURE_TAG= "velocity";
    String DENSITY_TEXTURE_TAG= "density";


//    public float SCALE =1.0f;
//    public float TIMESTEP  = 0.125f;
//    public float DISSIPATION = 0.99f;
//    public float VELOCITY_DISSIPATION =0.99f;
//    public int NUM_JACOBI_ITERATIONS =80;
//    public float EPSILON =2.4414e-4f;
//    public float CURL = 0.3f;
//    public float VISCOSITY =0.001f;





    public void setOutPutTexture(NPOTTexture outPutTexture)
    {

        this.outPutTexture = outPutTexture;
    }


    public PostProcessHandler(Resources res, FrameBuffer fb) {


      setUpCameras();
      setupTextures(fb.getWidth(),fb.getHeight());
      loadShaders(res);
      setupTextureInfos();
      setupObjects();


     //   this.theRenderspot= Primitives.getPlane(4,10);


      //  this.world = world;
     //   postProcessWorld = new World();
      //  cam = postProcessWorld.getCamera();
     //   cam.setPosition(-10, 0, 0);
     //   cam.lookAt(new SimpleVector(0, 0, 0));

//
//        processingTexture = new NPOTTexture(fb.getWidth() , fb.getHeight(), RGBColor.GREEN);
//        processingTexture.setFiltering(true);
//        processingTexture.setMipmap(false);
//        processingTexture.setTextureCompression(true);//turning on texture compression eliminates the artifacts, no idea why lol
//        tm.addTexture("processingTexture", processingTexture);





        //screens_ti = new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
       // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);

      ///  theRenderspot.setTransparency(3);
        //
        //
        //
        //
        //theRenderspot.setTexture(screens_ti);

     //   theRenderspot.setCulling(false);


      // renderHook = new PostProcessingRenderHook(theRenderspot, renderShader,this);
     //  renderHook.setCurrentShader(renderShader);

      //  theRenderspot.setOrigin(new SimpleVector(0.01, 0, 0));
     //   theRenderspot.setShader(renderShader);
     //   theRenderspot.setRenderHook(renderHook);
     //  postProcessWorld.addObject(theRenderspot);


    }








    public void Process(FrameBuffer fb) {


        if (firstRun)
    {

        fb.setRenderTarget(velocity);
        fb.clear(Color.BLACK);
        FillWorld.renderScene(fb);//WAS POST PROCESS
        FillWorld.draw(fb);
        fb.display();
        firstRun=false;

        FinalWorld= FillWorld;
    }


//        fb.setRenderTarget(processingTexture);
//        fb.clear(Color.BLACK);
//        postProcessWorld.renderScene(fb);//WAS POST PROCESS
//        postProcessWorld.draw(fb);
//        fb.display();
        //RENDERS A world with only the glow buffer.







        if(outPutTexture == null) {
            fb.removeRenderTarget();
            fb.clear();
            FinalWorld.renderScene(fb);//WAS POST PROCESS
            FinalWorld.draw(fb);
            fb.display();

        }
        else
        {
            fb.setRenderTarget(outPutTexture);
            fb.clear();
            FinalWorld.renderScene(fb);//WAS POST PROCESS
            FinalWorld.draw(fb);
            fb.display();
        }
    }



    public void setupObjects()
    {

        fillingObj = Primitives.getPlane(4,10);
        fillingObj.setOrigin(new SimpleVector(0.01, 0, 0));
        fillingObj.setShader(fillingShader);
        fillingObj.setTexture(VELOCITY_TEXTURE_TAG);
        FillWorld.addObject(fillingObj);


        impulseObj =  Primitives.getPlane(4,10);
        advectingObj =  Primitives.getPlane(4,10);
        //aboundaryObj = Primitives.getPlane(4,10);
        divergenceObj =  Primitives.getPlane(4,10);
        gradientObj =  Primitives.getPlane(4,10);
        jacobiObj =  Primitives.getPlane(4,10);
        subtractingObj =  Primitives.getPlane(4,10);

    }




    public void setupTextures(int w, int h)
    {
        velocity = new NPOTTexture(w , h, RGBColor.GREEN);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);

        density = new NPOTTexture(w , h, RGBColor.GREEN);
        density.setFiltering(textureFiltering);
        density.setMipmap(textureMipMap);
        density.setTextureCompression(textureCompression);
        tm.addTexture(DENSITY_TEXTURE_TAG, density);
    }

    public void setUpCameras()
    {

        FillWorld = new World();
        fillcam= FillWorld.getCamera();
        fillcam.setPosition(-10, 0, 0);
        fillcam.lookAt(new SimpleVector(0, 0, 0));

        impulseWorld = new World();
        cama= impulseWorld.getCamera();
        cama.setPosition(-10, 0, 0);
        cama.lookAt(new SimpleVector(0, 0, 0));


        AdvectingWorld = new World();
        camb=AdvectingWorld.getCamera();
        camb.setPosition(-10, 0, 0);
        camb.lookAt(new SimpleVector(0, 0, 0));


      //  BoundaryWorld = new World();
     //   camc=BoundaryWorld.getCamera();
     //   camc.setPosition(-10, 0, 0);
     //   camc.lookAt(new SimpleVector(0, 0, 0));


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
        String vertexShader =   Loader.loadTextFile(res.openRawResource(R.raw.mainvert));
        fillingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.fill_frag)));

        advectingShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.advect_frag)));


        renderShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));
        loopingshader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.tapadd_frag)));
        subtractingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));;
        impulseShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.impulse_frag)));

        //boundaryShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.boundary)));
        divergenceShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.divergence_frag)));
        //gradientShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.gr)));
        jacobiShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.jacobi_frag)));
    }

    public void setupTextureInfos() {


        fill_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
       // fill_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);

        advecting_ti   =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advecting_ti.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);


        adding_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);


        new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);


        //  boundary_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
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


    }






}
