precision highp float;
attribute lowp vec3 position;
varying  lowp vec2 v_texCoord;
attribute vec2 texture0;
const lowp vec2 scale = vec2(0.5, 0.5);
//varying lowp vec2 inversesize;

void main() {
v_texCoord = position.xy * scale + scale;
//inversesize= scale;


gl_Position = vec4(position.xy,0.0,1.0);
}
