
uniform sampler2D textureUnit0;//Veclocity
uniform sampler2D textureUnit1;//source texture


//uniform sampler2D textureUnit2;//Obstacle Texture


uniform highp float TimeStep;
uniform highp float Dissipation;
varying  lowp vec2 v_texCoord;
uniform lowp vec3 inversesize;
void main()
{
    highp vec2  fragCoord = v_texCoord;
    //float solid = texture(textureUnit2, inversesize.xy  * fragCoord).x;
    //if (solid > 0) {
     //   gl_FragColor = vec4(0);
    //}
                           //1.0 is inversesize
    highp vec2 u = texture2D(textureUnit0, inversesize.xy * fragCoord).xy;
    highp vec2 coord = inversesize.xy  * (fragCoord - TimeStep * u);      //1.0 is inversesize
  //  gl_FragColor = Dissipation * texture2D(textureUnit1, coord);
     gl_FragColor = vec4(0,1,0,1);
}


