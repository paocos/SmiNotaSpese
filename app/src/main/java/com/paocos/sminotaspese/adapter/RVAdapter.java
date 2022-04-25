package com.paocos.sminotaspese.adapter;


import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paocos.sminotaspese.R;
import com.paocos.sminotaspese.model.entities.CardAll;
import com.paocos.sminotaspese.shared.ConstantUtil;

import java.util.List;

/**
 * Created by paocos on 05/01/18.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardsViewHolder>{

    List<Integer> cards;

    private CardAll cardAll;

    public RVAdapter(List<Integer> cards){
        this.cards = cards;
    }

    public static class CardsViewHolder extends RecyclerView.ViewHolder {

        CardView gpscv;
        CardView oraricv;
        CardView pranzocv;
        CardView riepcv;

        // card GPS
        TextView gps_txt_km;
        TextView gps_txt_time;
        TextView gps_txt_vmedia;
        TextView gps_lbl_whereiam;
        // card ORARI
        TextView orario_txt_mattino_da;
        TextView orario_txt_mattino_a;
        TextView orario_txt_pomeriggio_da;
        TextView orario_txt_pomeriggio_a;
        // card PRANZO
        TextView pranzo_importo;
        ImageView pranzowhere;
        // card RIEP
        TextView riep_txt_lavoro_oggi_km;
        TextView riep_txt_lavoro_oggi_tempo;
        TextView riep_txt_lavoro_oggi_vmedia;
        TextView riep_txt_lavoro_mese_km;
        TextView riep_txt_lavoro_mese_tempo;
        TextView riep_txt_lavoro_mese_vmedia;
        TextView riep_txt_lavoro_mesep_km;
        TextView riep_txt_lavoro_mesep_tempo;
        TextView riep_txt_lavoro_mesep_vmedia;
        TextView riep_txt_lavoro_anno_km;
        TextView riep_txt_lavoro_anno_tempo;
        TextView riep_txt_lavoro_anno_vmedia;
        TextView riep_txt_home_oggi_km;
        TextView riep_txt_home_oggi_tempo;
        TextView riep_txt_home_oggi_vmedia;
        TextView riep_txt_home_mese_km;
        TextView riep_txt_home_mese_tempo;
        TextView riep_txt_home_mese_vmedia;
        TextView riep_txt_home_mesep_km;
        TextView riep_txt_home_mesep_tempo;
        TextView riep_txt_home_mesep_vmedia;
        TextView riep_txt_home_anno_km;
        TextView riep_txt_home_anno_tempo;
        TextView riep_txt_home_anno_vmedia;
        TextView riep_txt_gglav;
        TextView riep_txt_pastisede;
        TextView riep_txt_pastifuori;
        TextView riep_txt_rimborsato;
        TextView riep_txt_delta;

        CardsViewHolder(View itemView) {
            super(itemView);
            // card GPS
            gpscv = (CardView) itemView.findViewById(R.id.gpscv);
            gps_txt_km = (TextView) itemView.findViewById(R.id.gps_txt_km);
            gps_txt_time = (TextView) itemView.findViewById(R.id.gps_txt_time);
            gps_txt_vmedia = (TextView) itemView.findViewById(R.id.gps_txt_vmedia);
            gps_lbl_whereiam = (TextView) itemView.findViewById(R.id.gps_lbl_whereiam);
            // card ORARI
            oraricv = (CardView) itemView.findViewById(R.id.oraricv);
            orario_txt_mattino_da = (TextView) itemView.findViewById(R.id.orario_txt_mattino_da);
            orario_txt_mattino_a = (TextView) itemView.findViewById(R.id.orario_txt_mattino_a);
            orario_txt_pomeriggio_da = (TextView) itemView.findViewById(R.id.orario_txt_pomeriggio_da);
            orario_txt_pomeriggio_a = (TextView) itemView.findViewById(R.id.orario_txt_pomeriggio_a);
            // card PRANZO
            pranzocv = (CardView) itemView.findViewById(R.id.pranzocv);
            pranzo_importo = (TextView) itemView.findViewById(R.id.pranzo_importo);
            pranzowhere = (ImageView) itemView.findViewById(R.id.pranzowhere);
            // card RIEP
            riepcv = (CardView) itemView.findViewById(R.id.riepcv);
            riep_txt_lavoro_oggi_km = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_oggi_km);
            riep_txt_lavoro_oggi_tempo = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_oggi_tempo);
            riep_txt_lavoro_oggi_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_oggi_vmedia);
            riep_txt_lavoro_mese_km = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mese_km);
            riep_txt_lavoro_mese_tempo = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mese_tempo);
            riep_txt_lavoro_mese_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mese_vmedia);
            riep_txt_lavoro_mesep_km = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mesep_km);
            riep_txt_lavoro_mesep_tempo = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mesep_tempo);
            riep_txt_lavoro_mesep_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_mesep_vmedia);
            riep_txt_lavoro_anno_km = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_anno_km);
            riep_txt_lavoro_anno_tempo = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_anno_tempo);
            riep_txt_lavoro_anno_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_lavoro_anno_vmedia);
            riep_txt_home_oggi_km = (TextView) itemView.findViewById(R.id.riep_txt_home_oggi_km);
            riep_txt_home_oggi_tempo = (TextView) itemView.findViewById(R.id.riep_txt_home_oggi_tempo);
            riep_txt_home_oggi_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_home_oggi_vmedia);
            riep_txt_home_mese_km = (TextView) itemView.findViewById(R.id.riep_txt_home_mese_km);
            riep_txt_home_mese_tempo = (TextView) itemView.findViewById(R.id.riep_txt_home_mese_tempo);
            riep_txt_home_mese_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_home_mese_vmedia);
            riep_txt_home_mesep_km = (TextView) itemView.findViewById(R.id.riep_txt_home_mesep_km);
            riep_txt_home_mesep_tempo = (TextView) itemView.findViewById(R.id.riep_txt_home_mesep_tempo);
            riep_txt_home_mesep_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_home_mesep_vmedia);
            riep_txt_home_anno_km = (TextView) itemView.findViewById(R.id.riep_txt_home_anno_km);
            riep_txt_home_anno_tempo = (TextView) itemView.findViewById(R.id.riep_txt_home_anno_tempo);
            riep_txt_home_anno_vmedia = (TextView) itemView.findViewById(R.id.riep_txt_home_anno_vmedia);
            riep_txt_gglav = (TextView) itemView.findViewById(R.id.riep_lbl_gglav);
            riep_txt_pastisede = (TextView) itemView.findViewById(R.id.riep_txt_pastisede);
            riep_txt_pastifuori = (TextView) itemView.findViewById(R.id.riep_txt_pastifuori);
            riep_txt_rimborsato = (TextView) itemView.findViewById(R.id.riep_txt_rimborsato);
            riep_txt_delta = (TextView) itemView.findViewById(R.id.riep_txt_delta);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;

        CardsViewHolder pvh = null;

        if (cards.get(i) == ConstantUtil.CARD_GPS) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_gps, viewGroup, false);
        } else if (cards.get(i) == ConstantUtil.CARD_PRANZO) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_pranzo, viewGroup, false);
        } else if (cards.get(i) == ConstantUtil.CARD_ORARI) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_orari, viewGroup, false);
        } else if (cards.get(i) == ConstantUtil.CARD_RIEP) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_riep, viewGroup, false);
        }

        pvh = new CardsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CardsViewHolder cardsViewHolder, int i) {

        if (cards.get(i) == ConstantUtil.CARD_GPS) {
            // card GPS
            cardsViewHolder.gps_txt_km.setText(String.format("%.1f", cardAll.getCardGps().getKm()));
            cardsViewHolder.gps_txt_time.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardGps().getTime()));
            cardsViewHolder.gps_txt_vmedia.setText(String.format("%.0f",ConstantUtil.calcVelMedia(cardAll.getCardGps().getKm() , cardAll.getCardGps().getTime())));
            cardsViewHolder.gps_lbl_whereiam.setText(cardAll.getCardGps().getWhereIAm());
        } else if (cards.get(i) == ConstantUtil.CARD_ORARI) {
            // card ORARI
            cardsViewHolder.orario_txt_mattino_da.setText(ConstantUtil.getTimeFormattedFromTimeStamp(cardAll.getCardOrari().getMattino_da()));
            cardsViewHolder.orario_txt_mattino_a.setText(ConstantUtil.getTimeFormattedFromTimeStamp(cardAll.getCardOrari().getMattino_a()));
            cardsViewHolder.orario_txt_pomeriggio_da.setText(ConstantUtil.getTimeFormattedFromTimeStamp(cardAll.getCardOrari().getPomeriggio_da()));
            cardsViewHolder.orario_txt_pomeriggio_a.setText(ConstantUtil.getTimeFormattedFromTimeStamp(cardAll.getCardOrari().getPomeriggio_a()));
        } else if (cards.get(i) == ConstantUtil.CARD_PRANZO) {
            // card PRANZO
            cardsViewHolder.pranzo_importo.setText(String.format("%.2f", cardAll.getCardPranzo().getImporto()));
            if (cardAll.getCardPranzo().isSede()) {
                cardsViewHolder.pranzowhere.setImageResource(R.drawable.home);
            } else {
                cardsViewHolder.pranzowhere.setImageResource(R.drawable.work);
            }
        } else if (cards.get(i) == ConstantUtil.CARD_RIEP) {
            // card RIEP
            cardsViewHolder.riep_txt_lavoro_oggi_km.setText(String.format("%.1f", cardAll.getCardRiep().getWork_oggi_km()));
            cardsViewHolder.riep_txt_lavoro_oggi_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getWork_oggi_time()));
            cardsViewHolder.riep_txt_lavoro_oggi_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getWork_oggi_km(), cardAll.getCardRiep().getWork_oggi_time())));
            cardsViewHolder.riep_txt_lavoro_mese_km.setText(String.format("%.1f", cardAll.getCardRiep().getWork_mese_km()));
            cardsViewHolder.riep_txt_lavoro_mese_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getWork_mese_time()));
            cardsViewHolder.riep_txt_lavoro_mese_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getWork_mese_km(), cardAll.getCardRiep().getWork_mese_time())));
            cardsViewHolder.riep_txt_lavoro_mesep_km.setText(String.format("%.1f", cardAll.getCardRiep().getWork_mesep_km()));
            cardsViewHolder.riep_txt_lavoro_mesep_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getWork_mesep_time()));
            cardsViewHolder.riep_txt_lavoro_mesep_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getWork_mesep_km(), cardAll.getCardRiep().getWork_mesep_time())));
            cardsViewHolder.riep_txt_lavoro_anno_km.setText(String.format("%.1f", cardAll.getCardRiep().getWork_anno_km()));
            cardsViewHolder.riep_txt_lavoro_anno_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getWork_anno_time()));
            cardsViewHolder.riep_txt_lavoro_anno_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getWork_anno_km(), cardAll.getCardRiep().getWork_anno_time())));
            cardsViewHolder.riep_txt_home_oggi_km.setText(String.format("%.1f", cardAll.getCardRiep().getHome_oggi_km()));
            cardsViewHolder.riep_txt_home_oggi_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getHome_oggi_time()));
            cardsViewHolder.riep_txt_home_oggi_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getHome_oggi_km(), cardAll.getCardRiep().getHome_oggi_time())));
            cardsViewHolder.riep_txt_home_mese_km.setText(String.format("%.1f", cardAll.getCardRiep().getHome_mese_km()));
            cardsViewHolder.riep_txt_home_mese_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getHome_mese_time()));
            cardsViewHolder.riep_txt_home_mese_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getHome_mese_km(), cardAll.getCardRiep().getHome_mese_time())));
            cardsViewHolder.riep_txt_home_mesep_km.setText(String.format("%.1f", cardAll.getCardRiep().getHome_mesep_km()));
            cardsViewHolder.riep_txt_home_mesep_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getHome_mesep_time()));
            cardsViewHolder.riep_txt_home_mesep_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getHome_mesep_km(), cardAll.getCardRiep().getHome_mesep_time())));
            cardsViewHolder.riep_txt_home_anno_km.setText(String.format("%.1f", cardAll.getCardRiep().getHome_anno_km()));
            cardsViewHolder.riep_txt_home_anno_tempo.setText(ConstantUtil.getTimeFormattedFromMillsec(cardAll.getCardRiep().getHome_anno_time()));
            cardsViewHolder.riep_txt_home_anno_vmedia.setText(String.format("%.0f", ConstantUtil.calcVelMedia(cardAll.getCardRiep().getHome_anno_km(), cardAll.getCardRiep().getHome_anno_time())));
            cardsViewHolder.riep_txt_gglav.setText("" + cardAll.getCardRiep().getGglav());
            cardsViewHolder.riep_txt_pastisede.setText("" + cardAll.getCardRiep().getNrPastiSede() + "/" + String.format("%.2f", cardAll.getCardRiep().getImpPastiSede()));
            cardsViewHolder.riep_txt_pastifuori.setText("" + cardAll.getCardRiep().getNrPastiFuori() + "/" + String.format("%.2f", cardAll.getCardRiep().getImpPastiFuori()));
            cardsViewHolder.riep_txt_rimborsato.setText(String.format("%.2f", cardAll.getCardRiep().getImpRimborsato()));

            cardsViewHolder.riep_txt_delta.setText(String.format("%.2f", cardAll.getCardRiep().getImpDelta()));
        }
    }

    public CardAll getCardAll() {
        return cardAll;
    }

    public void setCardAll(CardAll cardAll) {
        this.cardAll = cardAll;
    }
}