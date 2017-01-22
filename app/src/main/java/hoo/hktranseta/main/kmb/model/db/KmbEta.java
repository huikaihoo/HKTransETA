package hoo.hktranseta.main.kmb.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import hoo.hktranseta.main.kmb.model.json.Eta;

@Entity(
        // Define indexes spanning multiple columns here.
        indexes = {
                @Index(value = "routeNo ASC, boundId ASC, serviceTypeId ASC, seq ASC, etaId ASC", unique = true)
        }
)
public class KmbEta {

    // Id For Database
    @Id(autoincrement = true)
    private Long id;

    // Indexes
    @NotNull
    private String routeNo;
    @NotNull
    private Integer boundId;
    @NotNull
    private Integer serviceTypeId;
    @NotNull
    private Integer seq;
    @NotNull
    private Integer etaId;

    // Other Fields
    private long etaTime;           // ETA in timestamp
    private String etaTimeMsg;      // whole ETA message from server
    private long busServiceTypeId;
    private String isScheduledOnly;
    private String wheelchair;
    private String eot;
    private String ol;

    private long generatedTime;     // retrieve record time from server
    private long expireTime;        // record expire time
    private long updatedTime;       // record last updated time

    private long updateTimestamp;

    public KmbEta (KmbRouteStop kmbRouteStop, Eta eta, int etaId, long updateTimestamp) {
        Eta.Response response = eta.data.response.get(etaId);

        this.routeNo = kmbRouteStop.getRouteNo();
        this.boundId = kmbRouteStop.getBoundId();
        this.serviceTypeId = kmbRouteStop.getServiceTypeId();
        this.seq = kmbRouteStop.getSeq();
        this.etaId = etaId;

        this.etaTime = 0;           // TODO: convert response.t to timestamp
        this.etaTimeMsg = response.t;
        this.busServiceTypeId = response.busServiceType;
        this.isScheduledOnly = response.ei;
        this.wheelchair = response.w;
        this.eot = response.eot;
        this.ol = response.ol;

        this.generatedTime = eta.data.generated / 1000L;
        this.expireTime = 0;        // TODO: convert response.ex to timestamp
        this.updatedTime = eta.data.updated / 1000L;

        this.updateTimestamp = updateTimestamp;
    }

    // Self define functions start
    private static int getTimeFromStr(String str) {
        int result = Integer.parseInt(str);
        return (result/100)*60 + result%100;
    }

    public int getTimeDiff(Date currentDate) {
        SimpleDateFormat etaTimeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
        Date etaDate;

        try {
            etaDate = etaTimeFormat.parse(etaTimeMsg);
        } catch (Exception e) {
            return -3000;
        }
        int etaTime = getTimeFromStr(timeFormat.format(etaDate));

        timeFormat.setTimeZone(TimeZone.getDefault());
        int currentTime = getTimeFromStr(timeFormat.format(currentDate));

        int result = etaTime - currentTime;

        result = (result > 720) ? result - 1440 : result;
        result = (result < -720) ? result + 1440 : result;
        result = (result > 180) ? -121 : result;

        return result;
    }

    public String getPhasedEtaTimeMsg(Date currentDate) {
        int diff = getTimeDiff(currentDate);

        return etaTimeMsg.replaceAll("　", " ")
                .replaceAll("班次", "")
                .replaceAll("時段", "")
                .replaceAll("九巴預定", "九巴 預定")
                + ((diff > 0) ? (" (" +  diff + "分鐘)") : "");
    }
    // Self define functions end

@Generated(hash = 1333709834)
public KmbEta(Long id, @NotNull String routeNo, @NotNull Integer boundId, @NotNull Integer serviceTypeId,
        @NotNull Integer seq, @NotNull Integer etaId, long etaTime, String etaTimeMsg, long busServiceTypeId,
        String isScheduledOnly, String wheelchair, String eot, String ol, long generatedTime, long expireTime,
        long updatedTime, long updateTimestamp) {
    this.id = id;
    this.routeNo = routeNo;
    this.boundId = boundId;
    this.serviceTypeId = serviceTypeId;
    this.seq = seq;
    this.etaId = etaId;
    this.etaTime = etaTime;
    this.etaTimeMsg = etaTimeMsg;
    this.busServiceTypeId = busServiceTypeId;
    this.isScheduledOnly = isScheduledOnly;
    this.wheelchair = wheelchair;
    this.eot = eot;
    this.ol = ol;
    this.generatedTime = generatedTime;
    this.expireTime = expireTime;
    this.updatedTime = updatedTime;
    this.updateTimestamp = updateTimestamp;
}

@Generated(hash = 1539972404)
public KmbEta() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getRouteNo() {
    return this.routeNo;
}

public void setRouteNo(String routeNo) {
    this.routeNo = routeNo;
}

public Integer getBoundId() {
    return this.boundId;
}

public void setBoundId(Integer boundId) {
    this.boundId = boundId;
}

public Integer getServiceTypeId() {
    return this.serviceTypeId;
}

public void setServiceTypeId(Integer serviceTypeId) {
    this.serviceTypeId = serviceTypeId;
}

public Integer getSeq() {
    return this.seq;
}

public void setSeq(Integer seq) {
    this.seq = seq;
}

public Integer getEtaId() {
    return this.etaId;
}

public void setEtaId(Integer etaId) {
    this.etaId = etaId;
}

public long getEtaTime() {
    return this.etaTime;
}

public void setEtaTime(long etaTime) {
    this.etaTime = etaTime;
}

public String getEtaTimeMsg() {
    return this.etaTimeMsg;
}

public void setEtaTimeMsg(String etaTimeMsg) {
    this.etaTimeMsg = etaTimeMsg;
}

public long getBusServiceTypeId() {
    return this.busServiceTypeId;
}

public void setBusServiceTypeId(long busServiceTypeId) {
    this.busServiceTypeId = busServiceTypeId;
}

public String getIsScheduledOnly() {
    return this.isScheduledOnly;
}

public void setIsScheduledOnly(String isScheduledOnly) {
    this.isScheduledOnly = isScheduledOnly;
}

public String getWheelchair() {
    return this.wheelchair;
}

public void setWheelchair(String wheelchair) {
    this.wheelchair = wheelchair;
}

public String getEot() {
    return this.eot;
}

public void setEot(String eot) {
    this.eot = eot;
}

public String getOl() {
    return this.ol;
}

public void setOl(String ol) {
    this.ol = ol;
}

public long getGeneratedTime() {
    return this.generatedTime;
}

public void setGeneratedTime(long generatedTime) {
    this.generatedTime = generatedTime;
}

public long getExpireTime() {
    return this.expireTime;
}

public void setExpireTime(long expireTime) {
    this.expireTime = expireTime;
}

public long getUpdatedTime() {
    return this.updatedTime;
}

public void setUpdatedTime(long updatedTime) {
    this.updatedTime = updatedTime;
}

public long getUpdateTimestamp() {
    return this.updateTimestamp;
}

public void setUpdateTimestamp(long updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
}
}
