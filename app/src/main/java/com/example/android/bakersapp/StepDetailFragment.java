package com.example.android.bakersapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakersapp.models.Recipe;
import com.example.android.bakersapp.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment {


    private ArrayList<Recipe> recipe;
    private ArrayList<Step> steps;
    private Integer clickedItemIndex;
    private long videoCurrentPosition;
    boolean videoPlayWhenReady = true;
    private boolean mTwoPane;

    private TextView stepTitleView;
    private TextView stepInstructionsView;
    private LinearLayout buttonsRowLayout;
    private PlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private ButtonClickListener navClickListener;

    public StepDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            navClickListener = (ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View stepView = inflater.inflate(R.layout.fragment_step_detail, viewGroup, false);
        stepTitleView = (TextView) stepView.findViewById(R.id.tv_step_short_description);
        stepInstructionsView = (TextView) stepView.findViewById(R.id.tv_step_description);
        buttonsRowLayout = (LinearLayout) stepView.findViewById(R.id.ll_buttons_row);
        if (getActivity().findViewById(R.id.divider1) != null) {
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            steps = recipe.get(0).getSteps();
            clickedItemIndex = savedInstanceState.getInt("Step_Index");
            videoCurrentPosition = savedInstanceState.getLong("Video_Position");
            videoPlayWhenReady = savedInstanceState.getBoolean("Video_State");
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                if (arguments.containsKey("Current_Recipe")) {
                    recipe = getArguments().getParcelableArrayList("Current_Recipe");
                    steps = recipe.get(0).getSteps();
                }
                if (arguments.containsKey("Step_Index")) {
                    clickedItemIndex = arguments.getInt("Step_Index");
                }
            }
        }

        Integer stepId = steps.get(clickedItemIndex).getId();
        String shortDescription = steps.get(clickedItemIndex).getShortDescription();
        String description = steps.get(clickedItemIndex).getDescription();
        String videoUrl = steps.get(clickedItemIndex).getVideoURL();
        String thumbnailURL = steps.get(clickedItemIndex).getThumbnailURL();

        String stepTitleString = (stepId < 1) ? "" : getString(R.string.step_header) +
                " " + stepId.toString() + ": ";
        stepTitleString = stepTitleString + shortDescription;
        stepTitleView.setText(stepTitleString);

        if (!description.equals(shortDescription)) {
            stepInstructionsView.setText(description);
        }

        ImageView exoPlaceholderView = (ImageView) stepView.findViewById(R.id.exo_placeholder_view);
        exoPlayerView = (PlayerView) stepView.findViewById(R.id.exo_player_view);
        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        String targetUrl = null;
        if (!TextUtils.isEmpty(videoUrl)) {
            targetUrl = videoUrl;
        } else if (thumbnailURL.endsWith(".mp4")) {
            targetUrl = thumbnailURL;
        }

        if (!TextUtils.isEmpty(targetUrl)) {
            exoPlaceholderView.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.VISIBLE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane) {
                fullScreenLandscape();
            }
            initializePlayer(Uri.parse(targetUrl), videoCurrentPosition);
        } else {
            exoPlayerView.setVisibility(View.GONE);
            exoPlaceholderView.setVisibility(View.VISIBLE);
            exoPlayer = null;
        }

        Button prevButtonView = (Button) stepView.findViewById(R.id.previous_button);
        prevButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedItemIndex > 0) {
                    int targetStepIndex = clickedItemIndex - 1;
                    if (exoPlayer != null) {
                        exoPlayer.stop();
                    }
                    setListIndex(targetStepIndex);
                    navClickListener.onButtonClick(targetStepIndex);
                } else {
                    Toast.makeText(getActivity(),
                            R.string.begin_of_steps_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button nextButtonView = (Button) stepView.findViewById(R.id.next_button);
        nextButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (clickedItemIndex < (steps.size() - 1)) {
                    int targetStepIndex = clickedItemIndex + 1;
                    if (exoPlayer != null) {
                        exoPlayer.stop();
                    }
                    setListIndex(targetStepIndex);
                    navClickListener.onButtonClick(targetStepIndex);
                } else {
                    Toast.makeText(getActivity(),
                            R.string.end_of_steps_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return stepView;

    }

    private void setListIndex(int newIndex) {
        clickedItemIndex = newIndex;
    }

    private void initializePlayer(Uri mediaUri, long seekPos) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            exoPlayerView.setPlayer(exoPlayer);

            MediaSource mediaSource = buildMediaSource(mediaUri);
            exoPlayer.prepare(mediaSource, false, true);
            exoPlayerView.hideController();
            exoPlayer.seekTo(seekPos);
            exoPlayer.setPlayWhenReady(videoPlayWhenReady);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        Context context = getContext();
        assert context != null;
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)), bandwidthMeter);
        return new ExtractorMediaSource.
                Factory(dataSourceFactory).createMediaSource(uri);
    }

    private void fullScreenLandscape() {
        stepTitleView.setVisibility(View.GONE);
        stepInstructionsView.setVisibility(View.GONE);
        buttonsRowLayout.setVisibility(View.GONE);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        exoPlayerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        LinearLayout.LayoutParams lLparams =
                (LinearLayout.LayoutParams) exoPlayerView.getLayoutParams();
        lLparams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lLparams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private void exoPlayerStopReleaseResources() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exoPlayerStopReleaseResources();
        exoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putInt("Step_Index", clickedItemIndex);

        if (exoPlayer != null) {
            videoCurrentPosition = exoPlayer.getCurrentPosition();
            videoPlayWhenReady = exoPlayer.getPlayWhenReady();
            currentState.putLong("Video_Position", videoCurrentPosition);
            currentState.putBoolean("Video_State", videoPlayWhenReady);
        }
    }

    public interface ButtonClickListener {
        void onButtonClick(int targetStepIndex);
    }

}
