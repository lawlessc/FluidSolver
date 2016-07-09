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
    Boolean setGLFiltering=false;

   // int resolutionDi= 1;


    String VELOCITY_TEXTURE_TAG= "velocity";
    String VELOCITY_TEXTURE_TAG_TWO= "velocitytwo";
    String DENSITY_TEXTURE_TAG= "density";
    String DENSITY_TEXTURE_TAG_TWO= "densitytwo";

    String DIVERGENCE_TEXTURE_TAG= "divergence";
    String PRESSURE_TEXTURE_TAG= "pressure";

    String ADDITIONAL_DENSITY_TAG="densitytoAdd";

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


    AdvectionHook  advectionHook;
    AdvectionHook advectionHooktwo;

    AdvectionHook  advectionHookForDensity;

    AdvectionHook advectionHookForDensitytwo;



    //public float HalfInverseCellSize;
    public DivergenceHook divergenceHook=null;
    public JacobiRenderHook jacobiRenderHook = null;
    public SubtractHook subtractHook = null;
    public ImpulseHook impulseHook = null;
    public DensitySplatHook densitySplatHook= null;

    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture velocity2;

    public NPOTTexture textureless;

    public NPOTTexture density;
    public NPOTTexture density2;
    public NPOTTexture add_density;


    public NPOTTexture pressure;
    public NPOTTexture divergence;


  // public NPOTTexture Temp;
   // public NPOTTexture vorticity;

    public TextureInfo advecting_ti= null;
    public TextureInfo advecting_tiTwo= null;

    public GLSLShader advectingShader;
    public Object3D  advectingObj = null;
    public Object3D  advectingObjTwo = null;



;
    public TextureInfo advectdensity_ti= null;
    public TextureInfo advectdensity_tiTwo= null;

    public GLSLShader advectingDensityShader;
    public Object3D DensityAdvection = null;
    public Object3D DensityAdvectionTwo = null;


    public TextureInfo density_ti = null;
    public TextureInfo density_tiTwo = null;
    public GLSLShader densityShader = null;
    public Object3D densityObj = null;
   // public Object3D densityObjTwo = null;

//
;
    public TextureInfo impulse_ti= null;
    public TextureInfo impulse_tiTwo= null;
    public GLSLShader impulseShader = null;
    public Object3D impulseObj = null;
   // public Object3D impulseObjTwo = null;



    public TextureInfo divergence_ti= null;
    public GLSLShader divergenceShader;
    public Object3D  divergenceObj = null;
    public Object3D  divergenceObjTwo = null;

    //This step is carried out multiple times
    public TextureInfo jacobi_ti= null;
   // public TextureInfo jacobi_tiTwo= null;
    public Object3D  jacobiObj;
   // public Object3D  jacobiObjTwo;
    public GLSLShader jacobiShader = null;



    public TextureInfo subGradient_ti;
    public GLSLShader subGradientShader = null;
    public Object3D subGradientObj = null;
  //  public Object3D subGradientObjTwo = null;

    public World displayWorld;
    public Camera displayCam = null;
    public GLSLShader displayShader = null;
    public Object3D displayObj = null;


    public  Boolean velocityswitch= true;




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





        if(velocityswitch) {


            advectingObj.setTexture("textureless");
            DensityAdvectionTwo.setTexture("textureless");

            fb.setRenderTarget(velocity);

            advectingObjTwo.setVisibility(true);
            fb.clear();
            displayWorld.renderScene(fb);
            displayWorld.draw(fb);
            fb.display();
            advectingObjTwo.setVisibility(false);
            fb.removeRenderTarget();

            //advectingObj.setTexture(advecting_ti);


            fb.setRenderTarget(textureless);
            fb.clear();
            fb.display();
            fb.sync();
            fb.removeRenderTarget();


            fb.setRenderTarget(density2);
            DensityAdvection.setVisibility(true);
            fb.clear();
            displayWorld.renderScene(fb);
            displayWorld.draw(fb);
            fb.display();
            DensityAdvection.setVisibility(false);

           // DensityAdvectionTwo.setTexture(advectdensity_tiTwo);
            advectingObj.setTexture(VELOCITY_TEXTURE_TAG);
        //    fb.removeRenderTarget();
            fb.setRenderTarget(density);
        }
       else
        {


            advectingObjTwo.setTexture("textureless");
            DensityAdvection.setTexture("textureless");

            fb.setRenderTarget(velocity2);
            advectingObj.setVisibility(true);
            fb.clear();
            displayWorld.renderScene(fb);
            displayWorld.draw(fb);
            fb.display();
            advectingObj.setVisibility(false);


            fb.removeRenderTarget();
            ///advectingObjTwo.setTexture(advecting_tiTwo);
            fb.sync();
            fb.setRenderTarget(textureless);
            fb.clear();
            displayWorld.renderScene(fb);
            displayWorld.draw(fb);
            fb.display();

            fb.removeRenderTarget();

            fb.setRenderTarget(density);
            DensityAdvectionTwo.setVisibility(true);
            fb.clear();
            displayWorld.renderScene(fb);
            displayWorld.draw(fb);
            fb.display();
            DensityAdvectionTwo.setVisibility(false);

           //DensityAdvection.setTexture(advectdensity_ti);
            advectingObjTwo.setTexture(VELOCITY_TEXTURE_TAG_TWO);
      //      fb.removeRenderTarget();
            fb.setRenderTarget(density2);
        }



    //add new density from the splat
    //fb.setRenderTarget(density);//DENSITY IS ALREADY SET


    fb.setRenderTarget(add_density);
    densityObj.setVisibility(true);
    fb.clear();
    displayWorld.renderScene(fb);
    displayWorld.draw(fb);
    fb.display();
    densityObj.setVisibility(false);


    DensityAdvection.setTexture(advectdensity_ti);
    DensityAdvectionTwo.setTexture(advectdensity_tiTwo);

    //add new velocity
    fb.setRenderTarget(velocity);
    impulseObj.setVisibility(true);
    fb.clear();
    displayWorld.renderScene(fb);
    displayWorld.draw(fb);
    fb.display();
    impulseObj.setVisibility(false);


    fb.setRenderTarget(divergence);
    divergenceObj.setVisibility(true);
    fb.clear();
    displayWorld.renderScene(fb);
    displayWorld.draw(fb);
    fb.display();
    divergenceObj.setVisibility(false);


    fb.setRenderTarget(pressure);
    fb.clear();
    fb.display();


