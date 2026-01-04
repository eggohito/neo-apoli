package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NbtPower extends Power {

	public static final MapCodec<NbtPower> CODEC = MapCodec.unit(NbtPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, NbtPower> STREAM_CODEC = StreamCodecUtil.unit(NbtPower::new);

	@Override
	public PowerType<?> getType() {
		return PowerTypes.NBT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<NbtPower> {

		protected CompoundTag data = new CompoundTag();

		protected Instance(@NotNull Entity holder, @NotNull NbtPower power) {
			super(holder, power);
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return NeoApoliMapCodecs.COMPOUND_TAG.decode(ops, mapInput)
				.map(compoundTag -> this.data = compoundTag)
				.map(compoundTag -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return NeoApoliMapCodecs.COMPOUND_TAG.encode(this.data, ops, prefix);
		}

		@Override
		public boolean isImmutable() {
			return false;
		}

	}

}
