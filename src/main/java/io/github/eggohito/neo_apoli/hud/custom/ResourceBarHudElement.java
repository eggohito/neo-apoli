package io.github.eggohito.neo_apoli.hud.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import io.github.eggohito.neo_apoli.hud.type.HudElementTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;

public record ResourceBarHudElement(Properties properties, NumberProvider x, NumberProvider y, BooleanProvider shouldRender, Optional<NumberProvider> min, Optional<NumberProvider> max, Optional<NumberProvider> value, int order) implements HudElement {

	public static final MapCodec<ResourceBarHudElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Properties.MAP_CODEC.forGetter(ResourceBarHudElement::properties),
		NumberProvider.CODEC.optionalFieldOf("x", new ConstantNumberProvider(0)).forGetter(ResourceBarHudElement::x),
		NumberProvider.CODEC.optionalFieldOf("y", new ConstantNumberProvider(0)).forGetter(ResourceBarHudElement::y),
		BooleanProvider.CODEC.optionalFieldOf("should_render", new ConstantBooleanProvider(true)).forGetter(ResourceBarHudElement::shouldRender),
		NumberProvider.CODEC.optionalFieldOf("min").forGetter(ResourceBarHudElement::min),
		NumberProvider.CODEC.optionalFieldOf("max").forGetter(ResourceBarHudElement::max),
		NumberProvider.CODEC.optionalFieldOf("value").forGetter(ResourceBarHudElement::value),
		Codec.INT.optionalFieldOf("order", 0).forGetter(ResourceBarHudElement::order)
	).apply(instance, ResourceBarHudElement::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResourceBarHudElement> STREAM_CODEC = StreamCodec.composite(
		Properties.STREAM_CODEC, ResourceBarHudElement::properties,
		NumberProvider.STREAM_CODEC, ResourceBarHudElement::x,
		NumberProvider.STREAM_CODEC, ResourceBarHudElement::y,
		BooleanProvider.STREAM_CODEC, ResourceBarHudElement::shouldRender,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::min,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::max,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::value,
		ByteBufCodecs.INT, ResourceBarHudElement::order,
		ResourceBarHudElement::new
	);

	@Override
	public HudElementType<?> getType() {
		return HudElementTypes.RESOURCE_BAR;
	}

	@Override
	public boolean shouldRender(Context context) {
		return shouldRender().next(context.makeChild(".should_render"));
	}

	@Override
	public void validate(ProblemReporter reporter) {

		HudElement.super.validate(reporter);

		//	Check if the following context keys and fields are absent
		reportIfBothAbsent(reporter, NeoApoliContextKeys.MAX_VALUE, max(), "max");
		reportIfBothAbsent(reporter, NeoApoliContextKeys.MIN_VALUE, min(), "min");
		reportIfBothAbsent(reporter, NeoApoliContextKeys.CUR_VALUE, value(), "value");

		properties().validate(reporter);
		min().ifPresent(min -> min.validate(reporter.forChild(".min")));
		max().ifPresent(max -> max.validate(reporter.forChild(".max")));
		value().ifPresent(value -> value.validate(reporter.forChild(".value")));

	}

	public double getFill(Context context) {

		Context minContext = context.makeChild(".min");
		double min = min()
			.map(p -> p.nextDouble(minContext))
			.or(() -> context.optional(NeoApoliContextKeys.MIN_VALUE))
			.orElse(0.0D);

		if (minContext.hasErrors()) {
			return 0.0D;
		}

		Context maxContext = context.makeChild(".max");
		double max = max()
			.map(p -> p.nextDouble(maxContext))
			.or(() -> context.optional(NeoApoliContextKeys.MAX_VALUE))
			.orElse(min + 1.0);

		if (maxContext.hasErrors()) {
			return 0.0D;
		}

		Context valueContext = context.makeChild(".value");
		double value = value()
			.map(p -> p.nextDouble(valueContext))
			.or(() -> context.optional(NeoApoliContextKeys.CUR_VALUE))
			.orElse(min);

		if (valueContext.hasErrors()) {
			return 0.0D;
		}

		Context invertedContext = context.makeChild(".inverted");
		boolean inverted = properties().inverted().next(invertedContext);

		double fill = Mth.clamp((value - min) / (max - min), 0.0D, 1.0D);
		return inverted ? 1.0 - fill : fill;

	}

	private static void reportIfBothAbsent(ProblemReporter reporter, ContextKey<?> key, Optional<NumberProvider> field, String fieldName) {

		if (!reporter.getKeySet().allowed().contains(key) && field.isEmpty()) {
			reporter.report("Either the parameter \"" + key.name() + "\" must be provided, or the field \"" + fieldName + "\" be defined!");
		}

	}

	public record Properties(SpriteLocation spriteLocation, BooleanProvider inverted) implements ContextAware {

		public static final MapCodec<Properties> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SpriteLocation.CODEC.optionalFieldOf("sprite_location", SpriteLocation.DEFAULT).forGetter(Properties::spriteLocation),
			BooleanProvider.CODEC.optionalFieldOf("inverted", new ConstantBooleanProvider(false)).forGetter(Properties::inverted)
		).apply(instance, Properties::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM_CODEC = StreamCodec.composite(
			SpriteLocation.STREAM_CODEC, Properties::spriteLocation,
			BooleanProvider.STREAM_CODEC, Properties::inverted,
			Properties::new
		);

		@Override
		public void validate(ProblemReporter reporter) {
			ContextAware.super.validate(reporter);
			inverted().validate(reporter.forChild(".inverted"));
		}

	}

	public record SpriteLocation(ResourceLocation background, ResourceLocation fill, ResourceLocation icon) {

		public static final SpriteLocation DEFAULT = new SpriteLocation(
			NeoApoli.id("resource_bar/1/background"),
			NeoApoli.id("resource_bar/1/fill"),
			NeoApoli.id("resource_bar/1/icon")
		);

		public static final Codec<SpriteLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DynamicResourceLocation.CODEC.fieldOf("background").forGetter(SpriteLocation::background),
			DynamicResourceLocation.CODEC.fieldOf("fill").forGetter(SpriteLocation::fill),
			DynamicResourceLocation.CODEC.fieldOf("icon").forGetter(SpriteLocation::icon)
		).apply(instance, SpriteLocation::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SpriteLocation> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, SpriteLocation::background,
			ResourceLocation.STREAM_CODEC, SpriteLocation::fill,
			ResourceLocation.STREAM_CODEC, SpriteLocation::icon,
			SpriteLocation::new
		);

	}

}
