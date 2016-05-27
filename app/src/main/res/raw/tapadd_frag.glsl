precision highp float;
varying vec2 v_texCoord;
uniform sampler2D textureUnit0;
uniform sampler2D textureUnit1;

uniform vec3 tapCoord;


void main() {


gl_FragColor = texture2D(textureUnit0, vec2(v_texCoord.x, v_texCoord.y));

}
