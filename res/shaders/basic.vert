#version 460

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textures;

out vec2 tex_coords;

void main() {
	tex_coords = textures;
	gl_Position = view * camera * transform * vec4(position, 1.0);
}
