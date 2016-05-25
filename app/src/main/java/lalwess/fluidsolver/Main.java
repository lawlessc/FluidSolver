package lalwess.fluidsolver;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;

import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.World;
import com.threed.jpct.util.AAConfigChooser;
import com.threed.jpct.util.MemoryHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Entity_types.Entity;
import Entity_types.SkySphere;
import EventManagers.*;
import MenuObjects.UIMenuManager;
import MenuObjects.UIObjectFactory;
import Particles.ParticleEvents;
import Particles.ParticleManager;
import baseinterfacesclasses.OrbitData;
import baseinterfacesclasses.TargetSelection;
import gameObjects.EntityFactory;
import gameObjects.FactoryObserver;

;

/**
 * @author Christopher Lawless
 */
public class Main extends Activity implements OnScaleGestureListener /*,Observer */ {
	// Used to handle pause and resume...
	private static Main master = null;
	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	//private final MyRenderer renderer = new MyRenderer();
	private FrameBuffer fb = null;
	private RGBColor back = new RGBColor(90, 50, 100);

	private boolean useRetroRender = false;

	public enum allGameObjects {
        INSTANCE;


        public World world = null;
    	public Camera cam = null;
    	//might make sense to start this at 0 anyway
    	public long runningTime=0;
		public long runningTimeSeconds;
    	public Boolean isActionPaused = false;




		public PostProcessHandler processHandler = null;

    }
	long MS_PER_UPDATE =16;
	
	public long previous;
	public long current;
	public long lag;
	public long elapsed;




	private ScaleGestureDetector mScaleDetector;
	private GestureDetector tapdetection;

	private Texture font = null;
	private Boolean texturesLoaded= false;
	//////////////////////////////////This needs to be seperated from the graphical stuff above somehow.




	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		//Logger.setLogLevel(Logger.LL_DEBUG);
		//Logger.setLogLevel(Logger.);
		//Logger.setLogLevel(Logger.LL_VERBOSE);


		//Context baseContext= this.getBaseContext();
		Resources res = getResources();


		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);



		if(master == null)
		{
			//This Sets Renderer , I may want to seperate these classes!

			mGLView = (GLSurfaceView) findViewById(R.id.graphics_glsurfaceview1);

			//mGLView = new GLSurfaceView(getApplication());
			// Enable the OpenGL ES2.0 context
			mGLView.setEGLContextClientVersion(2);
			//
			mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView));

			renderer = new MyRenderer();
			mGLView.setRenderer(renderer);

			Texture.defaultToMipmapping(true);
			Texture.defaultTo4bpp(true);
			Texture.defaultToKeepPixels(true);
			Config.maxTextureLayers = 4;
			Config.maxPolysVisible = 5000;
			Config.farPlane = 10500;
		}

		
		if (!texturesLoaded)
		{
		Context baseContext= this.getBaseContext();


		texturesLoaded=true;
		}

		mScaleDetector  = new ScaleGestureDetector(this , new ScaleListener());
		tapdetection    = new GestureDetector(this, new TapListener());
       // master = this;
	}






	@Override
	protected void onPause() {
		allGameObjects.INSTANCE.isActionPaused=true;
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
//		if(allGameObjects.INSTANCE.isActionPaused)
//		{
//			copy(master);
//		}
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		Logger.log("onStop");
		super.onStop();
	}

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

