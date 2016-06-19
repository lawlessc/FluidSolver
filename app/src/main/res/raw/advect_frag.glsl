precision lowp float;
uniform sampler2D textureUnit0;//Veclocity
uniform sampler2D textureUnit1;//source texture
                         //uniform sampler2D textureUnit2;//Obstacle Texture
uniform  float timeStep;
uniform  float dissipation;
uniform  vec3 inversesize;

varying  vec2 v_texCoord;
void main()
{
     vec2  fragCoord = v_texCoord;
    //float solid = texture(textureUnit2, inversesize.xy  * fragCoord).x;
    //if (solid > 0) {
     //   gl_FragColor = vec4(0);
    //}
                           //1.0 is inversesize
   vec2 u = texture2D(textureUnit0, inversesize.xy * fragCoord).xy;
   vec2 coord = inversesize.xy  * (fragCoord - timeStep * u);      //1.0 is inversesize
   gl_FragColor = dissipation * texture2D(textureUnit1, coord);
}