#version 410

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 norms;
layout (location = 2) in vec2 textures;

layout (location = 0) out vec3 fragPos;
layout (location = 1) out vec3 normal;
layout (location = 2) centroid out vec2 tex_coords;

void main() {
	fragPos = position;
	normal = norms;
	tex_coords = textures;
	gl_Position = view * camera * transform * vec4(position, 1.0);
}