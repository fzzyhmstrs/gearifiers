# Imbuing Automation Tool

### This is a tool for use with [Amethyst Imbuement](https://www.curseforge.com/minecraft/mc-mods/amethyst-imbuement), and requires *AI* to be included in order to function.

A tool for modpack developers or other users that want imbuing recipes for all vanilla style enchantments that don't already have an imbuing recipe. Recipes are generated via command and saved to the config folder, where the user can modify them and move them to a datapack as needed.

Recipe generation is semi-randomized, with general selections made based on the enchantment targets as well as the level requirements of the enchantment.

Imbuing works iteratively, so this tool will only generate recipes for level 1 enchantments, and the player must imbue X times to achieve *Enchant X*. This process is linear, rather than by powers of 2, so Sharpness 5 would be achieved with 5 imbues rather than the 16 Sharpness 1 books you would have to combine in an anvil to achieve the same effect. Keep this in mind when tweaking costs.

### Recipe Tweaking Guide

Imbuing recipes are based on the magic of imbuing objects with the properties of other objects and crystals. As such, the items in the recipe are meant to evoke the qualities of the end result, or have some physical or metaphysical connection to the improvement being made.

For imbuing gems, the basic structure is gems in the crafting grid, and components (typically non-gem) in the imbuing slots (the outer 4 slots).

For imbuing objects, the recipe is usually reversed, with components in the grid, and crystals in the imbuing slots.

The *Amethyst Imbuement* in-game guide book (the "Glistering Tome") contains brief summaries of each gems qualities, including their primary focus type, Fury, Wit, or Grace. I recommend you attempt to follow these qualities when tweaking recipes, as it maintains the consistency of the magical flavors each gem is supposed to provide. The generator attempts to maintain this consistency based on enchantment target, but may need some help.

**Fury**: Magic of anger, violence, and warfare. Use with damage enchantments or enchantments that grant harmful statuses to the target, summon allies in battle, etc.

**Wit**: Magic of guile, trickery, and creativity. Use with enchantments that provide unique effects such as teleportation, item drops, experience modififcation, etc.

**Grace**: Magic of healing, protection, and growth. Use with enchantments that provide defensive or healing buffs, or do something like speed up crop growth or summon helpful creatures (non-aggressive).

|Tier|Fury|Wit|Grace|Any|
|----|----|---|-----|---|
|1|Amethyst Shard|Danburite|Imbued Quartz|x|
|1|Citrine|Imbued Lapis|Smoky Quartz|x|
|2|Garnet|Pyrite|Moonstone|Opal|
|3|x|x|x|Ametrine|
|4|x|x|x|Celestine|
