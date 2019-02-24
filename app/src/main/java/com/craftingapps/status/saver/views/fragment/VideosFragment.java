package com.craftingapps.status.saver.views.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.craftingapps.status.saver.R;
import com.craftingapps.status.saver.adapter.StatusAdapter;
import com.craftingapps.status.saver.databinding.FragmentVideoBinding;
import com.craftingapps.status.saver.helper.AppConstants;
import com.craftingapps.status.saver.managers.AdsManager;
import com.craftingapps.status.saver.models.StatusModel;
import com.craftingapps.status.saver.models.UpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class VideosFragment extends Fragment {

    private static final String TAG = VideosFragment.class.getSimpleName();
    FragmentVideoBinding binding;

    StatusAdapter statusAdapter;
    ArrayList<StatusModel> videosList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);


        binding.emptyRecordLayout.setVisibility(View.GONE);

        videosList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            binding.noPermissionText.setVisibility(View.GONE);
            videosList = getVideosList();
            setUpRecyclerView();
        } else {
            binding.noPermissionText.setVisibility(View.VISIBLE);
        }
        AdsManager.getInstance().showAdMobLargeBanner(binding.triggerFragmentAdId);
        return binding.getRoot();
    }


    private void setUpRecyclerView() {
        statusAdapter = new StatusAdapter(getContext(), videosList);
        binding.videosRecycler.setHasFixedSize(true);
        binding.videosRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.videosRecycler.setAdapter(statusAdapter);
        statusAdapter.notifyDataSetChanged();

        if (statusAdapter.getItemCount() < 1) {
            binding.emptyRecordLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emptyRecordLayout.setVisibility(View.GONE);
        }
    }


    private ArrayList<StatusModel> getVideosList() {
        StatusModel status;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.GET_FOLDER_NAME;
        File targetDirector = new File(targetPath);

        File[] files = new File[0];
        try {
            files = targetDirector.listFiles();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (files == null) {
            binding.emptyRecordLayout.setVisibility(View.VISIBLE);
        } else {
            binding.emptyRecordLayout.setVisibility(View.GONE);

            try {
                Arrays.sort(files, new Comparator() {
                    public int compare(Object o1, Object o2) {

                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });

                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().toString().endsWith(".mp4")) {
                        File file = files[i];
                        status = new StatusModel();
                        status.setName("StatusSaver_" + (i + 1));
                        status.setUri(Uri.fromFile(file));
                        status.setPath(files[i].getAbsolutePath());
                        status.setFilename(file.getName());
                        status.setVideo(true);
                        videosList.add(status);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return videosList;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateEvent event) {
        if (event.isUpdate()) {
            videosList.clear();
            videosList = getVideosList();
            statusAdapter.notifyDataSetChanged();

            if (statusAdapter.getItemCount() < 1) {
                binding.emptyRecordLayout.setVisibility(View.VISIBLE);
            } else {
                binding.emptyRecordLayout.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

}
