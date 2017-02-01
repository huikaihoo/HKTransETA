package hoo.hktranseta.main.gov.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.activity.BaseActivity;
import hoo.hktranseta.main.gov.model.GovBusRoute;
import hoo.hktranseta.main.kmb.KmbActivity;


public class GovBusRoutesAdapter extends RecyclerView.Adapter<GovBusRoutesAdapter.MyViewHolder>
        implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    private static final String TAG = "GovBusRoutesAdapter";

    private GovBusRouteFilter mGovBusRouteFilter;

    private BaseActivity activity;
    private List<GovBusRoute> govBusRouteFullList, govBusRouteFilteredList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle, tvDescBottom, tvDescTopRight;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitle = (TextView) view.findViewById(R.id.title);
            tvDescBottom = (TextView) view.findViewById(R.id.desc_bottom);
            tvDescTopRight = (TextView) view.findViewById(R.id.desc_top_right);
        }
    }

    public GovBusRoutesAdapter(List<GovBusRoute> govBusRouteList, BaseActivity activity) {
        this.govBusRouteFullList = govBusRouteList;
        this.activity = activity;
        resetFilteredList();
    }

    public void resetFilteredList() {
        if (govBusRouteFilteredList == null) {
            govBusRouteFilteredList = new ArrayList<>();
        } else {
            govBusRouteFilteredList.clear();
        }
        govBusRouteFilteredList.addAll(govBusRouteFullList);
    }

    @Override
    public int getItemCount() {
        return govBusRouteFilteredList.size();
    }

    @Override
    public GovBusRoutesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gov_bus_route, parent, false);
        return new GovBusRoutesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GovBusRoutesAdapter.MyViewHolder holder, int position) {
        final GovBusRoute govBusRoute = govBusRouteFilteredList.get(position);

        String directionArrow;
        if (govBusRoute.getBoundCnt() >= 2) {
            directionArrow = BaseApplication.getContext().getResources().getString(R.string.arrow_two_ways);
        } else if (govBusRoute.getCircular() == 1) {
            directionArrow = BaseApplication.getContext().getResources().getString(R.string.arrow_circular);
        } else {
            directionArrow = BaseApplication.getContext().getResources().getString(R.string.arrow_one_way);
        }

        String desc = govBusRoute.getRouteTypeDesc();

        holder.tvTitle.setText(govBusRoute.getRouteNo());
        holder.tvDescBottom.setText(govBusRoute.getLocationFrom() + directionArrow + govBusRoute.getLocationTo());
        holder.tvDescTopRight.setText(govBusRoute.getPhasedDesc());

        // SetOnClickListener
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, govBusRoute.getRouteNo());
                if (activity instanceof KmbActivity) {
                    ((KmbActivity) activity).startRouteActivity(govBusRoute.getRouteNo());
                }
            }
        });
    }

    @Override
    public Filter getFilter() {
        if (mGovBusRouteFilter == null) {
            mGovBusRouteFilter = new GovBusRouteFilter();
        }
        return mGovBusRouteFilter;
    }

    public class GovBusRouteFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            govBusRouteFilteredList.clear();

            if (constraint.length() == 0) {
                govBusRouteFilteredList.addAll(govBusRouteFullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GovBusRoute govBusRoute : govBusRouteFullList) {
                    if (govBusRoute.getRouteNo().toLowerCase().contains(filterPattern)) {
                        govBusRouteFilteredList.add(govBusRoute);
                    }
                }
            }

            results.values = govBusRouteFilteredList;
            results.count = govBusRouteFilteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d(TAG, "publishResults");
            notifyDataSetChanged();
        }
    }

    @Override
    public String getSectionName(int position) {
        return govBusRouteFilteredList.get(position).getRouteNo();
        //return govBusRouteFilteredList.get(position).getSectionName();
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