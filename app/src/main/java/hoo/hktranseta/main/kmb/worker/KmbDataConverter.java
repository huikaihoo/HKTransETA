package hoo.hktranseta.main.kmb.worker;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.main.kmb.model.db.KmbEta;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;
import hoo.hktranseta.main.kmb.model.json.Eta;
import hoo.hktranseta.main.kmb.model.json.SpecialRoute;
import hoo.hktranseta.main.kmb.model.json.Stop;

public class KmbDataConverter {

    private static final String TAG = "KmbDataConverter";

    private static long getCurrentTimestamp () {
        return System.currentTimeMillis() / 1000L;
    }

    public static List<KmbServiceType> toKmbServiceTypeList(SpecialRoute specialRoute) {
        long timestamp = getCurrentTimestamp();

        List<KmbServiceType> result = new ArrayList<>();
        if (specialRoute.data != null){
            for (SpecialRoute.Route route : specialRoute.data.routes) {
                KmbServiceType kmbServiceType = new KmbServiceType(route, timestamp);
                result.add(kmbServiceType);
            }
        }
        return result;
    }

    public static List<KmbRouteStop> toKmbStopList(Stop stop) {
        long timestamp = getCurrentTimestamp();

        List<KmbRouteStop> result = new ArrayList<>();
        if (stop.data != null){
            for (Stop.RouteStop routeStop : stop.data.routeStops) {
                KmbRouteStop kmbRouteStop = new KmbRouteStop(routeStop, timestamp);
                result.add(kmbRouteStop);
            }
        }
        return result;
    }

    public static List<KmbEta> toKmbEta(KmbRouteStop kmbRouteStop, Eta eta) {
        long timestamp = getCurrentTimestamp();

        List<KmbEta> result = new ArrayList<>();
        if (eta.data != null) {
            for (int i=0; i <eta.data.response.size(); i++) {
                KmbEta kmbEta = new KmbEta(kmbRouteStop, eta, i, timestamp);
                result.add(kmbEta);
            }
        }
        return result;
    }
}
