package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
@Getter
public class NbtPower extends Power {

	public static final MapCodec<NbtPower> CODEC = MapCodec.unit(NbtPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, NbtPower> STREAM_CODEC = StreamCodecUtil.unit(NbtPower::new);

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.NBT;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<NbtPower> {

		protected CompoundTag data = new CompoundTag();

		protected Instance(@NotNull NbtPower power) {
			super(power);
		}

		@Override
		public <I> DataResult<Unit> decodeData(DynamicOps<I> ops, MapLike<I> mapInput) {
			return NeoApoliMapCodecs.COMPOUND_TAG.decode(ops, mapInput)
				.map(compoundTag -> this.data = compoundTag)
				.map(compoundTag -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return NeoApoliMapCodecs.COMPOUND_TAG.encode(this.data, ops, prefix);
		}

		@Override
		public boolean isImmutable(Entity holder) {
			return false;
		}

	}

}
