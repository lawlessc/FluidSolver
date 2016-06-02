
uniform sampler2D textureUnit0;//Veclocity

uniform sampler2D textureUnit1;//source texture


//uniform sampler2D textureUnit2;//Obstacle Texture

//uniform vec2 InverseSize;
uniform highp float TimeStep;
uniform highp float Dissipation;
varying  lowp vec2 v_texCoord;

void main()
{
    highp vec2  fragCoord = v_texCoord;
    //float solid = texture(textureUnit2, InverseSize * fragCoord).x;
    //if (solid > 0) {
     //   gl_FragColor = vec4(0);
    //}
                           //1.0 is inversesize
    highp vec2 u = texture2D(textureUnit0, 1.0 * fragCoord).xy;
    highp vec2 coord = 1.0 * (fragCoord - TimeStep * u);      //1.0 is inversesize
    gl_FragColor = Dissipation * texture2D(textureUnit1, coord);
}


