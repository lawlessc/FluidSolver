
uniform lowp vec3 Point;
uniform lowp float Radius;
uniform lowp vec3 FillColor;

void main()
{
    lowp float d = distance(Point.xy, gl_FragCoord.xy);
    if (d < Radius) {
       lowp float a = (Radius - d) * 0.5;
        a = min(a, 1.0);
        gl_FragColor = vec4(FillColor, a);
    } else {
       // gl_FragColor = vec4(0);
        lowp float a = (Radius - d) * 0.5;
                 a = min(a, 1.0);
                 gl_FragColor = vec4(FillColor, a);
    }
}
