
uniform sampler2D textureUnit0;//Velocity
uniform sampler2D textureUnit1;//Obstacles
uniform float HalfInverseCellSize;

void main()
{
    ivec2 T = ivec2(gl_FragCoord.xy);

    // Find neighboring velocities:
    vec2 vN = texelFetchOffset(textureUnit0, T, 0, ivec2(0, 1)).xy;
    vec2 vS = texelFetchOffset(textureUnit0, T, 0, ivec2(0, -1)).xy;
    vec2 vE = texelFetchOffset(textureUnit0, T, 0, ivec2(1, 0)).xy;
    vec2 vW = texelFetchOffset(textureUnit0, T, 0, ivec2(-1, 0)).xy;

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

    gl_FragColor = HalfInverseCellSize * (vE.x - vW.x + vN.y - vS.y);
}