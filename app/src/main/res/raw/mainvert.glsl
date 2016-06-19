precision lowp float;
attribute  vec3 position;
varying   vec2 v_texCoord;
attribute vec2 texture0;
const  vec2 scale = vec2(0.5, 0.5);
//varying lowp vec2 inversesize;

void main() {
v_texCoord = position.xy * scale + scale;
//inversesize= scale;


gl_Position = vec4(position.xy,0.0,1.0);
}
