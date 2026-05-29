package io.github.eggohito.neo_apoli.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.eggohito.neo_apoli.api.config.ConfigCategoryRegistrant;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@SuppressWarnings("UnstableApiUsage")
public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> {

			var yacl = YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("config.neo-apoli"))
				.category(this.createCommonCategory())
				.category(this.createHudElementTypesCategory())
				.category(this.createPowerTypesCategory())
				.save(NeoApoliCommonConfig.INSTANCE::saveToFile)
				.build();

			return yacl.generateScreen(screen);

		};
	}

	private ConfigCategory createCommonCategory() {

		var commandsGroup = this.createCommandGroup(NeoApoliCommonConfig.INSTANCE.command.asBinding());
		var placeholderIdentifierGroup = this.createPlaceholderIdentifierGroup(NeoApoliCommonConfig.INSTANCE.placeholderIdentifier.asBinding());

		return ConfigCategory.createBuilder()
			.name(Component.translatable("config.neo-apoli.common.name"))
			.group(commandsGroup)
			.group(placeholderIdentifierGroup)
			.build();

	}

	private ConfigCategory createHudElementTypesCategory() {

		var category = ConfigCategory.createBuilder().name(Component.translatable("config.neo-apoli.type.hud_element.name"));
		ConfigCategoryRegistrant.HUD_ELEMENT_TYPE.invoker().addGroup(category::group);

		return category.build();

	}

	private ConfigCategory createPowerTypesCategory() {

		var category = ConfigCategory.createBuilder().name(Component.translatable("config.neo-apoli.type.power.name"));
		ConfigCategoryRegistrant.POWER_TYPE.invoker().addGroup(category::group);

		return category.build();

	}

	private OptionGroup createCommandGroup(Binding<NeoApoliCommonConfig.Command> binding) {

		var showOutput = Option.<Boolean>createBuilder()
			.name(Component.translatable("config.neo-apoli.common.command.option.show_output.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.common.command.option.show_output.description")))
			.binding(binding.xmap(NeoApoliCommonConfig.Command::showOutput, binding.getValue()::showOutput))
			.controller(option -> BooleanControllerBuilder.create(option)
				.onOffFormatter()
				.coloured(true));

		var permissionLevel = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.common.command.option.permission_level.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.common.command.option.permission_level.description")))
			.binding(binding.xmap(NeoApoliCommonConfig.Command::permissionLevel, binding.getValue()::permissionLevel))
			.controller(option -> IntegerSliderControllerBuilder.create(option)
				.formatValue(ModMenuIntegration::formatPermissionLevel)
				.range(0, 4)
				.step(1));

		return OptionGroup.createBuilder()
			.name(Component.translatable("config.neo-apoli.common.command.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.common.command.description")))
			.option(showOutput.build())
			.option(permissionLevel.build())
			.build();

	}

	private OptionGroup createPlaceholderIdentifierGroup(Binding<NeoApoliCommonConfig.PlaceholderIdentifier> binding) {

		var enabled = Option.<Boolean>createBuilder()
			.name(Component.translatable("config.neo-apoli.common.placeholder_identifier.option.enabled.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.common.placeholder_identifier.option.enabled.description")))
			.binding(binding.xmap(NeoApoliCommonConfig.PlaceholderIdentifier::enabled, binding.getValue()::enabled))
			.controller(option -> BooleanControllerBuilder.create(option)
				.onOffFormatter()
				.coloured(true));

		return OptionGroup.createBuilder()
			.name(Component.translatable("config.neo-apoli.common.placeholder_identifier.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.common.placeholder_identifier.description")))
			.option(enabled.build())
			.build();

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
