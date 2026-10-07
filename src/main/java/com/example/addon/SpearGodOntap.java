package com.spearcheat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpearCheatMod implements ModInitializer {
    public static final String MOD_ID = "spearcheat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Charge threshold: 1.5 seconds = 30 ticks at 20tps
    public static final int CHARGE_TICKS_REQUIRED = 30;

    // Flat damage override
    public static final float OVERRIDE_DAMAGE = 1000f;

    @Override
    public void onInitialize() {
        LOGGER.info("[SpearCheat] Loaded — 1000dmg static charge, no launch, armor strip.");
    }
}
