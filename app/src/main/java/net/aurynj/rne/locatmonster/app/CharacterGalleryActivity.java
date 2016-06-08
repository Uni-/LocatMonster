package net.aurynj.rne.locatmonster.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.appframework.*;
import net.aurynj.rne.locatmonster.model.*;
import net.aurynj.rne.locatmonster.widget.CharacterBriefStatusView;

import java.util.List;

public class CharacterGalleryActivity extends BaseActivity implements ListView.OnItemClickListener {
    private UserPrefs mUserPrefs;
    private List<CharacterStatus> mCharacterStatusList;

    ListView mListView;
    TextView mListEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_gallery);

        mListView = (ListView) findViewById(android.R.id.list);
        mListEmptyTextView = (TextView) findViewById(android.R.id.empty);
    }

    @Override
    protected void onBindService() {
        super.onBindService();

        mUserPrefs = getLocatMonsterService().getUserPrefs();
        mCharacterStatusList = mUserPrefs.getCharacterStatusList();
        ArrayAdapter<CharacterStatus> arrayAdapter = new CharacterArrayAdapter(this, R.layout.item_character_gallery, mCharacterStatusList);

        mListView.setAdapter(arrayAdapter);
        mListView.setEmptyView(mListEmptyTextView);
        mListView.setOnItemClickListener(this);

        mListEmptyTextView.setText("캐릭터가 없습니다. 곧 캐릭터가 생길 거예요!");
    }

    private class CharacterArrayAdapter extends ArrayAdapter<CharacterStatus> {
        final int mLayoutResourceId;

        public CharacterArrayAdapter(Context context, int resource, List<CharacterStatus> data) {
            super(context, resource, data);
            mLayoutResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(mLayoutResourceId, parent, false);
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.item_character_gallery_image);
            CharacterBriefStatusView characterBriefStatusView = (CharacterBriefStatusView) view.findViewById(R.id.item_character_gallery_brief_status);

            CharacterStatus characterStatus = getItem(position);

            imageView.setImageResource(characterStatus.DrawableRes);
            characterBriefStatusView.setAllFrom(characterStatus);

            return view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("", "onItemClick");
        final CharacterStatus characterStatus = mCharacterStatusList.get(position);
        String name = characterStatus.Name;
        String alias = characterStatus.Alias;

        AlertDialog.Builder builder = new AlertDialog.Builder(CharacterGalleryActivity.this);
        builder.setTitle("별명 입력");
        builder.setMessage("<" + name + ">의 별명을 입력하세요");
        final EditText editText = new EditText(CharacterGalleryActivity.this);
        editText.setText(alias);
        builder.setView(editText);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                characterStatus.Alias = editText.getText().toString();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // silently ignore
            }
        });
        builder.show();
    }
}
