#version 120

uniform sampler2D tex;

varying vec3 norms;
varying vec2 tex_c;

const float ambient = 0.3;
float diffuse;
vec4 color;

void main() {
    diffuse = max(ambient,
    (dot(norms, vec3(0, -1, 0)) * 2 +
    abs(dot(norms, vec3(1, 0, 0))) +
    abs(dot(norms, vec3(0, 0, 1))) + 1) / 5);

    color = texture2D(tex, tex_c);

    gl_FragColor = vec4(color.rgb * diffuse, color.a);
}