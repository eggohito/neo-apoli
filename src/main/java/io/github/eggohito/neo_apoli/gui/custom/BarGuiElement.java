package io.github.eggohito.neo_apoli.gui.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.gui.type.GuiElementType;
import io.github.eggohito.neo_apoli.gui.type.GuiElementTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public record BarGuiElement(Properties properties, NumberProvider min, NumberProvider max, NumberProvider value) implements GuiElement, ContextAware {

	public static final int BAR_WIDTH = 72;
	public static final int BAR_HEIGHT = 8;
	public static final int ICON_SIZE = 8;

	private static final MapCodec<NumberProvider> ZERO_NUMBER = MapCodec.unit(new ConstantNumberProvider(0));

	public static final MapCodec<BarGuiElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Properties.CODEC.forGetter(BarGuiElement::properties),
		NumberProvider.CODEC.fieldOf("min").forGetter(BarGuiElement::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(BarGuiElement::max),
		NumberProvider.CODEC.fieldOf("value").forGetter(BarGuiElement::value)
	).apply(instance, BarGuiElement::new));

	public static final MapCodec<BarGuiElement> INTEGRATING_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Properties.CODEC.forGetter(BarGuiElement::properties),
		ZERO_NUMBER.forGetter(BarGuiElement::min),
		ZERO_NUMBER.forGetter(BarGuiElement::max),
		ZERO_NUMBER.forGetter(BarGuiElement::value)
	).apply(instance, BarGuiElement::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BarGuiElement> STREAM_CODEC = StreamCodec.composite(
		Properties.STREAM_CODEC, BarGuiElement::properties,
		NumberProvider.STREAM_CODEC, BarGuiElement::min,
		NumberProvider.STREAM_CODEC, BarGuiElement::max,
		NumberProvider.STREAM_CODEC, BarGuiElement::value,
		BarGuiElement::new
	);

	@Override
	public GuiElementType<?> getType() {
		return GuiElementTypes.BAR;
	}

	@Override
	public void validate(ProblemReporter reporter) {
		ContextAware.super.validate(reporter);
		properties().validate(reporter);
		min().validate(reporter.forChild(".min"));
		max().validate(reporter.forChild(".max"));
		value().validate(reporter.forChild(".value"));
	}

	public double getFill(Context context) {

		Context minContext = context.makeChild(".min");
		double min = min().nextDouble(minContext);

		Context maxContext = context.makeChild(".max");
		double max = max().nextDouble(maxContext);

		Context valueContext = context.makeChild(".value");
		double value = value().nextFloat(valueContext);

		if (minContext.hasErrors() || maxContext.hasErrors() || valueContext.hasErrors()) {
			return 0.0D;
		}

		else {

			Context invertedContext = context.makeChild(".inverted");
			boolean inverted = properties().inverted().next(invertedContext);

			double fill = Mth.clamp((value - min) / (max - min), 0.0D, 1.0D);

			return inverted
				? 1.0 - fill
				: fill;

		}

	}

	public record Properties(SpriteLocation spriteLocation, BooleanProvider shouldRender, BooleanProvider inverted) implements ContextAware {

		public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SpriteLocation.CODEC.codec().optionalFieldOf("sprite_location", SpriteLocation.DEFAULT).forGetter(Properties::spriteLocation),
			BooleanProvider.CODEC.optionalFieldOf("should_render", new ConstantBooleanProvider(true)).forGetter(Properties::shouldRender),
			BooleanProvider.CODEC.optionalFieldOf("inverted", new ConstantBooleanProvider(false)).forGetter(Properties::inverted)
		).apply(instance, Properties::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM_CODEC = StreamCodec.composite(
			SpriteLocation.STREAM_CODEC, Properties::spriteLocation,
			BooleanProvider.STREAM_CODEC, Properties::shouldRender,
			BooleanProvider.STREAM_CODEC, Properties::inverted,
			Properties::new
		);

		@Override
		public void validate(ProblemReporter reporter) {
			ContextAware.super.validate(reporter);
			spriteLocation().validate(reporter);
			shouldRender().validate(reporter.forChild(".should_render"));
			inverted().validate(reporter.forChild(".inverted"));
		}

	}

	public record SpriteLocation(ResourceLocation background, ResourceLocation fill, ResourceLocation icon, NumberProvider x, NumberProvider y) implements ContextAware {

		public static final SpriteLocation DEFAULT = new SpriteLocation(
			NeoApoli.id("resource_bar/1/background"),
			NeoApoli.id("resource_bar/1/fill"),
			NeoApoli.id("resource_bar/1/icon"),
			new ConstantNumberProvider(0),
			new ConstantNumberProvider(0)
		);

		public static final MapCodec<SpriteLocation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DynamicResourceLocation.CODEC.optionalFieldOf("background", DEFAULT.background()).forGetter(SpriteLocation::background),
			DynamicResourceLocation.CODEC.optionalFieldOf("fill", DEFAULT.fill()).forGetter(SpriteLocation::fill),
			DynamicResourceLocation.CODEC.optionalFieldOf("icon", DEFAULT.icon()).forGetter(SpriteLocation::icon),
			NumberProvider.CODEC.optionalFieldOf("x", DEFAULT.x()).forGetter(SpriteLocation::x),
			NumberProvider.CODEC.optionalFieldOf("y", DEFAULT.y()).forGetter(SpriteLocation::y)
		).apply(instance, SpriteLocation::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SpriteLocation> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, SpriteLocation::background,
			ResourceLocation.STREAM_CODEC, SpriteLocation::fill,
			ResourceLocation.STREAM_CODEC, SpriteLocation::icon,
			NumberProvider.STREAM_CODEC, SpriteLocation::x,
			NumberProvider.STREAM_CODEC, SpriteLocation::y,
			SpriteLocation::new
		);

		@Override
		public void validate(ProblemReporter reporter) {
			ContextAware.super.validate(reporter);
			x().validate(reporter.forChild(".x"));
			y().validate(reporter.forChild(".y"));
		}

	}

}
