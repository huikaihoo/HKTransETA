package hoo.hktranseta.main.gov.worker;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hoo.hktranseta.common.Utils;
import hoo.hktranseta.main.gov.model.GovBusRoute;

public class GovDataConverter {

    private static final String TAG = "GovDataConverter";

    private static final String RECORD_SEPARATOR = "\\|\\*\\|";
    private static final String FIELD_SEPARATOR = "\\|\\|";

    public static List<GovBusRoute> toGovBusRouteList(String input) {
        long timestamp = Utils.getCurrentTimestamp();

        List<GovBusRoute> result = new ArrayList<>();
        String[] busRoutList = input.split(RECORD_SEPARATOR);
        Log.d(TAG, "busRoutList.length=[" + busRoutList.length + "]");

        for (String busRoute : busRoutList) {
            String[] busRouteFields = busRoute.split(FIELD_SEPARATOR, -1);
            GovBusRoute govBusRoute = new GovBusRoute(busRouteFields, timestamp);
            result.add(govBusRoute);
        }
        return result;
    }

    public static List<GovBusRoute> filterDuplicateRoute(List<GovBusRoute> govBusRouteList) {
        List<GovBusRoute> result = new ArrayList<>();
        Map<String, List<GovBusRoute>> map = new LinkedHashMap<>();

        // Group GovBusRoute based on RouteNo
        for (GovBusRoute govBusRoute : govBusRouteList) {
            List<GovBusRoute> govBusRoutes;
            String routeNo = govBusRoute.getRouteNo();

            if (map.containsKey(routeNo)) {
                govBusRoutes = map.get(routeNo);
            } else {
                govBusRoutes = new ArrayList<>();
            }
            govBusRoutes.add(govBusRoute);
            map.put(routeNo, govBusRoutes);
        }

        for (List<GovBusRoute> govBusRoutes : map.values()) {
            if (govBusRoutes.size() == 1) {
                // One Record only
                result.add(govBusRoutes.get(0));
            } else if (govBusRoutes.size() > 1) {
                // Multiple Records
                int selected = -1;
                // Find the Non-Special Route
                for (int i=0; i<govBusRoutes.size(); i++) {
                    if (govBusRoutes.get(i).getSpecial() == 0) {
                        selected = i;
                        break;
                    }
                }
                if (selected >= 0) {
                    result.add(govBusRoutes.get(selected));
                    continue;
                }
                // Find Route with BoundCnt > 1
                for (int i=0; i<govBusRoutes.size(); i++) {
                    if (govBusRoutes.get(i).getBoundCnt() > 1) {
                        selected = i;
                        break;
                    }
                }
                if (selected >= 0) {
                    result.add(govBusRoutes.get(selected));
                    continue;
                }
                // Find Circular Route
                for (int i=0; i<govBusRoutes.size(); i++) {
                    if (govBusRoutes.get(i).getCircular() == 1) {
                        selected = i;
                        break;
                    }
                }
                if (selected >= 0) {
                    result.add(govBusRoutes.get(selected));
                    continue;
                }
                // Special Handle (multiple one way non-circular special routes: merge to one)
                int newBoundCnt = 1;
                String newLocationFrom = govBusRoutes.get(0).getLocationFrom();
                String newLocationTo = govBusRoutes.get(0).getLocationTo();
                for (int i=1; i<govBusRoutes.size(); i++) {
                    if (newLocationFrom.equals(govBusRoutes.get(i).getLocationFrom())) {
                        if (!newLocationTo.equals(govBusRoutes.get(i).getLocationTo())) {
                            newLocationTo += (" / " + govBusRoutes.get(i).getLocationTo());
                        }
                    } else if (newLocationTo.equals(govBusRoutes.get(i).getLocationTo())) {
                        newLocationFrom += (" / " + govBusRoutes.get(i).getLocationFrom());
                    } else if (newLocationFrom.equals(govBusRoutes.get(i).getLocationTo())) {
                        newBoundCnt++;
                        if (!newLocationTo.equals(govBusRoutes.get(i).getLocationFrom())) {
                            newLocationTo += (" / " + govBusRoutes.get(i).getLocationFrom());
                        }
                    } else if (newLocationTo.equals(govBusRoutes.get(i).getLocationFrom())) {
                        newBoundCnt++;
                        newLocationFrom += (" / " + govBusRoutes.get(i).getLocationTo());
                    }
                }
                govBusRoutes.get(0).setBoundCnt(newBoundCnt);
                govBusRoutes.get(0).setLocationFrom(newLocationFrom);
                govBusRoutes.get(0).setLocationTo(newLocationTo);
                result.add(govBusRoutes.get(0));
            }
        }
        return result;
    }
}
