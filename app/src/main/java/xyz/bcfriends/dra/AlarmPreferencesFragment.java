package xyz.bcfriends.dra;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class AlarmPreferencesFragment extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.alarm_preferences, rootKey);
//        prefs = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
    }
}
