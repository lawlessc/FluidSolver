
uniform lowp sampler2D textureUnit0;//Velocity
uniform lowp sampler2D textureUnit1;//Obstacles
uniform lowp float HalfInverseCellSize;
varying  lowp vec2 v_texCoord;


void main()
{
    lowp vec2 T = v_texCoord.xy;

    // Find neighboring velocities:
    lowp vec2 vN = texture2D(textureUnit0, T +vec2(0, 1)).xy;
    lowp vec2 vS = texture2D(textureUnit0, T +vec2(0, -1)).xy;
    lowp vec2 vE = texture2D(textureUnit0, T +vec2(1, 0)).xy;
    lowp vec2 vW = texture2D(textureUnit0, T +vec2(-1, 0)).xy;

    // Find neighboring obstacles:
  //  vec3 oN = texelFetchOffset(textureUnit1, T, 0, ivec2(0, 1)).xyz;
  //  vec3 oS = texelFetchOffset(textureUnit1, T, 0, ivec2(0, -1)).xyz;
  //  vec3 oE = texelFetchOffset(textureUnit1, T, 0, ivec2(1, 0)).xyz;
  //  vec3 oW = texelFetchOffset(textureUnit1, T, 0, ivec2(-1, 0)).xyz;

    // Use obstacle velocities for solid cells:
  //  if (oN.x > 0) vN = oN.yz;
  //  if (oS.x > 0) vS = oS.yz;
  //  if (oE.x > 0) vE = oE.yz;
  //  if (oW.x > 0) vW = oW.yz;

   // gl_FragColor = HalfInverseCellSize * (vE.x - vW.x + vN.y - vS.y);

    gl_FragColor = vec4(HalfInverseCellSize * (vE.x - vW.x + vN.y - vS.y)  ,0 ,0 ,0);
}