//    for(int i =1 ; i < JACOBI_ITERATIONS+1 ; i ++)
//    {
//    fb.setRenderTarget(pressure);
//    jacobiObj.setVisibility(true);
//    fb.clear();
//    displayWorld.renderScene(fb);
//    displayWorld.draw(fb);
//    fb.display();
//    jacobiObj.setVisibility(false);
//    }

//       fb.setRenderTarget(velocity);
//       fb.clear();
//       SubtractGradientWorld.renderScene(fb);
//       SubtractGradientWorld.draw(fb);
//       fb.display();
     //  doAdvectDensity= true;



        //DISPLAY -
        if(outPutTexture == null) {

            displayObj.setVisibility(true);
            fb.removeRenderTarget();
            fb.clear();
            displayWorld.renderScene(fb);//WAS POST PROCESS
            displayWorld.draw(fb);
            fb.display();
            displayObj.setVisibility(false);
        }
        else
        {
            fb.setRenderTarget(outPutTexture);
            fb.clear();
            displayWorld.renderScene(fb);//WAS POST PROCESS
            displayWorld.draw(fb);
            fb.display();
        }

        velocityswitch ^= true;

    }


    public void setupObjects()
    {




        advectingObj = Primitives.getPlane(4,10);
        advectingObj.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHook = new AdvectionHook(this,advectingShader);
        advectingObj.setRenderHook(advectionHook);
        advectingObj.setShader(advectingShader);
       // advecting_ti  =  new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
      //  advecting_ti.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG), TextureInfo.MODE_ADD);
        advectingObj.setTexture(VELOCITY_TEXTURE_TAG);
        advectingObj.setCulling(false);
        advectingObj.compile();
       // advectingObj.strip();
        displayWorld.addObject(advectingObj);
        advectingObj.setVisibility(false);

        advectingObjTwo = Primitives.getPlane(4,10);
        advectingObjTwo.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHooktwo = new AdvectionHook(this,advectingShader);
        advectingObjTwo.setRenderHook(advectionHooktwo);
        advectingObjTwo.setShader(advectingShader);
       // advecting_tiTwo   =  new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG_TWO));
       // advecting_tiTwo.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG_TWO), TextureInfo.MODE_ADD);
        advectingObjTwo.setTexture(VELOCITY_TEXTURE_TAG_TWO);
        advectingObjTwo.setCulling(false);
        advectingObjTwo.compile();
        //advectingObjTwo.strip();
        displayWorld.addObject(advectingObjTwo);
        advectingObjTwo.setVisibility(false);


        DensityAdvection = Primitives.getPlane(4,10);
        DensityAdvection.setOrigin(new SimpleVector(0.001, 0, 0));
        advectionHookForDensity = new AdvectionHook(this,advectingDensityShader);
        DensityAdvection.setRenderHook(advectionHookForDensity);
        DensityAdvection.setShader(advectingDensityShader);
        advectdensity_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advectdensity_ti.add(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG), TextureInfo.MODE_ADD);//add_density
        advectdensity_ti.add(TextureManager.getInstance().getTextureID(ADDITIONAL_DENSITY_TAG), TextureInfo.MODE_ADD);
        DensityAdvection.setTexture(advectdensity_ti);
        DensityAdvection.setCulling(false);
        DensityAdvection.compile();
        //DensityAdvection.strip();
        displayWorld.addObject(DensityAdvection);
        DensityAdvection.setVisibility(false);

        DensityAdvectionTwo = Primitives.getPlane(4,10);
        DensityAdvectionTwo.setOrigin(new SimpleVector(0.001, 0, 0));
        advectionHookForDensitytwo = new AdvectionHook(this,advectingDensityShader);
        DensityAdvectionTwo.setRenderHook(advectionHookForDensitytwo);
        DensityAdvectionTwo.setShader(advectingDensityShader);
        advectdensity_tiTwo =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG_TWO));
        advectdensity_tiTwo.add(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG_TWO), TextureInfo.MODE_ADD);
        DensityAdvectionTwo.setTexture(advectdensity_tiTwo);
        DensityAdvectionTwo.setCulling(false);
        DensityAdvectionTwo.compile();
     //   DensityAdvectionTwo.strip();
        displayWorld.addObject(DensityAdvectionTwo);
        DensityAdvectionTwo.setVisibility(false);



        densityObj = Primitives.getPlane(4,10);
        densityObj.setOrigin(new SimpleVector(0.01, 0, 0));
        densitySplatHook = new DensitySplatHook(this, densityShader);
        densityObj.setRenderHook(densitySplatHook);
        densityObj.setShader(densityShader);
        density_ti=new TextureInfo(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG));
        densityObj.setTexture(density_ti);
        densityObj.setCulling(false);
        densityObj.compile();
     //   densityObj.strip();
        displayWorld.addObject(densityObj);
        densityObj.setVisibility(false);

        impulseObj = Primitives.getPlane(4,10);
        impulseObj.setOrigin(new SimpleVector(0.01, 0, 0));
        impulseHook = new ImpulseHook(this, impulseShader);
        impulseObj.setShader(impulseShader);
        impulseObj.setRenderHook(impulseHook);
        impulse_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        impulseObj.setTexture(impulse_ti);
        impulseObj.setCulling(false);
        impulseObj.compile();
    //    impulseObj.strip();
        displayWorld.addObject(impulseObj);
        impulseObj.setVisibility(false);

        divergenceObj = Primitives.getPlane(4,10);
        divergenceObj.setOrigin(new SimpleVector(0.01, 0, 0));
        divergenceHook = new DivergenceHook(this,divergenceShader);
        divergenceObj.setShader(divergenceShader);
        divergenceObj.setRenderHook(divergenceHook);
        divergence_ti=new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        divergenceObj.setTexture(divergence_ti);
        divergenceObj.setCulling(false);
        divergenceObj.compile();
    //    divergenceObj.strip();
        displayWorld.addObject(divergenceObj);
        divergenceObj.setVisibility(false);

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
    //    jacobiObj.strip();
        displayWorld.addObject(jacobiObj);
        jacobiObj.setVisibility(false);

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
    //    subGradientObj.strip();
        displayWorld.addObject(subGradientObj);
        subGradientObj.setVisibility(false);

      //Displays a texture. doesn't process
        displayObj = Primitives.getPlane(4,10);
        displayObj.setOrigin(new SimpleVector(0.01, 0, 0));
        displayObj.setShader(displayShader);
        displayObj.setTexture(VELOCITY_TEXTURE_TAG);
        viewtype = Viewtype.VELOCITY;
        displayObj.setCulling(false);
        displayWorld.addObject(displayObj);
        displayObj.setVisibility(false);
    }




    public void setupTextures(int w, int h)
    {

        int width= w;
        int height= h;


        textureless = new NPOTTexture(width , height, RGBColor.BLACK);
        textureless.setFiltering(false);
        textureless.setMipmap(false);
        textureless.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture("textureless", textureless);



        velocity = new NPOTTexture(width , height, RGBColor.BLACK);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);

        velocity2 = new NPOTTexture(width , height, RGBColor.BLACK);
        velocity2.setFiltering(textureFiltering);
        velocity2.setMipmap(textureMipMap);
        velocity2.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG_TWO, velocity2);


        density = new NPOTTexture(width , height, RGBColor.BLACK);
        density.setFiltering(textureFiltering);
        density.setMipmap(textureMipMap);
        density.setTextureCompression(textureCompression);
        tm.addTexture(DENSITY_TEXTURE_TAG, density);

        density2 = new NPOTTexture(width , height, RGBColor.BLACK);
        density2.setFiltering(textureFiltering);
        density2.setMipmap(textureMipMap);
        density2.setTextureCompression(textureCompression);
        tm.addTexture(DENSITY_TEXTURE_TAG_TWO, density2);


        add_density = new NPOTTexture(width , height, RGBColor.BLACK);
        add_density.setFiltering(textureFiltering);
        add_density.setMipmap(textureMipMap);
        add_density.setTextureCompression(textureCompression);
        tm.addTexture(ADDITIONAL_DENSITY_TAG, add_density);


        pressure = new NPOTTexture(width , height, RGBColor.BLACK);
        pressure.setFiltering(textureFiltering);
        pressure.setMipmap(textureMipMap);
        pressure.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(PRESSURE_TEXTURE_TAG, pressure);

        divergence = new NPOTTexture(width , height, RGBColor.BLACK);
        divergence.setFiltering(textureFiltering);
        divergence.setMipmap(textureMipMap);
        divergence.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(DIVERGENCE_TEXTURE_TAG, divergence);
    }

    public void setUpCameras()
    {
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
        advectingDensityShader =new GLSLShader(vertexShader, Loader.loadTextFile(res.openRawResource(R.raw.advectdensity_frag)));

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





    //
    public void AddDensity()
    {


    }










}
