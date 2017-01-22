package hoo.hktranseta.main.kmb.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.main.kmb.KmbRouteFragment;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;

public class KmbRouteStopsAdapter extends RecyclerView.Adapter<KmbRouteStopsAdapter.MyViewHolder> {

    private static final String TAG = "KmbRouteStopsAdapter";

    private KmbServiceType kmbServiceType;
    private KmbRouteFragment kmbRouteFragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvStopName, tvFare, tvStopDesc;
        public List<TextView> tvEtaList = new ArrayList<>();

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tvStopName = (TextView) view.findViewById(R.id.stop_name);
            tvFare = (TextView) view.findViewById(R.id.fare);
            tvStopDesc = (TextView) view.findViewById(R.id.stop_desc);
            tvEtaList.add((TextView) view.findViewById(R.id.eta_0));
            tvEtaList.add((TextView) view.findViewById(R.id.eta_1));
            tvEtaList.add((TextView) view.findViewById(R.id.eta_2));
        }
    }

    public KmbRouteStopsAdapter(KmbServiceType kmbServiceType, KmbRouteFragment kmbRouteFragment) {
        this.kmbServiceType = kmbServiceType;
        this.kmbRouteFragment = kmbRouteFragment;
    }

    @Override
    public int getItemCount() {
        return kmbServiceType.getKmbRouteStopList().size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kmb_stop, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final KmbRouteStop kmbRouteStop = kmbServiceType.getKmbRouteStopList().get(position);

        // Basic Information
        holder.tvStopName.setText((position+1) + ". " +  kmbRouteStop.getStopName());
        holder.tvStopDesc.setText(kmbRouteStop.getStopDesc());
        if ((kmbRouteStop.getFare() > 0) || (kmbServiceType.getKmbRouteStopList().size() < position + 1)) {
            holder.tvFare.setText(String.format("$%.2f", kmbRouteStop.getFare()));
        }

        if (kmbRouteStop.etaStatus == Constants.Eta.SUCCESS) {
            Date currentDate = new Date();  // Get current datetime

            // Eta Message
            for (int i = 0; i < holder.tvEtaList.size(); i++) {
                if (i < kmbRouteStop.getKmbEtaList().size()) {
                    if (i == 0 || (kmbRouteStop.getKmbEtaList().get(i).getUpdateTimestamp() == kmbRouteStop.getKmbEtaList().get(i - 1).getUpdateTimestamp())) {
                        holder.tvEtaList.get(i).setText(kmbRouteStop.getKmbEtaList().get(i).getPhasedEtaTimeMsg(currentDate));
                    } else {
                        holder.tvEtaList.get(i).setText("");
                    }
                } else {
                    holder.tvEtaList.get(i).setText("");
                }
            }

            // Eta Text Color
            int etaTextColor = R.color.colorMenuWhite;
            int prevDiff = -3000, currDiff = -3000;

            if (position > 0 && kmbServiceType.getKmbRouteStopList().get(position - 1).getKmbEtaList().size() > 0) {
                prevDiff = kmbServiceType.getKmbRouteStopList().get(position - 1).getKmbEtaList().get(0).getTimeDiff(currentDate);
            }
            if (kmbRouteStop.getKmbEtaList().size() > 0) {
                currDiff = kmbRouteStop.getKmbEtaList().get(0).getTimeDiff(currentDate);
            }
            //Log.d(TAG, "Color" + position + "|" + prevDiff + "|" + currDiff);

            if (currDiff <= -3000 || (position > 0 && kmbServiceType.getKmbRouteStopList().get(position - 1).etaStatus == Constants.Eta.LOADING)) {
                etaTextColor = R.color.colorMenuWhite;
            } else if (currDiff > -3000 && currDiff < -10) {
                etaTextColor = R.color.colorMenuGrey;
            } else if ((position == 0 && currDiff <= 5) || (position > 0 && (prevDiff <= -3000 || currDiff < prevDiff))) {
                etaTextColor = R.color.colorKmbAccent;
            }

            holder.tvStopName.setTextColor(ContextCompat.getColor(BaseApplication.getContext(), etaTextColor));
            holder.tvEtaList.get(0).setTextColor(ContextCompat.getColor(BaseApplication.getContext(), etaTextColor));
        } else {
            switch (kmbRouteStop.etaStatus) {
                case Constants.Eta.LOADING:
                    holder.tvEtaList.get(0).setText(BaseApplication.getContext().getResources().getText(R.string.loading));
                    break;
                case Constants.Eta.ERROR:
                    holder.tvEtaList.get(0).setText(BaseApplication.getContext().getResources().getText(R.string.error_occur));
                    break;
                case Constants.Eta.NODATA:
                    holder.tvEtaList.get(0).setText(BaseApplication.getContext().getResources().getText(R.string.no_data));
                    break;
            }
            for (int i = 1; i < holder.tvEtaList.size(); i++) {
                holder.tvEtaList.get(i).setText("");
            }
            holder.tvStopName.setTextColor(ContextCompat.getColor(BaseApplication.getContext(), R.color.colorMenuWhite));
            holder.tvEtaList.get(0).setTextColor(ContextCompat.getColor(BaseApplication.getContext(), R.color.colorMenuWhite));
        }

        // SetOnClickListener
        final int pos = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kmbRouteFragment.updateEtaList(pos);
                Log.d(TAG, kmbRouteStop.getStopId() + "");
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}