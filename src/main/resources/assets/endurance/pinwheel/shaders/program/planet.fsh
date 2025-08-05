#version 150

uniform sampler2D DiffuseSampler0;

in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform CameraMatrices {
    mat4 ProjMat;
    mat4 IProjMat;
    mat4 ViewMat;
    mat4 IViewMat;
    mat3 IViewRotMat;
    vec3 CameraPosition;
    float NearPlane;
    float FarPlane;
} VeilCamera;

#define PI 3.14159265

uniform sampler2D PlanetTexture0;
uniform sampler2D PlanetTexture1;
uniform sampler2D PlanetTexture2;
uniform sampler2D PlanetTexture3;
uniform sampler2D PlanetTexture4;
uniform sampler2D PlanetTexture5;
uniform sampler2D PlanetTexture6;
uniform sampler2D PlanetTexture7;
uniform sampler2D PlanetTexture8;
uniform sampler2D PlanetTexture9;

#define PLANET_COUNT 10

uniform vec3 WorldPos[PLANET_COUNT];
uniform float Radius[PLANET_COUNT];
uniform float Rotation[PLANET_COUNT];
uniform float AuraStrength[PLANET_COUNT];  // Glow intensity multiplier
uniform float AuraFalloff[PLANET_COUNT];   // How quickly aura fades
uniform vec3 AtmoColors[PLANET_COUNT];
uniform vec3 SunPos;

const float EPSILON = 1e-4;

// Ray-sphere intersection
bool raySphereIntersect(vec3 rayOrigin, vec3 rayDir, vec3 center, float radius, out float tNear, out float tFar) {
    vec3 oc = rayOrigin - center;
    float b = dot(oc, rayDir);
    float c = dot(oc, oc) - radius * radius;
    float discriminant = b * b - c;
    if (discriminant < 0.0) return false;
    float sqrtDisc = sqrt(discriminant);
    tNear = -b - sqrtDisc;
    tFar = -b + sqrtDisc;
    if (tFar < EPSILON) return false;
    if (tNear < EPSILON) tNear = 0.0;
    return true;
}

vec3 getWorldRay(vec2 uv) {
    vec2 ndc = uv * 2.0 - 1.0;
    vec4 clip = vec4(ndc, -1.0, 1.0);
    vec4 eye = VeilCamera.IProjMat * clip;
    eye = vec4(eye.xy, -1.0, 0.0);
    vec3 worldDir = (VeilCamera.IViewMat * eye).xyz;
    return normalize(worldDir);
}

vec3 samplePlanetTexture(int index, vec2 uv) {
    if (index == 0) return texture(PlanetTexture0, uv).rgb;
    else if (index == 1) return texture(PlanetTexture1, uv).rgb;
    else if (index == 2) return texture(PlanetTexture2, uv).rgb;
    else if (index == 3) return texture(PlanetTexture3, uv).rgb;
    else if (index == 4) return texture(PlanetTexture4, uv).rgb;
    else if (index == 5) return texture(PlanetTexture5, uv).rgb;
    else if (index == 6) return texture(PlanetTexture6, uv).rgb;
    else if (index == 7) return texture(PlanetTexture7, uv).rgb;
    else if (index == 8) return texture(PlanetTexture8, uv).rgb;
    else if (index == 9) return texture(PlanetTexture9, uv).rgb;
    else return vec3(0.0);
}
// Rayleigh phase function
float rayleighPhase(float cosTheta) {
    return 0.75 * (1.0 + cosTheta * cosTheta);
}

// Henyey-Greenstein phase function for Mie scattering
float miePhase(float cosTheta, float g) {
    float g2 = g * g;
    float denom = pow(1.0 + g2 - 2.0 * g * cosTheta, 1.5);
    return (1.0 - g2) / (4.0 * PI * denom);
}

