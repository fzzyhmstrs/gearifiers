package me.fzzyhmstrs.gearifiers

import me.fzzyhmstrs.fzzy_core.coding_util.FzzyBlockSettings
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.block.RerollAltarBlock
import me.fzzyhmstrs.gearifiers.compat.ClientItemCostLoader
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.modifier.ModifierCommand
import me.fzzyhmstrs.gearifiers.registry.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.block.MapColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object Gearifiers: ModInitializer {
    const val MOD_ID = "gearifiers"
    val LOGGER: Logger = LoggerFactory.getLogger("gearifiers")
    internal val COST_MAP_SYNC = Identifier(MOD_ID,"cost_map_sync")

    val REROLL_ALTAR = RerollAltarBlock(FzzyBlockSettings.basic().mapColor(MapColor.DARK_RED).requiresTool().strength(1.5f, 6.0f))

    override fun onInitialize() {

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val buf = PacketByteBufs.create()
            ItemCostLoader.writeRawDataToClient(buf)
            ServerPlayNetworking.send(handler.player,COST_MAP_SYNC,buf)
        }

        FzzyPort.BLOCK.register(Identifier(MOD_ID, "reroll_altar"), REROLL_ALTAR)
        FzzyPort.ITEM.register(Identifier(MOD_ID,"reroll_altar"), BlockItem(REROLL_ALTAR, FabricItemSettings()))

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register(ModifyEntries { entries: FabricItemGroupEntries ->
                entries.add(
                    REROLL_ALTAR.asItem()
                )
            })
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register(ModifyEntries { entries: FabricItemGroupEntries ->
                entries.addAll(RegisterItem.regItem.stream().map { item -> ItemStack(item) }.toList())
            })

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemCostLoader)

        RegisterHandler.registerAll()
        RegisterModifier.registerAll()
        RegisterItem.registerAll()
        RegisterLoot.registerAll()
        ModifierCommand.registerAll()
    }
}

object GearifiersClient:ClientModInitializer{

    fun getPlayer(): PlayerEntity?{
        return MinecraftClient.getInstance().player
    }

    override fun onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(Gearifiers.COST_MAP_SYNC){ _, _, buf, _ ->
            ClientItemCostLoader.readRawDataFromServer(buf)
        }

        BlockRenderLayerMap.INSTANCE.putBlock(Gearifiers.REROLL_ALTAR, RenderLayer.getCutout())

        RegisterScreen.registerAll()
    }

}
