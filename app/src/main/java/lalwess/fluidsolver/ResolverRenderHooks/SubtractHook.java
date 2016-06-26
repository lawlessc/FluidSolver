package lalwess.fluidsolver.ResolverRenderHooks;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.Object3D;

import lalwess.fluidsolver.PostProcessHandler;

/**
 * Created by Chris on 29/05/2016.
 */
public class SubtractHook  implements IRenderHook {






    PostProcessHandler parent;
    GLSLShader subtraction;





    public SubtractHook(PostProcessHandler parent , GLSLShader subtraction)
    {
        this.parent=parent;
        this.subtraction =subtraction;


    }






    @Override
    public void beforeRendering(int i) {

       // subtraction.setStaticUniform("aspectRatio", parent.AspectRatio);
        subtraction.setStaticUniform("inversesize", parent.InverseSize);

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
