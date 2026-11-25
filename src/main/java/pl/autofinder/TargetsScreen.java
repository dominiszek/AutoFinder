package pl.autofinder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class TargetsScreen extends Screen {
    public TargetsScreen() {
        super(Component.literal("Anarchia AutoFinder"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0xB0000000);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 24, 0xFFFFFF);
        graphics.drawCenteredString(
                this.font,
                Component.literal("Wykryci gracze — /tpa co " +
                        AnarchiaAutoFinderClient.getIntervalSeconds() + " s"),
                this.width / 2, 42, 0xAAAAAA
        );

        Set<String> names = AnarchiaAutoFinderClient.getTargetsSnapshot();
        if (names.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.literal("Brak wykrytych graczy w zasięgu 64 bloków"),
                    this.width / 2, 70, 0xFFCC55);
        } else {
            int y = 66;
            for (String name : names) {
                graphics.drawCenteredString(this.font, Component.literal("• " + name),
                        this.width / 2, y, 0x55FF55);
                y += 14;
                if (y > this.height - 30) break;
            }
        }

        graphics.drawCenteredString(this.font,
                Component.literal("Naciśnij ESC lub 6, aby zamknąć"),
                this.width / 2, this.height - 20, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
