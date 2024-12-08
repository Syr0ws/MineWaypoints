package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.dto.DTO;
import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.i18n.I18n;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.CommonInventoryProvider;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class WaypointIconsMenuProvider extends CommonInventoryProvider {

    public static final String MENU_ID = "waypoint-icons-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoint-icons-menu.yml";

    public WaypointIconsMenuProvider(I18n i18n, Plugin plugin, InventoryConfigDAO dao) {
        super(i18n, plugin, dao);
    }

    @Override
    public void loadConfig() {
        super.getPlugin().saveResource(MENU_CONFIG_PATH, false);
        super.loadConfig();
    }

    @Override
    protected Path getInventoryConfigFile() {
        return Paths.get(super.getPlugin().getDataFolder() + "/" + MENU_CONFIG_PATH);
    }

    @Override
    protected void addPaginationProviders() {

        List<Material> icons = Arrays.stream(Material.values())
                .filter(material -> material.isItem() && !material.isAir())
                .toList();

        super.addPaginationProvider("icons-pagination", Material.class, () -> icons);
    }

    @Override
    protected void addEnhancements(EnhancementManager manager) {

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
    protected void addHooks(HookManager hookManager) {

    }

    @Override
    public String getId() {
        return MENU_ID;
    }
}
