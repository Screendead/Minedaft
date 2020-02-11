#version 120

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

attribute vec3 position;
attribute vec3 normals;
attribute vec2 tex_coords;

varying vec3 norms;
varying vec2 tex_c;

void main() {
	norms = normals;
	tex_c = tex_coords;
	gl_Position = view * camera * transform * vec4(position, 1.0);
}
