package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.meta.box.ConstantBoxProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Box;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class EntityBoxProvider extends BoxProvider {

	public static final MapCodec<EntityBoxProvider> CODEC = MapCodecUtil.lazy(EntityBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("entity").forGetter(EntityBoxProvider::entity),
		BoxProvider.CODEC.codec().optionalFieldOf("offset", new ConstantBoxProvider(0, 0, 0, 0, 0, 0)).forGetter(EntityBoxProvider::offset)
	).apply(instance, EntityBoxProvider::new)));

	public static final PacketCodec<RegistryByteBuf, EntityBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(EntityBoxProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, EntityBoxProvider::entity,
		BoxProvider.PACKET_CODEC, EntityBoxProvider::offset,
		EntityBoxProvider::new
	));

	private final EntityParameter entity;
	private final BoxProvider offset;

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.ENTITY;
	}

	@Override
	protected Box impl(Context context) {

		Box boundingBox = context.required(entity().getParameter()).getBoundingBox();
		Box offsetBox = offset().next(context.makeChild(".offset"));

		return new Box(
			boundingBox.minX + offsetBox.minX,
			boundingBox.minY + offsetBox.minY,
			boundingBox.minZ + offsetBox.minZ,
			boundingBox.maxX + offsetBox.maxX,
			boundingBox.maxY + offsetBox.maxY,
			boundingBox.maxZ + offsetBox.maxZ
		);

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(entity().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		offset().validate(reporter.makeChild(".offset"));
	}

}
