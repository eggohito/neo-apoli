package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.DummyPower;
import io.github.eggohito.neo_apoli.power.custom.GiveItemsPower;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PowerTypes {

	//	TODO: Integrate identifier aliasing to this codec
	public static final Codec<PowerType<?>> CODEC = NeoApoliRegistries.POWER_TYPE.getCodec();

	public static final PowerType<DummyPower> DUMMY = register("dummy", DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, MultiplePower.CODEC, MultiplePower.PACKET_CODEC);

	public static final PowerType<GiveItemsPower> GIVE_ITEMS = register("give_items", GiveItemsPower.CODEC, GiveItemsPower.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> register(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	private static <P extends Power> PowerType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(mapCodec, packetCodec));
	}

}
