#version 410 core

const int MAX_LIGHTS = %MAX_LIGHTS%;
const vec4 minVec4 = vec4(0, 0, 0, 0);
const vec4 maxVec4 = vec4(1, 1, 1, 1);

in vec2 fragTextureCoord;
in vec3 fragPos;
in vec3 fragNormal;
in vec3 camDirection;
in float vDepthVisibility;

out vec4 fragColor;

struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	int hasTexture;
	float shininess;
};

struct DirectionalLight {
	vec3 color;
	vec3 direction;
	float intensity;
};

struct Light {
	vec3 color;
	vec3 position;
	float intensity;
	float constant;
	float linear;
	float exponent;
	vec3 coneDirection;
	float cutOff;
	float isSpotLight;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform Light lights[MAX_LIGHTS];
uniform int ignoreLightIndex;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void initColor(Material material, vec2 textCoord) {
	if (material.hasTexture == 1) {
		ambientC = texture(textureSampler, textCoord);
		diffuseC = ambientC;
		specularC = ambientC;
	}
	else {
		ambientC = material.ambient;
		diffuseC = material.diffuse;
		specularC = material.specular;
	}
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
	vec4 diffuseColor = minVec4;
	vec4 specColor = minVec4;

	// Diffuse light
	//float diffuseFactor = clamp(dot(to_light_dir, normal), 0.0, 1.0);
	//float diffuseFactor = max(dot(to_light_dir, normal), 0);
	float diffuseFactor = dot(to_light_dir, normal);
	diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

	vec3 camera_direction = -camDirection;

	// Specular Color
	vec3 reflected_light = normalize(reflect(-to_light_dir, normal));
	float specularFactor = pow(max( dot(camera_direction, reflected_light), 0.0), specularPower * pow(vDepthVisibility, 1));
//	float specularFactor = pow(max( dot(camera_direction, reflected_light), 0.0), specularPower * pow(vDepthVisibility, 3));

	// Specular Color Phong; Gives more speckles.
// 	vec3 halfwayDir = normalize(to_light_dir + camera_direction);
// 	float specularFactor = pow(max(dot(normal, halfwayDir), 0.0), specularPower * pow(vDepthVisibility, 3));

	specColor = specularC * light_intensity * specularFactor * material.shininess * specularFactor * vec4(light_color, 1.0);

	return max(minVec4, min(diffuseColor, maxVec4)) + max(minVec4, min(specColor, maxVec4));
}

vec4 calcDirectionLight(DirectionalLight light, vec3 position, vec3 normal) {
	return calcLightColor(light.color, light.intensity, position, light.direction, normal);
}

vec4 calcLight(Light light, vec3 position, vec3 normal) {
	vec3 light_dir = light.position - position;
	vec3 to_light_dir = normalize(light_dir);
	float spot_alpha = dot(-to_light_dir, light.coneDirection);

	vec4 color;

	if (light.isSpotLight == 0 || spot_alpha >= light.cutOff) {
		// Attenuation
		float distance = length(light_dir);
		float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance;

		color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal) / attenuationInv;
		if (light.isSpotLight == 1) {
			color *= (1.0 - (1.0 - spot_alpha) / (1.0 - light.cutOff));
		}
	}
	else {
		color = minVec4;
	}

    return max(minVec4, min(color, maxVec4));
}

void main() {
	initColor(material, fragTextureCoord);

    vec3 fragN = normalize(fragNormal);

	vec4 diffuseSpecularComp = calcDirectionLight(directionalLight, fragPos, fragN);

	for (int i = 0; i < MAX_LIGHTS; i++) {
		if (i != ignoreLightIndex && lights[i].intensity > 0) {
			diffuseSpecularComp += calcLight(lights[i], fragPos, fragN);
		}
	}

	fragColor = max(minVec4, min((ambientC * vec4(ambientLight, 1) + max(minVec4, min(diffuseSpecularComp, maxVec4))) * vDepthVisibility, maxVec4));
}

