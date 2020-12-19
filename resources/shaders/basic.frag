#version 300 es

precision mediump float;

uniform sampler2D tex;
//uniform vec3 viewPos;
//uniform vec3 lampPos;

in vec3 fragPos;
in vec3 normal;
centroid in vec2 tex_coords;

out vec4 fragColor;

const float ambientStrength = 2.0f;
const float diffuseStrength = 3.0f;
const float specularStrength = 3.0f;

void main() {
//    float unit = 1 / (ambientStrength + diffuseStrength + specularStrength);
    float unit = 1.0f / (ambientStrength + diffuseStrength);

    // Ambient
    float ambient = ambientStrength * unit;

    // Diffuse
    float diffuse = diffuseStrength * unit * ((dot(normal, vec3(0.0f, -1.0f, 0.0f)) + 1.0f) / 2.0f);

    // Specular
//    vec3 lightDir = normalize(lampPos - fragPos);
//    vec3 viewDir = normalize(viewPos - fragPos);
//    vec3 reflectDir = reflect(-lightDir, normal);
//    float specular = specularStrength * unit * pow(max(dot(viewDir, reflectDir), 0.0), 16);

    vec4 t = texture(tex, tex_coords);

//    fragColor = vec4(t.rgb * (ambient + diffuse + specular), t.a);
    fragColor = vec4(t.rgb * (ambient + diffuse), t.a);
}