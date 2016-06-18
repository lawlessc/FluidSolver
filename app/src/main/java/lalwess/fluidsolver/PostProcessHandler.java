package lalwess.fluidsolver;

import android.content.res.Resources;

import com.threed.jpct.*;

import lalwess.fluidsolver.ResolverRenderHooks.AdvectionHook;
import lalwess.fluidsolver.ResolverRenderHooks.DivergenceHook;
import lalwess.fluidsolver.ResolverRenderHooks.ImpulseHook;
import lalwess.fluidsolver.ResolverRenderHooks.JacobiRenderHook;
import lalwess.fluidsolver.ResolverRenderHooks.SubtractHook;


/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessHandler {


    public NPOTTexture outPutTexture = null; //if not a null we output texture to this.

     World FillWorld;
     Camera fillcam = null;



    TextureManager tm = TextureManager.getInstance();


    //TextureInfo adding_ti;




    Boolean textureCompression= true;
    Boolean textureFiltering= true;
    Boolean textureMipMap= false;
    Boolean firstRun = true;



    String VELOCITY_TEXTURE_TAG= "velocity";
    String DENSITY_TEXTURE_TAG= "density";

    String DIVERGENCE_TEXTURE_TAG= "divergence";
    String PRESSURE_TEXTURE_TAG= "pressure";


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

    public NPOTTexture velocity;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture velocity2;//VECLOCITY IF FOR ADVECTING
    public NPOTTexture density;
    public NPOTTexture density2;
    public NPOTTexture pressure;
    public NPOTTexture pressure2;
    //    public NPOTTexture diffusion;
    public NPOTTexture divergence;
    public NPOTTexture divergence2;

    public NPOTTexture Temp;
   // public NPOTTexture vorticity;


    public World AdvectingWorld;
    public Camera camb = null;
    public TextureInfo advecting_ti= null;
    public GLSLShader advectingShader;
    public Object3D  advectingObj = null;


    World advectDensityWorld;
    Camera camden = null;
    TextureInfo advectdensity_ti= null;
    Object3D  advectDensity = null;

    World ImpulseWorld;
    Camera impulseCam = null;
    TextureInfo impulse_ti= null;
    GLSLShader impulseShader = null;
    Object3D impulseObj = null;

    World divergenceWorld;
    Camera camd = null;
    TextureInfo divergence_ti= null;
    GLSLShader divergenceShader;
    Object3D  divergenceObj = null;

    //This step is carried out multiple times
    World jacobiWorld;
    Camera camf = null;
    TextureInfo jacobi_ti= null;
    Object3D  jacobiObj;
    GLSLShader jacobiShader = null;


    //Gradient Subtraction, pressure subtracted from velocity
    World SubtractGradientWorld;
    Camera subGradientCam = null;
    TextureInfo subGradient_ti;
    GLSLShader subGradientShader = null;
    Object3D subGradientObj = null;


    public World displayWorld;
    public Camera displayCam = null;
    public GLSLShader displayShader = null;
    public Object3D displayObj = null;




    public PostProcessHandler(Resources res, FrameBuffer fb) {
    loadShaders(res);
    setUpCameras();//worlds
    setupTextures(fb.getWidth(),fb.getHeight());

     InverseSize = new SimpleVector(1.0f/ fb.getWidth() ,1.0f/ fb.getHeight() ,0);
     splatRadius =   fb.getWidth() /8.0f;
     splatPos    =  new SimpleVector(  fb.getWidth() / 2.0f, fb.getWidth() /2.0f , 0);
     setupTextureInfos();
     setupObjects();
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


         //First Stage we advect velocity with itself
        fb.setRenderTarget(velocity);
        fb.clear();
        AdvectingWorld.renderScene(fb);
        AdvectingWorld.draw(fb);
        fb.display();
        swapVelocities();


        fb.setRenderTarget(density);
        fb.clear();
        advectDensityWorld.renderScene(fb);
        advectDensityWorld.draw(fb);
        fb.display();
        swapDensity();


//       //IMPULSE is applied to density normally
        fb.setRenderTarget(density);
        fb.clear();
        ImpulseWorld.renderScene(fb);
        ImpulseWorld.draw(fb);
        fb.display();
        swapDensity();
       // swapPressure();


        fb.setRenderTarget(divergence);
        fb.clear();
        divergenceWorld.renderScene(fb);
        divergenceWorld.draw(fb);
        fb.display();


        //WE Clear Pressure
        fb.setRenderTarget(pressure);
        fb.clear();
        fb.display();
        swapPressure();

        for(int i =1 ; i < JACOBI_ITERATIONS+1 ; i ++)
        {

            fb.setRenderTarget(pressure);
            fb.clear();
            jacobiWorld.renderScene(fb);
            jacobiWorld.draw(fb);
            fb.display();
            swapPressure();
        }


       fb.setRenderTarget(velocity2);
       fb.clear();
       SubtractGradientWorld.renderScene(fb);
       SubtractGradientWorld.draw(fb);
       fb.display();
       swapVelocities();

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

    //have this rotate through textures to see what is going on.
    public void rotateDisplayTexture()
    {


       // displayObj.setTexture(DENSITY_TEXTURE_TAG);

    }


    public void setupObjects()
    {

        advectingObj = Primitives.getPlane(4,10);
        advectingObj.setOrigin(new SimpleVector(0.01, 0, 0));
        advectionHook = new AdvectionHook(this,advectingShader);
        advectingObj.setRenderHook(advectionHook);
        advectingObj.setShader(advectingShader);
        advectingObj.setTexture(advecting_ti);
        advectingObj.setCulling(false);
        AdvectingWorld.addObject(advectingObj);


        advectDensity = Primitives.getPlane(4,10);
        advectDensity.setOrigin(new SimpleVector(0.001, 0, 0));
        advectionHookForDensity = new AdvectionHook(this,advectingShader);
        advectDensity.setCulling(false);
        advectDensity.setShader(advectingShader);
        advectDensity.setRenderHook(advectionHookForDensity);
        advectDensity.setTexture(advectdensity_ti);
        advectDensityWorld.addObject(advectDensity);

        impulseObj = Primitives.getPlane(4,10);
        impulseObj.setOrigin(new SimpleVector(0.01, 0, 0));
        impulseHook = new ImpulseHook(this,impulseShader);
        impulseObj.setCulling(false);
        impulseObj.setShader(impulseShader);
        impulseObj.setRenderHook(impulseHook);
        impulseObj.setTexture(impulse_ti);
        ImpulseWorld.addObject(impulseObj);

        divergenceObj = Primitives.getPlane(4,10);
        divergenceObj.setOrigin(new SimpleVector(0.01, 0, 0));
        divergenceHook = new DivergenceHook(this,divergenceShader);
        divergenceObj.setCulling(false);
        divergenceObj.setShader(divergenceShader);
        divergenceObj.setRenderHook(divergenceHook);
        divergenceObj.setTexture(divergence_ti);
        divergenceWorld.addObject(divergenceObj);


        jacobiObj = Primitives.getPlane(4,10);
        jacobiObj.setOrigin(new SimpleVector(0.01, 0, 0));
        jacobiRenderHook = new JacobiRenderHook(this,divergenceShader);
        jacobiObj.setCulling(false);
        jacobiObj.setShader(jacobiShader);
        jacobiObj.setRenderHook(jacobiRenderHook);
        jacobiObj.setTexture(jacobi_ti);
        jacobiWorld.addObject(jacobiObj);


        subGradientObj = Primitives.getPlane(4,10);
        subGradientObj.setOrigin(new SimpleVector(0.01, 0, 0));
        subtractHook = new SubtractHook(this,subGradientShader);
        subGradientObj.setCulling(false);
        subGradientObj.setShader(subGradientShader);
        subGradientObj.setRenderHook(subtractHook);
        subGradientObj.setTexture(subGradient_ti);
        SubtractGradientWorld.addObject(subGradientObj);

      //Displays a texture. doesn't process
        displayObj = Primitives.getPlane(4,10);
        displayObj.setOrigin(new SimpleVector(0.01, 0, 0));
        displayObj.setShader(displayShader);
        displayObj.setTexture(DENSITY_TEXTURE_TAG);
        displayObj.setCulling(false);
        displayWorld.addObject(displayObj);
    }




    public void setupTextures(int w, int h)
    {
        velocity = new NPOTTexture(w , h, RGBColor.BLACK);
        velocity.setFiltering(textureFiltering);
        velocity.setMipmap(textureMipMap);
        velocity.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);

        velocity2 = new NPOTTexture(w , h, RGBColor.BLACK);
        velocity2.setFiltering(textureFiltering);
        velocity2.setMipmap(textureMipMap);
        velocity2.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        //tm.addTexture(VELOCITY_TEXTURE_TAG, velocity);


        pressure = new NPOTTexture(w , h, RGBColor.BLACK);
        pressure.setFiltering(textureFiltering);
        pressure.setMipmap(textureMipMap);
        pressure.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(PRESSURE_TEXTURE_TAG, pressure);

        pressure2 = new NPOTTexture(w , h, RGBColor.BLACK);
        pressure2.setFiltering(textureFiltering);
        pressure2.setMipmap(textureMipMap);
        pressure2.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        //tm.addTexture("pressure", pressure);



        divergence = new NPOTTexture(w , h, RGBColor.BLACK);
        divergence.setFiltering(textureFiltering);
        divergence.setMipmap(textureMipMap);
        divergence.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        tm.addTexture(DIVERGENCE_TEXTURE_TAG, divergence);

        divergence2 = new NPOTTexture(w , h, RGBColor.BLACK);
        divergence2.setFiltering(textureFiltering);
        divergence2.setMipmap(textureMipMap);
        divergence2.setTextureCompression(textureCompression);// texture compression eliminates the artifacts
        //tm.addTexture("divergence", divergence);


        density = new NPOTTexture(w , h, RGBColor.BLACK);
        density.setFiltering(textureFiltering);
        density.setMipmap(textureMipMap);
        density.setTextureCompression(textureCompression);
        tm.addTexture(DENSITY_TEXTURE_TAG, density);

        density2 = new NPOTTexture(w , h, RGBColor.BLACK);
        density2.setFiltering(textureFiltering);
        density2.setMipmap(textureMipMap);
        density2.setTextureCompression(textureCompression);
       // tm.addTexture(DENSITY_TEXTURE_TAG, density);


    }

    public void setUpCameras()
    {

        FillWorld = new World();
        fillcam= FillWorld.getCamera();
        fillcam.setPosition(-10, 0, 0);
        fillcam.lookAt(new SimpleVector(0, 0, 0));




        AdvectingWorld = new World();
        camb=AdvectingWorld.getCamera();
        camb.setPosition(-10, 0, 0);
        camb.lookAt(new SimpleVector(0, 0, 0));


        advectDensityWorld= new World();
        camden = advectDensityWorld.getCamera();
        camden.setPosition(-10, 0, 0);
        camden.lookAt(new SimpleVector(0, 0, 0));


        ImpulseWorld = new World();
        impulseCam= ImpulseWorld.getCamera();
        impulseCam.setPosition(-10, 0, 0);
        impulseCam.lookAt(new SimpleVector(0, 0, 0));

        divergenceWorld = new World();
        camd=divergenceWorld.getCamera();
        camd.setPosition(-10, 0, 0);
        camd.lookAt(new SimpleVector(0, 0, 0));



        jacobiWorld = new World();
        camf=jacobiWorld.getCamera();
        camf.setPosition(-10, 0, 0);
        camf.lookAt(new SimpleVector(0, 0, 0));



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
      //  fillingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.fill_frag)));
        advectingShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.advect_frag)));



        divergenceShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.divergence_frag)));
        impulseShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.splat_frag)));
        jacobiShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.jacobi_frag)));


        subGradientShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));
        //subtractingShader =  new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.subtract_frag)));;


        //boundaryShader = new GLSLShader(Loader.loadTextFile(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.boundary)));


        displayShader = new GLSLShader(vertexShader,Loader.loadTextFile(res.openRawResource(R.raw.rendering_frag)));
    }

    public void setupTextureInfos() {

        advecting_ti   =  new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advecting_ti.add(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG), TextureInfo.MODE_ADD);

        advectdensity_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        advectdensity_ti.add(TextureManager.getInstance().getTextureID(DENSITY_TEXTURE_TAG), TextureInfo.MODE_ADD);

        impulse_ti=new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));

      //  adding_ti=new TextureInfo(TextureManager.getInstance().getTextureID("processingTexture"));

        divergence_ti=new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));

        jacobi_ti   =new TextureInfo(TextureManager.getInstance().getTextureID(PRESSURE_TEXTURE_TAG));
        jacobi_ti.add(TextureManager.getInstance().getTextureID(DIVERGENCE_TEXTURE_TAG), TextureInfo.MODE_ADD);

        subGradient_ti =new TextureInfo(TextureManager.getInstance().getTextureID(VELOCITY_TEXTURE_TAG));
        subGradient_ti.add(TextureManager.getInstance().getTextureID(PRESSURE_TEXTURE_TAG), TextureInfo.MODE_ADD);
    }


    public void setOutPutTexture(NPOTTexture outPutTexture)
    {

        this.outPutTexture = outPutTexture;
        InverseSize = new SimpleVector(1.0f/ outPutTexture.getWidth() ,1.0f/ outPutTexture.getHeight() ,0);
    }


