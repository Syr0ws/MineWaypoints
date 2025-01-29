package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.menu.enhancement.IconEnhancement;
import com.github.syr0ws.minewaypoints.menu.hook.WaypointInitStoreHook;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class WaypointIconsMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "waypoint-icons-menu";
    public static final String ICONS_PAGINATION_ID = "icons-pagination";

    private static final String MENU_CONFIG_PATH = "menus/waypoint-icons-menu.yml";

    public WaypointIconsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        super(plugin, inventoryConfigDAO);
    }

    @Override
    public void addProviders(ProviderManager manager) {

        List<Material> icons = Arrays.stream(Material.values())
                .filter(material -> material.isItem() && !material.isAir())
                .toList();

        manager.addProvider(new PaginationProvider<>(ICONS_PAGINATION_ID, Material.class, (inventory, pagination) -> icons));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
    }

    @Override
    public void addEnhancements(EnhancementManager manager) {
        manager.addEnhancement(DtoNameEnum.PAGINATION_ITEM.name(), new IconEnhancement());
    }

    @Override
    public void addHooks(HookManager manager) {
        manager.addHook(WaypointInitStoreHook.HOOK_ID, CraftVentoryBeforeOpenEvent.class, new WaypointInitStoreHook());
    }

    @Override
    public String getInventoryResourceFile() {
        return MENU_CONFIG_PATH;
    }

    @Override
    public Path getInventoryConfigFile() {
        return Paths.get(super.getPlugin().getDataFolder() + File.separator + MENU_CONFIG_PATH);
    }

    @Override
    public String getInventoryId() {
        return MENU_ID;
    }
}
