package com.blockycraft.blockyfix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class BlockyFix extends JavaPlugin {

    @Override
    public void onEnable() {
        setupConfig();
        
        File configFile = new File(getDataFolder(), "config.yml");
        Configuration config = new Configuration(configFile);
        config.load();

        Map<Integer, Set<Byte>> axeBlockRules = loadBlockRules(config, "tools.axes");
        Map<Integer, Set<Byte>> pickaxeBlockRules = loadBlockRules(config, "tools.pickaxes");
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListener(axeBlockRules, pickaxeBlockRules), this);
        
        System.out.println("[BlockyFix] Plugin ativado. Regras (ID:Data) carregadas.");
    }

    @Override
    public void onDisable() {
        System.out.println("[BlockyFix] Plugin desativado.");
    }

    private Map<Integer, Set<Byte>> loadBlockRules(Configuration config, String path) {
        Map<Integer, Set<Byte>> ruleMap = new HashMap<>();
        List<String> stringList = config.getStringList(path, null);

        if (stringList == null) { return ruleMap; }

        for (String entry : stringList) {
            try {
                String[] parts = entry.split(":");
                int id = Integer.parseInt(parts[0]);
                byte data = Byte.parseByte(parts[1]);

                if (!ruleMap.containsKey(id)) {
                    ruleMap.put(id, new HashSet<Byte>());
                }
                ruleMap.get(id).add(data);
            } catch (Exception e) {
                System.out.println("[BlockyFix] AVISO: Entrada invalida no config.yml: '" + entry + "'.");
            }
        }
        return ruleMap;
    }

    private void setupConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getDataFolder().mkdir();
            try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("config.yml");
                 OutputStream out = new FileOutputStream(configFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}