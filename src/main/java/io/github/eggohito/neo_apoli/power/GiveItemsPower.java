package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.IndexedStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public class GiveItemsPower extends Power {

	public static final MapCodec<GiveItemsPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).and(
		IndexedStack.LIST_CODEC.fieldOf("stacks").forGetter(GiveItemsPower::getStacks)
	).apply(instance, GiveItemsPower::new));

	public static final PacketCodec<RegistryByteBuf, GiveItemsPower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> IndexedStack.LIST_PACKET_CODEC.encode(buf, power.getStacks()),
		(buf, metadata) -> new GiveItemsPower(metadata, IndexedStack.LIST_PACKET_CODEC.decode(buf))
	);

	private final List<IndexedStack> stacks;

	public GiveItemsPower(Metadata metadata, List<IndexedStack> stacks) {
		super(metadata);
		this.stacks = stacks;
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.GIVE_ITEMS;
	}

	public List<IndexedStack> getStacks() {
		return stacks;
	}

}
