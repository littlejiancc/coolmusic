package com.example.wangxiaojian.mediaplayer;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    private ListView mListView;
    private Adapter mAdapter;
    private ImageButton btn_play,btn_pause,btn_pre,btn_next,btn_stop;
    private TextView mTextView;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private List<String> mList=new ArrayList<String>();
    private List<Music> mMusics=new ArrayList<Music>();
    private int getPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate","oncreate");
        Log.d("当前position",String.valueOf(getPosition));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView=(ListView)findViewById(R.id.music_listview);
        mTextView=(TextView)findViewById(R.id.text_music);
        //获取按钮控件
        btn_next=(ImageButton)findViewById(R.id.btn_next);btn_pause=(ImageButton)findViewById(R.id.btn_pause);
        btn_play=(ImageButton)findViewById(R.id.btn_play);btn_stop=(ImageButton)findViewById(R.id.btn_stop);
        btn_pre=(ImageButton)findViewById(R.id.btn_pre);
        //btn_next.getBackground().setAlpha(0);//0~255透明度值,0表示透明

        btn_pre.setOnClickListener(this);btn_stop.setOnClickListener(this);
        btn_next.setOnClickListener(this);btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        init();
        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mList);
        mListView.setAdapter((ListAdapter) mAdapter);
        //mListView点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getPosition=position;
                Log.d("当前position",String.valueOf(getPosition));
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();//将 MediaPlayer 对象重置到刚刚创建的状态。
                }
                Music music=mMusics.get(position);
                initMediaPlayer(music.getUri());// 初始化MediaPlayer
                mediaPlayer.start(); // 开始播放
                //显示正在播放的歌曲
                String preMusic=music.getMusic_title()+"-"+music.getArtist()+" "+music.getDuration();
                mTextView.setText(preMusic);
            }
        });
        //自动切换歌曲
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();//将 MediaPlayer 对象重置到刚刚创建的状态。
                Music music=mMusics.get(++getPosition);
                initMediaPlayer(music.getUri());// 初始化MediaPlayer
                mediaPlayer.start(); // 开始播放
                //显示正在播放的歌曲
                String preMusic=music.getMusic_title()+"-"+music.getArtist()+" "+music.getDuration();
                mTextView.setText(preMusic);
            }
        });
    }
    private void init(){
        Cursor cursor =
                getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int i=0;
        String musicLine="";
        while (cursor.moveToNext()){
            i++;
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//将毫秒转化为分和秒格式
            String duration2=formatter.format(duration);
            musicLine=String.valueOf(i)+". "+title+" - "+artist+"   "+duration2;
            mList.add(musicLine);
            Music music=new Music(title,artist,duration2,url);
            mMusics.add(music);
        }
    }
    private void initMediaPlayer(String fileName){
        try {
            File file = new File(fileName);
            mediaPlayer.setDataSource(file.getPath()); // 指定音频文件的路径
            mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); // 暂停播放
                }
                break;
            case R.id.btn_play:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // 开始播放
                }
                break;
            case R.id.btn_stop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset(); // 停止播放
                    Music music=mMusics.get(getPosition);
                   initMediaPlayer(music.getUri());//重新开始播放当前歌曲
                }
                break;
            case R.id.btn_pre:
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();//将 MediaPlayer 对象重置到刚刚创建的状态。
                }
                getPosition-=1;
                //循环播放，当返回到getPosition=-1的时候，就回到最后一首歌
                if(getPosition==-1){
                    getPosition=mListView.getCount()-1;
                }
                Music music=mMusics.get(getPosition);
                initMediaPlayer(music.getUri());// 初始化MediaPlayer
                mediaPlayer.start(); // 开始播放
                //显示正在播放的歌曲
                String preMusic=music.getMusic_title()+"-"+music.getArtist()+" "+music.getDuration();
                mTextView.setText(preMusic);
                break;
            case R.id.btn_next:
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();//将 MediaPlayer 对象重置到刚刚创建的状态。
                }
                getPosition+=1;
                Log.d("next position",String.valueOf(getPosition));
                //循环播放如果getposition的值等于歌曲的数量，就取0，回到第一首
                if(getPosition==mListView.getCount()){
                    getPosition=0;
                }
                music=mMusics.get(getPosition);
                initMediaPlayer(music.getUri());// 初始化MediaPlayer
                mediaPlayer.start(); // 开始播放
                //显示正在播放的歌曲
                preMusic=music.getMusic_title()+"-"+music.getArtist()+" "+music.getDuration();
                mTextView.setText(preMusic);
                break;
        }
    }
}
