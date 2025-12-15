package io.github.eggohito.neo_apoli.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.NeoApoliClient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> YetAnotherConfigLib.createBuilder()
			.title(Component.literal("neo-apoli"))
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("config.neo-apoli.category.commands.name"))
				.option(Option.<Boolean>createBuilder()
					.name(Component.translatable("config.neo-apoli.category.commands.option.show_output.name"))
					.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.commands.option.show_output.description")))
					.binding(false, () -> NeoApoli.getConfig().command.showOutput, newValue -> NeoApoli.getConfig().command.showOutput = newValue)
					.controller(option -> BooleanControllerBuilder.create(option)
						.onOffFormatter()
						.coloured(true)).build())
				.option(Option.<Integer>createBuilder()
					.name(Component.translatable("config.neo-apoli.category.commands.option.permission_level.name"))
					.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.commands.option.permission_level.description")))
					.binding(2, () -> NeoApoli.getConfig().command.permissionLevel, newValue -> NeoApoli.getConfig().command.permissionLevel = newValue)
					.controller(option -> IntegerSliderControllerBuilder.create(option)
						.formatValue(ModMenuIntegration::formatPermissionLevel)
						.range(0, 4)
						.step(1)).build()).build())
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("config.neo-apoli.category.resource_bars.name"))
				.option(Option.<Integer>createBuilder()
					.name(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_x.name"))
					.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_x.description")))
					.binding(0, () -> NeoApoliClient.getConfig().resourceBars.offsetX, newValue -> NeoApoliClient.getConfig().resourceBars.offsetX = newValue)
					.controller(IntegerFieldControllerBuilder::create).build())
				.option(Option.<Integer>createBuilder()
					.name(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_y.name"))
					.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_y.description")))
					.binding(0, () -> NeoApoliClient.getConfig().resourceBars.offsetY, newValue -> NeoApoliClient.getConfig().resourceBars.offsetY = newValue)
					.controller(IntegerFieldControllerBuilder::create).build()).build()).build()
			.generateScreen(screen);
	}

	private static Component formatPermissionLevel(int value) {

		String valueString = Integer.toString(value);
		ChatFormatting formatting = switch (value) {
			case 1 ->
				ChatFormatting.GREEN;
			case 2 ->
				ChatFormatting.YELLOW;
			case 3 ->
				ChatFormatting.GOLD;
			case 4 ->
				ChatFormatting.RED;
			default ->
				ChatFormatting.WHITE;
		};

		return Component.literal(valueString).withStyle(formatting);

	}

}
