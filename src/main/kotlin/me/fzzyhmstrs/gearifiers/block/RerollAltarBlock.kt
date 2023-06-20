package me.fzzyhmstrs.gearifiers.block

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class RerollAltarBlock(settings: Settings): Block(settings) {

    companion object{
        private val FACING = Properties.HORIZONTAL_FACING
        private val NORTH_SOUTH_SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 1.0, 16.0, 15.0, 15.0)
        private val EAST_WEST_SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 0.0, 15.0, 15.0, 16.0)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val dir = ctx.horizontalPlayerFacing
        return super.getPlacementState(ctx)?.with(FACING,dir.opposite)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        val direction = state.get(FACING)
        if (direction.axis === Direction.Axis.X) {
            return EAST_WEST_SHAPE
        }
        return NORTH_SOUTH_SHAPE
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        return ActionResult.CONSUME
    }

    @Deprecated("Deprecated in Java")
    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory, _: PlayerEntity? ->
            RerollAltarScreenHandler(
                syncId,
                inventory,
                ScreenHandlerContext.create(world, pos)
            )
        }, AcText.translatable("screen.gearifiers.reroll_altar"))
    }

}