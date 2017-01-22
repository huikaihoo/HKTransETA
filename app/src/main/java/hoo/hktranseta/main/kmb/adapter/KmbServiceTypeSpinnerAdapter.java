package hoo.hktranseta.main.kmb.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;

public class KmbServiceTypeSpinnerAdapter extends ArrayAdapter<KmbServiceType> {

    private int resourceId;

    public KmbServiceTypeSpinnerAdapter(Context context, int resourceId, List<KmbServiceType> items) {
        super(context, resourceId, items);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(resourceId, null);
        }

        KmbServiceType kmbServiceType = getItem(position);

        if (kmbServiceType != null) {
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            TextView tvDesc = (TextView) convertView.findViewById(R.id.desc);

            if (tvTitle != null) {
                String directionArrow;
                List<KmbRouteStop> routeStopList = kmbServiceType.getKmbRouteStopList();
                KmbRouteStop firstStop = routeStopList.get(0);
                KmbRouteStop lastStop = routeStopList.get(routeStopList.size() - 1);

                if (firstStop.getStopId().equals(lastStop.getStopId())) {
                    directionArrow = BaseApplication.getContext().getResources().getString(R.string.arrow_circular);
                } else {
                    directionArrow = BaseApplication.getContext().getResources().getString(R.string.arrow_one_way);
                }

                tvTitle.setText((position>0 ? "#" : "")
                        + kmbServiceType.getLocationFrom() + directionArrow
                        + kmbServiceType.getLocationTo());
            }

            if (tvDesc != null) {
                tvDesc.setText(kmbServiceType.getDesc());
            }
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}