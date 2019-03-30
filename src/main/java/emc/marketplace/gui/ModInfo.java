package emc.marketplace.gui;

import java.util.ArrayList;

import emc.marketplace.main.Main;
import emc.marketplace.modinstaller.Mod;
import lombok.Getter;
import me.deftware.client.framework.wrappers.IMinecraft;
import me.deftware.client.framework.wrappers.gui.IGuiButton;
import me.deftware.client.framework.wrappers.gui.IGuiScreen;
import me.deftware.client.framework.wrappers.render.IFontRenderer;
import org.lwjgl.glfw.GLFW;

/**
 * Gui for showing more info about a mod
 * 
 * @author Deftware
 *
 */
public class ModInfo extends IGuiScreen {

	@Getter
	private Mod mod;
	private Main main;

	public ModInfo(Mod mod, Main main, IGuiScreen parent) {
		super(parent);
		this.mod = mod;
		this.main = main;
	}

	@Override
	protected void onInitGui() {
		this.clearButtons();
		this.addButton(new IGuiButton(0, getIGuiScreenWidth() / 2 - 100, getIGuiScreenHeight() - 28, 98, 20,
				mod.isInstalled() ? "Uninstall" : (mod.getPrice() == 0 || mod.isHasPaid()) ? "Install" : "Buy") {
			@Override
			public void onButtonClick(double v, double v1) {
				if (mod.isInstalled()) {
					mod.uninstall();
				} else {
					if (mod.getPrice() == 0 || mod.isHasPaid()) {
						getIButtonList().get(0).setButtonText("Installing mod...");
						mod.install(() -> {
							getIButtonList().get(0).setButtonText("Uninstall");
						});
					} else {
						//IGuiScreen.openLink(ModList.frontend + "product/" + mod.getName());
					}
				}
			}
		});
		this.addButton(new IGuiButton(1, getIGuiScreenWidth() / 2 + 2, getIGuiScreenHeight() - 28, 98, 20, "Back") {
			@Override
			public void onButtonClick(double v, double v1) {
				IMinecraft.setGuiScreen(new ModList(main, parent));
			}
		});
	}

	@Override
	protected void onDraw(int mouseX, int mouseY, float partialTicks) {
		this.drawITintBackground(0);
		IFontRenderer.drawCenteredString(mod.getName(), getIGuiScreenWidth() / 2, 8, 16777215);
		IFontRenderer.drawCenteredString("Developed by: " + mod.getAuthor(), getIGuiScreenWidth() / 2, 20, 16777215);
		IFontRenderer.drawCenteredString("Mod description:", getIGuiScreenWidth() / 2, 60, 16777215);

		int y = 70;
		String desc = mod.getDescription();

		if (IFontRenderer.getStringWidth(desc) > this.getIGuiScreenWidth()) {
			ArrayList<String> lines = new ArrayList<String>();
			String current = "";
			for (String c : desc.split("")) {
				if (IFontRenderer.getStringWidth(current + c) > this.getIGuiScreenWidth()) {
					lines.add(current + "-");
					current = c;
				} else {
					current += c;
				}
			}
			lines.add(current);
			for (String string : lines) {
				IFontRenderer.drawCenteredString(string, getIGuiScreenWidth() / 2, y, 16777215);
				y += IFontRenderer.getFontHeight() + 2;
			}
		} else {
			IFontRenderer.drawCenteredString(desc, getIGuiScreenWidth() / 2, y, 16777215);
		}

	}

	@Override
	protected void onUpdate() {

	}

	@Override
	protected void onKeyPressed(int keyCode, int action, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			IMinecraft.setGuiScreen(null);
		}
	}

	@Override
	protected void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

	}

	@Override
	protected void onMouseClicked(int mouseX, int mouseY, int mouseButton) {

	}

	@Override
	protected void onGuiResize(int w, int h) {

	}

}
