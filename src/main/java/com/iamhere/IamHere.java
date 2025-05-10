package com.iamhere;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IamHere implements ModInitializer {
	public static final String MOD_ID = "iamhere";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final int GLOW_DURATION_SECONDS = 5;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// コマンドの登録
		registerIamHereCommand();

		LOGGER.info("IamHere mod initialized!");
	}

	private void registerIamHereCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("iamhere")
							.executes(context -> {
								ServerPlayerEntity player = context.getSource().getPlayer();
								World world = player.getWorld();

								// 座標情報を取得
								int x = (int) player.getX();
								int y = (int) player.getY();
								int z = (int) player.getZ();

								// ディメンション名を取得
								String dimension = getDimensionName(world);

								// 座標情報をチャットに送信
								String message = String.format("%sの座標: X=%d, Y=%d, Z=%d (%s)",
										player.getName().getString(), x, y, z, dimension);

								context.getSource().getServer().getPlayerManager()
										.broadcast(Text.of(message), false);

								// プレイヤーに発光効果を付与
								player.addStatusEffect(
										new StatusEffectInstance(
												StatusEffects.GLOWING,
												GLOW_DURATION_SECONDS * 20, // 20 ticks = 1秒
												0, // 効果レベル (0 = レベル1)
												false, // 周囲の粒子を表示しない
												true, // 効果アイコンを表示
												true // 効果を表示
								));

								return 1;
							}));
		});
	}

	private String getDimensionName(World world) {
		RegistryKey<World> registryKey = world.getRegistryKey();
		Identifier id = registryKey.getValue();

		if (World.OVERWORLD.getValue().equals(id)) {
			return "オーバーワールド";
		} else if (World.NETHER.getValue().equals(id)) {
			return "ネザー";
		} else if (World.END.getValue().equals(id)) {
			return "エンド";
		} else {
			return id.toString();
		}
	}
}