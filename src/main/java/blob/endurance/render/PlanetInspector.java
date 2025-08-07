package blob.endurance.render;

import blob.endurance.Endurance;
import foundry.veil.api.client.editor.Inspector;
import imgui.ImGui;
import net.minecraft.text.Text;
import org.joml.Vector3f;

public class PlanetInspector implements Inspector {
    public void render() {
        float[] sunPos = new float[]{Endurance.SUN_POS.x, Endurance.SUN_POS.y, Endurance.SUN_POS.z};
        ImGui.sliderFloat3("SunPos", sunPos, -1000, 1000);
        Endurance.SUN_POS = new Vector3f(sunPos);
    }

    public Text getDisplayName() {
        return Text.of("Endurance");
    }
}
