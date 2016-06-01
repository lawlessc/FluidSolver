out vec4 FragColor;



uniform sampler2D textureUnit0;//Veclocity

uniform sampler2D textureUnit1;//source texture
//uniform sampler2D textureUnit2;//Obstacle Texture

uniform vec2 InverseSize;
uniform float TimeStep;
uniform float Dissipation;

void main()
{
    vec2 fragCoord = gl_FragCoord.xy;
    //float solid = texture(textureUnit2, InverseSize * fragCoord).x;
    //if (solid > 0) {
     //   gl_FragColor = vec4(0);
    //}

    vec2 u = texture(textureUnit0, InverseSize * fragCoord).xy;
    vec2 coord = InverseSize * (fragCoord - TimeStep * u);
    gl_FragColor = Dissipation * texture(textureUnit1, coord);
}
