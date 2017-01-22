package hoo.hktranseta.common.worker;

import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.main.kmb.model.db.DaoMaster;
import hoo.hktranseta.main.kmb.model.db.DaoSession;

public class GreenDaoManager {

    private static DaoSession mDaoSession;

    private GreenDaoManager() {
    }

    public static DaoSession getInstance() {
        if (mDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(BaseApplication.getContext(), Constants.Database.DB_NAME);
            mDaoSession = new DaoMaster(helper.getWritableDb()).newSession();
        }
        return mDaoSession;
    }
}
