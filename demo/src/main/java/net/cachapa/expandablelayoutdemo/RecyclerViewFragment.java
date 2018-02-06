package net.cachapa.expandablelayoutdemo;

import android.animation.ValueAnimator;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

public class RecyclerViewFragment extends Fragment {

    private static int HEIGHT_COLLAPSED;
    private static final int DURATION = 300;
    private static final double RADIUS_MIN = 0.0;
    private static final double RADIUS_MAX = 8.0;
    private static final double MARGIN_TOP_MIN = -8.0;
    private static final double MARGIN_TOP_MAX = 8.0;
    private static final double MARGIN_BOTTOM_MIN = MARGIN_TOP_MAX + 1.0;
    private static final double MARGIN_BOTTOM_MAX = MARGIN_BOTTOM_MIN + MARGIN_TOP_MAX - MARGIN_TOP_MIN;
    private static final double MARGIN_HORIZONTAL_MIN = 10.0;
    private static final double MARGIN_HORIZONTAL_MAX = 20.0;
    private static final Interpolator interpolator = new DecelerateInterpolator(          1.0f);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SimpleAdapter(recyclerView));

        return rootView;
    }

    private static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private static final int UNSELECTED = -1;
        private RecyclerView recyclerView;
        private int selectedItem = UNSELECTED;
        private int position = 0;

        SimpleAdapter(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.recyclerView.setClipChildren(false);
            this.recyclerView.setClipToPadding(false);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

            private ExpandableLayout expandableLayout;
            private LinearLayout expandButton;
            private CardView card;

            ViewHolder(View itemView) {
                super(itemView);

                expandableLayout = itemView.findViewById(R.id.expandable_layout);
                expandableLayout.setInterpolator(interpolator);
                expandableLayout.setOnExpansionUpdateListener(this);
                expandableLayout.setDuration(DURATION);
                expandButton = itemView.findViewById(R.id.expand_button);
                card = itemView.findViewById(R.id.itemCard);

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                lp.setMargins((int) dpToPx(MARGIN_HORIZONTAL_MIN),
                        (int) dpToPx(MARGIN_TOP_MIN),
                        (int) dpToPx(MARGIN_HORIZONTAL_MIN),
                        (int) dpToPx(MARGIN_BOTTOM_MIN));
                card.setLayoutParams(lp);
                card.setRadius(0);
                card.findViewById(R.id.expand_button).setVisibility(View.VISIBLE);
                expandButton.setOnClickListener(this);
            }

            void bind() {
                int position = getAdapterPosition();
                boolean isSelected = position == selectedItem;
                ((TextView) expandButton.findViewById(R.id.fold_subject_title_tv)).setText(position + ". EHHHHHHHHH");
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                lp.setMargins(isSelected ? (int) dpToPx(MARGIN_HORIZONTAL_MIN) : (int) dpToPx(MARGIN_HORIZONTAL_MAX),
                        isSelected ? (int) dpToPx(MARGIN_TOP_MAX) : (int) dpToPx(MARGIN_TOP_MIN),
                        isSelected ? (int) dpToPx(MARGIN_HORIZONTAL_MIN) : (int) dpToPx(MARGIN_HORIZONTAL_MAX),
                        isSelected ? (int) dpToPx(MARGIN_BOTTOM_MAX) : (int) dpToPx(MARGIN_BOTTOM_MIN));
                card.setLayoutParams(lp);
                card.setRadius(isSelected ? (int) dpToPx(8) : 0);
                card.findViewById(R.id.expand_button).setVisibility(isSelected ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onClick(View view) {

                final ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                if (holder != null) {
                    animateCardWidth(MARGIN_HORIZONTAL_MIN, MARGIN_HORIZONTAL_MAX, holder.card);
                    holder.expandButton.setSelected(false);
                    holder.expandableLayout.collapse();
                }
                position = getAdapterPosition();
                if (position == selectedItem) {
                    selectedItem = UNSELECTED;
                } else {
                    animateCardWidth(MARGIN_HORIZONTAL_MAX, MARGIN_HORIZONTAL_MIN, card);
                    expandButton.setSelected(true);
                    expandableLayout.expand();
                    selectedItem = position;
                }
            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                if (state == ExpandableLayout.State.EXPANDING) {
                    try {
                        recyclerView.smoothScrollToPosition(getAdapterPosition());
                        Log.d("][][][uisxdfbvnsxl", "onExpansionUpdate: "+getAdapterPosition());
                    }catch (Exception e) {
                        Log.d("][][][uisxdfbvnsxldui", "onExpansionUpdate: "+getAdapterPosition());
                    }
                }
            }
        }

        static void animateCardWidth(final double startOri, final double endOri, final CardView cardView) {

            final double start = 0.0;
            final double end = endOri - startOri;
            final boolean collapse = endOri >= startOri;
            final int heightOri = cardView.findViewById(R.id.expand_button).getHeight();

            cardView.findViewById(R.id.expand_button).setVisibility(View.VISIBLE);
            ValueAnimator animator = ValueAnimator.ofFloat((float) start, (float) end);
            animator.setInterpolator(interpolator);
            animator.setDuration(DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                    lp.setMargins(
                            (int) (dpToPx((float) valueAnimator.getAnimatedValue() + startOri)),
                            (int) (dpToPx((collapse ? MARGIN_TOP_MAX : MARGIN_TOP_MIN) +
                                    (collapse ? -1 : 1) * (MARGIN_TOP_MAX - MARGIN_TOP_MIN) * valueAnimator.getAnimatedFraction())),
                            (int) (dpToPx((float) valueAnimator.getAnimatedValue() + startOri)),
                            (int) (dpToPx((collapse ? MARGIN_BOTTOM_MAX : MARGIN_BOTTOM_MIN) +
                                    (collapse ? -1 : 1) * (MARGIN_BOTTOM_MAX - MARGIN_BOTTOM_MIN) * valueAnimator.getAnimatedFraction())));

                    cardView.setLayoutParams(lp);
                    cardView.setRadius((int) (dpToPx((collapse ? RADIUS_MAX : RADIUS_MIN) +
                            (collapse ? -1 : 1) * (RADIUS_MAX - RADIUS_MIN) * valueAnimator.getAnimatedFraction())));

                    int height = collapse ?
                            (int) (HEIGHT_COLLAPSED * valueAnimator.getAnimatedFraction()) :
                            (int) (HEIGHT_COLLAPSED * (1 - valueAnimator.getAnimatedFraction()));
                    if (height > 0) {
                        cardView.findViewById(R.id.expand_button).setVisibility(View.VISIBLE);
                        cardView.findViewById(R.id.expand_button).getLayoutParams().height = height;
                        cardView.findViewById(R.id.expand_button).setLayoutParams(cardView.findViewById(R.id.expand_button).getLayoutParams());
                    } else {
                        if (HEIGHT_COLLAPSED == 0)
                            HEIGHT_COLLAPSED = heightOri;
                        cardView.findViewById(R.id.expand_button).getLayoutParams().height = HEIGHT_COLLAPSED;
                        cardView.findViewById(R.id.expand_button).setLayoutParams(cardView.findViewById(R.id.expand_button).getLayoutParams());
                        cardView.findViewById(R.id.expand_button).setVisibility(View.GONE);
                    }
                }
            });
            animator.start();
        }

        static double dpToPx(double dp) {
            return dp * Resources.getSystem().getDisplayMetrics().density;
        }

        static double pxToDp(double px) {
            return px / Resources.getSystem().getDisplayMetrics().density;
        }
    }
}
