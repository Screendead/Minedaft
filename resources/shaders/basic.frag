#version 460

uniform sampler2D tex;

layout (location = 0) in vec3 normal;
layout (location = 1) centroid in vec2 tex_coords;

out vec4 fragColor;

const float ambient = 0.3;

void main() {
	float diffuse = max(ambient,
			(dot(normal, vec3(0, -1, 0)) * 2 +
			abs(dot(normal, vec3(1, 0, 0))) +
			abs(dot(normal, vec3(0, 0, 1))) + 1) / 5);

	vec4 t = texture(tex, tex_coords);

	fragColor = vec4(t.rgb * diffuse, t.a);
	// fragColor = texture(tex, tex_coords);
}
