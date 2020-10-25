#version 410

uniform sampler2D tex;
uniform vec3 viewPos;
uniform vec3 lampPos;

layout (location = 0) in vec3 fragPos;
layout (location = 1) in vec3 normal;
layout (location = 2) centroid in vec2 tex_coords;

out vec4 fragColor;

const float ambientStrength = 2;
const float diffuseStrength = 3;
const float specularStrength = 3;

void main() {
    float unit = 1 / (ambientStrength + diffuseStrength + specularStrength);

    // Ambient
    float ambient = ambientStrength * unit;

    // Diffuse
    float diffuse = diffuseStrength * unit * ((dot(normal, vec3(0, -1, 0)) + 1) / 2);

    // Specular
    vec3 lightDir = normalize(lampPos - fragPos);
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = specularStrength * unit * pow(max(dot(viewDir, reflectDir), 0.0), 16);

    vec4 t = texture(tex, tex_coords);

    fragColor = vec4(t.rgb * (ambient + diffuse + specular), t.a);
}