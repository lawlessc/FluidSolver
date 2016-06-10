uniform lowp sampler2D textureUnit0;//Pressure
uniform lowp sampler2D textureUnit1;//Divergence
//uniform sampler2D Obstacles;

uniform  lowp float Alpha;
uniform  lowp float InverseBeta;

void main()
{
    lowp vec2 T = vec2(gl_FragCoord.xy);

    // Find neighboring pressure:
    lowp vec4 pN = texture2D(textureUnit0, T +vec2(0, 1));
    lowp vec4 pS = texture2D(textureUnit0, T +vec2(0, -1));
    lowp vec4 pE = texture2D(textureUnit0, T +vec2(1, 0));
    lowp vec4 pW = texture2D(textureUnit0, T +vec2(-1, 0));
   // vec4 pC = texelFetch(Pressure, T, 0);

    // Find neighboring obstacles:
    //vec3 oN = texelFetchOffset(Obstacles, T, 0, ivec2(0, 1)).xyz;
    //vec3 oS = texelFetchOffset(Obstacles, T, 0, ivec2(0, -1)).xyz;
   //vec3 oE = texelFetchOffset(Obstacles, T, 0, ivec2(1, 0)).xyz;
    //vec3 oW = texelFetchOffset(Obstacles, T, 0, ivec2(-1, 0)).xyz;

    // Use center pressure for solid cells:
    //if (oN.x > 0) pN = pC;
    //if (oS.x > 0) pS = pC;
    //if (oE.x > 0) pE = pC;
   // if (oW.x > 0) pW = pC;

   lowp vec4  bC = texture2D(textureUnit1,T);
    gl_FragColor = (pW + pE + pS + pN + Alpha * bC) * InverseBeta;
}