//IMPORTANT
	public boolean onTouchEvent(MotionEvent me) {
		mScaleDetector.onTouchEvent(me);
		tapdetection.onTouchEvent(me);
        int left = mGLView.getLeft();
        int top  =mGLView.getTop();

	
//		if (me.getAction() == MotionEvent.ACTION_DOWN) {
//		 if(allGameObjects.INSTANCE.menumanager != null) {
//			 allGameObjects.INSTANCE.cameraCursor.actionDown(me, fb, left, top);
//		 }
//			return true;
//		}
//		if (me.getAction() == MotionEvent.ACTION_UP) {
//			if(allGameObjects.INSTANCE.menumanager != null) {
//				allGameObjects.INSTANCE.cameraCursor.actionUp(me, fb);
//			}
//			return true;
//		}
//		if (me.getAction() == MotionEvent.ACTION_MOVE) {
//			if(allGameObjects.INSTANCE.menumanager != null) {
//				allGameObjects.INSTANCE.cameraCursor.actionMove(me, fb, left, top);
//			}
//			return true;
//		}

//		try {
//			Thread.sleep(15);
//		} catch (Exception e) {
//		}
		return super.onTouchEvent(me);
	}



	class MyRenderer implements GLSurfaceView.Renderer {
		private int fps = 0;
		private int lfps = 0;
		private long time = System.currentTimeMillis();



		public MyRenderer() {
            Resources res = getResources();

			//time = System.currentTimeMillis();
			font = new Texture(res.openRawResource(R.raw.numbers));
			font.setMipmap(false);	
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {

			if (fb != null) {
			//	fb.dispose();
				fb.resize(w,h);
			}
           // else{
				fb = new FrameBuffer(w, h);
		//	}






          //  fb.setBlittingShader(EntityFactory.gameboy_shader);

			if (master == null) {
				System.out.println("MASTER IS NULLLLL");
				master = Main.this;


				Resources res = getResources();
				//allGameObjects.INSTANCE.gameeventmanager.mainSetup(res,w,h,fb);

			   
			   current = System.currentTimeMillis();
			   lag = 0;
			   previous = System.currentTimeMillis();

				//allGameObjects.INSTANCE.gameeventmanager.mainMenu();



				MemoryHelper.compact();
				Main.allGameObjects.INSTANCE.world.compileAllObjects();

			}
		//	allGameObjects.INSTANCE.menumanager.setCornerPositions(fb,w,h);
		}


		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			Logger.log("onSurfaceCreated");
		}


		public void onDrawFrame(GL10 gl) {

			//allGameObjects.INSTANCE.cameraCursor.setOrbit();
 			//allGameObjects.INSTANCE.menumanager.update(); // updates ui

			if(!allGameObjects.INSTANCE.isActionPaused)
			{
				current = System.currentTimeMillis();
				elapsed = current - previous;
				allGameObjects.INSTANCE.runningTime+= elapsed;
				allGameObjects.INSTANCE.runningTimeSeconds = (int) (allGameObjects.INSTANCE.runningTime* 0.001) ;
				previous = current;
				lag += elapsed;


				while (lag >= MS_PER_UPDATE){
					//allGameObjects.INSTANCE.factoryObserver.additions();
					//physics.update(lag);
					//updateLoop.updateEntities((lag)/1000.0f);
					//allGameObjects.INSTANCE.colldec.collisionDetection();
					lag -= MS_PER_UPDATE;
				}
			}
			else
			{
				
			 lag = 0;
			 previous= System.currentTimeMillis();
			 current = System.currentTimeMillis();
			}
//			allGameObjects.INSTANCE.factoryObserver.handleRemovals();
//			allGameObjects.INSTANCE.particleManager.doRemovals();
//
//			allGameObjects.INSTANCE.skysphere.Update();
//
//			allGameObjects.INSTANCE.cameraCursor.cameraFollow();
//			allGameObjects.INSTANCE.menumanager.cameraUpdate();


			if(useRetroRender ==false) {
				allGameObjects.INSTANCE.processHandler.doPostProcess(fb);
			}
			else
			{
				allGameObjects.INSTANCE.processHandler.doRetroPostProcess(fb);
			}

			blitNumber(lfps, 5, 5);
			
			fb.display();

			if (System.currentTimeMillis() - time >= 1000) {
				lfps = fps;
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}



		private void blitNumber(int number, int x, int y) {
			if (font != null) {
				String sNum = Integer.toString(number);

				for (int i = 0; i < sNum.length(); i++) {
					char cNum = sNum.charAt(i);
					int iNum = cNum - 48;
					fb.blit(font, iNum * 5, 0, x, y, 5, 9, 5, 9, 10, true, null);
					
					x += 5;
				}
			}
		}
	}




	public void pauseAction()
	{
		allGameObjects.INSTANCE.gameCommand.pauseGame();
	}

	public boolean onScale(ScaleGestureDetector detector) {
		return true;
	}
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}


	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
	}


private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //I NEED TO FIND A WAY TO CHECK IF THIS IS ON AN OBJECT OR NOT?
		allGameObjects.INSTANCE.cameraCursor.setScaleFactor(detector);
        return true;
    }
}


public void shieldTest(View view) {
	allShields.execute();

}


	public void asteroidTest(View view) {
		//allShields.execute();

		SimpleVector ctop=  new SimpleVector(10,20,10);
		SimpleVector planepos = new SimpleVector(20,0, 40);

		SimpleVector pucpos= new SimpleVector( 20,0,40);


		OrbitData orbitData = new OrbitData(ctop, 20f, 0, SimpleVector.ORIGIN);
		allGameObjects.INSTANCE.object_factory.createAsteroid(SimpleVector.ORIGIN,SimpleVector.ORIGIN,
				EntityFactory.states.orbitState
				,orbitData);

	}


	public void retroSwitch(View view) {


		if(useRetroRender == false)
		{
			useRetroRender=true;
		}
		else
		{
			useRetroRender=false;
		}

	}



	public void testLoad(View view)
	{

		allGameObjects.INSTANCE.gameeventmanager.testLevel();
	}
	public void clearAll(View view)
	{

		allGameObjects.INSTANCE.gameeventmanager.clearGame();
	}


private class TapListener implements OnGestureListener, GestureDetector.OnDoubleTapListener
{
	@Override
	public boolean onDown(MotionEvent e) {

			// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
        int left = mGLView.getLeft();
        int top  =mGLView.getTop();
		allGameObjects.INSTANCE.cameraCursor.onDoubleTap(e,fb, left, top);
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		int left = mGLView.getLeft();
		int top  =mGLView.getTop();
		allGameObjects.INSTANCE.cameraCursor.onSingleTapConfirmed(e,fb, left, top);
		//pauseAction();
        //cameraCursor.onDoubleTap(e,fb);
		return false;
	}
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
    ///DONT USE THIS EVER!
	//IT FIRES FAR TOO MANY TIMES ON EACH PRESS, WILL FUCK UP YOUR DAY.
	return false;
	}

}
}
