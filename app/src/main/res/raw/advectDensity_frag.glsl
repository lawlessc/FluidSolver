precision lowp float;
uniform sampler2D velocity;//Velocity
uniform sampler2D textureUnit1;//source texture
                         //uniform sampler2D textureUnit2;//Obstacle Texture
uniform sampler2D textureUnit2; // I NEED TO XOR THIS OVER THE CURRENT DENSITY.

uniform  float timeStep;
uniform  float dissipation;
uniform  vec3 inversesize;
varying vec2 v_texCoord;


void main()
{
   // vec2  fragCoord = v_texCoord.xy;
     vec2  fragCoord = gl_FragCoord.xy;

   //float solid = texture(textureUnit2, inversesize.xy  * fragCoord).x;
   //if (solid > 0) {
   //   gl_FragColor = vec4(0);
  //}
  vec2 u = texture2D(velocity, inversesize.xy * fragCoord).xy;
  u.x = (u.x*(-2.0))-1.0;
  u.y = (u.y*(-2.0))-1.0;

   vec2 coord = inversesize.xy  * (fragCoord - timeStep * u);



   gl_FragColor = dissipation * ( texture2D(textureUnit1, coord) + texture2D(textureUnit2, coord)/2.0 );
}