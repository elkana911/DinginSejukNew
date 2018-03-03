package com.elkana.teknisi.screen;

import com.elkana.dslibrary.pojo.mitra.Assignment;

import java.util.List;

/**
 * Created by Eric on 13-Nov-17.
 */

public interface ListenerAssignmentList {
    void onItemSelected(Assignment assignment);

    void onDataChanged(List<Assignment> list);
}
