package hoo.hktranseta.main.gov.model;

import android.util.Log;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import hoo.hktranseta.R;
import hoo.hktranseta.common.BaseApplication;

@Entity(
        // Define indexes spanning multiple columns here.
        indexes = {
                @Index(value = "id ASC")
                //@Index(value = "routeNo ASC, routeTypeId ASC, routeType1 ASC, routeType2 ASC, special ASC, boundCnt DESC, circular DESC, routeId ASC")
        }
)
public class GovBusRoute {

    // Id For Database
    @Id(autoincrement = true)
    private Long id;

    // Indexes
    @NotNull
    private String routeId;
    @NotNull
    private String routeNo;
    @NotNull
    private String routeType1;      // KMB/LWB/NWFB/CTB/NLB/GMB/LRTFeeder/DB/PI
    @NotNull
    private String routeType2;      // LWB/NWFB/HKI/KLN/NT
    @NotNull
    private Integer routeTypeId;    // 1: Bus; 2: MiniBus
    @NotNull
    private Integer boundCnt;
    @NotNull
    private Integer circular;       // 0: Not Circular; 1: Circular
    @NotNull
    private Integer special;        // 0: Not Special; 1: Special Time; 2: Special Price

    // Other Fields
    private String locationFrom;
    private String locationTo;
    private String routeTypeDesc;
    private String url1;
    private String url2;
    private double fare;
    private String specialMsg1;
    private String specialMsg2;
    private String specialMsg3;

    private long updateTimestamp;

    public GovBusRoute(String[] fields, long updateTimestamp) {
        if (fields.length < 16){
            Log.e("GovBusRoute", "Some fields missing; fields.length=[" + fields.length + "]; fields=[" + fields + "]");
            return;
        }
        routeId = fields[0];
        routeNo = fields[1];
        String[] routeTypeList = fields[15].split("\\+", -1);
        if (routeTypeList.length == 2) {
            routeType1 = routeTypeList[0];
            routeType2 = routeTypeList[1];
        } else {
            routeType1 = fields[15];
            routeType2 = fields[10];
        }
        routeTypeId = Integer.valueOf(fields[5]);
        boundCnt = Integer.valueOf(fields[6]);
        circular = Integer.valueOf(fields[7]);
        special = Integer.valueOf(fields[9]);
        locationFrom = fields[2];
        locationTo = fields[3];
        routeTypeDesc = fields[4];
        String[] urlList = fields[8].split("\\|", -1);
        if (urlList.length == 2) {
            url1 = urlList[0];
            url2 = urlList[1];
        } else {
            url1 = fields[8];
            url2 = "";
        }
        fare = Double.valueOf(fields[11]);
        specialMsg1 = fields[12];
        specialMsg2 = fields[13];
        specialMsg3 = fields[14];
        this.updateTimestamp = updateTimestamp;
    }

