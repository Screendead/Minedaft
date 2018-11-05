#version 460

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 norms;
layout (location = 2) in vec2 textures;

layout (location = 0) out vec3 normal;
layout (location = 1) out vec2 tex_coords;

void main() {
	// normal = (view * camera * transform * vec4(norms, 0.0)).xyz;
	normal = norms;
	tex_coords = textures;
	gl_Position = view * camera * transform * vec4(position, 1.0);
}