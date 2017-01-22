package hoo.hktranseta.main.kmb.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

import hoo.hktranseta.main.kmb.model.json.Stop;

@Entity(
    // Define indexes spanning multiple columns here.
        indexes = {
                @Index(value = "routeNo ASC, boundId ASC, serviceTypeId ASC, seq ASC", unique = true)
        }
)
public class KmbRouteStop {

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

    // Other Fields
    private String stopId;
    private double stopLat;
    private double stopLong;
    private double fare;

    private String stopName;
    private String stopNameEN;
    private String stopDesc;
    private String stopDescEN;

    private String special;
    private String busType;
    private String direction;
    private String airport;
    private String overnight;
    private String racecourse;

    private long updateTimestamp;

    // Transient: not store in DB
    @Transient
    public int etaStatus;

    // Relation with KmbEta
    @ToMany(joinProperties = {
            @JoinProperty(name = "routeNo", referencedName = "routeNo"),
            @JoinProperty(name = "boundId", referencedName = "boundId"),
            @JoinProperty(name = "serviceTypeId", referencedName = "serviceTypeId"),
            @JoinProperty(name = "seq", referencedName = "seq")
    })
    @OrderBy("routeNo ASC, boundId ASC, serviceTypeId ASC, seq ASC, etaId ASC")
    public List<KmbEta> kmbEtaList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 515639085)
    private transient KmbRouteStopDao myDao;

    public KmbRouteStop(Stop.RouteStop routeStop, long updateTimestamp) {
        routeNo = routeStop.route;
        boundId = routeStop.bound;
        serviceTypeId = routeStop.serviceType;
        seq = routeStop.seq;
        stopId = routeStop.bsiCode;
        stopLat = routeStop.x;
        stopLong = routeStop.y;
        fare = routeStop.airFare;
        stopName = routeStop.cName;
        stopNameEN = routeStop.eName;
        stopDesc = routeStop.cLocation;
        stopDescEN = routeStop.eLocation;
        special = routeStop.special;
        busType = routeStop.busType;
        direction = routeStop.direction;
        airport = routeStop.airport;
        overnight = routeStop.overnight;
        racecourse = routeStop.racecourse;

        this.updateTimestamp = updateTimestamp;
    }

    @Generated(hash = 1043138339)
    public KmbRouteStop(Long id, @NotNull String routeNo, @NotNull Integer boundId,
            @NotNull Integer serviceTypeId, @NotNull Integer seq, String stopId, double stopLat,
            double stopLong, double fare, String stopName, String stopNameEN, String stopDesc,
            String stopDescEN, String special, String busType, String direction, String airport,
            String overnight, String racecourse, long updateTimestamp) {
        this.id = id;
        this.routeNo = routeNo;
        this.boundId = boundId;
        this.serviceTypeId = serviceTypeId;
        this.seq = seq;
        this.stopId = stopId;
        this.stopLat = stopLat;
        this.stopLong = stopLong;
        this.fare = fare;
        this.stopName = stopName;
        this.stopNameEN = stopNameEN;
        this.stopDesc = stopDesc;
        this.stopDescEN = stopDescEN;
        this.special = special;
        this.busType = busType;
        this.direction = direction;
        this.airport = airport;
        this.overnight = overnight;
        this.racecourse = racecourse;
        this.updateTimestamp = updateTimestamp;
    }

    @Generated(hash = 1047963178)
    public KmbRouteStop() {
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

    public String getStopId() {
        return this.stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public double getStopLat() {
        return this.stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStopLong() {
        return this.stopLong;
    }

    public void setStopLong(double stopLong) {
        this.stopLong = stopLong;
    }

    public double getFare() {
        return this.fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getStopName() {
        return this.stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopNameEN() {
        return this.stopNameEN;
    }

    public void setStopNameEN(String stopNameEN) {
        this.stopNameEN = stopNameEN;
    }

    public String getStopDesc() {
        return this.stopDesc;
    }

    public void setStopDesc(String stopDesc) {
        this.stopDesc = stopDesc;
    }

    public String getStopDescEN() {
        return this.stopDescEN;
    }

    public void setStopDescEN(String stopDescEN) {
        this.stopDescEN = stopDescEN;
    }

    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getBusType() {
        return this.busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAirport() {
        return this.airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getOvernight() {
        return this.overnight;
    }

    public void setOvernight(String overnight) {
        this.overnight = overnight;
    }

    public String getRacecourse() {
        return this.racecourse;
    }

    public void setRacecourse(String racecourse) {
        this.racecourse = racecourse;
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
    @Generated(hash = 1409057646)
    public List<KmbEta> getKmbEtaList() {
        if (kmbEtaList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KmbEtaDao targetDao = daoSession.getKmbEtaDao();
            List<KmbEta> kmbEtaListNew = targetDao._queryKmbRouteStop_KmbEtaList(routeNo, boundId,
                    serviceTypeId, seq);
            synchronized (this) {
                if (kmbEtaList == null) {
                    kmbEtaList = kmbEtaListNew;
                }
            }
        }
        return kmbEtaList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 364052659)
    public synchronized void resetKmbEtaList() {
        kmbEtaList = null;
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
    @Generated(hash = 396929211)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getKmbRouteStopDao() : null;
    }

}