    // Self define functions start
    public String getPhasedDesc() {
        String company = getRouteTypeDesc().replaceAll("巴士", "");
        String desc = "";
        if (getRouteNo().startsWith("N")) {
            desc += BaseApplication.getContext().getResources().getString(R.string.overnight);
        }
        if (getRouteNo().startsWith("A") || getRouteNo().startsWith("NA")) {
            if (!desc.isEmpty()){
                desc += BaseApplication.getContext().getResources().getString(R.string.space_en_only);
            }
            desc += BaseApplication.getContext().getResources().getString(R.string.airport);
        }
        String str = getRouteNo().replaceAll("[^\\d.]", "");
        if (str.length() == 3 && (str.startsWith("1")||str.startsWith("3")||str.startsWith("6")||str.startsWith("9"))) {
            if (!desc.isEmpty()){
                desc += BaseApplication.getContext().getResources().getString(R.string.space_en_only);
            }
            desc += BaseApplication.getContext().getResources().getString(R.string.cross_harbor);
        }
        if (getSpecial() == 1) {
            if (!desc.isEmpty()){
                desc += BaseApplication.getContext().getResources().getString(R.string.space_en_only);
            }
            desc += BaseApplication.getContext().getResources().getString(R.string.special);
        }
        if (!desc.isEmpty()) {
            desc += BaseApplication.getContext().getResources().getString(R.string.line_zh_only);
            if (!company.isEmpty()) {
                company += " ";
            }
        }

        return company + desc;
    }
    // Self define functions end

@Generated(hash = 2025494691)
public GovBusRoute(Long id, @NotNull String routeId, @NotNull String routeNo, @NotNull String routeType1, @NotNull String routeType2,
        @NotNull Integer routeTypeId, @NotNull Integer boundCnt, @NotNull Integer circular, @NotNull Integer special, String locationFrom,
        String locationTo, String routeTypeDesc, String url1, String url2, double fare, String specialMsg1, String specialMsg2, String specialMsg3,
        long updateTimestamp) {
    this.id = id;
    this.routeId = routeId;
    this.routeNo = routeNo;
    this.routeType1 = routeType1;
    this.routeType2 = routeType2;
    this.routeTypeId = routeTypeId;
    this.boundCnt = boundCnt;
    this.circular = circular;
    this.special = special;
    this.locationFrom = locationFrom;
    this.locationTo = locationTo;
    this.routeTypeDesc = routeTypeDesc;
    this.url1 = url1;
    this.url2 = url2;
    this.fare = fare;
    this.specialMsg1 = specialMsg1;
    this.specialMsg2 = specialMsg2;
    this.specialMsg3 = specialMsg3;
    this.updateTimestamp = updateTimestamp;
}

@Generated(hash = 279431455)
public GovBusRoute() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getRouteId() {
    return this.routeId;
}

public void setRouteId(String routeId) {
    this.routeId = routeId;
}

public String getRouteNo() {
    return this.routeNo;
}

public void setRouteNo(String routeNo) {
    this.routeNo = routeNo;
}

public String getRouteType1() {
    return this.routeType1;
}

public void setRouteType1(String routeType1) {
    this.routeType1 = routeType1;
}

public String getRouteType2() {
    return this.routeType2;
}

public void setRouteType2(String routeType2) {
    this.routeType2 = routeType2;
}

public Integer getRouteTypeId() {
    return this.routeTypeId;
}

public void setRouteTypeId(Integer routeTypeId) {
    this.routeTypeId = routeTypeId;
}

public Integer getBoundCnt() {
    return this.boundCnt;
}

public void setBoundCnt(Integer boundCnt) {
    this.boundCnt = boundCnt;
}

public Integer getCircular() {
    return this.circular;
}

public void setCircular(Integer circular) {
    this.circular = circular;
}

public Integer getSpecial() {
    return this.special;
}

public void setSpecial(Integer special) {
    this.special = special;
}

public String getLocationFrom() {
    return this.locationFrom;
}

public void setLocationFrom(String locationFrom) {
    this.locationFrom = locationFrom;
}

public String getLocationTo() {
    return this.locationTo;
}

public void setLocationTo(String locationTo) {
    this.locationTo = locationTo;
}

public String getRouteTypeDesc() {
    return this.routeTypeDesc;
}

public void setRouteTypeDesc(String routeTypeDesc) {
    this.routeTypeDesc = routeTypeDesc;
}

public String getUrl1() {
    return this.url1;
}

public void setUrl1(String url1) {
    this.url1 = url1;
}

public String getUrl2() {
    return this.url2;
}

public void setUrl2(String url2) {
    this.url2 = url2;
}

public double getFare() {
    return this.fare;
}

public void setFare(double fare) {
    this.fare = fare;
}

public String getSpecialMsg1() {
    return this.specialMsg1;
}

public void setSpecialMsg1(String specialMsg1) {
    this.specialMsg1 = specialMsg1;
}

public String getSpecialMsg2() {
    return this.specialMsg2;
}

public void setSpecialMsg2(String specialMsg2) {
    this.specialMsg2 = specialMsg2;
}

public String getSpecialMsg3() {
    return this.specialMsg3;
}

public void setSpecialMsg3(String specialMsg3) {
    this.specialMsg3 = specialMsg3;
}

public long getUpdateTimestamp() {
    return this.updateTimestamp;
}

public void setUpdateTimestamp(long updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
}
}
