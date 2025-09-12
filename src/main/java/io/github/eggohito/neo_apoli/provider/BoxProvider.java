package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public abstract class BoxProvider extends ValueProvider<Box> {

	public static final MapCodec<BoxProvider> CODEC = BoxProviderTypes.CODEC.dispatchMap("type", BoxProvider::getType, BoxProviderType::mapCodec);
	public static final PacketCodec<RegistryByteBuf, BoxProvider> PACKET_CODEC = BoxProviderTypes.PACKET_CODEC.dispatch(BoxProvider::getType, BoxProviderType::packetCodec);

	@Override
	public abstract BoxProviderType<?> getType();

	@Override
	public Box next(Context context) {
		return this.provideValue("box", context, this::impl, () -> Box.from(Vec3d.ZERO));
	}

	@Override
	public String asDisplayString() {
		return "Box provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.BOX_PROVIDER_TYPE, this.getType()) + "\"";
	}

	protected abstract Box impl(Context context);

	public Box nextAndTranslate(Context context) {

		Box box = this.next(context);
		if (!this.hasEntity(context) && context.hasParameter(ContextParameters.POSITION)) {

			Vec3d position = context.required(ContextParameters.POSITION);

			Vec3d minPos = box.getMinPos();
			Vec3d maxPos = box.getMaxPos();

			return new Box(
				position.subtract(minPos),
				position.add(maxPos)
			);

		}

		else {
			return box;
		}

	}

	public ShapeContext getShapeContext(Context context) {
		return ShapeContext.absent();
	}

	public boolean hasEntity(Context context) {
		return this.getShapeContext(context) instanceof EntityShapeContext entityShapeContext
			&& entityShapeContext.getEntity() != null;
	}

}
