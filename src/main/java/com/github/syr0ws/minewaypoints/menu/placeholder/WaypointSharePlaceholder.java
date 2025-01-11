package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.model.WaypointShare;

public abstract class WaypointSharePlaceholder implements Placeholder {

    @Override
    public boolean accept(Context context) {
        return context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class);
    }

    protected WaypointShare getWaypointShare(Context context) {
        return context.getData(CommonContextKey.PAGINATED_DATA, WaypointShare.class);
    }
}
