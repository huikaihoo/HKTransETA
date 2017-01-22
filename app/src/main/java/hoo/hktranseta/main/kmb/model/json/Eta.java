package hoo.hktranseta.main.kmb.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Eta {
    // Json Data
    public Data data;
    public boolean result;

    public class Data {
        public long updated;
        public long generated;
        @SerializedName("responsecode")
        public int responseCode;

        public List<Response> response;
    }

    public class Response {
        @SerializedName("bus_service_type")
        public int busServiceType;
        public String t;        // time (hh:mm xxxx)
        public String ei;       // Is Scheduled Only (Y/N)
        public String w;        // wheelchair (Y/N)
        public String eot;      // E: time only; T: with text
        public String ol;       // (N)
        public String ex;       // expire time (YYYY-MM-DD hh:mm:ss)
    }
}
