package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface HudElement extends ContextUser {

	Codec<HudElement> CODEC = Type.CODEC.dispatch(HudElement::getType, Type::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, HudElement> STREAM_CODEC = Type.STREAM_CODEC.dispatch(HudElement::getType, Type::streamCodec);

	Type<?> getType();

	int order();

	default boolean shouldRender(Context context, RenderPhase renderPhase) {
		return renderPhase == RenderPhase.ABOVE_HUD;
	}

	default boolean hideWithHud(Context context) {
		return true;
	}

	@Getter
	@Setter
	class Position {
		int x;
		int y;
	}

	record Type<G extends HudElement>(MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.HUD_ELEMENT_TYPE);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.HUD_ELEMENT_TYPE);

	}

	enum RenderPhase {

		BELOW_HUD,

		ABOVE_HUD;

		public static final Codec<RenderPhase> CODEC = CodecUtil.enumType(RenderPhase.class);

		public static final StreamCodec<ByteBuf, RenderPhase> STREAM_CODEC = StreamCodecUtil.enumType(RenderPhase.class);

	}

}
