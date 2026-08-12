package com.doomclient;
import com.doomclient.gui.HackGuiScreen;
import com.doomclient.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DoomClientMod implements ClientModInitializer {
    
    public static final String MOD_ID = "doomclient";
    public static final String MOD_NAME = "§cD§4o§co§4m§cC§4l§ci§4e§cn§4t";
    public static final String VERSION = "3.5.0-FULL";
    
    public static DoomClientMod INSTANCE;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    
    public List<Module> modules = new CopyOnWriteArrayList<>();
    
    // Keybinds
    private static KeyBinding guiKey;
    private static KeyBinding maceDamageKey;
    private static KeyBinding killAuraKey;
    private static KeyBinding flyKey;
    private static KeyBinding speedKey;
    private static KeyBinding xrayKey;
    private static KeyBinding scaffoldKey;
    private static KeyBinding espKey;
    private static KeyBinding chestEspKey;
    private static KeyBinding autoTotemKey;
    private static KeyBinding noFallKey;
    private static KeyBinding fullBrightKey;
    private static KeyBinding jesusKey;
    private static KeyBinding noClipKey;
    private static KeyBinding autoSprintKey;
    private static KeyBinding freecamKey;
    private static KeyBinding autoFishKey;
    private static KeyBinding spearDamageKey;
    private static KeyBinding mlsacBypassKey;
    private static KeyBinding mlsacFastProKey;
    private static KeyBinding acBypassKey;
    private static KeyBinding stepKey;
    private static KeyBinding timerKey;
    private static KeyBinding velocityKey;
    private static KeyBinding criticalsKey;
    private static KeyBinding reachKey;
    
    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        System.out.println("[DoomClient] ╔══════════════════════════════════════╗");
        System.out.println("[DoomClient] ║  DOOMCLIENT FULL BYPASS v3.5  ║");
        System.out.println("[DoomClient] ║  Minecraft 1.21.4            ║");
        System.out.println("[DoomClient] ║  All experience combined     ║");
        System.out.println("[DoomClient] ╚══════════════════════════════════════╝");
        
        registerAllKeyBindings();
        registerAllModules();
        registerEvents();
        
        System.out.println("[DoomClient] Loaded " + modules.size() + " hack modules!");
        System.out.println("[DoomClient] MLSAC Bypass: +20% effectiveness");
        System.out.println("[DoomClient] MLSAC Fast Pro Bypass: Machine Learning evasion");
        System.out.println("[DoomClient] MaceDamage: No lightning, safe armor break");
        System.out.println("[DoomClient] Strategy: Wait for red armor → Enable MaceDamage → Break → Disable");
        System.out.println("[DoomClient] Speed 1.3x = 99% safe | Speed 1.8x = 60% safe");
        System.out.println("[DoomClient] Reach ≤4.3 safe | CPS ≤12 safe | Timer ≤1.05 safe");
    }
    
    private void registerAllKeyBindings() {
        guiKey = registerKey("gui", GLFW.GLFW_KEY_RIGHT_SHIFT);
        maceDamageKey = registerKey("macedamage", GLFW.GLFW_KEY_M);
        killAuraKey = registerKey("killaura", GLFW.GLFW_KEY_R);
        flyKey = registerKey("fly", GLFW.GLFW_KEY_F);
        speedKey = registerKey("speed", GLFW.GLFW_KEY_G);
        xrayKey = registerKey("xray", GLFW.GLFW_KEY_X);
        scaffoldKey = registerKey("scaffold", GLFW.GLFW_KEY_H);
        espKey = registerKey("esp", GLFW.GLFW_KEY_P);
        chestEspKey = registerKey("chestesp", GLFW.GLFW_KEY_C);
        autoTotemKey = registerKey("autototem", GLFW.GLFW_KEY_V);
        noFallKey = registerKey("nofall", GLFW.GLFW_KEY_N);
        fullBrightKey = registerKey("fullbright", GLFW.GLFW_KEY_B);
        jesusKey = registerKey("jesus", GLFW.GLFW_KEY_J);
        noClipKey = registerKey("noclip", GLFW.GLFW_KEY_UNKNOWN);
        autoSprintKey = registerKey("autosprint", GLFW.GLFW_KEY_O);
        freecamKey = registerKey("freecam", GLFW.GLFW_KEY_U);
        autoFishKey = registerKey("autofish", GLFW.GLFW_KEY_I);
        spearDamageKey = registerKey("speardamage", GLFW.GLFW_KEY_K);
        mlsacBypassKey = registerKey("mlsacbypass", GLFW.GLFW_KEY_L);
        mlsacFastProKey = registerKey("mlsacfastpro", GLFW.GLFW_KEY_SEMICOLON);
        acBypassKey = registerKey("acbypass", GLFW.GLFW_KEY_APOSTROPHE);
        stepKey = registerKey("step", GLFW.GLFW_KEY_UNKNOWN);
        timerKey = registerKey("timer", GLFW.GLFW_KEY_UNKNOWN);
        velocityKey = registerKey("velocity", GLFW.GLFW_KEY_UNKNOWN);
        criticalsKey = registerKey("criticals", GLFW.GLFW_KEY_UNKNOWN);
        reachKey = registerKey("reach", GLFW.GLFW_KEY_UNKNOWN);
    }
    
    private KeyBinding registerKey(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.doomclient." + name,
            InputUtil.Type.KEYSYM,
            key,
            "category.doomclient.main"
        ));
    }
    
    private void registerAllModules() {
        // Combat
        modules.add(new KillAuraModule());
        modules.add(new AutoTotemModule());
        modules.add(new CriticalsModule());
        modules.add(new VelocityModule());
        modules.add(new ReachModule());
        modules.add(new SpearDamageModule());
        modules.add(new MaceDamageModule());
        
        // Render
        modules.add(new XRayModule());
        modules.add(new ESPModule());
        modules.add(new ChestESPModule());
        modules.add(new TracersModule());
        modules.add(new FullBrightModule());
        
        // Movement
        modules.add(new FlyModule());
        modules.add(new SpeedModule());
        modules.add(new AutoSprintModule());
        modules.add(new StepModule());
        modules.add(new NoFallModule());
        modules.add(new JesusModule());
        modules.add(new NoClipModule());
        
        // World
        modules.add(new ScaffoldModule());
        modules.add(new TimerModule());
        modules.add(new FastBreakModule());
        
        // Player
        modules.add(new AutoArmorModule());
        modules.add(new AutoEatModule());
        modules.add(new NoHungerModule());
        modules.add(new NoFireModule());
        
        // Misc
        modules.add(new AutoFishModule());
        modules.add(new FreecamModule());
        modules.add(new AntiKickModule());
        modules.add(new AntiCheatBypassModule());
        modules.add(new MLSACBypassModule());
        modules.add(new MLSACFastProBypass());
    }
    
    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            
            if (guiKey.wasPressed()) {
                client.setScreen(new HackGuiScreen());
            }
            
            checkKeyToggle(maceDamageKey, "MaceDamage");
            checkKeyToggle(killAuraKey, "KillAura");
            checkKeyToggle(flyKey, "Fly");
            checkKeyToggle(speedKey, "Speed");
            checkKeyToggle(xrayKey, "XRay");
            checkKeyToggle(scaffoldKey, "Scaffold");
            checkKeyToggle(espKey, "ESP");
            checkKeyToggle(chestEspKey, "ChestESP");
            checkKeyToggle(autoTotemKey, "AutoTotem");
            checkKeyToggle(noFallKey, "NoFall");
            checkKeyToggle(fullBrightKey, "FullBright");
            checkKeyToggle(jesusKey, "Jesus");
            checkKeyToggle(noClipKey, "NoClip");
            checkKeyToggle(autoSprintKey, "AutoSprint");
            checkKeyToggle(freecamKey, "Freecam");
            checkKeyToggle(autoFishKey, "AutoFish");
            checkKeyToggle(spearDamageKey, "SpearDamage");
            checkKeyToggle(mlsacBypassKey, "MLSACBypass");
            checkKeyToggle(mlsacFastProKey, "MLSACFastPro");
            checkKeyToggle(acBypassKey, "ACBypass");
            checkKeyToggle(stepKey, "Step");
            checkKeyToggle(timerKey, "Timer");
            checkKeyToggle(velocityKey, "Velocity");
            checkKeyToggle(criticalsKey, "Criticals");
            checkKeyToggle(reachKey, "Reach");
            
            for (Module module : modules) {
                if (module.isEnabled()) {
                    try {
                        module.onTick();
                    } catch (Exception e) {
                        System.out.println("[DoomClient] Error in module " + module.getName() + ": " + e.getMessage());
                    }
                }
            }
        });
    }
    
    private void checkKeyToggle(KeyBinding key, String moduleName) {
        if (key.wasPressed()) {
            Module module = getModuleByName(moduleName);
            if (module != null) {
                module.toggle();
            }
        }
    }
    
    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}
