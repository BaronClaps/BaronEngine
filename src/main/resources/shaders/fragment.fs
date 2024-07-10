#version 400 core

in vec2 fragTextureCoord;

out vec4 fragColor;

uniform sampler2d textureSampler;

void main() {
    fragColor = texture(textureSampler, fragTextureCoord);
}