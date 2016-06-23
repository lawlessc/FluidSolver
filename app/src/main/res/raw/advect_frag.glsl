precision lowp float;
uniform sampler2D textureUnit0;//Velocity
uniform sampler2D textureUnit1;//source texture
                         //uniform sampler2D textureUnit2;//Obstacle Texture
uniform  float timeStep;
uniform  float dissipation;
uniform  vec3 inversesize;



varying  vec2 v_texCoord;

varying  vec2 texelCoord;
varying  vec2 p;

void main()
{



    vec2  fragCoord = gl_FragCoord.xy;


   //  vec2  fragCoord = texelCoord;


   //   vec2  fragCoord = v_texCoord;


//     vec2 fragCoord =   vec2(gl_FragCoord.xy);
    //float solid = texture(textureUnit2, inversesize.xy  * fragCoord).x;
    //if (solid > 0) {
     //   gl_FragColor = vec4(0);
    //}
                           //1.0 is inversesize
   vec2 u = texture2D(textureUnit0, inversesize.xy * fragCoord).xy;
  // u.x = (u.x*2.0)-1.0;
  // u.y = (u.y*2.0)-1.0;
  //u = p -u;
   vec2 coord = inversesize.xy  * (fragCoord - timeStep * u);
   // coord.x= coord.x-(inversesize.x);
    coord.y= coord.y+(inversesize.y);
  //  coord = p -coord;
    //  coord.x = (coord.x*inversesize.x);//-inversesize.x;
   //   coord.y = (coord.y*inversesize.y);//-inversesize.y;




   gl_FragColor = dissipation * texture2D(textureUnit1, coord);
}