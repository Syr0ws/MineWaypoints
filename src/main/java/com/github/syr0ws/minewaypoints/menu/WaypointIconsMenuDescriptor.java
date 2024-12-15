package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointNamePlaceholder;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class WaypointIconsMenuDescriptor implements InventoryDescriptor {

    public static final String MENU_ID = "waypoint-icons-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoint-icons-menu.yml";

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;

    public WaypointIconsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        this.plugin = plugin;
        this.inventoryConfigDAO = inventoryConfigDAO;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        List<Material> icons = Arrays.stream(Material.values())
                .filter(material -> material.isItem() && !material.isAir())
                .toList();

        manager.addProvider(new PaginationProvider<>(
                "icons-pagination", Material.class, (inventory) -> icons)
        );
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        manager.addPlaceholder(new WaypointNamePlaceholder());
    }

    @Override
    public void addEnhancements(EnhancementManager manager) {

        manager.addEnhancement(DtoNameEnum.PAGINATION_ITEM.name(), new Enhancement<PaginationItemDto>() {

            @Override
            public void enhance(PaginationItemDto dto, Context context) {
                Material material = context.getData(CommonContextKey.PAGINATION_ITEM.name(), Material.class);
                dto.getItem().setType(material);
            }

            @Override
            public Class<PaginationItemDto> getDTOClass() {
                return PaginationItemDto.class;
            }

            @Override
            public String getId() {
                return "icon-enhancement";
            }
        });
    }

    @Override
    public void addHooks(HookManager manager) {

        manager.addHook("move-data", CraftVentoryBeforeOpenEvent.class, event -> {

            CraftVentory inventory = event.getInventory();
            InventoryViewer viewer = event.getViewer();
            DataStore sharedStore = viewer.getViewManager().getSharedStore();

            Waypoint waypoint = sharedStore.getData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class)
                    .orElseThrow(() -> new NullPointerException("No waypoint found"));

            inventory.getLocalStore().setData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class, waypoint);
        });
    }

    @Override
    public String getInventoryResourceFile() {
        return MENU_CONFIG_PATH;
    }

    @Override
    public Path getInventoryConfigFile() {
        return Paths.get(this.plugin.getDataFolder() + File.separator + MENU_CONFIG_PATH);
    }

    @Override
    public String getInventoryId() {
        return MENU_ID;
    }

    @Override
    public InventoryConfigDAO getInventoryConfigDAO() {
        return this.inventoryConfigDAO;
    }
}
