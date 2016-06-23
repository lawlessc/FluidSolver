precision lowp float;
uniform sampler2D textureUnit0;
varying vec2 v_texCoord;

void main() {
gl_FragColor = texture2D(textureUnit0, vec2(v_texCoord.x, v_texCoord.y));
//gl_FragColor = texture2D(textureUnit0, vec2(gl_FragCoord.x, gl_FragCoord.y));
}