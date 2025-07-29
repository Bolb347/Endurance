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
uniform float AuraStength[PLANET_COUNT];
uniform float AuraFalloff[PLANET_COUNT];
uniform vec3 SunPos;
uniform vec3 ShipRot;

const float PI = 3.14159265;
const float EPSILON = 1e-4;

const vec3 RAYLEIGH_SCATTER = vec3(0.005, 0.015, 0.035);
const vec3 MIE_SCATTER = vec3(0.004);
const float MIE_G = 0.5;

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

float rayleighPhase(float cosTheta) {
    return (3.0 / (16.0 * PI)) * (1.0 + cosTheta * cosTheta);
}

float hgPhase(float cosTheta, float g) {
    float g2 = g * g;
    return (1.0 / (4.0 * PI)) * ((1.0 - g2) / pow(1.0 + g2 - 2.0 * g * cosTheta, 1.5));
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

// Rotation matrix with Y (yaw) * X (pitch) * Z (roll) order
mat3 rotationMatrixYXZ(vec3 rot) {
    float cx = cos(rot.x);
    float sx = sin(rot.x);
    float cy = cos(rot.y);
    float sy = sin(rot.y);
    float cz = cos(rot.z);
    float sz = sin(rot.z);

    mat3 rotX = mat3(
    1, 0, 0,
    0, cx, -sx,
    0, sx, cx
    );

    mat3 rotY = mat3(
    cy, 0, sy,
    0, 1, 0,
    -sy, 0, cy
    );

    mat3 rotZ = mat3(
    cz, -sz, 0,
    sz, cz, 0,
    0, 0, 1
    );

    return rotY * rotX * rotZ;
}

vec3 applyShipTransform(vec3 planetWorldPos, vec3 shipRot) {
    // Inverse rotation matrix is transpose since orthonormal
    mat3 rot = rotationMatrixYXZ(shipRot);
    mat3 invRot = transpose(rot);

    return invRot * planetWorldPos;
}

void main() {
    vec3 worldRayOrigin = VeilCamera.CameraPosition;
    vec3 worldRayDir = getWorldRay(texCoord);

    // Apply inverse ship transform to ray origin and direction
    mat3 rot = rotationMatrixYXZ(ShipRot);
    mat3 invRot = transpose(rot);

    vec3 rayOrigin = invRot * worldRayOrigin;
    vec3 rayDir = normalize(invRot * worldRayDir);

    vec4 bgColor = texture(DiffuseSampler0, texCoord);
    vec3 finalColor = bgColor.rgb;

    float closestT = 1e20;
    int closestPlanetIndex = -1;

    for (int i = 0; i < PLANET_COUNT; ++i) {
        vec3 transformedPlanetPos = applyShipTransform(WorldPos[i], ShipRot);
        float tNear, tFar;
        if (raySphereIntersect(rayOrigin, rayDir, transformedPlanetPos, Radius[i], tNear, tFar)) {
            if (tNear < closestT) {
                closestT = tNear;
                closestPlanetIndex = i;
            }
        }
    }

    if (closestPlanetIndex != -1) {
        vec3 planetWorldPos = WorldPos[closestPlanetIndex];
        float radius = Radius[closestPlanetIndex];
        float rotation = Rotation[closestPlanetIndex];

        vec3 transformedPlanetPos = applyShipTransform(planetWorldPos, ShipRot);
        vec3 hitPoint = rayOrigin + rayDir * closestT;
        vec3 normalLocal = normalize(hitPoint - transformedPlanetPos);

        // Normal back to world space
        vec3 normalWorld = rot * normalLocal;

        // Rotate normal by planet rotation around Y axis
        float cosR = cos(rotation);
        float sinR = sin(rotation);

        vec3 rotatedNormal = vec3(
        cosR * normalWorld.x + sinR * normalWorld.z,
        normalWorld.y,
        -sinR * normalWorld.x + cosR * normalWorld.z
        );

        // Spherical UV mapping
        float u = 0.5 + atan(rotatedNormal.x, rotatedNormal.z) / (2.0 * PI);
        float v = 0.5 - asin(clamp(rotatedNormal.y, -1.0, 1.0)) / PI;
        v = clamp(v, 0.0, 1.0);

        vec2 sphereUV = vec2(fract(u), v);

        finalColor = samplePlanetTexture(closestPlanetIndex, sphereUV);
    } else {
        vec3 accumulatedGlow = vec3(0.0);

        for (int i = 0; i < PLANET_COUNT; ++i) {
            vec3 transformedPlanetPos = applyShipTransform(WorldPos[i], ShipRot);
            vec3 planetCenter = transformedPlanetPos;
            float radius = Radius[i];

            vec3 toCenter = planetCenter - rayOrigin;
            float t = dot(toCenter, rayDir);
            if (t <= 0.0) continue;

            vec3 closestPoint = rayOrigin + rayDir * t;
            float distToCenter = length(planetCenter - closestPoint);
            float distToSurface = max(0.0, distToCenter - radius);

            float glowStrength = exp(-AuraFalloff[i] * distToSurface) * AuraStength[i];

            vec3 lightDir = normalize(SunPos - planetCenter);
            float mu = dot(rayDir, lightDir);
            float muClamped = clamp(mu, -1.0, 1.0);

            float phaseR = rayleighPhase(muClamped);
            float phaseM = hgPhase(muClamped, MIE_G);

            vec3 rayleighColor = vec3(0.06, 0.12, 0.35);
            vec3 mieColor = vec3(0.25, 0.08, 0.02);

            float redBoost = smoothstep(-0.2, 0.1, muClamped);
            mieColor *= mix(0.3, 1.0, redBoost);

            vec3 scatterColor = (rayleighColor * phaseR + mieColor * phaseM);

            accumulatedGlow += glowStrength * scatterColor;
        }

        accumulatedGlow *= 25.0;
        finalColor += clamp(accumulatedGlow, vec3(0.0), vec3(1.0));
    }

    fragColor = vec4(finalColor, 1.0);
}
