package lalwess.fluidsolver.ResolverRenderHooks;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.Object3D;

import lalwess.fluidsolver.PostProcessHandler;

/**
 * Created by Chris on 29/05/2016.
 */
public class AdvectionHook implements IRenderHook {


    PostProcessHandler parent;
    GLSLShader advection;


    public AdvectionHook(PostProcessHandler parent , GLSLShader advection)
    {
        this.parent=parent;
        this.advection =advection;


    }


    @Override
    public void beforeRendering(int i) {

      //advection.setStaticUniform("InverseSize", parent.SCALE);
       advection.setStaticUniform("TimeStep", parent.TIMESTEP);
       advection.setStaticUniform("Dissipation",parent.VELOCITY_DISSIPATION);


    }

    @Override
    public void afterRendering(int i) {

    }

    @Override
    public void setCurrentObject3D(Object3D object3D) {

    }

    @Override
    public void setCurrentShader(GLSLShader glslShader) {

    }

    @Override
    public void setTransparency(float v) {

    }

    @Override
    public void onDispose() {

    }

    @Override
    public boolean repeatRendering() {
        return false;
    }
}
