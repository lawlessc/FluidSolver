package lalwess.fluidsolver.ResolverRenderHooks;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.Object3D;

import lalwess.fluidsolver.PostProcessHandler;

/**
 * Created by Chris on 29/05/2016.
 */
public class JacobiRenderHook  implements IRenderHook {



    PostProcessHandler parent;
    GLSLShader jacobi;





    public JacobiRenderHook(PostProcessHandler parent , GLSLShader advection)
    {
        this.parent=parent;
        this.jacobi =advection;


    }




    @Override
    public void beforeRendering(int i) {


        jacobi.setStaticUniform("aspectRatio", parent.AspectRatio);

        jacobi.setStaticUniform("inversesize", parent.InverseSize);

        jacobi.setStaticUniform("Alpha", parent.alpha);
     //   advection.setStaticUniform("InverseBeta", parent.CELLSIZE);

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