//    private void swapTextures(FrameBuffer fb,String name,Texture one ,Texture two ) {
//
//        if (textSwitch) {
//            tm.replaceTexture(name, two);
//            fb.setRenderTarget(one);
//        } else {
//
//            tm.replaceTexture(name, one);
//            fb.setRenderTarget(two);
//        }
//
//    }

    public void setSplatPos(float x , float y)
    {

        y = velocity.getHeight() -y;
        splatPos = new SimpleVector(x,y,0);
    }



    private void swapVelocities()
    {
        Temp = velocity ;
        velocity=velocity2;
        velocity2=Temp;
        tm.replaceTexture(VELOCITY_TEXTURE_TAG, velocity2);
    }

    private void swapDensity()
    {
        Temp = density ;
        density=density2;
        density2=Temp;
        tm.replaceTexture(DENSITY_TEXTURE_TAG, density2);
    }


    private void swapPressure()
    {

        Temp = pressure ;
        pressure=pressure2;
        pressure2=Temp;
        tm.replaceTexture(PRESSURE_TEXTURE_TAG, pressure2);
    }

//    private void swapDivergence(FrameBuffer fb)
//    {
//
//        Temp = divergence ;
//        divergence=divergence2;
//        divergence2=Temp;
//
//        tm.replaceTexture("divergence", divergence2);
//        fb.setRenderTarget(divergence);
//
//    }






}
