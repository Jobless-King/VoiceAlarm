package com.galaxy.voicealarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SelectedAudioActivity extends AppCompatActivity {

    String mCurrent;
    String mRoot;
    ListView mFileList;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> arFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_audio);

        mFileList = (ListView)findViewById(R.id.filelist);

        arFiles = new ArrayList<String>();
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurrent = mRoot;

        mAdapter = new ArrayAdapter<String>(this, R.layout.activity_white_text_adapter , arFiles);
        mFileList.setAdapter(mAdapter);
        mFileList.setOnItemClickListener(mItemClickListener);

        refreshFiles();
    }

    //Root Page일 경우에는 Activity나가기
    //아닐 경우에는 이전 디렉토리로 이동
    @Override
    public void onBackPressed(){
        if( 0 == mRoot.compareTo(mCurrent)){
            finish();
        }else{
            int end = mCurrent.lastIndexOf("/");
            String uppath = mCurrent.substring(0, end);
            mCurrent = uppath;
            refreshFiles();
        }
    }

    AdapterView.OnItemClickListener mItemClickListener =
            new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long ld) {
                    String Name = arFiles.get(position);
                    if(Name.startsWith("[")&&Name.endsWith("]")){
                        Name = Name.substring(1, Name.length()-1);
                    }
                    String Path = mCurrent + "/" + Name;
                    File f = new File(Path);
                    if(f.isDirectory()){
                        mCurrent = Path;
                        refreshFiles();
                    }
                    //디렉토리가 아닌 파일인 경우
                    else{
                        String fileName = arFiles.get(position);

                        if(fileName.contains("'")){
                            Toast.makeText(SelectedAudioActivity.this, "작은 따음표나 큰따음표가 들어간 파일은 인식이 불가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(0 == ".mp3".compareTo(fileName.substring(fileName.length()-4, fileName.length()))){
                            Intent intent = new Intent();
                            AudioFile audioFile = new AudioFile(fileName, Uri.fromFile(f).toString());
                            intent.putExtra("AUDIO_FILE", audioFile);
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            Toast.makeText(SelectedAudioActivity.this, "mp3파일을 선택해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };

    //디렉토리에 있는 file들로 arFiles를 채우는 코드
    void refreshFiles(){
        arFiles.clear();
        File current = new File(mCurrent);
        String[] files = current.list();
        if(null != files){
            for(int i=0; i<files.length; ++i){
                String Path = mCurrent + "/" + files[i];
                String Name = "";
                File f = new File(Path);
                if(f.isDirectory()){
                    Name = "[" + files[i] + "]";
                }else{
                    Name = files[i];
                }

                arFiles.add(Name);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
