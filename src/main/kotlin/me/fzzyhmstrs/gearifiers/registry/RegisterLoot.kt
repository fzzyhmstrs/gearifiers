package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.fzzy_core.item_util.AbstractModLoot
import me.fzzyhmstrs.fzzy_core.registry.LootRegistry
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.entity.EntityType
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object RegisterLoot {

    fun registerAll(){
        loot()
    }

    /*
    * Repair Kit: village armorsmith, underwater ruin, ship
    * Awakening: desert pyramid, ship, underwater ruin, end city
    * Chaos: nether fortress, bastion, ruined portal
    * Cleansing: ship, ruined portal, desert pyramid, nether fortress
    * Legends: end city, ancient city, wither drop
    * Transfer: end city, wither drop, buried treasure
    * Whetstone: village weaponsmith, ruined portal, bastion
    * */

    /*
    * Desert Pyramid: Awakening, Cleansing
    * Armorsmith: Repair Kit
    * Underwater Ruin: Repair Kit, Awakening
    * Ship: Repair Kit, Awakening, Cleansing
    * End City: Awakening, Legends, Transfer
    * Nether Fortress: Chaos, Cleansing
    * Bastion: Chaos, Whetstone
    * Ruined Portal: Chaos, Cleansing, Whetstone
    * Wither: Legends, Transfer
    * Ancient City: Legends
    * Buried Treasure: Transfer
    * */

    private val common = { RandomChanceLootCondition.builder(GearifiersConfig.chances.commonLoot) }
    private val uncommon = { RandomChanceLootCondition.builder(GearifiersConfig.chances.uncommonLoot) }
    private val rare = { RandomChanceLootCondition.builder(GearifiersConfig.chances.rareLoot) }
    private val epic = { RandomChanceLootCondition.builder(GearifiersConfig.chances.epicLoot) }

    private fun loot() {
        LootTableEvents.MODIFY.register { _, _, id, table, _ ->
            if (LootTables.DESERT_PYRAMID_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                    .conditionally(common())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_CLEANSING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.VILLAGE_ARMORER_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(uncommon())
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.VILLAGE_WEAPONSMITH_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(uncommon())
                    .with(ItemEntry.builder(RegisterItem.WHETSTONE).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.UNDERWATER_RUIN_BIG_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(common())
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(2))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(2))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.UNDERWATER_RUIN_SMALL_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(rare())
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(4))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.SHIPWRECK_SUPPLY_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(common())
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_CLEANSING).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(common())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(3))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_LEGENDS).weight(2))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_TRANSFERAL).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.NETHER_BRIDGE_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                    .conditionally(common())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(4))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_CLEANSING).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.BASTION_BRIDGE_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(uncommon())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(4))
                    .with(ItemEntry.builder(RegisterItem.WHETSTONE).weight(2))
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.BASTION_OTHER_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(rare())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(4))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(2))
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.BASTION_TREASURE_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(epic())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_LEGENDS).weight(9))
                    .with(ItemEntry.builder(RegisterItem.WHETSTONE).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.STRONGHOLD_CROSSING_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(uncommon())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_AWAKENING).weight(8))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_CLEANSING).weight(10))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(10))
                    .with(ItemEntry.builder(RegisterItem.WHETSTONE).weight(5))
                    .with(ItemEntry.builder(RegisterItem.REPAIR_KIT).weight(5))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_LEGENDS).weight(2))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_TRANSFERAL).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.RUINED_PORTAL_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(uncommon())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_FATE).weight(1))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_CLEANSING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.WHETSTONE).weight(2))
                table.pool(poolBuilder)
            } else if (EntityType.WITHER.lootTableId.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(rare())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_LEGENDS).weight(3))
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_TRANSFERAL).weight(2))
                table.pool(poolBuilder)
            } else if (LootTables.ANCIENT_CITY_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(epic())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_TRANSFERAL).weight(1))
                table.pool(poolBuilder)
            } else if (LootTables.BURIED_TREASURE_CHEST.equals(id)) {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(epic())
                    .with(ItemEntry.builder(RegisterItem.SEAL_OF_LEGENDS).weight(1))
                table.pool(poolBuilder)
            }
        }
    }

}

