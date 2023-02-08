package me.fzzyhmstrs.gearifiers

import me.fzzyhmstrs.gearifiers.block.RerollAltarBlock
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.registry.RegisterHandler
import me.fzzyhmstrs.gearifiers.registry.RegisterModifier
import me.fzzyhmstrs.gearifiers.registry.RegisterScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object Gearifiers: ModInitializer {
    const val MOD_ID = "gearifiers"
    val LOGGER: Logger = LoggerFactory.getLogger("gearifiers")

    val REROLL_ALTAR = RerollAltarBlock(FabricBlockSettings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(1.5f, 6.0f))

    override fun onInitialize() {

        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "reroll_altar"), REROLL_ALTAR)
        Registry.register(Registry.ITEM, Identifier(MOD_ID,"reroll_altar"), BlockItem(REROLL_ALTAR, FabricItemSettings().group(ItemGroup.MISC)))

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemCostLoader)

        RegisterHandler.registerAll()
        RegisterModifier.registerAll()
    }
}

object GearifiersClient:ClientModInitializer{

    override fun onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(Gearifiers.REROLL_ALTAR, RenderLayer.getCutout())

        RegisterScreen.registerAll()
    }

}