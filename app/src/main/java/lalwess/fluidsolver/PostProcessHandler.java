package lalwess.fluidsolver;

import android.content.res.Resources;
import android.graphics.Color;

import com.threed.jpct.*;

import lalwess.fluidsolver.ResolverRenderHooks.AdvectionHook;
import lalwess.fluidsolver.ResolverRenderHooks.DivergenceHook;
import lalwess.fluidsolver.ResolverRenderHooks.JacobiRenderHook;


/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {

    //public NPOTTexture processingTexture;

    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture density;
    public NPOTTexture pressure;
//    public NPOTTexture diffusion;
   public NPOTTexture divergence;
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

    World gradientWorld;
    Camera came = null;



    //World postProcessWorld;
    //where our texture /object3d will exist to render a fullscreen quad;
    Camera cam = null;

    public int RenderMode = 0;  //Render Mode 0 is regular, 1 is glow, 2 is godrays

    GLSLShader impulseShader = null;
    Object3D impulseObj = null;

    GLSLShader advectingShader = null;
    Object3D  advectingObj = null;










    GLSLShader gradientShader = null;
    Object3D gradientObj = null;



    GLSLShader subtractingShader = null;
    Object3D subtractingObj = null;


    TextureManager tm = TextureManager.getInstance();
  //  TextureInfo screens_ti;


    TextureInfo fill_ti;

    TextureInfo adding_ti;
    TextureInfo advecting_ti;

   // TextureInfo boundary_ti;

    TextureInfo gradient_ti;



    Boolean textureCompression= false;
    Boolean textureFiltering= true;
    Boolean textureMipMap= false;
    Boolean firstRun = true;



    String VELOCITY_TEXTURE_TAG= "velocity";
    String DENSITY_TEXTURE_TAG= "density";


    public float SCALE =1.0f;
    public float TIMESTEP  = 0.125f;//0.125f;
    public float DISSIPATION = 0.99f;
    public float VELOCITY_DISSIPATION =0.99f;

    public int JACOBI_ITERATIONS =20;

    public float EPSILON =2.4414e-4f;
    public float CURL = 0.3f;
    public float VISCOSITY =0.001f;
    public float CELLSIZE = 1.25f;
    public float HALFCELL = 0.5f / CELLSIZE;
    public SimpleVector InverseSize = null;

    //public float HalfInverseCellSize;





    AdvectionHook  advectionHook = null;
    DivergenceHook divergenceHook=null;
    JacobiRenderHook jacobiRenderHook = null;


    World jacobiWorld;
    Camera camf = null;
    TextureInfo jacobi_ti;
    Object3D  jacobiObj = null;
    GLSLShader jacobiShader = null;



    World advectDensityWorld;
    Camera camden = null;
    TextureInfo advectdensity_ti;
    Object3D  advectDensity = null;


    World divergenceWorld;
    Camera camd = null;
    TextureInfo divergence_ti;
    GLSLShader divergenceShader = null;
    Object3D  divergenceObj = null;



    World displayWorld;
    Camera displayCam = null;
    TextureInfo display_ti;
    GLSLShader displayShader = null;
    Object3D displayObj = null;




    public PostProcessHandler(Resources res, FrameBuffer fb) {


      setUpCameras();//worlds

      loadShaders(res);
      setupTextures(fb.getWidth(),fb.getHeight());
        InverseSize = new SimpleVector(1.0f/ fb.getWidth() ,1.0f/ fb.getHeight() ,0);



      setupTextureInfos();
      setupObjects();
    }








    public void Process(FrameBuffer fb) {


        if (firstRun)
    {

        fb.setRenderTarget(velocity);
        fb.clear(Color.RED);
        FillWorld.renderScene(fb);//WAS POST PROCESS
        FillWorld.draw(fb);
        fb.display();
        firstRun=false;
        System.out.println("CALLED FILL");
    }


       // FinalWorld= AdvectingWorld;


        fb.setRenderTarget(velocity);
        fb.clear();
        AdvectingWorld.renderScene(fb);
        AdvectingWorld.draw(fb);
        fb.display();


        fb.setRenderTarget(density);
        fb.clear();
        advectDensityWorld.renderScene(fb);
        advectDensityWorld.draw(fb);
        fb.display();


        fb.setRenderTarget(divergence);
        fb.clear();
        divergenceWorld.renderScene(fb);
        divergenceWorld.draw(fb);
        fb.display();


//        for(int i =0 ; i < JACOBI_ITERATIONS ; i ++)
//        {
//
//            fb.setRenderTarget(pressure);
//            fb.clear();
//            jacobiWorld.renderScene(fb);
//            jacobiWorld.draw(fb);
//            fb.display();
//
//
//
//        }



        if(outPutTexture == null) {
            fb.removeRenderTarget();
            fb.clear();
            displayWorld.renderScene(fb);//WAS POST PROCESS
            displayWorld.draw(fb);
            fb.display();
        }
        else
        {
            fb.setRenderTarget(outPutTexture);
            fb.clear();
            displayWorld.renderScene(fb);//WAS POST PROCESS
            displayWorld.draw(fb);
            fb.display();
        }
    }



    public void setupObjects()
    {

       //does advection
        advectingObj = Primitives.getPlane(4,10);
        advectingObj.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHook = new AdvectionHook(this,advectingShader);
        advectingObj.setShader(advectingShader);
        advectingObj.setRenderHook(advectionHook);
        advectingObj.setTexture(advecting_ti);




        advectDensity = Primitives.getPlane(4,10);
        advectDensity.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHook = new AdvectionHook(this,advectingShader);
        advectDensity.setCulling(false);
        advectDensity.setShader(advectingShader);
        advectDensity.setRenderHook(advectionHook);
        advectDensity.setTexture(advectdensity_ti);
        advectDensityWorld.addObject(advectingObj);






        divergenceObj = Primitives.getPlane(4,10);
        divergenceObj.setOrigin(new SimpleVector(0.01, 0, 0));
        divergenceHook = new DivergenceHook(this,divergenceShader);
        divergenceObj.setCulling(false);
        divergenceObj.setShader(divergenceShader);
        divergenceObj.setRenderHook(divergenceHook);
        divergenceObj.setTexture(advecting_ti);
        divergenceWorld.addObject(divergenceObj);



        jacobiObj = Primitives.getPlane(4,10);
        jacobiObj.setOrigin(new SimpleVector(0.01, 0, 0));
        jacobiRenderHook = new JacobiRenderHook(this,divergenceShader);
        jacobiObj.setCulling(false);
        jacobiObj.setShader(jacobiShader);
        jacobiObj.setRenderHook(jacobiRenderHook);
        jacobiObj.setTexture(jacobi_ti);
        jacobiWorld.addObject(jacobiObj);






        //advectingObj.setTransparency(3);
        advectingObj.setCulling(false);
        AdvectingWorld.addObject(advectingObj);








        impulseObj =  Primitives.getPlane(4,10);

        //aboundaryObj = Primitives.getPlane(4,10);

        gradientObj =  Primitives.getPlane(4,10);
        jacobiObj =  Primitives.getPlane(4,10);
        subtractingObj =  Primitives.getPlane(4,10);




      //Displays a texture. doesn't process
        displayObj = Primitives.getPlane(4,10);
        displayObj.setOrigin(new SimpleVector(0.01, 0, 0));
        displayObj.setShader(displayShader);
        displayObj.setTexture(VELOCITY_TEXTURE_TAG);
        displayObj.setCulling(false);
        displayWorld.addObject(displayObj);
    }




    public void setupTextures(int w, int h)
    {
        velocity = new NPOTTexture(w , h, RGBColor.RED);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);


        pressure = new NPOTTexture(w , h, RGBColor.GREEN);
        pressure.setFiltering(textureFiltering);
        pressure.setMipmap(textureMipMap);
        pressure.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture("pressure", pressure);



        divergence = new NPOTTexture(w , h, RGBColor.GREEN);
        divergence.setFiltering(textureFiltering);
        divergence.setMipmap(textureMipMap);
        divergence.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture("divergence", divergence);


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


        advectDensityWorld= new World();
        camden = advectDensityWorld.getCamera();
        camden.setPosition(-10, 0, 0);
        camden.lookAt(new SimpleVector(0, 0, 0));


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



        displayWorld = new World();
        displayCam=displayWorld.getCamera();
        displayCam.setPosition(-10, 0, 0);
        displayCam.lookAt(new SimpleVector(0, 0, 0));





    }


    public void loadShaders(Resources res)
    {
        String vertexShader =   Loader.loadTextFile(res.openRawResource(R.raw.mainvert));
      //  fillingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.fill_frag)));

        advectingShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.advect_frag)));
        displayShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));
        divergenceShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.divergence_frag)));
        jacobiShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.jacobi_frag)));

      //  subtractingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));;
      //  impulseShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.impulse_frag)));

        //boundaryShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.boundary)));

        //gradientShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.gr)));

    }

    public void setupTextureInfos() {


        advecting_ti   =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advecting_ti.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG), TextureInfo.MODE_ADD);
       // advectingObj.setTexture(advecting_ti);


        advectdensity_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advectdensity_ti.add(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG), TextureInfo.MODE_ADD);

        adding_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));


        jacobi_ti   =new TextureInfo(TextureManager.getInstance().getTextureID("pressure"));
        jacobi_ti.add(TextureManager.getInstance().getTextureID("density"), TextureInfo.MODE_ADD);

        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);





       // new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);



      //  tm.replaceTexture();
        //  boundary_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
        divergence_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
        gradient_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);
     //   jacobi_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));
        // screens_ti.add(TextureManager.getInstance().getTextureID("glowscenemidp"), TextureInfo.MODE_ADD);
        // theRenderspot.setTexture(screens_ti);


    }


    public void setOutPutTexture(NPOTTexture outPutTexture)
    {

        this.outPutTexture = outPutTexture;
        InverseSize = new SimpleVector(1.0f/ outPutTexture.getWidth() ,1.0f/ outPutTexture.getHeight() ,0);
    }






}
