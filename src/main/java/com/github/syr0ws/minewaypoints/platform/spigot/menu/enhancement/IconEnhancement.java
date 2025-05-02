package com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement;

import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import org.bukkit.Material;

public class IconEnhancement implements Enhancement<PaginationItemDto> {

    public static final String ICON_ENHANCEMENT_ID = "icon-enhancement";

    @Override
    public void enhance(PaginationItemDto dto, Context context) {
        Material material = context.getData(CommonContextKey.PAGINATED_DATA, Material.class);
        dto.getItem().setType(material);
    }

    @Override
    public Class<PaginationItemDto> getDTOClass() {
        return PaginationItemDto.class;
    }

    @Override
    public String getId() {
        return ICON_ENHANCEMENT_ID;
    }
}
