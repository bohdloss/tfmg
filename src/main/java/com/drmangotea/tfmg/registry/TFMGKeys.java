package com.drmangotea.tfmg.registry;

import com.drmangotea.tfmg.TFMG;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum TFMGKeys {

	TRANSMISSION_SHIFT_UP("transmission_shift_up", GLFW.GLFW_KEY_V, "Increases transmission shift"),
	TRANSMISSION_SHIFT_DOWN("transmission_shift_down", GLFW.GLFW_KEY_C, "Decreases transmission shift"),
	ENGINE_CONTROLLER_CUSTOM_BUTTON("custom_button", GLFW.GLFW_KEY_B, "Button for custom engine controls feature"),
	ENGINE_START("engine_start", GLFW.GLFW_KEY_I, "Starts and stops the engine"),

	;

	private KeyMapping keybind;
	private final String description;
	private final String translation;
	private final int key;
	private final boolean modifiable;

	TFMGKeys(int defaultKey) {
		this("", defaultKey, "");
	}

	TFMGKeys(String description, int defaultKey, String translation) {
		this.description = TFMG.MOD_ID + ".keyinfo." + description;
		this.key = defaultKey;
		this.modifiable = !description.isEmpty();
		this.translation = translation;
	}

	public static void provideLang(BiConsumer<String, String> consumer) {
		for (TFMGKeys key : values())
			if (key.modifiable)
				consumer.accept(key.description, key.translation);
	}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		for (TFMGKeys key : values()) {
			key.keybind = new KeyMapping(key.description, key.key, "Create: The Factory Must Grow");
			if (!key.modifiable)
				continue;

			event.register(key.keybind);
		}
	}

	public KeyMapping getKeybind() {
		return keybind;
	}

	public boolean isPressed() {
		if (!modifiable)
			return isKeyDown(key);
		return keybind.isDown();
	}

	public String getBoundKey() {
		return keybind.getTranslatedKeyMessage()
				.getString()
				.toUpperCase();
	}

	public int getBoundCode() {
		return keybind.getKey()
				.getValue();
	}

	public static boolean isKeyDown(int key) {
		return InputConstants.isKeyDown(Minecraft.getInstance()
				.getWindow()
				.getWindow(), key);
	}

	public static boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(Minecraft.getInstance()
				.getWindow()
				.getWindow(), button) == 1;
	}

	public static boolean ctrlDown() {
		return Screen.hasControlDown();
	}

	public static boolean shiftDown() {
		return Screen.hasShiftDown();
	}

	public static boolean altDown() {
		return Screen.hasAltDown();
	}
}
