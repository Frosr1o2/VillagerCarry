# VillagerCarry 1.0.0

Paper 1.21.11 plugin.

- Shift + right-click villagers, wandering traders and zombie villagers.
- Saves entity NBT and restores it when the special head is placed.
- Uses supplied villager/trader/zombie-villager textures.
- Localized ActionBar messages: English, Russian, German, French, Spanish, Ukrainian, Polish.
- Special heads cannot be equipped through normal inventory interaction, Shift-click, drag, or 1-9 hotbar swaps.
- Uses PlayerInteractEntityEvent for entity pickup and the correct 1.21.11 ValueInput/TagValueInput + TagValueOutput API.
- Removes saved UUID before restoring a newly spawned entity.

# Build

```
cd ~/your/path/to/villagercarry
```
clean build with refresh dependencies
```
gradle clean build --refresh-dependencies
```
jar file build in /build/libs/
