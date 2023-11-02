package romanow.abc.ess2.android;

import java.util.ArrayList;

import romanow.abc.core.entity.metadata.StreamDataValue;

public class GraphData {
    public final String name;

    public final ArrayList<StreamDataValue> data;
    public GraphData(String name, ArrayList<StreamDataValue> data) {
        this.name = name;
        this.data = data;
        }
}
