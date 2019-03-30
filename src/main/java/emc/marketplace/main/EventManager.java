package emc.marketplace.main;

import emc.marketplace.gui.GuiSessionLoad;
import me.deftware.client.framework.event.EventHandler;
import me.deftware.client.framework.event.EventListener;
import me.deftware.client.framework.event.events.EventGuiScreenDraw;
import me.deftware.client.framework.wrappers.IMinecraft;
import me.deftware.client.framework.wrappers.gui.IGuiButton;

public class EventManager extends EventListener {

    private Main main;

    public EventManager(Main main) {
        this.main = main;
    }

    @EventHandler(eventType = EventGuiScreenDraw.class)
    private void drawButton(EventGuiScreenDraw event) {
        if (event.instanceOf(EventGuiScreenDraw.CommonScreenTypes.GuiIngameMenu)) {
            if (event.getIButtonList().isEmpty() || event.getIButtonList().size() == 1) {
                event.addButton(new IGuiButton(30, event.getWidth() / 2 - 100, event.getHeight() / 4 + 128,
                        "Addons Marketplace") {
                    @Override
                    public void onButtonClick(double mouseX, double mouseY) {
                        IMinecraft.setGuiScreen(new GuiSessionLoad(main, null));
                    }
                });
            }
        }
    }

}
