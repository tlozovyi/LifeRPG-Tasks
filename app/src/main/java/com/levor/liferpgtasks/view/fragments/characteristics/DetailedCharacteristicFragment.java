package com.levor.liferpgtasks.view.fragments.characteristics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.Skill;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;
import com.levor.liferpgtasks.view.fragments.skills.AddSkillFragment;
import com.levor.liferpgtasks.view.fragments.skills.DetailedSkillFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailedCharacteristicFragment extends DefaultFragment {
    public final static String CHARACTERISTIC_TITLE = "characteristic_title";

    private ListView listView;

    private Characteristic currentCharacteristic;
    private ArrayList<Skill> currentSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_characteristic, container, false);
        listView = (ListView) v;
        View header = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.detailed_characteristic_header, null);
        currentCharacteristic = getController().getCharacteristicByTitle(getArguments().getString(CHARACTERISTIC_TITLE));
        getCurrentActivity().setActionBarTitle(currentCharacteristic.getTitle());

        TextView levelValue = (TextView) header.findViewById(R.id.level_value);
        TextView characteristicTitle = (TextView) header.findViewById(R.id.characteristic_title);
        Button addSkillButton = (Button) header.findViewById(R.id.add_skill_button);

        characteristicTitle.setText(currentCharacteristic.getTitle());
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        levelValue.setText(Integer.toString(currentCharacteristic.getLevel()));

        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(AddSkillFragment.RECEIVED_CHARACTERISTIC_TITLE_TAG, currentCharacteristic.getTitle());
                getCurrentActivity().showChildFragment(new AddSkillFragment(), b);
            }
        });

        listView.addHeaderView(header, null, false);
        createAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG,
                        currentSkills.get(position - listView.getHeaderViewsCount()).getId());
                DefaultFragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Detailed Characteristic Fragment");
    }

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = getController().getSkillsByCharacteristic(currentCharacteristic);
        DecimalFormat df = new DecimalFormat("#.##");
        for (Skill sk : currentSkills){
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + df.format(sk.getSublevel()) + ")");
        }
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills));
    }
}
