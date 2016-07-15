package com.test.alex.thebeegame.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.alex.thebeegame.Config;
import com.test.alex.thebeegame.R;
import com.test.alex.thebeegame.model.Unit;
import com.test.alex.thebeegame.utils.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BattleFieldFragment extends Fragment {

    private final class HitData {
        HitData(String targetId, String targetTypeName, String targetUnitName, String hitterName, int hpBefore, int hpAfter, boolean isUserHit, boolean isTargetQueen) {
            this.targetId = targetId;
            this.targetTypeName = targetTypeName;
            this.targetUnitName = targetUnitName;
            this.hitterName = hitterName;
            this.hpBefore = hpBefore;
            this.hpAfter = hpAfter;

            this.userOrEnemyHit = isUserHit;
            this.isTargetQueen = isTargetQueen;
        }

        final String targetId;
        final String targetTypeName;
        final String targetUnitName;
        final String hitterName;
        final int hpBefore;
        final int hpAfter;
        final boolean isTargetQueen;

        /**
         * {@code true} for your hit, otherwise {@code false}
         */
        final boolean userOrEnemyHit;
    }

    //  View
    private BattleResultAdapter adapter;

    private ViewGroup header;
    private ListView resultList;


    //  Data
    private List<HitData> data = new ArrayList<HitData>();

    /**
     * Key is unit id, value - is unit alive
     */
    private HashMap<String, Boolean> aliveForId = new HashMap<String, Boolean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_bee_field, container, false);

        header = (ViewGroup)layout.findViewById(R.id.result_list_header);
        resultList = (ListView)layout.findViewById(R.id.result_list);

        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new BattleResultAdapter(getActivity(), data, aliveForId);
        resultList.setAdapter(adapter);

        initHeaderView();
    }

    private View initHeaderView() {
        HitResultItemViewHolder holder = new HitResultItemViewHolder(header);

        setupHeaderTextView(holder.targetTypeName, R.string.target_type_name_header_text);
        setupHeaderTextView(holder.targetUnitName, R.string.target_unit_name_header_text);
        setupHeaderTextView(holder.hitterName, R.string.hitter_name_header_text);
        setupHeaderTextView(holder.hpBefore, R.string.hp_before_header_text);
        setupHeaderTextView(holder.hpAfter, R.string.hp_after_header_text);

        return header;
    }

    private void setupHeaderTextView(TextView tv, int textId) {
        tv.setText(textId);
        tv.setMaxLines(3);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
    }


    public void clearList() {
        data.clear();
        aliveForId.clear();
        adapter.notifyDataSetChanged();
    }

    public void addHitResult(Unit hitUnit, int startHP, Unit hitter, boolean isUserHit) {
        Assert._assert(startHP > 0, "Logic error, start HP should be greater than 0");
        if(hitUnit == null || hitter == null)
            throw new IllegalArgumentException("Unit objects can't be null");

        String tId = hitUnit.getUnitId();
        String tName = hitUnit.getType().getTypeName();
        String uName = hitUnit.getUnitName();
        String hitterUName = hitter.getUnitName();
        boolean istQueen = hitUnit.getType().isQueen();

        HitData hitData = new HitData(tId, tName, uName, hitterUName, startHP, hitUnit.getHP(), isUserHit, istQueen);
        data.add(hitData);
        Boolean prev = aliveForId.put(tId, hitUnit.isAlive());

        //  Validate if dead item were hit
        if(Config.ASSERT_ENABLED) {
            if(prev != null && prev == false) {
                Assert._assert(false, "Logic error: already dead unit shouldn't never be hit");
            }
        }

        adapter.notifyDataSetChanged();
        resultList.setSelection(data.size() - 1);
    }

    public class HitResultItemViewHolder {
        public final TextView targetTypeName;
        public final TextView targetUnitName;
        public final TextView hitterName;
        public final TextView hpBefore;
        public final TextView hpAfter;

        HitResultItemViewHolder(ViewGroup rowView) {
            targetTypeName = (TextView) rowView.findViewById(R.id.target_type_name);
            targetUnitName = (TextView) rowView.findViewById(R.id.target_unit_name);
            hitterName = (TextView) rowView.findViewById(R.id.hitter_name);
            hpBefore = (TextView) rowView.findViewById(R.id.hp_before);
            hpAfter = (TextView) rowView.findViewById(R.id.hp_after);
        }
    }

    /**
     * Conventional method to determine is unit alive
     * @param unit
     * @return
     */
    private boolean isUnitAlive(HitData unit) {
        return aliveForId.get(unit.targetId);
    }

    /**
     * Uses 2 different item types for alive and dead units
     */
    class BattleResultAdapter extends ArrayAdapter<HitData> {

        final static int TYPE_NUMBER = 2;

        final static int ALIVE_UNIT_TYPE = 0;
        final static int DEAD_UNIT_TYPE = 1;

        private final LayoutInflater li;

        private final int userHitCollor;
        private final int pcHitCollor;
        private final Drawable strikeoutBackground;

        private final HashMap<String, Boolean> aliveForId;

        BattleResultAdapter(Context ctx, List<HitData> data, HashMap<String, Boolean> aliveForId) {
            super(ctx, R.layout.hit_result_item, data);
            this.aliveForId = aliveForId;

            li = LayoutInflater.from(ctx);

            userHitCollor = ctx.getResources().getColor(R.color.user_hit_item_bg);
            pcHitCollor = ctx.getResources().getColor(R.color.pc_hit_item_bg);
            strikeoutBackground = ctx.getResources().getDrawable(R.drawable.strikeout_bg);
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_NUMBER;
        }

        @Override
        public int getItemViewType(int position) {
            HitData hit = getItem(position);
            if (isUnitAlive(hit)) {
                return ALIVE_UNIT_TYPE;
            } else {
                return DEAD_UNIT_TYPE;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HitData hit = getItem(position);

            HitResultItemViewHolder holder;
            ViewGroup rowView = (ViewGroup) convertView;

            // Check if an existing view is being reused, otherwise inflate the view
            if (rowView == null) {
                rowView = (ViewGroup) li.inflate(R.layout.hit_result_item, parent, false);
                holder = new HitResultItemViewHolder(rowView);

                if(!isUnitAlive(hit)) {
                    setupDeadItemView(holder);
                }

                rowView.setTag(holder);
            } else {
                holder = (HitResultItemViewHolder) rowView.getTag();
            }

            holder.targetTypeName.setText(hit.targetTypeName);
            holder.targetUnitName.setText(hit.targetUnitName);
            holder.hitterName.setText(hit.hitterName);
            holder.hpBefore.setText(Integer.toString(hit.hpBefore));
            holder.hpAfter.setText(Integer.toString(hit.hpAfter));

            setupQueenUnit(holder, hit.isTargetQueen);

            if (hit.userOrEnemyHit) {
                rowView.setBackgroundColor(userHitCollor);
            } else {
                rowView.setBackgroundColor(pcHitCollor);
            }

            return rowView;
        }

        private final int QUEEN_TYPEFACE = Typeface.BOLD_ITALIC;
        private final int NOT_QUEEN_TYPEFACE = Typeface.NORMAL;

        private void setupQueenUnit(HitResultItemViewHolder holder, boolean isQueen) {
            // Optimization, check if need change typeface.
            boolean needChange = (isQueen) ?
                    // For Queen only if NOT_QUEEN_TYPEFACE
                    holder.targetTypeName.getTypeface().getStyle() != QUEEN_TYPEFACE :
                    // For other only if QUEEN_TYPEFACE
                    holder.targetTypeName.getTypeface().getStyle() != NOT_QUEEN_TYPEFACE;

            if(needChange) {
                setupQueenUnitTextView(holder.targetTypeName, isQueen);
                setupQueenUnitTextView(holder.targetUnitName, isQueen);
                setupQueenUnitTextView(holder.hitterName, isQueen);
                setupQueenUnitTextView(holder.hpBefore, isQueen);
                setupQueenUnitTextView(holder.hpAfter, isQueen);
            }
        }

        private void setupQueenUnitTextView(TextView tv, boolean isQueen) {
             if(isQueen) {
                 tv.setTypeface(Typeface.DEFAULT, QUEEN_TYPEFACE);
             } else {
                 tv.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
             }
        }

        private void setupDeadItemView(HitResultItemViewHolder holder) {
            setStrikeout(holder.targetTypeName);
            setStrikeout(holder.targetUnitName);
            setStrikeout(holder.hitterName);
            setStrikeout(holder.hpBefore);
            setStrikeout(holder.hpAfter);
        }

        private void setStrikeout(TextView tv) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
