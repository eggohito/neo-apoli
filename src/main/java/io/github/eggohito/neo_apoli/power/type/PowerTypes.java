package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.GiveItemsPower;
import io.github.eggohito.neo_apoli.power.internal.DummyPower;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PowerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<PowerType<?>> CODEC = RegistryUtil.getAliasedCodec(NeoApoliRegistries.POWER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, PowerType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE);

	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, MultiplePower.CODEC, MultiplePower.PACKET_CODEC);

	public static final PowerType<GiveItemsPower> GIVE_ITEMS = registerInternal("give_items", GiveItemsPower.CODEC, GiveItemsPower.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends Power> PowerType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(mapCodec, packetCodec));
	}

}
