package com.wedevgroup.weflyhelper.adapter;

/**
 * Created by Obrina.KIMI on 1/15/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.CreateParcelleActivity;
import com.wedevgroup.weflyhelper.activity.MainActivity;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.interfaces.Kamui;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;

import java.util.ArrayList;

/**
 * Created by nafiou on 8/25/17.
 */

public class ParcelleViewAdapter extends RecyclerView.Adapter<ParcelleViewAdapter.ParcelleViewHolder> implements Kamui, DBActivity.OnListChangeListener{
    private ArrayList<Parcelle> parcelles = new ArrayList<>();
    DBActivity act;
    private Context ctx;
    LinearLayout liMain;
    private OnListItemClickListener listener;
    private final  String TAG = getClass().getSimpleName();

    public ParcelleViewAdapter(final DBActivity act, @NonNull ArrayList<Parcelle> parcelles, @NonNull LinearLayout liMain)
    {
        this.parcelles.addAll(parcelles);
        this.act = act;
        this.liMain = liMain;
        act.setOnListChangeListener(this);
    }


    @Override
    public ParcelleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_parcelle, parent,false);
        ParcelleViewHolder parcelleViewHolder = new ParcelleViewHolder(view, act);
        return parcelleViewHolder;
    }

    @Override
    public void onBindViewHolder(final ParcelleViewHolder holder, final int position) {
        try{
            holder.parcel = (Parcelle) parcelles.get(position);
            //Made copy for OnClickListener
            final Parcelle parcelle = holder.parcel;
            ctx = holder.itemCardView.getContext();


            holder.parcelId.setText(act.getString(R.string.parcelle_num_maj) + String.valueOf(parcelle.getParcelleId()));
            holder.region.setText(parcelle.getRegion());
            holder.date.setText(parcelle.getDateCreatedFormatted());

            holder.image.setImageBitmap(parcelle.getImageAsBitmap(holder.itemCardView.getContext()));

            // status
            if (!parcelle.getDateSoumission().trim().equals("")){
                holder.sent.setIcon(new IconicsDrawable(ctx, FontAwesome.Icon.faw_check)
                        .color(ContextCompat.getColor(ctx,R.color.material_green_500))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_SIZE));
            }else {
                holder.sent.setIcon(new IconicsDrawable(ctx, FontAwesome.Icon.faw_exclamation)
                        .color(ContextCompat.getColor(ctx,R.color.material_red_500))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_SIZE));
            }



            holder.riplSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show details
                    notifyOnListItemClickListener(parcelle, act);
                }
            });

            holder.riplDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // delete parcelle
                    setCtx(holder.itemCardView.getContext());
                    hideDetailPanel();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (act != null){
                                    if (act instanceof MainActivity){
                                        MainActivity activity = (MainActivity) act;
                                        activity.removeParcelle(holder.parcel, activity, activity.getUI() );
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, Constants.ANIMATION_RIPPLE_DELAI);



                }
            });

            holder.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyOnListItemClickListener(parcelle, act);
                }
            });


            holder.riplEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Edit parcel
                    hideDetailPanel();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent edIntent = new Intent(getCtx(), CreateParcelleActivity.class);
                                edIntent.putExtra("toEdit", true);
                                Bundle b = new Bundle();
                                b.putSerializable("parcelObj", parcelle);
                                edIntent.putExtras(b);
                                launchActivity(edIntent, getCtx());

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, Constants.ANIMATION_RIPPLE_DELAI);


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hideDetailPanel() {
        if (act != null){
            if (act instanceof MainActivity){
                MainActivity activity = (MainActivity) act;
                activity.hideDetailPanel();
            }
        }
    }


    @Override
    public int getItemCount() {
        return parcelles.size();
    }

    @Override
    public void shouldNotifDataSetChange() {

    }

    @Override
    public void onParcelleDeleteError(@NonNull final Parcelle parcelle) {

    }

    @Override
    public void onParcelleDeleteDone() {
        // do nothing
    }

    @Override
    public void onParcelleAdded(@NonNull Parcelle p) {
        parcelles.add(p);
        notifyDataSetChanged();
        updateUI();
    }

    @Override
    public void onParcelleEdited(@NonNull Parcelle oldParcel, @NonNull Parcelle newParcel) {
        parcelles.remove(oldParcel);
        parcelles.add(newParcel);
        notifyDataSetChanged();
        updateUI();
    }


    @Override
    public void onParcelleDeleted(@NonNull Parcelle p) {
        parcelles.remove(p);
        notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        if (act != null){
            if (act instanceof MainActivity){
                MainActivity activity = (MainActivity) act;
                activity.onDisplayUi(parcelles.size() > 0, parcelles.size() == 0, false, false, false, false, false);
            }
        }
    }


    public static class ParcelleViewHolder extends RecyclerView.ViewHolder
    {
        Activity activity;
        CardView itemCardView;
        public Parcelle parcel;
        public ImageView image;
        public TextView region;
        public TextView parcelId;
        public TextView date;
        public MaterialRippleLayout riplSee;
        public MaterialRippleLayout riplDelete;
        public MaterialRippleLayout riplEdit;
        public IconicsImageView sent;


        public ParcelleViewHolder(View itemView, final Activity activity){
            super(itemView);
            this.activity    = activity;
            itemCardView     =          (CardView) itemView.findViewById(R.id.card_view_layout);
            image            =          (ImageView) itemView.findViewById(R.id.pImage);
            region           =          (TextView) itemView.findViewById(R.id.regionTv);
            date             =          (TextView) itemView.findViewById(R.id.pDateTView);
            parcelId         =          (TextView) itemView.findViewById(R.id.pIdTView);

            riplSee          =          (MaterialRippleLayout) itemView.findViewById(R.id.riplSee);
            riplDelete       =          (MaterialRippleLayout) itemView.findViewById(R.id.riplDelete);
            riplEdit         =          (MaterialRippleLayout) itemView.findViewById(R.id.riplEdit);

            sent             =          (IconicsImageView)  itemView.findViewById(R.id.sentIcon);



        }
    }

    public Context getCtx() {
        if (ctx == null)
            return act.getApplicationContext();
        return ctx;
    }

    public void setCtx(@NonNull Context ctx) {
        this.ctx = ctx;
    }

    public void setOnListItemClickListener(@NonNull OnListItemClickListener li, @NonNull DBActivity activity) {
        this.listener = li;
        this.act = activity;
    }

    public static interface OnListItemClickListener {
        void onClick(@NonNull Parcelle p, @NonNull DBActivity activity);
    }

    public void notifyOnListItemClickListener(@NonNull Parcelle parcel, @NonNull DBActivity activity) {
        if (listener != null){
            listener.onClick(parcel, activity);
        }

    }

    public void launchActivity(@NonNull final  Intent intent, @NonNull final Context ctx){
        AppController appController = AppController.getInstance();
        if (appController != null){
            if (appController.isTokenAndUserOk(ctx)){
                ctx.startActivity(intent);
            }
        }
    }

}
