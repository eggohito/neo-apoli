package io.github.eggohito.neo_apoli.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.eggohito.neo_apoli.client.config.NeoApoliClientConfig;
import io.github.eggohito.neo_apoli.config.NeoApoliConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@SuppressWarnings("UnstableApiUsage")
public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> {

			var yacl = YetAnotherConfigLib.createBuilder()
				.title(Component.literal("neo-apoli"))
				.category(createCommandsCategory(NeoApoliConfig.INSTANCE.command.asBinding()))
				.category(createIdentifierCategory(NeoApoliConfig.INSTANCE.placeholderIdentifier.asBinding()))
				.category(createModifyPlayerSpawnCategory(NeoApoliConfig.INSTANCE.modifyPlayerSpawn.asBinding()))
				.category(createResourceBarsCategory(NeoApoliClientConfig.INSTANCE.resourceBars.asBinding()))
				.save(NeoApoliConfig.INSTANCE::saveToFile)
				.build();

			return yacl.generateScreen(screen);

		};
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

	private static ConfigCategory createCommandsCategory(Binding<NeoApoliConfig.Command> binding) {

		var showOutput = Option.<Boolean>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.commands.option.show_output.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.commands.option.show_output.description")))
			.binding(binding.xmap(NeoApoliConfig.Command::showOutput, binding.getValue()::showOutput))
			.controller(option -> BooleanControllerBuilder.create(option)
				.onOffFormatter()
				.coloured(true));

		var permissionLevel = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.commands.option.permission_level.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.commands.option.permission_level.description")))
			.binding(binding.xmap(NeoApoliConfig.Command::permissionLevel, binding.getValue()::permissionLevel))
			.controller(option -> IntegerSliderControllerBuilder.create(option)
				.formatValue(ModMenuIntegration::formatPermissionLevel)
				.range(0, 4)
				.step(1));

		return ConfigCategory.createBuilder()
			.name(Component.translatable("config.neo-apoli.category.commands.name"))
			.option(showOutput.build())
			.option(permissionLevel.build())
			.build();

	}

	private static ConfigCategory createIdentifierCategory(Binding<NeoApoliConfig.PlaceholderIdentifier> binding) {

		var enabled = Option.<Boolean>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.placeholder_identifier.option.enabled.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.placeholder_identifier.option.enabled.description")))
			.binding(binding.xmap(NeoApoliConfig.PlaceholderIdentifier::enabled, binding.getValue()::enabled))
			.controller(option -> BooleanControllerBuilder.create(option)
				.onOffFormatter()
				.coloured(true));

		return ConfigCategory.createBuilder()
			.name(Component.translatable("config.neo-apoli.category.placeholder_identifier.name"))
			.option(enabled.build())
			.build();

	}

	private static ConfigCategory createModifyPlayerSpawnCategory(Binding<NeoApoliConfig.ModifyPlayerSpawn> binding) {

		var horizontalSteps = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.horizontal_step.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.horizontal_step.description")))
			.binding(binding.xmap(NeoApoliConfig.ModifyPlayerSpawn::horizontalStep, binding.getValue()::horizontalStep))
			.controller(option -> IntegerFieldControllerBuilder.create(option)
				.min(0));

		var verticalSteps = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.vertical_step.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.vertical_step.description")))
			.binding(binding.xmap(NeoApoliConfig.ModifyPlayerSpawn::verticalStep, binding.getValue()::verticalStep))
			.controller(option -> IntegerFieldControllerBuilder.create(option)
				.min(0));

		var radius = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.radius.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.modify_player_spawn.option.radius.description")))
			.binding(binding.xmap(NeoApoliConfig.ModifyPlayerSpawn::radius, binding.getValue()::radius))
			.controller(option -> IntegerFieldControllerBuilder.create(option)
				.min(0));

		return ConfigCategory.createBuilder()
			.name(Component.translatable("config.neo-apoli.category.modify_player_spawn.name"))
			.option(horizontalSteps.build())
			.option(verticalSteps.build())
			.option(radius.build())
			.build();

	}

	private static ConfigCategory createResourceBarsCategory(Binding<NeoApoliClientConfig.ResourceBars> binding) {

		var offsetX = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_x.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_x.description")))
			.binding(binding.xmap(NeoApoliClientConfig.ResourceBars::offsetX, binding.getValue()::offsetX))
			.controller(option -> IntegerFieldControllerBuilder.create(option)
				.min(0));

		var offsetY = Option.<Integer>createBuilder()
			.name(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_y.name"))
			.description(OptionDescription.of(Component.translatable("config.neo-apoli.category.resource_bars.option.offset_y.description")))
			.binding(binding.xmap(NeoApoliClientConfig.ResourceBars::offsetY, binding.getValue()::offsetY)).controller(option -> IntegerFieldControllerBuilder.create(option)
				.min(0));

		return ConfigCategory.createBuilder()
			.name(Component.translatable("config.neo-apoli.category.resource_bars.name"))
			.option(offsetX.build())
			.option(offsetY.build())
			.build();

	}

}
