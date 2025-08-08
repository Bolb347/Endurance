package blob.endurance;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import blob.endurance.entities.ControllerEntity;

public class InputHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void updateRotation() {
        if (client.player == null) return;

        Entity vehicle = client.player.getVehicle();
        if (!(vehicle instanceof ControllerEntity)) {
            return;
        }

        // Directly get pitch and yaw from client player, convert to radians
        float pitchRad = (float) Math.toRadians(-client.player.getPitch());
        float yawRad = (float) Math.toRadians(-client.player.getYaw());

        // Set shipRot x and y directly from player rotation

        // Handle roll input
        long handle = client.getWindow().getHandle();
        boolean qDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_Z);
        boolean eDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_X);
        boolean yDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_Y);
    }
}