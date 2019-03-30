package emc.marketplace.modinstaller;

import lombok.Getter;
import lombok.Setter;
import me.deftware.client.framework.main.Bootstrap;
import me.deftware.client.framework.main.EMCMod;
import me.deftware.client.framework.utils.OSUtils;
import me.deftware.client.framework.wrappers.IMinecraft;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * The mod class which contains all info about a mod
 *
 * @author Deftware
 */
public class Mod {

    @Getter
    String id, name, author, description, version;

    @Getter
    int price;

    @Getter
    @Setter
    private boolean hasPaid = false;

    @Getter
    private File modFile = null, deleted = null, update = null;

    private boolean updated = false;

    private EMCMod mod = null;

    public void init() {
        try {
            String EMCDir = OSUtils.getMCDir() + "libraries" + File.separator + "EMC" + File.separator + IMinecraft.getMinecraftVersion() + File.separator;
            modFile = new File(EMCDir + id + ".jar");
            deleted = new File(EMCDir + id + ".jar.delete");
            update = new File(EMCDir + id + "_update.jar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getVersionAsInt() {
        return Integer.valueOf(version);
    }

    public boolean hasUpdate() {
       try {
           if (!isInstalled() || updated) {
               return false;
           }
           for (EMCMod mod : Bootstrap.getMods().values()) {
               if (mod.modInfo.get("name").getAsString().equalsIgnoreCase(name)) {
                   if (mod.modInfo.get("version").getAsInt() < getVersionAsInt()) {
                       return true;
                   }
               }
           }
           return false;
       } catch (Exception ex) {
           return false;
       }
    }

    public boolean isInstalled() {
        return modFile == null || deleted == null ? false : modFile.exists() && !deleted.exists();
    }

    public void update(InstallCallback cb) {
        new Thread(() -> {
            updated = true;
            try {
                Web.downloadMod(API.endpoint + API.Types.Download.url, update.getAbsolutePath(), API.sessionID, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            cb.callback();
        }).start();
    }

    public void install(InstallCallback cb) {
        new Thread(() -> {
            if (deleted.exists() && modFile.exists()) {
                deleted.delete();
                if (!Bootstrap.getMods().containsKey(id) && mod != null) {
                    Bootstrap.getMods().put(id, mod);
                    mod = null;
                }
                cb.callback();
                return;
            }
            if (isInstalled()) {
                return;
            }
            try {
                Web.downloadMod(API.endpoint + API.Types.Download.url, modFile.getAbsolutePath(), API.sessionID, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (modFile.exists()) {
                try {
                    Bootstrap.loadMod(modFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cb.callback();
        }).start();
    }

    public void uninstall() {
        new Thread(() -> {
            if (!isInstalled()) {
                return;
            }
            try {
                FileUtils.writeStringToFile(deleted, "Delete mod", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Bootstrap.getMods().containsKey(id)) {
                mod = Bootstrap.getMods().get(id);
                Bootstrap.getMods().remove(id);
            }
        }).start();
    }

    @FunctionalInterface
    public interface InstallCallback {

        void callback();

    }

}
