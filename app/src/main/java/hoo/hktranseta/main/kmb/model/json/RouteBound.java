package hoo.hktranseta.main.kmb.model.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.TreeSet;

public class RouteBound {
    // Json Data
    public List<Data> data;
    public boolean result;

    // Custom Data
    private TreeSet<Integer> boundList = new TreeSet<>();

    public class Data {
        @SerializedName("SERVICE_TYPE")
        public int serviceType;

        @SerializedName("BOUND")
        public int bound;

        @SerializedName("ROUTE")
        public String route;    // route No
    }

    public TreeSet<Integer> getBoundList() {
        if (boundList.size() == 0) {
            for (Data d : data) {
                boundList.add(d.bound);
            }
        }
        return boundList;
    }
}
