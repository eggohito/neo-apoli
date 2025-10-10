package io.github.eggohito.neo_apoli.util.modifier.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import io.github.eggohito.neo_apoli.util.modifier.custom.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModifierTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ModifierType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.MODIFIER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, ModifierType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.MODIFIER_TYPE);

	public static final ModifierType<MultiplyModifier> MULTIPLY = registerInternal("multiply", MultiplyModifier.CODEC, MultiplyModifier.PACKET_CODEC);
	public static final ModifierType<MultiplyAdditiveModifier> MULTIPLY_ADDITIVE = registerInternal("multiply_additive", MultiplyAdditiveModifier.CODEC, MultiplyAdditiveModifier.PACKET_CODEC);
	public static final ModifierType<MultiplyMultiplicativeModifier> MULTIPLY_MULTIPLICATIVE = registerInternal("multiply_multiplicative", MultiplyMultiplicativeModifier.CODEC, MultiplyMultiplicativeModifier.PACKET_CODEC);
	public static final ModifierType<DivideModifier> DIVIDE = registerInternal("divide", DivideModifier.CODEC, DivideModifier.PACKET_CODEC);
	public static final ModifierType<AddModifier> ADD = registerInternal("add", AddModifier.CODEC, AddModifier.PACKET_CODEC);
	public static final ModifierType<MinModifier> MIN = registerInternal("min", MinModifier.CODEC, MinModifier.PACKET_CODEC);
	public static final ModifierType<MaxModifier> MAX = registerInternal("max", MaxModifier.CODEC, MaxModifier.PACKET_CODEC);
	public static final ModifierType<SetModifier> SET = registerInternal("set", SetModifier.CODEC, SetModifier.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.getPaths().addAlias("multiplication", getPath(MULTIPLY));
		ALIASES.getPaths().addAlias("division", getPath(DIVIDE));
		ALIASES.getPaths().addAlias("addition", getPath(ADD));

	}

	private static <M extends Modifier> ModifierType<M> registerInternal(String path, MapCodec<M> mapCodec, PacketCodec<RegistryByteBuf, M> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <M extends Modifier> String getPath(ModifierType<M> type) {
		return RegistryUtil.getIdPath(NeoApoliRegistries.MODIFIER_TYPE, type);
	}

	public static <M extends Modifier> ModifierType<M> register(Identifier id, MapCodec<M> mapCodec, PacketCodec<RegistryByteBuf, M> packetCodec) {
		return Registry.register(NeoApoliRegistries.MODIFIER_TYPE, id, new ModifierType<>(mapCodec, packetCodec));
	}

}
