precision lowp float;
uniform  vec3 Point;
uniform  float Radius;
uniform  vec3 FillColor;
uniform sampler2D textureUnit0;//PREVIOUS VELOCITY

varying  vec2 v_texCoord;

void main()
{

     float d = distance(Point.xy, gl_FragCoord.xy);
    if (d < Radius) {
        float a = (Radius - d) * 0.5;
        a = min(a, 1.0);
        //gl_FragColor = vec4(FillColor, a);
           gl_FragColor = vec4(Point.xy- gl_FragCoord.xy,0, a);
    } else {

       gl_FragColor = texture2D(textureUnit0,v_texCoord);

    }
}
