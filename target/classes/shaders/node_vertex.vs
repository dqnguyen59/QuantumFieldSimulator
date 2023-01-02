#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec3 camDirection;
out float vDepthVisibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform float depthVisibility;

void main() {
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
	
	fragPos = vec3(transformationMatrix * vec4(position, 1.0));
	vec3 camPosition = vec3(viewMatrix * transformationMatrix * vec4(0, 0, 0, 1.0));
	camDirection = normalize(camPosition);
	fragNormal = normalize(mat3(transformationMatrix) * normal);
	
	vDepthVisibility = 1 - max(0, (-camPosition * depthVisibility).z);
	
	fragTextureCoord = textureCoord;
}
