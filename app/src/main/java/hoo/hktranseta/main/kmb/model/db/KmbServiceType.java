package hoo.hktranseta.main.kmb.model.db;

import android.util.Log;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import hoo.hktranseta.main.kmb.model.json.SpecialRoute;

@Entity(
        // Define indexes spanning multiple columns here.
        indexes = {
                @Index(value = "routeNo ASC, boundId ASC, serviceTypeId ASC", unique = true)
        }
)
public class KmbServiceType {

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

    // Other Fields
    private String desc;
    private String descEN;
    private String locationFrom;
    private String locationFromEN;
    private String locationTo;
    private String locationToEN;
    private int normalStartTm;
    private int normalEndTm;
    private int satStartTm;
    private int satEndTm;
    private int holidayStartTm;
    private int holidayEndTm;

    private long updateTimestamp;

    // Relation with KmbRouteStop
    @ToMany(joinProperties = {
            @JoinProperty(name = "routeNo", referencedName = "routeNo"),
            @JoinProperty(name = "boundId", referencedName = "boundId"),
            @JoinProperty(name = "serviceTypeId", referencedName = "serviceTypeId")
    })
    @OrderBy("routeNo ASC, boundId ASC, serviceTypeId ASC, seq ASC")
    private List<KmbRouteStop> kmbRouteStopList;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 1518633273)
private transient KmbServiceTypeDao myDao;

    public KmbServiceType(SpecialRoute.Route route, long updateTimestamp) {
        routeNo = route.route;
        boundId = route.bound;
        serviceTypeId = Integer.parseInt(route.serviceType.trim());
        desc = route.descChi;
        descEN = route.descEng;
        locationFrom = route.originChi;
        locationFromEN = route.originEng;
        locationTo = route.destinationChi;
        locationToEN = route.destinationEng;
        normalStartTm = route.fromWeekDay;
        normalEndTm = route.toWeekDay;
        satStartTm = route.fromSaturday;
        satEndTm = route.toSaturday;
        holidayStartTm = route.fromHoliday;
        holidayEndTm = route.toHoliday;
        this.updateTimestamp = updateTimestamp;
        Log.d("SpecialRoute", routeNo + "-" + serviceTypeId + "<" + route.serviceType + "<");
    }

@Generated(hash = 800307803)
public KmbServiceType(Long id, @NotNull String routeNo, @NotNull Integer boundId,
        @NotNull Integer serviceTypeId, String desc, String descEN, String locationFrom,
        String locationFromEN, String locationTo, String locationToEN, int normalStartTm,
        int normalEndTm, int satStartTm, int satEndTm, int holidayStartTm,
        int holidayEndTm, long updateTimestamp) {
    this.id = id;
    this.routeNo = routeNo;
    this.boundId = boundId;
    this.serviceTypeId = serviceTypeId;
    this.desc = desc;
    this.descEN = descEN;
    this.locationFrom = locationFrom;
    this.locationFromEN = locationFromEN;
    this.locationTo = locationTo;
    this.locationToEN = locationToEN;
    this.normalStartTm = normalStartTm;
    this.normalEndTm = normalEndTm;
    this.satStartTm = satStartTm;
    this.satEndTm = satEndTm;
    this.holidayStartTm = holidayStartTm;
    this.holidayEndTm = holidayEndTm;
    this.updateTimestamp = updateTimestamp;
}

@Generated(hash = 434058798)
public KmbServiceType() {
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

public String getDesc() {
    return this.desc;
}

public void setDesc(String desc) {
    this.desc = desc;
}

public String getDescEN() {
    return this.descEN;
}

public void setDescEN(String descEN) {
    this.descEN = descEN;
}

public String getLocationFrom() {
    return this.locationFrom;
}

public void setLocationFrom(String locationFrom) {
    this.locationFrom = locationFrom;
}

public String getLocationFromEN() {
    return this.locationFromEN;
}

public void setLocationFromEN(String locationFromEN) {
    this.locationFromEN = locationFromEN;
}

public String getLocationTo() {
    return this.locationTo;
}

public void setLocationTo(String locationTo) {
    this.locationTo = locationTo;
}

public String getLocationToEN() {
    return this.locationToEN;
}

public void setLocationToEN(String locationToEN) {
    this.locationToEN = locationToEN;
}

public int getNormalStartTm() {
    return this.normalStartTm;
}

public void setNormalStartTm(int normalStartTm) {
    this.normalStartTm = normalStartTm;
}

public int getNormalEndTm() {
    return this.normalEndTm;
}

public void setNormalEndTm(int normalEndTm) {
    this.normalEndTm = normalEndTm;
}

public int getSatStartTm() {
    return this.satStartTm;
}

public void setSatStartTm(int satStartTm) {
    this.satStartTm = satStartTm;
}

public int getSatEndTm() {
    return this.satEndTm;
}

public void setSatEndTm(int satEndTm) {
    this.satEndTm = satEndTm;
}

public int getHolidayStartTm() {
    return this.holidayStartTm;
}

public void setHolidayStartTm(int holidayStartTm) {
    this.holidayStartTm = holidayStartTm;
}

public int getHolidayEndTm() {
    return this.holidayEndTm;
}

public void setHolidayEndTm(int holidayEndTm) {
    this.holidayEndTm = holidayEndTm;
}

public long getUpdateTimestamp() {
    return this.updateTimestamp;
}

public void setUpdateTimestamp(long updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 105489946)
public List<KmbRouteStop> getKmbRouteStopList() {
    if (kmbRouteStopList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        KmbRouteStopDao targetDao = daoSession.getKmbRouteStopDao();
        List<KmbRouteStop> kmbRouteStopListNew = targetDao
                ._queryKmbServiceType_KmbRouteStopList(routeNo, boundId, serviceTypeId);
        synchronized (this) {
            if (kmbRouteStopList == null) {
                kmbRouteStopList = kmbRouteStopListNew;
            }
        }
    }
    return kmbRouteStopList;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 2041676954)
public synchronized void resetKmbRouteStopList() {
    kmbRouteStopList = null;
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1487870547)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getKmbServiceTypeDao() : null;
}

}
