package romanow.abc.ess2.android.service;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;

import romanow.abc.core.entity.metadata.StreamDataValue;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.ess2.android.GraphData;
import romanow.abc.ess2.android.R;

public class ESS2FullScreenGraph extends AppCompatActivity {
    private final static int paintColors[]={0x00007000,0x000000FF,0x00A00000,0x000070C0,0x00C000C0,0x00206060};
    AppData ctx;
    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ctx = AppData.ctx();
            try {
                setContentView(R.layout.graph_gorizontal);
                getSupportActionBar().hide();
                LinearLayout panel = (LinearLayout) findViewById(R.id.viewPanelHoriz);
                LinearLayout hd = (LinearLayout) findViewById(R.id.viewPanelHead);
                LinearLayout graph=(LinearLayout) getLayoutInflater().inflate(R.layout.graphviewhoriz, null);
                int procHigh=0;
                if (procHigh!=0){
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)panel.getLayoutParams();
                    params.height = (int)(getResources().getDisplayMetrics().widthPixels*procHigh);
                    panel.setLayoutParams(params);
                    }
                LineGraphView multiGraph = new LineGraphView(this,"");
                multiGraph.setScalable(true);
                multiGraph.setScrollable(true);
                multiGraph.getGraphViewStyle().setTextSize(15);
                panel.addView(multiGraph);
                ArrayList<GraphData> data = ctx.getGraphData();
                int idx=0;
                for(GraphData seq : data) {
                    LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.graph_button,null);
                    Button bb = ll.findViewById(R.id.graph_button_press);
                    bb.setTextColor(getPaintColor(idx++) | 0xFF000000);
                    bb.setText(seq.name+": "+new OwnDateTime(seq.data.get(0).timeStamp).dateTimeToString());
                    hd.addView(ll);
                    }
                idx=0;
                long minStamp = 0;
                for(GraphData seq : data) {
                    if (minStamp==0 || seq.data.get(0).timeStamp < minStamp)
                        minStamp = seq.data.get(0).timeStamp;
                    }
                for(GraphData seq : data) {
                    GraphView.GraphViewData zz[] = new GraphView.GraphViewData[seq.data.size()];
                    for (int j = 0; j < seq.data.size(); j++) {                    // Подпись значений факторов j-ой ячейки
                        StreamDataValue value = seq.data.get(j);
                        zz[j] = new GraphView.GraphViewData((value.timeStamp-minStamp)/1000./3600, value.value);
                        }
                    GraphViewSeries series = new GraphViewSeries(zz);
                    series.getStyle().color = getPaintColor(idx++) | 0xFF000000;
                    multiGraph.addSeries(series);
                    }
                } catch (Exception ee){
                    ctx.createBugMessage(ctx.createFatalMessage(ee));
                    }
                }
    public int getPaintColor(int idx){
        if (idx < paintColors.length)
            return paintColors[idx];
        idx -= paintColors.length;
        int color = 0x00808080;
        while(idx--!=0 && color!=0)
            color-=0x00202020;
        return color;
        }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data_return", "Hello FirstActivity");
        setResult(RESULT_OK, intent);
        finish();
        }
    }
