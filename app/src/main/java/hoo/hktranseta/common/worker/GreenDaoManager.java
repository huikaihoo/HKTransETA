package hoo.hktranseta.common.worker;

import org.greenrobot.greendao.database.Database;

import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.main.gov.model.DaoMaster;
import hoo.hktranseta.main.gov.model.DaoSession;

public class GreenDaoManager {

    private static Database mDatabase;
    private static DaoSession mDaoSession;

    private GreenDaoManager() {
    }

    public static DaoSession getInstance() {
        if (mDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(BaseApplication.getContext(), Constants.Database.DB_NAME);
            mDatabase = helper.getWritableDb();
            mDaoSession = new DaoMaster(mDatabase).newSession();
        }
        return mDaoSession;
    }

    private static Database getDatabase() {
        if (mDaoSession == null || mDatabase == null) {
            getInstance();
        }
        return mDatabase;
    }
}
