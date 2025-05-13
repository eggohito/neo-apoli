package io.github.eggohito.neo_apoli.duck;

import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import net.minecraft.util.math.Direction;

public interface BlockBreakingContextAccess {

	SavedBlockPosition neo_apoli$getBrokenBlockCache();

	Direction neo_apoli$getBrokenBlockDirection();

	boolean neo_apoli$wasHarvested();

}
