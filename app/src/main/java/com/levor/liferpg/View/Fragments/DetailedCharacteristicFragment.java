package com.levor.liferpg.View.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedCharacteristicFragment extends DefaultFragment {
    public final static String CHARACTERISTIC_TITLE = "characteristic_title";

    private TextView levelValue;
    private ListView listView;

    private Characteristic currentCharacteristic;
    private ArrayList<Skill> currentSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detailed_characteristic, container, false);
        currentCharacteristic = getController().getCharacteristicByTitle(getArguments().getString(CHARACTERISTIC_TITLE));
        getActivity().setTitle(currentCharacteristic.getTitle() + " details");

        levelValue = (TextView) v.findViewById(R.id.level_value);
        listView = (ListView) v.findViewById(R.id.list_view);
        levelValue.setText("" + currentCharacteristic.getLevel());
        createAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putSerializable(DetailedSkillFragment.SELECTED_SKILL_UUID_TAG, currentSkills.get(position).getId());
                Fragment f = new DetailedSkillFragment();
                getCurrentActivity().showChildFragment(f, b);
            }
        });
        return v;
    }

    private void createAdapter(){
        ArrayList<String> skills = new ArrayList<>();
        currentSkills = getController().getSkillsByCharacteristic(currentCharacteristic);
        for (Skill sk : currentSkills){
            skills.add(sk.getTitle() + " - " + sk.getLevel() + "(" + sk.getSublevel() + ")");
        }
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills));
    }
}
