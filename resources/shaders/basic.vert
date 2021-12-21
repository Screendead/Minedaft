#version 410

uniform mat4 view;
uniform mat4 transform;
uniform mat4 camera;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 norms;
layout (location = 2) in vec2 textures;
layout (location = 3) in float shadow;

layout (location = 0) out vec3 _position;
layout (location = 1) out vec3 _norms;
layout (location = 2) centroid out vec2 _textures;
layout (location = 3) out float _shadow;

//layout (location = 0) out vec3 fragPos;
//layout (location = 1) out vec3 normal;
//layout (location = 2) centroid out vec2 tex_coords;
//layout (location = 3) out float shadowLevel;

void main() {
	_position = position;
	_norms = norms;
	_textures = textures;
	_shadow = shadow;
	gl_Position = view * camera * transform * vec4(position, 1.0);
}