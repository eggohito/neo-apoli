package io.github.eggohito.neo_apoli.hud.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.hud.NumberBoundHudElement;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import io.github.eggohito.neo_apoli.hud.type.HudElementTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Optional;

public record ResourceBarHudElement(Properties properties, NumberProvider x, NumberProvider y, BooleanProvider shouldRender, Optional<NumberProvider> value, Optional<NumberProvider> min, Optional<NumberProvider> max, int order) implements NumberBoundHudElement {

	public static final MapCodec<ResourceBarHudElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Properties.MAP_CODEC.forGetter(ResourceBarHudElement::properties),
		NumberProvider.CODEC.optionalFieldOf("x", new ConstantNumberProvider(0)).forGetter(ResourceBarHudElement::x),
		NumberProvider.CODEC.optionalFieldOf("y", new ConstantNumberProvider(0)).forGetter(ResourceBarHudElement::y),
		BooleanProvider.CODEC.optionalFieldOf("should_render", new ConstantBooleanProvider(true)).forGetter(ResourceBarHudElement::shouldRender),
		NumberProvider.CODEC.optionalFieldOf("value").forGetter(ResourceBarHudElement::value),
		NumberProvider.CODEC.optionalFieldOf("min").forGetter(ResourceBarHudElement::min),
		NumberProvider.CODEC.optionalFieldOf("max").forGetter(ResourceBarHudElement::max),
		Codec.INT.optionalFieldOf("order", 0).forGetter(ResourceBarHudElement::order)
	).apply(instance, ResourceBarHudElement::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResourceBarHudElement> STREAM_CODEC = StreamCodec.composite(
		Properties.STREAM_CODEC, ResourceBarHudElement::properties,
		NumberProvider.STREAM_CODEC, ResourceBarHudElement::x,
		NumberProvider.STREAM_CODEC, ResourceBarHudElement::y,
		BooleanProvider.STREAM_CODEC, ResourceBarHudElement::shouldRender,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::value,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::min,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), ResourceBarHudElement::max,
		ByteBufCodecs.INT, ResourceBarHudElement::order,
		ResourceBarHudElement::new
	);

	@Override
	public HudElementType<?> getType() {
		return HudElementTypes.RESOURCE_BAR;
	}

	@Override
	public boolean shouldRender(Context context, HudRenderPhase renderPhase) {
		return NumberBoundHudElement.super.shouldRender(context, renderPhase)
			&& shouldRender().next(context.forChild(".should_render"));
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberBoundHudElement.super.validate(validator);

		properties().validate(validator);
		min().ifPresent(min -> min.validate(validator.forChild(".min")));
		max().ifPresent(max -> max.validate(validator.forChild(".max")));
		value().ifPresent(value -> value.validate(validator.forChild(".value")));

	}

	public double getFill(Context context) {

		Context minContext = context.forChild(".min");
		double min = min()
			.map(p -> p.nextDouble(minContext))
			.or(() -> context.getOptional(NeoApoliContextParams.MIN_VALUE))
			.orElse(0.0D);

		if (minContext.hasErrors()) {
			return 0.0D;
		}

		Context maxContext = context.forChild(".max");
		double max = max()
			.map(p -> p.nextDouble(maxContext))
			.or(() -> context.getOptional(NeoApoliContextParams.MAX_VALUE))
			.orElse(min + 1.0);

		if (maxContext.hasErrors()) {
			return 0.0D;
		}

		Context valueContext = context.forChild(".value");
		double value = value()
			.map(p -> p.nextDouble(valueContext))
			.or(() -> context.getOptional(NeoApoliContextParams.CURRENT_VALUE))
			.orElse(min);

		if (valueContext.hasErrors()) {
			return 0.0D;
		}

		Context invertedContext = context.forChild(".inverted");
		boolean inverted = properties().inverted().next(invertedContext);

		double fill = Mth.clamp((value - min) / (max - min), 0.0D, 1.0D);
		return inverted ? 1.0 - fill : fill;

	}

	public record Properties(SpriteLocation spriteLocation, BooleanProvider inverted) implements ContextUser {

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
		public void validate(Context.Validator validator) {
			ContextUser.super.validate(validator);
			inverted().validate(validator.forChild(".inverted"));
		}

	}

	public record SpriteLocation(ResourceLocation background, ResourceLocation fill, ResourceLocation icon) {

		public static final SpriteLocation DEFAULT = new SpriteLocation(
			NeoApoli.id("resource_bar/1/background"),
			NeoApoli.id("resource_bar/1/fill"),
			NeoApoli.id("resource_bar/1/icon")
		);

		public static final Codec<SpriteLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("background").forGetter(SpriteLocation::background),
			ResourceLocation.CODEC.fieldOf("fill").forGetter(SpriteLocation::fill),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(SpriteLocation::icon)
		).apply(instance, SpriteLocation::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SpriteLocation> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, SpriteLocation::background,
			ResourceLocation.STREAM_CODEC, SpriteLocation::fill,
			ResourceLocation.STREAM_CODEC, SpriteLocation::icon,
			SpriteLocation::new
		);

	}

}
