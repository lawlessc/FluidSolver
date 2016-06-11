uniform lowp sampler2D Velocity;
uniform lowp sampler2D Pressure;
//uniform sampler2D Obstacles;
uniform lowp float GradientScale;

void main()
{
    lowp vec2 T = vec2(gl_FragCoord.xy);

    lowp vec3 oC = texelFetch(Obstacles, T, 0).xyz;
    if (oC.x > 0) {
        FragColor = oC.yz;
        return;
    }

    // Find neighboring pressure:
    lowp float pN = texture2D(Pressure, T +vec2(0, 1)).r;
    lowp float pS = texture2D(Pressure, T+ vec2(0, -1)).r;
    lowp float pE = texture2D(Pressure, T+ vec2(1, 0)).r;
    lowp float pW = texture2D(Pressure, T+ vec2(-1, 0)).r;
    lowp float pC = texture2D(Pressure, T).r;

//    // Find neighboring obstacles:
//    vec3 oN = texelFetchOffset(Obstacles, T, 0, ivec2(0, 1)).xyz;
//    vec3 oS = texelFetchOffset(Obstacles, T, 0, ivec2(0, -1)).xyz;
//    vec3 oE = texelFetchOffset(Obstacles, T, 0, ivec2(1, 0)).xyz;
//    vec3 oW = texelFetchOffset(Obstacles, T, 0, ivec2(-1, 0)).xyz;

    // Use center pressure for solid cells:
    lowp vec2 obstV = vec2(0);
    lowp vec2 vMask = vec2(1);

   // if (oN.x > 0) { pN = pC; obstV.y = oN.z; vMask.y = 0; }
   // if (oS.x > 0) { pS = pC; obstV.y = oS.z; vMask.y = 0; }
   // if (oE.x > 0) { pE = pC; obstV.x = oE.y; vMask.x = 0; }
    //if (oW.x > 0) { pW = pC; obstV.x = oW.y; vMask.x = 0; }

    // Enforce the free-slip boundary condition:
    lowp vec2 oldV = texelFetch(Velocity, T, 0).xy;
    lowp vec2 grad =  vec2(pE - pW, pN - pS) * GradientScale;
    lowp vec2 newV = oldV - grad;

    lowp vec2 fin = (vMask * newV) + obstV;
    gl_FragColor = vec4(fin.x,fin.y,0,1);
}