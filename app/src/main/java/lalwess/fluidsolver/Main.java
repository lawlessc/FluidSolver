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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * @author Christopher Lawless
 */
public class Main extends Activity implements OnScaleGestureListener /*,Observer */ {
	// Used to handle pause and resume...
	private static Main master = null;
	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;

	private boolean useRetroRender = false;
	public enum allGameObjects {
        INSTANCE;

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

	//private Texture font = null;
	private Boolean texturesLoaded= false;
	//////////////////////////////////This needs to be seperated from the graphical stuff above somehow.




	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		//Logger.setLogLevel(Logger.LL_DEBUG);
		//Logger.setLogLevel(Logger.);
		//
		//
		Logger.setLogLevel(Logger.LL_VERBOSE);


		//Context baseContext= this.getBaseContext();
		//Resources res = getResources();


		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);



		if(master == null)
		{
			//This Sets Renderer , I may want to seperate these classes!
			setContentView(R.layout.activity_main); //or whatever the layout you want to use
			mGLView = (GLSurfaceView) findViewById(R.id.graphics_glsurfaceview1);


			//mGLView = new GLSurfaceView(getApplication());
			// Enable the OpenGL ES2.0 context
			mGLView.setEGLContextClientVersion(2);
			//
			//mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView));

			renderer = new MyRenderer();
			mGLView.setRenderer(renderer);

			Texture.defaultToMipmapping(false);
			Texture.defaultTo4bpp(true);
			Texture.defaultToKeepPixels(true);
			Config.maxTextureLayers = 4;
			Config.maxPolysVisible = 5000;
			Config.farPlane = 100;
			//Config.nearPlane = 0;
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
    //    int left = mGLView.getLeft();
    //    int top  =mGLView.getTop();


		allGameObjects.INSTANCE.processHandler.setSplatPos(me.getX() , me.getY());

		return super.onTouchEvent(me);
	}



	class MyRenderer implements GLSurfaceView.Renderer {
		private int fps = 0;
		private int lfps = 0;
		private long time = System.currentTimeMillis();


		public MyRenderer() {
		//	Resources res = getResources();

			//time = System.currentTimeMillis();
			//font = new Texture(res.openRawResource(R.raw.numbers));
			//	font.setMipmap(false);
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {

			if (fb != null) {
				//	fb.dispose();
				fb.resize(w, h);
			}
			// else{
			fb = new FrameBuffer(w, h);
			//	}


			//  fb.setBlittingShader(EntityFactory.gameboy_shader);

			if (master == null) {
			//	System.out.println("MASTER IS NULLLLL");
				master = Main.this;


				Resources res = getResources();
				//allGameObjects.INSTANCE.gameeventmanager.mainSetup(res,w,h,fb);
				allGameObjects.INSTANCE.processHandler = new PostProcessHandler(res, fb);


				current = System.currentTimeMillis();
				lag = 0;
				previous = System.currentTimeMillis();

				//allGameObjects.INSTANCE.gameeventmanager.mainMenu();


				MemoryHelper.compact();
				//Main.allGameObjects.INSTANCE.world.compileAllObjects();
			}

		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		//	Logger.log("onSurfaceCreated");
		}


		public void onDrawFrame(GL10 gl) {


			if (!allGameObjects.INSTANCE.isActionPaused) {
				current = System.currentTimeMillis();
				elapsed = current - previous;
				allGameObjects.INSTANCE.runningTime += elapsed;
				allGameObjects.INSTANCE.runningTimeSeconds = (int) (allGameObjects.INSTANCE.runningTime * 0.001);
				previous = current;
				lag += elapsed;


				while (lag >= MS_PER_UPDATE) {

					lag -= MS_PER_UPDATE;
				}
			} else {

				lag = 0;
				previous = System.currentTimeMillis();
				current = System.currentTimeMillis();
			}

			allGameObjects.INSTANCE.processHandler.Process(fb);


			if (System.currentTimeMillis() - time >= 1000) {
				lfps = fps;
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}
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
		//allGameObjects.INSTANCE.cameraCursor.setScaleFactor(detector);
        return true;
    }
}





	public void asteroidTest(View view) {
		//allShields.execute();

		SimpleVector ctop=  new SimpleVector(10,20,10);
		SimpleVector planepos = new SimpleVector(20,0, 40);

		SimpleVector pucpos= new SimpleVector( 20,0,40);




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
		//allGameObjects.INSTANCE.processHandler.setSplatPos(e.getX() , e.getY());
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

		allGameObjects.INSTANCE.processHandler.switchView();
		//allGameObjects.INSTANCE.cameraCursor.onDoubleTap(e,fb, left, top);
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		int left = mGLView.getLeft();
		int top  =mGLView.getTop();
		//a/llGameObjects.INSTANCE.cameraCursor.onSingleTapConfirmed(e,fb, left, top);
		//pauseAction();
        //cameraCursor.onDoubleTap(e,fb);
		//allGameObjects.INSTANCE.processHandler.setSplatPos(e.getX() , e.getY());
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
