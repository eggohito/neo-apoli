package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockSelectablePower;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.*;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class ModifyBlockSelectablePowerMixin extends State<Block, BlockState> {

	@Shadow
	protected abstract BlockState asBlockState();

	protected ModifyBlockSelectablePowerMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
		super(owner, propertyMap, codec);
	}

	@ModifyReturnValue(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
	private VoxelShape neo_apoli$modifySelectableShape(VoxelShape original, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {

		if (shapeContext instanceof EntityShapeContext entityShapeContext && entityShapeContext.getEntity() != null) {

			Context context = ModifyBlockSelectablePower.createContext(entityShapeContext.getEntity(), blockPos, this.asBlockState(), blockView.getBlockEntity(blockPos));

			return ModifyBlockSelectablePower.modify(context, () -> original);

		}

		else {
			return original;
		}

	}

}
