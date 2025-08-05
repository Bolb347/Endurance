package blob.endurance.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Optional;

public class AtmoColor {
    public static final int PLANET_COUNT = 10;
    private final NativeImage[] planetImages = new NativeImage[PLANET_COUNT];
    public final Vector3f[] atmoColors = new Vector3f[PLANET_COUNT];

    public AtmoColor() {
        loadPlanetTextures();
        computeAtmoColors();
    }

    private void loadPlanetTextures() {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        for (int i = 0; i < PLANET_COUNT; i++) {
            Identifier loc = Identifier.of("endurance", "textures/planets/" + planetName(i) + ".png");
            Optional<Resource> optionalResource = resourceManager.getResource(loc);
            if (optionalResource.isPresent()) {
                Resource res = optionalResource.get();
                try {
                    planetImages[i] = NativeImage.read(res.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    planetImages[i] = createFallbackImage();
                }
            } else {
                System.err.println("Planet texture not found: " + loc);
                planetImages[i] = createFallbackImage();
            }
        }
    }

    private NativeImage createFallbackImage() {
        NativeImage fallback = new NativeImage(1, 1, true);
        fallback.setColor(0, 0, 0xFF000000); // Black opaque
        return fallback;
    }


    private String planetName(int i) {
        return switch (i) {
            case 0 -> "sun";
            case 1 -> "mercury";
            case 2 -> "venus";
            case 3 -> "earth";
            case 4 -> "mars";
            case 5 -> "jupiter";
            case 6 -> "saturn";
            case 7 -> "uranus";
            case 8 -> "neptune";
            case 9 -> "pluto";
            default -> "unknown";
        };
    }

    private Vector3f samplePlanetTextureCPU(int planetIndex, float u, float v) {
        NativeImage img = planetImages[planetIndex];
        int x = Math.min((int)(u * (img.getWidth() - 1)), img.getWidth() - 1);
        int y = Math.min((int)(v * (img.getHeight() - 1)), img.getHeight() - 1);

        int rgba = img.getColor(x, y);

        // NativeImage returns ABGR packed int; convert to RGBA floats:
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float b = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float r = (rgba & 0xFF) / 255f;

        // Optionally multiply by alpha if needed
        return new Vector3f(r * a, g * a, b * a);
    }

    private Vector3f mix(Vector3f a, Vector3f b, float t) {
        return new Vector3f(
                a.x() * (1 - t) + b.x() * t,
                a.y() * (1 - t) + b.y() * t,
                a.z() * (1 - t) + b.z() * t
        );
    }

    public void computeAtmoColors() {
        for (int i = 0; i < PLANET_COUNT; i++) {
            Vector3f color = new Vector3f(0f, 0f, 0.25f);
            for (float x = 0; x <= 1; x += 0.25f) {
                for (float y = 0; y <= 1; y += 0.25f) {
                    Vector3f sample = samplePlanetTextureCPU(i, x, y);
                    color = mix(color, sample, 0.02f);
                }
            }
            atmoColors[i] = color;
        }
    }

    public Vector3f getAtmoColor(int planetIndex) {
        if (planetIndex < 0 || planetIndex >= PLANET_COUNT) return new Vector3f(0,0,0);
        return atmoColors[planetIndex];
    }

    public Vector3f[] getAtmoColors() {
        return atmoColors;
    }
}
