package hoo.hktranseta.main.kmb.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpecialRoute {
    // Json Data
    public Data data;
    public boolean result;

    public class Data {
        @SerializedName("CountSpecal")
        public int countSpecial;

        public List<Route> routes;
    }

    public class Route {
        @SerializedName("Route")
        public String route;

        @SerializedName("Bound")
        public int bound;

        @SerializedName("ServiceType")
        public String serviceType;  // Need to trim spaces and convert to int

        @SerializedName("Desc_CHI")
        public String descChi;

        @SerializedName("Desc_ENG")
        public String descEng;

        @SerializedName("Origin_CHI")
        public String originChi;

        @SerializedName("Origin_ENG")
        public String originEng;

        @SerializedName("Destination_CHI")
        public String destinationChi;

        @SerializedName("Destination_ENG")
        public String destinationEng;

        @SerializedName("From_weekday")
        public int fromWeekDay;

        @SerializedName("To_weekday")
        public int toWeekDay;

        @SerializedName("From_saturday")
        public int fromSaturday;

        @SerializedName("To_saturday")
        public int toSaturday;

        @SerializedName("From_holiday")
        public int fromHoliday;

        @SerializedName("To_holiday")
        public int toHoliday;
    }

}
