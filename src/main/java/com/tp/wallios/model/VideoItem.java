package com.tp.wallios.model;

import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.reference.DataType;
import com.tp.wallios.model.common.DataItem;

public class VideoItem extends DataItem {
    public VideoItem(Data data) {
        super(data.toJson(), DataType.VIDEO.value);

    }
}
