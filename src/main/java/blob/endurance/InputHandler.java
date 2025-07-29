package blob.endurance;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import blob.endurance.entities.SeatEntity;

public class InputHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void updateRotation() {
        if (client.player == null) return;

        Entity vehicle = client.player.getVehicle();
        if (!(vehicle instanceof SeatEntity)) {
            return;
        }

        // Directly get pitch and yaw from client player, convert to radians
        float pitchRad = (float) Math.toRadians(-client.player.getPitch());
        float yawRad = (float) Math.toRadians(-client.player.getYaw());

        // Set shipRot x and y directly from player rotation
        Endurance.shipRot.x = pitchRad;
        Endurance.shipRot.y = yawRad;

        // Handle roll input
        long handle = client.getWindow().getHandle();
        boolean qDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_Z);
        boolean eDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_X);
        boolean yDown = InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_Y);

        float rollSpeed = 0.03f;
        if (qDown) Endurance.shipRot.z -= rollSpeed;
        if (eDown) Endurance.shipRot.z += rollSpeed;

        // Reset position and rotation on Y key press
        if (yDown) {
            Endurance.shipRot.set(0, 0, 0);
            Endurance.shipPos.set(0, 0, 0);
            Endurance.shipVel.set(0, 0, 0);
        }

        // Wrap roll angle between -PI and PI
        if (Endurance.shipRot.z > Math.PI) Endurance.shipRot.z -= 2 * Math.PI;
        if (Endurance.shipRot.z < -Math.PI) Endurance.shipRot.z += 2 * Math.PI;
    }
}