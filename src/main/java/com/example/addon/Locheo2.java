package com.example.mod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MyCustomCheatScreen extends Screen {
    protected MyCustomCheatScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Bật/Tắt Bay"),
            button -> {
                if (this.client != null && this.client.player != null) {
                    ExampleModClient.toggleFlight(this.client.player);
                }
            }
        ).dimensions(this.width / 2 - 100, this.height / 2 - 20, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
