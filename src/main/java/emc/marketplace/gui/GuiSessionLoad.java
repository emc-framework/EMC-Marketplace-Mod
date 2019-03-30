package emc.marketplace.gui;

import emc.marketplace.main.Main;
import emc.marketplace.modinstaller.API;
import me.deftware.client.framework.apis.oauth.OAuth;
import me.deftware.client.framework.wrappers.IMinecraft;
import me.deftware.client.framework.wrappers.gui.IGuiScreen;
import me.deftware.client.framework.wrappers.render.IFontRenderer;

public class GuiSessionLoad extends IGuiScreen {

	private Main main;
	private String status = "Validating session...";
	private boolean ready = false;

	public GuiSessionLoad(Main main, IGuiScreen parent) {
		super(parent);
		this.main = main;
	}

	@Override
	protected void onInitGui() {
		new Thread(() -> {
			try {
				if (API.validSession()) {
					ready = true;
				} else {
					status = "Authenticating with oAuth...";
					OAuth.oAuth((success, code, time) -> {
						if (!success) {
							status = "Session authentication failed, please try again x3";
							API.sessionID = "";
						} else {
							status = "Authenticating with the marketplace...";
							try {
								if (API.updateSession(code)) {
									ready = true;
								} else {
									throw new Exception("Failed to get session id");
								}
							} catch (Exception ex) {
								status = "Session authentication failed, please try again x1";
								API.sessionID = "";
								ex.printStackTrace();
							}
						}
					});
				}
			} catch (Exception ex) {
				status = "Session authentication failed, please try again x2";
				API.sessionID = "";
				ex.printStackTrace();
			}
		}).start();
	}

	@Override
	protected void onDraw(int mouseX, int mouseY, float partialTicks) {
		this.drawIDefaultBackground();
		IFontRenderer.drawCenteredString(status, this.getIGuiScreenWidth() / 2, this.getIGuiScreenHeight() / 2, 0xFFFFFF);
		if (ready) {
			IMinecraft.setGuiScreen(new ModList(main, parent));
		}
	}

	@Override
	protected void onUpdate() {

	}

	@Override
	protected void onKeyPressed(int keyCode, int action, int modifiers) {

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
