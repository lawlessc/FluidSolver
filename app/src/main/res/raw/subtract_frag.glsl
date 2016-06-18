uniform highp sampler2D textureUnit0;//Velocity
uniform highp sampler2D textureUnit1;//Pressure
//uniform sampler2D Obstacles;
uniform highp float GradientScale;

void main()
{
    highp vec2 T = vec2(gl_FragCoord.xy);

//    highp vec3 oC = texelFetch(Obstacles, T, 0).xyz;
//    if (oC.x > 0) {
//        FragColor = oC.yz;
//        return;
//    }

    // Find neighboring pressure:
    highp float pN = texture2D(textureUnit1, T +vec2(0, 1)).r;
    highp float pS = texture2D(textureUnit1, T+ vec2(0, -1)).r;
    highp float pE = texture2D(textureUnit1, T+ vec2(1, 0)).r;
    highp float pW = texture2D(textureUnit1, T+ vec2(-1, 0)).r;
    //highp float pC = texture2D(textureUnit1, T).r;

//    // Find neighboring obstacles:
//    vec3 oN = texelFetchOffset(Obstacles, T, 0, ivec2(0, 1)).xyz;
//    vec3 oS = texelFetchOffset(Obstacles, T, 0, ivec2(0, -1)).xyz;
//    vec3 oE = texelFetchOffset(Obstacles, T, 0, ivec2(1, 0)).xyz;
//    vec3 oW = texelFetchOffset(Obstacles, T, 0, ivec2(-1, 0)).xyz;

    // Use center pressure for solid cells:
    highp vec2 obstV = vec2(0);
    highp vec2 vMask = vec2(1);

   // if (oN.x > 0) { pN = pC; obstV.y = oN.z; vMask.y = 0; }
   // if (oS.x > 0) { pS = pC; obstV.y = oS.z; vMask.y = 0; }
   // if (oE.x > 0) { pE = pC; obstV.x = oE.y; vMask.x = 0; }
    //if (oW.x > 0) { pW = pC; obstV.x = oW.y; vMask.x = 0; }

    // Enforce the free-slip boundary condition:
    highp vec2 oldV = texture2D(textureUnit0, T).xy;
    highp vec2 grad =  vec2(pE - pW, pN - pS) * GradientScale;
    highp vec2 newV = oldV - grad;

    highp vec2 fin = (vMask * newV) + obstV;
   // gl_FragColor = vec4(fin.x,fin.y,0,1);
     gl_FragColor = vec4(fin.xy,0,1);

}