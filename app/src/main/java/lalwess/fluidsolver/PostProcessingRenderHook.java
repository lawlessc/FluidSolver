package lalwess.fluidsolver;

import com.threed.jpct.GLSLShader;
import com.threed.jpct.IRenderHook;
import com.threed.jpct.Object3D;

/**
 * Created by lawless on 12/10/2015.
 */
public class PostProcessingRenderHook implements IRenderHook {

    Object3D obj;
    GLSLShader shader;
    PostProcessHandler handler;

    public PostProcessingRenderHook(Object3D obj, GLSLShader shader, PostProcessHandler handler) {

        this.obj=obj;
        this.shader=shader;
        this.handler=handler;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void beforeRendering(int i) {



        shader.setUniform("doLightScattering", handler.doLightScattering);
        shader.setUniform("lightPositionOnScreen", handler.sunScreenPos);
        shader.setUniform("exposure", 0.2f);//0.0034
        shader.setUniform("decay", 0.96815f);// 0.96815f);
        shader.setUniform("density",0.926f);//0.84
        shader.setUniform("weight", 0.58767f);
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


