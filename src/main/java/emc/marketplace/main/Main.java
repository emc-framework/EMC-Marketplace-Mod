package emc.marketplace.main;

import emc.marketplace.gui.GuiSessionLoad;
import me.deftware.client.framework.main.EMCMod;
import me.deftware.client.framework.wrappers.IMinecraft;
import me.deftware.client.framework.wrappers.gui.IGuiScreen;

public class Main extends EMCMod {

    @Override
    public void initialize() {
        new EventManager(this);
    }

    @Override
    public void callMethod(String method, String caller, Object parent) {
        if (method.equals("openGUI()")) {
            IMinecraft.setGuiScreen(new GuiSessionLoad(this, (IGuiScreen) parent));
        }
    }

    @Override
    public void callMethod(String method, String caller) {
        if (method.equals("openGUI()")) {
            IMinecraft.setGuiScreen(new GuiSessionLoad(this, null));
        }
    }

}
