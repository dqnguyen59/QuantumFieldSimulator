#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec3 camDirection;
out float vDepth;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform float depthFading;

void main() {
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
	
	vDepthFading = max(depthFading, ((normalize(vec3(transformationMatrix * vec4(0.0, 0.0, 0.0, 1.0)) - position)).z + 1.0)) * 0.25 + 0.5;
	
	fragPos = vec3(transformationMatrix * vec4(position, 1.0));
	camDirection = normalize(vec3(viewMatrix * transformationMatrix * vec4(position, 1.0)));
	fragNormal = normalize(mat3(transformationMatrix) * normal);
	
	fragTextureCoord = textureCoord;
}
