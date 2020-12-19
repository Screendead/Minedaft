#version 300 es

precision mediump float;

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

in vec3 position;
in vec3 norms;
in vec2 textures;

out vec3 fragPos;
out vec3 normal;
centroid out vec2 tex_coords;

void main() {
	fragPos = position;
	normal = norms;
	tex_coords = textures;
	gl_Position = view * camera * transform * vec4(position, 1.0f);
}