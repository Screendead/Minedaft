#version 460

uniform sampler2D tex;

in vec2 tex_coords;

out vec4 fragColor;

void main() {
	fragColor = texture(tex, tex_coords);
}
