precision lowp float;
uniform  sampler2D textureUnit0;//Velocity
//uniform  sampler2D textureUnit1;//Obstacles
uniform  float HalfInverseCellSize;
//varying   vec2 v_texCoord;
uniform  vec3 inversesize;

void main()
{
   //  vec2 T = v_texCoord.xy;

   vec2 T = vec2(gl_FragCoord.xy);
   vec2 stepX = vec2(inversesize.x, 0);
   vec2 stepY = vec2(0, inversesize.y);



    // Find neighboring velocities:
//     vec2 vN = texture2D(textureUnit0, T +vec2(0, 1)).xy;
//     vec2 vS = texture2D(textureUnit0, T +vec2(0, -1)).xy;
//     vec2 vE = texture2D(textureUnit0, T +vec2(1, 0)).xy;
//     vec2 vW = texture2D(textureUnit0, T +vec2(-1, 0)).xy;


      vec2 vN = texture2D(textureUnit0, T + stepY).xy;
      vec2 vS = texture2D(textureUnit0, T - stepY).xy;
      vec2 vE = texture2D(textureUnit0, T + stepX).xy;
      vec2 vW = texture2D(textureUnit0, T - stepX).xy;

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

     float fin = HalfInverseCellSize * (vE.x - vW.x + vN.y - vS.y);

   // gl_FragColor = vec4(fin.xy,0,1);
}