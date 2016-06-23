precision lowp float;
attribute  vec3 position;
varying   vec2 v_texCoord;
attribute vec2 texture0;
const  vec2 scale = vec2(0.5, 0.5);

varying  vec2 texelCoord;
varying  vec2 p;
uniform  float aspectRatio;

void main() {
texelCoord = position.xy;



	vec2 clipSpace = 2.0*texelCoord - 1.0;	//from 0->1 to -1, 1 (clip space)

	p = vec2(clipSpace.x * aspectRatio, clipSpace.y);


v_texCoord = position.xy * scale + scale;
gl_Position = vec4(position.xy,0.0,1.0);
}
