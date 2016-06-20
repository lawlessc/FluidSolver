package lalwess.fluidsolver.ResolverRenderHooks;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

import lalwess.fluidsolver.PostProcessHandler;

/**
 * Created by Chris on 29/05/2016.
 *
 * Basically so i can tap the screen
 */
public class DensitySplatHook implements IRenderHook {

    PostProcessHandler parent;
    GLSLShader impulse;
    SimpleVector fillcol = new SimpleVector(1,0.5,0);


    public DensitySplatHook(PostProcessHandler parent , GLSLShader impulse)
    {
        this.parent=parent;
        this.impulse =impulse;
    }

    @Override
    public void beforeRendering(int i) {

        impulse.setStaticUniform("Point", parent.splatPos);
        impulse.setStaticUniform("Radius", parent.splatRadius);
        impulse.setStaticUniform("FillColor", fillcol);
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
