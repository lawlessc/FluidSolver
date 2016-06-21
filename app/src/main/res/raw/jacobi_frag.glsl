precision lowp float;
uniform  sampler2D textureUnit0;//Pressure

uniform  sampler2D textureUnit1;//Divergence
//uniform sampler2D Obstacles;
uniform   float Alpha;
uniform   float InverseBeta;

varying  vec2 v_texCoord;

uniform  vec3 inversesize;

void main()
{
     vec2 T =  v_texCoord; // vec2(gl_FragCoord.xy);
     vec2 stepX = vec2(inversesize.x, 0);
     vec2 stepY = vec2(0, inversesize.y);



    // Find neighboring pressure:
//     vec4 pN = texture2D(textureUnit0, T +vec2(0, 1));
//
//     vec4 pS = texture2D(textureUnit0, T +vec2(0, -1));
//     vec4 pE = texture2D(textureUnit0, T +vec2(1, 0));
//     vec4 pW = texture2D(textureUnit0, T +vec2(-1, 0));

       vec4 pN = texture2D(textureUnit0, T +stepY);
       vec4 pS = texture2D(textureUnit0, T -stepY);
       vec4 pE = texture2D(textureUnit0, T +stepX);
       vec4 pW = texture2D(textureUnit0, T -stepX);



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

    vec4  bC = texture2D(textureUnit1,T);
    gl_FragColor = (pW + pE + pS + pN + Alpha * bC) * InverseBeta;

}