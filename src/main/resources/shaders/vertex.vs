#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec2 fragTextureCoord;

void main() {
    gl_Position = vec4(position, 1.0);
    fragTextureCoord = textureCoords;
}