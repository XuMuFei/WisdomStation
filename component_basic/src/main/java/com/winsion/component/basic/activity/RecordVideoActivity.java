package com.winsion.component.basic.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winsion.component.basic.R;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.winsion.component.basic.constants.Intents.Media.MEDIA_FILE;

/**
 * Created by wyl on 2016/8/27.
 * 录制视频
 * TODO 动态权限-相机
 * TODO 动态权限-录音
 */
public class RecordVideoActivity extends BaseActivity {
    private SurfaceView mCameraPreview;
    private TextView mSecondPrefix;
    private TextView mSecondText;
    private ImageView mShutter;
    private LinearLayout llButton;
    private TitleView tvTitle;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private MediaRecorder mRecorder;

    private boolean mIsRecording = false;

    private File mFile;

    private boolean isPressed = false;  // 记录按钮是否按下
    private boolean isPermissionDenied;
    private List<Camera.Size> svs;

    @Override
    protected int setContentView() {
        return R.layout.basic_activity_record_video;
    }

    @Override
    protected void start() {
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mCameraPreview = findViewById(R.id.camera_preview);
        mSecondPrefix = findViewById(R.id.timestamp_second_prefix);
        mSecondText = findViewById(R.id.timestamp_second_text);
        mShutter = findViewById(R.id.iv_shutter);
        llButton = findViewById(R.id.ll_button);
        tvTitle = findViewById(R.id.tv_title);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> {
            deleteFile();
            finish();
        });
        addOnClickListeners(R.id.iv_shutter, R.id.btn_confirm, R.id.btn_cancel);
    }

    private void initData() {
        mCameraPreview.setKeepScreenOn(true);
        mCameraPreview.setFocusable(true);
        mFile = (File) getIntent().getSerializableExtra(MEDIA_FILE);
        mSurfaceHolder = mCameraPreview.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isPermissionDenied = false;
            try {
                startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isPermissionDenied) {
                releaseCamera();
            }
        }
    };

    //启动预览
    private void startPreview() throws IOException {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Parameters parameters = mCamera.getParameters();

        svs = parameters.getSupportedVideoSizes();
        List<Camera.Size> sps = parameters.getSupportedPreviewSizes();
        if (svs != null && sps != null && sps.size() != 0) {
            Camera.Size size = svs.get(sps.size() / 2);
            parameters.setPreviewSize(size.width, size.height);
        } else {
            showToast(R.string.toast_video_not_support);
            finish();
            return;
        }

        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setPreviewFrameRate(20);
        //设置相机预览方向
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
    }

    private void releaseCamera() {
        //释放Camera对象
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_shutter) {
            if (!isPressed) {
                isPressed = true;
                mShutter.setImageResource(R.drawable.basic_btn_record_reverse);
            } else {
                isPressed = false;
                mShutter.setImageResource(R.drawable.basic_bg_start_record);
            }
            if (mIsRecording) {
                stopRecording();
            } else {
                // 延迟一秒才可以停止，避免误操作
                mShutter.setEnabled(false);
                mHandler.postDelayed(() -> mShutter.setEnabled(true), 1000);
                initMediaRecorder();
                startRecording();
                //开始录像后，每隔1s去更新录像的时间戳
                mHandler.postDelayed(mTimestampRunnable, 1000);
            }
        } else if (id == R.id.btn_confirm) {
            setResult(Activity.RESULT_OK);
            finish();
        } else if (id == R.id.btn_cancel) {
            if (!mFile.exists()) {
                mFile.delete();
            }
            mShutter.setVisibility(View.VISIBLE);
            llButton.setVisibility(View.GONE);
            mCamera.startPreview();
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText("0");
        }
    }

    private void initMediaRecorder() {
        //实例化
        mRecorder = new MediaRecorder();
        mCamera.unlock();
        //给Recorder设置Camera对象，保证录像跟预览的方向保持一致
        mRecorder.setCamera(mCamera);
        //改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
        mRecorder.setOrientationHint(90);
        // 设置从麦克风采集声音
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置从摄像头采集图像
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置视频的输出格式 为MP4
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置音频的编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        // 设置视频的编码格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        // 设置视频大小
        if (svs != null && svs.size() != 0) {
            Camera.Size size = svs.get(svs.size() / 2);
            mRecorder.setVideoSize(size.width, size.height);
        }
        // 设置帧率
        // 视频的帧率和视频大小是需要硬件支持的，如果设置的帧率和视频大小,如果硬件不支持就会出现错误
//        mRecorder.setVideoFrameRate(20);
        //设置最大录像时间为60S
        mRecorder.setMaxDuration(60000);
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mRecorder.setOutputFile(mFile.getAbsolutePath());
    }

    private void startRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception e) {
                mIsRecording = false;
                logE(e.getMessage());
            }
        }
        mIsRecording = true;
    }

    private void stopRecording() {
        if (mCamera != null) {
            mCamera.lock();
        }
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        mIsRecording = false;
        mHandler.removeCallbacks(mTimestampRunnable);
        mCamera.stopPreview();
        mShutter.setVisibility(View.GONE);
        llButton.setVisibility(View.VISIBLE);
    }

    private Runnable mTimestampRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimestamp();
            mHandler.postDelayed(this, 1000);
        }
    };

    private void updateTimestamp() {
        int second = Integer.parseInt(mSecondText.getText().toString());
        second++;
        logI("second: " + second);
        if (second >= 60) {
            stopRecording();
        } else if (second >= 10) {
            mSecondPrefix.setVisibility(View.GONE);
            mSecondText.setText(String.valueOf(second));
        } else {
            mSecondText.setText(String.valueOf(second));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deleteFile();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void deleteFile() {
        if (mFile.exists()) {
            mFile.delete();
        }
        mHandler.removeCallbacks(mTimestampRunnable);
    }
}
