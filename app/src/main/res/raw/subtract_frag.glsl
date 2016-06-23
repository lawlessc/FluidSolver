precision lowp float;
uniform  sampler2D textureUnit0;//Velocity
uniform  sampler2D textureUnit1;//Pressure
//uniform sampler2D Obstacles;
uniform  float GradientScale;

varying  vec2 v_texCoord;
uniform  vec3 inversesize;

void main()
{


     vec2 T = vec2(gl_FragCoord.xy);
     vec2 stepX = vec2(inversesize.x, 0);
     vec2 stepY = vec2(0, inversesize.y);
    //  vec2 T =  v_texCoord;


//    highp vec3 oC = texelFetch(Obstacles, T, 0).xyz;
//    if (oC.x > 0) {
//        FragColor = oC.yz;
//        return;
//    }

    // Find neighboring pressure:
//     float pN = texture2D(textureUnit1, T +vec2(0, 1)).r;
//     float pS = texture2D(textureUnit1, T+ vec2(0, -1)).r;
//     float pE = texture2D(textureUnit1, T+ vec2(1, 0)).r;
//     float pW = texture2D(textureUnit1, T+ vec2(-1, 0)).r;
      float pN = texture2D(textureUnit1, T+ stepY).r;
      float pS = texture2D(textureUnit1, T- stepY).r;
      float pE = texture2D(textureUnit1, T+ stepX).r;
      float pW = texture2D(textureUnit1, T- stepX).r;




    //highp float pC = texture2D(textureUnit1, T).r;

//    // Find neighboring obstacles:
//    vec3 oN = texelFetchOffset(Obstacles, T, 0, ivec2(0, 1)).xyz;
//    vec3 oS = texelFetchOffset(Obstacles, T, 0, ivec2(0, -1)).xyz;
//    vec3 oE = texelFetchOffset(Obstacles, T, 0, ivec2(1, 0)).xyz;
//    vec3 oW = texelFetchOffset(Obstacles, T, 0, ivec2(-1, 0)).xyz;

    // Use center pressure for solid cells:
   //  vec2 obstV = vec2(0);
   //  vec2 vMask = vec2(1);

   // if (oN.x > 0) { pN = pC; obstV.y = oN.z; vMask.y = 0; }
   // if (oS.x > 0) { pS = pC; obstV.y = oS.z; vMask.y = 0; }
   // if (oE.x > 0) { pE = pC; obstV.x = oE.y; vMask.x = 0; }
    //if (oW.x > 0) { pW = pC; obstV.x = oW.y; vMask.x = 0; }

    // Enforce the free-slip boundary condition:
     vec2 oldV = texture2D(textureUnit0, T).xy;
     vec2 grad =  vec2(pE - pW, pN - pS) * GradientScale;
     vec2 newV = oldV - grad;
    // vec2 fin = (vMask * newV) + obstV;
     vec2 fin = newV;

     gl_FragColor = vec4(fin.xy,0,1);
      // gl_FragColor = (vMask * newV) + obstV;
}