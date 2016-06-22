precision lowp float;
uniform sampler2D textureUnit0;
varying vec2 v_texCoord;

void main() {
gl_FragColor = texture2D(textureUnit0, vec2(v_texCoord.x, v_texCoord.y));


//vec2  ren = texture2D(textureUnit0, vec2(v_texCoord.x, v_texCoord.y)).xy;
//
//float k = (ren.x+ren.y)/2.0;

//gl_FragColor = vec4(k,k,0,1);

}