void main() {
    vec3 rayOrigin = VeilCamera.CameraPosition;
    vec3 rayDir = getWorldRay(texCoord);

    vec4 bgColor = texture(DiffuseSampler0, texCoord);
    vec3 finalColor = bgColor.rgb;

    float closestT = 1e20;
    int closestPlanetIndex = -1;

    for (int i = 0; i < PLANET_COUNT; ++i) {
        float tNear, tFar;
        if (raySphereIntersect(rayOrigin, rayDir, WorldPos[i], Radius[i], tNear, tFar)) {
            if (tNear < closestT) {
                closestT = tNear;
                closestPlanetIndex = i;
            }
        }
    }
    if (closestPlanetIndex != -1) {
        vec3 planetCenter = WorldPos[closestPlanetIndex];
        float radius = Radius[closestPlanetIndex];
        float rotation = Rotation[closestPlanetIndex];

        vec3 hitPoint = rayOrigin + rayDir * closestT;
        vec3 normalWorld = normalize(hitPoint - planetCenter);

        float cosR = cos(rotation);
        float sinR = sin(rotation);
        vec3 rotatedNormal = vec3(
            cosR * normalWorld.x + sinR * normalWorld.z,
            normalWorld.y,
            -sinR * normalWorld.x + cosR * normalWorld.z
        );

        float u = 0.5 + atan(rotatedNormal.x, rotatedNormal.z) / (2.0 * PI);
        float v = 0.5 - asin(clamp(rotatedNormal.y, -1.0, 1.0)) / PI;
        v = clamp(v, 0.0, 1.0);
        vec2 sphereUV = vec2(fract(u), v);

        finalColor = samplePlanetTexture(closestPlanetIndex, sphereUV);
    }

    // Atmosphere glow
    // Find closest planet intersection tNear
    float closestPlanetT = 1e20;
    for (int i = 0; i < PLANET_COUNT; ++i) {
        float tNear, tFar;
        if (raySphereIntersect(rayOrigin, rayDir, WorldPos[i], Radius[i], tNear, tFar)) {
            if (tNear > 0.0 && tNear < closestPlanetT) {
                closestPlanetT = tNear;
            }
        }
    }

    vec3 sunCenter = WorldPos[0];
    float sunRadius = Radius[0];
    float glowRadius = sunRadius * 1.2;

    vec3 toSun = sunCenter - rayOrigin;
    float tClosestSun = dot(toSun, rayDir);

    if (tClosestSun > 0.0) {
        vec3 closestPointSun = rayOrigin + rayDir * tClosestSun;
        float distToSun = length(closestPointSun - sunCenter);

        // Only add glow if the glow point is before any planet intersection (not occluded)
        if (distToSun < glowRadius && distToSun > sunRadius && tClosestSun < closestPlanetT) {
            float glowFactor = 1.0 - smoothstep(sunRadius, glowRadius, distToSun);
            glowFactor = pow(glowFactor, 2.0);

            vec3 glowColor = vec3(1.0, 0.8, 0.4);

            finalColor += glowColor * glowFactor * 2.0;
        }
    }

    for (int i = 1; i < PLANET_COUNT; ++i) {
        vec3 planetCenter = WorldPos[i];
        float radius = Radius[i];
        float auraStrength = AuraStrength[i];
        float auraFalloff = AuraFalloff[i];

        vec3 toCenter = planetCenter - rayOrigin;
        float tClosest = dot(toCenter, rayDir);

        if (tClosest < 0.0) continue;

        vec3 closestPoint = rayOrigin + rayDir * tClosest;
        float distToCenter = length(closestPoint - planetCenter);
        float atmosphereRadius = radius * 1.1;

        if (distToCenter > radius && distToCenter < atmosphereRadius) {

            // Occlusion check
            bool occluded = false;
            for (int j = 0; j < PLANET_COUNT; ++j) {
                if (j == i) continue;
                float tNearJ, tFarJ;
                if (raySphereIntersect(rayOrigin, rayDir, WorldPos[j], Radius[j], tNearJ, tFarJ)) {
                    if (tNearJ > 0.0 && tNearJ < tClosest) {
                        occluded = true;
                        break;
                    }
                }
            }
            if (occluded) continue;

            float glowFactor = 1.0 - smoothstep(radius, atmosphereRadius, distToCenter);
            glowFactor = pow(glowFactor, auraFalloff);

            vec3 sunDir = normalize(SunPos - planetCenter);
            vec3 viewDir = normalize(rayOrigin - planetCenter);
            float cosTheta = dot(-viewDir, sunDir);

            // Rayleigh scattering constants (blueish)
            vec3 rayleighCoeff = AtmoColors[i];
            float rPhase = rayleighPhase(cosTheta);

            // Mie scattering constants (more warmish)
            float g = 0.76;
            float mPhase = miePhase(cosTheta, g);
            vec3 mieCoeff = vec3(0.2, 0, 0);

            vec3 scatter = auraStrength * glowFactor * (rayleighCoeff * rPhase + mieCoeff * mPhase);

            // Scale down so it never blows out white
            vec3 glow = clamp(scatter * 5, vec3(0.0), vec3(1.0));

            finalColor += glow;
        }
    }

    fragColor = vec4(finalColor, 1.0);
}

