package lalwess.fluidsolver;

import android.content.res.Resources;

import com.threed.jpct.*;

import lalwess.fluidsolver.ResolverRenderHooks.AdvectionHook;
import lalwess.fluidsolver.ResolverRenderHooks.DensitySplatHook;
import lalwess.fluidsolver.ResolverRenderHooks.DivergenceHook;
import lalwess.fluidsolver.ResolverRenderHooks.ImpulseHook;
import lalwess.fluidsolver.ResolverRenderHooks.JacobiRenderHook;
import lalwess.fluidsolver.ResolverRenderHooks.SubtractHook;


/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {


    public NPOTTexture outPutTexture = null; //if not a null we output texture to this.


    public enum Viewtype {
        VELOCITY, DENSITY, PRESSURE,DIVERGENCE
    }



    Viewtype viewtype = Viewtype.VELOCITY;

    TextureManager tm = TextureManager.getInstance();

    Boolean textureCompression= true;
    Boolean textureFiltering= true;
    Boolean textureMipMap= false;


    int resolutionDi= 1;


    String VELOCITY_TEXTURE_TAG= "velocity";
    String DENSITY_TEXTURE_TAG= "density";

    String DIVERGENCE_TEXTURE_TAG= "divergence";
    String PRESSURE_TEXTURE_TAG= "pressure";


    public float SCALE =1.0f;
    public float TIMESTEP  = 0.12f;//0.125f;
    public float DISSIPATION = 0.99f;
    public float VELOCITY_DISSIPATION =0.99f;

    public int JACOBI_ITERATIONS =40;

    public float EPSILON =2.4414e-4f;
    public float CURL = 0.3f;
    public float VISCOSITY =0.001f;
    public float CELLSIZE = 1.25f;
    public float HALFCELL = 0.5f / CELLSIZE;


    public float AspectRatio;


    public SimpleVector InverseSize = null;
    public float alpha = 0.05f;
    public float  InverseBeta = 0.1666f;
    public float splatRadius  = 0;
    public SimpleVector splatPos = null;

    //public float HalfInverseCellSize;

    public AdvectionHook  advectionHook = null;
    public AdvectionHook  advectionHookForDensity = null;
    public DivergenceHook divergenceHook=null;
    public JacobiRenderHook jacobiRenderHook = null;
    public SubtractHook subtractHook = null;
    public ImpulseHook impulseHook = null;
    public DensitySplatHook densitySplatHook= null;

    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture density;
    public NPOTTexture pressure;
    //    public NPOTTexture diffusion;
    public NPOTTexture divergence;


  //  public NPOTTexture Temp;
   // public NPOTTexture vorticity;


    public World AdvectingWorld;
    public Camera AdvectionCamera = null;
    public TextureInfo advecting_ti= null;
    public GLSLShader advectingShader;
    public Object3D  advectingObj = null;



    public World advectDensityWorld;
    public Camera advectDensityCam = null;
    public TextureInfo advectdensity_ti= null;
    public GLSLShader advectingDensityShader;
    public Object3D  advectDensity = null;

    public World AddDensity;
    public Camera addDensity = null;
    public TextureInfo density_ti = null;
    public GLSLShader densityShader = null;
    public Object3D densityObj = null;

//
    public World impulseWorld;
    public Camera impulseCam = null;
    public TextureInfo impulse_ti= null;
    public GLSLShader impulseShader = null;
    public Object3D impulseObj = null;


    public World divergenceWorld;
    public Camera divergenceCam = null;
    public TextureInfo divergence_ti= null;
    public GLSLShader divergenceShader;
    public Object3D  divergenceObj = null;

    //This step is carried out multiple times
    public World jacobiWorld;
    public Camera jacobiCam = null;
    public TextureInfo jacobi_ti= null;
    public Object3D  jacobiObj;
    public GLSLShader jacobiShader = null;

    //Gradient Subtraction, pressure subtracted from velocity
    public World SubtractGradientWorld;
    public Camera subGradientCam = null;
    public TextureInfo subGradient_ti;
    public GLSLShader subGradientShader = null;
    public Object3D subGradientObj = null;

    public World displayWorld;
    public Camera displayCam = null;
    public GLSLShader displayShader = null;
    public Object3D displayObj = null;


    //public boolean doAdvectDensity = false;


    public PostProcessHandler(Resources res, FrameBuffer fb) {
     fb.freeMemory();
    loadShaders(res);
    setUpCameras();//worlds
    setupTextures(fb.getWidth(),fb.getHeight());

     InverseSize = new SimpleVector(1.0f/ fb.getWidth() ,1.0f/ fb.getHeight() ,0);
     splatRadius =   fb.getWidth() /8.0f;
     splatPos    =  new SimpleVector(  fb.getWidth() / 2.0f, fb.getWidth() /2.0f , 0);
     AspectRatio = fb.getWidth()/fb.getHeight();
     setupObjects();


     displayWorld.compileAllObjects();



    }



    public void Process(FrameBuffer fb) {
//        if (firstRun)
//    {
//        fb.setRenderTarget(velocity);
//        fb.clear(Color.RED);
//        FillWorld.renderScene(fb);//WAS POST PROCESS
//        FillWorld.draw(fb);
//        fb.display();
//        swapVelocities();
//        firstRun=false;
//    }



    //
     //First Stage we advect velocity with itself
    fb.setRenderTarget(velocity);
   // fb.clearColorBufferOnly(RGBColor.BLACK);

        fb.clear();
    AdvectingWorld.renderScene(fb);
    AdvectingWorld.draw(fb);
    fb.display();


    fb.setRenderTarget(density);
       // fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    advectDensityWorld.renderScene(fb);
    advectDensityWorld.draw(fb);
    fb.display();


     //add new density from the splat
    fb.setRenderTarget(density);
       // fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    AddDensity.renderScene(fb);
    AddDensity.draw(fb);
    fb.display();

                   //add new velocity
    fb.setRenderTarget(velocity);
//        fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    impulseWorld.renderScene(fb);
    impulseWorld.draw(fb);
    fb.display();

    fb.setRenderTarget(divergence);
      //  fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    divergenceWorld.renderScene(fb);
    divergenceWorld.draw(fb);
    fb.display();


    fb.setRenderTarget(pressure);
       // fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    fb.display();


    for(int i =1 ; i < JACOBI_ITERATIONS+1 ; i ++)
    {


    fb.setRenderTarget(pressure);
       // fb.clearColorBufferOnly(RGBColor.BLACK);
        fb.clear();
    jacobiWorld.renderScene(fb);
    jacobiWorld.draw(fb);
    fb.display();

    }


//       fb.setRenderTarget(velocity);
//       fb.clear();
//       SubtractGradientWorld.renderScene(fb);
//       SubtractGradientWorld.draw(fb);
//       fb.display();
     //  doAdvectDensity= true;
    //



        //DISPLAY -
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

        advectingObj = Primitives.getPlane(4,10);
        advectingObj.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHook = new AdvectionHook(this,advectingShader);
        advectingObj.setRenderHook(advectionHook);
        advectingObj.setShader(advectingShader);
        advecting_ti   =  new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advecting_ti.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG), TextureInfo.MODE_ADD);
        advectingObj.setTexture(advecting_ti);
        advectingObj.setCulling(false);
        advectingObj.compile();
        advectingObj.strip();
        AdvectingWorld.addObject(advectingObj);

        advectDensity = Primitives.getPlane(4,10);
        advectDensity.setOrigin(new SimpleVector(0.001, 0, 0));
        advectionHookForDensity = new AdvectionHook(this,advectingDensityShader);
        advectDensity.setRenderHook(advectionHookForDensity);
        advectDensity.setShader(advectingDensityShader);
        advectdensity_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advectdensity_ti.add(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG), TextureInfo.MODE_ADD);
        advectDensity.setTexture(advectdensity_ti);
        advectDensity.setCulling(false);
        advectDensity.compile();
        advectDensity.strip();
        advectDensityWorld.addObject(advectDensity);

        densityObj = Primitives.getPlane(4,10);
        densityObj.setOrigin(new SimpleVector(0.01, 0, 0));
        densitySplatHook = new DensitySplatHook(this, densityShader);
        densityObj.setRenderHook(densitySplatHook);
        densityObj.setShader(densityShader);
        density_ti=new TextureInfo(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG));
        densityObj.setTexture(density_ti);
        densityObj.setCulling(false);
        densityObj.compile();
        densityObj.strip();
        AddDensity.addObject(densityObj);

        impulseObj = Primitives.getPlane(4,10);
        impulseObj.setOrigin(new SimpleVector(0.01, 0, 0));
        impulseHook = new ImpulseHook(this, impulseShader);
        impulseObj.setShader(impulseShader);
        impulseObj.setRenderHook(impulseHook);
        impulse_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        impulseObj.setTexture(impulse_ti);
        impulseObj.setCulling(false);
        impulseObj.compile();
        impulseObj.strip();
        impulseWorld.addObject(impulseObj);

        divergenceObj = Primitives.getPlane(4,10);
        divergenceObj.setOrigin(new SimpleVector(0.01, 0, 0));
        divergenceHook = new DivergenceHook(this,divergenceShader);
        divergenceObj.setShader(divergenceShader);
        divergenceObj.setRenderHook(divergenceHook);
        divergence_ti=new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        divergenceObj.setTexture(divergence_ti);
        divergenceObj.setCulling(false);
        divergenceObj.compile();
        divergenceObj.strip();
        divergenceWorld.addObject(divergenceObj);

        jacobiObj = Primitives.getPlane(4,10);
        jacobiObj.setOrigin(new SimpleVector(0.01, 0, 0));
        jacobiRenderHook = new JacobiRenderHook(this,jacobiShader);
        jacobiObj.setShader(jacobiShader);
        jacobiObj.setRenderHook(jacobiRenderHook);
        jacobi_ti   =new TextureInfo(TextureManager.getInstance().getTextureID(PRESSURE_TEXTURE_TAG));
        jacobi_ti.add(TextureManager.getInstance().getTextureID(DIVERGENCE_TEXTURE_TAG), TextureInfo.MODE_ADD);
        jacobiObj.setTexture(jacobi_ti);
        jacobiObj.setCulling(false);
        jacobiObj.compile();
        jacobiObj.strip();
        jacobiWorld.addObject(jacobiObj);

        subGradientObj = Primitives.getPlane(4,10);
        subGradientObj.setOrigin(new SimpleVector(0.01, 0, 0));
        subtractHook = new SubtractHook(this,subGradientShader);
        subGradientObj.setCulling(false);
        subGradientObj.setShader(subGradientShader);
        subGradientObj.setRenderHook(subtractHook);
        subGradient_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        subGradient_ti.add(TextureManager.getInstance().getTextureID(PRESSURE_TEXTURE_TAG), TextureInfo.MODE_ADD);
        subGradientObj.setTexture(subGradient_ti);
        subGradientObj.compile();
        subGradientObj.strip();
        SubtractGradientWorld.addObject(subGradientObj);

      //Displays a texture. doesn't process
        displayObj = Primitives.getPlane(4,10);
        displayObj.setOrigin(new SimpleVector(0.01, 0, 0));
        displayObj.setShader(displayShader);

        displayObj.setTexture(VELOCITY_TEXTURE_TAG);
        viewtype = Viewtype.VELOCITY;

        displayObj.setCulling(false);
        displayWorld.addObject(displayObj);
    }




    public void setupTextures(int w, int h)
    {

        int width= w;
        int height= h;

        velocity = new NPOTTexture(width , height, RGBColor.BLACK);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);

        density = new NPOTTexture(width/2 , height/2, RGBColor.BLACK);
        density.setFiltering(textureFiltering);
        density.setMipmap(textureMipMap);
        density.setTextureCompression(textureCompression);
        tm.addTexture(DENSITY_TEXTURE_TAG, density);

        pressure = new NPOTTexture(width /4, height/4, RGBColor.BLACK);
        pressure.setFiltering(textureFiltering);
        pressure.setMipmap(textureMipMap);
        pressure.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(PRESSURE_TEXTURE_TAG, pressure);

        divergence = new NPOTTexture(width /4, height/4, RGBColor.BLACK);
        divergence.setFiltering(textureFiltering);
        divergence.setMipmap(textureMipMap);
        divergence.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(DIVERGENCE_TEXTURE_TAG, divergence);



    }

    public void setUpCameras()
    {

        AdvectingWorld = new World();
        AdvectionCamera =AdvectingWorld.getCamera();
        AdvectionCamera.setPosition(-10, 0, 0);
        AdvectionCamera.lookAt(new SimpleVector(0, 0, 0));

        advectDensityWorld= new World();
        advectDensityCam = advectDensityWorld.getCamera();
        advectDensityCam.setPosition(-10, 0, 0);
        advectDensityCam.lookAt(new SimpleVector(0, 0, 0));

        AddDensity = new World();
        addDensity= AddDensity.getCamera();
        addDensity.setPosition(-10, 0, 0);
        addDensity.lookAt(new SimpleVector(0, 0, 0));


        impulseWorld = new World();
        impulseCam= impulseWorld.getCamera();
        impulseCam.setPosition(-10, 0, 0);
        impulseCam.lookAt(new SimpleVector(0, 0, 0));

        divergenceWorld = new World();
        divergenceCam =divergenceWorld.getCamera();
        divergenceCam.setPosition(-10, 0, 0);
        divergenceCam.lookAt(new SimpleVector(0, 0, 0));

        jacobiWorld = new World();
        jacobiCam =jacobiWorld.getCamera();
        jacobiCam.setPosition(-10, 0, 0);
        jacobiCam.lookAt(new SimpleVector(0, 0, 0));

        SubtractGradientWorld = new World();
        subGradientCam=SubtractGradientWorld.getCamera();
        subGradientCam.setPosition(-10, 0, 0);
        subGradientCam.lookAt(new SimpleVector(0, 0, 0));

        displayWorld = new World();
        displayCam=displayWorld.getCamera();
        displayCam.setPosition(-10, 0, 0);
        displayCam.lookAt(new SimpleVector(0, 0, 0));
    }


    public void loadShaders(Resources res)
    {
        String vertexShader =   Loader.loadTextFile(res.openRawResource(R.raw.mainvert));
        String advectFragmentShader =   Loader.loadTextFile(res.openRawResource(R.raw.advect_frag));
      //  fillingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.fill_frag)));
        advectingShader        =new GLSLShader(vertexShader,advectFragmentShader);
        advectingDensityShader =new GLSLShader(vertexShader,advectFragmentShader);

        divergenceShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.divergence_frag)));
        densityShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.splat_frag)));
        jacobiShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.jacobi_frag)));

         impulseShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.velocity_splat_frag)));



        subGradientShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));
        //subtractingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));;

        //boundaryShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.boundary)));

        displayShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));
    }




    public void setOutPutTexture(NPOTTexture outPutTexture)
    {

        this.outPutTexture = outPutTexture;
       InverseSize = new SimpleVector(1.0f/ outPutTexture.getWidth() ,1.0f/ outPutTexture.getHeight() ,0);
    }



    public void setSplatPos(float x , float y)
    {

        y = velocity.getHeight() -y;
        splatPos = new SimpleVector(x,y,0);
    }



    public void switchView()
    {
        switch (viewtype)
        {
            case DENSITY:
                displayObj.setTexture(DENSITY_TEXTURE_TAG);
                viewtype = Viewtype.VELOCITY;
                break;
            case VELOCITY:
                displayObj.setTexture(VELOCITY_TEXTURE_TAG);
                viewtype = Viewtype.DIVERGENCE;
                break;
            case DIVERGENCE:
                displayObj.setTexture(DIVERGENCE_TEXTURE_TAG);
                viewtype = Viewtype.PRESSURE;
                break;

            case PRESSURE:
                displayObj.setTexture(PRESSURE_TEXTURE_TAG);
                viewtype = Viewtype.DENSITY;
                break;
        }
    }






}
