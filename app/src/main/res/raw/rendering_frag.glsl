precision highp float;
varying vec2 v_texCoord;
uniform sampler2D textureUnit0;


void main() {

//
gl_FragColor = texture2D(textureUnit0, vec2(v_texCoord.x, v_texCoord.y));

//gl_FragColor = vec4(1,0,0,1);

}
