package hoo.hktranseta.main.kmb.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stop {
    // Json Data
    public Data data;
    public boolean result;

    public class Data {
        @SerializedName("CountSpecal")
        public Route route;

        public List<RouteStop> routeStops;
    }

    public class Route {
        public String route;
        public int bound;
        public int serviceType;
        public String lineGeometry; //TODO: Convert it to List<List<List<double>>> and display
    }

    public class RouteStop {
        @SerializedName("Route")
        public String route;

        @SerializedName("Bound")
        public int bound;

        @SerializedName("ServiceType")
        public int serviceType;

        @SerializedName("Seq")
        public int seq;

        @SerializedName("BSICode")
        public String bsiCode;

        @SerializedName("X")
        public double x;

        @SerializedName("Y")
        public double y;

        @SerializedName("AirFare")
        public double airFare;

        @SerializedName("CName")
        public String cName;

        @SerializedName("EName")
        public String eName;

        @SerializedName("CLocation")
        public String cLocation;

        @SerializedName("ELocation")
        public String eLocation;

        @SerializedName("Special")
        public String special;

        @SerializedName("BusType")
        public String busType;

        @SerializedName("Direction")
        public String direction;

        @SerializedName("Airport")
        public String airport;

        @SerializedName("Overnight")
        public String overnight;

        @SerializedName("Racecourse")
        public String racecourse;
    }

}
