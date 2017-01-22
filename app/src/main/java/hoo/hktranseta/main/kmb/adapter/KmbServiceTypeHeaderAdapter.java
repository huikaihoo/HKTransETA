package hoo.hktranseta.main.kmb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.main.kmb.KmbRouteFragment;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;

public class KmbServiceTypeHeaderAdapter extends RecyclerView.Adapter<KmbServiceTypeHeaderAdapter.MyViewHolder> {

    private static final String TAG = "KmbSTHeaderAdapter";

    private List<KmbServiceType> kmbServiceTypeList;
    private KmbRouteFragment kmbRouteFragment;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public Spinner spinner;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
        }
    }

    public KmbServiceTypeHeaderAdapter(List<KmbServiceType> kmbServiceTypeList, KmbRouteFragment kmbRouteFragment) {
        this.kmbServiceTypeList = kmbServiceTypeList;
        this.kmbRouteFragment = kmbRouteFragment;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kmb_service_type_header, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.spinner.setEnabled(kmbServiceTypeList.size() > 1);
        holder.spinner.setAdapter(new KmbServiceTypeSpinnerAdapter(holder.context, R.layout.item_kmb_service_type, kmbServiceTypeList));
        holder.spinner.setSelection(kmbRouteFragment.getSelectedServiceTypePosition());
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int beforePosition = kmbRouteFragment.getSelectedServiceTypePosition();
                Log.d(TAG, "Before position=" + beforePosition);
                Log.d(TAG, "New position=" + position);
                if (beforePosition != position) {
                    kmbRouteFragment.setSelectedServiceTypePosition(position);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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


